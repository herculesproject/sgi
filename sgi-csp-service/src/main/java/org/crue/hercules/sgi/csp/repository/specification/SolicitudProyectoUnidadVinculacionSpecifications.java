package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoUnidadVinculacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoUnidadVinculacion_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Specifications para {@link SolicitudProyectoUnidadVinculacion}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SolicitudProyectoUnidadVinculacionSpecifications {

  /**
   * {@link SolicitudProyectoUnidadVinculacion} de la entidad
   * {@link SolicitudProyecto} con el id indicado.
   *
   * @param id identificador del {@link SolicitudProyecto}.
   * @return specification para obtener los
   *         {@link SolicitudProyectoUnidadVinculacion} del
   *         {@link SolicitudProyecto} con el id indicado.
   */
  public static Specification<SolicitudProyectoUnidadVinculacion> bySolicitudProyectoId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(SolicitudProyectoUnidadVinculacion_.solicitudProyectoId), id);
  }

}
