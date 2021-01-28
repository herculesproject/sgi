package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos_;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudProyectoDatosSpecifications {

  /**
   * {@link SolicitudProyectoDatos} del {@link Solicitud} con el id indicado.
   * 
   * @param id identificador de la {@link Solicitud}.
   * @return specification para obtener los {@link SolicitudProyectoDatos} de la
   *         {@link Solicitud} con el id indicado.
   */
  public static Specification<SolicitudProyectoDatos> bySolicitudId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(SolicitudProyectoDatos_.solicitud).get(Solicitud_.id), id);
    };
  }

  /**
   * {@link SolicitudProyectoDatos} con presupuestoPorEntidades true.
   * 
   * @return specification para obtener los {@link SolicitudProyectoDatos} con
   *         presupuestoPorEntidades true.
   */
  public static Specification<SolicitudProyectoDatos> presupuestoPorEntidades() {
    return (root, query, cb) -> {
      return cb.isTrue(root.get(SolicitudProyectoDatos_.presupuestoPorEntidades));
    };
  }

}
