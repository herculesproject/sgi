package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar
 * {@link SolicitudProyectoPeriodoJustificacion}.
 */

public interface SolicitudProyectoPeriodoJustificacionService {

  /**
   * Actualiza los datos del {@link SolicitudProyectoPeriodoJustificacion}.
   * 
   * @param solicitudProyectoSocioId        Id de la
   *                                        {@link SolicitudProyectoSocio}.
   * @param solicitudPeriodoJustificaciones lista con los nuevos
   *                                        {@link SolicitudProyectoPeriodoJustificacion}
   *                                        a guardar.
   * @return Listado de SolicitudProyectoPeriodoJustificacion
   *         {@link SolicitudProyectoPeriodoJustificacion} actualizado.
   */
  List<SolicitudProyectoPeriodoJustificacion> update(Long solicitudProyectoSocioId,
      List<SolicitudProyectoPeriodoJustificacion> solicitudPeriodoJustificaciones);

  /**
   * Comprueba la existencia del {@link SolicitudProyectoPeriodoJustificacion} por
   * id.
   *
   * @param id el id de la entidad {@link SolicitudProyectoPeriodoJustificacion}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene una entidad {@link SolicitudProyectoPeriodoJustificacion} por id.
   * 
   * @param id Identificador de la entidad
   *           {@link SolicitudProyectoPeriodoJustificacion}.
   * @return SolicitudProyectoPeriodoJustificacion la entidad
   *         {@link SolicitudProyectoPeriodoJustificacion}.
   */
  SolicitudProyectoPeriodoJustificacion findById(final Long id);

  /**
   * Elimina el {@link SolicitudProyectoPeriodoJustificacion}.
   *
   * @param id Id del {@link SolicitudProyectoPeriodoJustificacion}.
   */
  void delete(Long id);

  /**
   * Obtiene la {@link SolicitudProyectoPeriodoJustificacion} para una
   * {@link SolicitudProyectoSocio}.
   *
   * @param solicitudProyectoSocioId el id de la {@link SolicitudProyectoSocio}.
   * @param query                    parámentros de búsqueda.
   * @param paging                   parámetros de paginación.
   * @return la lista de entidades {@link SolicitudProyectoPeriodoJustificacion}
   *         de la {@link SolicitudProyectoSocio} paginadas.
   */
  Page<SolicitudProyectoPeriodoJustificacion> findAllBySolicitudProyectoSocio(Long solicitudProyectoSocioId,
      String query, Pageable paging);

}