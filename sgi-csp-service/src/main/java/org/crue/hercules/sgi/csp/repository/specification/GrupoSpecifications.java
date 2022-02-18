package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.springframework.data.jpa.domain.Specification;

public class GrupoSpecifications {

  private GrupoSpecifications() {
  }

  /**
   * {@link Grupo} activos.
   * 
   * @return specification para obtener los {@link Grupo} activos.
   */
  public static Specification<Grupo> activos() {
    return (root, query, cb) -> {
      return cb.isTrue(root.get(Activable_.activo));
    };
  }

}
