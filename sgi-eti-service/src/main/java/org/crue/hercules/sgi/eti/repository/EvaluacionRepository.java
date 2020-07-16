package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Evaluacion}.
 */
@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long>, JpaSpecificationExecutor<Evaluacion> {

}