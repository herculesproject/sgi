package org.crue.hercules.sgi.csp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumerado tipo formulario solicitud.
 *
 */
public enum TipoFormularioSolicitudEnum {

  /** ESTANDAR */
  ESTANDAR("Estándar"),
  /** RRHH */
  RRHH("RRHH (predoctoral, posdoctoral)"),
  /** AYUDAS A GRUPOS */
  AYUDAS_GRUPOS("Ayudas a grupos");

  /**
   * The value.
   */
  private String value;

  /**
   * Private constructor to asign the value.
   *
   * @param the value.
   */
  private TipoFormularioSolicitudEnum(final String value) {
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
