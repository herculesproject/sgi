package org.crue.hercules.sgi.csp.dto;

import java.time.LocalDate;

import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ConvocatoriaConceptoGastoCodigoEc
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConvocatoriaConceptoGastoCodigoEcWithEnableAccion extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  private Long id;

  /** Convocatoria Concepto Gasto */
  private ConvocatoriaConceptoGasto convocatoriaConceptoGasto;

  /** Referencia código económico. */
  private String codigoEconomicoRef;

  /** Fecha Inicio. */
  private LocalDate fechaInicio;

  /** Fecha Fin. */
  private LocalDate fechaFin;

  /**
   * Si se permite realizar o no cualquier acción sobre el listado de conceptos de
   * gasto codigo económico
   */
  private Boolean enableAccion;

  /** Observaciones */
  private String observaciones;

}