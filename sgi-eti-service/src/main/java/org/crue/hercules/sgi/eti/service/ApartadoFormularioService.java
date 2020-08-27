package org.crue.hercules.sgi.eti.service;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.ApartadoFormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.ApartadoFormulario;
import org.crue.hercules.sgi.eti.model.BloqueFormulario;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ApartadoFormulario}.
 */
public interface ApartadoFormularioService {

  /**
   * Crea {@link ApartadoFormulario}.
   *
   * @param apartadoFormulario La entidad {@link ApartadoFormulario} a crear.
   * @return La entidad {@link ApartadoFormulario} creada.
   * @throws IllegalArgumentException Si la entidad {@link ApartadoFormulario}
   *                                  tiene id.
   */
  ApartadoFormulario create(ApartadoFormulario apartadoFormulario) throws IllegalArgumentException;

  /**
   * Actualiza {@link ApartadoFormulario}.
   *
   * @param apartadoFormularioActualizar La entidad {@link ApartadoFormulario} a
   *                                     actualizar.
   * @return La entidad {@link ApartadoFormulario} actualizada.
   * @throws ApartadoFormularioNotFoundException Si no existe ninguna entidad
   *                                             {@link ApartadoFormulario} con
   *                                             ese id.
   * @throws IllegalArgumentException            Si la entidad
   *                                             {@link ApartadoFormulario} no
   *                                             tiene id.
   */
  ApartadoFormulario update(ApartadoFormulario apartadoFormularioActualizar)
      throws ApartadoFormularioNotFoundException, IllegalArgumentException;

  /**
   * Elimina todas las entidades {@link ApartadoFormulario}.
   *
   */
  void deleteAll();

  /**
   * Elimina {@link ApartadoFormulario} por id.
   *
   * @param id El id de la entidad {@link ApartadoFormulario}.
   * @throws ApartadoFormularioNotFoundException Si no existe ninguna entidad
   *                                             {@link ApartadoFormulario} con
   *                                             ese id.
   * @throws IllegalArgumentException            Si no se informa Id.
   */
  void delete(Long id) throws ApartadoFormularioNotFoundException, IllegalArgumentException;

  /**
   * Obtiene las entidades {@link ApartadoFormulario} filtradas y paginadas según
   * los criterios de búsqueda.
   *
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   * @return el listado de entidades {@link ApartadoFormulario} paginadas y
   *         filtradas.
   */
  Page<ApartadoFormulario> findAll(List<QueryCriteria> query, Pageable paging);

  /**
   * Obtiene {@link ApartadoFormulario} por id.
   *
   * @param id El id de la entidad {@link ApartadoFormulario}.
   * @return La entidad {@link ApartadoFormulario}.
   * @throws ApartadoFormularioNotFoundException Si no existe ninguna entidad
   *                                             {@link ApartadoFormulario} con
   *                                             ese id.
   * @throws IllegalArgumentException            Si no se informa Id.
   */
  ApartadoFormulario findById(Long id) throws ApartadoFormularioNotFoundException, IllegalArgumentException;

  /**
   * Obtiene las entidades {@link ApartadoFormulario} filtradas y paginadas según
   * por el id de su {@link BloqueFormulario}.
   *
   * @param id       id del {@link BloqueFormulario}.
   * @param pageable pageable
   * @return el listado de entidades {@link ApartadoFormulario} paginadas y
   *         filtradas.
   */
  Page<ApartadoFormulario> findByBloqueFormularioId(Long id, Pageable pageable);

  /**
   * Obtiene las entidades {@link ApartadoFormulario} filtradas y paginadas según
   * por el id de su padre {@link ApartadoFormulario}.
   *
   * @param id       id del {@link ApartadoFormulario}.
   * @param pageable pageable
   * @return el listado de entidades {@link ApartadoFormulario} paginadas y
   *         filtradas.
   */
  Page<ApartadoFormulario> findByApartadoFormularioPadreId(Long id, Pageable pageable);
}
