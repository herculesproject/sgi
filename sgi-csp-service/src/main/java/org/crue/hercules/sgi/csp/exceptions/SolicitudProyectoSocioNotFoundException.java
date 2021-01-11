package org.crue.hercules.sgi.csp.exceptions;

/**
 * SolicitudProyectoDatosNotFoundException
 */
public class SolicitudProyectoSocioNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoSocioNotFoundException(Long solicitudProyectoSocioId) {
    super("Solicitud proyecto socio " + solicitudProyectoSocioId + " does not exist.");
  }

}
