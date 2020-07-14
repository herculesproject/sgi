package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link EstadoMemoria}.
 */

@Repository
public interface EstadoMemoriaRepository
    extends JpaRepository<EstadoMemoria, Long>, JpaSpecificationExecutor<EstadoMemoria> {

}