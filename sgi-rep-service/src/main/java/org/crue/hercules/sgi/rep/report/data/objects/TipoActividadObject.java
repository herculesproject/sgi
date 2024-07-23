package org.crue.hercules.sgi.rep.report.data.objects;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;
import org.crue.hercules.sgi.rep.dto.eti.TipoActividadDto;
import org.crue.hercules.sgi.rep.enums.TiposEnumI18n.TipoActividadI18n;

import lombok.Getter;

@Getter
public class TipoActividadObject {
  private Long id;
  private String nombre;
  private Boolean activo;

  public TipoActividadObject(TipoActividadDto dto) {
    this(dto, SgiLocaleContextHolder.getLanguage());
  }

  public TipoActividadObject(TipoActividadDto dto, Language lang) {
    if (dto != null) {
      this.id = dto.getId();
      this.nombre = TipoActividadI18n.getI18nMessageFromEnumAndLocale(
          this.id, lang.getLocale());
      this.activo = dto.getActivo();
    }
  }
}
