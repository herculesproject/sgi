package org.crue.hercules.sgi.csp.dto.com;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

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
public class CspComInicioPresentacionGastoData implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  private LocalDate fecha;
  private List<Proyecto> proyectos;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Proyecto implements Serializable {
    /** Serial version */
    private static final long serialVersionUID = 1L;

    private String titulo;
    private Instant fechaInicio;
    private Instant fechaFin;
  }
}
