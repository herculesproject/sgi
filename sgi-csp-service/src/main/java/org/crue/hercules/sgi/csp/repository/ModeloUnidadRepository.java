package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ModeloUnidad}.
 */
@Repository
public interface ModeloUnidadRepository
    extends JpaRepository<ModeloUnidad, Long>, JpaSpecificationExecutor<ModeloUnidad> {

}