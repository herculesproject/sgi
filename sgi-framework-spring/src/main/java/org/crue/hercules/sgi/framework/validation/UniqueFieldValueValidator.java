package org.crue.hercules.sgi.framework.validation;

import javax.persistence.Query;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

/**
 * Field validator to check than an entity current field value is not pressent
 * in the database.
 */
public class UniqueFieldValueValidator extends AbstractFieldValidator<UniqueFieldValue, Object> {
  private static final String QUERY_COUNT = "SELECT count(e) FROM %s e WHERE %s=?1";
  private String currValue;

  @Override
  protected boolean validate(Object value, ConstraintValidatorContext context) {
    Query query = entityManager.createQuery(String.format(QUERY_COUNT, entityClass.getSimpleName(), field));
    BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
    Object fieldValue = wrapper.getPropertyValue(field);
    currValue = fieldValue.toString();
    Long count = (Long) query.setParameter(1, fieldValue).getSingleResult();
    if (count.equals(0l)) {
      return true;
    }
    return false;
  }

  @Override
  protected String getValue(Object value, ConstraintValidatorContext context) {
    return currValue;
  }
}
