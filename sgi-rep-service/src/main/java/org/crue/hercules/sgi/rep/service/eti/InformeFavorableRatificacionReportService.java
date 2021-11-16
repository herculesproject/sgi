package org.crue.hercules.sgi.rep.service.eti;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.ComiteDto.Genero;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeFavorableRatificacion;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Servicio de generación de informe favorable ratificación de ética
 */
@Service
@Validated
public class InformeFavorableRatificacionReportService extends InformeEvaluacionBaseReportService {

  public InformeFavorableRatificacionReportService(SgiConfigProperties sgiConfigProperties,
      PersonaService personaService, EvaluacionService evaluacionService) {

    super(sgiConfigProperties, personaService, evaluacionService);
  }

  protected DefaultTableModel getTableModelGeneral(EvaluacionDto evaluacion) {

    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();
    Vector<Object> elementsRow = new Vector<>();

    addColumnAndRowtDataInvestigador(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef(), columnsData,
        elementsRow);

    columnsData.add("tituloProyecto");
    elementsRow.add(evaluacion.getMemoria().getPeticionEvaluacion().getTitulo());

    columnsData.add("comite");
    elementsRow.add(evaluacion.getMemoria().getComite().getComite());

    columnsData.add("nombreSecretario");
    elementsRow.add(evaluacion.getMemoria().getComite().getNombreSecretario());

    columnsData.add("nombreDecreto");
    elementsRow.add(evaluacion.getMemoria().getComite().getNombreDecreto());

    columnsData.add("nombreArticulo");
    elementsRow.add(evaluacion.getMemoria().getComite().getArticulo());

    columnsData.add("nombreInvestigacion");
    elementsRow.add(evaluacion.getMemoria().getComite().getNombreInvestigacion());

    columnsData.add("del");
    if (evaluacion.getMemoria().getComite().getGenero().equals(Genero.F)) {
      String i18nDela = ApplicationContextSupport.getMessage("common.dela");
      elementsRow.add(i18nDela);
    } else {
      String i18nDel = ApplicationContextSupport.getMessage("common.del");
      elementsRow.add(i18nDel);
    }
    rowsData.add(elementsRow);

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  public void getReportInformeFavorableRatificacion(ReportInformeFavorableRatificacion sgiReport, Long idEvaluacion) {
    getReportFromIdEvaluacion(sgiReport, idEvaluacion, "informeFavorableRatificacion");
  }

}