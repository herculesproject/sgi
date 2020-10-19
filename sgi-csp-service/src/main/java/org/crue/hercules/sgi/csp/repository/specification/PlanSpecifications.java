package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Plan;
import org.crue.hercules.sgi.csp.model.Plan_;
import org.springframework.data.jpa.domain.Specification;

public class PlanSpecifications {

  /**
   * {@link Plan} activos.
   * 
   * @return specification para obtener los {@link Plan} activos.
   */
  public static Specification<Plan> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Plan_.activo), Boolean.TRUE);
    };
  }

}