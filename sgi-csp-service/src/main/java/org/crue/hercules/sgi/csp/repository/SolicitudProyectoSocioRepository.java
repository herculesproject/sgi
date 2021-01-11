package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProyectoSocioRepository
    extends JpaRepository<SolicitudProyectoSocio, Long>, JpaSpecificationExecutor<SolicitudProyectoSocio> {

}
