package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report de informe de evaluador
 */
public class ReportInformeEvaluador extends SgiReportDto {
  public ReportInformeEvaluador(Language lang) {
    this.setPath("rep-eti-ficha-evaluador-docx-" + lang.getCode());
    this.setName("informeEvaluador");
    this.setLang(lang);
  }
}