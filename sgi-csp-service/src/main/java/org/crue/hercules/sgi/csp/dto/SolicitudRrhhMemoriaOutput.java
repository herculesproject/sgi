package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.util.Collection;

import org.crue.hercules.sgi.csp.model.SolicitudRrhhTituloTrabajo;

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
public class SolicitudRrhhMemoriaOutput implements Serializable {
  private Collection<SolicitudRrhhTituloTrabajo> tituloTrabajo;
  private String resumen;
  private String observaciones;
}
