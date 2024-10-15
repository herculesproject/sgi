package org.crue.hercules.sgi.rep.report.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.rep.dto.eti.ActaComentariosMemoriaReportOutput;
import org.crue.hercules.sgi.rep.report.data.objects.ActaComentarioObject;

public class ActaComentariosReportData {
  private static final String COMENTARIOS_KEY = "comentariosMemoria";

  private Map<String, Object> dataReport = new HashMap<>();
  private Language lang;

  public ActaComentariosReportData(Language lang) {
    this.lang = lang;
  }

  public void setComentarios(List<ActaComentariosMemoriaReportOutput> value) {
    this.dataReport.put(COMENTARIOS_KEY, value.stream().map(c -> new ActaComentarioObject(c, lang)).toList());
  }

  public Map<String, Object> getDataReport() {
    return this.dataReport;
  }
}
