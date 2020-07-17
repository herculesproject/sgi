package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.ApartadoFormulario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ApartadoFormulario}.
 */

@Repository
public interface ApartadoFormularioRepository
    extends JpaRepository<ApartadoFormulario, Long>, JpaSpecificationExecutor<ApartadoFormulario> {

}