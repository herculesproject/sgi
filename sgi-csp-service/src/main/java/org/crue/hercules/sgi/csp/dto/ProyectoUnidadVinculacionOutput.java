package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

import org.crue.hercules.sgi.csp.model.ProyectoUnidadVinculacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para una {@link ProyectoUnidadVinculacion}.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoUnidadVinculacionOutput implements Serializable {

  /** Identificador de la unidad de vinculación. */
  private Long id;

  /** Referencia a la unidad de vinculación en el SGO. */
  private String unidadVinculacionRef;

}
