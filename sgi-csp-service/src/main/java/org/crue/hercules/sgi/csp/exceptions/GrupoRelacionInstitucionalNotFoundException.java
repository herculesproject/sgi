package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.GrupoRelacionInstitucional;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;

/**
 * GrupoRelacionInstitucionalNotFoundException
 */
@SuppressWarnings("java:S110")
public class GrupoRelacionInstitucionalNotFoundException extends CspNotFoundException {

  private static final long serialVersionUID = 1L;

  public GrupoRelacionInstitucionalNotFoundException(Long id) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", getEntityName(GrupoRelacionInstitucional.class))
        .parameter("id", id).build());
  }

}
