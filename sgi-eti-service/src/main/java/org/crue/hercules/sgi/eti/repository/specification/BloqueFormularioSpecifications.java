package org.crue.hercules.sgi.eti.repository.specification;

import java.util.List;

import org.crue.hercules.sgi.eti.model.BloqueFormulario;
import org.crue.hercules.sgi.eti.model.BloqueFormulario_;
import org.crue.hercules.sgi.eti.model.Formulario_;
import org.springframework.data.jpa.domain.Specification;

public class BloqueFormularioSpecifications {

  public static Specification<BloqueFormulario> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(BloqueFormulario_.activo), Boolean.TRUE);
    };
  }

  public static Specification<BloqueFormulario> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(BloqueFormulario_.activo), Boolean.FALSE);
    };
  }

  public static Specification<BloqueFormulario> formularioIdsIn(List<Long> formulariosIds) {
    return (root, query, cb) -> {
      return (root.get(BloqueFormulario_.formulario).get(Formulario_.id)).in(formulariosIds);
    };
  }
}
