package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.repository.custom.CustomSolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProyectoSocioRepository extends JpaRepository<SolicitudProyectoSocio, Long>,
    JpaSpecificationExecutor<SolicitudProyectoSocio>, CustomSolicitudProyectoSocioRepository {

  /**
   * Recupera los {@link SolicitudProyectoSocio} asociados a un
   * {@link SolicitudProyectoDatos} cuya rol socio sea coordinador.
   * 
   * @param idSolicitudProyectoDatos Id {@link SolicitudProyectoDatos}
   * @return listado {@link SolicitudProyectoSocio}
   */
  List<SolicitudProyectoSocio> findAllBySolicitudProyectoDatosIdAndRolSocioCoordinadorTrue(
      Long idSolicitudProyectoDatos);

  /**
   * Obtiene los {@link SolicitudProyectoSocio} asociados a una {@link Solicitud}
   * 
   * @param solicitudProyectoDatosId Identificador de la {@link Solicitud}
   * @return Listado de solicitudes modalidad
   */
  List<SolicitudProyectoSocio> findAllBySolicitudProyectoDatosId(Long solicitudProyectoDatosId);

}
