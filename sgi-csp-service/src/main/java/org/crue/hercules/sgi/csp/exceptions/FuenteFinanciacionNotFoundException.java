package org.crue.hercules.sgi.csp.exceptions;

/**
 * FuenteFinanciacionNotFoundException
 */
public class FuenteFinanciacionNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public FuenteFinanciacionNotFoundException(Long fuenteFinanciacionId) {
    super("FuenteFinanciacion " + fuenteFinanciacionId + " does not exist.");
  }
}
