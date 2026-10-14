package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.eti.model.PeticionEvaluacionUnidadVinculacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para la actualización de las
 * {@link PeticionEvaluacionUnidadVinculacion}.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeticionEvaluacionUnidadVinculacionInput implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Referencia a la unidad de vinculación en el SGO. */
  @NotNull
  @Size(max = PeticionEvaluacionUnidadVinculacion.UNIDAD_VINCULACION_REF_LENGTH)
  private String unidadVinculacionRef;

}
