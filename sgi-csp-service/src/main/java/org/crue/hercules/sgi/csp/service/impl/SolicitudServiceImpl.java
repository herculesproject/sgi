package org.crue.hercules.sgi.csp.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.csp.enums.TipoEstadoSolicitudEnum;
import org.crue.hercules.sgi.csp.enums.TipoFormularioSolicitudEnum;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.EstadoSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoDatosRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudService;
import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.framework.security.access.expression.SgiMethodSecurityExpressionRoot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link Solicitud}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudServiceImpl implements SolicitudService {

  private final SolicitudRepository repository;
  private final EstadoSolicitudRepository estadoSolicitudRepository;
  private final ConfiguracionSolicitudRepository configuracionSolicitudRepository;
  private final ProyectoRepository proyectoRepository;
  private final SolicitudProyectoDatosRepository solicitudProyectoDatosRepository;

  public SolicitudServiceImpl(SolicitudRepository repository, EstadoSolicitudRepository estadoSolicitudRepository,
      ConfiguracionSolicitudRepository configuracionSolicitudRepository, ProyectoRepository proyectoRepository,
      SolicitudProyectoDatosRepository solicitudProyectoDatosRepository) {
    this.repository = repository;
    this.estadoSolicitudRepository = estadoSolicitudRepository;
    this.configuracionSolicitudRepository = configuracionSolicitudRepository;
    this.proyectoRepository = proyectoRepository;
    this.solicitudProyectoDatosRepository = solicitudProyectoDatosRepository;
  }

  /**
   * Guarda la entidad {@link Solicitud}.
   * 
   * @param solicitud         la entidad {@link Solicitud} a guardar.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion
   *                          permitidas para el usuario.
   * @return solicitud la entidad {@link Solicitud} persistida.
   */
  @Override
  @Transactional
  public Solicitud create(Solicitud solicitud, List<String> unidadGestionRefs) {
    log.debug("create(Solicitud solicitud) - start");

    Assert.isNull(solicitud.getId(), "Solicitud id tiene que ser null para crear una Solicitud");
    Assert.notNull(solicitud.getCreadorRef(), "CreadorRef no puede ser null para crear una Solicitud");

    Assert.isTrue(
        (solicitud.getConvocatoria() != null && solicitud.getConvocatoria().getId() != null)
            || solicitud.getConvocatoriaExterna() != null,
        "Convocatoria o Convocatoria externa tienen que ser distinto de null para crear una Solicitud");

    if (solicitud.getConvocatoria() != null && solicitud.getConvocatoria().getId() != null) {
      ConfiguracionSolicitud configuracionSolicitud = configuracionSolicitudRepository
          .findByConvocatoriaId(solicitud.getConvocatoria().getId())
          .orElseThrow(() -> new ConfiguracionSolicitudNotFoundException(solicitud.getConvocatoria().getId()));

      Assert.isTrue(
          unidadGestionRefs.stream()
              .anyMatch(unidadGestionRef -> unidadGestionRef
                  .equals(configuracionSolicitud.getConvocatoria().getUnidadGestionRef())),
          "La Convocatoria pertenece a una Unidad de Gestión no gestionable por el usuario");

      solicitud.setConvocatoria(configuracionSolicitud.getConvocatoria());
      solicitud.setUnidadGestionRef(configuracionSolicitud.getConvocatoria().getUnidadGestionRef());
      solicitud.setFormularioSolicitud(configuracionSolicitud.getFormularioSolicitud());
    } else {
      Assert.isTrue(
          unidadGestionRefs.stream()
              .anyMatch(unidadGestionRef -> unidadGestionRef.equals(solicitud.getUnidadGestionRef())),
          "La Unidad de Gestión no es gestionable por el usuario");
    }

    solicitud.setActivo(Boolean.TRUE);

    // Crea la solicitud
    repository.save(solicitud);

    // Crea el estado inicial de la solicitud
    EstadoSolicitud estadoSolicitud = addEstadoSolicitud(solicitud, TipoEstadoSolicitudEnum.BORRADOR, null);

    // Actualiza la el estado actual de la solicitud con el nuevo estado
    solicitud.setEstado(estadoSolicitud);
    solicitud.setCodigoRegistroInterno(generateCodigoRegistroInterno(solicitud.getId()));
    Solicitud returnValue = repository.save(solicitud);

    log.debug("create(Solicitud solicitud) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link Solicitud}.
   * 
   * @param solicitud         solicitudActualizar {@link Solicitud} con los datos
   *                          actualizados.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion
   *                          permitidas para el usuario.
   * @return {@link Solicitud} actualizado.
   */
  @Override
  @Transactional
  public Solicitud update(Solicitud solicitud, List<String> unidadGestionRefs) {
    log.debug("update(Solicitud solicitud) - start");

    Assert.notNull(solicitud.getId(), "Id no puede ser null para actualizar Solicitud");

    Assert.notNull(solicitud.getSolicitanteRef(), "El solicitante no puede ser null para actualizar Solicitud");

    Assert.isTrue(
        solicitud.getConvocatoria() != null
            || (solicitud.getConvocatoriaExterna() != null && !solicitud.getConvocatoriaExterna().isEmpty()),
        "Se debe seleccionar una convocatoria del SGI o convocatoria externa para actualizar Solicitud");

    // comprobar si la solicitud es modificable
    Assert.isTrue(modificable(solicitud.getId()), "No se puede modificar la Solicitud");

    return repository.findById(solicitud.getId()).map((data) -> {

      Assert.isTrue(solicitud.getActivo(), "Solicitud tiene que estar activo para actualizarse");

      Assert.isTrue(
          unidadGestionRefs.stream().anyMatch(unidadGestionRef -> unidadGestionRef.equals(data.getUnidadGestionRef())),
          "La Convocatoria pertenece a una Unidad de Gestión no gestionable por el usuario");

      data.setSolicitanteRef(solicitud.getSolicitanteRef());
      data.setCodigoExterno(solicitud.getCodigoExterno());
      data.setObservaciones(solicitud.getObservaciones());

      if (null == data.getConvocatoria()) {
        data.setConvocatoriaExterna(solicitud.getConvocatoriaExterna());
        if (data.getEstado().getEstado().getValue().equals(TipoEstadoSolicitudEnum.BORRADOR.getValue())) {
          data.setCodigoExterno(solicitud.getCodigoExterno());
        }
      }

      Solicitud returnValue = repository.save(data);

      log.debug("update(Solicitud solicitud) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudNotFoundException(solicitud.getId()));
  }

  /**
   * Reactiva el {@link Solicitud}.
   *
   * @param id                Id del {@link Solicitud}.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion
   *                          permitidas para el usuario.
   * @return la entidad {@link Solicitud} persistida.
   */
  @Override
  @Transactional
  public Solicitud enable(Long id, List<String> unidadGestionRefs) {
    log.debug("enable(Long id) - start");

    Assert.notNull(id, "Solicitud id no puede ser null para reactivar un Solicitud");

    return repository.findById(id).map(solicitud -> {

      Assert.isTrue(
          unidadGestionRefs.stream()
              .anyMatch(unidadGestionRef -> unidadGestionRef.equals(solicitud.getUnidadGestionRef())),
          "La Convocatoria pertenece a una Unidad de Gestión no gestionable por el usuario");

      if (solicitud.getActivo()) {
        // Si esta activo no se hace nada
        return solicitud;
      }

      solicitud.setActivo(true);

      Solicitud returnValue = repository.save(solicitud);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudNotFoundException(id));
  }

  /**
   * Desactiva el {@link Solicitud}.
   *
   * @param id                Id del {@link Solicitud}.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion
   *                          permitidas para el usuario.
   * @return la entidad {@link Solicitud} persistida.
   */
  @Override
  @Transactional
  public Solicitud disable(Long id, List<String> unidadGestionRefs) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "Solicitud id no puede ser null para desactivar un Solicitud");

    return repository.findById(id).map(solicitud -> {
      Assert.isTrue(
          unidadGestionRefs.stream()
              .anyMatch(unidadGestionRef -> unidadGestionRef.equals(solicitud.getUnidadGestionRef())),
          "La Convocatoria pertenece a una Unidad de Gestión no gestionable por el usuario");

      if (!solicitud.getActivo()) {
        // Si no esta activo no se hace nada
        return solicitud;
      }

      solicitud.setActivo(false);

      Solicitud returnValue = repository.save(solicitud);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudNotFoundException(id));
  }

  /**
   * Obtiene una entidad {@link Solicitud} por id.
   * 
   * @param id                Identificador de la entidad {@link Solicitud}.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion
   *                          permitidas para el usuario.
   * @return Solicitud la entidad {@link Solicitud}.
   */
  @Override
  public Solicitud findById(Long id, List<String> unidadGestionRefs) {
    log.debug("findById(Long id) - start");
    final Solicitud returnValue = repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));

    Assert.isTrue(
        unidadGestionRefs.stream()
            .anyMatch(unidadGestionRef -> unidadGestionRef.equals(returnValue.getUnidadGestionRef())),
        "La Convocatoria pertenece a una Unidad de Gestión no gestionable por el usuario");

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Solicitud} activas paginadas y filtradas.
   *
   * @param query             información del filtro.
   * @param paging            información de paginación.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion.
   * @return el listado de entidades {@link Solicitud} activas paginadas y
   *         filtradas.
   */
  @Override
  public Page<Solicitud> findAllRestringidos(List<QueryCriteria> query, Pageable paging,
      List<String> unidadGestionRefs) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging, List<String> unidadGestionRefs) - start");
    if (query == null) {
      query = new ArrayList<>();
    }

    List<QueryCriteria> querySinReferenciaConvocatoria = query.stream()
        .filter(queryCriteria -> !queryCriteria.getKey().equals("referenciaConvocatoria")).collect(Collectors.toList());

    Specification<Solicitud> specByQuery = new QuerySpecification<Solicitud>(querySinReferenciaConvocatoria);
    Specification<Solicitud> specActivos = SolicitudSpecifications.activos();
    Specification<Solicitud> specByUnidadGestionRefIn = SolicitudSpecifications.unidadGestionRefIn(unidadGestionRefs);
    Specification<Solicitud> specs = Specification.where(specActivos).and(specByUnidadGestionRefIn).and(specByQuery);

    if (query.size() > querySinReferenciaConvocatoria.size()) {
      String referenciaConvocatoria = query.stream()
          .filter(queryCriteria -> queryCriteria.getKey().equals("referenciaConvocatoria")).findFirst().get()
          .getValue();
      specs = specs.and(SolicitudSpecifications.byReferenciaConvocatoria(referenciaConvocatoria));
    }

    Page<Solicitud> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(List<QueryCriteria> query, Pageable paging, List<String> unidadGestionRefs) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Solicitud} paginadas y filtradas.
   *
   * @param query             información del filtro.
   * @param paging            información de paginación.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion.
   * @return el listado de entidades {@link Solicitud} paginadas y filtradas.
   */
  @Override
  public Page<Solicitud> findAllTodosRestringidos(List<QueryCriteria> query, Pageable paging,
      List<String> unidadGestionRefs) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging, List<String> unidadGestionRefs) - start");
    if (query == null) {
      query = new ArrayList<>();
    }

    List<QueryCriteria> querySolicitud = query.stream()
        .filter(queryCriteria -> !queryCriteria.getKey().equals("referenciaConvocatoria")
            && !queryCriteria.getKey().startsWith("convocatoria.configuracionSolicitud")
            && !queryCriteria.getKey().startsWith("convocatoria.entidadFinanciadora")
            && !queryCriteria.getKey().startsWith("convocatoria.entidadConvocante"))
        .collect(Collectors.toList());

    Specification<Solicitud> specByQuery = new QuerySpecification<Solicitud>(querySolicitud);
    Specification<Solicitud> specByUnidadGestionRefIn = SolicitudSpecifications.unidadGestionRefIn(unidadGestionRefs);
    Specification<Solicitud> specs = Specification.where(specByUnidadGestionRefIn).and(specByQuery);

    // Referencia Convocatoria
    if (query.size() > querySolicitud.size()) {
      List<QueryCriteria> referenciaConvocatoria = query.stream()
          .filter(queryCriteria -> queryCriteria.getKey().equals("referenciaConvocatoria"))
          .collect(Collectors.toList());
      if (CollectionUtils.isNotEmpty(referenciaConvocatoria)) {
        specs = specs.and(SolicitudSpecifications.byReferenciaConvocatoria(referenciaConvocatoria.get(0).getValue()));
      }

    }

    // Fecha de inicio de la convocatoria fase
    // Desde
    if (query.size() > querySolicitud.size()) {
      List<QueryCriteria> fechaInicioFasePresentacionSolicitudes = query.stream()
          .filter(queryCriteria -> queryCriteria.getKey()
              .equals("convocatoria.configuracionSolicitud.fasePresentacionSolicitudes.fechaInicio.desde"))
          .collect(Collectors.toList());
      if (CollectionUtils.isNotEmpty(fechaInicioFasePresentacionSolicitudes)) {
        specs = specs.and(SolicitudSpecifications.byFechaInicioDesdeConfiguracionSolicitud(
            LocalDateTime.parse(fechaInicioFasePresentacionSolicitudes.get(0).getValue())));
      }
    }

    // Hasta
    if (query.size() > querySolicitud.size()) {
      List<QueryCriteria> fechaInicioFasePresentacionSolicitudes = query.stream()
          .filter(queryCriteria -> queryCriteria.getKey()
              .equals("convocatoria.configuracionSolicitud.fasePresentacionSolicitudes.fechaInicio.hasta"))
          .collect(Collectors.toList());
      if (CollectionUtils.isNotEmpty(fechaInicioFasePresentacionSolicitudes)) {
        specs = specs.and(SolicitudSpecifications.byFechaInicioHastaConfiguracionSolicitud(
            LocalDateTime.parse(fechaInicioFasePresentacionSolicitudes.get(0).getValue())));
      }
    }

    // Fecha fin de la convocatoria fase
    // Desde
    if (query.size() > querySolicitud.size()) {
      List<QueryCriteria> fechaInicioFasePresentacionSolicitudes = query.stream()
          .filter(queryCriteria -> queryCriteria.getKey()
              .equals("convocatoria.configuracionSolicitud.fasePresentacionSolicitudes.fechaFin.desde"))
          .collect(Collectors.toList());
      if (CollectionUtils.isNotEmpty(fechaInicioFasePresentacionSolicitudes)) {
        specs = specs.and(SolicitudSpecifications.byFechaFinDesdeConfiguracionSolicitud(
            LocalDateTime.parse(fechaInicioFasePresentacionSolicitudes.get(0).getValue())));
      }
    }

    // Hasta
    if (query.size() > querySolicitud.size()) {
      List<QueryCriteria> fechaInicioFasePresentacionSolicitudes = query.stream()
          .filter(queryCriteria -> queryCriteria.getKey()
              .equals("convocatoria.configuracionSolicitud.fasePresentacionSolicitudes.fechaFin.hasta"))
          .collect(Collectors.toList());
      if (CollectionUtils.isNotEmpty(fechaInicioFasePresentacionSolicitudes)) {
        specs = specs.and(SolicitudSpecifications.byFechaFinHastaConfiguracionSolicitud(
            LocalDateTime.parse(fechaInicioFasePresentacionSolicitudes.get(0).getValue())));
      }
    }

    // Entidad financiadora
    if (query.size() > querySolicitud.size()) {
      List<QueryCriteria> convocatoriaEntidadFinanciadora = query.stream()
          .filter(queryCriteria -> queryCriteria.getKey().equals("convocatoria.entidadFinanciadora.entidadRef"))
          .collect(Collectors.toList());
      if (CollectionUtils.isNotEmpty(convocatoriaEntidadFinanciadora)) {
        specs = specs.and(SolicitudSpecifications
            .byConvocatoriaEntidadFinanciadora(convocatoriaEntidadFinanciadora.get(0).getValue()));
      }
    }

    // Entidad financiadora fuente financiación
    if (query.size() > querySolicitud.size()) {
      List<QueryCriteria> convocatoriaEntidadFinanciadoraFuenteFinanciacionId = query.stream()
          .filter(
              queryCriteria -> queryCriteria.getKey().equals("convocatoria.entidadFinanciadora.fuenteFinanciacion.id"))
          .collect(Collectors.toList());
      if (CollectionUtils.isNotEmpty(convocatoriaEntidadFinanciadoraFuenteFinanciacionId)) {
        specs = specs.and(SolicitudSpecifications.byConvocatoriaEntidadFinanciadoraFinanciacionId(
            Long.valueOf(convocatoriaEntidadFinanciadoraFuenteFinanciacionId.get(0).getValue())));
      }
    }

    // Entidad convocante
    if (query.size() > querySolicitud.size()) {
      List<QueryCriteria> convocatoriaEntidadConvocante = query.stream()
          .filter(queryCriteria -> queryCriteria.getKey().equals("convocatoria.entidadConvocante.entidadRef"))
          .collect(Collectors.toList());
      if (CollectionUtils.isNotEmpty(convocatoriaEntidadConvocante)) {
        specs = specs.and(
            SolicitudSpecifications.byConvocatoriaEntidadConvocante(convocatoriaEntidadConvocante.get(0).getValue()));
      }
    }

    // Entidad convocante programa
    if (query.size() > querySolicitud.size()) {
      List<QueryCriteria> convocatoriaEntidadConvocanteProgramaId = query.stream()
          .filter(queryCriteria -> queryCriteria.getKey().equals("convocatoria.entidadConvocante.programa.id"))
          .collect(Collectors.toList());
      if (CollectionUtils.isNotEmpty(convocatoriaEntidadConvocanteProgramaId)) {
        specs = specs.and(SolicitudSpecifications.byConvocatoriaEntidadConvocanteProgramaId(
            Long.valueOf(convocatoriaEntidadConvocanteProgramaId.get(0).getValue())));
      }
    }

    Page<Solicitud> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(List<QueryCriteria> query, Pageable paging, List<String> unidadGestionRefs) - end");
    return returnValue;
  }

  /**
   * Comprueba si la soliciutd está asociada a una convocatoria SGI.
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return indicador de si se encuentra asociado o no la solicitud a una
   *         convocatoria SGI
   */
  @Override
  public boolean hasConvocatoriaSgi(Long id) {
    log.debug("hasConvocatoriaSgi(Long id) - start");
    Assert.notNull(id, "Solicitud id no puede ser null para comprobar su convocatoria");

    return repository.findById(id).map(solicitud -> {

      log.debug("hasConvocatoriaSgi(Long id) - end");
      return solicitud.getConvocatoria() != null;
    }).orElseThrow(() -> new SolicitudNotFoundException(id));
  }

  /**
   * Genera el codigo de registro interno de la solicitud
   * 
   * @param solicitudId
   * @return
   */
  private String generateCodigoRegistroInterno(Long solicitudId) {
    log.debug("generateCodigoRegistroInterno(Long solicitudId) - start");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    String codigoRegistroInterno = "SGI_SLC" + solicitudId
        + formatter.format(LocalDateTime.now(ZoneId.of("Europe/Madrid")));

    log.debug("generateCodigoRegistroInterno(Long solicitudId) - end");
    return codigoRegistroInterno;
  }

  /**
   * Añade el nuevo {@link EstadoSolicitud} y actualiza la {@link Solicitud} con
   * dicho estado.
   * 
   * @param solicitud           la {@link Solicitud} para la que se añade el nuevo
   *                            estado.
   * @param tipoEstadoSolicitud El nuevo {@link TipoEstadoSolicitudEnum} de la
   *                            {@link Solicitud}.
   * @return la {@link Solicitud} con el estado actualizado.
   */
  private EstadoSolicitud addEstadoSolicitud(Solicitud solicitud, TipoEstadoSolicitudEnum tipoEstadoSolicitud,
      String comentario) {
    log.debug(
        "addEstadoSolicitud(Solicitud solicitud, TipoEstadoSolicitudEnum tipoEstadoSolicitud, String comentario) - start");

    EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
    estadoSolicitud.setEstado(tipoEstadoSolicitud);
    estadoSolicitud.setIdSolicitud(solicitud.getId());
    estadoSolicitud.setComentario(comentario);
    estadoSolicitud.setFechaEstado(LocalDateTime.now(ZoneId.of("Europe/Madrid")));

    EstadoSolicitud returnValue = estadoSolicitudRepository.save(estadoSolicitud);

    log.debug(
        "addEstadoSolicitud(Solicitud solicitud, TipoEstadoSolicitudEnum tipoEstadoSolicitud, String comentario) - end");
    return returnValue;
  }

  /**
   * Hace las comprobaciones necesarias para determinar si se puede crear un
   * {@link Proyecto} a partir de la {@link Solicitud}
   * 
   * @param id Id de la {@link Solicitud}.
   * @return true si se permite la creación / false si no se permite creación
   */
  @Override
  public Boolean isPosibleCrearProyecto(Long id) {
    Boolean posibleCrearProyecto = true;

    final Solicitud solicitud = repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));
    // Si la solicitud no está en estado CONCEDIDA no se puede crear el proyecto a
    // partir de la misma
    if (!solicitud.getEstado().getEstado().equals(TipoEstadoSolicitudEnum.CONCECIDA)) {
      posibleCrearProyecto = false;
    }

    // Si la solicitud ya está asignada a un proyecto no se podrá crear otro
    // proyecto para la solicitud
    if (posibleCrearProyecto && proyectoRepository.existsBySolicitudId(solicitud.getId())) {
      posibleCrearProyecto = false;
    }

    // Si el formulario de la solicitud no es de tipo ESTANDAR no se podrá crear el
    // proyecto a partir de ella
    if (posibleCrearProyecto && !solicitud.getFormularioSolicitud().equals(TipoFormularioSolicitudEnum.ESTANDAR)) {
      posibleCrearProyecto = false;
    }

    // Si no hay datos del proyecto en la solicitud, no se podrá crear el proyecto
    if (posibleCrearProyecto && !solicitudProyectoDatosRepository.existsBySolicitudId(solicitud.getId())) {
      posibleCrearProyecto = false;
    }

    return posibleCrearProyecto;
  }

  /**
   * Hace las comprobaciones para determinar si el usuario tiene la autorización
   * indicada tanto de manera independiente como dentro de la unidad organizativa
   * indicada.
   *
   * @param authority Permiso a comprobar.
   * @param unidad    unidad organizativa
   * @return true tiene autorización / false no tiene autorización
   */
  protected Boolean checkAuthority(String authority, String unidad) {
    log.debug("checkAuthority(String authority, String unidad) - start");

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    SgiMethodSecurityExpressionRoot sgiMethodSecurityExpressionRoot = new SgiMethodSecurityExpressionRoot(
        authentication);

    Boolean returnValue = Boolean.FALSE;

    if (StringUtils.isBlank(unidad)) {
      returnValue = sgiMethodSecurityExpressionRoot.hasAuthority(authority);
    } else {
      returnValue = (sgiMethodSecurityExpressionRoot.hasAuthority(authority)
          || sgiMethodSecurityExpressionRoot.hasAuthority(authority + "_" + unidad));
    }

    log.debug("checkAuthority(String authority, String unidad) - end");
    return returnValue;
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Solicitud}
   * puede ser modificada. También se utilizará para permitir la creación,
   * modificación o eliminación de ciertas entidades relacionadas con la propia
   * {@link Solicitud}.
   *
   * @param id Id del {@link Solicitud}.
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  @Override
  public Boolean modificable(Long id) {
    log.debug("modificable(Long id) - start");

    Solicitud solicitud = repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));

    String unidadGestionSolicitud = solicitud.getUnidadGestionRef();

    // solicitud activa para poder modificar
    boolean returnValue = solicitud.getActivo();

    // Se comprueba si el usuario tiene el permiso correspondiente
    Boolean isAdministradorOrGestor = checkAuthority("CSP-SOL-C", unidadGestionSolicitud);
    Boolean isInvestigador = checkAuthority("CSP-SOL-C-INV", unidadGestionSolicitud);

    // Si no tiene permisos ni de administrador o gestor ni investigador no se podrá
    // realizar la acción
    if (returnValue && !isAdministradorOrGestor && !isInvestigador) {
      returnValue = Boolean.FALSE;
    }

    if (returnValue && isAdministradorOrGestor) {
      returnValue = (solicitud.getEstado().getEstado().equals(TipoEstadoSolicitudEnum.BORRADOR)
          || solicitud.getEstado().getEstado().equals(TipoEstadoSolicitudEnum.PRESENTADA)
          || solicitud.getEstado().getEstado().equals(TipoEstadoSolicitudEnum.ADMITIDA_PROVISIONAL)
          || solicitud.getEstado().getEstado().equals(TipoEstadoSolicitudEnum.ALEGADA_ADMISION)
          || solicitud.getEstado().getEstado().equals(TipoEstadoSolicitudEnum.ADMITIDA_DEFINITIVA)
          || solicitud.getEstado().getEstado().equals(TipoEstadoSolicitudEnum.CONCECIDA_PROVISIONAL)
          || solicitud.getEstado().getEstado().equals(TipoEstadoSolicitudEnum.ALEGADA_CONCESION));
    }

    if (returnValue && isInvestigador) {
      returnValue = (solicitud.getEstado().getEstado().equals(TipoEstadoSolicitudEnum.BORRADOR)
          || solicitud.getEstado().getEstado().equals(TipoEstadoSolicitudEnum.EXCLUIDA_PROVISIONAL)
          || solicitud.getEstado().getEstado().equals(TipoEstadoSolicitudEnum.DENEGADA_PROVISIONAL));
    }

    log.debug("modificable(Long id) - end");
    return returnValue;
  }

}
