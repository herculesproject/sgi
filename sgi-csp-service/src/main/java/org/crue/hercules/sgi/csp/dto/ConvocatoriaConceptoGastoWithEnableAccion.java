package org.crue.hercules.sgi.csp.dto;

import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ConvocatoriaConceptoGasto
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConvocatoriaConceptoGastoWithEnableAccion extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  private Long id;

  /** Convocatoria */
  private Convocatoria convocatoria;

  /** ConceptoGasto */
  private ConceptoGasto conceptoGasto;

  /** Observaciones */
  private String observaciones;

  /** Importe máximo */
  private Double importeMaximo;

  /** Permitido */
  private Boolean permitido;

  /** Número de meses */
  private Integer numMeses;

  /** Porcentaje coste indirecto */
  private Integer porcentajeCosteIndirecto;

  /**
   * Si se permite realizar o no cualquier acción sobre el listado de conceptos de
   * gasto
   */
  private Boolean enableAccion;

}