package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoUnidadVinculacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para {@link SolicitudProyectoUnidadVinculacion}.
 */
@Repository
public interface SolicitudProyectoUnidadVinculacionRepository
    extends JpaRepository<SolicitudProyectoUnidadVinculacion, Long>,
    JpaSpecificationExecutor<SolicitudProyectoUnidadVinculacion> {

  /**
   * Devuelve las {@link SolicitudProyectoUnidadVinculacion} del
   * {@link SolicitudProyecto} con el id indicado.
   *
   * @param solicitudProyectoId id del {@link SolicitudProyecto}.
   * @return listado de {@link SolicitudProyectoUnidadVinculacion}.
   */
  List<SolicitudProyectoUnidadVinculacion> findAllBySolicitudProyectoId(Long solicitudProyectoId);

}
