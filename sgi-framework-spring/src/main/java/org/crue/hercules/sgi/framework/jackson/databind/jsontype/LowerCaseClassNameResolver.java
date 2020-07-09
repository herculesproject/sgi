package org.crue.hercules.sgi.framework.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;

/**
 * Implementation of {@link TypeIdResolver}.
 * <p>
 * Note that instances created to be constructed from annotations
 * ({@link com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver}) are
 * always created using no-arguments constructor; protected constructor is only
 * used sub-classes.
 */
public class LowerCaseClassNameResolver extends TypeIdResolverBase {

  /**
   * Method called to serialize type of the type of given value as a String to
   * include in serialized JSON content.
   * 
   * @param value the value whos type must be serialized
   * @return String the serialized type of given value
   */
  @Override
  public String idFromValue(Object value) {
    return value.getClass().getSimpleName().toLowerCase();
  }

  /**
   * Alternative method used for determining type from combination of value and
   * type, using suggested type (that serializer provides) and possibly value of
   * that type. Most common implementation will use suggested type as is.
   *
   * @param value         the value whos type must be serialized
   * @param suggestedType suggested type (that serializer provides)
   * @return String the serialized type of given value
   */
  @Override
  public String idFromValueAndType(Object value, Class<?> suggestedType) {
    return idFromValue(value);
  }

  /**
   * Accessor for mechanism that this resolver uses for determining type id from
   * type. Mostly informational; not required to be called or used.
   *
   * @return Id JsonTypeInfo.Id.CUSTOM
   */
  @Override
  public JsonTypeInfo.Id getMechanism() {
    return JsonTypeInfo.Id.CUSTOM;
  }
}