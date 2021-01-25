package org.crue.hercules.sgi.csp.exceptions;

/**
 * SolicitudProyectoPeriodoJustificacionNotFoundException
 */
public class SolicitudProyectoPeriodoJustificacionNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoPeriodoJustificacionNotFoundException(Long solicitudProyectoPeriodoJustificacionId) {
    super("Solicitud proyecto periodo justificación " + solicitudProyectoPeriodoJustificacionId + " does not exist.");
  }

}
