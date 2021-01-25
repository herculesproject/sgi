package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para
 * {@link SolicitudProyectoEntidadFinanciadoraAjena}.
 */
@Repository
public interface SolicitudProyectoEntidadFinanciadoraAjenaRepository
    extends JpaRepository<SolicitudProyectoEntidadFinanciadoraAjena, Long>,
    JpaSpecificationExecutor<SolicitudProyectoEntidadFinanciadoraAjena> {

}
