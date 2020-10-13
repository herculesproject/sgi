package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Plan;
import org.crue.hercules.sgi.csp.model.Plan_;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Programa_;
import org.springframework.data.jpa.domain.Specification;

public class ProgramaSpecifications {

  /**
   * {@link Programa} del {@link Plan} con el id indicado.
   * 
   * @param id identificador del {@link Plan}.
   * @return specification para obtener los {@link Programa} del {@link Plan} con
   *         el id indicado.
   */
  public static Specification<Programa> byPlanId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(Programa_.plan).get(Plan_.id), id);
    };
  }

}