package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;

import org.crue.hercules.sgi.eti.dto.EvaluacionWithNumComentario;
import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.specification.EvaluacionSpecifications;
import org.crue.hercules.sgi.eti.service.EvaluacionService;
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
 * Service Implementation para la gestión de {@link Evaluacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class EvaluacionServiceImpl implements EvaluacionService {
  /** Evaluación repository */
  private final EvaluacionRepository evaluacionRepository;

  /**
   * Instancia un nuevo {@link EvaluacionServiceImpl}
   * 
   * @param evaluacionRepository {@link} EvaluacionRepository}
   */
  public EvaluacionServiceImpl(EvaluacionRepository evaluacionRepository) {
    this.evaluacionRepository = evaluacionRepository;
  }

  /**
   * Guarda la entidad {@link Evaluacion}.
   *
   * @param evaluacion la entidad {@link Evaluacion} a guardar.
   * @return la entidad {@link Evaluacion} persistida.
   */
  @Transactional
  public Evaluacion create(Evaluacion evaluacion) {
    log.debug("Petición a create Evaluacion : {} - start", evaluacion);
    Assert.isNull(evaluacion.getId(), "Evaluacion id tiene que ser null para crear una nueva evaluacion");

    return evaluacionRepository.save(evaluacion);
  }

  /**
   * Obtiene todas las entidades {@link Evaluacion} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Evaluacion} paginadas y filtradas.
   */
  public Page<Evaluacion> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Specification<Evaluacion> specByQuery = new QuerySpecification<Evaluacion>(query);
    Specification<Evaluacion> specActivos = EvaluacionSpecifications.activos();

    Specification<Evaluacion> specs = Specification.where(specActivos).and(specByQuery);

    Page<Evaluacion> returnValue = evaluacionRepository.findAll(specs, paging);
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades paginadas {@link Evaluacion} activas para una
   * determinada {@link ConvocatoriaReunion}.
   *
   * @param id       Id de {@link ConvocatoriaReunion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  public Page<Evaluacion> findAllActivasByConvocatoriaReunionId(Long id, Pageable pageable) {
    log.debug("findAllActivasByConvocatoriaReunionId(Long id, Pageable pageable) - start");
    Page<Evaluacion> returnValue = evaluacionRepository.findAllByActivoTrueAndConvocatoriaReunionId(id, pageable);
    log.debug("findAllActivasByConvocatoriaReunionId(Long id, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades paginadas {@link Evaluacion} para una determinada
   * {@link Memoria}.
   *
   * @param idMemoria    Id de {@link Memoria}.
   * @param idEvaluacion Id de {@link Evaluacion}
   * @param pageable     la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @Override
  public Page<EvaluacionWithNumComentario> findEvaluacionesAnterioresByMemoria(Long idMemoria, Long idEvaluacion,
      Pageable pageable) {
    log.debug("findEvaluacionesAnterioresByMemoria(Long id, Pageable pageable) - start");
    Assert.notNull(idMemoria, "El id de la memoria no puede ser nulo para mostrar sus evaluaciones");
    Assert.notNull(idEvaluacion, "El id de la evaluación no puede ser nulo para recuperar las evaluaciones anteriores");
    Page<EvaluacionWithNumComentario> returnValue = evaluacionRepository.findEvaluacionesAnterioresByMemoria(idMemoria,
        idEvaluacion, pageable);
    log.debug("findEvaluacionesAnterioresByMemoria(Long id, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Evaluacion} según su
   * {@link Evaluador}.
   * 
   * @param personaRef Identificador del {@link Evaluacion}
   * @param query      filtro de {@link QueryCriteria}.
   * @param pageable   pageable
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @Override
  public Page<Evaluacion> findByEvaluadorPersonaRef(String personaRef, List<QueryCriteria> query, Pageable pageable) {
    log.debug("findByEvaluador(Long id, Pageable pageable) - start");
    Assert.notNull(personaRef, "El userRefId de la evaluación no puede ser nulo para mostrar sus evaluaciones");
    Page<Evaluacion> returnValue = evaluacionRepository.findByEvaluador(personaRef, query, pageable);
    log.debug("findByEvaluador(Long id, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Evaluacion} según su
   * {@link Evaluador}.
   * 
   * @param personaRef Identificador del {@link Evaluacion}
   * @param query      filtro de {@link QueryCriteria}.
   * @param pageable   pageable
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @Override
  public Page<Evaluacion> findByEvaluador(String personaRef, List<QueryCriteria> query, Pageable pageable) {
    log.debug("findByEvaluador(Long id, Pageable pageable) - start");
    Assert.notNull(personaRef, "El personaRef de la evaluación no puede ser nulo para mostrar sus evaluaciones");
    Page<Evaluacion> returnValue = evaluacionRepository.findByEvaluador(personaRef, query, pageable);
    log.debug("findByEvaluador(Long id, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene la última versión de las memorias en estado "En evaluación" o "En
   * secretaria revisión mínima", y evaluaciones de tipo retrospectiva asociadas a
   * memoria con el campo estado de retrospectiva en "En evaluación".
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Evaluacion} paginadas y filtradas.
   */
  public Page<Evaluacion> findAllByMemoriaAndRetrospectivaEnEvaluacion(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAllByMemoriaAndRetrospectivaEnEvaluacion(List<QueryCriteria> query,Pageable paging) - start");

    Page<Evaluacion> returnValue = evaluacionRepository.findAllByMemoriaAndRetrospectivaEnEvaluacion(query, paging);
    log.debug("findAllByMemoriaAndRetrospectivaEnEvaluacion(List<QueryCriteria> query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Evaluacion}, en estado "En evaluación
   * seguimiento anual" (id = 11), "En evaluación seguimiento final" (id = 12) o
   * "En secretaría seguimiento final aclaraciones" (id = 13), paginadas asociadas
   * a un evaluador
   * 
   * @param personaRef Persona ref del {@link Evaluador}
   * @param query      filtro de {@link QueryCriteria}.
   * @param pageable   pageable
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  @Override
  public Page<Evaluacion> findEvaluacionesEnSeguimientosByEvaluador(String personaRef, List<QueryCriteria> query,
      Pageable pageable) {
    log.debug(
        "findEvaluacionesEnSeguimientosByEvaluador(String idEvaluador, List<QueryCriteria> query, Pageable pageable) - start");
    Assert.notNull(personaRef,
        "El personaRef de la evaluación no puede ser nulo para mostrar sus evaluaciones en seguimiento");
    Page<Evaluacion> evaluaciones = evaluacionRepository.findEvaluacionesEnSeguimientosByEvaluador(personaRef, query,
        pageable);
    log.debug(
        "findEvaluacionesEnSeguimientosByEvaluador(String personaRef, List<QueryCriteria> query, Pageable pageable) - end");
    return evaluaciones;
  }

  /**
   * Obtiene una entidad {@link Evaluacion} por id.
   *
   * @param id el id de la entidad {@link Evaluacion}.
   * @return la entidad {@link Evaluacion}.
   * @throws EvaluacionNotFoundException Si no existe ningún {@link Evaluacion} *
   *                                     con ese id.
   */
  public Evaluacion findById(final Long id) throws EvaluacionNotFoundException {
    log.debug("Petición a get Evaluacion : {}  - start", id);
    final Evaluacion Evaluacion = evaluacionRepository.findById(id)
        .orElseThrow(() -> new EvaluacionNotFoundException(id));
    log.debug("Petición a get Evaluacion : {}  - end", id);
    return Evaluacion;

  }

  /**
   * Elimina una entidad {@link Evaluacion} por id.
   *
   * @param id el id de la entidad {@link Evaluacion}.
   */
  @Transactional
  public void delete(Long id) throws EvaluacionNotFoundException {
    log.debug("Petición a delete Evaluacion : {}  - start", id);
    Assert.notNull(id, "El id de Evaluacion no puede ser null.");
    if (!evaluacionRepository.existsById(id)) {
      throw new EvaluacionNotFoundException(id);
    }
    evaluacionRepository.deleteById(id);
    log.debug("Petición a delete Evaluacion : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link Evaluacion}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de Evaluacion: {} - start");
    evaluacionRepository.deleteAll();
    log.debug("Petición a deleteAll de Evaluacion: {} - end");

  }

  /**
   * Actualiza los datos del {@link Evaluacion}.
   * 
   * @param evaluacionActualizar {@link Evaluacion} con los datos actualizados.
   * @return El {@link Evaluacion} actualizado.
   * @throws EvaluacionNotFoundException Si no existe ningún {@link Evaluacion}
   *                                     con ese id.
   * @throws IllegalArgumentException    Si el {@link Evaluacion} no tiene id.
   */

  @Transactional
  public Evaluacion update(final Evaluacion evaluacionActualizar) {
    log.debug("update(Evaluacion evaluacionActualizar) - start");

    Assert.notNull(evaluacionActualizar.getId(), "Evaluacion id no puede ser null para actualizar una evaluacion");

    return evaluacionRepository.findById(evaluacionActualizar.getId()).map(evaluacion -> {
      evaluacion.setDictamen(evaluacionActualizar.getDictamen());
      evaluacion.setEsRevMinima(evaluacionActualizar.getEsRevMinima());
      evaluacion.setFechaDictamen(evaluacionActualizar.getFechaDictamen());
      evaluacion.setMemoria(evaluacionActualizar.getMemoria());
      evaluacion.setVersion(evaluacionActualizar.getVersion());
      evaluacion.setConvocatoriaReunion(evaluacionActualizar.getConvocatoriaReunion());
      evaluacion.setActivo(evaluacionActualizar.getActivo());
      evaluacion.setTipoEvaluacion(evaluacionActualizar.getTipoEvaluacion());

      Evaluacion returnValue = evaluacionRepository.save(evaluacion);
      log.debug("update(Evaluacion evaluacionActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new EvaluacionNotFoundException(evaluacionActualizar.getId()));
  }
}
