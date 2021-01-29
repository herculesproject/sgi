package org.crue.hercules.sgi.csp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumerado tipo prórroga.
 *
 */
public enum TipoProrrogaEnum {

  /** TIEMPO */
  TIEMPO("Tiempo"),

  /** IMPORTE */
  IMPORTE("Importe"),

  /** TIEMPO e IMPORTE */
  TIEMPO_IMPORTE("Tiempo e importe");

  /**
   * The value.
   */
  private String value;

  /**
   * Private constructor to asign the value.
   *
   * @param the value.
   */
  private TipoProrrogaEnum(final String value) {
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
