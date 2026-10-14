package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO de salida con el nombre y el valor de un parámetro de configuración.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigParamOutput implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Clave del parámetro */
  private String name;
  /** Valor del parámetro como texto, vacío si no está informado */
  private String value;
}
