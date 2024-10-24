package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;

import org.crue.hercules.sgi.csp.model.TipoFaseNombre;

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
public class ConvocatoriaFaseOutput implements Serializable {
  private Long id;
  private Long convocatoriaId;
  private TipoFase tipoFase;
  private Instant fechaInicio;
  private Instant fechaFin;
  private String observaciones;
  private ConvocatoriaFaseAvisoOutput aviso1;
  private ConvocatoriaFaseAvisoOutput aviso2;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TipoFase implements Serializable {
    private Long id;
    private Collection<TipoFaseNombre> nombre;
    private String descripcion;
    private Boolean activo;
  }
}
