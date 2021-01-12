package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EstadoProyectoRepository
    extends JpaRepository<EstadoProyecto, Long>, JpaSpecificationExecutor<EstadoProyecto> {

}
