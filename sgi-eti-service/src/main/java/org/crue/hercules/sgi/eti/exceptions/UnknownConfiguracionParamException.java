package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.problem.spring.web.ProblemExceptionHandler;
import org.springframework.http.HttpStatus;

/**
 * Se lanza cuando se solicita un parámetro de configuración que no existe.
 */
public class UnknownConfiguracionParamException extends ProblemException {

  private static final long serialVersionUID = 1L;

  /**
   * Crea la excepción para el parámetro de configuración indicado.
   *
   * @param name clave del parámetro de configuración solicitado.
   */
  public UnknownConfiguracionParamException(String name) {
    super(Problem.builder().type(ProblemExceptionHandler.BAD_REQUEST_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, HttpStatus.BAD_REQUEST.name()).build())
        .detail(ProblemMessage.builder().key(UnknownConfiguracionParamException.class)
            .parameter("name", name)
            .build())
        .status(HttpStatus.BAD_REQUEST.value()).build());
  }

}
