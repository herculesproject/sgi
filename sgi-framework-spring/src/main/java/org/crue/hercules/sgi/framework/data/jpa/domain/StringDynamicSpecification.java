package org.crue.hercules.sgi.framework.data.jpa.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.framework.exception.IllegalSpecificationArgumentException;
import org.crue.hercules.sgi.framework.exception.UnsupportedSpecificationOperationException;
import org.springframework.data.jpa.domain.Specification;

/**
 * Base class for dynamic specification implementations based on string paths.
 * 
 * @param <T> The entity type
 */
public abstract class StringDynamicSpecification<T> implements Specification<T> {
  private static final long serialVersionUID = 1L;

  /**
   * Split a path separated by <code>.</code> in field names
   * 
   * @param stringPath the path to split
   * @return list of field names
   */
  private List<String> parseStringPath(String stringPath) {
    List<String> fields = new ArrayList<>();
    if (StringUtils.isBlank(stringPath)) {
      return fields;
    }
    for (String field : stringPath.split("\\.")) {
      fields.add(field);
    }
    return fields;
  }

  /**
   * Checks if path correspond to a OneToMany or ManyToMany relationships
   * 
   * @param path Path to check
   * @return <code>true</code> if path correspond to a OneToMany or ManyToMany
   *         attribute, <code>false</code> otherwise
   */
  private boolean isPluralAttribute(Path<?> path) {
    Class<?> clazz = path.getJavaType();
    return clazz.isAssignableFrom(Collection.class) || clazz.isAssignableFrom(List.class)
        || clazz.isAssignableFrom(Map.class) || clazz.isAssignableFrom(Set.class);
  }

  /**
   * Builds a Join Path to transverse OneToMany or ManyToMany relationships
   * 
   * @param <X>        Source type
   * @param <Y>        Target type
   * @param root       Starting path
   * @param attributes Nested attributes names
   * @return
   */
  private <X, Y> Join<X, Y> getJoin(Root<?> root, List<String> attributes) {
    Join<X, Y> join = null;
    for (String attribute : attributes) {
      if (join == null) {
        join = root.join(attribute);
      } else {
        join = join.join(attribute);
      }
    }

    return join;
  }

  /**
   * Gets the {@link Path} recursively from string path that represent field name
   * or nested field names separated by dot (<code>.</code>) For example:
   * <code>field.nestedField.nestedField</code>
   *
   * @param root       Entity root to scan
   * @param stringPath String path representation to get
   * @return
   * 
   * @throws IllegalSpecificationArgumentException If the field name don't exists
   */
  protected <Y> Path<Y> getPath(Root<?> root, String stringPath) throws IllegalSpecificationArgumentException {
    List<String> fields = parseStringPath(stringPath);
    List<String> checkedFields = new ArrayList<>();
    Path<Y> path = null;
    try {
      for (String field : fields) {
        checkedFields.add(field);
        if (path == null) {
          if (isPluralAttribute(root.get(field))) {
            path = root.join(field);
          } else {
            path = root.get(field);
          }
        } else {
          if (isPluralAttribute(path.get(field))) {
            path = getJoin(root, checkedFields);
          } else {
            path = path.get(field);
          }
        }
      }
    } catch (IllegalArgumentException e) {
      throw new IllegalSpecificationArgumentException(
          "The path " + stringPath + " don't exists in entity " + root.getJavaType().getSimpleName(), e);
    }

    return path;
  }

