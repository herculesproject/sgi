package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.context.NoSuchMessageException;

/**
 * CspNotFoundException
 */
@SuppressWarnings("java:S110")
public class CspNotFoundException extends NotFoundException {

  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String MESSAGE_KEY_ID = "id";

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public CspNotFoundException(String message) {
    super(message);
  }

  public CspNotFoundException(Long id, Class<?> clazz) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, getEntityName(clazz))
        .parameter(MESSAGE_KEY_ID, id).build());
  }

  /**
   * Devuelve el nombre localizado de la entidad, o su nombre de clase si no hay
   * traducción para el locale actual.
   *
   * @param clazz la clase de la entidad.
   * @return el nombre de la entidad.
   */
  protected static String getEntityName(Class<?> clazz) {
    try {
      return ApplicationContextSupport.getMessage(clazz);
    } catch (NoSuchMessageException e) {
      return clazz.getSimpleName();
    }
  }

}
