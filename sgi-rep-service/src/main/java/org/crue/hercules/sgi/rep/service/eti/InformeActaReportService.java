package org.crue.hercules.sgi.rep.service.eti;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;
import org.crue.hercules.sgi.rep.dto.eti.ActaComentariosMemoriaReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ActaComentariosReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ActaDto;
import org.crue.hercules.sgi.rep.dto.eti.AsistentesDto;
import org.crue.hercules.sgi.rep.dto.eti.BloqueOutput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportInput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ComentarioDto;
import org.crue.hercules.sgi.rep.dto.eti.FormularioDto;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaEvaluadaDto;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaEvaluadaReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeActa;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.enums.TiposEnumI18n.DictamenI18n;
import org.crue.hercules.sgi.rep.enums.TiposEnumI18n.TipoConvocatoriaReunionI18n;
import org.crue.hercules.sgi.rep.enums.TiposEnumI18n.TipoEvaluacionI18n;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.report.data.ActaComentariosReportData;
import org.crue.hercules.sgi.rep.report.data.objects.AsistenteObject;
import org.crue.hercules.sgi.rep.service.SgiReportDocxService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import com.deepoove.poi.data.Includes;
import com.deepoove.poi.data.RenderData;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe acta de ética
 */
@Service
@Slf4j
@Validated
public class InformeActaReportService extends SgiReportDocxService {

  private final PersonaService personaService;
  private final ConvocatoriaReunionService convocatoriaReunionService;
  private final ActaService actaService;
  private final EvaluacionService evaluacionService;
  private final BaseApartadosRespuestasReportService baseApartadosRespuestasReportService;

  public InformeActaReportService(SgiConfigProperties sgiConfigProperties, PersonaService personaService,
      SgiApiConfService sgiApiConfService,
      ConvocatoriaReunionService convocatoriaReunionService, ActaService actaService,
      EvaluacionService evaluacionService,
      BaseApartadosRespuestasReportService baseApartadosRespuestasReportService) {

    super(sgiConfigProperties, sgiApiConfService);
    this.personaService = personaService;
    this.convocatoriaReunionService = convocatoriaReunionService;
    this.actaService = actaService;
    this.evaluacionService = evaluacionService;
    this.baseApartadosRespuestasReportService = baseApartadosRespuestasReportService;
  }

  private XWPFDocument getDocument(ActaDto acta, HashMap<String, Object> dataReport, InputStream path, Language lang) {
    Locale locale = Locale.forLanguageTag(lang.getCode());
    Assert.notNull(
        acta,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field",
                ApplicationContextSupport.getMessage("org.crue.hercules.sgi.rep.dto.eti.ActaDto.message"))
            .parameter("entity",
                ApplicationContextSupport.getMessage(ActaDto.class))
            .build());

