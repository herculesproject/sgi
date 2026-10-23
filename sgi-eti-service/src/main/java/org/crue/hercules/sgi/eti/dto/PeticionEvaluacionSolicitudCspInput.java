package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para la creación de una petición de evaluación desde una
 * solicitud de CSP.
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
  @NotEmpty
  private List<I18nFieldValueDto> titulo;

  /** Referencia de la persona solicitante. */
  @NotNull
  private String personaRef;

  /** Indica si la solicitud tiene financiación. */
  @NotNull
  private Boolean existeFinanciacion;

  /** Referencia de la solicitud de CSP. */
  @NotNull
  private String solicitudConvocatoriaRef;

  /** Identificador del checklist de autoevaluación ética. */
  @NotNull
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
