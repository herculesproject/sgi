package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumento_;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
    return (root, query, cb) -> cb.equal(
        root.get(ProyectoPeriodoSeguimientoDocumento_.proyectoPeriodoSeguimiento).get(ProyectoPeriodoSeguimiento_.id),
        id);

  }

  /**
   * {@link ProyectoPeriodoSeguimientoDocumento} de la {@link Proyecto} con el id
   * indicado.
   * 
   * @param id identificador de la {@link Proyecto}.
   * @return specification para obtener los
   *         {@link ProyectoPeriodoSeguimientoDocumento} de la {@link Proyecto}
   *         con el id indicado.
   */
  public static Specification<ProyectoPeriodoSeguimientoDocumento> byProyectoId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(ProyectoPeriodoSeguimientoDocumento_.proyectoPeriodoSeguimiento)
        .get(ProyectoPeriodoSeguimiento_.proyecto).get(Proyecto_.id), id);
  }

  /**
   * {@link ProyectoPeriodoSeguimientoDocumento} visibles.
   *
   * @return specification para obtener los
   *         {@link ProyectoPeriodoSeguimientoDocumento} visibles.
   */
  public static Specification<ProyectoPeriodoSeguimientoDocumento> onlyVisibles() {
    return (root, query, cb) -> cb.isTrue(root.get(ProyectoPeriodoSeguimientoDocumento_.visible));

  }

}
