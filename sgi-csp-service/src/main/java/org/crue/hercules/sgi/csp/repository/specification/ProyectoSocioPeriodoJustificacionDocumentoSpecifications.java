package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionDocumento_;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion_;
import org.crue.hercules.sgi.csp.model.ProyectoSocio_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProyectoSocioPeriodoJustificacionDocumentoSpecifications {

  /**
   * {@link ProyectoSocioPeriodoJustificacionDocumento} del
   * {@link ProyectoSocioPeriodoJustificacion} con el id indicado.
   * 
   * @param id identificador de la {@link ProyectoSocioPeriodoJustificacion}.
   * @return specification para obtener los
   *         {@link ProyectoSocioPeriodoJustificacionDocumento} del
   *         {@link ProyectoSocioPeriodoJustificacion} con el id indicado.
   */
  public static Specification<ProyectoSocioPeriodoJustificacionDocumento> byProyectoSocioPeriodoJustificacionId(
      Long id) {
    return (root, query, cb) -> cb
        .equal(root.get(ProyectoSocioPeriodoJustificacionDocumento_.proyectoSocioPeriodoJustificacion)
            .get(ProyectoSocioPeriodoJustificacion_.id), id);
  }

  /**
   * {@link ProyectoSocioPeriodoJustificacionDocumento} de la {@link Proyecto} con
   * el id indicado.
   * 
   * @param id identificador de la {@link Proyecto}.
   * @return specification para obtener los
   *         {@link ProyectoSocioPeriodoJustificacionDocumento} de la
   *         {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoSocioPeriodoJustificacionDocumento> byProyectoId(Long id) {
    return (root, query, cb) -> cb.equal(
        root.get(ProyectoSocioPeriodoJustificacionDocumento_.proyectoSocioPeriodoJustificacion)
            .get(ProyectoSocioPeriodoJustificacion_.proyectoSocio).get(ProyectoSocio_.proyecto).get(Proyecto_.id),
        id);
  }

  /**
   * {@link ProyectoSocioPeriodoJustificacionDocumento} visibles.
   *
   * @return specification para obtener los
   *         {@link ProyectoSocioPeriodoJustificacionDocumento} visibles.
   */
  public static Specification<ProyectoSocioPeriodoJustificacionDocumento> onlyVisibles() {
    return (root, query, cb) -> cb.isTrue(root.get(ProyectoSocioPeriodoJustificacionDocumento_.visible));

  }

}
