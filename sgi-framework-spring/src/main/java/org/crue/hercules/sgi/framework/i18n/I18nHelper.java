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
   * is null
   * or dosen't contains a value for requested languague <code>null</code> is
   * returned.
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
    if (field == null) {
      return null;
    }
    Optional<? extends I18nFieldValue> match = field.stream()
        .filter(value -> language == value.getLang()).findFirst();
    if (match.isPresent()) {
      return match.get().getValue();
    }
    return null;
  }
}
