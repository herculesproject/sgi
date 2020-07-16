package org.crue.hercules.sgi.eti.exceptions;

/**
 * BloqueFormularioNotFoundException
 */
public class BloqueFormularioNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public BloqueFormularioNotFoundException(Long bloqueFormularioId) {
    super("BloqueFormulario " + bloqueFormularioId + " does not exist.");
  }

}