package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link PeticionEvaluacion}.
 */

@Repository
public interface PeticionEvaluacionRepository
    extends JpaRepository<PeticionEvaluacion, Long>, JpaSpecificationExecutor<PeticionEvaluacion> {

}