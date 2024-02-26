package org.crue.hercules.sgi.csp.util;

import java.util.Locale;

import org.springframework.util.ObjectUtils;

public class SgiLocaleHelper {

  private SgiLocaleHelper() {
  }

  public static String getLang(Locale locale) {
    if (!ObjectUtils.isEmpty(locale.getCountry())) {
      return locale.getCountry().toLowerCase();
    } else {
      return locale.getLanguage();
    }
  }
}