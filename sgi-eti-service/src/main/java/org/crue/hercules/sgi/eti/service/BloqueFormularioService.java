package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.BloqueFormulario;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.BloqueFormularioNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link BloqueFormulario}.
 */
public interface BloqueFormularioService {
  /**
   * Guardar {@link BloqueFormulario}.
   *
   * @param bloqueFormulario la entidad {@link BloqueFormulario} a guardar.
   * @return la entidad {@link BloqueFormulario} persistida.
   */
  BloqueFormulario create(BloqueFormulario bloqueFormulario);

  /**
   * Actualizar {@link BloqueFormulario}.
   *
   * @param bloqueFormulario la entidad {@link BloqueFormulario} a actualizar.
   * @return la entidad {@link BloqueFormulario} persistida.
   */
  BloqueFormulario update(BloqueFormulario bloqueFormulario);

  /**
   * Obtener todas las entidades {@link BloqueFormulario} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link BloqueFormulario} paginadas y/o
   *         filtradas.
   */
  Page<BloqueFormulario> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link BloqueFormulario} por id.
   *
   * @param id el id de la entidad {@link BloqueFormulario}.
   * @return la entidad {@link BloqueFormulario}.
   */
  BloqueFormulario findById(Long id);

  /**
   * Elimina el {@link BloqueFormulario} por id.
   *
   * @param id el id de la entidad {@link BloqueFormulario}.
   */
  void delete(Long id) throws BloqueFormularioNotFoundException;

  /**
   * Elimina todos los {@link BloqueFormulario}.
   */
  void deleteAll();

}