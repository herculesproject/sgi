package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento_;
import org.springframework.data.jpa.domain.Specification;

public class TipoDocumentoSpecifications {

  /**
   * {@link TipoDocumento} activos.
   * 
   * @return specification para obtener los {@link TipoDocumento} activos.
   */
  public static Specification<TipoDocumento> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TipoDocumento_.activo), Boolean.TRUE);
    };
  }

}