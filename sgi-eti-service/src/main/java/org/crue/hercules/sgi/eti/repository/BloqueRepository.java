package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.repository.custom.CustomBloqueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Bloque}.
 */
@Repository
public interface BloqueRepository
    extends JpaRepository<Bloque, Long>, JpaSpecificationExecutor<Bloque>, CustomBloqueRepository {

  Page<Bloque> findByFormularioId(Long idFormulario, Pageable pageable);

}