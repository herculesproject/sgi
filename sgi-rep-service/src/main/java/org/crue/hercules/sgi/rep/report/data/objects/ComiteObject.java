package org.crue.hercules.sgi.rep.report.data.objects;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;
import org.crue.hercules.sgi.rep.dto.eti.ComiteDto;
import org.crue.hercules.sgi.rep.dto.eti.ComiteDto.Genero;

import lombok.Getter;

@Getter
public class ComiteObject {
  private Long id;
  private String comite;
  private String nombreInvestigacion;
  private Genero genero;
  private FormularioObject formulario;
  private Boolean activo;

  public ComiteObject(ComiteDto dto) {
    this(dto, SgiLocaleContextHolder.getLanguage());
  }

  public ComiteObject(ComiteDto dto, Language lang) {
    if (dto != null) {
      this.id = dto.getId();
      this.comite = dto.getComite();
      this.nombreInvestigacion = dto.getNombreInvestigacion();
      this.genero = dto.getGenero();
      if (dto.getFormulario() != null) {
        this.formulario = new FormularioObject(dto.getFormulario(), lang);
      }
      this.activo = dto.getActivo();
    }
  }
}
