package org.crue.hercules.sgi.csp.exceptions;

/**
 * PlanNotFoundException
 */
public class PlanNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public PlanNotFoundException(Long planId) {
    super("Plan " + planId + " does not exist.");
  }
}