  /**
   * Convert the string representation of an value to field class type.
   * 
   * @param stringPath The String path of the field
   * @param clazz      The target class of the field
   * @param value      The value to convert
   * 
   * @return the value converted to the target class
   * 
   * @throws IllegalSpecificationArgumentException If the value cannot be
   *                                               converted
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected Object stringToFieldClass(Class<? extends Object> clazz, String value)
      throws IllegalSpecificationArgumentException {
    if (value == null) {
      return null;
    }
    try {
      if (clazz.isAssignableFrom(Integer.class)) {
        return Integer.valueOf(value.trim());
      }
      if (clazz.isAssignableFrom(int.class)) {
        return Integer.parseInt(value);
      }
      if (clazz.isAssignableFrom(Long.class)) {
        return Long.valueOf(value);
      }
      if (clazz.isAssignableFrom(long.class)) {
        return Long.parseLong(value);
      }
      if (clazz.isAssignableFrom(Float.class)) {
        return Float.valueOf(value);
      }
      if (clazz.isAssignableFrom(float.class)) {
        return Float.parseFloat(value);
      }
    } catch (NumberFormatException e) {
      throw new IllegalSpecificationArgumentException(value + " cannot be assigned to " + clazz.getSimpleName(), e);
    }
    if (clazz.isAssignableFrom(Boolean.class)) {
      if (StringUtils.isBlank(value)) {
        return null;
      }
      if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
        throw new IllegalSpecificationArgumentException(value + " cannot be assigned to " + clazz.getSimpleName());
      }
      return Boolean.valueOf(value);
    }
    if (clazz.isAssignableFrom(boolean.class)) {
      if (StringUtils.isBlank(value) || (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value))) {
        throw new IllegalSpecificationArgumentException(value + " cannot be assigned to " + clazz.getSimpleName());
      }
      return Boolean.parseBoolean(value);
    }
    if (clazz.isEnum()) {
      try {
        return Enum.valueOf((Class<Enum>) clazz, value);
      } catch (IllegalArgumentException e) {
        throw new IllegalSpecificationArgumentException(value + " cannot be assigned to " + clazz.getSimpleName(), e);
      }
    }
    try {
      if (clazz.isAssignableFrom(LocalDateTime.class)) {
        return LocalDateTime.parse(value);
      }
      if (clazz.isAssignableFrom(LocalDate.class)) {
        return LocalDate.parse(value);
      }
      if (clazz.isAssignableFrom(ZonedDateTime.class)) {
        return ZonedDateTime.parse(value);
      }
    } catch (DateTimeParseException e) {
      throw new IllegalSpecificationArgumentException(value + " cannot be assigned to " + clazz.getSimpleName(), e);
    }
    if (clazz.isAssignableFrom(String.class)) {
      return value;
    }
    throw new IllegalSpecificationArgumentException(value + " cannot be assigned to " + clazz.getSimpleName());
  }

  /**
   * Builds equal predicate
   * 
   * @param cb    The JPA criteria builder to use
   * @param field The field to use
   * @param value The value to use
   * 
   * @return the predicate
   */
  protected Predicate buildEqual(CriteriaBuilder cb, Expression<?> field, Object value) {
    if (value == null) {
      return field.isNull();
    }
    return cb.equal(field, value);
  }

  /**
   * Builds notEqual predicate
   * 
   * @param cb    The JPA criteria builder to use
   * @param field The field to use
   * @param value The value to use
   * 
   * @return the predicate
   */
  protected Predicate buildNotEqual(CriteriaBuilder cb, Expression<?> field, Object value) {
    if (value == null) {
      return field.isNotNull();
    }
    return cb.notEqual(field, value);
  }

  /**
   * Builds lessThanOrEqual predicate
   * 
   * @param cb    The JPA criteria builder to use
   * @param field The field to use
   * @param value The value to use
   * 
   * @return the predicate
   * 
   * @throws UnsupportedSpecificationOperationException If the field don't support
   *                                                    lessThanOrEquals
   */
  @SuppressWarnings("unchecked")
  protected Predicate buildLessThanOrEqual(CriteriaBuilder cb, Expression<?> field, Object value)
      throws UnsupportedSpecificationOperationException {
    if (value == null) {
      throw new UnsupportedSpecificationOperationException("LessThanOrEqual don't support null values");
    }
    if (field.getJavaType().isAssignableFrom(Integer.class) || field.getJavaType().isAssignableFrom(int.class)) {
      return cb.lessThanOrEqualTo((Expression<Integer>) field, (Integer) value);
    }
    if (field.getJavaType().isAssignableFrom(Long.class) || field.getJavaType().isAssignableFrom(long.class)) {
      return cb.lessThanOrEqualTo((Expression<Long>) field, (Long) value);
    }
    if (field.getJavaType().isAssignableFrom(Float.class) || field.getJavaType().isAssignableFrom(float.class)) {
      return cb.lessThanOrEqualTo((Expression<Float>) field, (Float) value);
    }
    if (field.getJavaType().isAssignableFrom(LocalDate.class)) {
      return cb.lessThanOrEqualTo((Expression<LocalDate>) field, (LocalDate) value);
    }
    if (field.getJavaType().isAssignableFrom(LocalDateTime.class)) {
      return cb.lessThanOrEqualTo((Expression<LocalDateTime>) field, (LocalDateTime) value);
    }
    if (field.getJavaType().isAssignableFrom(ZonedDateTime.class)) {
      return cb.lessThanOrEqualTo((Expression<ZonedDateTime>) field, (ZonedDateTime) value);
    }
    throw new UnsupportedSpecificationOperationException(
        "LessThanOrEqual isn't supported for field type [" + field.getJavaType() + "]");
  }

