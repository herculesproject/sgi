package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProyectoPresupuestoRepository
    extends JpaRepository<SolicitudProyectoPresupuesto, Long>, JpaSpecificationExecutor<SolicitudProyectoPresupuesto> {

}
