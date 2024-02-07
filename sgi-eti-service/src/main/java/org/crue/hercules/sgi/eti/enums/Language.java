package org.crue.hercules.sgi.eti.enums;

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

  private Language(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
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
