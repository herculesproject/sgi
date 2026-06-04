package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ProyectoUnidadVinculacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para {@link ProyectoUnidadVinculacion}.
 */
@Repository
public interface ProyectoUnidadVinculacionRepository extends JpaRepository<ProyectoUnidadVinculacion, Long>,
    JpaSpecificationExecutor<ProyectoUnidadVinculacion> {

}
