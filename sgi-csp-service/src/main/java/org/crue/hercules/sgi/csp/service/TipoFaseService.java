package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.exceptions.TipoFaseNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoFase}.
 */
public interface TipoFaseService {

  /**
   * Guardar {@link TipoFase}.
   *
   * @param tipoFase la entidad {@link TipoFase} a guardar.
   * @return la entidad {@link TipoFase} persistida.
   */
  TipoFase create(TipoFase tipoFase);

  /**
   * Actualizar {@link TipoFase}.
   *
   * @param tipoFase la entidad {@link TipoFase} a actualizar.
   * @return la entidad {@link TipoFase} persistida.
   */
  TipoFase update(TipoFase tipoFase);

  /**
   * Obtener todas las entidades {@link TipoFase} activas paginadas y/o filtradas.
   * Obtener todas las entidades {@link TipoFase} paginadas y/o filtradas
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link TipoFase} paginadas y/o filtradas.
   */
  Page<TipoFase> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtener todas las entidades {@link TipoFase} paginadas y/o filtradas. Obtener
   * todas las entidades {@link TipoFase} paginadas y/o filtradas
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link TipoFase} paginadas y/o filtradas.
   */
  Page<TipoFase> findAllTodos(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link TipoFase} por id.
   *
   * @param id el id de la entidad {@link TipoFase}.
   * @return la entidad {@link TipoFase}.
   */
  TipoFase findById(Long id);

  /**
   * Desactiva el {@link TipoFase} por id.
   *
   * @param id el id de la entidad {@link TipoFase}.
   * @return la entidad {@link TipoFase} persistida.
   */
  TipoFase disable(Long id) throws TipoFaseNotFoundException;

}
