package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.CertificadoAutorizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CertificadoAutorizacionRepository
    extends JpaRepository<CertificadoAutorizacion, Long>, JpaSpecificationExecutor<CertificadoAutorizacion> {

}
