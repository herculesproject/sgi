package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report de informe acta
 */
public class ReportInformeActa extends SgiReportDto {
  public ReportInformeActa(Language lang) {
    this.setPath("rep-eti-acta-docx-" + lang.getCode());
    this.setName("informeActa");
    this.setLang(lang);
  }
}