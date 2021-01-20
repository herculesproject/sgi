package org.crue.hercules.sgi.csp.exceptions;

/**
 * SolicitudProyectoPeriodoPagoNotFoundException
 */
public class SolicitudProyectoPeriodoPagoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoPeriodoPagoNotFoundException(Long solicitudProyectoPeriodoPagoId) {
    super("Solicitud proyecto periodo pago " + solicitudProyectoPeriodoPagoId + " does not exist.");
  }

}
