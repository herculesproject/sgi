package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ConvocatoriaDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConvocatoriaDocumentoRepository
    extends JpaRepository<ConvocatoriaDocumento, Long>, JpaSpecificationExecutor<ConvocatoriaDocumento> {
}
