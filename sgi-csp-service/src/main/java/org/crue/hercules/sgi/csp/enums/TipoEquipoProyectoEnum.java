package org.crue.hercules.sgi.csp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumerado tipo equipo proyecto.
 *
 */
public enum TipoEquipoProyectoEnum {

  /** INVESTIGACION */
  INVESTIGACION("Equipo de investigación"),
  /** TRABAJO */
  TRABAJO("Equipo de trabajo");

  /**
   * The value.
   */
  private String value;

  /**
   * Private constructor to asign the value.
   *
   * @param the value.
   */
  private TipoEquipoProyectoEnum(final String value) {
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
