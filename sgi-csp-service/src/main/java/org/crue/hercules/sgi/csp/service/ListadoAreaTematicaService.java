package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ListadoAreaTematica;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ListadoAreaTematica}.
 */

public interface ListadoAreaTematicaService {

  /**
   * Guarda la entidad {@link ListadoAreaTematica}.
   * 
   * @param listadoAreaTematica la entidad {@link ListadoAreaTematica} a guardar.
   * @return ListadoAreaTematica la entidad {@link ListadoAreaTematica} persistida.
   */
  ListadoAreaTematica create(ListadoAreaTematica listadoAreaTematica);

  /**
   * Actualiza los datos del {@link ListadoAreaTematica}.
   * 
   * @param listadoAreaTematicaActualizar {@link ListadoAreaTematica} con los datos
   *                                actualizados.
   * @return ListadoAreaTematica {@link ListadoAreaTematica} actualizado.
   */
  ListadoAreaTematica update(final ListadoAreaTematica listadoAreaTematicaActualizar);

  /**
   * Desactiva el {@link ListadoAreaTematica}.
   *
   * @param id Id del {@link ListadoAreaTematica}.
   * @return la entidad {@link ListadoAreaTematica} persistida.
   */
  ListadoAreaTematica disable(Long id);

  /**
   * Obtiene todas las entidades {@link ListadoAreaTematica} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link ListadoAreaTematica} paginadas y filtradas.
   */
  Page<ListadoAreaTematica> findAll(List<QueryCriteria> query, Pageable paging);

  /**
   * Obtiene una entidad {@link ListadoAreaTematica} por id.
   * 
   * @param id Identificador de la entidad {@link ListadoAreaTematica}.
   * @return ListadoAreaTematica la entidad {@link ListadoAreaTematica}.
   */
  ListadoAreaTematica findById(final Long id);

}
