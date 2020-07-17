package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.EvaluadorEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.EvaluadorEvaluacion;
import org.crue.hercules.sgi.eti.repository.EvaluadorEvaluacionRepository;
import org.crue.hercules.sgi.eti.service.EvaluadorEvaluacionService;
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
 * Service Implementation para la gestión de {@link EvaluadorEvaluacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class EvaluadorEvaluacionServiceImpl implements EvaluadorEvaluacionService {
  private final EvaluadorEvaluacionRepository evaluadorEvaluacionRepository;

  public EvaluadorEvaluacionServiceImpl(EvaluadorEvaluacionRepository evaluadorEvaluacionRepository) {
    this.evaluadorEvaluacionRepository = evaluadorEvaluacionRepository;
  }

  /**
   * Guarda la entidad {@link EvaluadorEvaluacion}.
   *
   * @param evaluadorEvaluacion la entidad {@link EvaluadorEvaluacion} a guardar.
   * @return la entidad {@link EvaluadorEvaluacion} persistida.
   */
  @Transactional
  public EvaluadorEvaluacion create(EvaluadorEvaluacion evaluadorEvaluacion) {
    log.debug("Petición a create EvaluadorEvaluacion : {} - start", evaluadorEvaluacion);
    Assert.isNull(evaluadorEvaluacion.getId(),
        "EvaluadorEvaluacion id tiene que ser null para crear un nuevo EvaluadorEvaluacion");

    return evaluadorEvaluacionRepository.save(evaluadorEvaluacion);
  }

  /**
   * Obtiene todas las entidades {@link EvaluadorEvaluacion} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link EvaluadorEvaluacion} paginadas y
   *         filtradas.
   */
  public Page<EvaluadorEvaluacion> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Specification<EvaluadorEvaluacion> spec = new QuerySpecification<EvaluadorEvaluacion>(query);

    Page<EvaluadorEvaluacion> returnValue = evaluadorEvaluacionRepository.findAll(spec, paging);
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link EvaluadorEvaluacion} por id.
   *
   * @param id el id de la entidad {@link EvaluadorEvaluacion}.
   * @return la entidad {@link EvaluadorEvaluacion}.
   * @throws EvaluadorEvaluacionNotFoundException Si no existe ningún
   *                                              {@link EvaluadorEvaluacion} con
   *                                              ese id.
   */
  public EvaluadorEvaluacion findById(final Long id) throws EvaluadorEvaluacionNotFoundException {
    log.debug("Petición a get EvaluadorEvaluacion : {}  - start", id);
    final EvaluadorEvaluacion evaluadorEvaluacion = evaluadorEvaluacionRepository.findById(id)
        .orElseThrow(() -> new EvaluadorEvaluacionNotFoundException(id));
    log.debug("Petición a get EvaluadorEvaluacion : {}  - end", id);
    return evaluadorEvaluacion;

  }

  /**
   * Elimina una entidad {@link EvaluadorEvaluacion} por id.
   *
   * @param id el id de la entidad {@link EvaluadorEvaluacion}.
   */
  @Transactional
  public void delete(Long id) throws EvaluadorEvaluacionNotFoundException {
    log.debug("Petición a delete EvaluadorEvaluacion : {}  - start", id);
    Assert.notNull(id, "El id de EvaluadorEvaluacion no puede ser null.");
    if (!evaluadorEvaluacionRepository.existsById(id)) {
      throw new EvaluadorEvaluacionNotFoundException(id);
    }
    evaluadorEvaluacionRepository.deleteById(id);
    log.debug("Petición a delete EvaluadorEvaluacion : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link EvaluadorEvaluacion}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de EvaluadorEvaluacion: {} - start");
    evaluadorEvaluacionRepository.deleteAll();
    log.debug("Petición a deleteAll de EvaluadorEvaluacion: {} - end");

  }

  /**
   * Actualiza los datos del {@link EvaluadorEvaluacion}.
   * 
   * @param evaluadorEvaluacionActualizar {@link EvaluadorEvaluacion} con los
   *                                      datos actualizados.
   * @return El {@link EvaluadorEvaluacion} actualizado.
   * @throws EvaluadorEvaluacionNotFoundException Si no existe ningún
   *                                              {@link EvaluadorEvaluacion} con
   *                                              ese id.
   * @throws IllegalArgumentException             Si el
   *                                              {@link EvaluadorEvaluacion} no
   *                                              tiene id.
   */

  @Transactional
  public EvaluadorEvaluacion update(final EvaluadorEvaluacion evaluadorEvaluacionActualizar) {
    log.debug("update(EvaluadorEvaluacion EvaluadorEvaluacionActualizar) - start");

    Assert.notNull(evaluadorEvaluacionActualizar.getId(),
        "EvaluadorEvaluacion id no puede ser null para actualizar un EvaluadorEvaluacion");

    return evaluadorEvaluacionRepository.findById(evaluadorEvaluacionActualizar.getId()).map(evaluadorEvaluacion -> {
      evaluadorEvaluacion.setEvaluador(evaluadorEvaluacionActualizar.getEvaluador());
      evaluadorEvaluacion.setEvaluacion(evaluadorEvaluacionActualizar.getEvaluacion());

      EvaluadorEvaluacion returnValue = evaluadorEvaluacionRepository.save(evaluadorEvaluacion);
      log.debug("update(EvaluadorEvaluacion EvaluadorEvaluacionActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new EvaluadorEvaluacionNotFoundException(evaluadorEvaluacionActualizar.getId()));
  }

}
