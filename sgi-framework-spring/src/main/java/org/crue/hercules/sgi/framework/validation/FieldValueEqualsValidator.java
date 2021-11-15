package org.crue.hercules.sgi.framework.validation;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.ApplicationContext;

/**
 * Field validator to check than an entity database field value is equal to the
 * provided value.
 * <p>
 * The entity is read from database using it's identifier before checking it's
 * field value.
 */
public class FieldValueEqualsValidator extends AbstractFieldValidator<FieldValueEquals, Object> {
  private PersistenceUnitUtil persistenceUnitUtil;
  private String value;

  @Override
  public void initialize(FieldValueEquals constraintAnnotation) {
    super.initialize(constraintAnnotation);
    value = constraintAnnotation.value();
    persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
  }

  @Override
  protected boolean validate(Object value, ConstraintValidatorContext context) {
    Object id = persistenceUnitUtil.getIdentifier(value);
    if (id != null) {
      Object entity = entityManager.find(entityClass, id);
      if (entity != null) {
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(entity);
        Object fieldValue = wrapper.getPropertyValue(field);
        if (this.value.equals(fieldValue)) {
          return true;
        }
      }
    } else {
      throw new IllegalArgumentException(
          String.format("Can't get identifier value from type %s", field, entityClass.getSimpleName()));
    }
    return false;
  }
}
