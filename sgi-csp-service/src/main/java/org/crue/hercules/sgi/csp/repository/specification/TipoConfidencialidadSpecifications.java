package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.TipoConfidencialidad;
import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * TipoConfidencialidadSpecifications
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TipoConfidencialidadSpecifications {

  /**
   * {@link TipoConfidencialidad} activos.
   *
   * @return specification para obtener los {@link TipoConfidencialidad} activos.
   */
  public static Specification<TipoConfidencialidad> activos() {
    return (root, query, cb) -> cb.isTrue(root.get(Activable_.activo));
  }

}
