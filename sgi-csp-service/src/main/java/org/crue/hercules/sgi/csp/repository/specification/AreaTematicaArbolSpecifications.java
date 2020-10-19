package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.AreaTematicaArbol;
import org.crue.hercules.sgi.csp.model.AreaTematicaArbol_;
import org.crue.hercules.sgi.csp.model.ListadoAreaTematica;
import org.crue.hercules.sgi.csp.model.ListadoAreaTematica_;
import org.springframework.data.jpa.domain.Specification;

public class AreaTematicaArbolSpecifications {

  /**
   * {@link AreaTematicaArbol} activos.
   * 
   * @return specification para obtener los {@link AreaTematicaArbol} activos.
   */
  public static Specification<AreaTematicaArbol> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(AreaTematicaArbol_.activo), Boolean.TRUE);
    };
  }

  /**
   * {@link AreaTematicaArbol} del {@link ListadoAreaTematica} con el id indicado.
   * 
   * @param id identificador del {@link ListadoAreaTematica}.
   * @return specification para obtener los {@link AreaTematicaArbol} del
   *         {@link ListadoAreaTematica} con el id indicado.
   */
  public static Specification<AreaTematicaArbol> byListadoAreaTematicaId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(AreaTematicaArbol_.listadoAreaTematica).get(ListadoAreaTematica_.id), id);
    };
  }

}