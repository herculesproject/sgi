package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.TipoConfidencialidad;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;

/**
 * TipoConfidencialidadNotFoundException
 */
@SuppressWarnings("java:S110")
public class TipoConfidencialidadNotFoundException extends CspNotFoundException {

  private static final long serialVersionUID = 1L;

  public TipoConfidencialidadNotFoundException(Long id) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", getEntityName(TipoConfidencialidad.class))
        .parameter("id", id).build());
  }

}
