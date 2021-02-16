package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
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

  /**
   * {@link SolicitudProyectoEquipoSocio} cuyo id no es el recibido.
   * 
   * @param id identificador de la {@link SolicitudProyectoEquipoSocio}.
   * @return specification para obtener los {@link SolicitudProyectoEquipoSocio}
   *         cuyo id no sea el recibido
   */
  public static Specification<SolicitudProyectoEquipoSocio> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      return cb.notEqual(root.get(SolicitudProyectoEquipoSocio_.id), id);
    };
  }

  /**
   * Comprueba si el mes inicio se encuentra {@link SolicitudProyectoEquipo}
   * dentro del rango de mese inicio de otro
   * 
   * @param mesInicio mes inicio de {@link SolicitudProyectoEquipo}.
   * @return specification para obtener los {@link SolicitudProyectoEquipo} cuyo
   *         mes inicio se encuentra en el rango de otro
   */
  public static Specification<SolicitudProyectoEquipoSocio> inRangoMesInicio(Integer mesInicio) {
    return (root, query, cb) -> {

      return cb.or(
          cb.and(cb.lessThanOrEqualTo(root.get(SolicitudProyectoEquipoSocio_.mesInicio), mesInicio),
              cb.greaterThanOrEqualTo(root.get(SolicitudProyectoEquipoSocio_.mesFin), mesInicio)),
          cb.isNull(root.get(SolicitudProyectoEquipoSocio_.mesInicio)));
    };
  }

  /**
   * Comprueba si el mes fin se encuentra {@link SolicitudProyectoEquipoSocio}
   * dentro del rango de mese fin de otro
   * 
   * @param mesFin mes fin de {@link SolicitudProyectoEquipoSocio}.
   * @return specification para obtener los {@link SolicitudProyectoEquipoSocio}
   *         cuyo mes fin se encuentra en el rango de otro
   */
  public static Specification<SolicitudProyectoEquipoSocio> inRangoMesFin(Integer mesFin) {
    return (root, query, cb) -> {

      return cb.or(
          cb.and(cb.lessThanOrEqualTo(root.get(SolicitudProyectoEquipoSocio_.mesInicio), mesFin),
              cb.greaterThanOrEqualTo(root.get(SolicitudProyectoEquipoSocio_.mesFin), mesFin)),
          cb.isNull(root.get(SolicitudProyectoEquipoSocio_.mesFin)));
    };
  }

  /**
   * {@link SolicitudProyectoEquipoSocio} cuyo personaRef de
   * 
   * @param personaRef personaRef de la {@link SolicitudProyectoEquipoSocio}.
   * @return specification para obtener los {@link SolicitudProyectoEquipoSocio}
   *         cuyo solicitante ref es el recibido.
   */
  public static Specification<SolicitudProyectoEquipoSocio> bySolicitanteRef(String personaRef) {
    return (root, query, cb) -> {

      return cb.equal(root.get(SolicitudProyectoEquipoSocio_.personaRef), personaRef);
    };
  }
}
