package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProyectoDatosRepository
    extends JpaRepository<SolicitudProyectoDatos, Long>, JpaSpecificationExecutor<SolicitudProyectoDatos> {

  /**
   * Recupera un {@link SolicitudProyectoDatos} por solicitud id
   * 
   * @param solicitudId Identificador de la {@link Solicitud}
   * @return {@link SolicitudProyectoDatos}
   */
  Optional<SolicitudProyectoDatos> findBySolicitudId(Long solicitudId);

  boolean existsBySolicitudId(Long solicitudId);

}
