package org.crue.hercules.sgi.eti.exceptions;

/**
 * RespuestaFormularioNotFoundException
 */
public class RespuestaFormularioNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public RespuestaFormularioNotFoundException(Long respuestaFormularioId) {
    super("RespuestaFormulario " + respuestaFormularioId + " does not exist.");
  }

}