package org.crue.hercules.sgi.rep.service.eti;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.InformeEvaluacionReportInput;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeEvaluacionRetrospectiva;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.SgiReportService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe evaluación retrospectiva de ética
 */
@Service
@Slf4j
@Validated
public class InformeEvaluacionRetrospectivaReportService extends SgiReportService {

  private final PersonaService personaService;
  private final EvaluacionService evaluacionService;

  public InformeEvaluacionRetrospectivaReportService(SgiConfigProperties sgiConfigProperties,
      PersonaService personaService, EvaluacionService evaluacionService) {

    super(sgiConfigProperties);
    this.personaService = personaService;
    this.evaluacionService = evaluacionService;
  }

  private DefaultTableModel getTableModelGeneral(EvaluacionDto evaluacion) {

    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();
    Vector<Object> elementsRow = new Vector<>();

    columnsData.add("nombreInvestigador");
    try {
      PersonaDto persona = personaService.findById(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef());
      elementsRow.add(persona.getNombre() + " " + persona.getApellidos());
    } catch (Exception e) {
      elementsRow.add(getErrorMessageToReport(e));
    }

    columnsData.add("tituloProyecto");
    elementsRow.add(evaluacion.getMemoria().getPeticionEvaluacion().getTitulo());

    columnsData.add("lugar");
    elementsRow.add(evaluacion.getConvocatoriaReunion().getNumeroActa());

    columnsData.add("codigoOrgano");
    elementsRow.add(evaluacion.getMemoria().getCodOrganoCompetente());

    columnsData.add("nombreSecretario");
    elementsRow.add(evaluacion.getMemoria().getComite().getNombreSecretario());

    columnsData.add("nombrePresidente");
    String idPresidente = evaluacionService.findIdPresidenteByIdEvaluacion(evaluacion.getId());
    if (StringUtils.hasText(idPresidente)) {
      try {
        PersonaDto presidente = personaService.findById(idPresidente);
        elementsRow.add(presidente.getNombre() + " " + presidente.getApellidos());
      } catch (Exception e) {
        elementsRow.add(getErrorMessageToReport(e));
      }
    }

    rowsData.add(elementsRow);

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  public void getReportInformeEvaluacionRetrospectiva(ReportInformeEvaluacionRetrospectiva sgiReport,
      InformeEvaluacionReportInput input) {
    try {

      final MasterReport report = getReportDefinition(sgiReport.getPath());

      EvaluacionDto evaluacion = evaluacionService.findById(input.getIdEvaluacion());

      String queryGeneral = QUERY_TYPE + SEPARATOR_KEY + NAME_GENERAL_TABLE_MODEL + SEPARATOR_KEY
          + "informeEvaluacionRetrospectiva";
      DefaultTableModel tableModelGeneral = getTableModelGeneral(evaluacion);

      TableDataFactory dataFactory = new TableDataFactory();
      dataFactory.addTable(queryGeneral, tableModelGeneral);
      report.setDataFactory(dataFactory);

      sgiReport.setContent(generateReportOutput(sgiReport.getOutputReportType(), report));

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
  }

}