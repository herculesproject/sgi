package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.ApartadoFormulario;
import org.crue.hercules.sgi.eti.model.ApartadoFormulario_;
import org.springframework.data.jpa.domain.Specification;

public class ApartadoFormularioSpecifications {

  public static Specification<ApartadoFormulario> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ApartadoFormulario_.activo), Boolean.TRUE);
    };
  }

  public static Specification<ApartadoFormulario> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ApartadoFormulario_.activo), Boolean.FALSE);
    };
  }
}
