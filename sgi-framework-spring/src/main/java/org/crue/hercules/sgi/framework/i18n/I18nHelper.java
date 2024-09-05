package org.crue.hercules.sgi.framework.i18n;

import java.util.Collection;
import java.util.Optional;

import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;

/**
 * Helper class with method related to i18n management
 */
public class I18nHelper {

  private I18nHelper() {
  }

  /**
   * Get the value of an i18n field that match the current language. If the field
   * is null or dosen't contains a value for requested languague <code>null</code>
   * is returned.
   * 
   * @see SgiLocaleContextHolder#getLanguage()
   * @see #getValueForLanguage(Collection, Language)
   * 
   * @param field the field where value would be extracted
   * @return the value
   */
  public static String getValueForCurrentLanguage(Collection<? extends I18nFieldValue> field) {
    return getValueForLanguage(field, SgiLocaleContextHolder.getLanguage());
  }

  /**
   * Get the value of an i18n field that match the language. If the field is null
   * or dosen't contains a value for requested languague <code>null</code> is
   * returned.
   * 
   * @param field    the field where value would be extracted
   * @param language the language to extract
   * @return the value
   */
  public static String getValueForLanguage(Collection<? extends I18nFieldValue> field, Language language) {
    Optional<? extends I18nFieldValue> match = getFieldValueForLanguage(field, language);
    if (match.isPresent()) {
      return match.get().getValue();
    }
    return null;
  }

  /**
   * Get an i18n field that match the current language. Return value is empty if
   * the field is null or not contains the current language.
   * 
   * @see SgiLocaleContextHolder#getLanguage()
   * @see #getFieldValueForLanguage(Collection, Language)
   * 
   * @param <T>   the type of field, must extends {@link I18nFieldValue}
   * @param field the i18n field where field would be extracted
   * @return the field
   */
  public static <T extends I18nFieldValue> Optional<T> getFieldValueForCurrentLanguage(Collection<T> field) {
    return getFieldValueForLanguage(field, SgiLocaleContextHolder.getLanguage());
  }

  /**
   * Get an i18n field that match the requested language. Return value is empty if
   * the field is null or not contains the requested language.
   * 
   * @param <T>      the type of field, must extends {@link I18nFieldValue}
   * @param field    the i18n field where field would be extracted
   * @param language the language to extract
   * @return the field
   */
  public static <T extends I18nFieldValue> Optional<T> getFieldValueForLanguage(Collection<T> field,
      Language language) {
    if (field == null) {
      return Optional.empty();
    }
    return field.stream()
        .filter(value -> language == value.getLang()).findFirst();
  }
}
