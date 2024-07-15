package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report M10, M20 o M30
 */
public class ReportMXX extends SgiReportDto {
  public ReportMXX(Language lang) {
    this.setPath("rep-eti-mxx-docx-" + lang.getCode());
    this.setName("mxx");
    this.setLang(lang);
  }
}