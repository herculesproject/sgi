package org.crue.hercules.sgi.usr.service;

import java.util.List;

import org.crue.hercules.sgi.usr.model.Unidad;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Unidad}.
 */
public interface UnidadService {

  /**
   * Guardar un nuevo {@link Unidad}.
   *
   * @param unidad la entidad {@link Unidad} a guardar.
   * @return la entidad {@link Unidad} persistida.
   */
  Unidad create(Unidad unidad);

  /**
   * Actualizar {@link Unidad}.
   *
   * @param unidadActualizar la entidad {@link Unidad} a actualizar.
   * @return la entidad {@link Unidad} persistida.
   */
  Unidad update(Unidad unidadActualizar);

  /**
   * Desactiva el {@link Unidad}.
   *
   * @param id Id del {@link Unidad}.
   * @return la entidad {@link Unidad} persistida.
   */
  Unidad disable(Long id);

  /**
   * Obtener todas las entidades {@link Unidad} activas paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Unidad} paginadas y/o filtradas.
   */
  Page<Unidad> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtener todas las entidades {@link Unidad} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Unidad} paginadas y/o filtradas.
   */
  Page<Unidad> findAllTodos(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link Unidad} por su id.
   *
   * @param id el id de la entidad {@link Unidad}.
   * @return la entidad {@link Unidad}.
   */
  Unidad findById(Long id);

  /**
   * Obtiene {@link Unidad} por su acrónimo.
   *
   * @param acronimo el acrónimo de la entidad {@link Unidad}.
   * @return la entidad {@link Unidad}.
   */
  Unidad findByAcronimo(String acronimo);

  /**
   * Recupera una lista de paginada de {@link Unidad} restringidas por los
   * permisos del usuario logueado.
   * 
   * @param query                  datos de búsqueda
   * @param acronimosUnidadGestion listado de los acrónimos de las unidades de
   *                               gestión del usuario logueado.
   * @param paging                 datos de la paginación
   * @return listado paginado de {@link Unidad}
   */
  Page<Unidad> findAllRestringidos(List<QueryCriteria> query, List<String> acronimosUnidadGestion, Pageable paging);

}
