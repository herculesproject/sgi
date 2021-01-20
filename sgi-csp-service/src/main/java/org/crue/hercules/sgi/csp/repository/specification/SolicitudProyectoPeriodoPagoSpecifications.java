package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoPago;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoPago_;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudProyectoPeriodoPagoSpecifications {
  /**
   * {@link SolicitudProyectoPeriodoPago} del {@link SolicitudProyectoSocio} con
   * el id indicado.
   * 
   * @param id identificador de la {@link SolicitudProyectoSocio}.
   * @return specification para obtener los {@link SolicitudProyectoPeriodoPago}
   *         de la {@link SolicitudProyectoSocio} con el id indicado.
   */
  public static Specification<SolicitudProyectoPeriodoPago> bySolicitudProyectoSocioId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(SolicitudProyectoPeriodoPago_.solicitudProyectoSocio).get(SolicitudProyectoSocio_.id),
          id);
    };
  }

}
