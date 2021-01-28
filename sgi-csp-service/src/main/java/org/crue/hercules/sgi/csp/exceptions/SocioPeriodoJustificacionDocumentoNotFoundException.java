package org.crue.hercules.sgi.csp.exceptions;

public class SocioPeriodoJustificacionDocumentoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SocioPeriodoJustificacionDocumentoNotFoundException(Long socioPeriodoJustificacionDocumentoId) {
    super("Socio periodo justificación documento " + socioPeriodoJustificacionDocumentoId + " does not exist.");
  }
}
