package org.crue.hercules.sgi.framework.i18n;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * SGI Language
 */
public enum Language {
  /* Code lang español */
  ES("es"),
  /* Code lang inglés */
  EN("en"),
  /* Code lang euskera */
  EU("eu");

  private String code;
  private Locale locale;

  private Language(String code) {
    this.code = code;
    this.locale = new Locale.Builder().setLanguage(code).build();
  }

  @JsonValue
  public String getCode() {
    return code;
  }

  public Locale getLocale() {
    return locale;
  }

  public static Language fromCode(String code) {
    for (Language lang : Language.values()) {
      if (lang.code.equals(code)) {
        return lang;
      }
    }
    return null;
  }
}
