package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

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

  /**
   * {@link Convocatoria} unidadGestionRef.
   * 
   * @return specification para obtener los {@link Convocatoria} cuyo
   *         unidadGestionRef se encuentre entre los recibidos.
   */
  public static Specification<Convocatoria> acronimosIn(List<String> acronimos) {
    return (root, query, cb) -> {
      return root.get(Convocatoria_.unidadGestionRef).in(acronimos);
    };
  }

}
