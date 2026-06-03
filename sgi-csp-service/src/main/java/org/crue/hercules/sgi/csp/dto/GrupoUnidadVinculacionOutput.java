package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

import org.crue.hercules.sgi.csp.model.GrupoUnidadVinculacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para una {@link GrupoUnidadVinculacion}.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoUnidadVinculacionOutput implements Serializable {

  private Long id;
  private String unidadVinculacionRef;

}
