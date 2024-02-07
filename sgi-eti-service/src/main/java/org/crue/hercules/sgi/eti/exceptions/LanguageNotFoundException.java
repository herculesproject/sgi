package org.crue.hercules.sgi.eti.exceptions;

/**
 * LanguageNotFoundException
 */
public class LanguageNotFoundException extends EtiNotFoundException {
  /**
   * Serial version.
   */
  private static final long serialVersionUID = 1L;

  public LanguageNotFoundException(String code) {
    super("Language " + code + " does not exist.");
  }
}