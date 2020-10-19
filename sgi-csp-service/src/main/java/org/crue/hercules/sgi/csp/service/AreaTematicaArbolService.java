package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.AreaTematicaArbol;
import org.crue.hercules.sgi.csp.model.ListadoAreaTematica;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link AreaTematicaArbol}.
 */
public interface AreaTematicaArbolService {

  /**
   * Guardar un nuevo {@link AreaTematicaArbol}.
   *
   * @param areaTematicaArbol la entidad {@link AreaTematicaArbol} a guardar.
   * @return la entidad {@link AreaTematicaArbol} persistida.
   */
  AreaTematicaArbol create(AreaTematicaArbol areaTematicaArbol);

  /**
   * Actualizar {@link AreaTematicaArbol} y si se pone activo a false hace lo
   * mismo con todos sus hijos en cascada.
   *
   * @param areaTematicaArbolActualizar la entidad {@link AreaTematicaArbol} a
   *                                    actualizar.
   * @return la entidad {@link AreaTematicaArbol} persistida.
   */
  AreaTematicaArbol update(AreaTematicaArbol areaTematicaArbolActualizar);

  /**
   * Desactiva el {@link AreaTematicaArbol} y todos sus hijos en cascada.
   *
   * @param id Id del {@link AreaTematicaArbol}.
   * @return la entidad {@link AreaTematicaArbol} persistida.
   */
  AreaTematicaArbol disable(Long id);

  /**
   * Obtiene {@link AreaTematicaArbol} por su id.
   *
   * @param id el id de la entidad {@link AreaTematicaArbol}.
   * @return la entidad {@link AreaTematicaArbol}.
   */
  AreaTematicaArbol findById(Long id);

  /**
   * Obtiene los {@link AreaTematicaArbol} activos para un
   * {@link ListadoAreaTematica}.
   *
   * @param idListadoAreaTematica el id de la entidad {@link ListadoAreaTematica}.
   * @param query                 la información del filtro.
   * @param pageable              la información de la paginación.
   * @return la lista de entidades {@link AreaTematicaArbol} del
   *         {@link ListadoAreaTematica} paginadas.
   */
  Page<AreaTematicaArbol> findAllByListadoAreaTematica(Long idListadoAreaTematica, List<QueryCriteria> query,
      Pageable pageable);

  /**
   * Obtiene los {@link AreaTematicaArbol} para un {@link ListadoAreaTematica}.
   *
   * @param idListadoAreaTematica el id de la entidad {@link ListadoAreaTematica}.
   * @param query                 la información del filtro.
   * @param pageable              la información de la paginación.
   * @return la lista de entidades {@link AreaTematicaArbol} del
   *         {@link ListadoAreaTematica} paginadas.
   */
  Page<AreaTematicaArbol> findAllTodosByListadoAreaTematica(Long idListadoAreaTematica, List<QueryCriteria> query,
      Pageable pageable);

}