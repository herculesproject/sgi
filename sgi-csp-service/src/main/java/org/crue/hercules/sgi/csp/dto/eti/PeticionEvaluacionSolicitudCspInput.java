package org.crue.hercules.sgi.csp.dto.eti;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Datos para la creación de una petición de evaluación del API del módulo de
 * Ética a partir de una solicitud.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeticionEvaluacionSolicitudCspInput implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Título. */
  private List<I18nFieldValueDto> titulo;

  /** Referencia de la persona solicitante. */
  private String personaRef;

  /** Indica si la solicitud tiene financiación. */
  private Boolean existeFinanciacion;

  /** Identificador de la solicitud. */
  private String solicitudConvocatoriaRef;

  /** Identificador del checklist de autoevaluación ética. */
  private Long checklistId;

  /** Estado de la financiación. */
  private EstadoFinanciacionDto estadoFinanciacion;

  /** Importe de la financiación. */
  private BigDecimal importeFinanciacion;

  /** Fuente de la financiación. */
  private List<I18nFieldValueDto> fuenteFinanciacion;

  /** Resumen. */
  private List<I18nFieldValueDto> resumen;

  /** Objetivos. */
  private List<I18nFieldValueDto> objetivos;

}
