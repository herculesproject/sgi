package org.crue.hercules.sgi.csp.exceptions.rel;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;

/**
 * Excepción lanzada cuando se produce un error no controlado recuperando las
 * relaciones del módulo REL.
 */
public class GetRelacionesException extends ProblemException {
  private static final long serialVersionUID = 1L;

  public GetRelacionesException() {
    super(Problem.builder()
        .title(ProblemMessage.builder().key(GetRelacionesException.class).build())
        .build());
  }
}
