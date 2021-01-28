package org.crue.hercules.sgi.csp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumerado tipos de propiedad resultado
 */
public enum TipoPropiedadResultadosEnum {

  /** Sin resultados */
  SIN_RESULTADOS("Sin resultados"),
  /** Universidad */
  UNIVERSIDAD("Universidad"),
  /** Entidad financiadora */
  ENTIDAD_FINANCIADORA("Entidad financiadora"),
  /** Compartida */
  COMPARTIDA("Compartida");

  /**
   * The value.
   */
  private String value;

  /**
   * Private constructor to asign the value.
   *
   * @param the value.
   */
  private TipoPropiedadResultadosEnum(final String value) {
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