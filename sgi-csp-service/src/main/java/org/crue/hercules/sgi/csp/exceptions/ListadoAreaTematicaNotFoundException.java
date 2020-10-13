package org.crue.hercules.sgi.csp.exceptions;

public class ListadoAreaTematicaNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ListadoAreaTematicaNotFoundException(Long listadoAreaTematicaId) {
    super("ListadoAreaTematica " + listadoAreaTematicaId + " does not exist.");
  }
}