    String i18nDe = ApplicationContextSupport.getMessage("common.de");
    String pattern = String.format("EEEE dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);
    Instant fecha = acta.getConvocatoriaReunion().getFechaEvaluacion();
    dataReport.put("fecha", formatInstantToString(fecha, pattern));

    dataReport.put("numeroActa", acta.getNumero());

    dataReport.put("comite", acta.getConvocatoriaReunion().getComite().getCodigo());

    dataReport.put("nombreInvestigacion",
        I18nHelper.getFieldValue(acta.getConvocatoriaReunion().getComite().getNombre(), lang));

    String patternFechaConv = String.format("dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);
    Instant fechaEvaluacion = acta.getConvocatoriaReunion().getFechaEvaluacion();
    dataReport.put("fechaConvocatoria", formatInstantToString(fechaEvaluacion, patternFechaConv));

    dataReport.put("lugar", I18nHelper.getFieldValue(acta.getConvocatoriaReunion().getLugar(), lang));

    dataReport.put("isVideoconferencia", acta.getConvocatoriaReunion().getVideoconferencia());

    LocalDateTime fechaInicio = LocalDateTime.now().withHour(acta.getHoraInicio()).withMinute(acta.getMinutoInicio());
    LocalDateTime fechaFin = LocalDateTime.now().withHour(acta.getHoraFin()).withMinute(acta.getMinutoFin());

    dataReport.put("horaInicio", fechaInicio.format(DateTimeFormatter.ofPattern("HH:mm")));

    dataReport.put("horaFin", fechaFin.format(DateTimeFormatter.ofPattern("HH:mm")));

    Duration duracionEntreFechas = Duration.between(fechaInicio, fechaFin);
    StringBuilder strDuracion = new StringBuilder();
    long durationHoras = duracionEntreFechas.toHours();
    long durationMinutos = duracionEntreFechas.toMinutes() % 60;
    String i18nY = ApplicationContextSupport.getMessage("common.y");
    String i18nHoras = ApplicationContextSupport.getMessage("common.horas");
    String i18nMinutos = ApplicationContextSupport.getMessage("common.minutos");
    if (durationHoras > 0) {
      strDuracion.append(durationHoras);
      strDuracion.append(" ");
      strDuracion.append(i18nHoras);
      strDuracion.append(" ");
      strDuracion.append(i18nY);
      strDuracion.append(" ");

    }
    strDuracion.append(durationMinutos);
    strDuracion.append(" ");
    strDuracion.append(i18nMinutos);
    dataReport.put("duracion", strDuracion.toString());

    dataReport.put("tipoConvocatoria", TipoConvocatoriaReunionI18n
        .getI18nMessageFromEnumAndLocale(acta.getConvocatoriaReunion().getTipoConvocatoriaReunion().getId(), locale));

    dataReport.put("resumenActa", I18nHelper.getFieldValue(acta.getResumen(), lang));

    String codigoActa = acta.getNumero() + "/" + formatInstantToString(fechaEvaluacion, "YYYY") + "/" + acta
        .getConvocatoriaReunion().getComite().getCodigo();
    dataReport.put("codigoActa", codigoActa);

    Long numeroEvaluacionesNuevas = actaService.countEvaluacionesNuevas(acta.getId());
    dataReport.put("numeroEvaluacionesNuevas", null != numeroEvaluacionesNuevas ? numeroEvaluacionesNuevas : 0);

    Long numeroEvaluacionesRevisiones = actaService.countEvaluacionesRevisionSinMinima(acta.getId());
    dataReport.put("numeroEvaluacionesRevisiones",
        null != numeroEvaluacionesRevisiones ? numeroEvaluacionesRevisiones : 0);

    dataReport.put("ordenDelDia", I18nHelper.getFieldValue(acta.getConvocatoriaReunion().getOrdenDia(), lang));

    List<MemoriaEvaluadaDto> memorias = actaService.findAllMemoriasEvaluadasSinRevMinimaByActaId(acta.getId());

    Optional<MemoriaEvaluadaDto> memoriasEvaluadasNoFavorables = memorias
        .stream().filter(memoria -> !memoria.getDictamenId().equals(DictamenI18n.FAVORABLE.getId())
            && (memoria.getTipoEvaluacionId().equals(TipoEvaluacionI18n.MEMORIA.getId())
                || memoria.getTipoEvaluacionId().equals(TipoEvaluacionI18n.SEGUIMIENTO_ANUAL.getId())
                || memoria.getTipoEvaluacionId().equals(TipoEvaluacionI18n.SEGUIMIENTO_FINAL.getId())))
        .findAny();

    dataReport.put("existsComentarios", memoriasEvaluadasNoFavorables.isPresent());

    addDataAsistentes(acta, dataReport, lang);

    getTableMemoriasEvaluadas(acta, dataReport, locale);

    dataReport.put("bloqueApartados",
        generarBloqueApartados(getActaComentariosSubReport(acta.getId(), lang), lang));

    return compileReportData(path, dataReport);
  }

  private void addDataAsistentes(ActaDto acta, HashMap<String, Object> dataReport, Language lang) {

    List<AsistentesDto> asistentes = convocatoriaReunionService
        .findAsistentesByConvocatoriaReunionId(acta.getConvocatoriaReunion().getId());

    List<AsistenteObject> asistentesReportData = new ArrayList<>();
    asistentes.forEach(asistente -> {
      AsistenteObject asistenteReportData = new AsistenteObject();
      try {
        PersonaDto persona = personaService.findById(asistente.getEvaluador().getPersonaRef());
        asistenteReportData.setPersona(persona);
      } catch (Exception e) {
        log.error(e.getMessage());
      }
      asistenteReportData.setMotivo(I18nHelper.getFieldValue(asistente.getMotivo(), lang));
      asistentesReportData.add(asistenteReportData);
    });

    dataReport.put("asistentes", asistentesReportData);

  }

  public byte[] getReportInformeActa(ReportInformeActa sgiReport, Long idActa) {
    getReportFromIdActa(sgiReport, idActa);
    return sgiReport.getContent();
  }

  private XWPFDocument getReportFromIdActa(SgiReportDto sgiReport, Long idActa) {
    try {
      HashMap<String, Object> dataReport = new HashMap<>();
      ActaDto acta = actaService.findById(idActa);

      dataReport.put("headerImg", getImageHeaderLogo());

      XWPFDocument document = getDocument(acta, dataReport,
          getReportDefinitionStream(sgiReport.getPath()), sgiReport.getLang());

      ByteArrayOutputStream outputPdf = new ByteArrayOutputStream();
      PdfOptions pdfOptions = createCustomPdfOptions();

      PdfConverter.getInstance().convert(document, outputPdf, pdfOptions);

      sgiReport.setContent(outputPdf.toByteArray());
      return document;

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException(e);
    }
  }

  private void getTableMemoriasEvaluadas(ActaDto acta, HashMap<String, Object> dataReport, Locale locale) {

    List<MemoriaEvaluadaDto> memorias = actaService.findAllMemoriasEvaluadasSinRevMinimaByActaId(acta.getId());

    List<MemoriaEvaluadaReportOutput> memoriasEvaluadas = new ArrayList<>();
    memorias.forEach(memoria -> {
      MemoriaEvaluadaReportOutput memoriaEvaluada = new MemoriaEvaluadaReportOutput();
      memoriaEvaluada.setDictamen(DictamenI18n.getI18nMessageFromEnumAndLocale(memoria.getDictamenId(), locale));
      memoriaEvaluada.setEvaluacionId(memoria.getEvaluacionId());
      memoriaEvaluada.setId(memoria.getId());
      memoriaEvaluada.setNumReferencia(memoria.getNumReferencia());
      memoriaEvaluada.setPersonaRef(memoria.getPersonaRef());
      memoriaEvaluada
          .setTipoEvaluacion(TipoEvaluacionI18n.getI18nMessageFromEnumAndLocale(memoria.getTipoEvaluacionId(), locale));
      memoriaEvaluada.setTitulo(I18nHelper.getFieldValue(memoria.getTitulo()));
      memoriaEvaluada.setVersion(memoria.getVersion());
      try {
        PersonaDto persona = personaService.findById(memoria.getPersonaRef());
        memoriaEvaluada.setResponsable(persona);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
      memoriasEvaluadas.add(memoriaEvaluada);
    });

    dataReport.put("isMemoriasEvaluadas", !memorias.isEmpty());
    dataReport.put("memoriasEvaluadas", memoriasEvaluadas);

  }

  /**
   * Devuelve el contenido para generar el subreport de comentarios del acta
   *
   * @param actaId Id del acta
   * @return ActaComentariosReportOutput Datos a presentar en el informe
   */
  private ActaComentariosReportOutput getActaComentariosSubReport(Long actaId, Language lang) {
    log.debug("getActaComentariosSubReport(actaId) - start");
    Locale locale = Locale.forLanguageTag(lang.getCode());
    Assert.notNull(
        actaId,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity",
                ApplicationContextSupport.getMessage(ActaDto.class))
            .build());

    ActaComentariosReportOutput actaComentariosSubReportOutput = new ActaComentariosReportOutput(lang);
    actaComentariosSubReportOutput.setComentariosMemoria(new ArrayList<>());

    try {

      List<MemoriaEvaluadaDto> memorias = actaService.findAllMemoriasEvaluadasSinRevMinimaByActaId(actaId);

      memorias.stream().filter(memoria -> !memoria.getDictamenId().equals(DictamenI18n.FAVORABLE.getId())
          && (memoria.getTipoEvaluacionId().equals(TipoEvaluacionI18n.MEMORIA.getId())
              || memoria.getTipoEvaluacionId().equals(TipoEvaluacionI18n.SEGUIMIENTO_ANUAL.getId())
              || memoria.getTipoEvaluacionId().equals(TipoEvaluacionI18n.SEGUIMIENTO_FINAL.getId())))
          .forEach(memoria -> {
            ActaComentariosMemoriaReportOutput comentariosMemoriaReportOutput = new ActaComentariosMemoriaReportOutput();
            comentariosMemoriaReportOutput.setNumReferenciaMemoria(memoria.getNumReferencia());
            comentariosMemoriaReportOutput.setTituloProyecto(I18nHelper.getFieldValue(memoria.getTitulo(), lang));
            comentariosMemoriaReportOutput
                .setDictamen(DictamenI18n.getI18nMessageFromEnumAndLocale(memoria.getDictamenId(), locale));
            comentariosMemoriaReportOutput.setBloques(new ArrayList<>());

            try {
              PersonaDto persona = personaService.findById(memoria.getPersonaRef());
              comentariosMemoriaReportOutput.setResponsable(persona.getNombre() + " " +
                  persona.getApellidos());
            } catch (Exception e) {
              log.error(e.getMessage(), e);
            }

            List<ComentarioDto> comentarios = evaluacionService.findByEvaluacionIdGestor(memoria.getEvaluacionId());

            if (null != comentarios && !comentarios.isEmpty()) {
              comentariosMemoriaReportOutput.setNumComentarios(comentarios.size());

              final Set<Long> apartados = new HashSet<>();
              comentarios
                  .forEach(
                      c -> baseApartadosRespuestasReportService.getApartadoService().findTreeApartadosById(apartados,
                          c.getApartado()));

              Long idFormulario = 0L;

              Optional<FormularioDto> formulario = comentarios.stream()
                  .map(c -> c.getApartado().getBloque().getFormulario())
                  .filter(f -> f != null)
                  .findFirst();

              if (formulario.isPresent()) {
                idFormulario = formulario.get().getId();
              }

          // @formatter:off
              BloquesReportInput etiBloquesReportInput = BloquesReportInput.builder()
              .idMemoria(memoria.getId())
              .idFormulario(idFormulario)
              .mostrarRespuestas(false)
              .mostrarContenidoApartado(false)
              .comentarios(comentarios)
              .apartados(apartados)
              .build();
              // @formatter:on

              BloquesReportOutput reportOutput = baseApartadosRespuestasReportService
                  .getDataFromApartadosAndRespuestas(etiBloquesReportInput, lang);

              final int orden = comentariosMemoriaReportOutput.getBloques().size();
              for (BloqueOutput bloque : reportOutput.getBloques()) {
                bloque.setOrden(bloque.getOrden() + orden);
              }

              comentariosMemoriaReportOutput.getBloques().addAll(reportOutput.getBloques());
            }
            actaComentariosSubReportOutput.getComentariosMemoria().add(comentariosMemoriaReportOutput);
          });

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException(e);
    }

    log.debug("getActaComentariosSubReport(actaId) - end");

    return actaComentariosSubReportOutput;
  }

  private RenderData generarBloqueApartados(ActaComentariosReportOutput actaComentariosReportOutput, Language lang) {
    ActaComentariosReportData reportData = new ActaComentariosReportData(lang);
    if (ObjectUtils.isNotEmpty(actaComentariosReportOutput)
        && ObjectUtils.isNotEmpty(actaComentariosReportOutput.getComentariosMemoria())
        && ObjectUtils
            .isNotEmpty(actaComentariosReportOutput.getComentariosMemoria().stream().findAny().get().getBloques())
        && actaComentariosReportOutput.getComentariosMemoria().stream().findAny().get().getBloques().size() > 0) {
      reportData.setComentarios(actaComentariosReportOutput.getComentariosMemoria());
    } else {
      return null;
    }
    return Includes.ofStream(getReportDefinitionStream(
        actaComentariosReportOutput.getReportName()))
        .setRenderModel(reportData.getDataReport()).create();
  }

}