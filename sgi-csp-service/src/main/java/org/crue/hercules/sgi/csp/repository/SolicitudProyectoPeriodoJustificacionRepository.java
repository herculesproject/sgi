package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProyectoPeriodoJustificacionRepository
    extends JpaRepository<SolicitudProyectoPeriodoJustificacion, Long>,
    JpaSpecificationExecutor<SolicitudProyectoPeriodoJustificacion> {

  /**
   * Recupera el listado de solicitud proyecto periodo justificación asociados a
   * una solicitud proyecto socio id.
   * 
   * @param idSolicitudProyectoSocio Identificador de un
   *                                 {@link SolicitudProyectoSocio}
   * @return listado de {@link SolicitudProyectoPeriodoJustificacion}
   */
  List<SolicitudProyectoPeriodoJustificacion> findAllBySolicitudProyectoSocioId(Long idSolicitudProyectoSocio);

  /**
   * Se eliminan todos los {@link SolicitudProyectoPeriodoJustificacion}
   * asociadosal id de {@link SolicitudProyectoSocio} recibido por parámetro.
   * 
   * @param solicitudProyectoSocioId Identificador de
   *                                 {@link SolicitudProyectoSocio}
   */
  void deleteBySolicitudProyectoSocioId(Long solicitudProyectoSocioId);

}
