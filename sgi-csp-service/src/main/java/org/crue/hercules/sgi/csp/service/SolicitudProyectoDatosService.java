package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;

/**
 * Service Interface para gestionar {@link SolicitudProyectoDatos}.
 */

public interface SolicitudProyectoDatosService {

  /**
   * Guarda la entidad {@link SolicitudProyectoDatos}.
   * 
   * @param solicitudProyectoDatos la entidad {@link SolicitudProyectoDatos} a
   *                               guardar.
   * @return SolicitudProyectoDatos la entidad {@link SolicitudProyectoDatos}
   *         persistida.
   */
  SolicitudProyectoDatos create(SolicitudProyectoDatos solicitudProyectoDatos);

  /**
   * Actualiza los datos del {@link SolicitudProyectoDatos}.
   * 
   * @param solicitudProyectoDatos {@link SolicitudProyectoDatos} con los datos
   *                               actualizados.
   * @return SolicitudProyectoDatos {@link SolicitudProyectoDatos} actualizado.
   */
  SolicitudProyectoDatos update(final SolicitudProyectoDatos solicitudProyectoDatos);

  /**
   * Comprueba la existencia del {@link SolicitudProyectoDatos} por id.
   *
   * @param id el id de la entidad {@link SolicitudProyectoDatos}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene una entidad {@link SolicitudProyectoDatos} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyectoDatos}.
   * @return SolicitudProyectoDatos la entidad {@link SolicitudProyectoDatos}.
   */
  SolicitudProyectoDatos findById(final Long id);

  /**
   * Elimina el {@link SolicitudProyectoDatos}.
   *
   * @param id Id del {@link SolicitudProyectoDatos}.
   */
  void delete(Long id);

  /**
   * Obtiene la {@link SolicitudProyectoDatos} para una {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @return la lista de entidades {@link SolicitudProyectoDatos} de la
   *         {@link Solicitud} paginadas.
   */
  SolicitudProyectoDatos findBySolicitud(Long solicitudId);

  /**
   * Comprueba si existe una solicitud proyecto datos
   * 
   * @param id Identificador de la {@link Solicitud}
   * @return Indicador de si existe o no solicitud datos proyecto.
   */
  boolean existsBySolicitudId(Long id);

  /**
   * Comprueba si tiene presupuesto por entidades.
   * 
   * @param solicitudId Identificador de la {@link Solicitud}
   * @return Indicador de si tiene o no presupuesto por entidades.
   */
  boolean hasPresupuestoPorEntidades(Long solicitudId);

}