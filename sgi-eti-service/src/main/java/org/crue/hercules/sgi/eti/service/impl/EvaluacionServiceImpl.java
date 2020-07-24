package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
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
  private final EvaluacionRepository evaluacionRepository;

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
    Specification<Evaluacion> spec = new QuerySpecification<Evaluacion>(query);

    Page<Evaluacion> returnValue = evaluacionRepository.findAll(spec, paging);
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Evaluacion} por id.
   *
   * @param id el id de la entidad {@link Evaluacion}.
   * @return la entidad {@link Evaluacion}.
   * @throws EvaluacionNotFoundException Si no existe ningún {@link Evaluacion}
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
