package org.crue.hercules.sgi.framework.util;

import java.math.BigDecimal;
import java.util.Collection;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValue;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.Assert;

public class AssertHelper {
  public static final String MESSAGE_KEY_DATE = "date";
  public static final String MESSAGE_KEY_DATE_END = "date.end";
  public static final String MESSAGE_KEY_DATE_START = "date.start";
  public static final String MESSAGE_KEY_ID = "id";
  public static final String MESSAGE_KEY_NAME = "name";
  public static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  public static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  public static final String PROBLEM_MESSAGE_PARAMETER_OTHER_FIELD = "otherField";

  public static final String PROBLEM_MESSAGE_BEFORE = "before";
  public static final String PROBLEM_MESSAGE_EXISTS = "exists";
  public static final String PROBLEM_MESSAGE_FIELD_BEFORE = "field.before";
  public static final String PROBLEM_MESSAGE_INACTIVO = "inactivo";
  public static final String PROBLEM_MESSAGE_ISNULL = "isNull";
  public static final String PROBLEM_MESSAGE_NOT_EXISTS = "notExists";
  public static final String PROBLEM_MESSAGE_NOTNULL = "notNull";

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
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, getEntityName(clazz))
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
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, getEntityName(entityClazz))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, getEntityName(clazz))
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
   * Comprueba que el id no sea null
   *
   * @param id    Id de la entidad.
   * @param clazz clase para la que se comprueba el id
   */
  public static void idNotNull(String id, Class<?> clazz) {
    fieldNotNull((Object) id, clazz, MESSAGE_KEY_ID);
  }

  /**
   * Comprueba que el campo no sea null
   *
   * @param fieldValue      el valor del campo.
   * @param clazz           clase para la que se comprueba el campo.
   * @param messageKeyField clave del campo del messages properties.
   */
  public static void fieldNotNull(String fieldValue, Class<?> clazz, String messageKeyField) {
    fieldNotNull((Object) fieldValue, clazz, messageKeyField);
  }

  /**
   * Comprueba que el campo no sea null
   *
   * @param fieldValue      el valor del campo.
   * @param clazz           clase para la que se comprueba el campo.
   * @param messageKeyField clave del campo del messages properties.
   */
  public static void fieldNotNull(BigDecimal fieldValue, Class<?> clazz, String messageKeyField) {
    fieldNotNull((Object) fieldValue, clazz, messageKeyField);
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
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, getEntityName(clazz))
            .build());
  }

  /**
   * Comprueba que el campo sea null
   *
   * @param fieldValue      el valor del campo.
   * @param clazz           clase para la que se comprueba el campo.
   * @param messageKeyField clave del campo del messages properties.
   */
  public static void fieldIsNull(Object fieldValue, Class<?> clazz, String messageKeyField) {
    Assert.isNull(fieldValue,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_ISNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(messageKeyField))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, getEntityName(clazz))
            .build());
  }

  /**
   * Comprueba que el campo no sea null
   *
   * @param fieldValue      el valor del campo.
   * @param clazz           clase para la que se comprueba el campo.
   * @param messageKeyField clave del campo del messages properties.
   */
  public static void fieldNotNull(boolean fieldValue, Class<?> clazz, String messageKeyField) {
    Assert.isTrue(fieldValue,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(messageKeyField))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, getEntityName(clazz))
            .build());
  }

  /**
   * Comprueba que el campo no sea null ni vacío
   *
   * @param fieldValue      el valor del campo.
   * @param clazz           clase para la que se comprueba el campo.
   * @param messageKeyField clave del campo del messages properties.
   */
  public static void fieldNotBlank(boolean fieldValue, Class<?> clazz, String messageKeyField) {
    Assert.isTrue(fieldValue,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(messageKeyField))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, getEntityName(clazz))
            .build());
  }

  /**
   * Comprueba que un campo i18n no sea esté vacio. Equivalente a
   * {@link #fieldNotNull(String, Class, String)}
   *
   * @param fieldValue      el valor del campo.
   * @param clazz           clase para la que se comprueba el campo.
   * @param messageKeyField clave del campo del messages properties.
   */
  public static void i18nFieldNotNull(Collection<? extends I18nFieldValue> fieldValue, Class<?> clazz,
      String messageKeyField) {
    Assert.notEmpty(fieldValue,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(messageKeyField))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, getEntityName(clazz))
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
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, getEntityName(entityClazz))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, getEntityName(clazz))
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
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, getEntityName(entityClazz))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, getEntityName(clazz))
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
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, getEntityName(clazz))
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
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, getEntityName(entityClazz))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, getEntityName(clazz))
            .build());
  }

  /**
   * Comprueba que la fecha de inicio no sea posterior a la fecha de fin
   *
   * @param fieldValue resultado de la comparación de las fechas.
   */
  public static void isBefore(boolean fieldValue) {
    Assert.isTrue(
        fieldValue,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_BEFORE)
            .build());
  }

  /**
   * Comprueba que un campo sea anterior a otro
   *
   * @param fieldValue           resultado de la comparación de los campos.
   * @param messageKeyField      clave del campo del messages properties.
   * @param messageOtherKeyField clave del otro campo del messages properties.
   */
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

  /**
   * Comprueba que la entidad esté activa.
   *
   * @param activo      indica si la entidad está activa.
   * @param entityClazz clase de la entidad cuya activación se comprueba.
   * @param entityName  identificador/nombre concreto de la entidad para incluir
   *                    en el mensaje de error.
   */
  public static void entityActivo(boolean activo, Class<?> entityClazz, String entityName) {
    Assert.isTrue(activo,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_INACTIVO)
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, getEntityName(entityClazz))
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, entityName)
            .build());
  }

  /**
   * Comprueba que el campo de tipo listado tenga elementos
   *
   * @param field           el valor del campo.
   * @param entityClazz     clase para la que se comprueba el campo.
   * @param messageKeyField clave del campo del messages properties.
   */
  public static void fieldNotEmpty(Collection<?> field, Class<?> entityClazz, String messageKeyField) {
    Assert.notEmpty(field,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(
                PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(
                    messageKeyField))
            .parameter(
                PROBLEM_MESSAGE_PARAMETER_ENTITY,
                entityClazz)
            .build());
  }

  /**
   * Comprueba que el campo de tipo listado no tenga elementos nulos
   *
   * @param field           el valor del campo.
   * @param entityClazz     clase para la que se comprueba el campo.
   * @param messageKeyField clave del campo del messages properties.
   */
  public static void fieldNoNullElements(Collection<?> field, Class<?> entityClazz, String messageKeyField) {
    Assert.notEmpty(field,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(
                PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(
                    messageKeyField))
            .parameter(
                PROBLEM_MESSAGE_PARAMETER_ENTITY,
                entityClazz)
            .build());
  }

  /**
   * Devuelve el nombre localizado de la entidad, o su nombre de clase si no hay
   * traducción para el locale actual.
   *
   * @param clazz la clase de la entidad.
   * @return el nombre de la entidad.
   */
  private static String getEntityName(Class<?> clazz) {
    try {
      return ApplicationContextSupport.getMessage(clazz);
    } catch (NoSuchMessageException e) {
      return clazz.getSimpleName();
    }
  }

}
