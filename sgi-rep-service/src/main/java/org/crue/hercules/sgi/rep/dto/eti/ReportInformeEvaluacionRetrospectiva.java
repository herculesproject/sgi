package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report de informe evaluación retrospectiva
 */
public class ReportInformeEvaluacionRetrospectiva extends SgiReportDto {
  public ReportInformeEvaluacionRetrospectiva(Language lang) {
    this.setPath("rep-eti-evaluacion-retrospectiva-docx-" + lang.getCode());
    this.setName("informeEvaluacionRetrospectiva");
    this.setLang(lang);
  }
}