package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConvocatoriaFaseRepository
    extends JpaRepository<ConvocatoriaFase, Long>, JpaSpecificationExecutor<ConvocatoriaFase> {

}
