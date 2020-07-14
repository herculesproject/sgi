package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.InformeFormulario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link InformeFormulario}.
 */
@Repository
public interface InformeFormularioRepository
    extends JpaRepository<InformeFormulario, Long>, JpaSpecificationExecutor<InformeFormulario> {

}