package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoUnidadVinculacion;
import org.crue.hercules.sgi.csp.model.GrupoUnidadVinculacion_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GrupoUnidadVinculacionSpecifications {

  /**
   * {@link GrupoUnidadVinculacion} de la entidad {@link Grupo} con el id
   * indicado.
   *
   * @param id identificador del {@link Grupo}.
   * @return specification para obtener los {@link GrupoUnidadVinculacion} del
   *         {@link Grupo} con el id indicado.
   */
  public static Specification<GrupoUnidadVinculacion> byGrupoId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(GrupoUnidadVinculacion_.grupoId), id);
  }

}
