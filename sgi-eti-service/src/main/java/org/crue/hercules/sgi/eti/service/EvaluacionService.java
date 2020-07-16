package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Evaluacion}.
 */
public interface EvaluacionService {
  /**
   * Guardar {@link Evaluacion}.
   *
   * @param evaluacion la entidad {@link Evaluacion} a guardar.
   * @return la entidad {@link Evaluacion} persistida.
   */
  Evaluacion create(Evaluacion evaluacion);

  /**
   * Actualizar {@link Evaluacion}.
   *
   * @param evaluacion la entidad {@link Evaluacion} a actualizar.
   * @return la entidad {@link Evaluacion} persistida.
   */
  Evaluacion update(Evaluacion evaluacion);

  /**
   * Obtener todas las entidades {@link Evaluacion} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  Page<Evaluacion> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link Evaluacion} por id.
   *
   * @param id el id de la entidad {@link Evaluacion}.
   * @return la entidad {@link Evaluacion}.
   */
  Evaluacion findById(Long id);

  /**
   * Elimina el {@link Evaluacion} por id.
   *
   * @param id el id de la entidad {@link Evaluacion}.
   */
  void delete(Long id) throws EvaluacionNotFoundException;

  /**
   * Elimina todos los {@link Evaluacion}.
   */
  void deleteAll();

}