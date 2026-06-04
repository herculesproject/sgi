package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.ProyectoUnidadVinculacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para la creación/actualización de un
 * {@link ProyectoUnidadVinculacion}.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoUnidadVinculacionInput implements Serializable {

  /** Referencia a la unidad de vinculación en el SGO. */
  @NotNull
  @Size(max = ProyectoUnidadVinculacion.UNIDAD_VINCULACION_REF_LENGTH)
  private String unidadVinculacionRef;

}
