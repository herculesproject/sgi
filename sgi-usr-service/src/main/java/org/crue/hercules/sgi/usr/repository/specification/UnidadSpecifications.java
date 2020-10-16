package org.crue.hercules.sgi.usr.repository.specification;

import org.crue.hercules.sgi.usr.model.Unidad;
import org.crue.hercules.sgi.usr.model.Unidad_;
import org.springframework.data.jpa.domain.Specification;

public class UnidadSpecifications {
  /**
   * {@link Unidad} activos.
   * 
   * @return specification para obtener los {@link Unidad} activos.
   */
  public static Specification<Unidad> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Unidad_.activo), Boolean.TRUE);
    };
  }

}