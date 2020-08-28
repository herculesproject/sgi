package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluacion_;
import org.springframework.data.jpa.domain.Specification;

public class EvaluacionSpecifications {

  public static Specification<Evaluacion> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Evaluacion_.activo), Boolean.TRUE);
    };
  }

  public static Specification<Evaluacion> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Evaluacion_.activo), Boolean.FALSE);
    };
  }
}
