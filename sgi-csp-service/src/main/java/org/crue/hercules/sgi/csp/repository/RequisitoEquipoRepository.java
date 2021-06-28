package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link RequisitoEquipo}.
 */
@Repository
public interface RequisitoEquipoRepository
    extends JpaRepository<RequisitoEquipo, Long>, JpaSpecificationExecutor<RequisitoEquipo> {
}
