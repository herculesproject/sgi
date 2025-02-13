package org.crue.hercules.sgi.rep.report.data.objects;

import java.time.Duration;

import org.crue.hercules.sgi.rep.dto.csp.AutorizacionDto;

import lombok.Getter;

@Getter
public class AutorizacionObject {

  private String tituloProyecto;
  private Duration horasDedicacion;
  private String datosConvocatoria;
  private String datosEntidad;
  private String datosResponsable;

  public AutorizacionObject(AutorizacionDto dto) {
    this.tituloProyecto = dto.getTituloProyecto();
    if (dto.getHorasDedicacion() != null) {
      this.horasDedicacion = Duration.ofHours(dto.getHorasDedicacion());
    }
    this.datosConvocatoria = dto.getDatosConvocatoria();
    this.datosEntidad = dto.getDatosEntidad();
    this.datosResponsable = dto.getDatosResponsable();
  }
}
