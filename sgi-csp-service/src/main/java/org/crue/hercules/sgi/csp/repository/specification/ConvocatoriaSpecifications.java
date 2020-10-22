package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaSpecifications {

  /**
   * {@link Convocatoria} con Activo a True
   * 
   * @return specification para obtener las {@link Convocatoria} activas
   */
  public static Specification<Convocatoria> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Convocatoria_.activo), Boolean.TRUE);
    };
  }

}
