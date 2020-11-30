package org.crue.hercules.sgi.csp.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.enums.TipoEstadoSolicitudEnum;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.EstadoSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudService;
import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

  public SolicitudServiceImpl(SolicitudRepository repository, EstadoSolicitudRepository estadoSolicitudRepository,
      ConfiguracionSolicitudRepository configuracionSolicitudRepository) {
    this.repository = repository;
    this.estadoSolicitudRepository = estadoSolicitudRepository;
    this.configuracionSolicitudRepository = configuracionSolicitudRepository;
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
   * @param solicitud               solicitudActualizar {@link Solicitud} con los
   *                                datos actualizados.
   * @param unidadGestionRefs       lista de referencias de las unidades de
   *                                gestion permitidas para el usuario.
   * @param isAdministradorOrGestor Indicador de si el usuario que realiza la
   *                                acutalización es administrador o gestor.
   * @return {@link Solicitud} actualizado.
   */
  @Override
  @Transactional
  public Solicitud update(Solicitud solicitud, List<String> unidadGestionRefs, Boolean isAdministradorOrGestor) {
    log.debug("update(Solicitud solicitud) - start");

    Assert.notNull(solicitud.getId(), "Id no puede ser null para actualizar Solicitud");

    Assert.notNull(solicitud.getSolicitanteRef(), "El solicitante no puede ser null para actualizar Solicitud");

    Assert.isTrue(
        solicitud.getConvocatoria() != null
            || (solicitud.getConvocatoriaExterna() != null && !solicitud.getConvocatoriaExterna().isEmpty()),
        "Se debe seleccionar una convocatoria del SGI o convocatoria externa para actualizar Solicitud");

    if (isAdministradorOrGestor) {
      Assert.isTrue(solicitud.getEstado().getEstado().getValue().equals(TipoEstadoSolicitudEnum.BORRADOR.getValue())
          || solicitud.getEstado().getEstado().getValue().equals(TipoEstadoSolicitudEnum.PRESENTADA.getValue())
          || solicitud.getEstado().getEstado().getValue()
              .equals(TipoEstadoSolicitudEnum.ADMITIDA_PROVISIONAL.getValue())
          || solicitud.getEstado().getEstado().getValue().equals(TipoEstadoSolicitudEnum.ALEGADA_ADMISION.getValue())
          || solicitud.getEstado().getEstado().getValue().equals(TipoEstadoSolicitudEnum.ADMITIDA_DEFINITIVA.getValue())
          || solicitud.getEstado().getEstado().getValue()
              .equals(TipoEstadoSolicitudEnum.CONCECIDA_PROVISIONAL.getValue())
          || solicitud.getEstado().getEstado().getValue().equals(TipoEstadoSolicitudEnum.ALEGADA_CONCESION.getValue()),
          "La solicitud no se encuentra en un estado adecuado para ser actualizada");
    } else {
      Assert.isTrue(
          solicitud.getEstado().getEstado().getValue().equals(TipoEstadoSolicitudEnum.BORRADOR.getValue())
              || solicitud.getEstado().getEstado().getValue()
                  .equals(TipoEstadoSolicitudEnum.EXCLUIDA_PROVISIONAL.getValue())
              || solicitud.getEstado().getEstado().getValue()
                  .equals(TipoEstadoSolicitudEnum.DENEGADA_PROVISIONAL.getValue()),
          "La solicitud no se encuentra en un estado adecuado para ser actualizada");
    }

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

    List<QueryCriteria> querySinReferenciaConvocatoria = query.stream()
        .filter(queryCriteria -> !queryCriteria.getKey().equals("referenciaConvocatoria")).collect(Collectors.toList());

    Specification<Solicitud> specByQuery = new QuerySpecification<Solicitud>(querySinReferenciaConvocatoria);
    Specification<Solicitud> specByUnidadGestionRefIn = SolicitudSpecifications.unidadGestionRefIn(unidadGestionRefs);
    Specification<Solicitud> specs = Specification.where(specByUnidadGestionRefIn).and(specByQuery);

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

}
