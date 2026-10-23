package org.crue.hercules.sgi.csp.dto.eti;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Petición de evaluación del API del módulo de Ética, con los datos que se
 * usan en CSP.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeticionEvaluacionOutput implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Identificador. */
  private Long id;

  /** Código. */
  private String codigo;

}
