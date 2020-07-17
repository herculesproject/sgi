package org.crue.hercules.sgi.eti.exceptions;

/**
 * FormularioMemoriaNotFoundException
 */
public class FormularioMemoriaNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public FormularioMemoriaNotFoundException(Long formularioMemoriaId) {
    super("FormularioMemoria " + formularioMemoriaId + " does not exist.");
  }

}