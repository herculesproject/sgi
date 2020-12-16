package org.crue.hercules.sgi.csp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumerado tipo horas anuales
 */
public enum TipoHorasAnualesEnum {

  /** Valor fijo */
  VALOR_FIJO("Valor fijo"),
  /** Reales */
  REALES("Reales (TS)"),
  /** Por categoría */
  POR_CATEGORIA("Por categoría");

  /**
   * The value.
   */
  private String value;

  /**
   * Private constructor to asign the value.
   *
   * @param the value.
   */
  private TipoHorasAnualesEnum(final String value) {
    this.value = value;
  }

  /**
   * Return the value.
   *
   * @return the value
   */
  @JsonValue
  public String getValue() {
    return this.value;
  }

}