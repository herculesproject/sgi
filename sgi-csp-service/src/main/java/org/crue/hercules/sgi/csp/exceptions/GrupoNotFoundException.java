package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class GrupoNotFoundException extends CspNotFoundException {

  /**
   * GrupoNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public GrupoNotFoundException(Long id) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(
            Autorizacion.class))
        .parameter("id", id).build());
  }
}
