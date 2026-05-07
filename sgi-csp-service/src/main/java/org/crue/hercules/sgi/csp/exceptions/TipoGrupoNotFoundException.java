package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.TipoGrupo;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * TipoGrupoNotFoundException
 */
public class TipoGrupoNotFoundException extends CspNotFoundException {

  private static final long serialVersionUID = 1L;

  public TipoGrupoNotFoundException(Long id) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(TipoGrupo.class))
        .parameter("id", id).build());
  }

}
