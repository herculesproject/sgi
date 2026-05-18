package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.util.Collection;

import org.crue.hercules.sgi.csp.model.TipoDescriptorGrupoNombre;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * TipoDescriptorGrupoOutput
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoDescriptorGrupoOutput implements Serializable {

  private Long id;
  private Collection<TipoDescriptorGrupoNombre> nombre;
  private Boolean activo;

}
