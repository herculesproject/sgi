package org.crue.hercules.sgi.csp.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoDatosNotFoundException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadConvocanteRepository;
import org.crue.hercules.sgi.csp.repository.DocumentoRequeridoSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.EstadoSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoDatosRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.predicate.SolicitudPredicateResolver;
import org.crue.hercules.sgi.csp.repository.specification.DocumentoRequeridoSolicitudSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.access.expression.SgiMethodSecurityExpressionRoot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

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
  private final DocumentoRequeridoSolicitudRepository documentoRequeridoSolicitudRepository;
  private final SolicitudDocumentoRepository solicitudDocumentoRepository;
  private final ConvocatoriaEntidadConvocanteRepository convocatoriaEntidadConvocanteRepository;
  private final SolicitudProyectoDatosRepository solicitudProyectoDatosRepository;
  private final SolicitudProyectoEquipoRepository solicitudProyectoEquipoRepository;
  private final SolicitudProyectoSocioRepository solicitudProyectoSocioRepository;

  public SolicitudServiceImpl(SolicitudRepository repository, EstadoSolicitudRepository estadoSolicitudRepository,
      ConfiguracionSolicitudRepository configuracionSolicitudRepository, ProyectoRepository proyectoRepository,
      SolicitudProyectoDatosRepository solicitudProyectoDatosRepository,
      DocumentoRequeridoSolicitudRepository documentoRequeridoSolicitudRepository,
      SolicitudDocumentoRepository solicitudDocumentoRepository,
      ConvocatoriaEntidadConvocanteRepository convocatoriaEntidadConvocanteRepository,
      SolicitudProyectoEquipoRepository solicitudProyectoEquipoRepository,
      SolicitudProyectoSocioRepository solicitudProyectoSocioRepository) {
    this.repository = repository;
    this.estadoSolicitudRepository = estadoSolicitudRepository;
    this.configuracionSolicitudRepository = configuracionSolicitudRepository;
    this.proyectoRepository = proyectoRepository;
    this.documentoRequeridoSolicitudRepository = documentoRequeridoSolicitudRepository;
    this.solicitudDocumentoRepository = solicitudDocumentoRepository;
    this.convocatoriaEntidadConvocanteRepository = convocatoriaEntidadConvocanteRepository;
    this.solicitudProyectoDatosRepository = solicitudProyectoDatosRepository;
    this.solicitudProyectoEquipoRepository = solicitudProyectoEquipoRepository;
    this.solicitudProyectoSocioRepository = solicitudProyectoSocioRepository;
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
    EstadoSolicitud estadoSolicitud = addEstadoSolicitud(solicitud, EstadoSolicitud.Estado.BORRADOR, null);

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
        if (data.getEstado().getEstado() == EstadoSolicitud.Estado.BORRADOR) {
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
  public Page<Solicitud> findAllRestringidos(String query, Pageable paging, List<String> unidadGestionRefs) {
    log.debug("findAll(String query, Pageable paging, List<String> unidadGestionRefs) - start");

    Specification<Solicitud> specs = SolicitudSpecifications.activos()
        .and(SolicitudSpecifications.unidadGestionRefIn(unidadGestionRefs))
        .and(SgiRSQLJPASupport.toSpecification(query, SolicitudPredicateResolver.getInstance()));

    Page<Solicitud> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging, List<String> unidadGestionRefs) - end");
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
  public Page<Solicitud> findAllTodosRestringidos(String query, Pageable paging, List<String> unidadGestionRefs) {
    log.debug("findAll(String query, Pageable paging, List<String> unidadGestionRefs) - start");

    Specification<Solicitud> specs = SolicitudSpecifications.unidadGestionRefIn(unidadGestionRefs)
        .and(SgiRSQLJPASupport.toSpecification(query, SolicitudPredicateResolver.getInstance()));

    Page<Solicitud> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging, List<String> unidadGestionRefs) - end");
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
   * Se hace el cambio de estado de "Borrador" a "Presentada".
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link Solicitud} actualizado.
   */
  @Override
  @Transactional
  public Solicitud presentarSolicitud(Long id) {

    log.debug("presentarSolicitud(Long idSolicitud) - start");

    Solicitud solicitud = repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));

    // VALIDACIONES

    // Estado
    Assert.isTrue(solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.BORRADOR,
        "La solicitud no se encuentra en un estado correcto.");

    // Existe una convocatoria asociada a la solicitud
    if (solicitud.getConvocatoria() != null) {

      // Se comprueba que la documentación requerida de la configuración de la
      // convocatoria solicitud ha sido adjuntada.
      Assert.isTrue(hasDocumentacionRequerida(solicitud.getId(), solicitud.getConvocatoria().getId()),
          "La solicitud no tiene la documentación requerida asociada.");

      Assert.isTrue(hasModalidadEntidadesConvocantes(solicitud.getConvocatoria().getId()),
          "Las entidades convocantes de la convocatoria deben tener rellenada la modalidad.");

    }

    // Si el formulario es de tipo Estándar
    if (solicitud.getFormularioSolicitud() == FormularioSolicitud.ESTANDAR) {

      SolicitudProyectoDatos solicitudProyectoDatos = solicitudProyectoDatosRepository
          .findBySolicitudId(solicitud.getId())
          .orElseThrow(() -> new SolicitudProyectoDatosNotFoundException(solicitud.getId()));

      Assert.notNull(solicitudProyectoDatos.getTitulo(), "El título de proyecto datos no puede ser null");
      Assert.notNull(solicitudProyectoDatos.getColaborativo(),
          "El campo colaborativo de proyecto datos no puede ser null");

      // En caso de ser colaborativo coordinador externo es obligatorio.
      if (solicitudProyectoDatos.getColaborativo()) {
        Assert.notNull(solicitudProyectoDatos.getCoordinadorExterno(),
            "El campo coordinador externo de proyecto datos no puede ser null");
      }

      Assert.isTrue(isSolicitanteMiembroEquipo(solicitudProyectoDatos.getId(), solicitud.getSolicitanteRef()),
          "El solicitante debe ser miembro del equipo.");

      if (solicitudProyectoDatos.getColaborativo() && solicitudProyectoDatos.getCoordinadorExterno()) {
        List<SolicitudProyectoSocio> solicitudProyectoSocios = solicitudProyectoSocioRepository
            .findAllBySolicitudProyectoDatosIdAndRolSocioCoordinadorTrue(solicitudProyectoDatos.getId());
        Assert.isTrue(!CollectionUtils.isEmpty(solicitudProyectoSocios),
            "Al menos debe existir un socio con Rol socio coordinador.");

      }

    }
    // Se cambia el estado de la solicitud a presentada

    EstadoSolicitud estadoSolicitud = addEstadoSolicitud(solicitud, EstadoSolicitud.Estado.PRESENTADA, null);

    // Actualiza el estado actual de la solicitud con el nuevo estado
    solicitud.setEstado(estadoSolicitud);

    Solicitud returnValue = repository.save(solicitud);

    log.debug("presentarSolicitud(Long idSolicitud) - end");
    return returnValue;

  }

  /**
   * Cambio de estado de "Presentada" a "Admitida provisionalmente".
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link Solicitud} actualizado.
   */
  @Override
  @Transactional
  public Solicitud admitirProvisionalmente(Long id) {
    log.debug("admitirProvisionalmente(Long idSolicitud) - start");

    Solicitud solicitud = repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));

    // Estado
    Assert.isTrue(solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.PRESENTADA,
        "La solicitud no se encuentra en un estado correcto.");

    // Se cambia el estado de la solicitud a "Admitida provisionalmente".

    EstadoSolicitud estadoSolicitud = addEstadoSolicitud(solicitud, EstadoSolicitud.Estado.ADMITIDA_PROVISIONAL, null);

    // Actualiza el estado actual de la solicitud con el nuevo estado
    solicitud.setEstado(estadoSolicitud);

    Solicitud returnValue = repository.save(solicitud);

    log.debug("admitirProvisionalmente(Long idSolicitud) - end");
    return returnValue;
  }

  /**
   * Cambio de estado de "Admitida provisionalmente" a "Admitida definitivamente".
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link Solicitud} actualizado.
   */
  @Override
  @Transactional
  public Solicitud admitirDefinitivamente(Long id) {
    log.debug("admitirDefinitivamente(Long idSolicitud) - start");

    Solicitud solicitud = repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));

    // Estado
    Assert.isTrue(
        solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.ADMITIDA_PROVISIONAL
            || solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.ALEGADA_ADMISION,
        "La solicitud no se encuentra en un estado correcto.");

    // Se cambia el estado de la solicitud a "Admitida definitivamente".

    EstadoSolicitud estadoSolicitud = addEstadoSolicitud(solicitud, EstadoSolicitud.Estado.ADMITIDA_DEFINITIVA, null);

    // Actualiza el estado actual de la solicitud con el nuevo estado
    solicitud.setEstado(estadoSolicitud);

    Solicitud returnValue = repository.save(solicitud);

    log.debug("admitirDefinitivamente(Long idSolicitud) - end");
    return returnValue;
  }

  /**
   * Cambio de estado de "Admitida definitivamente" a "Concedida provisional".
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link Solicitud} actualizado.
   */
  @Override
  @Transactional
  public Solicitud concederProvisionalmente(Long id) {
    log.debug("concederProvisionalmente(Long idSolicitud) - start");

    Solicitud solicitud = repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));

    // Estado
    Assert.isTrue(solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.ADMITIDA_DEFINITIVA,
        "La solicitud no se encuentra en un estado correcto.");

    // Se cambia el estado de la solicitud a "Concedida provisional".

    EstadoSolicitud estadoSolicitud = addEstadoSolicitud(solicitud, EstadoSolicitud.Estado.CONCECIDA_PROVISIONAL, null);

    // Actualiza el estado actual de la solicitud con el nuevo estado
    solicitud.setEstado(estadoSolicitud);

    Solicitud returnValue = repository.save(solicitud);

    log.debug("concederProvisionalmente(Long idSolicitud) - end");
    return returnValue;
  }

  /**
   * Cambio de estado de "Concedida provisional" o "Alegada concesión" a
   * "Concedida".
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link Solicitud} actualizado.
   */
  @Override
  @Transactional
  public Solicitud conceder(Long id) {
    log.debug("conceder(Long idSolicitud) - start");

    Solicitud solicitud = repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));

    // Estado
    Assert.isTrue(
        solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.CONCECIDA_PROVISIONAL
            || solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.ALEGADA_CONCESION,
        "La solicitud no se encuentra en un estado correcto.");

    // Se cambia el estado de la solicitud a "Concedida".

    EstadoSolicitud estadoSolicitud = addEstadoSolicitud(solicitud, EstadoSolicitud.Estado.CONCECIDA, null);

    // Actualiza el estado actual de la solicitud con el nuevo estado
    solicitud.setEstado(estadoSolicitud);

    Solicitud returnValue = repository.save(solicitud);

    log.debug("conceder(Long idSolicitud) - end");
    return returnValue;
  }

  /**
   * Cambio de estado de "Presentada" a "Excluir provisionalmente".
   * 
   * @param id         Identificador de {@link Solicitud}.
   * @param comentario Comentario de {@link EstadoSolicitud}.
   * @return {@link Solicitud} actualizado.
   */
  @Override
  @Transactional
  public Solicitud exlcluirProvisionalmente(Long id, String comentario) {
    log.debug("exlcluirProvisionalmente(Long idSolicitud, String comentario) - start");

    Solicitud solicitud = repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));

    // Estado
    Assert.isTrue(solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.PRESENTADA,
        "La solicitud no se encuentra en un estado correcto.");

    // Comentario
    Assert.notNull(comentario, "El comentario no puede ser null para el cambio de estado.");

    // Se cambia el estado de la solicitud a "Excluir provisionalmente".

    EstadoSolicitud estadoSolicitud = addEstadoSolicitud(solicitud, EstadoSolicitud.Estado.EXCLUIDA_PROVISIONAL,
        comentario);

    // Actualiza el estado actual de la solicitud con el nuevo estado
    solicitud.setEstado(estadoSolicitud);

    Solicitud returnValue = repository.save(solicitud);

    log.debug("exlcluirProvisionalmente(Long idSolicitud, String comentario) - end");
    return returnValue;
  }

  /**
   * Cambio de estado de "Excluida provisional" a "Alegada admisión".
   * 
   * @param id         Identificador de {@link Solicitud}.
   * @param comentario Comentario de {@link EstadoSolicitud}.
   * @return {@link Solicitud} actualizado.
   */
  @Override
  @Transactional
  public Solicitud alegarAdmision(Long id, String comentario) {
    log.debug("alegarAdmision(Long idSolicitud, String comentario) - start");

    Solicitud solicitud = repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));

    // Estado
    Assert.isTrue(solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.EXCLUIDA_PROVISIONAL,
        "La solicitud no se encuentra en un estado correcto.");

    // Comentario
    Assert.notNull(comentario, "El comentario no puede ser null para el cambio de estado.");

    // Se cambia el estado de la solicitud a "Alegada admisión".

    EstadoSolicitud estadoSolicitud = addEstadoSolicitud(solicitud, EstadoSolicitud.Estado.ALEGADA_ADMISION,
        comentario);

    // Actualiza el estado actual de la solicitud con el nuevo estado
    solicitud.setEstado(estadoSolicitud);

    Solicitud returnValue = repository.save(solicitud);

    log.debug("alegarAdmision(Long idSolicitud, String comentario) - end");
    return returnValue;
  }

  /**
   * Cambio de estado de "Alegada admisión" a "Excluida".
   * 
   * @param id         Identificador de {@link Solicitud}.
   * @param comentario Comentario de {@link EstadoSolicitud}.
   * @return {@link Solicitud} actualizado.
   */
  @Override
  @Transactional
  public Solicitud excluir(Long id, String comentario) {
    log.debug("excluir(Long idSolicitud, String comentario) - start");

    Solicitud solicitud = repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));

    // Estado
    Assert.isTrue(solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.ALEGADA_ADMISION,
        "La solicitud no se encuentra en un estado correcto.");

    // Comentario
    Assert.notNull(comentario, "El comentario no puede ser null para el cambio de estado.");

    // Se cambia el estado de la solicitud a "Excluida".

    EstadoSolicitud estadoSolicitud = addEstadoSolicitud(solicitud, EstadoSolicitud.Estado.EXCLUIDA, comentario);

    // Actualiza el estado actual de la solicitud con el nuevo estado
    solicitud.setEstado(estadoSolicitud);

    Solicitud returnValue = repository.save(solicitud);

    log.debug("excluir(Long idSolicitud, String comentario) - end");
    return returnValue;
  }

  /**
   * Cambio de estado de "Admitida definitiva" a "Denegada provisional".
   * 
   * @param id         Identificador de {@link Solicitud}.
   * @param comentario Comentario de {@link EstadoSolicitud}.
   * @return {@link Solicitud} actualizado.
   */
  @Override
  @Transactional
  public Solicitud denegarProvisionalmente(Long id, String comentario) {
    log.debug("denegarProvisionalmente(Long idSolicitud, String comentario) - start");

    Solicitud solicitud = repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));

    // Estado
    Assert.isTrue(solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.ADMITIDA_DEFINITIVA,
        "La solicitud no se encuentra en un estado correcto.");

    // Comentario
    Assert.notNull(comentario, "El comentario no puede ser null para el cambio de estado.");

    // Se cambia el estado de la solicitud a "Denegada provisional".

    EstadoSolicitud estadoSolicitud = addEstadoSolicitud(solicitud, EstadoSolicitud.Estado.DENEGADA_PROVISIONAL,
        comentario);

    // Actualiza el estado actual de la solicitud con el nuevo estado
    solicitud.setEstado(estadoSolicitud);

    Solicitud returnValue = repository.save(solicitud);

    log.debug("denegarProvisionalmente(Long idSolicitud, String comentario) - end");
    return returnValue;
  }

  /**
   * Cambio de estado de "Denegada provisional" a "Alegada concesión".
   * 
   * @param id         Identificador de {@link Solicitud}.
   * @param comentario Comentario de {@link EstadoSolicitud}.
   * @return {@link Solicitud} actualizado.
   */
  @Override
  @Transactional
  public Solicitud alegarConcesion(Long id, String comentario) {
    log.debug("alegarConcesion(Long idSolicitud, String comentario) - start");

    Solicitud solicitud = repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));

    // Estado
    Assert.isTrue(solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.DENEGADA_PROVISIONAL,
        "La solicitud no se encuentra en un estado correcto.");

    // Comentario
    Assert.notNull(comentario, "El comentario no puede ser null para el cambio de estado.");

    // Se cambia el estado de la solicitud a "Alegada concesión".

    EstadoSolicitud estadoSolicitud = addEstadoSolicitud(solicitud, EstadoSolicitud.Estado.ALEGADA_CONCESION,
        comentario);

    // Actualiza el estado actual de la solicitud con el nuevo estado
    solicitud.setEstado(estadoSolicitud);

    Solicitud returnValue = repository.save(solicitud);

    log.debug("alegarConcesion(Long idSolicitud, String comentario) - end");
    return returnValue;
  }

  /**
   * Cambio de estado de "Alegada concesión" a "Denegada".
   * 
   * @param id         Identificador de {@link Solicitud}.
   * @param comentario Comentario de {@link EstadoSolicitud}.
   * @return {@link Solicitud} actualizado.
   */
  @Override
  @Transactional
  public Solicitud denegar(Long id, String comentario) {
    log.debug("denegar(Long idSolicitud, String comentario) - start");

    Solicitud solicitud = repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));

    // Estado
    Assert.isTrue(solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.ALEGADA_CONCESION,
        "La solicitud no se encuentra en un estado correcto.");

    // Comentario
    Assert.notNull(comentario, "El comentario no puede ser null para el cambio de estado.");

    // Se cambia el estado de la solicitud a "Denegada provisional".

    EstadoSolicitud estadoSolicitud = addEstadoSolicitud(solicitud, EstadoSolicitud.Estado.DENEGADA, comentario);

    // Actualiza el estado actual de la solicitud con el nuevo estado
    solicitud.setEstado(estadoSolicitud);

    Solicitud returnValue = repository.save(solicitud);

    log.debug("denegar(Long idSolicitud, String comentario) - end");
    return returnValue;
  }

  /**
   * Cambio de estado de "Presentada", "Admitida provisional", "Excluida
   * provisional", "Admitida definitiva", "Denegada provisional" o "Concedida
   * provisional" a "Desistida".
   * 
   * @param id         Identificador de {@link Solicitud}.
   * @param comentario Comentario de {@link EstadoSolicitud}.
   * @return {@link Solicitud} actualizado.
   */
  @Override
  @Transactional
  public Solicitud desistir(Long id, String comentario) {
    log.debug("desistir(Long idSolicitud, String comentario) - start");

    Solicitud solicitud = repository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));

    // Estado
    Assert.isTrue(
        solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.PRESENTADA
            || solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.ADMITIDA_PROVISIONAL
            || solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.ADMITIDA_DEFINITIVA
            || solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.EXCLUIDA_PROVISIONAL
            || solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.DENEGADA_PROVISIONAL
            || solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.CONCECIDA_PROVISIONAL,
        "La solicitud no se encuentra en un estado correcto.");

    // Comentario
    Assert.notNull(comentario, "El comentario no puede ser null para el cambio de estado.");

    // Se cambia el estado de la solicitud a "Desistida".

    EstadoSolicitud estadoSolicitud = addEstadoSolicitud(solicitud, EstadoSolicitud.Estado.DESISTIDA, comentario);

    // Actualiza el estado actual de la solicitud con el nuevo estado
    solicitud.setEstado(estadoSolicitud);

    Solicitud returnValue = repository.save(solicitud);

    log.debug("desistir(Long idSolicitud, String comentario) - end");
    return returnValue;
  }

  /**
   * Comprueba si se cumplen todas las condiciones para que la solicitud pueda
   * pasar al estado de "Presentada."
   * 
   * @param id Id del {@link Solicitud}.
   * @return <code>true</code> Cumple condiciones para el cambio de estado.
   *         <code>false</code>No cumple condiciones.
   */
  @Override
  public Boolean cumpleValidacionesPresentada(Long id) {
    log.debug("cumpleValidacionesPresentada(Long id) - start");
    Solicitud solicitud = repository.findById(id).get();

    if (solicitud == null || solicitud.getEstado().getEstado() != EstadoSolicitud.Estado.BORRADOR) {
      log.debug("cumpleValidacionesPresentada(Long id) - end");
      return Boolean.FALSE;
    }

    if (solicitud.getConvocatoria() != null
        && (!hasDocumentacionRequerida(solicitud.getId(), solicitud.getConvocatoria().getId())
            || !hasModalidadEntidadesConvocantes(solicitud.getConvocatoria().getId()))) {
      log.debug("cumpleValidacionesPresentada(Long id) - end");
      return Boolean.FALSE;
    }

    // Si el formulario es de tipo Estándar
    if (solicitud.getFormularioSolicitud() == FormularioSolicitud.ESTANDAR) {

      SolicitudProyectoDatos solicitudProyectoDatos = solicitudProyectoDatosRepository
          .findBySolicitudId(solicitud.getId())
          .orElseThrow(() -> new SolicitudProyectoDatosNotFoundException(solicitud.getId()));

      // En caso de que no tenga titulo o sea colaborativo y no tenga coordinador
      // externo releno
      if (StringUtils.isEmpty(solicitudProyectoDatos.getTitulo())
          || (solicitudProyectoDatos.getColaborativo() && solicitudProyectoDatos.getCoordinadorExterno() == null)) {
        log.debug("cumpleValidacionesPresentada(Long id) - end");
        return Boolean.FALSE;
      }

      if (!isSolicitanteMiembroEquipo(solicitudProyectoDatos.getId(), solicitud.getSolicitanteRef())) {
        log.debug("cumpleValidacionesPresentada(Long id) - end");
        return Boolean.FALSE;
      }

      if (solicitudProyectoDatos.getColaborativo() && solicitudProyectoDatos.getCoordinadorExterno()) {
        List<SolicitudProyectoSocio> solicitudProyectoSocios = solicitudProyectoSocioRepository
            .findAllBySolicitudProyectoDatosIdAndRolSocioCoordinadorTrue(solicitudProyectoDatos.getId());
        log.debug("cumpleValidacionesPresentada(Long id) - end");
        return !CollectionUtils.isEmpty(solicitudProyectoSocios);

      }
    }
    log.debug("cumpleValidacionesPresentada(Long id) - end");
    return Boolean.TRUE;
  }

  /**
   * Comprueba si la solicitud tiene la asociada la documentación requerida en la
   * configuración de la solicitud de la convocatoria.
   * 
   * @param idSolicitud    Identificador de {@link Solicitud}.
   * @param idConvocatoria Identificador de {@link Convocatoria}.
   * @return <code>true</code> En caso de tener asociada la documentación;
   *         <code>false</code>Documentación no asociada.
   */
  private Boolean hasDocumentacionRequerida(Long idSolicitud, Long idConvocatoria) {
    log.debug("hasDocumentacionRequerida(Long idConvocatoria) - start");

    Specification<DocumentoRequeridoSolicitud> specByConvocatoria = DocumentoRequeridoSolicitudSpecifications
        .byConvocatoriaId(idConvocatoria);
    Specification<DocumentoRequeridoSolicitud> specs = Specification.where(specByConvocatoria);

    List<DocumentoRequeridoSolicitud> documentosRequeridosSolicitud = documentoRequeridoSolicitudRepository
        .findAll(specs);

    if (CollectionUtils.isEmpty(documentosRequeridosSolicitud)) {
      return Boolean.TRUE;
    }

    List<Long> tiposDocumentoRequeridosSolicitud = documentosRequeridosSolicitud.stream()
        .map(documentoRequerido -> documentoRequerido.getTipoDocumento().getId()).collect(Collectors.toList());

    List<SolicitudDocumento> solicitudDocumentos = solicitudDocumentoRepository
        .findAllByTipoDocumentoIdInAndSolicitudId(tiposDocumentoRequeridosSolicitud, idSolicitud);

    log.debug("hasDocumentacionRequerida(Long idConvocatoria) - end");

    return !CollectionUtils.isEmpty(solicitudDocumentos);
  }

  /**
   * Comprueba si todas las entidades convocantes asociadas a la convocatoria de
   * la solicitud tienen cumplimentada la modalidad.
   * 
   * @param idConvocatoria Identificador de {@link Convocatoria}.
   * @return <code>true</code> Modalidad cumplimentada;
   *         <code>false</code>Modalidad no cumplimentada.
   */
  private Boolean hasModalidadEntidadesConvocantes(Long idConvocatoria) {
    log.debug("hasModalidadEntidadesConvocantes(Long idConvocatoria) - start");

    // Se comprueba que todas las entidades convocantes tienen rellenada una
    // modalidad.
    List<ConvocatoriaEntidadConvocante> convocatoriasEntidadConvocante = convocatoriaEntidadConvocanteRepository
        .findByProgramaIsNullAndConvocatoriaId(idConvocatoria);

    log.debug("hasModalidadEntidadesConvocantes(Long idConvocatoria) - end");
    return CollectionUtils.isEmpty(convocatoriasEntidadConvocante);
  }

  /**
   * Comprueba si el solicitante es miembro del equipo.
   * 
   * @param idSolicitudProyectoDatos Identificador de
   *                                 {@link SolicitudProyectoDatos}.
   * @param solicitanteRef           Solicitante ref.
   * @return <code>true</code> El solicitante es miembro del equipo;
   *         <code>false</code>No pertenece al equipo.
   */
  private Boolean isSolicitanteMiembroEquipo(Long idSolicitudProyectoDatos, String solicitanteRef) {
    // El solicitante debe pertenecer al equipo
    List<SolicitudProyectoEquipo> solicitudProyectoEquipos = solicitudProyectoEquipoRepository
        .findAllBySolicitudProyectoDatosIdAndPersonaRef(idSolicitudProyectoDatos, solicitanteRef);

    return !CollectionUtils.isEmpty(solicitudProyectoEquipos);
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
   * @param solicitud la {@link Solicitud} para la que se añade el nuevo estado.
   * @param estado    El nuevo {@link EstadoSolicitud.Estado} de la
   *                  {@link Solicitud}.
   * @return la {@link Solicitud} con el estado actualizado.
   */
  private EstadoSolicitud addEstadoSolicitud(Solicitud solicitud, EstadoSolicitud.Estado estado, String comentario) {
    log.debug(
        "addEstadoSolicitud(Solicitud solicitud, TipoEstadoSolicitudEnum tipoEstadoSolicitud, String comentario) - start");

    EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
    estadoSolicitud.setEstado(estado);
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
    if (!solicitud.getEstado().getEstado().equals(EstadoSolicitud.Estado.CONCECIDA)) {
      posibleCrearProyecto = false;
    }

    // Si la solicitud ya está asignada a un proyecto no se podrá crear otro
    // proyecto para la solicitud
    if (posibleCrearProyecto && proyectoRepository.existsBySolicitudId(solicitud.getId())) {
      posibleCrearProyecto = false;
    }

    // Si el formulario de la solicitud no es de tipo ESTANDAR no se podrá crear el
    // proyecto a partir de ella
    if (posibleCrearProyecto && solicitud.getFormularioSolicitud() != FormularioSolicitud.ESTANDAR) {
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
      returnValue = (solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.BORRADOR
          || solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.PRESENTADA
          || solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.ADMITIDA_PROVISIONAL
          || solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.ALEGADA_ADMISION
          || solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.ADMITIDA_DEFINITIVA
          || solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.CONCECIDA_PROVISIONAL
          || solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.ALEGADA_CONCESION);
    }

    if (returnValue && isInvestigador) {
      returnValue = (solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.BORRADOR
          || solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.EXCLUIDA_PROVISIONAL
          || solicitud.getEstado().getEstado() == EstadoSolicitud.Estado.DENEGADA_PROVISIONAL);
    }

    log.debug("modificable(Long id) - end");
    return returnValue;
  }

}