  /**
   * Builds greatherThanOrEqual predicate
   * 
   * @param cb    The JPA criteria builder to use
   * @param field The field to use
   * @param value The value to use
   * 
   * @return the predicate
   * 
   * @throws UnsupportedSpecificationOperationException If the field don't support
   *                                                    greatherThanOrEqual
   */
  @SuppressWarnings("unchecked")
  protected Predicate buildGreaterThanOrEqual(CriteriaBuilder cb, Expression<?> field, Object value)
      throws UnsupportedSpecificationOperationException {
    if (value == null) {
      throw new UnsupportedSpecificationOperationException("GreatherThanOrEqual don't support null values");
    }
    if (field.getJavaType().isAssignableFrom(Integer.class) || field.getJavaType().isAssignableFrom(int.class)) {
      return cb.greaterThanOrEqualTo((Expression<Integer>) field, (Integer) value);
    }
    if (field.getJavaType().isAssignableFrom(Long.class) || field.getJavaType().isAssignableFrom(long.class)) {
      return cb.greaterThanOrEqualTo((Expression<Long>) field, (Long) value);
    }
    if (field.getJavaType().isAssignableFrom(Float.class) || field.getJavaType().isAssignableFrom(float.class)) {
      return cb.greaterThanOrEqualTo((Expression<Float>) field, (Float) value);
    }
    if (field.getJavaType().isAssignableFrom(LocalDate.class)) {
      return cb.greaterThanOrEqualTo((Expression<LocalDate>) field, (LocalDate) value);
    }
    if (field.getJavaType().isAssignableFrom(LocalDateTime.class)) {
      return cb.greaterThanOrEqualTo((Expression<LocalDateTime>) field, (LocalDateTime) value);
    }
    if (field.getJavaType().isAssignableFrom(ZonedDateTime.class)) {
      return cb.greaterThanOrEqualTo((Expression<ZonedDateTime>) field, (ZonedDateTime) value);
    }
    throw new UnsupportedSpecificationOperationException(
        "GreatherThanOrEqual isn't supported for field type [" + field.getJavaType() + "]");
  }

  /**
   * Builds like predicate
   * 
   * @param cb    The JPA criteria builder to use
   * @param field The field to use
   * @param value The value to use
   * 
   * @return the predicate
   * 
   * @throws UnsupportedSpecificationOperationException If the field don't support
   *                                                    like or value is null
   */
  @SuppressWarnings("unchecked")
  protected Predicate buildLike(CriteriaBuilder cb, Expression<?> field, Object value)
      throws UnsupportedSpecificationOperationException {
    if (value == null) {
      throw new UnsupportedSpecificationOperationException("Like won't support null values");
    }
    if (field.getJavaType().isAssignableFrom(String.class)) {
      return cb.like((Expression<String>) field, (String) value);
    }
    throw new UnsupportedSpecificationOperationException(
        "Like isn't supported for field type [" + field.getJavaType() + "]");
  }

  /**
   * Builds notLike predicate
   * 
   * @param cb    The JPA criteria builder to use
   * @param field The field to use
   * @param value The value to use
   * 
   * @return the predicate
   * 
   * @throws UnsupportedSpecificationOperationException If the field don't support
   *                                                    notLike or value is null
   */
  @SuppressWarnings("unchecked")
  protected Predicate buildNotLike(CriteriaBuilder cb, Expression<?> field, Object value)
      throws UnsupportedSpecificationOperationException {
    if (value == null) {
      throw new UnsupportedSpecificationOperationException("NotLike don't support null values");
    }
    if (field.getJavaType().isAssignableFrom(String.class)) {
      return cb.notLike((Expression<String>) field, (String) value);
    }
    throw new UnsupportedSpecificationOperationException(
        "NotLike isn't supported for field type [" + field.getJavaType() + "]");
  }

  /**
   * Builds contains case-insensitive predicate
   * 
   * @param cb    The JPA criteria builder to use
   * @param field The field to use
   * @param value The value to use
   * 
   * @return the predicate
   * 
   * @throws UnsupportedSpecificationOperationException If the field don't support
   *                                                    contains or value is null
   */
  @SuppressWarnings("unchecked")
  protected Predicate buildContains(CriteriaBuilder cb, Expression<?> field, Object value)
      throws UnsupportedSpecificationOperationException {
    if (value == null) {
      throw new UnsupportedSpecificationOperationException("Contains don't support null values");
    }
    if (field.getJavaType().isAssignableFrom(String.class)) {
      return cb.like(cb.lower((Expression<String>) field), "%" + ((String) value).toLowerCase() + "%");
    }
    throw new UnsupportedSpecificationOperationException(
        "Contains isn't supported for field type [" + field.getJavaType() + "]");
  }

