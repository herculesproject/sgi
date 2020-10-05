package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion_;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace_;
import org.springframework.data.jpa.domain.Specification;

public class ModeloTipoEnlaceSpecifications {

  /**
   * {@link ModeloTipoEnlace} activos.
   * 
   * @return specification para obtener los {@link ModeloTipoEnlace} activos.
   */
  public static Specification<ModeloTipoEnlace> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloTipoEnlace_.activo), Boolean.TRUE);
    };
  }

  /**
   * {@link ModeloTipoEnlace} del {@link ModeloEjecucion} con el id indicado.
   * 
   * @param id identificador del {@link ModeloEjecucion}.
   * @return specification para obtener los {@link ModeloTipoEnlace} del
   *         {@link ModeloEjecucion} con el id indicado.
   */
  public static Specification<ModeloTipoEnlace> byModeloEjecucionId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloTipoEnlace_.modeloEjecucion).get(ModeloEjecucion_.id), id);
    };
  }

}