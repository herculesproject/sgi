package org.crue.hercules.sgi.eti.repository;

import java.util.List;

import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionUnidadVinculacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para {@link PeticionEvaluacionUnidadVinculacion}.
 */
@Repository
public interface PeticionEvaluacionUnidadVinculacionRepository
    extends JpaRepository<PeticionEvaluacionUnidadVinculacion, Long>,
    JpaSpecificationExecutor<PeticionEvaluacionUnidadVinculacion> {

  /**
   * Devuelve las {@link PeticionEvaluacionUnidadVinculacion} de la
   * {@link PeticionEvaluacion} con el id indicado.
   *
   * @param peticionEvaluacionId id de la {@link PeticionEvaluacion}.
   * @return listado de {@link PeticionEvaluacionUnidadVinculacion}.
   */
  List<PeticionEvaluacionUnidadVinculacion> findAllByPeticionEvaluacionId(Long peticionEvaluacionId);

}
