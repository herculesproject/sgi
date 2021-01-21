package org.crue.hercules.sgi.csp.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.csp.enums.TipoEstadoProyectoEnum;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadFinanciadoraRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.EstadoProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ModeloUnidadRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadFinanciadoraService;
import org.crue.hercules.sgi.csp.service.ProyectoService;
import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio implementación para la gestión de {@link Proyecto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoServiceImpl implements ProyectoService {

  /**
   * Valor por defecto del atributo ajena en la copia de entidades financiadoras
   */
  private static final Boolean DEFAULT_COPY_ENTIDAD_FINANCIADORA_AJENA_VALUE = Boolean.FALSE;

  private final ProyectoRepository repository;
  private final EstadoProyectoRepository estadoProyectoRepository;
  private final ModeloUnidadRepository modeloUnidadRepository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ConvocatoriaEntidadFinanciadoraRepository convocatoriaEntidadFinanciadoraRepository;
  private final ProyectoEntidadFinanciadoraService proyectoEntidadFinanciadoraService;

  public ProyectoServiceImpl(ProyectoRepository repository, EstadoProyectoRepository estadoProyectoRepository,
      ModeloUnidadRepository modeloUnidadRepository, ConvocatoriaRepository convocatoriaRepository,
      ConvocatoriaEntidadFinanciadoraRepository convocatoriaEntidadFinanciadoraRepository,
      ProyectoEntidadFinanciadoraService proyectoEntidadFinanciadoraService) {
    this.repository = repository;
    this.estadoProyectoRepository = estadoProyectoRepository;
    this.modeloUnidadRepository = modeloUnidadRepository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.convocatoriaEntidadFinanciadoraRepository = convocatoriaEntidadFinanciadoraRepository;
    this.proyectoEntidadFinanciadoraService = proyectoEntidadFinanciadoraService;
  }

  /**
   * Guarda la entidad {@link Proyecto}.
   * 
   * @param proyecto la entidad {@link Proyecto} a guardar.
   * @return proyecto la entidad {@link Proyecto} persistida.
   */
  @Override
  @Transactional
  public Proyecto create(Proyecto proyecto) {
    log.debug("create(Proyecto proyecto) - start");
    Assert.isNull(proyecto.getId(), "Proyecto id tiene que ser null para crear un Proyecto");
    // TODO: Add right authority
    Assert.isTrue(SgiSecurityContextHolder.hasAuthorityForUO("CSP-PRO-C", proyecto.getUnidadGestionRef()),
        "La Unidad de Gestión no es gestionable por el usuario");
    this.validarDatos(proyecto);

    proyecto.setActivo(Boolean.TRUE);

    // Crea el proyecto
    repository.save(proyecto);

    // Crea el estado inicial del proyecto
    EstadoProyecto estadoProyecto = addEstadoProyecto(proyecto, TipoEstadoProyectoEnum.BORRADOR, null);

    proyecto.setEstado(estadoProyecto);
    // Actualiza el estado actual del proyecto con el nuevo estado
    Proyecto returnValue = repository.save(proyecto);

    // Si hay asignada una convocatoria se deben de rellenar las entidades
    // correspondientes con los datos de la convocatoria
    if (proyecto.getConvocatoria() != null) {
      // TODO implementar a medida que se vayan haciendo las pestañas de proyecto
      this.guardarDatosEntidadesRelacionadas(proyecto.getConvocatoria());
      this.copyEntidadesFinanciadoras(proyecto.getId(), proyecto.getConvocatoria().getId());
    }

    log.debug("create(Proyecto proyecto) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link Proyecto}.
   * 
   * @param proyectoActualizar proyectoActualizar {@link Proyecto} con los datos
   *                           actualizados.
   * @return {@link Proyecto} actualizado.
   */
  @Override
  @Transactional
  public Proyecto update(Proyecto proyectoActualizar) {
    log.debug("update(Proyecto proyecto) - start");
    // TODO: Add right authority
    Assert.isTrue(SgiSecurityContextHolder.hasAuthorityForUO("CSP-PRO-C", proyectoActualizar.getUnidadGestionRef()),
        "La Unidad de Gestión no es gestionable por el usuario");

    Assert.isTrue(
        !proyectoActualizar.getEstado().getEstado().equals(TipoEstadoProyectoEnum.FINALIZADO)
            && !proyectoActualizar.getEstado().getEstado().equals(TipoEstadoProyectoEnum.CANCELADO),
        "El proyecto no está en un estado en el que puede ser actualizado");

    this.validarDatos(proyectoActualizar);

    return repository.findById(proyectoActualizar.getId()).map((data) -> {

      Assert.isTrue(
          proyectoActualizar.getEstado().getId() == data.getEstado().getId()
              && ((proyectoActualizar.getConvocatoria() == null && data.getConvocatoria() == null)
                  || (proyectoActualizar.getConvocatoria() != null && data.getConvocatoria() != null
                      && proyectoActualizar.getConvocatoria().equals(data.getConvocatoria())))
              && ((proyectoActualizar.getSolicitud() == null && data.getSolicitud() == null)
                  || (proyectoActualizar.getSolicitud() != null && data.getSolicitud() != null
                      && proyectoActualizar.getSolicitud().equals(data.getSolicitud()))),
          "Existen campos del proyecto modificados que no se pueden modificar");

      data.setAcronimo(proyectoActualizar.getAcronimo());
      data.setAmbitoGeografico(proyectoActualizar.getAmbitoGeografico());
      data.setAnualidades(proyectoActualizar.getAnualidades());
      data.setClasificacionCVN(proyectoActualizar.getClasificacionCVN());
      data.setCodigoExterno(proyectoActualizar.getCodigoExterno());
      data.setColaborativo(proyectoActualizar.getColaborativo());
      data.setConfidencial(proyectoActualizar.getConfidencial());
      data.setContratos(proyectoActualizar.getContratos());
      data.setConvocatoriaExterna(proyectoActualizar.getConvocatoriaExterna());
      data.setCoordinadorExterno(proyectoActualizar.getCoordinadorExterno());
      data.setCosteHora(proyectoActualizar.getCosteHora());
      data.setFacturacion(proyectoActualizar.getFacturacion());
      data.setFechaFin(proyectoActualizar.getFechaFin());
      data.setFechaInicio(proyectoActualizar.getFechaInicio());
      data.setFinalidad(proyectoActualizar.getFinalidad());
      data.setFinalista(proyectoActualizar.getFinalista());
      data.setIva(proyectoActualizar.getIva());
      data.setLimitativo(proyectoActualizar.getLimitativo());
      data.setModeloEjecucion(proyectoActualizar.getModeloEjecucion());
      data.setObservaciones(proyectoActualizar.getObservaciones());
      data.setPaquetesTrabajo(proyectoActualizar.getPaquetesTrabajo());
      data.setPlantillaHojaFirma(proyectoActualizar.getPlantillaHojaFirma());
      data.setPlantillaJustificacion(proyectoActualizar.getPlantillaJustificacion());
      data.setTimesheet(proyectoActualizar.getTimesheet());
      data.setTipoHorasAnuales(proyectoActualizar.getTipoHorasAnuales());
      data.setTitulo(proyectoActualizar.getTitulo());
      data.setUniSubcontratada(proyectoActualizar.getUniSubcontratada());
      data.setUnidadGestionRef(proyectoActualizar.getUnidadGestionRef());

      Proyecto returnValue = repository.save(data);
      log.debug("update(Proyecto proyecto) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoNotFoundException(proyectoActualizar.getId()));
  }

  /**
   * Reactiva el {@link Proyecto}.
   *
   * @param id Id del {@link Proyecto}.
   * @return la entidad {@link Proyecto} persistida.
   */
  @Override
  @Transactional
  public Proyecto enable(Long id) {
    log.debug("enable(Long id) - start");

    Assert.notNull(id, "Proyecto id no puede ser null para reactivar un Proyecto");

    return repository.findById(id).map(proyecto -> {
      // TODO: Add right authority
      Assert.isTrue(SgiSecurityContextHolder.hasAuthorityForUO("CSP-PRO-C", proyecto.getUnidadGestionRef()),
          "El proyecto pertenece a una Unidad de Gestión no gestionable por el usuario");

      if (proyecto.getActivo()) {
        // Si esta activo no se hace nada
        return proyecto;
      }

      proyecto.setActivo(true);

      Proyecto returnValue = repository.save(proyecto);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoNotFoundException(id));
  }

  /**
   * Desactiva el {@link Proyecto}.
   *
   * @param id Id del {@link Proyecto}.
   * @return la entidad {@link Proyecto} persistida.
   */
  @Override
  @Transactional
  public Proyecto disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "Proyecto id no puede ser null para desactivar un Proyecto");

    return repository.findById(id).map(proyecto -> {
      // TODO: Add right authority
      Assert.isTrue(SgiSecurityContextHolder.hasAuthorityForUO("CSP-PRO-C", proyecto.getUnidadGestionRef()),
          "El proyecto pertenece a una Unidad de Gestión no gestionable por el usuario");

      if (!proyecto.getActivo()) {
        // Si no esta activo no se hace nada
        return proyecto;
      }

      proyecto.setActivo(false);

      Proyecto returnValue = repository.save(proyecto);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoNotFoundException(id));
  }

  /**
   * Comprueba la existencia del {@link Proyecto} por id.
   *
   * @param id el id de la entidad {@link Proyecto}.
   * @return true si existe y false en caso contrario.
   */
  @Override
  public boolean existsById(final Long id) {
    log.debug("existsById(final Long id)  - start", id);
    final boolean existe = repository.existsById(id);
    log.debug("existsById(final Long id)  - end", id);
    return existe;
  }

  /**
   * Obtiene el {@link ModeloEjecucion} asignada al {@link Proyecto}.
   * 
   * @param id Id del {@link Proyecto}.
   * @return {@link ModeloEjecucion} asignado
   */
  @Override
  public ModeloEjecucion getModeloEjecucion(Long id) {
    log.debug("getModeloEjecucion(Long id) - start");

    Optional<ModeloEjecucion> returnValue = null;
    if (repository.existsById(id)) {
      returnValue = repository.getModeloEjecucion(id);
    } else {
      throw (new ProyectoNotFoundException(id));
    }
    log.debug("getModeloEjecucion(Long id) - end");
    return returnValue.isPresent() ? returnValue.get() : null;
  }

  /**
   * Obtiene una entidad {@link Proyecto} por id.
   * 
   * @param id Identificador de la entidad {@link Proyecto}.
   * @return Proyecto la entidad {@link Proyecto}.
   */
  @Override
  public Proyecto findById(Long id) {
    log.debug("findById(Long id) - start");
    final Proyecto returnValue = repository.findById(id).orElseThrow(() -> new ProyectoNotFoundException(id));

    // TODO: Add right authority
    Assert.isTrue(
        SgiSecurityContextHolder.hasAnyAuthorityForUO(new String[] { "CSP-PRO-C", "CSP-PRO-V-INV" },
            returnValue.getUnidadGestionRef()),
        "El proyecto pertenece a una Unidad de Gestión no gestionable por el usuario");

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Proyecto} activas paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Proyecto} activas paginadas y
   *         filtradas.
   */
  @Override
  public Page<Proyecto> findAllRestringidos(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - start");
    if (query == null) {
      query = new ArrayList<>();
    }

    Specification<Proyecto> specByQuery = new QuerySpecification<Proyecto>(query);
    Specification<Proyecto> specActivos = ProyectoSpecifications.activos();
    Specification<Proyecto> specs = Specification.where(specActivos).and(specByQuery);

    // TODO: Add right authority
    // No tiene acceso a todos los UO
    if (!SgiSecurityContextHolder.hasAuthority("CSP-PRO-C")) {
      Specification<Proyecto> specByUnidadGestionRefIn = ProyectoSpecifications
          .unidadGestionRefIn(SgiSecurityContextHolder.getUOsForAuthority("CSP-PRO-C"));
      specs = Specification.where(specActivos).and(specByUnidadGestionRefIn).and(specByQuery);
    }

    Page<Proyecto> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Proyecto} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Proyecto} paginadas y filtradas.
   */
  @Override
  public Page<Proyecto> findAllTodosRestringidos(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - start");
    if (query == null) {
      query = new ArrayList<>();
    }

    Specification<Proyecto> specByQuery = new QuerySpecification<Proyecto>(query);
    Specification<Proyecto> specs = Specification.where(specByQuery);

    // TODO: Add right authority
    // No tiene acceso a todos los UO
    if (!SgiSecurityContextHolder.hasAuthority("CSP-PRO-C")) {
      Specification<Proyecto> specByUnidadGestionRefIn = ProyectoSpecifications
          .unidadGestionRefIn(SgiSecurityContextHolder.getUOsForAuthority("CSP-PRO-C"));
      specs = Specification.where(specByUnidadGestionRefIn).and(specByQuery);
    }

    // TODO implementar buscador avanzado

    Page<Proyecto> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Añade el nuevo {@link EstadoProyecto} y actualiza la {@link Proyecto} con
   * dicho estado.
   * 
   * @param proyecto           la {@link Proyecto} para la que se añade el nuevo
   *                           estado.
   * @param tipoEstadoProyecto El nuevo {@link TipoEstadoProyectoEnum} de la
   *                           {@link Proyecto}.
   * @return la {@link Proyecto} con el estado actualizado.
   */
  private EstadoProyecto addEstadoProyecto(Proyecto proyecto, TipoEstadoProyectoEnum tipoEstadoProyecto,
      String comentario) {
    log.debug(
        "addEstadoProyecto(Proyecto proyecto, TipoEstadoProyectoEnum tipoEstadoProyecto, String comentario) - start");

    EstadoProyecto estadoProyecto = new EstadoProyecto();
    estadoProyecto.setEstado(tipoEstadoProyecto);
    estadoProyecto.setIdProyecto(proyecto.getId());
    estadoProyecto.setComentario(comentario);
    estadoProyecto.setFechaEstado(LocalDateTime.now(ZoneId.of("Europe/Madrid")));

    EstadoProyecto returnValue = estadoProyectoRepository.save(estadoProyecto);

    log.debug(
        "addEstadoProyecto(Proyecto proyecto, TipoEstadoProyectoEnum tipoEstadoProyecto, String comentario) - end");
    return returnValue;
  }

  /**
   * Se comprueba que los datos a guardar cumplan las validaciones oportunas
   * 
   * @param proyecto          datos del proyecto
   * @param unidadGestionRefs las unidades de gestión del usuario
   * 
   */
  private void validarDatos(Proyecto proyecto) {
    Assert.isTrue(
        (proyecto.getConvocatoria() != null && proyecto.getConvocatoriaExterna() == null)
            || proyecto.getConvocatoria() == null,
        "La convocatoria no puede ser externa ya que el proyecto tiene una convocatoria asignada");

    if (proyecto.getConvocatoria() != null && proyecto.getConvocatoria().getId() != null) {
      Assert.isTrue(convocatoriaRepository.existsById(proyecto.getConvocatoria().getId()),
          "La convocatoria con id '" + proyecto.getConvocatoria().getId() + "' no existe");
      proyecto.setConvocatoria(convocatoriaRepository.findById(proyecto.getConvocatoria().getId())
          .orElseThrow(() -> new ConvocatoriaNotFoundException(proyecto.getConvocatoria().getId())));
    }

    // TODO comprobar la solicitud cuando se implemente la posibilidad de la
    // creación de proyectos a través de la misma

    // ModeloEjecucion correcto
    Optional<ModeloUnidad> modeloUnidad = modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(
        proyecto.getModeloEjecucion().getId(), proyecto.getUnidadGestionRef());

    Assert.isTrue(modeloUnidad.isPresent(), "ModeloEjecucion '" + proyecto.getModeloEjecucion().getNombre()
        + "' no disponible para la UnidadGestion " + proyecto.getUnidadGestionRef());

    if (proyecto.getCosteHora() != null && proyecto.getCosteHora()) {
      Assert.isTrue(proyecto.getTimesheet() != null && proyecto.getTimesheet(), "El proyecto requiere timesheet");
      Assert.isTrue(proyecto.getTipoHorasAnuales() != null,
          "El campo tipoHorasAnuales debe ser obligatorio para el proyecto");
    }

    // Validación de campos obligatorios según estados. Solo aplicaría en el
    // actualizar ya que en el crear el estado siempre será "Borrador"
    if (proyecto.getEstado() != null && proyecto.getEstado().getEstado().equals(TipoEstadoProyectoEnum.ABIERTO)) {
      // En la validación del crear no pasará por aquí, aún no tendrá estado.
      Assert.isTrue(proyecto.getFinalidad() != null,
          "El campo finalidad debe ser obligatorio para el proyecto en estado 'Abierto'");

      Assert.isTrue(proyecto.getAmbitoGeografico() != null,
          "El campo ambitoGeografico debe ser obligatorio para el proyecto en estado 'Abierto'");

      Assert.isTrue(proyecto.getConfidencial() != null,
          "El campo confidencial debe ser obligatorio para el proyecto en estado 'Abierto'");

      Assert.isTrue(proyecto.getColaborativo() != null,
          "El campo colaborativo debe ser obligatorio para el proyecto en estado 'Abierto'");

      Assert.isTrue(proyecto.getCoordinadorExterno() != null,
          "El campo coordinadorExterno debe ser obligatorio para el proyecto en estado 'Abierto'");

      Assert.isTrue(
          proyecto.getEstado().getEstado().equals(TipoEstadoProyectoEnum.ABIERTO) && proyecto.getTimesheet() != null,
          "El campo timesheet debe ser obligatorio para el proyecto en estado 'Abierto'");

      Assert.isTrue(proyecto.getPaquetesTrabajo() != null,
          "El campo paquetesTrabajo debe ser obligatorio para el proyecto en estado 'Abierto'");

      Assert.isTrue(proyecto.getCosteHora() != null,
          "El campo costeHora debe ser obligatorio para el proyecto en estado 'Abierto'");

      Assert.isTrue(proyecto.getContratos() != null,
          "El campo contratos debe ser obligatorio para el proyecto en estado 'Abierto'");

      Assert.isTrue(proyecto.getFacturacion() != null,
          "El campo facturacion debe ser obligatorio para el proyecto en estado 'Abierto'");

      Assert.isTrue(proyecto.getIva() != null,
          "El campo iva debe ser obligatorio para el proyecto en estado 'Abierto'");
    }
  }

  /**
   * Guarda los datos de la convocatoria asociada al proyecto en las entidades
   * relacionadas con el proyecto
   * 
   * @param convocatoriaProyecto la {@link Convocatoria}
   */
  private void guardarDatosEntidadesRelacionadas(Convocatoria convocatoriaProyecto) {

  }

  /**
   * Copia las entidades financiadores de una convocatoria a un proyecto
   * 
   * @param proyectoId     Identificador del proyecto de destino
   * @param convocatoriaId Identificador de la convocatoria
   */
  private void copyEntidadesFinanciadoras(Long proyectoId, Long convocatoriaId) {
    log.debug("copyEntidadesFinanciadoras(Long proyectoId, Long convocatoriaId) - start");
    List<ConvocatoriaEntidadFinanciadora> entidadesConvocatoria = convocatoriaEntidadFinanciadoraRepository
        .findAllByConvocatoriaId(convocatoriaId);
    entidadesConvocatoria.stream().forEach((entidadConvocatoria) -> {
      log.debug("Copy ConvocatoriaEntidadFinanciadora with id: {0}", entidadConvocatoria.getId());
      ProyectoEntidadFinanciadora entidadProyecto = new ProyectoEntidadFinanciadora();
      entidadProyecto.setProyectoId(proyectoId);
      entidadProyecto.setEntidadRef(entidadConvocatoria.getEntidadRef());
      entidadProyecto.setFuenteFinanciacion(entidadConvocatoria.getFuenteFinanciacion());
      entidadProyecto.setTipoFinanciacion(entidadConvocatoria.getTipoFinanciacion());
      entidadProyecto.setPorcentajeFinanciacion(entidadConvocatoria.getPorcentajeFinanciacion());
      entidadProyecto.setAjena(DEFAULT_COPY_ENTIDAD_FINANCIADORA_AJENA_VALUE);

      this.proyectoEntidadFinanciadoraService.create(entidadProyecto);
    });
    log.debug("copyEntidadesFinanciadoras(Long proyectoId, Long convocatoriaId) - start");
  }
}
