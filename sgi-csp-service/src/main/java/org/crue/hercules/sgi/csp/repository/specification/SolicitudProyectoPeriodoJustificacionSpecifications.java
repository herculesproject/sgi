package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoJustificacion_;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudProyectoPeriodoJustificacionSpecifications {
  /**
   * {@link SolicitudProyectoPeriodoJustificacion} del
   * {@link SolicitudProyectoSocio} con el id indicado.
   * 
   * @param id identificador del {@link SolicitudProyectoSocio}.
   * @return specification para obtener los
   *         {@link SolicitudProyectoPeriodoJustificacion} de la
   *         {@link SolicitudProyectoSocio} con el id indicado.
   */
  public static Specification<SolicitudProyectoPeriodoJustificacion> bySolicitudId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(
          root.get(SolicitudProyectoPeriodoJustificacion_.solicitudProyectoSocio).get(SolicitudProyectoSocio_.id), id);
    };
  }

}
