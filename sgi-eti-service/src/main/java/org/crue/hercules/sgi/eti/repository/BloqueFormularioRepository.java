package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.BloqueFormulario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link BloqueFormulario}.
 */
@Repository
public interface BloqueFormularioRepository
    extends JpaRepository<BloqueFormulario, Long>, JpaSpecificationExecutor<BloqueFormulario> {

}