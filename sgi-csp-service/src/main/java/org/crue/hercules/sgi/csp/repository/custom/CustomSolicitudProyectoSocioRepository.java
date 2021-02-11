package org.crue.hercules.sgi.csp.repository.custom;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoPago;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipoSocio;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link SolicitudProyectoSocio}.
 */
@Component
public interface CustomSolicitudProyectoSocioRepository {

  /**
   * Indica si {@link SolicitudProyectoSocio} tiene
   * {@link SolicitudProyectoPeriodoJustificacion},
   * {@link SolicitudProyectoPeriodoPago} y/o {@link SolicitudProyectoEquipoSocio}
   * relacionadas.
   *
   * @param id Id de la {@link SolicitudProyectoSocio}.
   * @return True si tiene {@link SolicitudProyectoPeriodoJustificacion},
   *         {@link SolicitudProyectoPeriodoPago} y/o
   *         {@link SolicitudProyectoEquipoSocio} relacionadas. En caso contrario
   *         false
   */
  Boolean vinculaciones(Long id);

}
