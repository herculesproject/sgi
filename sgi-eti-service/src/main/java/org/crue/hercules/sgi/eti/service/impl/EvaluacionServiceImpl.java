package org.crue.hercules.sgi.eti.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.crue.hercules.sgi.eti.dto.EvaluacionWithNumComentario;
import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.repository.EstadoMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.MemoriaRepository;
import org.crue.hercules.sgi.eti.repository.RetrospectivaRepository;
import org.crue.hercules.sgi.eti.repository.specification.EvaluacionSpecifications;
import org.crue.hercules.sgi.eti.service.EvaluacionService;
import org.crue.hercules.sgi.eti.util.Constantes;
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
  /** Memoria repository */
  private final MemoriaRepository memoriaRepository;
  /** Estado Memoria repository */
  private final EstadoMemoriaRepository estadoMemoriaRepository;
  /** Retrospectiva repository */
  private final RetrospectivaRepository retrospectivaRepository;

  /**
   * Instancia un nuevo {@link EvaluacionServiceImpl}
   * 
   * @param evaluacionRepository    repository para {@link Evaluacion}
   * @param memoriaRepository       repository para {@link Memoria}
   * @param estadoMemoriaRepository repository para {@link EstadoMemoria}
   * @param retrospectivaRepository repository para {@link Retrospectiva}
   */
  public EvaluacionServiceImpl(EvaluacionRepository evaluacionRepository, MemoriaRepository memoriaRepository,
      EstadoMemoriaRepository estadoMemoriaRepository, RetrospectivaRepository retrospectivaRepository) {
    this.evaluacionRepository = evaluacionRepository;
    this.memoriaRepository = memoriaRepository;
    this.estadoMemoriaRepository = estadoMemoriaRepository;
    this.retrospectivaRepository = retrospectivaRepository;
  }

  /**
   * Guarda la entidad {@link Evaluacion}.
   * 
   * Cuando se generan las evaluaciones al asignar memorias a una
   * {@link ConvocatoriaReunion} el tipo de la evaluación vendrá dado por el tipo
   * de la {@link ConvocatoriaReunion} y el estado de la {@link Memoria} o de la
   * {@link Retrospectiva}. Además será necesario actualizar el estado de la
   * {@link Memoria} o de la {@link Retrospectiva} al estado 'En evaluacion'
   * dependiendo del tipo de evaluación.
   *
   * @param evaluacion la entidad {@link Evaluacion} a guardar.
   * @return la entidad {@link Evaluacion} persistida.
   */
  @Transactional
  public Evaluacion create(Evaluacion evaluacion) {
    log.debug("Petición a create Evaluacion : {} - start", evaluacion);
    Assert.isNull(evaluacion.getId(), "Evaluacion id tiene que ser null para crear una nueva evaluacion");

    // Si la evaluación es creada mediante la asignación de memorias en
    // ConvocatoriaReunión
    if (evaluacion.getConvocatoriaReunion() != null && evaluacion.getConvocatoriaReunion().getId() != null) {

      // Convocatoria Seguimiento
      if (evaluacion.getConvocatoriaReunion().getTipoConvocatoriaReunion()
          .getId() == Constantes.TIPO_CONVOCATORIA_REUNION_SEGUIMIENTO) {
        // mismo tipo seguimiento que la memoria Anual(3) Final(4)
        evaluacion.getTipoEvaluacion()
            .setId((evaluacion.getMemoria().getEstadoActual()
                .getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_ANUAL)
                    ? Constantes.TIPO_EVALUACION_SEGUIMIENTO_ANUAL
                    : Constantes.TIPO_EVALUACION_SEGUIMIENTO_FINAL);
        // se actualiza estado de la memoria a 'En evaluación'
        evaluacion.getMemoria().getEstadoActual()
            .setId((evaluacion.getMemoria().getEstadoActual()
                .getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_ANUAL)
                    ? Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_ANUAL
                    : Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_FINAL);

        // Convocatoria Ordinaria o Extraordinaria
      } else {
        // memoria 'en secretaría' y retrospectiva 'en secretaría'
        if (evaluacion.getMemoria().getEstadoActual().getId() > Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA
            && evaluacion.getMemoria().getRequiereRetrospectiva() && evaluacion.getMemoria().getRetrospectiva()
                .getEstadoRetrospectiva().getId() == Constantes.ESTADO_RETROSPECTIVA_EN_SECRETARIA) {
          // tipo retrospectiva
          evaluacion.getTipoEvaluacion().setId(Constantes.TIPO_EVALUACION_RETROSPECTIVA);
          // se actualiza el estado retrospectiva a 'En evaluación'
          evaluacion.getMemoria().getRetrospectiva().getEstadoRetrospectiva()
              .setId(Constantes.ESTADO_RETROSPECTIVA_EN_EVALUACION);

        } else {
          // tipo 'memoria'
          evaluacion.getTipoEvaluacion().setId(Constantes.TIPO_EVALUACION_MEMORIA);
          // se actualiza estado de la memoria a 'En evaluación'
          evaluacion.getMemoria().getEstadoActual().setId(Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION);
        }
      }

      if (evaluacion.getTipoEvaluacion().getId() == Constantes.TIPO_EVALUACION_RETROSPECTIVA) {
        retrospectivaRepository.save(evaluacion.getMemoria().getRetrospectiva());
      } else {
        this.estadoMemoriaRepository.save(new EstadoMemoria(null, evaluacion.getMemoria(),
            evaluacion.getMemoria().getEstadoActual(), LocalDateTime.now()));
      }
      evaluacion.getMemoria().setVersion(evaluacion.getVersion());
      this.memoriaRepository.save(evaluacion.getMemoria());

    }

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
   * Obtiene la lista de evaluaciones activas de una convocatoria reunion que no
   * estan en revisión mínima.
   * 
   * @param idConvocatoriaReunion Id de {@link ConvocatoriaReunion}.
   * @param query                 información del filtro.
   * @param paging                la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @Override
  public Page<Evaluacion> findAllByConvocatoriaReunionIdAndNoEsRevMinima(Long idConvocatoriaReunion,
      List<QueryCriteria> query, Pageable paging) {
    log.debug(
        "findAllByConvocatoriaReunionIdAndNoEsRevMinima(Long idConvocatoriaReunion, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<Evaluacion> specByQuery = new QuerySpecification<Evaluacion>(query);
    Specification<Evaluacion> specByConvocatoriaReunion = EvaluacionSpecifications
        .byConvocatoriaReunionId(idConvocatoriaReunion);
    Specification<Evaluacion> specByEsRevMinima = EvaluacionSpecifications.byEsRevMinima(false);
    Specification<Evaluacion> specActivas = EvaluacionSpecifications.activos();

    Specification<Evaluacion> specs = Specification.where(specByConvocatoriaReunion).and(specByEsRevMinima)
        .and(specActivas).and(specByQuery);

    Page<Evaluacion> returnValue = evaluacionRepository.findAll(specs, paging);

    log.debug(
        "findAllByConvocatoriaReunionIdAndNoEsRevMinima(Long idConvocatoriaReunion, List<QueryCriteria> query, Pageable pageable) - end");

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
