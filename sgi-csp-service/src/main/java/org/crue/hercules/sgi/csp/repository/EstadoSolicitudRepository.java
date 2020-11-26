package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EstadoSolicitudRepository
    extends JpaRepository<EstadoSolicitud, Long>, JpaSpecificationExecutor<EstadoSolicitud> {

}
