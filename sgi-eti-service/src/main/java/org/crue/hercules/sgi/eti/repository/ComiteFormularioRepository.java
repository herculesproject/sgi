package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.ComiteFormulario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ComiteFormulario}.
 */

@Repository
public interface ComiteFormularioRepository
    extends JpaRepository<ComiteFormulario, Long>, JpaSpecificationExecutor<ComiteFormulario> {

}