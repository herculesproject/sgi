package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.exceptions.TipoFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoFinanciacion}.
 */
public interface TipoFinanciacionService {

  /**
   * Guardar {@link TipoFinanciacion}.
   *
   * @param tipoFinanciacion la entidad {@link TipoFinanciacion} a guardar.
   * @return la entidad {@link TipoFinanciacion} persistida.
   */
  TipoFinanciacion create(TipoFinanciacion tipoFinanciacion);

  /**
   * Actualizar {@link TipoFinanciacion}.
   *
   * @param tipoFinanciacion la entidad {@link TipoFinanciacion} a actualizar.
   * @return la entidad {@link TipoFinanciacion} persistida.
   */
  TipoFinanciacion update(TipoFinanciacion tipoFinanciacion);

  /**
   * Obtener todas las entidades {@link TipoFinanciacion} activos paginadas y/o
   * filtradas
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link TipoFinanciacion} paginadas y/o
   *         filtradas
   */
  Page<TipoFinanciacion> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtener todas las entidades {@link TipoFinanciacion} paginadas y/o filtradas
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link TipoFinanciacion} paginadas y/o
   *         filtradas
   */
  Page<TipoFinanciacion> findAllTodos(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link TipoFinanciacion} por id.
   *
   * @param id el id de la entidad {@link TipoFinanciacion}.
   * @return la entidad {@link TipoFinanciacion}.
   */
  TipoFinanciacion findById(Long id);

  /**
   * Reactiva el {@link TipoFinanciacion}.
   *
   * @param id Id del {@link TipoFinanciacion}.
   * @return la entidad {@link TipoFinanciacion} persistida.
   */
  TipoFinanciacion enable(Long id);

  /**
   * Desactiva el {@link TipoFinanciacion}.
   *
   * @param id el id de la entidad {@link TipoFinanciacion}.
   * @return la entidad {@link TipoFinanciacion} actualizadas.
   */
  TipoFinanciacion disable(Long id) throws TipoFinanciacionNotFoundException;

}
