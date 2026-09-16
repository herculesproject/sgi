package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.problem.spring.web.ProblemExceptionHandler;
import org.springframework.http.HttpStatus;

/**
 * Se intentan modificar los datos de un aviso que ya ha sido enviado.
 */
public class SentAvisoNotUpdatableException extends ProblemException {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public SentAvisoNotUpdatableException() {
    super(Problem.builder().type(ProblemExceptionHandler.VALIDATION_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, HttpStatus.BAD_REQUEST.name()).build())
        .detail(ProblemMessage.builder().key(SentAvisoNotUpdatableException.class).build())
        .status(HttpStatus.BAD_REQUEST.value()).build());
  }
}
