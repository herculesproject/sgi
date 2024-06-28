package org.crue.hercules.sgi.rep.util;

import java.util.Locale;

import org.springframework.util.ObjectUtils;

import fr.opensagres.poi.xwpf.converter.core.utils.StringUtils;

public class SgiLocaleHelper {

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

  private SgiLocaleHelper() {
  }

  public static String getLang(Locale locale) {
    if (!ObjectUtils.isEmpty(locale.getCountry())) {
      return locale.getCountry().toLowerCase();
    } else {
      return locale.getLanguage();
    }
  }

  public static Locale newLocale(String lang) {
    if (StringUtils.isNotEmpty(lang) && lang.equals(Language.EU.getCode())) {
      return new Locale(Language.ES.getCode(), Language.EU.getCode());
    } else {
      return new Locale(lang);
    }
  }
}