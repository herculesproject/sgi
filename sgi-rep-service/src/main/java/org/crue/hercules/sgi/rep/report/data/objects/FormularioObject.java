package org.crue.hercules.sgi.rep.report.data.objects;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;
import org.crue.hercules.sgi.rep.dto.eti.FormularioDto;

import lombok.Getter;

@Getter
public class FormularioObject {
  private Long id;
  private String nombre;
  private String descripcion;

  public FormularioObject(FormularioDto dto) {
    this(dto, SgiLocaleContextHolder.getLanguage());
  }

  public FormularioObject(FormularioDto dto, Language lang) {
    if (dto != null) {
      this.id = dto.getId();
      this.nombre = dto.getNombre();
      this.descripcion = dto.getDescripcion();
    }
  }
}
