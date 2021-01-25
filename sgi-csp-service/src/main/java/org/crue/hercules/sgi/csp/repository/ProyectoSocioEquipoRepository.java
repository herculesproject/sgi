package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoSocioEquipoRepository
    extends JpaRepository<ProyectoSocioEquipo, Long>, JpaSpecificationExecutor<ProyectoSocioEquipo> {

  /**
   * Recupera la lista de {@link ProyectoSocioEquipo} asociados a un
   * {@link ProyectoSocio}.
   * 
   * @param proyectoSocioId Identificador de {@link ProyectoSocio}.
   * @return lista de {@link ProyectoSocioEquipo}
   */
  List<ProyectoSocioEquipo> findAllByProyectoSocioId(Long proyectoSocioId);

}
