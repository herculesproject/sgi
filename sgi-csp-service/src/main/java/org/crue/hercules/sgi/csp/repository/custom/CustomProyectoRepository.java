package org.crue.hercules.sgi.csp.repository.custom;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link Proyecto}.
 */
@Component
public interface CustomProyectoRepository {

  /**
   * Obtiene el {@link ModeloEjecucion} asignada al {@link Proyecto}.
   *
   * @param id Id de la {@link Proyecto}.
   * @return {@link ModeloEjecucion} asignado
   */
  Optional<ModeloEjecucion> getModeloEjecucion(Long id);

  /**
   * Indica si en el {@link Proyecto} se permiten {@link ProyectoPaqueteTrabajo}.
   *
   * @param id Id de la {@link Proyecto}.
   * @return true si se permiten {@link ProyectoPaqueteTrabajo}, false si no se
   *         permiten {@link ProyectoPaqueteTrabajo}
   */
  Optional<Boolean> getPermitePaquetesTrabajo(Long id);
}
