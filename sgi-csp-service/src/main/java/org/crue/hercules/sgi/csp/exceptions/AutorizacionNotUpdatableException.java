package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.problem.spring.web.ProblemExceptionHandler;
import org.springframework.http.HttpStatus;

/**
 * Se intenta modificar una solicitud de autorización en un estado que no lo
 * permite.
 */
public class AutorizacionNotUpdatableException extends ProblemException {

  /** Serial version */
  private static final long serialVersionUID = 1L;

  /**
   * Crea la excepción con el mensaje de solicitud de autorización no
   * modificable.
   */
  public AutorizacionNotUpdatableException() {
    super(Problem.builder().type(ProblemExceptionHandler.VALIDATION_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, HttpStatus.BAD_REQUEST.name()).build())
        .detail(ProblemMessage.builder().key(AutorizacionNotUpdatableException.class).build())
        .status(HttpStatus.BAD_REQUEST.value()).build());
  }
}
