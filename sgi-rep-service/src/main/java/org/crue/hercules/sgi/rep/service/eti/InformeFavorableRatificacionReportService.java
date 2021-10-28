package org.crue.hercules.sgi.rep.service.eti;

import java.time.Instant;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.InformeEvaluacionReportInput;
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

  protected DefaultTableModel getTableModelGeneral(EvaluacionDto evaluacion, Instant fechaInforme) {

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

    columnsData.add("fechaFirmante");
    String i18nDe = ApplicationContextSupport.getMessage("common.de");
    String pattern = String.format("dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);
    elementsRow.add(formatInstantToString(fechaInforme, pattern));

    rowsData.add(elementsRow);

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  public void getReportInformeFavorableRatificacion(ReportInformeFavorableRatificacion sgiReport,
      InformeEvaluacionReportInput input) {
    getReportFromIdEvaluacion(sgiReport, input.getIdEvaluacion(), "informeFavorableRatificacion");
  }

}