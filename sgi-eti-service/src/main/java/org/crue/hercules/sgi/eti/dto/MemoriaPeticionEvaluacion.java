package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoriaPeticionEvaluacion implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  private Long id;

  private String numReferencia;

  private String titulo;

  private Comite comite;

  private TipoEstadoMemoria estadoActual;

  private LocalDateTime fechaEvaluacion;

  private LocalDate fechaLimite;

}