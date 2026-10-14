package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;

import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionUnidadVinculacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para una {@link PeticionEvaluacionUnidadVinculacion}.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeticionEvaluacionUnidadVinculacionOutput implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Id */
  private Long id;
  /** Identificador de la {@link PeticionEvaluacion} */
  private Long peticionEvaluacionId;
  /** Referencia a la unidad de vinculación en el SGO. */
  private String unidadVinculacionRef;

}
