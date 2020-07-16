package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.Asistentes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Asistentes}.
 */

@Repository
public interface AsistentesRepository extends JpaRepository<Asistentes, Long>, JpaSpecificationExecutor<Asistentes> {

}