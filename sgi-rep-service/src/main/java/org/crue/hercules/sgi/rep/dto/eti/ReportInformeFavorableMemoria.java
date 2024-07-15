package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report de informe favorable memoria
 */
public class ReportInformeFavorableMemoria extends SgiReportDto {
  public ReportInformeFavorableMemoria(Language lang) {
    this.setPath("rep-eti-evaluacion-favorable-memoria-nueva-docx-" + lang.getCode());
    this.setName("informeFavorableMemoria");
    this.setLang(lang);
  }
}