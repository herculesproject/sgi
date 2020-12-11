package org.crue.hercules.sgi.csp.exceptions;

/**
 * SolicitudProyectoDatosNotFoundException
 */
public class SolicitudProyectoDatosNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoDatosNotFoundException(Long solicitudProyectoDatosId) {
    super("Solicitud proyecto datos " + solicitudProyectoDatosId + " does not exist.");
  }

}
