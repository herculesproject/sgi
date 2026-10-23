package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.problem.spring.web.ProblemExceptionHandler;
import org.springframework.http.HttpStatus;

/**
 * La solicitud no tiene título y es obligatorio para crear su solicitud de
 * evaluación ética.
 */
public class SolicitudTituloRequiredForPeticionEvaluacionException extends ProblemException {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /**
   * Crea la excepción de validación de la solicitud sin título.
   */
  public SolicitudTituloRequiredForPeticionEvaluacionException() {
    super(Problem.builder().type(ProblemExceptionHandler.VALIDATION_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, HttpStatus.BAD_REQUEST.name()).build())
        .detail(ProblemMessage.builder().key(SolicitudTituloRequiredForPeticionEvaluacionException.class).build())
        .status(HttpStatus.BAD_REQUEST.value()).build());
  }
}
