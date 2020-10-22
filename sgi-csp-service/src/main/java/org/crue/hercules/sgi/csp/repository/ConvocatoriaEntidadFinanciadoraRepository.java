package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ConvocatoriaEntidadFinanciadora}.
 */
@Repository
public interface ConvocatoriaEntidadFinanciadoraRepository extends JpaRepository<ConvocatoriaEntidadFinanciadora, Long>,
    JpaSpecificationExecutor<ConvocatoriaEntidadFinanciadora> {

}
