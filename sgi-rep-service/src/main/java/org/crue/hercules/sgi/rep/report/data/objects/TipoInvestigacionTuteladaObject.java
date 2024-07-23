package org.crue.hercules.sgi.rep.report.data.objects;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;
import org.crue.hercules.sgi.rep.dto.eti.TipoInvestigacionTuteladaDto;
import org.crue.hercules.sgi.rep.enums.TiposEnumI18n.TipoInvestigacionTuteladaI18n;

import lombok.Getter;

@Getter
public class TipoInvestigacionTuteladaObject {
  private Long id;
  private String nombre;
  private Boolean activo;

  public TipoInvestigacionTuteladaObject(TipoInvestigacionTuteladaDto dto) {
    this(dto, SgiLocaleContextHolder.getLanguage());
  }

  public TipoInvestigacionTuteladaObject(TipoInvestigacionTuteladaDto dto, Language lang) {
    if (dto != null) {
      this.id = dto.getId();
      this.nombre = TipoInvestigacionTuteladaI18n.getI18nMessageFromEnumAndLocale(
          this.id, lang.getLocale());
      this.activo = dto.getActivo();
    }
  }
}
