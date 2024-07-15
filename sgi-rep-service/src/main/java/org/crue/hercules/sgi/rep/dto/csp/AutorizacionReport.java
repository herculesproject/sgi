package org.crue.hercules.sgi.rep.dto.csp;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report de autorización para participar en proyectos
 * de investigación
 */
public class AutorizacionReport extends SgiReportDto {
  public AutorizacionReport(Language lang) {
    this.setPath("rep-csp-certificado-autorizacion-proyecto-externo-docx-" + lang.getCode());
    this.setName("autorizacionProyectoExterno");
    this.setLang(lang);
  }
}