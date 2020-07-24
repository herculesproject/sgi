package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoEvaluacion}.
 */

@Repository
public interface TipoEvaluacionRepository
    extends JpaRepository<TipoEvaluacion, Long>, JpaSpecificationExecutor<TipoEvaluacion> {

}