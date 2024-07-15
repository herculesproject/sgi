package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report de informe de evaluación
 */
public class ReportInformeEvaluacion extends SgiReportDto {
  public ReportInformeEvaluacion(Language lang) {
    this.setPath("rep-eti-evaluacion-docx-" + lang.getCode());
    this.setName("informeEvaluacion");
    this.setLang(lang);
  }
}