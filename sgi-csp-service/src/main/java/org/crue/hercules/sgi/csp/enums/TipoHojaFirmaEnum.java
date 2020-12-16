package org.crue.hercules.sgi.csp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumerado tipo hoja firma
 */
public enum TipoHojaFirmaEnum {

  /** TODO */
  N("N/A");

  /**
   * The value.
   */
  private String value;

  /**
   * Private constructor to asign the value.
   *
   * @param the value.
   */
  private TipoHojaFirmaEnum(final String value) {
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