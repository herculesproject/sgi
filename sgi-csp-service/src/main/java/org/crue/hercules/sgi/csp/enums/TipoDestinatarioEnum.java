package org.crue.hercules.sgi.csp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumerado destinatarios de las convocatorias.
 *
 */
public enum TipoDestinatarioEnum {

  /** INDIVIDUAL */
  INDIVIDUAL("Individual"),

  /** EQUIPO PROYECTO */
  EQUIPO_PROYECTO("Equipo de proyecto"),

  /** GRUPO INVESTIGACION */
  GRUPO_INVESTIGACION("Grupo de investigación");

  /**
   * The value.
   */
  private String value;

  /**
   * Private constructor to asign the value.
   *
   * @param the value.
   */
  private TipoDestinatarioEnum(final String value) {
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
