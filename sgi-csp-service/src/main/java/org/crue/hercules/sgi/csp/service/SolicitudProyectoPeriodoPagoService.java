package org.crue.hercules.sgi.csp.service;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoPago;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link SolicitudProyectoPeriodoPago}.
 */

public interface SolicitudProyectoPeriodoPagoService {

  /**
   * Comprueba la existencia del {@link SolicitudProyectoPeriodoPago} por id.
   *
   * @param id el id de la entidad {@link SolicitudProyectoPeriodoPago}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene una entidad {@link SolicitudProyectoPeriodoPago} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyectoPeriodoPago}.
   * @return SolicitudProyectoPeriodoPago la entidad
   *         {@link SolicitudProyectoPeriodoPago}.
   */
  SolicitudProyectoPeriodoPago findById(final Long id);

  /**
   * Recupera la lista paginada de socios colaborativos de una
   * {@link SolicitudProyectoSocio}.
   * 
   * @param idSolicitudProyectoProyectoSocio Identificador de la
   *                                         {@link SolicitudProyectoSocio}.
   * @param query                            parámentros de búsqueda.
   * @param paging                           parámetros de paginación.
   * @return lista paginada.
   */
  Page<SolicitudProyectoPeriodoPago> findAllBySolicitudProyectoSocio(Long idSolicitudProyectoProyectoSocio,
      String query, Pageable paging);

  /**
   * Actualiza el listado de {@link SolicitudProyectoPeriodoPago} de la
   * {@link SolicitudProyectoSocio} con el listado solicitudPeriodoPagos
   * añadiendo, editando o eliminando los elementos segun proceda.
   *
   * @param solicitudProyectoSocioId Id de la {@link SolicitudProyectoSocio}.
   * @param solicitudPeriodoPagos    lista con los nuevos
   *                                 {@link SolicitudProyectoPeriodoPago} a
   *                                 guardar.
   * @return la entidad {@link SolicitudProyectoPeriodoPago} persistida.
   */
  List<SolicitudProyectoPeriodoPago> update(Long solicitudProyectoSocioId,
      @Valid List<SolicitudProyectoPeriodoPago> solicitudPeriodoPagos);

}