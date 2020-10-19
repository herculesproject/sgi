package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.ListadoAreaTematica;
import org.crue.hercules.sgi.csp.model.ListadoAreaTematica_;
import org.springframework.data.jpa.domain.Specification;

public class ListadoAreaTematicaSpecifications {

  /**
   * {@link ListadoAreaTematica} activos.
   * 
   * @return specification para obtener los {@link ListadoAreaTematica} activos.
   */
  public static Specification<ListadoAreaTematica> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ListadoAreaTematica_.activo), Boolean.TRUE);
    };
  }

}