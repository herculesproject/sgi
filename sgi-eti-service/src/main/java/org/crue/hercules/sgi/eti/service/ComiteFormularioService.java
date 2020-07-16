package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.exceptions.ComiteFormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.ComiteFormulario;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ComiteFormulario}.
 */
public interface ComiteFormularioService {
  /**
   * Guardar {@link ComiteFormulario}.
   *
   * @param comiteFormulario la entidad {@link ComiteFormulario} a guardar.
   * @return la entidad {@link ComiteFormulario} persistida.
   */
  ComiteFormulario create(ComiteFormulario comiteFormulario);

  /**
   * Actualizar {@link ComiteFormulario}.
   *
   * @param comiteFormulario la entidad {@link ComiteFormulario} a actualizar.
   * @return la entidad {@link ComiteFormulario} persistida.
   */
  ComiteFormulario update(ComiteFormulario comiteFormulario);

  /**
   * Obtener todas las entidades {@link ComiteFormulario} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ComiteFormulario} paginadas y/o
   *         filtradas.
   */
  Page<ComiteFormulario> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link ComiteFormulario} por id.
   *
   * @param id el id de la entidad {@link ComiteFormulario}.
   * @return la entidad {@link ComiteFormulario}.
   */
  ComiteFormulario findById(Long id);

  /**
   * Elimina el {@link ComiteFormulario} por id.
   *
   * @param id el id de la entidad {@link ComiteFormulario}.
   */
  void deleteById(Long id) throws ComiteFormularioNotFoundException;

  /**
   * Elimina todos los {@link ComiteFormulario}.
   */
  void deleteAll();

}