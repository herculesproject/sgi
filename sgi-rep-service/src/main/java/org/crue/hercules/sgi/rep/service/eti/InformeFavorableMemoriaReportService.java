package org.crue.hercules.sgi.rep.service.eti;

import java.time.Instant;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.InformeEvaluacionReportInput;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeFavorableMemoria;
import org.crue.hercules.sgi.rep.dto.eti.TareaDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.SgiReportService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe favorable memoria de ética
 */
@Service
@Slf4j
@Validated
public class InformeFavorableMemoriaReportService extends SgiReportService {

  private final PersonaService personaService;
  private final EvaluacionService evaluacionService;
  private final PeticionEvaluacionService peticionEvaluacionService;

  public InformeFavorableMemoriaReportService(SgiConfigProperties sgiConfigProperties, PersonaService personaService,
      EvaluacionService evaluacionService, PeticionEvaluacionService peticionEvaluacionService) {

    super(sgiConfigProperties);
    this.personaService = personaService;
    this.evaluacionService = evaluacionService;
    this.peticionEvaluacionService = peticionEvaluacionService;
  }

  private DefaultTableModel getTableModelGeneral(EvaluacionDto evaluacion, Instant fechaInforme) {

    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();
    Vector<Object> elementsRow = new Vector<>();

    columnsData.add("codigoMemoria");
    elementsRow.add(evaluacion.getMemoria().getNumReferencia());

    columnsData.add("nombreInvestigador");
    try {
      PersonaDto persona = personaService.findById(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef());

      elementsRow.add(persona.getNombre() + " " + persona.getApellidos());
    } catch (Exception e) {
      elementsRow.add(getErrorMessageToReport(e));
    }

    columnsData.add("tituloProyecto");
    elementsRow.add(evaluacion.getMemoria().getPeticionEvaluacion().getTitulo());

    columnsData.add("fechaDictamen");
    String i18nDe = ApplicationContextSupport.getMessage("common.de");
    String pattern = String.format("EEEE dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);
    elementsRow.add(formatInstantToString(evaluacion.getFechaDictamen(), pattern));

    columnsData.add("fecha");
    elementsRow.add(formatInstantToString(fechaInforme, pattern));

    columnsData.add("numeroActa");
    elementsRow.add(evaluacion.getConvocatoriaReunion().getNumeroActa());

    columnsData.add("comite");
    elementsRow.add(evaluacion.getMemoria().getComite().getComite());

    columnsData.add("nombreSecretario");
    elementsRow.add(evaluacion.getMemoria().getComite().getNombreSecretario());

    columnsData.add("nombreInvestigacion");
    elementsRow.add(evaluacion.getMemoria().getComite().getNombreInvestigacion());

    columnsData.add("nombreDecreto");
    elementsRow.add(evaluacion.getMemoria().getComite().getNombreDecreto());

    rowsData.add(elementsRow);

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  private DefaultTableModel getTableModelEquipoInvestigador(EvaluacionDto evaluacion) {
    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();

    List<TareaDto> tareas = peticionEvaluacionService
        .findTareasEquipoTrabajo(evaluacion.getMemoria().getPeticionEvaluacion().getId());

    columnsData.add("nombreInvestigador");

    tareas.forEach(tarea -> {
      Vector<Object> elementsRow = new Vector<>();

      try {
        PersonaDto persona = personaService.findById(tarea.getEquipoTrabajo().getPersonaRef());
        elementsRow.add(persona.getNombre() + " " + persona.getApellidos());
      } catch (Exception e) {
        elementsRow.add(getErrorMessageToReport(e));
      }

      rowsData.add(elementsRow);
    });

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  public void getReportInformeFavorableMemoria(ReportInformeFavorableMemoria sgiReport,
      InformeEvaluacionReportInput input) {
    try {

      final MasterReport report = getReportDefinition(sgiReport.getPath());

      EvaluacionDto evaluacion = evaluacionService.findById(input.getIdEvaluacion());

      String queryGeneral = QUERY_TYPE + SEPARATOR_KEY + NAME_GENERAL_TABLE_MODEL + SEPARATOR_KEY
          + "informeFavorableMemoria";
      DefaultTableModel tableModelGeneral = getTableModelGeneral(evaluacion, input.getFecha());

      TableDataFactory dataFactory = new TableDataFactory();
      dataFactory.addTable(queryGeneral, tableModelGeneral);
      report.setDataFactory(dataFactory);

      String queryEquipoInvestigador = QUERY_TYPE + SEPARATOR_KEY + "equipoInvestigador";
      DefaultTableModel tableModeEquipoInvestigador = getTableModelEquipoInvestigador(evaluacion);
      TableDataFactory dataFactorySubReportEquipoInvestigador = new TableDataFactory();
      dataFactorySubReportEquipoInvestigador.addTable(queryEquipoInvestigador, tableModeEquipoInvestigador);
      Band bandEquipoInvestigador = (Band) report.getItemBand().getElement(1);
      SubReport subreportEquipoInvestigador = (SubReport) bandEquipoInvestigador.getElement(0);
      subreportEquipoInvestigador.setDataFactory(dataFactorySubReportEquipoInvestigador);

      sgiReport.setContent(generateReportOutput(sgiReport.getOutputReportType(), report));

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
  }

}