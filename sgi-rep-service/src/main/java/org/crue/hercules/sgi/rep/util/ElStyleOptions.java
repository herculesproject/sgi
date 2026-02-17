package org.crue.hercules.sgi.rep.util;

/**
 * Representa las opciones de estilo extraídas de una expresión EL.
 */
public class ElStyleOptions {

  private final String realEl;
  private final boolean italic;
  private final boolean clarify;

  public ElStyleOptions(String realEl, boolean italic, boolean clarify) {
    this.realEl = realEl;
    this.italic = italic;
    this.clarify = clarify;
  }

  public String getRealEl() {
    return realEl;
  }

  public boolean isItalic() {
    return italic;
  }

  public boolean isClarify() {
    return clarify;
  }
}
