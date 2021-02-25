package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link SolicitudProyectoEquipo}.
 */

public interface SolicitudProyectoEquipoService {

  /**
   * Guarda la entidad {@link SolicitudProyectoEquipo}.
   * 
   * @param solicitudProyectoEquipo la entidad {@link SolicitudProyectoEquipo} a
   *                                guardar.
   * @return SolicitudProyectoEquipo la entidad {@link SolicitudProyectoEquipo}
   *         persistida.
   */
  SolicitudProyectoEquipo create(SolicitudProyectoEquipo solicitudProyectoEquipo);

  /**
   * Actualiza los datos del {@link SolicitudProyectoEquipo}.
   * 
   * @param solicitudProyectoEquipo {@link SolicitudProyectoEquipo} con los datos
   *                                actualizados.
   * @return SolicitudProyectoEquipo {@link SolicitudProyectoEquipo} actualizado.
   */
  SolicitudProyectoEquipo update(final SolicitudProyectoEquipo solicitudProyectoEquipo);

  /**
   * Comprueba la existencia del {@link SolicitudProyectoEquipo} por id.
   *
   * @param id el id de la entidad {@link SolicitudProyectoEquipo}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene una entidad {@link SolicitudProyectoEquipo} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyectoEquipo}.
   * @return SolicitudProyectoEquipo la entidad {@link SolicitudProyectoEquipo}.
   */
  SolicitudProyectoEquipo findById(final Long id);

  /**
   * Elimina el {@link SolicitudProyectoEquipo}.
   *
   * @param id Id del {@link SolicitudProyectoEquipo}.
   */
  void delete(Long id);

  /**
   * Obtiene las {@link SolicitudProyectoEquipo} para una {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @param query       la información del filtro.
   * @param pageable    la información de la paginación.
   * @return la lista de entidades {@link SolicitudProyectoEquipo} de la
   *         {@link Solicitud} paginadas.
   */
  Page<SolicitudProyectoEquipo> findAllBySolicitud(Long solicitudId, String query, Pageable pageable);

}