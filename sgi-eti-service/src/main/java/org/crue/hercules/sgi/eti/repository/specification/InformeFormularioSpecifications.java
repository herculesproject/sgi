package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.FormularioMemoria_;
import org.crue.hercules.sgi.eti.model.InformeFormulario;
import org.crue.hercules.sgi.eti.model.InformeFormulario_;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.springframework.data.jpa.domain.Specification;

public class InformeFormularioSpecifications {

  public static Specification<InformeFormulario> byMemoriaOrderByVersionDesc(Long idMemoria) {
    return (root, query, cb) -> {
      return cb.equal(root.get(InformeFormulario_.formularioMemoria).get(FormularioMemoria_.memoria).get(Memoria_.id),
          idMemoria);
    };
  }

}
