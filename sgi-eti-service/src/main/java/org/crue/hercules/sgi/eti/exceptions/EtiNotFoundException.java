package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.context.NoSuchMessageException;

/**
 * EtiServiceNotFoundException
 */
@SuppressWarnings("java:S110")
public class EtiNotFoundException extends NotFoundException {

  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String MESSAGE_KEY_ID = "id";

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public EtiNotFoundException(String message) {
    super(message);
  }

  /**
   * Crea la excepción para la entidad de la clase indicada con el identificador
   * indicado.
   *
   * @param id    identificador de la entidad.
   * @param clazz clase de la entidad.
   */
  public EtiNotFoundException(Long id, Class<?> clazz) {
    super(ProblemMessage.builder().key(EtiNotFoundException.class)
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
