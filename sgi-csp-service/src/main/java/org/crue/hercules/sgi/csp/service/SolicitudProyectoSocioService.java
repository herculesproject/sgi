package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link SolicitudProyectoSocio}.
 */

public interface SolicitudProyectoSocioService {

  /**
   * Guarda la entidad {@link SolicitudProyectoSocio}.
   * 
   * @param solicitudProyectoSocio la entidad {@link SolicitudProyectoSocio} a
   *                               guardar.
   * @return SolicitudProyectoSocio la entidad {@link SolicitudProyectoSocio}
   *         persistida.
   */
  SolicitudProyectoSocio create(SolicitudProyectoSocio solicitudProyectoSocio);

  /**
   * Actualiza los datos del {@link SolicitudProyectoSocio}.
   * 
   * @param solicitudProyectoSocio {@link SolicitudProyectoSocio} con los datos
   *                               actualizados.
   * 
   * @return SolicitudProyectoSocio {@link SolicitudProyectoSocio} actualizado.
   */
  SolicitudProyectoSocio update(final SolicitudProyectoSocio solicitudProyectoSocio);

  /**
   * Comprueba la existencia del {@link SolicitudProyectoSocio} por id.
   *
   * @param id el id de la entidad {@link SolicitudProyectoSocio}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene una entidad {@link SolicitudProyectoSocio} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyectoSocio}.
   * @return SolicitudProyectoSocio la entidad {@link SolicitudProyectoSocio}.
   */
  SolicitudProyectoSocio findById(final Long id);

  /**
   * Elimina el {@link SolicitudProyectoSocio}.
   *
   * @param id Id del {@link SolicitudProyectoSocio}.
   */
  void delete(Long id);

  /**
   * Recupera la lista paginada de socios colaborativos de una {@link Solicitud}.
   * 
   * @param idSolicitud Identificador de la {@link Solicitud}.
   * @param query       parámentros de búsqueda.
   * @param paging      parámetros de paginación.
   * @return lista paginada.
   */
  Page<SolicitudProyectoSocio> findAllBySolicitud(Long idSolicitud, List<QueryCriteria> query, Pageable paging);

}