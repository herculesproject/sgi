package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoDescriptor;
import org.crue.hercules.sgi.csp.model.GrupoDescriptor_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * GrupoDescriptorSpecifications
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GrupoDescriptorSpecifications {

  /**
   * {@link GrupoDescriptor} por {@link Grupo} id.
   *
   * @param grupoId identificador del {@link Grupo}.
   * @return specification para filtrar por grupoId.
   */
  public static Specification<GrupoDescriptor> byGrupoId(Long grupoId) {
    return (root, query, cb) -> cb.equal(root.get(GrupoDescriptor_.grupoId), grupoId);
  }

}
