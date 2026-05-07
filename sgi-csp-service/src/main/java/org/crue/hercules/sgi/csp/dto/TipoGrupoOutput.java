package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.util.Collection;

import org.crue.hercules.sgi.csp.model.TipoGrupoDescripcion;
import org.crue.hercules.sgi.csp.model.TipoGrupoNombre;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoGrupoOutput implements Serializable {

  private Long id;
  private Collection<TipoGrupoNombre> nombre;
  private Collection<TipoGrupoDescripcion> descripcion;
  private Boolean activo;

}
