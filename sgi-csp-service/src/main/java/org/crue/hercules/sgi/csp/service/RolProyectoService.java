package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link RolProyecto}.
 */

public interface RolProyectoService {

  /**
   * Guarda la entidad {@link RolProyecto}.
   * 
   * @param rolProyecto la entidad {@link RolProyecto} a guardar.
   * @return RolProyecto la entidad {@link RolProyecto} persistida.
   */
  RolProyecto create(RolProyecto rolProyecto);

  /**
   * Actualiza los datos del {@link RolProyecto}.
   * 
   * @param rolProyecto {@link RolProyecto} con los datos actualizados.
   * @return RolProyecto {@link RolProyecto} actualizado.
   */
  RolProyecto update(final RolProyecto rolProyecto);

  /**
   * Reactiva el {@link RolProyecto}.
   *
   * @param id Id del {@link RolProyecto}.
   * @return la entidad {@link RolProyecto} persistida.
   */
  RolProyecto enable(Long id);

  /**
   * Desactiva el {@link RolProyecto}.
   *
   * @param id Id del {@link RolProyecto}.
   * @return la entidad {@link RolProyecto} persistida.
   */
  RolProyecto disable(Long id);

  /**
   * Comprueba la existencia del {@link RolProyecto} por id.
   *
   * @param id el id de la entidad {@link RolProyecto}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene una entidad {@link RolProyecto} por id.
   * 
   * @param id Identificador de la entidad {@link RolProyecto}.
   * @return RolProyecto la entidad {@link RolProyecto}.
   */
  RolProyecto findById(final Long id);

  /**
   * Obtiene todas las entidades {@link RolProyecto} activas paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link RolProyecto} activas paginadas y
   *         filtradas.
   */
  Page<RolProyecto> findAll(String query, Pageable paging);

  /**
   * Obtiene todas las entidades {@link RolProyecto} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link RolProyecto} paginadas y filtradas.
   */
  Page<RolProyecto> findAllTodos(String query, Pageable paging);

}
