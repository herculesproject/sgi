package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * GrupoRelacionInstitucionalOutput
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoRelacionInstitucionalOutput implements Serializable {

  private Long id;
  private Long grupoId;
  private String entidadRef;
  private String institucion;

}
