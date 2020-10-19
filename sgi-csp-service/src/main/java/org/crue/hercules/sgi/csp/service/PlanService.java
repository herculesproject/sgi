package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Plan;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Plan}.
 */
public interface PlanService {

  /**
   * Guardar un nuevo {@link Plan}.
   *
   * @param plan la entidad {@link Plan} a guardar.
   * @return la entidad {@link Plan} persistida.
   */
  Plan create(Plan plan);

  /**
   * Actualizar {@link Plan}.
   *
   * @param planActualizar la entidad {@link Plan} a actualizar.
   * @return la entidad {@link Plan} persistida.
   */
  Plan update(Plan planActualizar);

  /**
   * Desactiva el {@link Plan}.
   *
   * @param id Id del {@link Plan}.
   * @return la entidad {@link Plan} persistida.
   */
  Plan disable(Long id);

  /**
   * Obtener todas las entidades {@link Plan} activos paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Plan} paginadas y/o filtradas.
   */
  Page<Plan> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtener todas las entidades {@link Plan} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Plan} paginadas y/o filtradas.
   */
  Page<Plan> findAllTodos(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link Plan} por su id.
   *
   * @param id el id de la entidad {@link Plan}.
   * @return la entidad {@link Plan}.
   */
  Plan findById(Long id);

}