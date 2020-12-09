package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.SolicitudHito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudHitoRepository
    extends JpaRepository<SolicitudHito, Long>, JpaSpecificationExecutor<SolicitudHito> {

}
