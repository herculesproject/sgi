package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionUnidadVinculacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionUnidadVinculacion_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Specifications para {@link PeticionEvaluacionUnidadVinculacion}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PeticionEvaluacionUnidadVinculacionSpecifications {

  /**
   * {@link PeticionEvaluacionUnidadVinculacion} de la {@link PeticionEvaluacion}
   * con el id indicado.
   *
   * @param id identificador de la {@link PeticionEvaluacion}.
   * @return specification para obtener las
   *         {@link PeticionEvaluacionUnidadVinculacion} de la
   *         {@link PeticionEvaluacion} con el id indicado.
   */
  public static Specification<PeticionEvaluacionUnidadVinculacion> byPeticionEvaluacionId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(PeticionEvaluacionUnidadVinculacion_.peticionEvaluacionId), id);
  }

}
