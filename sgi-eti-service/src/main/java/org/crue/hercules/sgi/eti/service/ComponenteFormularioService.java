package org.crue.hercules.sgi.eti.service;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.ComponenteFormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.ComponenteFormulario;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ComponenteFormulario}.
 */
public interface ComponenteFormularioService {

  /**
   * Crea {@link ComponenteFormulario}.
   *
   * @param componenteFormulario La entidad {@link ComponenteFormulario} a crear.
   * @return La entidad {@link ComponenteFormulario} creada.
   * @throws IllegalArgumentException Si la entidad {@link ComponenteFormulario}
   *                                  tiene id.
   */
  ComponenteFormulario create(ComponenteFormulario componenteFormulario) throws IllegalArgumentException;

  /**
   * Actualiza {@link ComponenteFormulario}.
   *
   * @param componenteFormularioActualizar La entidad {@link ComponenteFormulario}
   *                                       a actualizar.
   * @return La entidad {@link ComponenteFormulario} actualizada.
   * @throws ComponenteFormularioNotFoundException Si no existe ninguna entidad
   *                                               {@link ComponenteFormulario}
   *                                               con ese id.
   * @throws IllegalArgumentException              Si la entidad
   *                                               {@link ComponenteFormulario} no
   *                                               tiene id.
   */
  ComponenteFormulario update(ComponenteFormulario componenteFormularioActualizar)
      throws ComponenteFormularioNotFoundException, IllegalArgumentException;

  /**
   * Elimina todas las entidades {@link ComponenteFormulario}.
   *
   */
  void deleteAll();

  /**
   * Elimina {@link ComponenteFormulario} por id.
   *
   * @param id El id de la entidad {@link ComponenteFormulario}.
   * @throws ComponenteFormularioNotFoundException Si no existe ninguna entidad
   *                                               {@link ComponenteFormulario}
   *                                               con ese id.
   * @throws IllegalArgumentException              Si no se informa Id.
   */
  void delete(Long id) throws ComponenteFormularioNotFoundException, IllegalArgumentException;

  /**
   * Obtiene las entidades {@link ComponenteFormulario} filtradas y paginadas
   * según los criterios de búsqueda.
   *
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   * @return el listado de entidades {@link ComponenteFormulario} paginadas y
   *         filtradas.
   */
  Page<ComponenteFormulario> findAll(List<QueryCriteria> query, Pageable paging);

  /**
   * Obtiene {@link ComponenteFormulario} por id.
   *
   * @param id El id de la entidad {@link ComponenteFormulario}.
   * @return La entidad {@link ComponenteFormulario}.
   * @throws ComponenteFormularioNotFoundException Si no existe ninguna entidad
   *                                               {@link ComponenteFormulario}
   *                                               con ese id.
   * @throws IllegalArgumentException              Si no se informa Id.
   */
  ComponenteFormulario findById(Long id) throws ComponenteFormularioNotFoundException, IllegalArgumentException;

}
