package org.crue.hercules.sgi.eti.exceptions;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.problem.spring.web.ProblemExceptionHandler;
import org.springframework.http.HttpStatus;

/**
 * Se lanza cuando se intenta guardar en un parámetro de configuración un valor
 * que no es válido para su tipo.
 */
public class ConfiguracionParamInvalidValueException extends ProblemException {

  private static final long serialVersionUID = 1L;

  /**
   * Crea la excepción para el parámetro de configuración indicado.
   *
   * @param name  clave del parámetro de configuración.
   * @param value valor no válido recibido.
   */
  public ConfiguracionParamInvalidValueException(String name, String value) {
    super(Problem.builder().type(ProblemExceptionHandler.BAD_REQUEST_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, HttpStatus.BAD_REQUEST.name()).build())
        .detail(ProblemMessage.builder().key(ConfiguracionParamInvalidValueException.class)
            .parameter("name", name)
            .parameter("value", StringUtils.defaultString(value))
            .build())
        .status(HttpStatus.BAD_REQUEST.value()).build());
  }

}
