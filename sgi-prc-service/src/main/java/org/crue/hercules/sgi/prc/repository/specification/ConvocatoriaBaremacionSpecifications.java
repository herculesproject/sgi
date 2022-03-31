package org.crue.hercules.sgi.prc.repository.specification;

import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion_;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaBaremacionSpecifications {

  /**
   * {@link ConvocatoriaBaremacion} activos.
   * 
   * @return specification para obtener los {@link ConvocatoriaBaremacion}
   *         activos.
   */
  public static Specification<ConvocatoriaBaremacion> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaBaremacion_.activo), Boolean.TRUE);
    };
  }
}
