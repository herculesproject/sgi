package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConvocatoriaRepository
    extends JpaRepository<Convocatoria, Long>, JpaSpecificationExecutor<Convocatoria> {
}
