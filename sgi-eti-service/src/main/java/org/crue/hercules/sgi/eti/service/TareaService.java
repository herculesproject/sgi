package org.crue.hercules.sgi.eti.service;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.PeticionEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Tarea}.
 */
public interface TareaService {

  /**
   * Guardar {@link Tarea}.
   *
   * @param tarea la entidad {@link Tarea} a guardar.
   * @return la entidad {@link Tarea} persistida.
   */
  Tarea create(Tarea tarea);

  /**
   * Actualizar {@link Tarea}.
   *
   * @param tarea la entidad {@link Tarea} a actualizar.
   * @return la entidad {@link Tarea} persistida.
   */
  Tarea update(Tarea tarea);

  /**
   * Obtener todas las entidades {@link Tarea} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Tarea} paginadas y/o filtradas.
   */
  Page<Tarea> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link Tarea} por id.
   *
   * @param id el id de la entidad {@link Tarea}.
   * @return la entidad {@link Tarea}.
   */
  Tarea findById(Long id);

  /**
   * Elimina la {@link Tarea} por id.
   *
   * @param id el id de la entidad {@link Tarea}.
   */
  void delete(Long id) throws PeticionEvaluacionNotFoundException;

  /**
   * Elimina todos las {@link Tarea}.
   */
  void deleteAll();

}