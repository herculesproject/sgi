package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.problem.spring.web.ProblemExceptionHandler;
import org.springframework.http.HttpStatus;

/**
 * Se lanza cuando se intenta habilitar un parámetro de configuración sin estar
 * habilitado el parámetro del que depende.
 */
public class ConfiguracionParamDependencyException extends ProblemException {

  private static final long serialVersionUID = 1L;

  /**
   * Crea la excepción para el parámetro de configuración indicado.
   *
   * @param name      clave del parámetro de configuración que se quiere
   *                  habilitar.
   * @param dependsOn clave del parámetro de configuración del que depende.
   */
  public ConfiguracionParamDependencyException(String name, String dependsOn) {
    super(Problem.builder().type(ProblemExceptionHandler.BAD_REQUEST_PROBLEM_TYPE)
        .title(ProblemMessage.builder().key(HttpStatus.class, HttpStatus.BAD_REQUEST.name()).build())
        .detail(ProblemMessage.builder().key(ConfiguracionParamDependencyException.class)
            .parameter("name", name)
            .parameter("dependsOn", dependsOn)
            .build())
        .status(HttpStatus.BAD_REQUEST.value()).build());
  }

}
