package org.crue.hercules.sgi.rep.service.eti;

import java.time.Instant;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.ComiteDto.Genero;
import org.crue.hercules.sgi.rep.dto.eti.ConvocatoriaReunionDto;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeFavorableModificacion;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Servicio de generación de informe favorable modificación de ética
 */
@Service
@Validated
public class InformeFavorableModificacionReportService extends InformeEvaluacionBaseReportService {

  private final EvaluacionService evaluacionService;
  private final ConvocatoriaReunionService convocatoriaReunionService;

  public InformeFavorableModificacionReportService(SgiConfigProperties sgiConfigProperties,
      PersonaService personaService, EvaluacionService evaluacionService,
      ConvocatoriaReunionService convocatoriaReunionService) {

    super(sgiConfigProperties, personaService, evaluacionService);
    this.convocatoriaReunionService = convocatoriaReunionService;
    this.evaluacionService = evaluacionService;
  }

  protected DefaultTableModel getTableModelGeneral(EvaluacionDto evaluacion) {

    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();
    Vector<Object> elementsRow = new Vector<>();

    addColumnAndRowtDataInvestigador(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef(), columnsData,
        elementsRow);

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
      elementsRow.add(getErrorMessageToReport(e));
      elementsRow.add(getErrorMessageToReport(e));

    }

    columnsData.add("tituloProyecto");
    elementsRow.add(evaluacion.getMemoria().getPeticionEvaluacion().getTitulo());

    columnsData.add("fechaEnvioSecretaria");
    try {
      Instant fechaEnvioSecretaria = evaluacionService.findFirstFechaEnvioSecretariaByIdEvaluacion(evaluacion.getId());
      elementsRow.add(formatInstantToString(fechaEnvioSecretaria, pattern));
    } catch (Exception e) {
      elementsRow.add(getErrorMessageToReport(e));
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

    columnsData.add("nombreInvestigacion");
    elementsRow.add(evaluacion.getMemoria().getComite().getNombreInvestigacion());

    columnsData.add("del");
    columnsData.add("el");
    if (evaluacion.getMemoria().getComite().getGenero().equals(Genero.F)) {
      String i18nDela = ApplicationContextSupport.getMessage("common.dela");
      elementsRow.add(i18nDela);
      String i18nLa = ApplicationContextSupport.getMessage("common.la");
      elementsRow.add(StringUtils.capitalize(i18nLa));
    } else {
      String i18nDel = ApplicationContextSupport.getMessage("common.del");
      elementsRow.add(i18nDel);
      String i18nEl = ApplicationContextSupport.getMessage("common.el");
      elementsRow.add(StringUtils.capitalize(i18nEl));
    }
    rowsData.add(elementsRow);

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  public void getReportInformeFavorableModificacion(ReportInformeFavorableModificacion sgiReport, Long idEvaluacion) {
    getReportFromIdEvaluacion(sgiReport, idEvaluacion, "informeFavorableModificacion");
  }

}