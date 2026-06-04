package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoUnidadVinculacion;
import org.crue.hercules.sgi.csp.model.ProyectoUnidadVinculacion_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Specifications para {@link ProyectoUnidadVinculacion}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProyectoUnidadVinculacionSpecifications {

  /**
   * {@link ProyectoUnidadVinculacion} del {@link Proyecto} con el id indicado.
   *
   * @param id identificador del {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoUnidadVinculacion} del
   *         {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoUnidadVinculacion> byProyectoId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(ProyectoUnidadVinculacion_.proyectoId), id);
  }

}
