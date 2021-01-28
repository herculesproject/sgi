package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumento_;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoPeriodoSeguimientoDocumentoSpecifications {

  /**
   * {@link ProyectoPeriodoSeguimientoDocumento} de la
   * {@link ProyectoPeriodoSeguimiento} con el id indicado.
   * 
   * @param id identificador de la {@link ProyectoPeriodoSeguimiento}.
   * @return specification para obtener los
   *         {@link ProyectoPeriodoSeguimientoDocumento} de la
   *         {@link ProyectoPeriodoSeguimiento} con el id indicado.
   */
  public static Specification<ProyectoPeriodoSeguimientoDocumento> byProyectoPeriodoSeguimientoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(
          root.get(ProyectoPeriodoSeguimientoDocumento_.proyectoPeriodoSeguimiento).get(ProyectoPeriodoSeguimiento_.id),
          id);
    };
  }

}
