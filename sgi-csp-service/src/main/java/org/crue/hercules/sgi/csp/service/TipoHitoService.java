package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.exceptions.TipoHitoNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoHito}.
 */
public interface TipoHitoService {

  /**
   * Guardar {@link TipoHito}.
   *
   * @param tipoHito la entidad {@link TipoHito} a guardar.
   * @return la entidad {@link TipoHito} persistida.
   */
  TipoHito create(TipoHito tipoHito);

  /**
   * Actualizar {@link TipoHito}.
   *
   * @param tipoHito la entidad {@link TipoHito} a actualizar.
   * @return la entidad {@link TipoHito} persistida.
   */
  TipoHito update(TipoHito tipoHito);

  /**
   * Obtener todas las entidades {@link TipoHito} paginadas y/o filtradas
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link TipoHito} paginadas y/o filtradas
   */
  Page<TipoHito> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtener todas las entidades {@link TipoHito} activas paginadas y/o filtradas
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link TipoHito} paginadas y/o filtradas
   */
  Page<TipoHito> findAllTodos(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link TipoHito} por id.
   *
   * @param id el id de la entidad {@link TipoHito}.
   * @return la entidad {@link TipoHito}.
   */
  TipoHito findById(Long id);

  /**
   * Elimina el {@link TipoHito} por id.
   *
   * @param id el id de la entidad {@link TipoHito}.
   * @return la entidad {@link TipoHito} actualizadas.
   */
  TipoHito disable(Long id) throws TipoHitoNotFoundException;

}