package org.crue.hercules.sgi.pii.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.pii.model.SolicitudProteccion;

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
public class SolicitudProteccionInput {

  @NotNull
  private Long invencionId;

  @NotEmpty
  @Size(max = SolicitudProteccion.TITULO_MAX_LENGTH)
  private String titulo;

  private LocalDate fechaPrioridadSolicitud;

  private LocalDate fechaFinPriorPresFasNacRec;

  private LocalDate fechaPublicacion;

  private LocalDate fechaConcesion;

  private LocalDate fechaCaducid;

  @NotNull
  private Long viaProteccionId;

  @NotEmpty
  @Size(max = SolicitudProteccion.NUMERO_SOLICITUD_MAX_LENGTH)
  private String numeroSolicitud;

  private String numeroPublicacion;

  private String numeroConcesion;

  private String numeroRegistro;

  @NotNull
  private SolicitudProteccion.EstadoSolicitudProteccion estado;

  private Long tipoCaducidadId;

  private String agentePropiedadRef;

  @NotEmpty
  private String paisProteccionRef;

  private String comentarios;
}
