package org.crue.hercules.sgi.csp.util;

import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.util.Assert;

public class ProyectoHelper {

  /**
   * Comprueba que el proyecto está en un estado que permita su modificación, así
   * como que pertenece a una unidad de gestión que el usuario actual pueda
   * gestionar.
   * 
   * @param proyecto el {@link Proyecto} sobre el que realizar las comprobaciones
   */
  public static void checkCanUpdate(Proyecto proyecto) {
    checkCanRead(proyecto);
    Assert.isTrue(proyecto.getActivo(), "El Proyecto está desactivado");
    EstadoProyecto.Estado estadoActual = proyecto.getEstado().getEstado();
    Assert.isTrue(estadoActual != EstadoProyecto.Estado.FINALIZADO && estadoActual != EstadoProyecto.Estado.CANCELADO,
        "El proyecto no está en un estado en el que puede ser actualizado");
  }

  /**
   * Comprueba que el proyecto pertenece a una unidad de gestión que el usuario
   * actual pueda gestionar.
   * 
   * @param proyecto el {@link Proyecto} sobre el que realizar las comprobaciones
   */
  public static void checkCanRead(Proyecto proyecto) {
    // TODO: Comprobar authorities correctas
    Assert.isTrue(
        SgiSecurityContextHolder.hasAnyAuthorityForUO(new String[] { "CSP-PRO-C", "CSP-PRO-E", "CSP-PRO-V-INV" },
            proyecto.getUnidadGestionRef()),
        "El proyecto no pertenece a una Unidad de Gestión gestionable por el usuario");
  }
}