  /**
   * Builds notContains case-insensitive predicate
   * 
   * @param cb    The JPA criteria builder to use
   * @param field The field to use
   * @param value The value to use
   * 
   * @return the predicate
   * 
   * @throws UnsupportedSpecificationOperationException If the field don't support
   *                                                    notContains or value is
   *                                                    null
   */
  @SuppressWarnings("unchecked")
  protected Predicate buildNotContains(CriteriaBuilder cb, Expression<?> field, Object value)
      throws UnsupportedSpecificationOperationException {
    if (value == null) {
      throw new UnsupportedSpecificationOperationException("NotContains don't support null values");
    }
    if (field.getJavaType().isAssignableFrom(String.class)) {
      return cb.notLike(cb.lower((Expression<String>) field), "%" + ((String) value).toLowerCase() + "%");
    }
    throw new UnsupportedSpecificationOperationException(
        "NotContains isn't supported for field type [" + field.getJavaType() + "]");
  }

  /**
   * Builds lessThan predicate
   * 
   * @param cb    The JPA criteria builder to use
   * @param field The field to use
   * @param value The value to use
   * 
   * @return the predicate
   * 
   * @throws UnsupportedSpecificationOperationException If the field don't support
   *                                                    lessThan or value is null
   */
  @SuppressWarnings("unchecked")
  protected Predicate buildLessThan(CriteriaBuilder cb, Expression<?> field, Object value)
      throws UnsupportedSpecificationOperationException {
    if (value == null) {
      throw new UnsupportedSpecificationOperationException("LessThan don't support null values");
    }
    if (field.getJavaType().isAssignableFrom(Integer.class) || field.getJavaType().isAssignableFrom(int.class)) {
      return cb.lessThan((Expression<Integer>) field, (Integer) value);
    }
    if (field.getJavaType().isAssignableFrom(Long.class) || field.getJavaType().isAssignableFrom(long.class)) {
      return cb.lessThan((Expression<Long>) field, (Long) value);
    }
    if (field.getJavaType().isAssignableFrom(Float.class) || field.getJavaType().isAssignableFrom(float.class)) {
      return cb.lessThan((Expression<Float>) field, (Float) value);
    }
    if (field.getJavaType().isAssignableFrom(LocalDate.class)) {
      return cb.lessThan((Expression<LocalDate>) field, (LocalDate) value);
    }
    if (field.getJavaType().isAssignableFrom(LocalDateTime.class)) {
      return cb.lessThan((Expression<LocalDateTime>) field, (LocalDateTime) value);
    }
    if (field.getJavaType().isAssignableFrom(ZonedDateTime.class)) {
      return cb.lessThan((Expression<ZonedDateTime>) field, (ZonedDateTime) value);
    }
    throw new UnsupportedSpecificationOperationException(
        "LessThan isn't supported for field type [" + field.getJavaType() + "]");
  }

  /**
   * Builds greatherThan predicate
   * 
   * @param cb    The JPA criteria builder to use
   * @param field The field to use
   * @param value The value to use
   * 
   * @return the predicate
   * 
   * @throws UnsupportedSpecificationOperationException If the field don't support
   *                                                    greatherThan or value is
   *                                                    null
   */
  @SuppressWarnings("unchecked")
  protected Predicate buildGreaterThan(CriteriaBuilder cb, Expression<?> field, Object value)
      throws UnsupportedSpecificationOperationException {
    if (value == null) {
      throw new UnsupportedSpecificationOperationException("GreaterThan don't support null values");
    }
    if (field.getJavaType().isAssignableFrom(Integer.class) || field.getJavaType().isAssignableFrom(int.class)) {
      return cb.greaterThan((Expression<Integer>) field, (Integer) value);
    }
    if (field.getJavaType().isAssignableFrom(Long.class) || field.getJavaType().isAssignableFrom(long.class)) {
      return cb.greaterThan((Expression<Long>) field, (Long) value);
    }
    if (field.getJavaType().isAssignableFrom(Float.class) || field.getJavaType().isAssignableFrom(float.class)) {
      return cb.greaterThan((Expression<Float>) field, (Float) value);
    }
    if (field.getJavaType().isAssignableFrom(LocalDate.class)) {
      return cb.greaterThan((Expression<LocalDate>) field, (LocalDate) value);
    }
    if (field.getJavaType().isAssignableFrom(LocalDateTime.class)) {
      return cb.greaterThan((Expression<LocalDateTime>) field, (LocalDateTime) value);
    }
    if (field.getJavaType().isAssignableFrom(ZonedDateTime.class)) {
      return cb.greaterThan((Expression<ZonedDateTime>) field, (ZonedDateTime) value);
    }
    throw new UnsupportedSpecificationOperationException(
        "GreaterThan isn't supported for field type [" + field.getJavaType() + "]");
  }
}