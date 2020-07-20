package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.RespuestaFormulario;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.RespuestaFormularioNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link RespuestaFormulario}.
 */
public interface RespuestaFormularioService {
  /**
   * Guardar {@link RespuestaFormulario}.
   *
   * @param respuestaFormulario la entidad {@link RespuestaFormulario} a guardar.
   * @return la entidad {@link RespuestaFormulario} persistida.
   */
  RespuestaFormulario create(RespuestaFormulario respuestaFormulario);

  /**
   * Actualizar {@link RespuestaFormulario}.
   *
   * @param respuestaFormulario la entidad {@link RespuestaFormulario} a
   *                            actualizar.
   * @return la entidad {@link RespuestaFormulario} persistida.
   */
  RespuestaFormulario update(RespuestaFormulario respuestaFormulario);

  /**
   * Obtener todas las entidades {@link RespuestaFormulario} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link RespuestaFormulario} paginadas y/o
   *         filtradas.
   */
  Page<RespuestaFormulario> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link RespuestaFormulario} por id.
   *
   * @param id el id de la entidad {@link RespuestaFormulario}.
   * @return la entidad {@link RespuestaFormulario}.
   */
  RespuestaFormulario findById(Long id);

  /**
   * Elimina el {@link RespuestaFormulario} por id.
   *
   * @param id el id de la entidad {@link RespuestaFormulario}.
   */
  void delete(Long id) throws RespuestaFormularioNotFoundException;

  /**
   * Elimina todos los {@link RespuestaFormulario}.
   */
  void deleteAll();

}