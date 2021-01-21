package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoSocioEquipoRepository
    extends JpaRepository<ProyectoSocioEquipo, Long>, JpaSpecificationExecutor<ProyectoSocioEquipo> {

  /**
   * 
   * @param proyectoSocioId
   * @return
   */
  List<ProyectoSocioEquipo> findAllByProyectoSocioId(Long proyectoSocioId);

}
