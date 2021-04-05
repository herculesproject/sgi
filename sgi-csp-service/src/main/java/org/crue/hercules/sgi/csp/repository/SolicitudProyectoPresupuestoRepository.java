package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.repository.custom.CustomSolicitudProyectoPresupuestoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SolicitudProyectoPresupuestoRepository extends JpaRepository<SolicitudProyectoPresupuesto, Long>,
    JpaSpecificationExecutor<SolicitudProyectoPresupuesto>, CustomSolicitudProyectoPresupuestoRepository {

  /**
   * Recupera todos las {@link SolicitudProyectoPresupuesto} asociados a una
   * {@link SolicitudProyecto}.
   * 
   * @param solicitudProyectoId Identificador de
   *                            {@link SolicitudProyectoPresupuesto}
   * @return listado de {@link SolicitudProyectoPresupuesto}
   */
  List<SolicitudProyectoPresupuesto> findBySolicitudProyectoId(Long solicitudProyectoId);

}
