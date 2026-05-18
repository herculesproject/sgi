package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.TipoDescriptorGrupo;
import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * TipoDescriptorGrupoSpecifications
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TipoDescriptorGrupoSpecifications {

  /**
   * {@link TipoDescriptorGrupo} activos.
   *
   * @return specification para obtener los {@link TipoDescriptorGrupo} activos.
   */
  public static Specification<TipoDescriptorGrupo> activos() {
    return (root, query, cb) -> cb.isTrue(root.get(Activable_.activo));
  }

}
