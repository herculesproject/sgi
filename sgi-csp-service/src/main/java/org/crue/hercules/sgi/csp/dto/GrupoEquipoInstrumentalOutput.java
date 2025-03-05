package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.util.Collection;

import org.crue.hercules.sgi.csp.model.GrupoEquipoInstrumentalDescripcion;
import org.crue.hercules.sgi.csp.model.GrupoEquipoInstrumentalNombre;

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
public class GrupoEquipoInstrumentalOutput implements Serializable {

  private Long id;
  private String numRegistro;
  private Collection<GrupoEquipoInstrumentalNombre> nombre;
  private Collection<GrupoEquipoInstrumentalDescripcion> descripcion;
  private Long grupoId;

}
