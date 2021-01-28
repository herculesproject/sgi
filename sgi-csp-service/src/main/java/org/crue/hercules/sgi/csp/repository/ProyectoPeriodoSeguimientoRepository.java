package org.crue.hercules.sgi.csp.repository;

import java.util.List;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoPeriodoSeguimientoRepository
    extends JpaRepository<ProyectoPeriodoSeguimiento, Long>, JpaSpecificationExecutor<ProyectoPeriodoSeguimiento> {

  /**
   * Obtiene un listado de {@link ProyectoPeriodoSeguimiento} por su
   * {@link Proyecto} ordenado por la fecha de inicio
   * 
   * @param proyectoId Id de la Proyecto de la {@link ProyectoPeriodoSeguimiento}
   * @return un listado {@link ProyectoPeriodoSeguimiento} ordenado por la fecha
   *         de inicio
   */
  List<ProyectoPeriodoSeguimiento> findAllByProyectoIdOrderByFechaInicio(Long proyectoId);

}
