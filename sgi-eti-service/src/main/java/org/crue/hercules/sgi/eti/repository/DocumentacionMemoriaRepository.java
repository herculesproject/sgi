package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link DocumentacionMemoria}.
 */

@Repository
public interface DocumentacionMemoriaRepository
    extends JpaRepository<DocumentacionMemoria, Long>, JpaSpecificationExecutor<DocumentacionMemoria> {

}