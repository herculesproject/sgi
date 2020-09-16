package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link EquipoTrabajo}.
 */
@Repository
public interface EquipoTrabajoRepository
    extends JpaRepository<EquipoTrabajo, Long>, JpaSpecificationExecutor<EquipoTrabajo> {

  /**
   * Obtener todas las entidades paginadas {@link EquipoTrabajo} activas para una
   * determinada {@link PeticionEvaluacion}.
   *
   * @param id       Id de {@link PeticionEvaluacion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link EquipoTrabajo} paginadas.
   */
  Page<EquipoTrabajo> findAllByPeticionEvaluacionId(Long id, Pageable pageable);

}