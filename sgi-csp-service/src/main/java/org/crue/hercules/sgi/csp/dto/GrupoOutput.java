package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;

import org.crue.hercules.sgi.csp.model.GrupoNombre;
import org.crue.hercules.sgi.csp.model.GrupoResumen;

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
public class GrupoOutput implements Serializable {

  private Long id;
  private String acronimo;
  private Collection<GrupoNombre> nombre;
  private Instant fechaInicio;
  private Instant fechaFin;
  private String email;
  private String proyectoSgeRef;
  private Long solicitudId;
  private String codigo;
  private TipoGrupoOutput tipoGrupo;
  private Boolean especialInvestigacion;
  private Collection<GrupoResumen> resumen;
  private boolean activo;

}
