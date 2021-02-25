package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link SolicitudProyectoEquipoSocio}.
 */

public interface SolicitudProyectoEquipoSocioService {

  /**
   * Actualiza los datos del {@link SolicitudProyectoEquipoSocio}.
   * 
   * @param solicitudProyectoSocioId      Id de la {@link SolicitudProyectoSocio}.
   * @param solicitudProyectoEquipoSocios lista con los nuevos
   *                                      {@link SolicitudProyectoEquipoSocio} a
   *                                      guardar.
   * @return SolicitudProyectoEquipoSocio {@link SolicitudProyectoEquipoSocio}
   *         actualizado.
   */
  List<SolicitudProyectoEquipoSocio> update(Long solicitudProyectoSocioId,
      List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocios);

  /**
   * Comprueba la existencia del {@link SolicitudProyectoEquipoSocio} por id.
   *
   * @param id el id de la entidad {@link SolicitudProyectoEquipoSocio}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene una entidad {@link SolicitudProyectoEquipoSocio} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyectoEquipoSocio}.
   * @return SolicitudProyectoEquipoSocio la entidad
   *         {@link SolicitudProyectoEquipoSocio}.
   */
  SolicitudProyectoEquipoSocio findById(final Long id);

  /**
   * Recupera la lista paginada de equipos socio de una
   * {@link SolicitudProyectoSocio}.
   * 
   * @param idSolicitudProyectoSocio Identificador de la
   *                                 {@link SolicitudProyectoSocio}.
   * @param query                    parámentros de búsqueda.
   * @param paging                   parámetros de paginación.
   * @return lista paginada.
   */
  Page<SolicitudProyectoEquipoSocio> findAllBySolicitudProyectoSocio(Long idSolicitudProyectoSocio, String query,
      Pageable paging);

}