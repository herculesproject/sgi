package org.crue.hercules.sgi.usr.exceptions;

/**
 * UnidadNotFoundException
 */
public class UnidadNotFoundException extends UsrNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public UnidadNotFoundException(Long unidadId) {
    super("Unidad " + unidadId + " does not exist.");
  }

}
