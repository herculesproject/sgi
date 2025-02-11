package org.crue.hercules.sgi.csp.dto;

import java.time.Instant;
import java.util.Collection;

import org.crue.hercules.sgi.csp.model.ProyectoTitulo;

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
public class ProyectoResumenOutput {
  private Long id;
  private Collection<ProyectoTitulo> titulo;
  private String acronimo;
  private String codigoExterno;
  private Instant fechaInicio;
  private Instant fechaFin;
  private Instant fechaFinDefinitiva;
}
