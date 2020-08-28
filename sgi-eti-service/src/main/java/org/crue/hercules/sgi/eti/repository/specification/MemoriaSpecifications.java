package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.springframework.data.jpa.domain.Specification;

public class MemoriaSpecifications {

  public static Specification<Memoria> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Memoria_.activo), Boolean.TRUE);
    };
  }

  public static Specification<Memoria> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Memoria_.activo), Boolean.FALSE);
    };
  }
}
