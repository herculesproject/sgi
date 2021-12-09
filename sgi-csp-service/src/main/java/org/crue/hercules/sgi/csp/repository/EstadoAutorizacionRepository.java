package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.EstadoAutorizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EstadoAutorizacionRepository
    extends JpaRepository<EstadoAutorizacion, Long>, JpaSpecificationExecutor<EstadoAutorizacion> {
}