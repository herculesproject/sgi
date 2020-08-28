package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Formulario_;
import org.springframework.data.jpa.domain.Specification;

public class FormularioSpecifications {

  public static Specification<Formulario> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Formulario_.activo), Boolean.TRUE);
    };
  }

  public static Specification<Formulario> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Formulario_.activo), Boolean.FALSE);
    };
  }
}
