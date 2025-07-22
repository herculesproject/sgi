package org.crue.hercules.sgi.eti.util;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.util.Assert;

public class AssertHelper {
  public static final String MESSAGE_KEY_ID = "id";
  public static final String MESSAGE_KEY_NAME = "name";
  public static final String MESSAGE_KEY_DATE = "date";
  public static final String MESSAGE_KEY_DATE_START = "date.start";
  public static final String MESSAGE_KEY_DATE_END = "date.end";
  public static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  public static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  public static final String PROBLEM_MESSAGE_PARAMETER_OTHER_FIELD = "otherField";

  public static final String PROBLEM_MESSAGE_NOTNULL = "notNull";
  private static final String PROBLEM_MESSAGE_ISNULL = "isNull";
  private static final String PROBLEM_MESSAGE_EXISTS = "exists";
  private static final String PROBLEM_MESSAGE_NOT_EXISTS = "notExists";
  private static final String PROBLEM_MESSAGE_BEFORE = "before";
  private static final String PROBLEM_MESSAGE_FIELD_BEFORE = "field.before";

  private AssertHelper() {
  }

  /**
   * Comprueba que el id sea null
   * 
   * @param id    Id de la entidad.
   * @param clazz clase para la que se comprueba el id
   */
  public static void idIsNull(Long id, Class<?> clazz) {
    Assert.isNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_ISNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(clazz))
            .build());
  }

  /**
   * Comprueba que la entidad perteneciente a una clase sea null
   * 
   * @param entityValue el valor de la entidad.
   * @param clazz       clase para la que se comprueba la entidad.
   * @param entityClazz clase de la entidad a comprobar entidad.
   */
  public static void entityIsNull(Object entityValue, Class<?> clazz, Class<?> entityClazz) {
    Assert.isNull(entityValue,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_ISNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(entityClazz))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(clazz))
            .build());
  }

  /**
   * Comprueba que el id no sea null
   * 
   * @param id    Id de la entidad.
   * @param clazz clase para la que se comprueba el id
   */
  public static void idNotNull(Long id, Class<?> clazz) {
    fieldNotNull(id, clazz, MESSAGE_KEY_ID);
  }

  /**
   * Comprueba que el campo no sea null
   * 
   * @param fieldValue      el valor del campo.
   * @param clazz           clase para la que se comprueba el campo.
   * @param messageKeyField clave del campo del messages properties.
   */
  public static void fieldNotNull(Object fieldValue, Class<?> clazz, String messageKeyField) {
    Assert.notNull(fieldValue,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(messageKeyField))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(clazz))
            .build());
  }

  /**
   * Comprueba que la entidad perteneciente a una clase no sea null
   * 
   * @param entityValue el valor de la entidad.
   * @param clazz       clase para la que se comprueba la entidad.
   * @param entityClazz clase de la entidad a comprobar entidad.
   */
  public static void entityNotNull(Object entityValue, Class<?> clazz, Class<?> entityClazz) {
    Assert.notNull(entityValue,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(entityClazz))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(clazz))
            .build());
  }

  /**
   * Comprueba que la entidad perteneciente a una clase exista
   * 
   * @param entityValue el valor de la entidad.
   * @param clazz       clase para la que se comprueba la entidad.
   * @param entityClazz clase de la entidad a comprobar entidad.
   */
  public static void entityExists(boolean entityValue, Class<?> clazz, Class<?> entityClazz) {
    Assert.isTrue(entityValue,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_EXISTS)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(entityClazz))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(clazz))
            .build());
  }

  /**
   * Comprueba que el campo no exista
   * 
   * @param fieldValue      el valor del campo.
   * @param clazz           clase para la que se comprueba el campo.
   * @param messageKeyField clave del campo del messages properties.
   */
  public static void fieldExists(boolean fieldValue, Class<?> clazz, String messageKeyField) {
    Assert.isTrue(fieldValue,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class,
            PROBLEM_MESSAGE_EXISTS)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(messageKeyField))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(clazz))
            .build());
  }

  /**
   * Comprueba que la entidad perteneciente a una clase no exista
   * 
   * @param entityValue el valor de la entidad.
   * @param clazz       clase para la que se comprueba la entidad.
   * @param entityClazz clase de la entidad a comprobar entidad.
   */
  public static void entityNotExists(boolean entityValue, Class<?> clazz, Class<?> entityClazz) {
    Assert.isTrue(entityValue,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOT_EXISTS)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(entityClazz))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(clazz))
            .build());
  }

  public static void isBefore(boolean fieldValue) {
    Assert.isTrue(
        fieldValue,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_BEFORE)
            .build());
  }

  public static void fieldBefore(boolean fieldValue, String messageKeyField, String messageOtherKeyField) {
    Assert.isTrue(
        fieldValue,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_FIELD_BEFORE)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(messageKeyField))
            .parameter(PROBLEM_MESSAGE_PARAMETER_OTHER_FIELD, ApplicationContextSupport.getMessage(
                messageOtherKeyField))
            .build());
  }
}