package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoPago;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProyectoPeriodoPagoRepository
    extends JpaRepository<SolicitudProyectoPeriodoPago, Long>, JpaSpecificationExecutor<SolicitudProyectoPeriodoPago> {

  /**
   * Recupera todos los {@link SolicitudProyectoPeriodoPago} asociados a un
   * {@link SolicitudProyectoSocio}.
   * 
   * @param solicitudProyectoSocioId Identificador de
   *                                 {@link SolicitudProyectoSocio}
   * @return listado de {@link SolicitudProyectoPeriodoPago}
   */
  List<SolicitudProyectoPeriodoPago> findAllBySolicitudProyectoSocioId(Long solicitudProyectoSocioId);

}
