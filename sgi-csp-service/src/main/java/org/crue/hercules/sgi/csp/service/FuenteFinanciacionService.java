package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link FuenteFinanciacion}.
 */
public interface FuenteFinanciacionService {

  /**
   * Guardar un nuevo {@link FuenteFinanciacion}.
   *
   * @param fuenteFinanciacion la entidad {@link FuenteFinanciacion} a guardar.
   * @return la entidad {@link FuenteFinanciacion} persistida.
   */
  FuenteFinanciacion create(FuenteFinanciacion fuenteFinanciacion);

  /**
   * Actualizar {@link FuenteFinanciacion}.
   *
   * @param fuenteFinanciacionActualizar la entidad {@link FuenteFinanciacion} a
   *                                     actualizar.
   * @return la entidad {@link FuenteFinanciacion} persistida.
   */
  FuenteFinanciacion update(FuenteFinanciacion fuenteFinanciacionActualizar);

  /**
   * Reactiva el {@link FuenteFinanciacion}.
   *
   * @param id Id del {@link FuenteFinanciacion}.
   * @return la entidad {@link FuenteFinanciacion} persistida.
   */
  FuenteFinanciacion enable(Long id);

  /**
   * Desactiva el {@link FuenteFinanciacion}.
   *
   * @param id Id del {@link FuenteFinanciacion}.
   * @return la entidad {@link FuenteFinanciacion} persistida.
   */
  FuenteFinanciacion disable(Long id);

  /**
   * Obtener todas las entidades {@link FuenteFinanciacion} activos paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link FuenteFinanciacion} paginadas y/o
   *         filtradas.
   */
  Page<FuenteFinanciacion> findAll(String query, Pageable pageable);

  /**
   * Obtener todas las entidades {@link FuenteFinanciacion} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link FuenteFinanciacion} paginadas y/o
   *         filtradas.
   */
  Page<FuenteFinanciacion> findAllTodos(String query, Pageable pageable);

  /**
   * Obtiene {@link FuenteFinanciacion} por su id.
   *
   * @param id el id de la entidad {@link FuenteFinanciacion}.
   * @return la entidad {@link FuenteFinanciacion}.
   */
  FuenteFinanciacion findById(Long id);

}