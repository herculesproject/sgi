package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Plan;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Programa}.
 */
public interface ProgramaService {

  /**
   * Guardar un nuevo {@link Programa}.
   *
   * @param programa la entidad {@link Programa} a guardar.
   * @return la entidad {@link Programa} persistida.
   */
  Programa create(Programa programa);

  /**
   * Actualizar {@link Programa} y si se pone activo a false hace lo mismo con
   * todos sus hijos en cascada.
   *
   * @param programaActualizar la entidad {@link Programa} a actualizar.
   * @return la entidad {@link Programa} persistida.
   */
  Programa update(Programa programaActualizar);

  /**
   * Desactiva el {@link Programa} y todos sus hijos en cascada.
   *
   * @param id Id del {@link Programa}.
   * @return la entidad {@link Programa} persistida.
   */
  Programa disable(Long id);

  /**
   * Obtiene {@link Programa} por su id.
   *
   * @param id el id de la entidad {@link Programa}.
   * @return la entidad {@link Programa}.
   */
  Programa findById(Long id);

  /**
   * Obtiene los {@link Programa} activos para un {@link Plan}.
   *
   * @param idPlan   el id de la entidad {@link Plan}.
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Programa} del {@link Plan} paginadas.
   */
  Page<Programa> findAllByPlan(Long idPlan, List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene los {@link Programa} para un {@link Plan}.
   *
   * @param idPlan   el id de la entidad {@link Plan}.
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Programa} del {@link Plan} paginadas.
   */
  Page<Programa> findAllTodosByPlan(Long idPlan, List<QueryCriteria> query, Pageable pageable);

}