package org.crue.hercules.sgi.rep.service.eti;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;
import org.crue.hercules.sgi.rep.dto.eti.ComiteDto.Genero;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.EvaluadorDto;
import org.crue.hercules.sgi.rep.dto.sgp.EmailDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.SgiReportDocxService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiSgpService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación base de informe evaluación
 */
@Service
@Slf4j
@Validated
public abstract class InformeEvaluacionBaseReportService extends SgiReportDocxService {

  private final EvaluacionService evaluacionService;
  private final SgiApiSgpService personaService;

  private static final Long TIPO_ACTIVIDAD_PROYECTO_DE_INVESTIGACION = 1L;
  private static final Long TIPO_ACTIVIDAD_INVESTIGACION_TUTELADA = 3L;
  private static final Long TIPO_INVESTIGACION_TUTELADA_TESIS_DOCTORAL = 1L;

  protected InformeEvaluacionBaseReportService(SgiConfigProperties sgiConfigProperties,
      SgiApiConfService sgiApiConfService, SgiApiSgpService personaService,
      EvaluacionService evaluacionService) {

    super(sgiConfigProperties, sgiApiConfService);
    this.personaService = personaService;
    this.evaluacionService = evaluacionService;
  }

  protected XWPFDocument getReportFromIdEvaluacion(SgiReportDto sgiReport, Long idEvaluacion) {
    try {
      HashMap<String, Object> dataReport = new HashMap<String, Object>();
      EvaluacionDto evaluacion = evaluacionService.findById(idEvaluacion);

      dataReport.put("headerImg", getImageHeaderLogo());

      XWPFDocument document = getDocument(evaluacion, dataReport, getReportDefinitionStream(sgiReport.getPath()));

      ByteArrayOutputStream outputPdf = new ByteArrayOutputStream();
      PdfOptions pdfOptions = PdfOptions.create();

      PdfConverter.getInstance().convert(document, outputPdf, pdfOptions);

      sgiReport.setContent(outputPdf.toByteArray());
      return document;

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
  }

  protected void addDataPersona(String personaRef, HashMap<String, Object> dataReport) {
    generateDataInvestigadorWithAttribute(personaRef, dataReport, null);
  }

  protected void addDataPersona(String personaRef, HashMap<String, Object> dataReport, String attribute) {
    generateDataInvestigadorWithAttribute(personaRef, dataReport, attribute);
  }

  protected void generateDataInvestigadorWithAttribute(String personaRef, HashMap<String, Object> dataReport,
      String attribute) {
    String attributeElementData = "nombre" + ((ObjectUtils.isEmpty(attribute)) ? "Investigador" : attribute);
    String attributeArticleData = "articulo" + ((ObjectUtils.isEmpty(attribute)) ? "Investigador" : attribute);
    String attributeDelData = "fieldDel" + ((ObjectUtils.isEmpty(attribute)) ? "Investigador" : attribute);
    String attributeElData = "fieldEl" + ((ObjectUtils.isEmpty(attribute)) ? "Investigador" : attribute);
    String attributeFieldData = "field" + ((ObjectUtils.isEmpty(attribute)) ? "Investigador" : attribute);
    String attributeFieldCapitalizeData = "fieldCapitalize"
        + ((ObjectUtils.isEmpty(attribute)) ? "Investigador" : attribute);
    String attributeEmailData = "email" + ((ObjectUtils.isEmpty(attribute)) ? "Investigador" : attribute);
    try {
      PersonaDto persona = personaService.findById(personaRef);
      dataReport.put(attributeElementData, persona.getNombre() + " " + persona.getApellidos());
      dataReport.put(attributeDelData, persona.getNombre() + " " + persona.getApellidos());
      String masculino = ((ObjectUtils.isEmpty(attribute))
          ? ApplicationContextSupport.getMessage("investigador.masculino")
          : ApplicationContextSupport.getMessage(attribute.toLowerCase() + ".masculino"));
      String femenino = ((ObjectUtils.isEmpty(attribute))
          ? ApplicationContextSupport.getMessage("investigador.femenino")
          : ApplicationContextSupport.getMessage(attribute.toLowerCase() + ".femenino"));

      String delMasculino = ApplicationContextSupport.getMessage("common.del");
      String deLaFemenino = ApplicationContextSupport.getMessage("common.dela");

      String elMasculino = ApplicationContextSupport.getMessage("common.el");
      String laFemenino = ApplicationContextSupport.getMessage("common.la");

      String fieldMasculino = ((ObjectUtils.isEmpty(attribute))
          ? ApplicationContextSupport.getMessage("field.investigador.masculino")
          : ApplicationContextSupport.getMessage("field." + attribute.toLowerCase() + ".masculino"));
      String fieldFemenino = ((ObjectUtils.isEmpty(attribute))
          ? ApplicationContextSupport.getMessage("field.investigador.femenino")
          : ApplicationContextSupport.getMessage("field." + attribute.toLowerCase() + ".femenino"));

      String fieldCapitalizeMasculino = ((ObjectUtils.isEmpty(attribute))
          ? ApplicationContextSupport.getMessage("field.capitalize.investigador.masculino")
          : ApplicationContextSupport.getMessage("field.capitalize." + attribute.toLowerCase() + ".masculino"));
      String fieldCapitalizeFemenino = ((ObjectUtils.isEmpty(attribute))
          ? ApplicationContextSupport.getMessage("field.capitalize.investigador.femenino")
          : ApplicationContextSupport.getMessage("field.capitalize." + attribute.toLowerCase() + ".femenino"));

      String email = "";
      if (null != persona.getEmails() && !persona.getEmails().isEmpty()) {
        email = persona.getEmails().stream()
            .filter(e -> null != e.getPrincipal() && e.getPrincipal().equals(Boolean.TRUE)).findFirst()
            .orElse(new EmailDto())
            .getEmail();
      }

      dataReport.put(
          attributeArticleData,
          persona.getSexo().getId().equals("V") ? masculino : femenino);

      dataReport.put(
          attributeDelData,
          persona.getSexo().getId().equals("V") ? delMasculino : deLaFemenino);

      dataReport.put(
          attributeElData,
          persona.getSexo().getId().equals("V") ? elMasculino : laFemenino);

      dataReport.put(
          attributeFieldData,
          persona.getSexo().getId().equals("V") ? fieldMasculino : fieldFemenino);

      dataReport.put(
          attributeFieldCapitalizeData,
          persona.getSexo().getId().equals("V") ? fieldCapitalizeMasculino : fieldCapitalizeFemenino);

      dataReport.put(attributeEmailData, email);

    } catch (Exception e) {
      dataReport.put(attributeElementData, getErrorMessage(e));
      dataReport.put(attributeArticleData,
          ((ObjectUtils.isEmpty(attribute)) ? ApplicationContextSupport.getMessage("investigador.masculinoFemenino")
              : ApplicationContextSupport.getMessage(attribute + ".masculinoFemenino")));
      dataReport.put(attributeDelData, ApplicationContextSupport.getMessage("common.del.masculinoFemenino"));
      dataReport.put(attributeElData, ApplicationContextSupport.getMessage("common.el.masculinoFemenino"));
      dataReport.put(attributeFieldData,
          ((ObjectUtils.isEmpty(attribute))
              ? ApplicationContextSupport.getMessage("field.investigador.masculinoFemenino")
              : ApplicationContextSupport.getMessage("field." + attribute.toLowerCase() + ".masculinoFemenino")));
      dataReport.put(attributeFieldCapitalizeData,
          ((ObjectUtils.isEmpty(attribute))
              ? ApplicationContextSupport.getMessage("field.capitalize.investigador.masculinoFemenino")
              : ApplicationContextSupport
                  .getMessage("field.capitalize." + attribute.toLowerCase() + ".masculinoFemenino")));
      dataReport.put(attributeEmailData, getErrorMessage(e));
    }
  }

  protected void addDataEvaluacion(EvaluacionDto evaluacion, HashMap<String, Object> dataReport) {
    dataReport.put("tituloProyecto", evaluacion.getMemoria().getPeticionEvaluacion().getTitulo());
    dataReport.put("referenciaProyecto", evaluacion.getMemoria().getPeticionEvaluacion().getCodigo());
    dataReport.put("comite", evaluacion.getMemoria().getComite().getComite());
    try {
      EvaluadorDto secretario = evaluacionService.findSecretarioEvaluacion(evaluacion.getId());
      if (ObjectUtils.isNotEmpty(secretario)) {
        addDataPersona(secretario.getPersonaRef(), dataReport, "Secretario");
      } else {
        dataReport.put("nombreSecretario", " - ");
        dataReport.put("articuloSecretario", ApplicationContextSupport.getMessage("secretario.masculinoFemenino"));
        dataReport.put("fieldDelSecretario", ApplicationContextSupport.getMessage("common.del.masculinoFemenino"));
        dataReport.put("fieldElSecretario", ApplicationContextSupport.getMessage("common.el.masculinoFemenino"));
        dataReport.put("fieldSecretario", ApplicationContextSupport.getMessage("field.secretario.masculinoFemenino"));
        dataReport.put("fieldCapitalizeSecretario",
            ApplicationContextSupport.getMessage("field.capitalize.secretario.masculinoFemenino"));
      }
    } catch (Exception e) {
      dataReport.put("nombreSecretario", getErrorMessage(e));
      dataReport.put("articuloSecretario", getErrorMessage(e));
      dataReport.put("fieldDelSecretario", getErrorMessage(e));
      dataReport.put("fieldElSecretario", getErrorMessage(e));
      dataReport.put("fieldSecretario", getErrorMessage(e));
      dataReport.put("fieldCapitalizeSecretario", getErrorMessage(e));
    }

    try {
      String presidenteRef = evaluacionService.findIdPresidenteByIdEvaluacion(evaluacion.getId());
      if (ObjectUtils.isNotEmpty(presidenteRef)) {
        addDataPersona(presidenteRef, dataReport, "Presidente");
      } else {
        dataReport.put("nombrePresidente", " - ");
        dataReport.put("articuloPresidente", ApplicationContextSupport.getMessage("presidente.masculinoFemenino"));
        dataReport.put("fieldDelPresidente", ApplicationContextSupport.getMessage("common.del.masculinoFemenino"));
        dataReport.put("fieldElPresidente", ApplicationContextSupport.getMessage("common.el.masculinoFemenino"));
        dataReport.put("fieldPresidente", ApplicationContextSupport.getMessage("field.presidente.masculinoFemenino"));
        dataReport.put("fieldCapitalizePresidente",
            ApplicationContextSupport.getMessage("field.capitalize.presidente.masculinoFemenino"));
      }
    } catch (Exception e) {
      dataReport.put("nombrePresidente", getErrorMessage(e));
      dataReport.put("articuloPresidente", getErrorMessage(e));
      dataReport.put("fieldDelPresidente", getErrorMessage(e));
      dataReport.put("fieldElPresidente", getErrorMessage(e));
      dataReport.put("fieldPresidente", getErrorMessage(e));
      dataReport.put("fieldCapitalizePresidente",
          getErrorMessage(e));
    }

    dataReport.put("nombreInvestigacion", evaluacion.getMemoria().getComite().getNombreInvestigacion());
    if (evaluacion.getMemoria().getComite().getGenero().equals(Genero.F)) {
      String i18nDela = ApplicationContextSupport.getMessage("common.dela");
      dataReport.put("del", i18nDela);
      String i18nLa = ApplicationContextSupport.getMessage("common.la");
      dataReport.put("el", StringUtils.capitalize(i18nLa));
      String i18nEsta = ApplicationContextSupport.getMessage("common.esta");
      dataReport.put("este", i18nEsta);
    } else {
      String i18nDel = ApplicationContextSupport.getMessage("common.del");
      dataReport.put("del", i18nDel);
      String i18nEl = ApplicationContextSupport.getMessage("common.el");
      dataReport.put("el", StringUtils.capitalize(i18nEl));
      String i18nEste = ApplicationContextSupport.getMessage("common.este");
      dataReport.put("este", i18nEste);
    }

    addDataActividad(evaluacion, dataReport);

    evaluacion.getMemoria().getPeticionEvaluacion().getTipoInvestigacionTutelada();
  }

  protected void addDataActividad(EvaluacionDto evaluacion, HashMap<String, Object> dataReport) {
    if (ObjectUtils.isNotEmpty(evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad())
        && !evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad().getId()
            .equals(TIPO_ACTIVIDAD_INVESTIGACION_TUTELADA)) {
      dataReport.put("actividad",
          evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad().getNombre().toLowerCase());
      if (evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad().getId()
          .equals(TIPO_ACTIVIDAD_PROYECTO_DE_INVESTIGACION)) {
        dataReport.put("fieldDelActividad", ApplicationContextSupport.getMessage("common.del"));
        dataReport.put("fieldDichoActividad", ApplicationContextSupport.getMessage("common.dicho"));
        dataReport.put("fieldRealizadoActividad", ApplicationContextSupport.getMessage("common.realizado"));
        dataReport.put("fieldAlActividad", ApplicationContextSupport.getMessage("common.al"));
      } else {
        dataReport.put("fieldDelActividad", ApplicationContextSupport.getMessage("common.dela"));
        dataReport.put("fieldDichoActividad", ApplicationContextSupport.getMessage("common.dicha"));
        dataReport.put("fieldRealizadoActividad", ApplicationContextSupport.getMessage("common.realizada"));
        dataReport.put("fieldAlActividad", ApplicationContextSupport.getMessage("common.a.la"));
      }
    } else if (ObjectUtils.isNotEmpty(evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad()) && ObjectUtils
        .isNotEmpty(evaluacion.getMemoria().getPeticionEvaluacion().getTipoInvestigacionTutelada())) {
      dataReport.put("actividad",
          evaluacion.getMemoria().getPeticionEvaluacion().getTipoInvestigacionTutelada().getNombre().toLowerCase());
      if (!evaluacion.getMemoria().getPeticionEvaluacion().getTipoInvestigacionTutelada().getId()
          .equals(TIPO_INVESTIGACION_TUTELADA_TESIS_DOCTORAL)) {
        dataReport.put("fieldDelActividad", ApplicationContextSupport.getMessage("common.del"));
        dataReport.put("fieldDichoActividad", ApplicationContextSupport.getMessage("common.dicho"));
        dataReport.put("fieldRealizadoActividad", ApplicationContextSupport.getMessage("common.realizado"));
        dataReport.put("fieldAlActividad", ApplicationContextSupport.getMessage("common.al"));
      } else {
        dataReport.put("fieldDelActividad", ApplicationContextSupport.getMessage("common.dela"));
        dataReport.put("fieldDichoActividad", ApplicationContextSupport.getMessage("common.dicha"));
        dataReport.put("fieldRealizadoActividad", ApplicationContextSupport.getMessage("common.realizada"));
        dataReport.put("fieldAlActividad", ApplicationContextSupport.getMessage("common.a.la"));
      }
    }

  }

  protected abstract XWPFDocument getDocument(EvaluacionDto evaluacion, HashMap<String, Object> dataReport,
      InputStream path);

}