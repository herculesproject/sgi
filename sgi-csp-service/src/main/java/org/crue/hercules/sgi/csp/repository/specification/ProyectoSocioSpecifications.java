package org.crue.hercules.sgi.csp.repository.specification;

import java.time.LocalDate;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocio_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.model.RolSocio_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoSocioSpecifications {

  /**
   * {@link ProyectoSocio} con rol que tenga el campo coordinador a true.
   * 
   * @return specification para obtener los {@link ProyectoSocio} coordinadores.
   */
  public static Specification<ProyectoSocio> sociosCoordinadores() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoSocio_.rolSocio).get(RolSocio_.coordinador), Boolean.TRUE);
    };
  }

  /**
   * {@link ProyectoSocio} del proyecto
   * 
   * @param proyectoId el id del {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoSocio} del proyecto.
   */
  public static Specification<ProyectoSocio> byProyecto(Long proyectoId) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoSocio_.proyecto).get(Proyecto_.id), proyectoId);
    };
  }

  /**
   * {@link ProyectoSocio} del empresaRef
   * 
   * @param empresaRef empresaRef.
   * @return specification para obtener los {@link ProyectoSocio} del empresaRef.
   */
  public static Specification<ProyectoSocio> byEmpresaRef(String empresaRef) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoSocio_.empresaRef), empresaRef);
    };
  }

  /**
   * {@link ConvocatoriaFase} de la {@link Convocatoria} con fechas solapadas
   * 
   * @param fechaInicio fecha inicio de {@link ConvocatoriaFase}
   * @param fechaFin    fecha fin de la {@link ConvocatoriaFase}.
   * @return specification para obtener los {@link ConvocatoriaFase} con rango de
   *         fechas solapadas
   */
  public static Specification<ProyectoSocio> byRangoFechaSolapados(LocalDate fechaInicio, LocalDate fechaFin) {
    return (root, query, cb) -> {
      return cb.and(
          cb.or(cb.isNull(root.get(ProyectoSocio_.fechaInicio)),
              cb.lessThanOrEqualTo(root.get(ProyectoSocio_.fechaInicio),
                  fechaFin != null ? fechaFin : LocalDate.of(2500, 1, 1))),
          cb.or(cb.isNull(root.get(ProyectoSocio_.fechaFin)), cb.greaterThanOrEqualTo(root.get(ProyectoSocio_.fechaFin),
              fechaInicio != null ? fechaInicio : LocalDate.of(1900, 1, 1))));
    };
  }

  /**
   * {@link ProyectoSocio} id diferente de {@link ProyectoSocio} con el indicado.
   * 
   * @param id identificador de la {@link ProyectoSocio}.
   * @return specification para comprobar si el id es diferentes del
   *         {@link ConvocatoriaFase} indicado.
   */
  public static Specification<ProyectoSocio> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      if (id == null) {
        return cb.isTrue(cb.literal(true)); // always true = no filtering
      }
      return cb.equal(root.get(ProyectoSocio_.id), id).not();
    };
  }

}
