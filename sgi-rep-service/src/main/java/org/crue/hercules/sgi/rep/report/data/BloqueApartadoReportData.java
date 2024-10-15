package org.crue.hercules.sgi.rep.report.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.rep.dto.eti.BloqueOutput;
import org.crue.hercules.sgi.rep.report.data.objects.BloqueApartadoObject;

public class BloqueApartadoReportData {
  private static final String ID_DICTAMEN_KEY = "idDictamen";
  private static final String ID_DICTAMEN_NO_PROCEDE_EVALUEAR = "idDictamenNoProcedeEvaluar";
  private static final String NUM_COMENTARIOS_KEY = "numComentarios";
  private static final String BLOQUES_KEY = "bloques";

  private Map<String, Object> dataReport = new HashMap<>();
  private Language lang;

  public BloqueApartadoReportData(Language lang) {
    this.lang = lang;

  }

  public void setIdDictamen(Long value) {
    this.dataReport.put(ID_DICTAMEN_KEY, value);
  }

  public void setIdDictamenNoProcedeEvaluar(Long value) {
    this.dataReport.put(ID_DICTAMEN_NO_PROCEDE_EVALUEAR, value);
  }

  public void setNumComentarios(Integer value) {
    this.dataReport.put(NUM_COMENTARIOS_KEY, value);
  }

  public void setBloques(List<BloqueOutput> value) {
    this.dataReport.put(BLOQUES_KEY, value.stream().map(c -> new BloqueApartadoObject(c, lang)).toList());
  }

  public Map<String, Object> getDataReport() {
    return this.dataReport;
  }
}
