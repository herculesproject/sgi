package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ModeloTipoHito}.
 */
@Repository
public interface ModeloTipoHitoRepository
    extends JpaRepository<ModeloTipoHito, Long>, JpaSpecificationExecutor<ModeloTipoHito> {

}