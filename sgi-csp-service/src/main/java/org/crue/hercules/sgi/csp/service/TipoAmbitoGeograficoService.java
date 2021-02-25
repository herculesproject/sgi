package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoAmbitoGeografico}.
 */
public interface TipoAmbitoGeograficoService {

  /**
   * Guardar un nuevo {@link TipoAmbitoGeografico}.
   *
   * @param tipoAmbitoGeografico la entidad {@link TipoAmbitoGeografico} a
   *                             guardar.
   * @return la entidad {@link TipoAmbitoGeografico} persistida.
   */
  TipoAmbitoGeografico create(TipoAmbitoGeografico tipoAmbitoGeografico);

  /**
   * Actualizar {@link TipoAmbitoGeografico}.
   *
   * @param tipoAmbitoGeograficoActualizar la entidad {@link TipoAmbitoGeografico}
   *                                       a actualizar.
   * @return la entidad {@link TipoAmbitoGeografico} persistida.
   */
  TipoAmbitoGeografico update(TipoAmbitoGeografico tipoAmbitoGeograficoActualizar);

  /**
   * Desactiva el {@link TipoAmbitoGeografico}.
   *
   * @param id Id del {@link TipoAmbitoGeografico}.
   * @return la entidad {@link TipoAmbitoGeografico} persistida.
   */
  TipoAmbitoGeografico disable(Long id);

  /**
   * Obtener todas las entidades {@link TipoAmbitoGeografico} activos paginadas
   * y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoAmbitoGeografico} paginadas y/o
   *         filtradas.
   */
  Page<TipoAmbitoGeografico> findAll(String query, Pageable pageable);

  /**
   * Obtener todas las entidades {@link TipoAmbitoGeografico} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoAmbitoGeografico} paginadas y/o
   *         filtradas.
   */
  Page<TipoAmbitoGeografico> findAllTodos(String query, Pageable pageable);

  /**
   * Obtiene {@link TipoAmbitoGeografico} por su id.
   *
   * @param id el id de la entidad {@link TipoAmbitoGeografico}.
   * @return la entidad {@link TipoAmbitoGeografico}.
   */
  TipoAmbitoGeografico findById(Long id);

}