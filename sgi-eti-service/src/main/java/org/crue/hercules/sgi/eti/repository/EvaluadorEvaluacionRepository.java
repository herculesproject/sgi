package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.EvaluadorEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link EvaluadorEvaluacion}.
 */
@Repository
public interface EvaluadorEvaluacionRepository
    extends JpaRepository<EvaluadorEvaluacion, Long>, JpaSpecificationExecutor<EvaluadorEvaluacion> {

}