package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ProyectoProyectoSge}.
 */
@Repository
public interface ProyectoProyectoSgeRepository
    extends JpaRepository<ProyectoProyectoSge, Long>, JpaSpecificationExecutor<ProyectoProyectoSge> {

}
