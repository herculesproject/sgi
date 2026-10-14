package org.crue.hercules.sgi.eti.dto.sgp;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Vinculación de una persona en el SGP. Solo incluye los datos que usa ETI.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VinculacionOutput implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Id */
  private String id;
  /** Departamento de la persona */
  private DepartamentoOutput departamento;

  /**
   * Departamento de una vinculación del SGP.
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class DepartamentoOutput implements Serializable {
    /** Serial version */
    private static final long serialVersionUID = 1L;

    /** Id */
    private String id;
    /** Nombre */
    private String nombre;
  }
}
