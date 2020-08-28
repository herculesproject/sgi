package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.FormularioMemoria;
import org.crue.hercules.sgi.eti.model.FormularioMemoria_;
import org.springframework.data.jpa.domain.Specification;

public class FormularioMemoriaSpecifications {

  public static Specification<FormularioMemoria> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(FormularioMemoria_.activo), Boolean.TRUE);
    };
  }

  public static Specification<FormularioMemoria> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(FormularioMemoria_.activo), Boolean.FALSE);
    };
  }
}
