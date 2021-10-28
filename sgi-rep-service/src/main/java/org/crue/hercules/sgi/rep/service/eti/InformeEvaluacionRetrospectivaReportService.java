package org.crue.hercules.sgi.rep.service.eti;

import java.time.Instant;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.InformeEvaluacionReportInput;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeEvaluacionRetrospectiva;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

/**
 * Servicio de generación de informe evaluación retrospectiva de ética
 */
@Service
@Validated
public class InformeEvaluacionRetrospectivaReportService extends InformeEvaluacionBaseReportService {

  private final PersonaService personaService;
  private final EvaluacionService evaluacionService;

  public InformeEvaluacionRetrospectivaReportService(SgiConfigProperties sgiConfigProperties,
      PersonaService personaService, EvaluacionService evaluacionService) {

    super(sgiConfigProperties, personaService, evaluacionService);
    this.personaService = personaService;
    this.evaluacionService = evaluacionService;
  }

  protected DefaultTableModel getTableModelGeneral(EvaluacionDto evaluacion, Instant fechaInforme) {

    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();
    Vector<Object> elementsRow = new Vector<>();

    addColumnAndRowtDataInvestigador(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef(), columnsData,
        elementsRow);

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
    addRowDataInvestigador(idPresidente, elementsRow);

    String i18nDe = ApplicationContextSupport.getMessage("common.de");
    String fechaFirmantePattern = String.format("EEEE dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);
    columnsData.add("fechaFirmante");
    elementsRow.add(formatInstantToString(fechaInforme, fechaFirmantePattern));

    rowsData.add(elementsRow);

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  public void getReportInformeEvaluacionRetrospectiva(ReportInformeEvaluacionRetrospectiva sgiReport,
      InformeEvaluacionReportInput input) {
    getReportFromIdEvaluacion(sgiReport, input.getIdEvaluacion(), "informeEvaluacionRetrospectiva");
  }

}