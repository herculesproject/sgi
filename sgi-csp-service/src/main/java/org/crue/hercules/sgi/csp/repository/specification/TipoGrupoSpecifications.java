package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.TipoGrupo;
import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TipoGrupoSpecifications {

  /**
   * {@link TipoGrupo} activos.
   * 
   * @return specification para obtener los {@link TipoGrupo} activos.
   */
  public static Specification<TipoGrupo> activos() {
    return (root, query, cb) -> cb.isTrue(root.get(Activable_.activo));
  }

}
