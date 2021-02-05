package org.crue.hercules.sgi.csp.repository.custom;

import java.util.List;

import org.crue.hercules.sgi.csp.dto.SolicitudProyectoPresupuestoTotalConceptoGasto;
import org.crue.hercules.sgi.csp.dto.SolicitudProyectoPresupuestoTotales;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link Proyecto}.
 */
@Component
public interface CustomSolicitudProyectoPresupuestoRepository {

  /**
   * Obtiene el {@link SolicitudProyectoPresupuestoTotales} de la
   * {@link Solicitud}.
   * 
   * @param solicitudId Id de la {@link Solicitud}.
   * @return {@link SolicitudProyectoPresupuestoTotales}.
   */
  SolicitudProyectoPresupuestoTotales getTotales(Long solicitudId);

  /**
   * Obtiene los {@link SolicitudProyectoPresupuestoTotalConceptoGasto} de la
   * {@link Solicitud}.
   * 
   * @param solicitudId Id de la {@link Solicitud}.
   * @return lista de {@link SolicitudProyectoPresupuestoTotalConceptoGasto}.
   */
  List<SolicitudProyectoPresupuestoTotalConceptoGasto> getSolicitudProyectoPresupuestoTotalConceptoGastos(
      Long solicitudId);

}
