package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.SolicitudModalidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudModalidadRepository
    extends JpaRepository<SolicitudModalidad, Long>, JpaSpecificationExecutor<SolicitudModalidad> {

}
