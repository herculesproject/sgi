package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link SolicitudProyectoPresupuesto}.
 */

public interface SolicitudProyectoPresupuestoService {

  /**
   * Guarda la entidad {@link SolicitudProyectoPresupuesto}.
   * 
   * @param solicitudProyectoPresupuesto la entidad
   *                                     {@link SolicitudProyectoPresupuesto} a
   *                                     guardar.
   * @return SolicitudProyectoPresupuesto la entidad
   *         {@link SolicitudProyectoPresupuesto} persistida.
   */
  SolicitudProyectoPresupuesto create(SolicitudProyectoPresupuesto solicitudProyectoPresupuesto);

  /**
   * Actualiza los datos del {@link SolicitudProyectoPresupuesto}.
   * 
   * @param solicitudProyectoPresupuesto {@link SolicitudProyectoPresupuesto} con
   *                                     los datos actualizados.
   * 
   * @return SolicitudProyectoPresupuesto {@link SolicitudProyectoPresupuesto}
   *         actualizado.
   */
  SolicitudProyectoPresupuesto update(final SolicitudProyectoPresupuesto solicitudProyectoPresupuesto);

  /**
   * Comprueba la existencia del {@link SolicitudProyectoPresupuesto} por id.
   *
   * @param id el id de la entidad {@link SolicitudProyectoPresupuesto}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene una entidad {@link SolicitudProyectoPresupuesto} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyectoPresupuesto}.
   * @return SolicitudProyectoPresupuesto la entidad
   *         {@link SolicitudProyectoPresupuesto}.
   */
  SolicitudProyectoPresupuesto findById(final Long id);

  /**
   * Elimina el {@link SolicitudProyectoPresupuesto}.
   *
   * @param id Id del {@link SolicitudProyectoPresupuesto}.
   */
  void delete(Long id);

  /**
   * Recupera la lista paginada de {@link SolicitudProyectoPresupuesto} de una
   * {@link Solicitud}.
   * 
   * @param solicitudId Identificador de la {@link Solicitud}.
   * @param query       parámentros de búsqueda.
   * @param paging      parámetros de paginación.
   * @return lista paginada.
   */
  Page<SolicitudProyectoPresupuesto> findAllBySolicitud(Long solicitudId, List<QueryCriteria> query, Pageable paging);

}