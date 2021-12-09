package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NotificacionProyectoExternoCVNRepository extends JpaRepository<NotificacionProyectoExternoCVN, Long>,
    JpaSpecificationExecutor<NotificacionProyectoExternoCVN> {
}
