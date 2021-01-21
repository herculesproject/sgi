package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProyectoEquipoSocioRepository
    extends JpaRepository<SolicitudProyectoEquipoSocio, Long>, JpaSpecificationExecutor<SolicitudProyectoEquipoSocio> {

  /**
   * Recupera los {@link SolicitudProyectoEquipoSocio} asociados al id de
   * {@link SolicitudProyectoSocio} recibido por parámetro
   * 
   * @param solicitudProyectoSocioId Identificador de
   *                                 {@link SolicitudProyectoSocio}
   * @return listado {@link SolicitudProyectoEquipoSocio}
   */
  List<SolicitudProyectoEquipoSocio> findAllBySolicitudProyectoSocioId(Long solicitudProyectoSocioId);

  /**
   * Se eliminan todos los {@link SolicitudProyectoEquipoSocio} asociadosal id de
   * {@link SolicitudProyectoSocio} recibido por parámetro.
   * 
   * @param solicitudProyectoSocioId Identificador de
   *                                 {@link SolicitudProyectoSocio}
   */
  void deleteBySolicitudProyectoSocioId(Long solicitudProyectoSocioId);

}
