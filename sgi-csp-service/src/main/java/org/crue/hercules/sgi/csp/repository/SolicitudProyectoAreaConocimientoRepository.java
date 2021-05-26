package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoAreaConocimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Spring Data JPA repository para {@link SolicitudProyectoAreaConocimiento}.
 */

public interface SolicitudProyectoAreaConocimientoRepository
    extends JpaRepository<SolicitudProyectoAreaConocimiento, Long>,
    JpaSpecificationExecutor<SolicitudProyectoAreaConocimiento> {

}
