package org.crue.hercules.sgi.rep.report.data.objects;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;
import org.crue.hercules.sgi.rep.dto.eti.EstadoRetrospectivaDto;

import lombok.Getter;

@Getter
public class EstadoRetrospectivaObject {
  private Long id;
  private String nombre;
  private Boolean activo;

  public EstadoRetrospectivaObject(EstadoRetrospectivaDto dto) {
    this(dto, SgiLocaleContextHolder.getLanguage());
  }

  public EstadoRetrospectivaObject(EstadoRetrospectivaDto dto, Language lang) {
    if (dto != null) {
      this.id = dto.getId();
      this.nombre = dto.getNombre();
      this.activo = dto.getActivo();
    }
  }
}
