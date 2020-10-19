package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConvocatoriaEnlaceRepository
    extends JpaRepository<ConvocatoriaEnlace, Long>, JpaSpecificationExecutor<ConvocatoriaEnlace> {

}
