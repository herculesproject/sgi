package org.crue.hercules.sgi.csp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumerado tipo estado de las convocatorias.
 *
 */
public enum TipoEstadoConvocatoriaEnum {

  /** BORRADOR */
  BORRADOR("Borrador"),

  /** REGISTRADA */
  REGISTRADA("Registrada");

  /**
   * The value.
   */
  private String value;

  /**
   * Private constructor to asign the value.
   *
   * @param the value.
   */
  private TipoEstadoConvocatoriaEnum(final String value) {
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
