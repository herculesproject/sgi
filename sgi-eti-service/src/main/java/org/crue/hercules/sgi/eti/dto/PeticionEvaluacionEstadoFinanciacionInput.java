package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para la actualización del estado de financiación de una
 * petición de evaluación.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeticionEvaluacionEstadoFinanciacionInput implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Estado de la financiación. */
  @NotNull
  private EstadoFinanciacionDto estadoFinanciacion;

}
