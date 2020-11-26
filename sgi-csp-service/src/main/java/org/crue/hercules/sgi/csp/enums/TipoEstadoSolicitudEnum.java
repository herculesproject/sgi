package org.crue.hercules.sgi.csp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumerado tipo estado de las solicitudes.
 *
 */
public enum TipoEstadoSolicitudEnum {

  BORRADOR("Borrador"), //
  PRESENTADA("Presentada"), //
  ADMITIDA_PROVISIONAL("Admitida provisional"), //
  EXCLUIDA_PROVISIONAL("Excluida provisional"), //
  ALEGADA_ADMISION("Alegada admisión"), //
  EXCLUIDA("Excluida"), //
  ADMITIDA_DEFINITIVA("Admitida definitiva"), //
  CONCECIDA_PROVISIONAL("Concedida provisional"), //
  DENEGADA_PROVISIONAL("Denegada provisional"), //
  ALEGADA_CONCESION("Alegada concesión"), //
  DESISTIDA("Desistida"), //
  CONCECIDA("Concedida"), //
  DENEGADA("Denegada");

  /**
   * The value.
   */
  private String value;

  /**
   * Private constructor to asign the value.
   *
   * @param the value.
   */
  private TipoEstadoSolicitudEnum(final String value) {
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
