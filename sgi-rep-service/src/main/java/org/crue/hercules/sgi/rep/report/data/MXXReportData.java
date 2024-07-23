package org.crue.hercules.sgi.rep.report.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaDto;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaPeticionEvaluacionDto;
import org.crue.hercules.sgi.rep.dto.sgp.DatosContactoDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto.VinculacionDto;
import org.crue.hercules.sgi.rep.report.data.objects.MemoriaObject;
import org.crue.hercules.sgi.rep.report.data.objects.MemoriaPeticionEvaluacionObject;

import com.deepoove.poi.data.PictureRenderData;

public class MXXReportData {

  private static final String LOGO_KEY = "headerImg";
  private static final String FORMULARIO_ID_KEY = "formularioId";
  private static final String MEMORIA_KEY = "memoria";
  private static final String MEMORIAS_PETICION_EVALUACION_KEY = "memoriasPeticionEvaluacion";
  private static final String SOLICITANTE_KEY = "solicitante";
  private static final String SOLICITANTE_DATOS_CONTACTO_KEY = "solicitanteDatosContacto";
  private static final String SOLICITANTE_VINCULACION_KEY = "solicitanteVinculacion";
  private static final String TUTOR_KEY = "tutor";
  private static final String ZONE_ID_KEY = "zoneId";

  private Map<String, Object> dataReport = new HashMap<>();
  private Language lang;

  public MXXReportData(Map<String, Object> formularioData, Language lang) {
    this.dataReport = formularioData;
    this.lang = lang;
  }

  public void setHeaderLogo(PictureRenderData value) {
    this.dataReport.put(LOGO_KEY, value);
  }

  public void setFormularioId(Long value) {
    this.dataReport.put(FORMULARIO_ID_KEY, value);
  }

  public void setMemoria(MemoriaDto value) {
    this.dataReport.put(MEMORIA_KEY, new MemoriaObject(value, lang));
  }

  public void setMemoriasPeticionEvaluacion(List<MemoriaPeticionEvaluacionDto> value) {
    if (value == null) {
      this.dataReport.put(MEMORIAS_PETICION_EVALUACION_KEY, value);
    } else {
      this.dataReport.put(MEMORIAS_PETICION_EVALUACION_KEY,
          value.stream().map(mpe -> new MemoriaPeticionEvaluacionObject(mpe, lang)).toList());
    }
  }

  public void setSolicitante(PersonaDto value) {
    this.dataReport.put(SOLICITANTE_KEY, value);
  }

  public void setSolicitanteDatosContacto(DatosContactoDto value) {
    this.dataReport.put(SOLICITANTE_DATOS_CONTACTO_KEY, value);
  }

  public void setSolicitanteVinculacion(VinculacionDto value) {
    this.dataReport.put(SOLICITANTE_VINCULACION_KEY, value);
  }

  public void setTutor(PersonaDto value) {
    this.dataReport.put(TUTOR_KEY, value);
  }

  public void setZoneId(TimeZone value) {
    this.dataReport.put(ZONE_ID_KEY, value.toZoneId().getId());
  }

  public Map<String, Object> getDataReport() {
    return this.dataReport;
  }

}