package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ModeloTipoFinalidad}.
 */
@Repository
public interface ModeloTipoFinalidadRepository
    extends JpaRepository<ModeloTipoFinalidad, Long>, JpaSpecificationExecutor<ModeloTipoFinalidad> {

}