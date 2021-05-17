package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoClasificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link SolicitudProyectoClasificacion}.
 */
@Repository
public interface SolicitudProyectoClasificacionRepository extends JpaRepository<SolicitudProyectoClasificacion, Long>,
    JpaSpecificationExecutor<SolicitudProyectoClasificacion> {

}
