package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.RespuestaFormulario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link RespuestaFormulario}.
 */
@Repository
public interface RespuestaFormularioRepository
    extends JpaRepository<RespuestaFormulario, Long>, JpaSpecificationExecutor<RespuestaFormulario> {

}