package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.FormularioMemoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link FormularioMemoria}.
 */
@Repository
public interface FormularioMemoriaRepository
    extends JpaRepository<FormularioMemoria, Long>, JpaSpecificationExecutor<FormularioMemoria> {

}