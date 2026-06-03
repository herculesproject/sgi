package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.GrupoUnidadVinculacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupoUnidadVinculacionRepository extends JpaRepository<GrupoUnidadVinculacion, Long>,
    JpaSpecificationExecutor<GrupoUnidadVinculacion> {

}
