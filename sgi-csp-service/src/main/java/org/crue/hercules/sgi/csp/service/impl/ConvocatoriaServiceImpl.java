package org.crue.hercules.sgi.csp.service.impl;

import java.time.LocalDate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.enums.TipoEstadoConvocatoriaEnum;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoSeguimientoCientificoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFinalidadRepository;
import org.crue.hercules.sgi.csp.repository.ModeloUnidadRepository;
import org.crue.hercules.sgi.csp.repository.TipoAmbitoGeograficoRepository;
import org.crue.hercules.sgi.csp.repository.TipoRegimenConcurrenciaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link Convocatoria}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaServiceImpl implements ConvocatoriaService {

  private final ConvocatoriaRepository repository;
  private final ConvocatoriaPeriodoJustificacionRepository convocatoriaPeriodoJustificacionRepository;
  private final ModeloUnidadRepository modeloUnidadRepository;
  private final ModeloTipoFinalidadRepository modeloTipoFinalidadRepository;
  private final TipoRegimenConcurrenciaRepository tipoRegimenConcurrenciaRepository;
  private final TipoAmbitoGeograficoRepository tipoAmbitoGeograficoRepository;
  private final ConvocatoriaPeriodoSeguimientoCientificoRepository convocatoriaPeriodoSeguimientoCientificoRepository;

  public ConvocatoriaServiceImpl(ConvocatoriaRepository repository,
      ConvocatoriaPeriodoJustificacionRepository convocatoriaPeriodoJustificacionRepository,
      ModeloUnidadRepository modeloUnidadRepository, ModeloTipoFinalidadRepository modeloTipoFinalidadRepository,
      TipoRegimenConcurrenciaRepository tipoRegimenConcurrenciaRepository,
      TipoAmbitoGeograficoRepository tipoAmbitoGeograficoRepository,
      ConvocatoriaPeriodoSeguimientoCientificoRepository convocatoriaPeriodoSeguimientoCientificoRepository) {
    this.repository = repository;
    this.convocatoriaPeriodoJustificacionRepository = convocatoriaPeriodoJustificacionRepository;
    this.modeloUnidadRepository = modeloUnidadRepository;
    this.modeloTipoFinalidadRepository = modeloTipoFinalidadRepository;
    this.tipoRegimenConcurrenciaRepository = tipoRegimenConcurrenciaRepository;
    this.tipoAmbitoGeograficoRepository = tipoAmbitoGeograficoRepository;
    this.convocatoriaPeriodoSeguimientoCientificoRepository = convocatoriaPeriodoSeguimientoCientificoRepository;
  }

  /**
   * Guarda la entidad {@link Convocatoria}.
   * 
   * @param convocatoria la entidad {@link Convocatoria} a guardar.
   * @return Convocatoria la entidad {@link Convocatoria} persistida.
   */
  @Override
  @Transactional
  public Convocatoria create(Convocatoria convocatoria, List<String> acronimosUnidadGestion) {
    log.debug("create(Convocatoria convocatoria) - start");

    Assert.isNull(convocatoria.getId(), "Id tiene que ser null para crear la Convocatoria");

    Convocatoria validConvocatoria = validarDatosConvocatoria(convocatoria, null, acronimosUnidadGestion);
    validConvocatoria.setEstadoActual(TipoEstadoConvocatoriaEnum.BORRADOR);
    validConvocatoria.setActivo(Boolean.TRUE);
    Convocatoria returnValue = repository.save(validConvocatoria);

    log.debug("create(Convocatoria convocatoria) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link Convocatoria}.
   * 
   * @param convocatoria convocatoriaActualizar {@link Convocatoria} con los datos
   *                     actualizados.
   * @return {@link Convocatoria} actualizado.
   */
  @Override
  @Transactional
  public Convocatoria update(Convocatoria convocatoria, List<String> acronimosUnidadGestion) {
    log.debug("update(Convocatoria convocatoria) - start");

    Assert.notNull(convocatoria.getId(), "Id no puede ser null para actualizar Convocatoria");
    // TODO: FALTA AÑADIR VALIDACIÓN DE QUE NO EXISTAN SOLICITUDES ASOCIADAS O
    // PROYECTOS (update y delete)

    return repository.findById(convocatoria.getId()).map((data) -> {

      Convocatoria validConvocatoria = validarDatosConvocatoria(convocatoria, data, acronimosUnidadGestion);

      data.setUnidadGestionRef(validConvocatoria.getUnidadGestionRef());
      data.setModeloEjecucion(validConvocatoria.getModeloEjecucion());
      data.setCodigo(validConvocatoria.getCodigo());
      data.setAnio(validConvocatoria.getAnio());
      data.setTitulo(validConvocatoria.getTitulo());
      data.setObjeto(validConvocatoria.getObjeto());
      data.setObservaciones(validConvocatoria.getObservaciones());
      data.setFinalidad(validConvocatoria.getFinalidad());
      data.setRegimenConcurrencia(validConvocatoria.getRegimenConcurrencia());
      data.setDestinatarios(validConvocatoria.getDestinatarios());
      data.setColaborativos(validConvocatoria.getColaborativos());
      data.setDuracion(validConvocatoria.getDuracion());
      data.setAmbitoGeografico(validConvocatoria.getAmbitoGeografico());
      data.setClasificacionCVN(validConvocatoria.getClasificacionCVN());
      data.setActivo(validConvocatoria.getActivo());

      Convocatoria returnValue = repository.save(validConvocatoria);

      log.debug("update(Convocatoria convocatoria) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoria.getId()));
  }

  /**
   * Registra una {@link Convocatoria} actualizando su estado de 'Borrador' a
   * 'Registrada'
   * 
   * @param id Identificador de la {@link Convocatoria}.
   * @return Convocatoria {@link Convocatoria} actualizada.
   */
  @Override
  @Transactional
  public Convocatoria registrar(final Long id) {
    log.debug("registrar(Long id) - start");

    Assert.notNull(id, "Id no puede ser null para registrar Convocatoria");

    return repository.findById(id).map((data) -> {

      Assert.isTrue(data.getEstadoActual().equals(TipoEstadoConvocatoriaEnum.BORRADOR),
          "Convocatoria deber estar en estado 'Borrador' para pasar a 'Registrada'");

      // Campos obligatorios en estado Registrada
      validarRequeridosConvocatoriaRegistrada(data);

      data.setEstadoActual(TipoEstadoConvocatoriaEnum.REGISTRADA);
      Convocatoria returnValue = repository.save(data);

      log.debug("registrar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaNotFoundException(id));
  }

  /**
   * Reactiva el {@link Convocatoria}.
   *
   * @param id Id del {@link Convocatoria}.
   * @return la entidad {@link Convocatoria} persistida.
   */
  @Override
  @Transactional
  public Convocatoria enable(Long id) {
    log.debug("enable(Long id) - start");

    Assert.notNull(id, "Convocatoria id no puede ser null para reactivar un Convocatoria");

    return repository.findById(id).map(convocatoria -> {
      if (convocatoria.getActivo()) {
        return convocatoria;
      }
      convocatoria.setActivo(Boolean.TRUE);
      Convocatoria returnValue = repository.save(convocatoria);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaNotFoundException(id));
  }

  /**
   * Desactiva el {@link Convocatoria}.
   *
   * @param id Id del {@link Convocatoria}.
   * @return la entidad {@link Convocatoria} persistida.
   */
  @Override
  @Transactional
  public Convocatoria disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "Convocatoria id no puede ser null para desactivar un Convocatoria");
    // TODO: FALTA AÑADIR VALIDACIÓN DE QUE NO EXISTAN SOLICITUDES ASOCIADAS O
    // PROYECTOS (update y delete)

    return repository.findById(id).map(convocatoria -> {
      if (!convocatoria.getActivo()) {
        return convocatoria;
      }
      convocatoria.setActivo(Boolean.FALSE);
      Convocatoria returnValue = repository.save(convocatoria);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaNotFoundException(id));
  }

  /**
   * Obtiene una entidad {@link Convocatoria} por id.
   * 
   * @param id Identificador de la entidad {@link Convocatoria}.
   * @return Convocatoria la entidad {@link Convocatoria}.
   */
  @Override
  public Convocatoria findById(Long id) {
    log.debug("findById(Long id) - start");
    final Convocatoria returnValue = repository.findById(id).orElseThrow(() -> new ConvocatoriaNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Convocatoria} activas paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Convocatoria} activas paginadas y
   *         filtradas.
   */
  @Override
  public Page<Convocatoria> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - start");
    Specification<Convocatoria> specByQuery = new QuerySpecification<Convocatoria>(query);
    Specification<Convocatoria> specActivos = ConvocatoriaSpecifications.activos();
    Specification<Convocatoria> specs = Specification.where(specByQuery).and(specActivos);
    Page<Convocatoria> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Convocatoria} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Convocatoria} paginadas y filtradas.
   */
  @Override
  public Page<Convocatoria> findAllTodos(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - start");

    Specification<Convocatoria> spec = getFiltroAplicado(query);

    Page<Convocatoria> returnValue = repository.findAll(spec, paging);
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - end");
    return returnValue;
  }

  @Override
  public Page<Convocatoria> findAllTodosRestringidos(List<QueryCriteria> query, Pageable paging,
      List<String> acronimosUnidadGestion) {
    log.debug(
        "findAllTodosRestringidos(List<QueryCriteria> query, Pageable paging,  List<String> acronimosUnidadGestion) - start");

    Specification<Convocatoria> spec = getFiltroAplicado(query);

    Specification<Convocatoria> specAcronimos = ConvocatoriaSpecifications.acronimosIn(acronimosUnidadGestion);
    if (spec != null) {
      spec = spec.and(specAcronimos);
    } else {
      spec = Specification.where(specAcronimos);
    }

    Page<Convocatoria> returnValue = repository.findAll(spec, paging);

    log.debug(
        "findAllTodosRestringidos(List<QueryCriteria> query, Pageable paging,  List<String> acronimosUnidadGestion) - end");
    return returnValue;
  }

  /**
   * Comprueba y valida los datos de una convocatoria.
   * 
   * @param datosConvocatoria
   * @param datosOriginales
   * @param acronimosUnidadGestion
   * @return convocatoria con los datos validados
   */
  private Convocatoria validarDatosConvocatoria(Convocatoria datosConvocatoria, Convocatoria datosOriginales,
      List<String> acronimosUnidadGestion) {
    log.debug("validarDatosConvocatoria(Convocatoria datosConvocatoria, Convocatoria datosOriginales) - start");

    // Campos obligatorios en estado Registrada
    if (datosConvocatoria.getEstadoActual().equals(TipoEstadoConvocatoriaEnum.REGISTRADA)) {
      validarRequeridosConvocatoriaRegistrada(datosConvocatoria);
    }

    // ModeloUnidadGestion
    if (datosConvocatoria.getUnidadGestionRef() != null && !CollectionUtils.isEmpty(acronimosUnidadGestion)) {

      Assert.isTrue(acronimosUnidadGestion.contains(datosConvocatoria.getUnidadGestionRef()),
          "El usuario no tiene permisos para crear una convocatoria asociada a la unidad de gestión recibida.");
    }

    // ModeloEjecucion
    if (datosConvocatoria.getModeloEjecucion() != null) {

      Assert.notNull(datosConvocatoria.getUnidadGestionRef(),
          "UnidadGestionRef requerido para obtener ModeloEjecucion");

      Optional<ModeloUnidad> modeloUnidad = modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(
          datosConvocatoria.getModeloEjecucion().getId(), datosConvocatoria.getUnidadGestionRef());

      Assert.isTrue(modeloUnidad.isPresent(), "ModeloEjecucion '" + datosConvocatoria.getModeloEjecucion().getNombre()
          + "' no disponible para la UnidadGestion " + datosConvocatoria.getUnidadGestionRef());

      // Permitir no activos solo si estamos modificando y es el mismo
      if (datosOriginales == null || (datosOriginales.getModeloEjecucion() != null
          && (modeloUnidad.get().getModeloEjecucion().getId() != datosOriginales.getModeloEjecucion().getId()))) {
        Assert.isTrue(modeloUnidad.get().getActivo(),
            "ModeloEjecucion '" + modeloUnidad.get().getModeloEjecucion().getNombre()
                + "' no está activo para la UnidadGestion " + modeloUnidad.get().getUnidadGestionRef());
      }
      // Permitir no activos solo si estamos modificando y es el mismo
      if (datosOriginales == null || (datosOriginales.getModeloEjecucion() != null
          && (modeloUnidad.get().getModeloEjecucion().getId() != datosOriginales.getModeloEjecucion().getId()))) {
        Assert.isTrue(modeloUnidad.get().getModeloEjecucion().getActivo(),
            "ModeloEjecucion '" + modeloUnidad.get().getModeloEjecucion().getNombre() + "' no está activo");
      }
      datosConvocatoria.setModeloEjecucion(modeloUnidad.get().getModeloEjecucion());
    }

    // Codigo
    if (datosConvocatoria.getCodigo() != null) {
      repository.findByCodigo(datosConvocatoria.getCodigo()).ifPresent((convocatoriaExistente) -> {
        Assert.isTrue(datosConvocatoria.getId() == convocatoriaExistente.getId(),
            "Ya existe una Convocatoria con el código " + convocatoriaExistente.getCodigo());
      });
    }

    // Anio
    if (datosConvocatoria.getAnio() != null) {
      Assert.isTrue(datosConvocatoria.getAnio() <= (LocalDate.now().getYear() + 1),
          "Año no debe ser mayor que el año actual + 1");
    }

    // TipoFinalidad
    if (datosConvocatoria.getFinalidad() != null) {

      Assert.notNull(datosConvocatoria.getModeloEjecucion(), "ModeloEjecucion requerido para obtener TipoFinalidad");

      Optional<ModeloTipoFinalidad> modeloTipoFinalidad = modeloTipoFinalidadRepository
          .findByModeloEjecucionIdAndTipoFinalidadId(datosConvocatoria.getModeloEjecucion().getId(),
              datosConvocatoria.getFinalidad().getId());

      Assert.isTrue(modeloTipoFinalidad.isPresent(), "TipoFinalidad '" + datosConvocatoria.getFinalidad().getNombre()
          + "' no disponible para el ModeloEjecucion " + datosConvocatoria.getModeloEjecucion().getNombre());

      // Permitir no activos solo si estamos modificando y es el mismo
      if (datosOriginales == null || (datosOriginales.getFinalidad() != null
          && (modeloTipoFinalidad.get().getTipoFinalidad().getId() != datosOriginales.getFinalidad().getId()))) {
        Assert.isTrue(modeloTipoFinalidad.get().getActivo(),
            "ModeloTipoFinalidad '" + modeloTipoFinalidad.get().getTipoFinalidad().getNombre()
                + "' no está activo para el ModeloEjecucion "
                + modeloTipoFinalidad.get().getModeloEjecucion().getNombre());
      }

      // Permitir no activos solo si estamos modificando y es el mismo
      if (datosOriginales == null || (datosOriginales.getFinalidad() != null
          && (modeloTipoFinalidad.get().getTipoFinalidad().getId() != datosOriginales.getFinalidad().getId()))) {
        Assert.isTrue(modeloTipoFinalidad.get().getTipoFinalidad().getActivo(),
            "TipoFinalidad '" + modeloTipoFinalidad.get().getTipoFinalidad().getNombre() + "' no está activo");
      }
      datosConvocatoria.setFinalidad(modeloTipoFinalidad.get().getTipoFinalidad());
    }

    // TipoRegimenConcurrencia
    if (datosConvocatoria.getRegimenConcurrencia() != null) {
      if (datosConvocatoria.getRegimenConcurrencia().getId() == null) {
        datosConvocatoria.setRegimenConcurrencia(null);
      } else {

        Optional<TipoRegimenConcurrencia> tipoRegimenConcurrencia = tipoRegimenConcurrenciaRepository
            .findById(datosConvocatoria.getRegimenConcurrencia().getId());

        Assert.isTrue(tipoRegimenConcurrencia.isPresent(),
            "RegimenConcurrencia '" + datosConvocatoria.getRegimenConcurrencia().getNombre() + "' no disponible");

        // Permitir no activos solo si estamos modificando y es el mismo
        if (datosOriginales == null || (datosOriginales.getRegimenConcurrencia() != null
            && (tipoRegimenConcurrencia.get().getId() != datosOriginales.getRegimenConcurrencia().getId()))) {
          Assert.isTrue(tipoRegimenConcurrencia.get().getActivo(),
              "RegimenConcurrencia '" + tipoRegimenConcurrencia.get().getNombre() + "' no está activo");
        }
        datosConvocatoria.setRegimenConcurrencia(tipoRegimenConcurrencia.get());
      }
    }

    // TipoAmbitoGeografico
    if (datosConvocatoria.getAmbitoGeografico() != null) {

      Optional<TipoAmbitoGeografico> tipoAmbitoGeografico = tipoAmbitoGeograficoRepository
          .findById(datosConvocatoria.getAmbitoGeografico().getId());

      Assert.isTrue(tipoAmbitoGeografico.isPresent(),
          "AmbitoGeografico '" + datosConvocatoria.getAmbitoGeografico().getNombre() + "' no disponible");

      // Permitir no activos solo si estamos modificando y es el mismo
      if (datosOriginales == null || (datosOriginales.getAmbitoGeografico() != null
          && (tipoAmbitoGeografico.get().getId() != datosOriginales.getAmbitoGeografico().getId()))) {
        Assert.isTrue(tipoAmbitoGeografico.get().getActivo(),
            "AmbitoGeografico '" + tipoAmbitoGeografico.get().getNombre() + "' no está activo");
      }
      datosConvocatoria.setAmbitoGeografico(tipoAmbitoGeografico.get());
    }

    if (datosConvocatoria.getId() != null) {
      // Comprueba que la duracion no sea menor que el ultimo mes del ultimo
      // ConvocatoriaPeriodoJustificacion de la convocatoria
      if (datosConvocatoria.getDuracion() != null && datosConvocatoria.getDuracion() != datosOriginales.getDuracion()) {
        convocatoriaPeriodoJustificacionRepository
            .findFirstByConvocatoriaIdOrderByNumPeriodoDesc(datosConvocatoria.getId())
            .ifPresent(convocatoriaPeriodoJustificacion -> {
              Assert.isTrue(convocatoriaPeriodoJustificacion.getMesFinal() <= datosConvocatoria.getDuracion(),
                  "Hay ConvocatoriaPeriodoJustificacion con mesFinal inferior a la nueva duracion");
            });
      }
    }

    // Duración mayor que el mayor mes del Periodo Seguimiento Cientifico
    if (datosOriginales != null && datosConvocatoria.getDuracion() != null
        && (datosConvocatoria.getDuracion() != datosOriginales.getDuracion())) {
      List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientificos = convocatoriaPeriodoSeguimientoCientificoRepository
          .findAllByConvocatoriaIdOrderByMesInicial(datosConvocatoria.getId());
      if (!listaConvocatoriaPeriodoSeguimientoCientificos.isEmpty()) {
        Assert.isTrue(
            listaConvocatoriaPeriodoSeguimientoCientificos
                .get(listaConvocatoriaPeriodoSeguimientoCientificos.size() - 1)
                .getMesFinal() < datosConvocatoria.getDuracion(),
            "Existen periodos de seguimiento científico con una duración en meses superior a la indicada");
      }
    }

    log.debug("validarDatosConvocatoria(Convocatoria datosConvocatoria, Convocatoria datosOriginales) - end");

    return datosConvocatoria;
  }

  /**
   * Valida los campos requieridos para una convocatoria en estado 'Registrada'
   * 
   * @param datosConvocatoria
   */
  private void validarRequeridosConvocatoriaRegistrada(Convocatoria datosConvocatoria) {
    log.debug("validarRequeridosConvocatoriaRegistrada(Convocatoria datosConvocatoria) - start");

    // ModeloUnidadGestion
    Assert.notNull(datosConvocatoria.getUnidadGestionRef(), "UnidadGestionRef no puede ser null en la Convocatoria");
    // ModeloEjecucion
    Assert.notNull(datosConvocatoria.getModeloEjecucion(), "ModeloEjecucion no puede ser null en la Convocatoria");
    // Codigo
    Assert.notNull(datosConvocatoria.getCodigo(), "Codigo no puede ser null en la Convocatoria");
    // Anio
    Assert.notNull(datosConvocatoria.getAnio(), "Año no puede ser null en la Convocatoria");
    // Titulo
    Assert.notNull(datosConvocatoria.getTitulo(), "Titulo no puede ser null en la Convocatoria");
    // TipoFinalidad
    Assert.notNull(datosConvocatoria.getFinalidad(), "Finalidad no puede ser null en la Convocatoria");
    // TipoDestinatario
    Assert.notNull(datosConvocatoria.getDestinatarios(), "Destinatarios no puede ser null en la Convocatoria");
    // TipoAmbitoGeografico
    Assert.notNull(datosConvocatoria.getAmbitoGeografico(), "AmbitoGeografico no puede ser null en la Convocatoria");

    log.debug("validarRequeridosConvocatoriaRegistrada(Convocatoria datosConvocatoria) - end");
  }

  /**
   * Devuelve la Specification para el filtro avanzado de convocatoria.
   * 
   * @param query opciones de búsqueda.
   */
  private Specification<Convocatoria> getFiltroAplicado(List<QueryCriteria> query) {
    log.debug("getFiltroAplicado(List<QueryCriteria> query) - start");
    Specification<Convocatoria> spec = null;

    List<QueryCriteria> queryEntity = CollectionUtils.isEmpty(query) ? query
        : query.stream()
            .filter(criteria -> !criteria.getKey().equals("areaTematica.id")
                && !criteria.getKey().equals("convocatoriaEntidadConvocante.entidadRef")
                && !criteria.getKey().equals("convocatoriaEntidadFinanciadora.entidadRef")
                && !criteria.getKey().equals("fuenteFinanciacion.id"))
            .collect(Collectors.toList());

    if (!CollectionUtils.isEmpty(queryEntity)) {
      Specification<Convocatoria> specByQuery = new QuerySpecification<Convocatoria>(queryEntity);
      spec = Specification.where(specByQuery);
    }

    // Área temática
    List<QueryCriteria> idAreaTematica = CollectionUtils.isEmpty(query) ? query
        : query.stream().filter(criteria -> criteria.getKey().equals("areaTematica.id")).collect(Collectors.toList());
    if (!CollectionUtils.isEmpty(idAreaTematica)) {
      Specification<Convocatoria> specByAreaTematicaId = ConvocatoriaSpecifications
          .byAreaTematicaId(Long.valueOf(idAreaTematica.get(0).getValue()));

      if (spec != null) {

        spec = spec.and(specByAreaTematicaId);
      } else {
        spec = Specification.where(specByAreaTematicaId);
      }

    }

    // Entidad convocante
    List<QueryCriteria> entidadConvocanteRef = CollectionUtils.isEmpty(query) ? query
        : query.stream().filter(criteria -> criteria.getKey().equals("convocatoriaEntidadConvocante.entidadRef"))
            .collect(Collectors.toList());
    if (!CollectionUtils.isEmpty(entidadConvocanteRef)) {
      Specification<Convocatoria> specByEntidadConvocanteRef = ConvocatoriaSpecifications
          .byEntidadConvocanteRef(entidadConvocanteRef.get(0).getValue());

      if (spec != null) {

        spec = spec.and(specByEntidadConvocanteRef);
      } else {
        spec = Specification.where(specByEntidadConvocanteRef);
      }

    }

    // Entidad financiadora
    List<QueryCriteria> entidadFinanciadoraRef = CollectionUtils.isEmpty(query) ? query
        : query.stream().filter(criteria -> criteria.getKey().equals("convocatoriaEntidadFinanciadora.entidadRef"))
            .collect(Collectors.toList());
    if (!CollectionUtils.isEmpty(entidadFinanciadoraRef)) {
      Specification<Convocatoria> specByEntidadFinanciadoraRef = ConvocatoriaSpecifications
          .byEntidadFinancieraRef(entidadFinanciadoraRef.get(0).getValue());

      if (spec != null) {

        spec = spec.and(specByEntidadFinanciadoraRef);
      } else {
        spec = Specification.where(specByEntidadFinanciadoraRef);
      }

    }

    // Fuente financiación
    List<QueryCriteria> fuenteFinanciacionId = CollectionUtils.isEmpty(query) ? query
        : query.stream().filter(criteria -> criteria.getKey().equals("fuenteFinanciacion.id"))
            .collect(Collectors.toList());
    if (!CollectionUtils.isEmpty(fuenteFinanciacionId)) {
      Specification<Convocatoria> specByFuenteFinanciacionRef = ConvocatoriaSpecifications
          .byFuenteFinanciacionId(Long.valueOf(fuenteFinanciacionId.get(0).getValue()));

      if (spec != null) {

        spec = spec.and(specByFuenteFinanciacionRef);
      } else {
        spec = Specification.where(specByFuenteFinanciacionRef);
      }

    }
    log.debug("getFiltroAplicado(List<QueryCriteria> query) - start");

    return spec;

  }

}
