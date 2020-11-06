package org.crue.hercules.sgi.csp.exceptions;

/**
 * ConvocatoriaConceptoGastoNotFoundException
 */
public class ConvocatoriaConceptoGastoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaConceptoGastoNotFoundException(Long convocatoriaEnlaceId) {
    super("ConvocatoriaConceptoGasto " + convocatoriaEnlaceId + " does not exist.");
  }

}
