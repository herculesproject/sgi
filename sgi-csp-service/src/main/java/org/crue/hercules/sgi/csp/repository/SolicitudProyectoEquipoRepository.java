package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProyectoEquipoRepository
    extends JpaRepository<SolicitudProyectoEquipo, Long>, JpaSpecificationExecutor<SolicitudProyectoEquipo> {

}
