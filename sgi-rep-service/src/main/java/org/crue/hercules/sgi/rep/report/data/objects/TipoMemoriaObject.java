package org.crue.hercules.sgi.rep.report.data.objects;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;
import org.crue.hercules.sgi.rep.dto.eti.TipoMemoriaDto;

import lombok.Getter;

@Getter
public class TipoMemoriaObject {
  private Long id;
  private String nombre;
  private Boolean activo;

  public TipoMemoriaObject(TipoMemoriaDto dto) {
    this(dto, SgiLocaleContextHolder.getLanguage());
  }

  public TipoMemoriaObject(TipoMemoriaDto dto, Language lang) {
    if (dto != null) {
      this.id = dto.getId();
      this.nombre = dto.getNombre();
      this.activo = dto.getActivo();
    }
  }
}
