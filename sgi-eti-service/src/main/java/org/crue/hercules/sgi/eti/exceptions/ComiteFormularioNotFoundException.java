package org.crue.hercules.sgi.eti.exceptions;

/**
 * ComiteFormularioNotFoundException
 */
public class ComiteFormularioNotFoundException extends EtiNotFoundException {
  /**
   * Serial version.
   */
  private static final long serialVersionUID = 1L;

  public ComiteFormularioNotFoundException(Long comiteFormularioId) {
    super("ComiteFormulario " + comiteFormularioId + " does not exist.");
  }
}