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

import lombok.extern.slf4j.Slf4j;

/**
 * Base class for dynamic specification implementations based on string paths.
 * 
 * @param <T> The entity type
 */
@Slf4j
public abstract class StringDynamicSpecification<T> implements Specification<T> {
  private static final long serialVersionUID = 1L;

  /**
   * Split a path separated by <code>.</code> in field names
   * 
   * @param stringPath the path to split
   * @return list of field names
   */
  private List<String> parseStringPath(String stringPath) {
    log.debug("parseStringPath(String stringPath) - start");
    List<String> fields = new ArrayList<>();
    if (StringUtils.isBlank(stringPath)) {
      log.warn("Empty string path");
      log.debug("parseStringPath(String stringPath) - end");
      return fields;
    }
    for (String field : stringPath.split("\\.")) {
      fields.add(field);
    }
    log.debug("parseStringPath(String stringPath) - end");
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
    log.debug("isPluralAttribute(Path<?> path) - start");
    Class<?> clazz = path.getJavaType();
    boolean returnValue = clazz.isAssignableFrom(Collection.class) || clazz.isAssignableFrom(List.class)
        || clazz.isAssignableFrom(Map.class) || clazz.isAssignableFrom(Set.class);
    log.debug("isPluralAttribute(Path<?> path) - end");
    return returnValue;
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
    log.debug("getJoin(Root<?> root, List<String> attributes) - start");
    Join<X, Y> join = null;
    for (String attribute : attributes) {
      if (join == null) {
        join = root.join(attribute);
      } else {
        join = join.join(attribute);
      }
    }
    log.debug("getJoin(Root<?> root, List<String> attributes) - end");
    return join;
  }

  /**
   * Gets the {@link Path} recursively from string path that represent field name
   * or nested field names separated by dot (<code>.</code>) For example:
   * <code>field.nestedField.nestedField</code>
   *
   * @param root       Entity root to scan
   * @param stringPath String path representation to get
   * @param <Y>        the Type
   * @return the Path
   * 
   * @throws IllegalSpecificationArgumentException If the field name don't exists
   */
  protected <Y> Path<Y> getPath(Root<?> root, String stringPath) throws IllegalSpecificationArgumentException {
    log.debug("getPath(Root<?> root, String stringPath) - start");
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
      log.error("The path " + stringPath + " don't exists in entity " + root.getJavaType().getSimpleName(), e);
      throw new IllegalSpecificationArgumentException(
          "The path " + stringPath + " don't exists in entity " + root.getJavaType().getSimpleName(), e);
    }

