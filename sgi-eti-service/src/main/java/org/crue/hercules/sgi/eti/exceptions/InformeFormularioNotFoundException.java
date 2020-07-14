package org.crue.hercules.sgi.eti.exceptions;

/**
 * InformeFormularioNotFoundException
 */
public class InformeFormularioNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public InformeFormularioNotFoundException(Long informeFormularioId) {
    super("InformeFormulario " + informeFormularioId + " does not exist.");
  }

}