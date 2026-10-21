package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.problem.spring.web.ProblemExceptionHandler;
import org.springframework.http.HttpStatus;

/**
 * Se lanza cuando el usuario no tiene permiso para consultar un parámetro de
 * configuración.
 */
public class UserNotAuthorizedToReadConfiguracionParamException extends ProblemException {

  private static final long serialVersionUID = 1L;

  /**
   * Crea la excepción para el parámetro de configuración indicado.
   *
   * @param name clave del parámetro de configuración solicitado.
   */
  public UserNotAuthorizedToReadConfiguracionParamException(String name) {
    super(Problem.builder().type(ProblemExceptionHandler.ACCESS_DENIED_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, HttpStatus.FORBIDDEN.name()).build())
        .detail(ProblemMessage.builder().key(UserNotAuthorizedToReadConfiguracionParamException.class)
            .parameter("name", name)
            .build())
        .status(HttpStatus.FORBIDDEN.value()).build());
  }

}
