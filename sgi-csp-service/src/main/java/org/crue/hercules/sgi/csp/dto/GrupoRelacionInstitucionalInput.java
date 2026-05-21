package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.GrupoRelacionInstitucional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * GrupoRelacionInstitucionalInput
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoRelacionInstitucionalInput implements Serializable {

  @NotNull
  private Long grupoId;

  @Size(max = GrupoRelacionInstitucional.ENTIDAD_REF_LENGTH)
  private String entidadRef;

  @Size(max = GrupoRelacionInstitucional.INSTITUCION_LENGTH)
  private String institucion;

}
