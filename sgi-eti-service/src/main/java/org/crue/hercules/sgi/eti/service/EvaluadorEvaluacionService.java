package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.EvaluadorEvaluacion;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.EvaluadorEvaluacionNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link EvaluadorEvaluacion}.
 */
public interface EvaluadorEvaluacionService {
  /**
   * Guardar {@link EvaluadorEvaluacion}.
   *
   * @param evaluadorEvaluacion la entidad {@link EvaluadorEvaluacion} a guardar.
   * @return la entidad {@link EvaluadorEvaluacion} persistida.
   */
  EvaluadorEvaluacion create(EvaluadorEvaluacion evaluadorEvaluacion);

  /**
   * Actualizar {@link EvaluadorEvaluacion}.
   *
   * @param evaluadorEvaluacion la entidad {@link EvaluadorEvaluacion} a
   *                            actualizar.
   * @return la entidad {@link EvaluadorEvaluacion} persistida.
   */
  EvaluadorEvaluacion update(EvaluadorEvaluacion evaluadorEvaluacion);

  /**
   * Obtener todas las entidades {@link EvaluadorEvaluacion} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link EvaluadorEvaluacion} paginadas y/o
   *         filtradas.
   */
  Page<EvaluadorEvaluacion> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link EvaluadorEvaluacion} por id.
   *
   * @param id el id de la entidad {@link EvaluadorEvaluacion}.
   * @return la entidad {@link EvaluadorEvaluacion}.
   */
  EvaluadorEvaluacion findById(Long id);

  /**
   * Elimina el {@link EvaluadorEvaluacion} por id.
   *
   * @param id el id de la entidad {@link EvaluadorEvaluacion}.
   */
  void delete(Long id) throws EvaluadorEvaluacionNotFoundException;

  /**
   * Elimina todos los {@link EvaluadorEvaluacion}.
   */
  void deleteAll();

}