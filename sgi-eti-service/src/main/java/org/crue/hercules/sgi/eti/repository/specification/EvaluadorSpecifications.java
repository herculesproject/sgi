package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Evaluador_;
import org.springframework.data.jpa.domain.Specification;

public class EvaluadorSpecifications {

  public static Specification<Evaluador> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Evaluador_.activo), Boolean.TRUE);
    };
  }

  public static Specification<Evaluador> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Evaluador_.activo), Boolean.FALSE);
    };
  }
}
