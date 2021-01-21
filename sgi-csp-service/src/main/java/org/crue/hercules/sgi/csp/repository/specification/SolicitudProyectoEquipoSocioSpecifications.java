package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipoSocio_;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudProyectoEquipoSocioSpecifications {
  /**
   * {@link SolicitudProyectoEquipoSocio} del {@link Solicitud} con el id
   * indicado.
   * 
   * @param id identificador de la {@link Solicitud}.
   * @return specification para obtener los {@link SolicitudProyectoEquipoSocio}
   *         de la {@link SolicitudProyectoDatos} con el id indicado.
   */
  public static Specification<SolicitudProyectoEquipoSocio> bySolicitudProyectoSocio(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(SolicitudProyectoEquipoSocio_.solicitudProyectoSocio).get(SolicitudProyectoSocio_.id),
          id);
    };
  }

}
