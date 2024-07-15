package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report de informe favorable ratificación
 */
public class ReportInformeFavorableRatificacion extends SgiReportDto {
  public ReportInformeFavorableRatificacion(Language lang) {
    this.setPath("rep-eti-evaluacion-favorable-memoria-ratificacion-docx-" + lang.getCode());
    this.setName("informeFavorableRatificacion");
    this.setLang(lang);
  }
}