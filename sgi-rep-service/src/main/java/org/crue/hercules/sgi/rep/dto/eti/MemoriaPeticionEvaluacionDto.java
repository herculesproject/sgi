package org.crue.hercules.sgi.rep.dto.eti;

import java.io.Serializable;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoriaPeticionEvaluacionDto implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  private Long id;

  private String numReferencia;

  private String titulo;

  private ComiteDto comite;

  private TipoEstadoMemoriaDto estadoActual;

  private boolean requiereRetrospectiva;

  private RetrospectivaDto retrospectiva;

  private Instant fechaEvaluacion;

  private Instant fechaLimite;

  private boolean isResponsable;

  private boolean activo;

  public MemoriaPeticionEvaluacionDto(Long id, String numReferencia, String titulo, ComiteDto comite,
      TipoEstadoMemoriaDto estadoActual, Instant fechaEvaluacion, Instant fechaLimite, boolean isResponsable,
      boolean activo, boolean requiereRetrospectiva, RetrospectivaDto retrospectiva) {

    this.id = id;
    this.numReferencia = numReferencia;
    this.titulo = titulo;
    this.comite = comite;
    this.estadoActual = estadoActual;
    this.fechaEvaluacion = fechaEvaluacion;
    this.fechaLimite = fechaLimite;
    this.isResponsable = isResponsable;
    this.activo = activo;
    this.requiereRetrospectiva = requiereRetrospectiva;
    this.retrospectiva = retrospectiva;
  }

}