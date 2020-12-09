package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudDocumentoRepository
    extends JpaRepository<SolicitudDocumento, Long>, JpaSpecificationExecutor<SolicitudDocumento> {

}
