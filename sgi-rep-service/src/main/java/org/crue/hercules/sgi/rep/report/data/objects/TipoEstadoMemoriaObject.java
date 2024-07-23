package org.crue.hercules.sgi.rep.report.data.objects;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;
import org.crue.hercules.sgi.rep.dto.eti.TipoEstadoMemoriaDto;
import org.crue.hercules.sgi.rep.enums.TiposEnumI18n.TipoEstadoMemoriaI18n;

import lombok.Getter;

@Getter
public class TipoEstadoMemoriaObject {
  private Long id;
  private String nombre;
  private Boolean activo;

  public TipoEstadoMemoriaObject(TipoEstadoMemoriaDto dto) {
    this(dto, SgiLocaleContextHolder.getLanguage());
  }

  public TipoEstadoMemoriaObject(TipoEstadoMemoriaDto dto, Language lang) {
    if (dto != null) {
      this.id = dto.getId();
      this.nombre = TipoEstadoMemoriaI18n.getI18nMessageFromEnumAndLocale(this.id,
          lang.getLocale());
      this.activo = dto.getActivo();
    }
  }
}
