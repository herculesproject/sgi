package org.crue.hercules.sgi.csp.dto.eti;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Estado de financiación de una petición de evaluación del API del módulo de
 * Ética
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeticionEvaluacionEstadoFinanciacionInput implements Serializable {

  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Nuevo estado de la financiación */
  private EstadoFinanciacionDto estadoFinanciacion;
}
