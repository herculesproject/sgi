package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProyectoRepository
    extends JpaRepository<SolicitudProyecto, Long>, JpaSpecificationExecutor<SolicitudProyecto> {

  /**
   * Recupera un {@link SolicitudProyecto} por solicitud id
   * 
   * @param solicitudId Identificador de la {@link Solicitud}
   * @return {@link SolicitudProyecto}
   */
  Optional<SolicitudProyecto> findBySolicitudId(Long solicitudId);

  boolean existsBySolicitudId(Long solicitudId);

}
