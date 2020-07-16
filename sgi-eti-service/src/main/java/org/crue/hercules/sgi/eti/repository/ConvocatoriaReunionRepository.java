package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ConvocatoriaReunion}.
 */

@Repository
public interface ConvocatoriaReunionRepository
    extends JpaRepository<ConvocatoriaReunion, Long>, JpaSpecificationExecutor<ConvocatoriaReunion> {

}