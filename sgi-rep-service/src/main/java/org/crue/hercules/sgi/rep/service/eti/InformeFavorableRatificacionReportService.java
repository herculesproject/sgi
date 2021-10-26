package org.crue.hercules.sgi.rep.service.eti;

import java.time.Instant;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.InformeEvaluacionReportInput;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeFavorableRatificacion;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.SgiReportService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe favorable ratificación de ética
 */
@Service
@Slf4j
@Validated
public class InformeFavorableRatificacionReportService extends SgiReportService {

  private final PersonaService personaService;
  private final EvaluacionService evaluacionService;

  public InformeFavorableRatificacionReportService(SgiConfigProperties sgiConfigProperties,
      PersonaService personaService, EvaluacionService evaluacionService) {

    super(sgiConfigProperties);
    this.personaService = personaService;
    this.evaluacionService = evaluacionService;
  }

  private DefaultTableModel getTableModelGeneral(EvaluacionDto evaluacion, Instant fechaInforme) {

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
    try {

      final MasterReport report = getReportDefinition(sgiReport.getPath());

      EvaluacionDto evaluacion = evaluacionService.findById(input.getIdEvaluacion());

      String queryGeneral = QUERY_TYPE + SEPARATOR_KEY + NAME_GENERAL_TABLE_MODEL + SEPARATOR_KEY
          + "informeFavorableRatificacion";
      DefaultTableModel tableModelGeneral = getTableModelGeneral(evaluacion, input.getFecha());

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