package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion_;
import org.crue.hercules.sgi.csp.model.SocioPeriodoJustificacionDocumento;
import org.crue.hercules.sgi.csp.model.SocioPeriodoJustificacionDocumento_;
import org.springframework.data.jpa.domain.Specification;

public class SocioPeriodoJustificacionDocumentoSpecifications {

  /**
   * {@link SocioPeriodoJustificacionDocumento} de la
   * {@link ProyectoSocioPeriodoJustificacion} con el id indicado.
   * 
   * @param id identificador de la {@link ProyectoSocioPeriodoJustificacion}.
   * @return specification para obtener los
   *         {@link SocioPeriodoJustificacionDocumento} de la
   *         {@link ProyectoSocioPeriodoJustificacion} con el id indicado.
   */
  public static Specification<SocioPeriodoJustificacionDocumento> byProyectoSocioPeriodoJustificacionId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(SocioPeriodoJustificacionDocumento_.proyectoSocioPeriodoJustificacion)
          .get(ProyectoSocioPeriodoJustificacion_.id), id);
    };
  }

}
