package org.crue.hercules.sgi.csp.exceptions;

/**
 * AreaTematicaArbolNotFoundException
 */
public class AreaTematicaArbolNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public AreaTematicaArbolNotFoundException(Long areaTematicaArbolId) {
    super("AreaTematicaArbol " + areaTematicaArbolId + " does not exist.");
  }
}
