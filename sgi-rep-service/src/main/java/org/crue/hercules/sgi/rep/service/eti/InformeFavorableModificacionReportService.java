package org.crue.hercules.sgi.rep.service.eti;

import java.time.Instant;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.ConvocatoriaReunionDto;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.InformeEvaluacionReportInput;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeFavorableModificacion;
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
 * Servicio de generación de informe favorable modificación de ética
 */
@Service
@Slf4j
@Validated
public class InformeFavorableModificacionReportService extends SgiReportService {

  private final PersonaService personaService;
  private final EvaluacionService evaluacionService;
  private final ConvocatoriaReunionService convocatoriaReunionService;

  public InformeFavorableModificacionReportService(SgiConfigProperties sgiConfigProperties,
      PersonaService personaService, EvaluacionService evaluacionService,
      ConvocatoriaReunionService convocatoriaReunionService) {

    super(sgiConfigProperties);
    this.personaService = personaService;
    this.evaluacionService = evaluacionService;
    this.convocatoriaReunionService = convocatoriaReunionService;
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

    String i18nDe = ApplicationContextSupport.getMessage("common.de");
    String pattern = String.format("dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);

    columnsData.add("fechaDictamenMemOriginal");
    columnsData.add("numeroActaMemOriginal");
    try {

      ConvocatoriaReunionDto convocatoriaUltimaEvaluacion = convocatoriaReunionService
          .findConvocatoriaUltimaEvaluacionTipoMemoria(evaluacion.getId(), evaluacion.getDictamen().getId());

      elementsRow.add(formatInstantToString(convocatoriaUltimaEvaluacion.getFechaEvaluacion(), pattern));

      elementsRow.add(convocatoriaUltimaEvaluacion.getNumeroActa() + "/" + convocatoriaUltimaEvaluacion.getAnio());
    } catch (Exception e) {
      elementsRow.add(e.getMessage());
      elementsRow.add(e.getMessage());
    }

    columnsData.add("tituloProyecto");
    elementsRow.add(evaluacion.getMemoria().getPeticionEvaluacion().getTitulo());

    columnsData.add("fechaEnvioSecretaria");
    try {
      Instant fechaEnvioSecretaria = evaluacionService.findFirstFechaEnvioSecretariaByIdEvaluacion(evaluacion.getId());
      elementsRow.add(formatInstantToString(fechaEnvioSecretaria, pattern));
    } catch (Exception e) {
      elementsRow.add(e.getMessage());
    }

    columnsData.add("fechaDictamen");
    elementsRow.add(formatInstantToString(evaluacion.getFechaDictamen(), pattern));

    columnsData.add("numeroActa");
    elementsRow.add(evaluacion.getConvocatoriaReunion().getNumeroActa());

    columnsData.add("referenciaMemoria");
    elementsRow.add(evaluacion.getMemoria().getNumReferencia());

    columnsData.add("comite");
    elementsRow.add(evaluacion.getMemoria().getComite().getComite());

    columnsData.add("nombreSecretario");
    elementsRow.add(evaluacion.getMemoria().getComite().getNombreSecretario());

    columnsData.add("nombreDecreto");
    elementsRow.add(evaluacion.getMemoria().getComite().getNombreDecreto());

    columnsData.add("fechaFirmante");
    elementsRow.add(formatInstantToString(fechaInforme, pattern));

    rowsData.add(elementsRow);

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  public void getReportInformeFavorableModificacion(ReportInformeFavorableModificacion sgiReport,
      InformeEvaluacionReportInput input) {
    try {

      final MasterReport report = getReportDefinition(sgiReport.getPath());

      EvaluacionDto evaluacion = evaluacionService.findById(input.getIdEvaluacion());

      String queryGeneral = QUERY_TYPE + SEPARATOR_KEY + NAME_GENERAL_TABLE_MODEL + SEPARATOR_KEY
          + "informeFavorableModificacion";
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