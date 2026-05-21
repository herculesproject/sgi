package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoRelacionInstitucional;
import org.crue.hercules.sgi.csp.model.GrupoRelacionInstitucional_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * GrupoRelacionInstitucionalSpecifications
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GrupoRelacionInstitucionalSpecifications {

  /**
   * {@link GrupoRelacionInstitucional} del {@link Grupo} con el id indicado.
   *
   * @param grupoId identificador del {@link Grupo}.
   * @return specification para obtener los {@link GrupoRelacionInstitucional}
   *         del {@link Grupo} con el id indicado.
   */
  public static Specification<GrupoRelacionInstitucional> byGrupoId(Long grupoId) {
    return (root, query, cb) -> cb.equal(root.get(GrupoRelacionInstitucional_.grupoId), grupoId);
  }

}
