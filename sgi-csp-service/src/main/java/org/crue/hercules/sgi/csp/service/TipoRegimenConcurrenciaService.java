package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoRegimenConcurrencia}.
 */

public interface TipoRegimenConcurrenciaService {

  /**
   * Guarda la entidad {@link TipoRegimenConcurrencia}.
   * 
   * @param tipoRegimenConcurrencia la entidad {@link TipoRegimenConcurrencia} a
   *                                guardar.
   * @return TipoRegimenConcurrencia la entidad {@link TipoRegimenConcurrencia}
   *         persistida.
   */
  TipoRegimenConcurrencia create(TipoRegimenConcurrencia tipoRegimenConcurrencia);

  /**
   * Actualiza los datos del {@link TipoRegimenConcurrencia}.
   * 
   * @param tipoRegimenConcurrenciaActualizar {@link TipoRegimenConcurrencia} con
   *                                          los datos actualizados.
   * @return TipoRegimenConcurrencia {@link TipoRegimenConcurrencia} actualizado.
   */
  TipoRegimenConcurrencia update(final TipoRegimenConcurrencia tipoRegimenConcurrenciaActualizar);

  /**
   * Desactiva el {@link TipoRegimenConcurrencia}.
   *
   * @param id Id del {@link TipoRegimenConcurrencia}.
   * @return la entidad {@link TipoRegimenConcurrencia} persistida.
   */
  TipoRegimenConcurrencia disable(Long id);

  /**
   * Obtiene todas las entidades {@link TipoRegimenConcurrencia} activos paginadas
   * y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link TipoRegimenConcurrencia} paginadas y
   *         filtradas.
   */
  Page<TipoRegimenConcurrencia> findAll(String query, Pageable paging);

  /**
   * Obtiene todas las entidades {@link TipoRegimenConcurrencia} paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link TipoRegimenConcurrencia} paginadas y
   *         filtradas.
   */
  Page<TipoRegimenConcurrencia> findAllTodos(String query, Pageable paging);

  /**
   * Obtiene una entidad {@link TipoRegimenConcurrencia} por id.
   * 
   * @param id Identificador de la entidad {@link TipoRegimenConcurrencia}.
   * @return TipoRegimenConcurrencia la entidad {@link TipoRegimenConcurrencia}.
   */
  TipoRegimenConcurrencia findById(final Long id);

}
