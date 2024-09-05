package org.crue.hercules.sgi.rep.report.data.objects;

import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;
import org.crue.hercules.sgi.rep.dto.eti.ComiteDto;
import org.crue.hercules.sgi.rep.dto.eti.ComiteNombreDto;

import lombok.Getter;

@Getter
public class ComiteObject {
  private Long id;
  private String codigo;
  private ComiteNombreDto nombre;
  private Boolean activo;

  public ComiteObject(ComiteDto dto) {
    this(dto, SgiLocaleContextHolder.getLanguage());
  }

  public ComiteObject(ComiteDto dto, Language lang) {
    if (dto != null) {
      this.id = dto.getId();
      this.codigo = dto.getCodigo();
      this.nombre = I18nHelper.getFieldValueForLanguage(dto.getNombre(), lang).orElse(null);
      this.activo = dto.getActivo();
    }
  }

  /** Se mantiene por compatibilidad con reports antiguos */
  public String getComite() {
    return codigo;
  }

  /** Se mantiene por compatibilidad con reports antiguos */
  public String getNombreInvestigacion() {
    return nombre != null ? nombre.getValue() : null;
  }

  /** Se mantiene por compatibilidad con reports antiguos */
  public ComiteNombreDto.Genero getGenero() {
    return nombre != null ? nombre.getGenero() : null;
  }
}
