package org.crue.hercules.sgi.csp.exceptions;

/**
 * SolicitudProyectoEquipoSocioNotFoundException
 */
public class SolicitudProyectoEquipoSocioNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoEquipoSocioNotFoundException(Long solicitudProyectoEquipoSocioId) {
    super("Solicitud proyecto equipo socio " + solicitudProyectoEquipoSocioId + " does not exist.");
  }

}
