package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion_;
import org.crue.hercules.sgi.csp.model.ModeloTipoFaseDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoFaseDocumento_;
import org.springframework.data.jpa.domain.Specification;

public class ModeloTipoFaseDocumentoSpecifications {

  /**
   * {@link ModeloTipoFaseDocumento} activos.
   * 
   * @return specification para obtener los {@link ModeloTipoFaseDocumento}
   *         activos.
   */
  public static Specification<ModeloTipoFaseDocumento> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloTipoFaseDocumento_.activo), Boolean.TRUE);
    };
  }

  /**
   * {@link ModeloTipoFaseDocumento} del {@link ModeloEjecucion} con el id
   * indicado.
   * 
   * @param id identificador del {@link ModeloEjecucion}.
   * @return specification para obtener los {@link ModeloTipoFaseDocumento} del
   *         {@link ModeloEjecucion} con el id indicado.
   */
  public static Specification<ModeloTipoFaseDocumento> byModeloEjecucionId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloTipoFaseDocumento_.modeloEjecucion).get(ModeloEjecucion_.id), id);
    };
  }

}