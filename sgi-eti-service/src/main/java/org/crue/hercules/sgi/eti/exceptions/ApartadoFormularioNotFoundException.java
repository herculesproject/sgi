package org.crue.hercules.sgi.eti.exceptions;

/**
 * ApartadoFormularioNotFoundException
 */
public class ApartadoFormularioNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ApartadoFormularioNotFoundException(Long apartadoFormularioId) {
    super("ApartadoFormulario " + apartadoFormularioId + " does not exist.");
  }

}