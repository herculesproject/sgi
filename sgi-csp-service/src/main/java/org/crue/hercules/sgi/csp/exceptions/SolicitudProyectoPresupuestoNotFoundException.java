package org.crue.hercules.sgi.csp.exceptions;

/**
 * SolicitudProyectoDatosNotFoundException
 */
public class SolicitudProyectoPresupuestoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoPresupuestoNotFoundException(Long solicitudProyectoPresupuestoId) {
    super("Solicitud proyecto presupuesto " + solicitudProyectoPresupuestoId + " does not exist.");
  }

}
