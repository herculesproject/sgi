package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProyectoEquipoRepository
    extends JpaRepository<SolicitudProyectoEquipo, Long>, JpaSpecificationExecutor<SolicitudProyectoEquipo> {

  /**
   * Obtiene las {@link SolicitudProyectoEquipo} asociadas a una {@link Solicitud}
   * 
   * @param solicitudProyectoDatosId Identificador de la {@link Solicitud}
   * @return Listado de solicitudes modalidad
   */
  List<SolicitudProyectoEquipo> findAllBySolicitudProyectoDatosId(Long solicitudProyectoDatosId);

}
