package org.crue.hercules.sgi.csp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumerado tipo estado proyecto
 */
public enum TipoEstadoProyectoEnum {

  /** Borrador */
  BORRADOR("Borrador"),
  /** Provisional */
  PROVISIONAL("Provisional"),
  /** Abierto */
  ABIERTO("Abierto"),
  /** Finalizado */
  FINALIZADO("Finalizado"),
  /** Cancelado */
  CANCELADO("Cancelado");

  /**
   * The value.
   */
  private String value;

  /**
   * Private constructor to asign the value.
   *
   * @param the value.
   */
  private TipoEstadoProyectoEnum(final String value) {
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