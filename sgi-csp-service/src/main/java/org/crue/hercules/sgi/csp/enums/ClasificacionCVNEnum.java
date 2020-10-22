package org.crue.hercules.sgi.csp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumerado clasificación CVN de las convocatorias.
 *
 */
public enum ClasificacionCVNEnum {

  /** AYUDAS Y BECAS */
  AYUDAS("Ayudas y becas"),

  /** PROYECTOS COMPETITIVOS */
  COMPETITIVOS("Proyectos competitivos"),

  /** CONTRATOS */
  CONTRATOS("Contratos, convenios, proyectos no competitivos"),

  /** ESTANCIAS */
  ESTANCIAS("Estancias");

  /**
   * The value.
   */
  private String value;

  /**
   * Private constructor to asign the value.
   *
   * @param the value.
   */
  private ClasificacionCVNEnum(final String value) {
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
