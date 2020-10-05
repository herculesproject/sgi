package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ModeloTipoEnlace}.
 */
@Repository
public interface ModeloTipoEnlaceRepository
    extends JpaRepository<ModeloTipoEnlace, Long>, JpaSpecificationExecutor<ModeloTipoEnlace> {

}