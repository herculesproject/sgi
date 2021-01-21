package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoFaseRepository
    extends JpaRepository<ProyectoFase, Long>, JpaSpecificationExecutor<ProyectoFase> {

}
