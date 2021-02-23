package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para
 * {@link SolicitudProyectoEntidadFinanciadoraAjena}.
 */
@Repository
public interface SolicitudProyectoEntidadFinanciadoraAjenaRepository
    extends JpaRepository<SolicitudProyectoEntidadFinanciadoraAjena, Long>,
    JpaSpecificationExecutor<SolicitudProyectoEntidadFinanciadoraAjena> {

  /**
   * Obtiene las {@link SolicitudProyectoEntidadFinanciadoraAjena} asociadas a una
   * {@link Solicitud}
   * 
   * @param solicitudProyectoDatosId Identificador de la {@link Solicitud}
   * @return Listado de solicitudes modalidad
   */
  List<SolicitudProyectoEntidadFinanciadoraAjena> findAllBySolicitudProyectoDatosId(Long solicitudProyectoDatosId);

  /**
   * Recupera todos las {@link SolicitudProyectoEntidadFinanciadoraAjena}
   * asociados a una {@link SolicitudProyectoDatos}.
   * 
   * @param solicitudProyectoId Identificador de
   *                            {@link SolicitudProyectoEntidadFinanciadoraAjena}
   * @return listado de {@link SolicitudProyectoEntidadFinanciadoraAjena}
   */
  List<SolicitudProyectoEntidadFinanciadoraAjena> findBySolicitudProyectoDatosId(Long solicitudProyectoId);

}
