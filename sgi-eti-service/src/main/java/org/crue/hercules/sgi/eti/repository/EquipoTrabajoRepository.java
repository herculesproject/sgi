package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link EquipoTrabajo}.
 */

@Repository
public interface EquipoTrabajoRepository
    extends JpaRepository<EquipoTrabajo, Long>, JpaSpecificationExecutor<EquipoTrabajo> {

}