    log.debug("getPath(Root<?> root, String stringPath) - end");
    return path;
  }

  /**
   * Convert the string representation of an value to field class type.
   * 
   * @param clazz The target class of the field
   * @param value The value to convert
   * 
   * @return the value converted to the target class
   * 
   * @throws IllegalSpecificationArgumentException If the value cannot be
   *                                               converted
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected Object stringToFieldClass(Class<? extends Object> clazz, String value)
      throws IllegalSpecificationArgumentException {
    log.debug("stringToFieldClass(Class<? extends Object> clazz, String value) - start");
    if (value == null) {
      log.warn("No value provided");
      log.debug("stringToFieldClass(Class<? extends Object> clazz, String value) - end");
      return null;
    }
    try {
      if (clazz.isAssignableFrom(Integer.class)) {
        Integer returnValue = Integer.valueOf(value.trim());
        log.debug("stringToFieldClass(Class<? extends Object> clazz, String value) - end");
        return returnValue;
      }
      if (clazz.isAssignableFrom(int.class)) {
        Integer returnValue = Integer.parseInt(value);
        log.debug("stringToFieldClass(Class<? extends Object> clazz, String value) - end");
        return returnValue;
      }
      if (clazz.isAssignableFrom(Long.class)) {
        Long returnValue = Long.valueOf(value);
        log.debug("stringToFieldClass(Class<? extends Object> clazz, String value) - end");
        return returnValue;
      }
      if (clazz.isAssignableFrom(long.class)) {
        Long returnValue = Long.parseLong(value);
        log.debug("stringToFieldClass(Class<? extends Object> clazz, String value) - end");
        return returnValue;
      }
      if (clazz.isAssignableFrom(Float.class)) {
        Float returnValue = Float.valueOf(value);
        log.debug("stringToFieldClass(Class<? extends Object> clazz, String value) - end");
        return returnValue;
      }
      if (clazz.isAssignableFrom(float.class)) {
        Float returnValue = Float.parseFloat(value);
        log.debug("stringToFieldClass(Class<? extends Object> clazz, String value) - end");
        return returnValue;
      }
    } catch (NumberFormatException e) {
      log.error(value + " cannot be assigned to " + clazz.getSimpleName(), e);
      throw new IllegalSpecificationArgumentException(value + " cannot be assigned to " + clazz.getSimpleName(), e);
    }
    if (clazz.isAssignableFrom(Boolean.class)) {
      if (StringUtils.isBlank(value)) {
        log.warn("No value provided");
        log.debug("stringToFieldClass(Class<? extends Object> clazz, String value) - end");
        return null;
      }
      if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
        log.error(value + " cannot be assigned to " + clazz.getSimpleName());
        throw new IllegalSpecificationArgumentException(value + " cannot be assigned to " + clazz.getSimpleName());
      }
      Boolean returnValue = Boolean.valueOf(value);
      log.debug("stringToFieldClass(Class<? extends Object> clazz, String value) - end");
      return returnValue;
    }
    if (clazz.isAssignableFrom(boolean.class)) {
      if (StringUtils.isBlank(value) || (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value))) {
        log.error(value + " cannot be assigned to " + clazz.getSimpleName());
        throw new IllegalSpecificationArgumentException(value + " cannot be assigned to " + clazz.getSimpleName());
      }
      Boolean returnValue = Boolean.parseBoolean(value);
      log.debug("stringToFieldClass(Class<? extends Object> clazz, String value) - end");
      return returnValue;
    }
    if (clazz.isEnum()) {
      try {
        Enum returnValue = Enum.valueOf((Class<Enum>) clazz, value);
        log.debug("stringToFieldClass(Class<? extends Object> clazz, String value) - end");
        return returnValue;
      } catch (IllegalArgumentException e) {
        log.error(value + " cannot be assigned to " + clazz.getSimpleName(), e);
        throw new IllegalSpecificationArgumentException(value + " cannot be assigned to " + clazz.getSimpleName(), e);
      }
    }
    try {
      if (clazz.isAssignableFrom(LocalDateTime.class)) {
        LocalDateTime returnValue = LocalDateTime.parse(value);
        log.debug("stringToFieldClass(Class<? extends Object> clazz, String value) - end");
        return returnValue;
      }
      if (clazz.isAssignableFrom(LocalDate.class)) {
        LocalDate returnValue = LocalDate.parse(value);
        log.debug("stringToFieldClass(Class<? extends Object> clazz, String value) - end");
        return returnValue;
      }
      if (clazz.isAssignableFrom(ZonedDateTime.class)) {
        ZonedDateTime returnValue = ZonedDateTime.parse(value);
        log.debug("stringToFieldClass(Class<? extends Object> clazz, String value) - end");
        return returnValue;
      }
    } catch (DateTimeParseException e) {
      log.error(value + " cannot be assigned to " + clazz.getSimpleName(), e);
      throw new IllegalSpecificationArgumentException(value + " cannot be assigned to " + clazz.getSimpleName(), e);
    }
    if (clazz.isAssignableFrom(String.class)) {
      log.debug("stringToFieldClass(Class<? extends Object> clazz, String value) - end");
      return value;
    }
    log.error(value + " cannot be assigned to " + clazz.getSimpleName());
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
    log.debug("buildEqual(CriteriaBuilder cb, Expression<?> field, Object value) - start");
    if (value == null) {
      log.warn("No value provided");
      log.debug("buildEqual(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return field.isNull();
    }
    log.debug("buildEqual(CriteriaBuilder cb, Expression<?> field, Object value) - end");
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
    log.debug("buildNotEqual(CriteriaBuilder cb, Expression<?> field, Object value) - start");
    if (value == null) {
      log.warn("No value provided");
      log.debug("buildNotEqual(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return field.isNotNull();
    }
    log.debug("buildNotEqual(CriteriaBuilder cb, Expression<?> field, Object value) - end");
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
    log.debug("buildLessThanOrEqual(CriteriaBuilder cb, Expression<?> field, Object value) - start");
    if (value == null) {
      log.error("LessThanOrEqual don't support null values");
      throw new UnsupportedSpecificationOperationException("LessThanOrEqual don't support null values");
    }
    if (field.getJavaType().isAssignableFrom(Integer.class) || field.getJavaType().isAssignableFrom(int.class)) {
      Predicate returnValue = cb.lessThanOrEqualTo((Expression<Integer>) field, (Integer) value);
      log.debug("buildLessThanOrEqual(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    if (field.getJavaType().isAssignableFrom(Long.class) || field.getJavaType().isAssignableFrom(long.class)) {
      Predicate returnValue = cb.lessThanOrEqualTo((Expression<Long>) field, (Long) value);
      log.debug("buildLessThanOrEqual(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    if (field.getJavaType().isAssignableFrom(Float.class) || field.getJavaType().isAssignableFrom(float.class)) {
      Predicate returnValue = cb.lessThanOrEqualTo((Expression<Float>) field, (Float) value);
      log.debug("buildLessThanOrEqual(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    if (field.getJavaType().isAssignableFrom(LocalDate.class)) {
      Predicate returnValue = cb.lessThanOrEqualTo((Expression<LocalDate>) field, (LocalDate) value);
      log.debug("buildLessThanOrEqual(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    if (field.getJavaType().isAssignableFrom(LocalDateTime.class)) {
      Predicate returnValue = cb.lessThanOrEqualTo((Expression<LocalDateTime>) field, (LocalDateTime) value);
      log.debug("buildLessThanOrEqual(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    if (field.getJavaType().isAssignableFrom(ZonedDateTime.class)) {
      Predicate returnValue = cb.lessThanOrEqualTo((Expression<ZonedDateTime>) field, (ZonedDateTime) value);
      log.debug("buildLessThanOrEqual(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    log.error("LessThanOrEqual isn't supported for field type [" + field.getJavaType() + "]");
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
    log.debug("buildGreaterThanOrEqual(CriteriaBuilder cb, Expression<?> field, Object value) - start");
    if (value == null) {
      log.error("GreatherThanOrEqual don't support null values");
      throw new UnsupportedSpecificationOperationException("GreatherThanOrEqual don't support null values");
    }
    if (field.getJavaType().isAssignableFrom(Integer.class) || field.getJavaType().isAssignableFrom(int.class)) {
      Predicate returnValue = cb.greaterThanOrEqualTo((Expression<Integer>) field, (Integer) value);
      log.debug("buildGreaterThanOrEqual(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    if (field.getJavaType().isAssignableFrom(Long.class) || field.getJavaType().isAssignableFrom(long.class)) {
      Predicate returnValue = cb.greaterThanOrEqualTo((Expression<Long>) field, (Long) value);
      log.debug("buildGreaterThanOrEqual(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    if (field.getJavaType().isAssignableFrom(Float.class) || field.getJavaType().isAssignableFrom(float.class)) {
      Predicate returnValue = cb.greaterThanOrEqualTo((Expression<Float>) field, (Float) value);
      log.debug("buildGreaterThanOrEqual(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    if (field.getJavaType().isAssignableFrom(LocalDate.class)) {
      Predicate returnValue = cb.greaterThanOrEqualTo((Expression<LocalDate>) field, (LocalDate) value);
      log.debug("buildGreaterThanOrEqual(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    if (field.getJavaType().isAssignableFrom(LocalDateTime.class)) {
      Predicate returnValue = cb.greaterThanOrEqualTo((Expression<LocalDateTime>) field, (LocalDateTime) value);
      log.debug("buildGreaterThanOrEqual(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    if (field.getJavaType().isAssignableFrom(ZonedDateTime.class)) {
      Predicate returnValue = cb.greaterThanOrEqualTo((Expression<ZonedDateTime>) field, (ZonedDateTime) value);
      log.debug("buildGreaterThanOrEqual(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    log.error("GreatherThanOrEqual isn't supported for field type [" + field.getJavaType() + "]");
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
    log.debug("buildLike(CriteriaBuilder cb, Expression<?> field, Object value) - start");
    if (value == null) {
      log.error("Like don't support null values");
      throw new UnsupportedSpecificationOperationException("Like don't support null values");
    }
    if (field.getJavaType().isAssignableFrom(String.class)) {
      Predicate returnValue = cb.like((Expression<String>) field, (String) value);
      log.debug("buildLike(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    log.error("Like isn't supported for field type [" + field.getJavaType() + "]");
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
    log.debug("buildNotLike(CriteriaBuilder cb, Expression<?> field, Object value) - start");
    if (value == null) {
      log.error("NotLike don't support null values");
      throw new UnsupportedSpecificationOperationException("NotLike don't support null values");
    }
    if (field.getJavaType().isAssignableFrom(String.class)) {
      Predicate returnValue = cb.notLike((Expression<String>) field, (String) value);
      log.debug("buildNotLike(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    log.error("NotLike isn't supported for field type [" + field.getJavaType() + "]");
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
    log.debug("buildContains(CriteriaBuilder cb, Expression<?> field, Object value) - start");
    if (value == null) {
      log.error("Contains don't support null values");
      throw new UnsupportedSpecificationOperationException("Contains don't support null values");
    }
    if (field.getJavaType().isAssignableFrom(String.class)) {
      Predicate returnValue = cb.like(cb.lower((Expression<String>) field), "%" + ((String) value).toLowerCase() + "%");
      log.debug("buildContains(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    log.error("Contains isn't supported for field type [" + field.getJavaType() + "]");
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
    log.debug("buildNotContains(CriteriaBuilder cb, Expression<?> field, Object value) - start");
    if (value == null) {
      log.error("NotContains don't support null values");
      throw new UnsupportedSpecificationOperationException("NotContains don't support null values");
    }
    if (field.getJavaType().isAssignableFrom(String.class)) {
      Predicate returnValue = cb.notLike(cb.lower((Expression<String>) field),
          "%" + ((String) value).toLowerCase() + "%");
      log.debug("buildNotContains(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    log.error("NotContains isn't supported for field type [" + field.getJavaType() + "]");
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
    log.debug("buildLessThan(CriteriaBuilder cb, Expression<?> field, Object value) - start");
    if (value == null) {
      log.error("LessThan don't support null values");
      throw new UnsupportedSpecificationOperationException("LessThan don't support null values");
    }
    if (field.getJavaType().isAssignableFrom(Integer.class) || field.getJavaType().isAssignableFrom(int.class)) {
      Predicate returnValue = cb.lessThan((Expression<Integer>) field, (Integer) value);
      log.debug("buildLessThan(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    if (field.getJavaType().isAssignableFrom(Long.class) || field.getJavaType().isAssignableFrom(long.class)) {
      Predicate returnValue = cb.lessThan((Expression<Long>) field, (Long) value);
      log.debug("buildLessThan(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    if (field.getJavaType().isAssignableFrom(Float.class) || field.getJavaType().isAssignableFrom(float.class)) {
      Predicate returnValue = cb.lessThan((Expression<Float>) field, (Float) value);
      log.debug("buildLessThan(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    if (field.getJavaType().isAssignableFrom(LocalDate.class)) {
      Predicate returnValue = cb.lessThan((Expression<LocalDate>) field, (LocalDate) value);
      log.debug("buildLessThan(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    if (field.getJavaType().isAssignableFrom(LocalDateTime.class)) {
      Predicate returnValue = cb.lessThan((Expression<LocalDateTime>) field, (LocalDateTime) value);
      log.debug("buildLessThan(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    if (field.getJavaType().isAssignableFrom(ZonedDateTime.class)) {
      Predicate returnValue = cb.lessThan((Expression<ZonedDateTime>) field, (ZonedDateTime) value);
      log.debug("buildLessThan(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    log.error("LessThan isn't supported for field type [" + field.getJavaType() + "]");
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
    log.debug("buildGreaterThan(CriteriaBuilder cb, Expression<?> field, Object value) - start");
    if (value == null) {
      log.error("GreaterThan don't support null values");
      throw new UnsupportedSpecificationOperationException("GreaterThan don't support null values");
    }
    if (field.getJavaType().isAssignableFrom(Integer.class) || field.getJavaType().isAssignableFrom(int.class)) {
      Predicate returnValue = cb.greaterThan((Expression<Integer>) field, (Integer) value);
      log.debug("buildGreaterThan(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    if (field.getJavaType().isAssignableFrom(Long.class) || field.getJavaType().isAssignableFrom(long.class)) {
      Predicate returnValue = cb.greaterThan((Expression<Long>) field, (Long) value);
      log.debug("buildGreaterThan(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    if (field.getJavaType().isAssignableFrom(Float.class) || field.getJavaType().isAssignableFrom(float.class)) {
      Predicate returnValue = cb.greaterThan((Expression<Float>) field, (Float) value);
      log.debug("buildGreaterThan(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    if (field.getJavaType().isAssignableFrom(LocalDate.class)) {
      Predicate returnValue = cb.greaterThan((Expression<LocalDate>) field, (LocalDate) value);
      log.debug("buildGreaterThan(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    if (field.getJavaType().isAssignableFrom(LocalDateTime.class)) {
      Predicate returnValue = cb.greaterThan((Expression<LocalDateTime>) field, (LocalDateTime) value);
      log.debug("buildGreaterThan(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    if (field.getJavaType().isAssignableFrom(ZonedDateTime.class)) {
      Predicate returnValue = cb.greaterThan((Expression<ZonedDateTime>) field, (ZonedDateTime) value);
      log.debug("buildGreaterThan(CriteriaBuilder cb, Expression<?> field, Object value) - end");
      return returnValue;
    }
    log.error("GreaterThan isn't supported for field type [" + field.getJavaType() + "]");
    throw new UnsupportedSpecificationOperationException(
        "GreaterThan isn't supported for field type [" + field.getJavaType() + "]");
  }
}