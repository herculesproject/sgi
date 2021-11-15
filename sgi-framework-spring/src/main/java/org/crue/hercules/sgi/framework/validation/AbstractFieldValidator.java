package org.crue.hercules.sgi.framework.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for database driven field level entity validatos.
 */
public abstract class AbstractFieldValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {
  /** Message parameter holding the entity name */
  public static final String MESSAGE_PARAMETER_ENTITY = "entity";
  /** Message parameter holding the entity field name */
  public static final String MESSAGE_PARAMETER_FIELD = "field";
  /** Message parameter holding the entity field value */
  public static final String MESSAGE_PARAMETER_VALUE = "value";

  /** Annotation method to get validated entity class */
  public static final String METHOD_ENTITY_CLASS = "entityClass";
  /** Annotation method to get validated entity field name */
  public static final String METHOD_FIELD = "field";

  /** Annotation method signature to get validated entity class */
  private static final String METHOD_ENTITY_CLASS_SIGNATURE = "Class<?> " + METHOD_ENTITY_CLASS + "()";
  /** Annotation method signature to get validated entity field name */
  private static final String METHOD_FIELD_SIGNATURE = "String " + METHOD_FIELD + "()";

  /** The JPA {@link EntityManager} */
  protected EntityManager entityManager;
  /** The entity {@link Class} that this validatos can validate */
  protected Class<?> entityClass;
  /** The entity field name that this validatos can validate */
  protected String field;

  @Override
  public void initialize(A constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    Class<? extends Annotation> currentClass = constraintAnnotation.getClass();
    Method entytClassMethod;
    Method fieldMethod;
    try {
      entytClassMethod = currentClass.getMethod(METHOD_ENTITY_CLASS);
      entityClass = (Class<?>) entytClassMethod.invoke(constraintAnnotation);
      fieldMethod = currentClass.getMethod(METHOD_FIELD);
      field = (String) fieldMethod.invoke(constraintAnnotation);
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      throw new IllegalArgumentException(String.format("Annotation %s does not have methods %s and %s",
          currentClass.getSimpleName(), METHOD_ENTITY_CLASS_SIGNATURE, METHOD_FIELD_SIGNATURE), e);
    }

    // type check
    if (!(entityClass.isAnnotationPresent(Entity.class))) {
      throw new IllegalArgumentException("The provided entityClass is not annotated with @Entity");
    }
    ApplicationContext applicationContext = ApplicationContextSupport.getApplicationContext();
    entityManager = (EntityManager) applicationContext.getBean(EntityManager.class);
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    // fast-return
    if (value != null) {
      // type check
      if (!entityClass.isInstance(value)) {
        throw new IllegalArgumentException(
            String.format("The provided value is not of type %s", entityClass.getSimpleName()));
      }
      if (validate(value, context)) {
        return true;
      }
    }
    addEntityMessageParameter(value, context);
    return false;
  }

  /**
   * Customize the validation logic for the specific entity field level
   * {@link ConstraintValidator}. The state of {@code value} must not be altered.
   * 
   * @param value   object to validate
   * @param context context in which the constraint is evaluated
   *
   * @return {@code false} if {@code value} does not pass the constraint
   */
  protected abstract boolean validate(Object value, ConstraintValidatorContext context);

  /**
   * Add "entity", "field" and "value" message parameters so they can be used in
   * the error message.
   * <p>
   * The value can be customized with the
   * {@link #getValue(Object, ConstraintValidatorContext)} method.
   * 
   * @param value   object to validate
   * @param context context in which the constraint is evaluated
   */
  protected void addEntityMessageParameter(Object value, ConstraintValidatorContext context) {
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    // Add "entity", "field" and "value" message parameters so they can be used in
    // the error message
    hibernateContext.addMessageParameter(MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(entityClass));
    hibernateContext.addMessageParameter(MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(field));
    hibernateContext.addMessageParameter(MESSAGE_PARAMETER_VALUE, getValue(value, context));
  }

  /**
   * Customize the value used in the specific validation message for the specific
   * entity field level {@link ConstraintValidator}
   * 
   * @param value   object to validate
   * @param context context in which the constraint is evaluated
   *
   * @return the specific value (generally the field value) been validated
   */
  protected String getValue(Object value, ConstraintValidatorContext context) {
    return value.toString();
  };
}
