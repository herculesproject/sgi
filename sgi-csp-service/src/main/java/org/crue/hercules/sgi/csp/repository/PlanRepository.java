package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Plan}.
 */
@Repository
public interface PlanRepository extends JpaRepository<Plan, Long>, JpaSpecificationExecutor<Plan> {

  /**
   * Busca un {@link Plan} por su nombre.
   * 
   * @param nombre Nombre del {@link Plan}.
   * @return un {@link Plan} si tiene el nombre buscado.
   */
  Optional<Plan> findByNombre(String nombre);

}