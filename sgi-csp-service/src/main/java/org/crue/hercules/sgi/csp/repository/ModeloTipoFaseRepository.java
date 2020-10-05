package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ModeloTipoFase}.
 */
@Repository
public interface ModeloTipoFaseRepository
    extends JpaRepository<ModeloTipoFase, Long>, JpaSpecificationExecutor<ModeloTipoFase> {

}