package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoDocumento_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProyectoDocumentoSpecifications {

  /**
   * {@link ProyectoDocumento} de la {@link Proyecto} con el id indicado.
   * 
   * @param id identificador de la {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoDocumento} de la
   *         {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoDocumento> byProyectoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ProyectoDocumento_.proyecto).get(Proyecto_.id), id);
    };
  }

  /**
   * {@link ProyectoDocumento} visibles.
   *
   * @return specification para obtener los {@link ProyectoDocumento}
   *         visibles.
   */
  public static Specification<ProyectoDocumento> onlyVisibles() {
    return (root, query, cb) -> cb.isTrue(root.get(ProyectoDocumento_.visible));
  }

}
