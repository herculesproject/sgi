package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;

import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO de salida con las fechas de la {@link ConvocatoriaFase} configurada como
 * fase de presentación de solicitudes en la {@link ConfiguracionSolicitud} de
 * una convocatoria.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvocatoriaFasePresentacionSolicitudesFechasOutput implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Fecha de inicio de la fase de presentación de solicitudes */
  private Instant fechaInicio;

  /** Fecha de fin de la fase de presentación de solicitudes */
  private Instant fechaFin;

}
