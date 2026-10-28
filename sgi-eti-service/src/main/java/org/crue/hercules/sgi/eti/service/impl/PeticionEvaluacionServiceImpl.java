package org.crue.hercules.sgi.eti.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.dto.PeticionEvaluacionWithIsEliminable;
import org.crue.hercules.sgi.eti.dto.sgo.UnidadVinculacionOutput;
import org.crue.hercules.sgi.eti.dto.sgp.VinculacionOutput;
import org.crue.hercules.sgi.eti.exceptions.PeticionEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.Configuracion;
import org.crue.hercules.sgi.eti.model.Configuracion.FieldVisibility;
import org.crue.hercules.sgi.eti.model.Configuracion.Param;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.EstadoFinanciacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionUnidadVinculacion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.repository.ConfiguracionRepository;
import org.crue.hercules.sgi.eti.repository.PeticionEvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.PeticionEvaluacionUnidadVinculacionRepository;
import org.crue.hercules.sgi.eti.repository.predicate.MemoriaPredicateResolver;
import org.crue.hercules.sgi.eti.repository.predicate.PeticionEvaluacionPredicateResolver;
import org.crue.hercules.sgi.eti.repository.specification.MemoriaSpecifications;
import org.crue.hercules.sgi.eti.repository.specification.PeticionEvaluacionSpecifications;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.crue.hercules.sgi.eti.service.PeticionEvaluacionService;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiSgoService;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.util.AssertHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link PeticionEvaluacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class PeticionEvaluacionServiceImpl implements PeticionEvaluacionService {
  private static final String MSG_FIELD_FECHA_INICIO = "fechaInicio";
  private static final String MSG_FIELD_FUENTE_FINANCIACION = "fuenteFinanciacion";
  private static final String MSG_FIELD_IMPORTE_FINANCIACION = "importeFinanciacion";
  private static final String MSG_FIELD_INTRODUCCION = "introduccion";
  private static final String MSG_FIELD_OTRO_VALOR_SOCIAL = "otroValorSocial";
  private static final String MSG_FIELD_TITULO = "titulo";

  private final SgiConfigProperties sgiConfigProperties;
  private final PeticionEvaluacionRepository peticionEvaluacionRepository;
  private final MemoriaService memoriaService;
  private final ConfiguracionRepository configuracionRepository;
  private final PeticionEvaluacionUnidadVinculacionRepository peticionEvaluacionUnidadVinculacionRepository;
  private final SgiApiSgpService sgiApiSgpService;
  private final SgiApiSgoService sgiApiSgoService;

  /**
   * Instancia un nuevo PeticionEvaluacionServiceImpl.
   *
   * @param sgiConfigProperties                           {@link SgiConfigProperties}
   * @param peticionEvaluacionRepository                  {@link PeticionEvaluacionRepository}
   * @param memoriaService                                {@link MemoriaService}
   * @param configuracionRepository                       {@link ConfiguracionRepository}
   * @param peticionEvaluacionUnidadVinculacionRepository {@link PeticionEvaluacionUnidadVinculacionRepository}
   * @param sgiApiSgpService                              {@link SgiApiSgpService}
   * @param sgiApiSgoService                              {@link SgiApiSgoService}
   */
  public PeticionEvaluacionServiceImpl(SgiConfigProperties sgiConfigProperties,
      PeticionEvaluacionRepository peticionEvaluacionRepository, MemoriaService memoriaService,
      ConfiguracionRepository configuracionRepository,
      PeticionEvaluacionUnidadVinculacionRepository peticionEvaluacionUnidadVinculacionRepository,
      SgiApiSgpService sgiApiSgpService, SgiApiSgoService sgiApiSgoService) {
    this.sgiConfigProperties = sgiConfigProperties;
    this.peticionEvaluacionRepository = peticionEvaluacionRepository;
    this.memoriaService = memoriaService;
    this.configuracionRepository = configuracionRepository;
    this.peticionEvaluacionUnidadVinculacionRepository = peticionEvaluacionUnidadVinculacionRepository;
    this.sgiApiSgpService = sgiApiSgpService;
    this.sgiApiSgoService = sgiApiSgoService;
  }

  /**
   * Guarda la entidad {@link PeticionEvaluacion}.
   *
   * @param peticionEvaluacion la entidad {@link PeticionEvaluacion} a guardar.
   * @return la entidad {@link PeticionEvaluacion} persistida.
   */
  @Transactional
  public PeticionEvaluacion create(PeticionEvaluacion peticionEvaluacion) {
    log.debug("Petición a create PeticionEvaluacion : {} - start", peticionEvaluacion);
    AssertHelper.idIsNull(peticionEvaluacion.getId(), PeticionEvaluacion.class);
    // Inicialización de campos no especificados a sus valores por defecto
    if (peticionEvaluacion.getActivo() == null) {
      peticionEvaluacion.setActivo(Boolean.TRUE);
    }

    AssertHelper.fieldNotNull(peticionEvaluacion.getFechaInicio(), PeticionEvaluacion.class, MSG_FIELD_FECHA_INICIO);
    peticionEvaluacion.setCodigo(generateCode(peticionEvaluacion.getFechaInicio()));

    if (peticionEvaluacion.getExisteFinanciacion().booleanValue()) {
      AssertHelper.entityNotNull(peticionEvaluacion.getEstadoFinanciacion(), PeticionEvaluacion.class,
          EstadoFinanciacion.class);
    }

    if (peticionEvaluacion.getValorSocial() != null
        && peticionEvaluacion.getValorSocial().equals(TipoValorSocial.OTRA_FINALIDAD)) {
      AssertHelper.i18nFieldNotNull(peticionEvaluacion.getOtroValorSocial(), PeticionEvaluacion.class,
          MSG_FIELD_OTRO_VALOR_SOCIAL);
    }

    assertIntroduccionPresentIfRequired(peticionEvaluacion);

    PeticionEvaluacion saved = peticionEvaluacionRepository.save(peticionEvaluacion);
    initializeUnidadVinculacionFromDepartamento(saved);

    return saved;
  }

  @Override
  @Transactional
  public PeticionEvaluacion createFromSolicitudCsp(PeticionEvaluacion peticionEvaluacion) {
    log.debug("createFromSolicitudCsp - data: {}", peticionEvaluacion);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    if (peticionEvaluacion.getExisteFinanciacion().booleanValue()) {
      AssertHelper.entityNotNull(peticionEvaluacion.getEstadoFinanciacion(), PeticionEvaluacion.class,
          EstadoFinanciacion.class);
    }

    // La petición creada desde CSP no tiene fecha de inicio: el código usa el año
    // de alta
    peticionEvaluacion.setCodigo(generateCode(Instant.now()));

    PeticionEvaluacion saved = peticionEvaluacionRepository.save(peticionEvaluacion);
    initializeUnidadVinculacionFromDepartamento(saved);
    log.debug("createFromSolicitudCsp - id: {}", saved.getId());
    return saved;
  }

  /**
   * Obtiene todas las entidades {@link PeticionEvaluacion} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link PeticionEvaluacion} paginadas y
   *         filtradas.
   */
  public Page<PeticionEvaluacion> findAll(String query, Pageable paging) {
    log.debug("findAllPeticionEvaluacion(String query,Pageable paging) - start");
    Specification<PeticionEvaluacion> specs = PeticionEvaluacionSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<PeticionEvaluacion> returnValue = peticionEvaluacionRepository.findAll(specs, paging);
    log.debug("findAllPeticionEvaluacion(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link PeticionEvaluacion} por id.
   *
   * @param id el id de la entidad {@link PeticionEvaluacion}.
   * @return la entidad {@link PeticionEvaluacion}.
   * @throws PeticionEvaluacionNotFoundException Si no existe ningún
   *                                             {@link PeticionEvaluacion}e con
   *                                             ese id.
   */
  public PeticionEvaluacion findById(final Long id) throws PeticionEvaluacionNotFoundException {
    log.debug("Petición a get PeticionEvaluacion : {}  - start", id);
    final PeticionEvaluacion peticionEvaluacion = peticionEvaluacionRepository.findById(id)
        .orElseThrow(() -> new PeticionEvaluacionNotFoundException(id));
    log.debug("Petición a get PeticionEvaluacion : {}  - end", id);
    return peticionEvaluacion;

  }

  /**
   * {@inheritDoc}
   */
  public void existsById(final Long id) throws PeticionEvaluacionNotFoundException {
    log.debug("Petición a get PeticionEvaluacion : {}  - start", id);
    boolean exists = peticionEvaluacionRepository.existsById(id);
    if (!exists) {
      throw new PeticionEvaluacionNotFoundException(id);
    }
    log.debug("Petición a get PeticionEvaluacion : {}  - end", id);
  }

  /**
   * Elimina una entidad {@link PeticionEvaluacion} por id.
   *
   * @param id el id de la entidad {@link PeticionEvaluacion}.
   */
  @Transactional
  public void delete(Long id) throws PeticionEvaluacionNotFoundException {
    log.debug("Petición a delete PeticionEvaluacion : {}  - start", id);

    AssertHelper.idNotNull(id, PeticionEvaluacion.class);
    if (!peticionEvaluacionRepository.existsById(id)) {
      throw new PeticionEvaluacionNotFoundException(id);
    }

    // Si la petición de evaluación tiene memorias en estado "en elaboración" o
    // "completadas" se desactivan
    List<Memoria> memoriasElaboracionDesactivar = this.memoriaService
        .findAllByPeticionEvaluacionIdAndEstadoActualId(id,
            TipoEstadoMemoria.Tipo.EN_ELABORACION.getId());
    List<Memoria> memoriasCompletadasDesactivar = this.memoriaService
        .findAllByPeticionEvaluacionIdAndEstadoActualId(id,
            TipoEstadoMemoria.Tipo.COMPLETADA.getId());

    if (!memoriasElaboracionDesactivar.isEmpty()) {
      memoriasElaboracionDesactivar.forEach(memoria -> {
        memoriaService.desactivar(memoria.getId());
      });
    }

    if (!memoriasCompletadasDesactivar.isEmpty()) {
      memoriasCompletadasDesactivar.forEach(memoria -> {
        memoriaService.desactivar(memoria.getId());
      });
    }

    peticionEvaluacionRepository.findById(id).map(peticionEvaluacion -> {
      peticionEvaluacion.setActivo(Boolean.FALSE);
      PeticionEvaluacion returnValue = peticionEvaluacionRepository.save(peticionEvaluacion);
      log.debug("update(PeticionEvaluacion peticionEvaluacionActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new PeticionEvaluacionNotFoundException(id));

    log.debug("Petición a delete PeticionEvaluacion : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link PeticionEvaluacion}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de PeticionEvaluacion: {} - start");
    peticionEvaluacionRepository.deleteAll();
    log.debug("Petición a deleteAll de PeticionEvaluacion: {} - end");

  }

  /**
   * Actualiza los datos del {@link PeticionEvaluacion}.
   * 
   * @param peticionEvaluacionActualizar {@link PeticionEvaluacion} con los datos
   *                                     actualizados.
   * @return El {@link PeticionEvaluacion} actualizado.
   * @throws PeticionEvaluacionNotFoundException Si no existe ningún
   *                                             {@link PeticionEvaluacion} con
   *                                             ese id.
   * @throws IllegalArgumentException            Si el {@link PeticionEvaluacion}
   *                                             no tiene id.
   */

  @Transactional
  public PeticionEvaluacion update(final PeticionEvaluacion peticionEvaluacionActualizar) {
    log.debug("update(PeticionEvaluacion peticionEvaluacionActualizar) - start");

    AssertHelper.idNotNull(peticionEvaluacionActualizar.getId(), PeticionEvaluacion.class);
    AssertHelper.i18nFieldNotNull(peticionEvaluacionActualizar.getTitulo(), PeticionEvaluacion.class, MSG_FIELD_TITULO);

    if (peticionEvaluacionActualizar.getExisteFinanciacion().booleanValue()) {
      AssertHelper.i18nFieldNotNull(peticionEvaluacionActualizar.getFuenteFinanciacion(), PeticionEvaluacion.class,
          MSG_FIELD_FUENTE_FINANCIACION);
      AssertHelper.entityNotNull(peticionEvaluacionActualizar.getEstadoFinanciacion(), PeticionEvaluacion.class,
          EstadoFinanciacion.class);
      AssertHelper.fieldNotNull(peticionEvaluacionActualizar.getImporteFinanciacion(), PeticionEvaluacion.class,
          MSG_FIELD_IMPORTE_FINANCIACION);
    }

    if (peticionEvaluacionActualizar.getValorSocial() != null
        && peticionEvaluacionActualizar.getValorSocial().equals(TipoValorSocial.OTRA_FINALIDAD)) {
      AssertHelper.i18nFieldNotNull(peticionEvaluacionActualizar.getOtroValorSocial(), PeticionEvaluacion.class,
          MSG_FIELD_OTRO_VALOR_SOCIAL);
    }

    assertIntroduccionPresentIfRequired(peticionEvaluacionActualizar);

    return peticionEvaluacionRepository.findById(peticionEvaluacionActualizar.getId()).map(peticionEvaluacion -> {
      peticionEvaluacion.setActivo(peticionEvaluacionActualizar.getActivo());
      peticionEvaluacion.setCodigo(peticionEvaluacionActualizar.getCodigo());
      peticionEvaluacion.setDisMetodologico(peticionEvaluacionActualizar.getDisMetodologico());
      peticionEvaluacion.setEstadoFinanciacion(peticionEvaluacionActualizar.getEstadoFinanciacion());
      peticionEvaluacion.setExisteFinanciacion(peticionEvaluacionActualizar.getExisteFinanciacion());
      peticionEvaluacion.setFechaFin(peticionEvaluacionActualizar.getFechaFin());
      peticionEvaluacion.setFechaInicio(peticionEvaluacionActualizar.getFechaInicio());
      peticionEvaluacion.setFuenteFinanciacion(peticionEvaluacionActualizar.getFuenteFinanciacion());
      peticionEvaluacion.setImporteFinanciacion(peticionEvaluacionActualizar.getImporteFinanciacion());
      peticionEvaluacion.setIntroduccion(peticionEvaluacionActualizar.getIntroduccion());
      peticionEvaluacion.setObjetivos(peticionEvaluacionActualizar.getObjetivos());
      peticionEvaluacion.setOtroValorSocial(peticionEvaluacionActualizar.getOtroValorSocial());
      peticionEvaluacion.setPersonaRef(peticionEvaluacionActualizar.getPersonaRef());
      peticionEvaluacion.setResumen(peticionEvaluacionActualizar.getResumen());
      peticionEvaluacion.setTieneFondosPropios(peticionEvaluacionActualizar.getTieneFondosPropios());
      peticionEvaluacion.setTipoActividad(peticionEvaluacionActualizar.getTipoActividad());
      peticionEvaluacion.setTipoInvestigacionTutelada(peticionEvaluacionActualizar.getTipoInvestigacionTutelada());
      peticionEvaluacion.setTitulo(peticionEvaluacionActualizar.getTitulo());
      peticionEvaluacion.setTutorRef(peticionEvaluacionActualizar.getTutorRef());
      peticionEvaluacion.setValorSocial(peticionEvaluacionActualizar.getValorSocial());

      PeticionEvaluacion returnValue = peticionEvaluacionRepository.save(peticionEvaluacion);
      log.debug("update(PeticionEvaluacion peticionEvaluacionActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new PeticionEvaluacionNotFoundException(peticionEvaluacionActualizar.getId()));
  }

  @Override
  @Transactional
  public void updateEstadoFinanciacion(Long id, EstadoFinanciacion estadoFinanciacion) {
    log.debug("updateEstadoFinanciacion - id: {}, estadoFinanciacion: {}", id, estadoFinanciacion);
    PeticionEvaluacion peticionEvaluacion = peticionEvaluacionRepository.findById(id)
        .orElseThrow(() -> new PeticionEvaluacionNotFoundException(id));

    if (Boolean.FALSE.equals(peticionEvaluacion.getExisteFinanciacion())
        || estadoFinanciacion == peticionEvaluacion.getEstadoFinanciacion()) {
      log.debug(
          "updateEstadoFinanciacion - sin cambios - id: {}, existeFinanciacion: {}, estadoFinanciacion: {}", id,
          peticionEvaluacion.getExisteFinanciacion(), peticionEvaluacion.getEstadoFinanciacion());
      return;
    }

    peticionEvaluacion.setEstadoFinanciacion(estadoFinanciacion);
    peticionEvaluacionRepository.save(peticionEvaluacion);
  }

  /**
   * Obtiene todas las entidades {@link PeticionEvaluacion} paginadas y filtadas.
   *
   * @param paging     la información de paginación.
   * @param query      información del filtro.
   * @param personaRef Referencia de la persona
   * @return el listado de entidades {@link PeticionEvaluacion} paginadas y
   *         filtradas.
   */
  public Page<PeticionEvaluacion> findAllByPersonaRef(String query, Pageable paging, String personaRef) {
    log.debug("findAllPeticionEvaluacion(String query,Pageable paging) - start");
    Specification<PeticionEvaluacion> specs = PeticionEvaluacionSpecifications.activos()
        .and(PeticionEvaluacionSpecifications.byPersonaRef(personaRef)).and(SgiRSQLJPASupport.toSpecification(query));

    Page<PeticionEvaluacion> returnValue = peticionEvaluacionRepository.findAll(specs, paging);
    log.debug("findAllPeticionEvaluacion(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una lista paginada y filtrada {@link PeticionEvaluacion} de una
   * persona responsable de memorias o creador de peticiones de evaluacion
   * 
   * @param query      Criterios de búsqueda
   * @param pageable   datos paginación
   * @param personaRef usuario
   * @return las entidades {@link PeticionEvaluacion}
   */
  @Override
  public Page<PeticionEvaluacionWithIsEliminable> findAllPeticionesWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(
      String query, Pageable pageable, String personaRef) {
    log.debug("findAllPeticionEvaluacionMemoria(String query, Pageable pageable, String personaRef) - start");
    Specification<Memoria> specsMem = null;
    Specification<PeticionEvaluacion> specsPet = null;
    if (StringUtils.isNotBlank(query)) {
      specsPet = SgiRSQLJPASupport.toSpecification(query, PeticionEvaluacionPredicateResolver.getInstance());
      specsMem = SgiRSQLJPASupport.toSpecification(query, MemoriaPredicateResolver.getInstance());
    }

    Page<PeticionEvaluacionWithIsEliminable> returnValue = peticionEvaluacionRepository
        .findAllPeticionEvaluacionMemoria(specsMem, specsPet, pageable, personaRef);
    log.debug("findAllPeticionEvaluacionMemoria(String query, Pageable pageable,  String personaRef) - end");
    return returnValue;
  }

  /**
   * Devuelve si la {@link PeticionEvaluacion} existe para la persona responsable
   * de memorias o creador de la petición de evaluación
   * 
   * @param personaRef           usuario
   * @param idPeticionEvaluacion identificador de la {@link PeticionEvaluacion}
   * @return las entidades {@link PeticionEvaluacion}
   */
  @Override
  public Boolean isPeticionWithPersonaRefCreadorPeticionEvaluacionOrResponsableMemoria(String personaRef,
      Long idPeticionEvaluacion) {
    log.debug(
        "isPeticionWithPersonaRefCreadorPeticionEvaluacionOrResponsableMemoria(String personaRef, Long idPeticionEvaluacion) - start");
    Specification<PeticionEvaluacion> specsPet = PeticionEvaluacionSpecifications.byId(idPeticionEvaluacion);
    Specification<Memoria> specsMem = MemoriaSpecifications.byPeticionEvaluacion(idPeticionEvaluacion);

    Page<PeticionEvaluacionWithIsEliminable> returnValue = peticionEvaluacionRepository
        .findAllPeticionEvaluacionMemoria(specsMem, specsPet, PageRequest.of(0, 1), personaRef);
    log.debug(
        "isPeticionWithPersonaRefCreadorPeticionEvaluacionOrResponsableMemoria(String personaRef, Long idPeticionEvaluacion) - end");
    return returnValue.hasContent();
  }

  /**
   * Comprueba que la {@link PeticionEvaluacion} tiene
   * {@link PeticionEvaluacion#introduccion} si el campo está configurado como
   * obligatorio.
   *
   * @param peticionEvaluacion una {@link PeticionEvaluacion}.
   * @throws IllegalArgumentException si el campo Introducción es obligatorio y
   *                                  no tiene valor.
   */
  private void assertIntroduccionPresentIfRequired(PeticionEvaluacion peticionEvaluacion) {
    Configuracion configuracion = configuracionRepository.findFirstByOrderByIdAsc().orElse(null);
    if (configuracion != null
        && configuracion.getPeticionEvaluacionIntroduccionVisibility() == FieldVisibility.REQUIRED) {
      AssertHelper.i18nFieldNotNull(peticionEvaluacion.getIntroduccion(), PeticionEvaluacion.class,
          MSG_FIELD_INTRODUCCION);
    }
  }

  /**
   * Genera el código de una {@link PeticionEvaluacion} con el formato
   * {@code AAAA/NNN}, el año de la fecha de referencia y el siguiente número
   * secuencial de ese año.
   *
   * @param referenceDate fecha de la que se toma el año del código.
   * @return el código generado.
   */
  private String generateCode(Instant referenceDate) {
    String year = String.valueOf(referenceDate
        .atZone(sgiConfigProperties.getTimeZone().toZoneId()).get(ChronoField.YEAR));
    PeticionEvaluacion lastPeticionEvaluacion = peticionEvaluacionRepository
        .findFirstByCodigoStartingWithOrderByCodigoDesc(year);

    long nextNumber = 1L;
    if (lastPeticionEvaluacion != null) {
      nextNumber = Long.parseLong(lastPeticionEvaluacion.getCodigo().split("/")[1]) + 1;
    }

    return String.format("%s/%03d", year, nextNumber);
  }

  /**
   * Vincula la {@link PeticionEvaluacion} a la unidad de vinculación que se
   * corresponde con el departamento del solicitante, si así está configurado.
   * 
   * Si el solicitante no tiene departamento, no existe la unidad, el id no está
   * dentro de la jerarquía esperada o falla la consulta a los sistemas externos,
   * la petición se queda sin unidad de vinculación y sin dar error.
   *
   * @param peticionEvaluacion una {@link PeticionEvaluacion}.
   */
  private void initializeUnidadVinculacionFromDepartamento(PeticionEvaluacion peticionEvaluacion) {
    Configuracion configuracion = configuracionRepository.findFirstByOrderByIdAsc().orElse(null);
    if (configuracion == null
        || !configuracion.isParamEnabled(Param.PETICION_EVALUACION_INICIALIZAR_UNIDAD_VINCULACION_ENABLED)) {
      return;
    }

    String tipoRef = configuracion.getTipoUnidadVinculacionDepartamentoRef();
    if (tipoRef == null) {
      log.warn(
          "initializeUnidadVinculacionFromDepartamento - inicialización habilitada pero sin tipo de unidad de vinculación de departamento configurado");
      return;
    }

    Long peticionEvaluacionId = peticionEvaluacion.getId();
    String personaRef = peticionEvaluacion.getPersonaRef();
    UnidadVinculacionOutput unidadVinculacion;
    try {
      String departamentoId = findDepartamentoId(personaRef).orElse(null);
      if (departamentoId == null) {
        log.debug(
            "initializeUnidadVinculacionFromDepartamento - el solicitante no tiene departamento - peticionEvaluacionId: {}, personaRef: {}",
            peticionEvaluacionId, personaRef);
        return;
      }

      unidadVinculacion = findUnidadVinculacionActiva(departamentoId).orElse(null);
      if (unidadVinculacion == null) {
        log.warn(
            "initializeUnidadVinculacionFromDepartamento - sin unidad de vinculación activa para el departamento - peticionEvaluacionId: {}, personaRef: {}, departamentoId: {}",
            peticionEvaluacionId, personaRef, departamentoId);
        return;
      }

      if (!isUnidadVinculacionOfTipo(unidadVinculacion, tipoRef)) {
        log.warn(
            "initializeUnidadVinculacionFromDepartamento - la unidad de vinculación no pertenece al tipo configurado - peticionEvaluacionId: {}, personaRef: {}, departamentoId: {}, tipoRef: {}",
            peticionEvaluacionId, personaRef, departamentoId, tipoRef);
        return;
      }
    } catch (RuntimeException e) {
      log.warn(
          "initializeUnidadVinculacionFromDepartamento - error consultando el departamento - peticionEvaluacionId: {}, personaRef: {}, tipoRef: {}",
          peticionEvaluacionId, personaRef, tipoRef, e);
      return;
    }

    PeticionEvaluacionUnidadVinculacion saved = peticionEvaluacionUnidadVinculacionRepository
        .save(PeticionEvaluacionUnidadVinculacion.builder()
            .peticionEvaluacionId(peticionEvaluacionId)
            .unidadVinculacionRef(unidadVinculacion.getId())
            .build());
    log.debug("initializeUnidadVinculacionFromDepartamento - peticionEvaluacionId: {}, unidadVinculacionRef: {}",
        peticionEvaluacionId, saved.getUnidadVinculacionRef());
  }

  /**
   * Busca en el SGP el departamento de la persona.
   *
   * @param personaRef referencia de la persona.
   * @return el id del departamento o vacío si la persona no tiene.
   */
  private Optional<String> findDepartamentoId(String personaRef) {
    return sgiApiSgpService.findVinculacionByPersonaId(personaRef)
        .map(VinculacionOutput::getDepartamento)
        .map(VinculacionOutput.DepartamentoOutput::getId);
  }

  /**
   * Busca en el SGO la unidad de vinculación activa con el mismo id que el
   * departamento.
   *
   * @param departamentoId id del departamento.
   * @return la unidad de vinculación o vacío si no existe o no está activa.
   */
  private Optional<UnidadVinculacionOutput> findUnidadVinculacionActiva(String departamentoId) {
    return sgiApiSgoService.findUnidadVinculacionById(departamentoId)
        .filter(unidad -> Boolean.TRUE.equals(unidad.getActivo()));
  }

  /**
   * Comprueba si la unidad de vinculación pertenece al tipo indicado, es decir,
   * si el tipo es alguno de sus predecesores.
   *
   * @param unidad  unidad de vinculación.
   * @param tipoRef referencia del tipo de unidad de vinculación.
   * @return true si la unidad pertenece al tipo de unidad de vinculación.
   */
  private boolean isUnidadVinculacionOfTipo(UnidadVinculacionOutput unidad, String tipoRef) {
    Set<String> visitedPredecesorIds = new HashSet<>();
    String predecesorId = unidad.getPredecesorId();
    while (predecesorId != null && visitedPredecesorIds.add(predecesorId)) {
      if (predecesorId.equals(tipoRef)) {
        return true;
      }

      predecesorId = sgiApiSgoService.findUnidadVinculacionById(predecesorId)
          .map(UnidadVinculacionOutput::getPredecesorId)
          .orElse(null);
    }

    return false;
  }

}
