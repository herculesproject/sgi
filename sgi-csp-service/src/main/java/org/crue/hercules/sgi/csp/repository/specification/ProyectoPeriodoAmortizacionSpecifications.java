package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad_;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoAmortizacion;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoAmortizacion_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProyectoPeriodoAmortizacionSpecifications {

  /**
   * {@link ProyectoPeriodoAmortizacion} del {@link Proyecto} con el id
   * indicado.
   * 
   * @param id identificador del {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoPeriodoAmortizacion}
   *         de la {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoPeriodoAmortizacion> byProyectoId(Long id) {
    return (root, query, cb) -> cb
        .equal(root.get(ProyectoPeriodoAmortizacion_.proyectoAnualidad).get(ProyectoAnualidad_.proyectoId), id);
  }

}
