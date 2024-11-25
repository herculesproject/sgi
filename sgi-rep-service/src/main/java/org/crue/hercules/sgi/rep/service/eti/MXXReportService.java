package org.crue.hercules.sgi.rep.service.eti;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.crue.hercules.sgi.framework.i18n.I18nConfig;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.ApartadoDefinicionDto;
import org.crue.hercules.sgi.rep.dto.eti.ApartadoTreeDto;
import org.crue.hercules.sgi.rep.dto.eti.BloqueDto;
import org.crue.hercules.sgi.rep.dto.eti.BloqueNombreDto;
import org.crue.hercules.sgi.rep.dto.eti.FormularioDto;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaDto;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaPeticionEvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.ReportMXX;
import org.crue.hercules.sgi.rep.dto.eti.RespuestaInput;
import org.crue.hercules.sgi.rep.dto.sgp.DatosContactoDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto.VinculacionDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.report.data.MXXReportData;
import org.crue.hercules.sgi.rep.service.SgiReportDocxService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.crue.hercules.sgi.rep.util.AssertHelper;
import org.crue.hercules.sgi.rep.util.CustomSpELRenderDataCompute;
import org.crue.hercules.sgi.rep.util.SgiHtmlRenderPolicy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

import com.deepoove.poi.config.Configure;
import com.deepoove.poi.jsonmodel.support.DefaultGsonHandler;
import com.deepoove.poi.jsonmodel.support.GsonHandler;
import com.deepoove.poi.jsonmodel.support.GsonPreRenderDataCastor;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informes de ética M10, M20, M30
 */
@Service
@Slf4j
@Validated
public class MXXReportService extends SgiReportDocxService {

  private final SgiConfigProperties sgiConfigProperties;
  private final MemoriaService memoriaService;
  private final BloqueService bloqueService;
  private final PeticionEvaluacionService peticionEvaluacionService;
  private final PersonaService personaService;
  private final FormularioService formularioService;

  public MXXReportService(SgiConfigProperties sgiConfigProperties, SgiApiConfService sgiApiConfService,
      MemoriaService memoriaService, BloqueService bloqueService, PeticionEvaluacionService peticionEvaluacionService,
      PersonaService personaService,
      FormularioService formularioService) {

    super(sgiConfigProperties, sgiApiConfService);
    this.sgiConfigProperties = sgiConfigProperties;
    this.memoriaService = memoriaService;
    this.bloqueService = bloqueService;
    this.peticionEvaluacionService = peticionEvaluacionService;
    this.personaService = personaService;
    this.formularioService = formularioService;
  }

  /**
   * Devuelve un formulario M10, M20 o M30 a partir del microservicio de eti
   * 
   * @param reportMXX    SgiReport
   * @param memoriaId    Id de la memoria
   * @param formularioId Id del formulario
   * @return byte[] Report
   */
  public byte[] getReport(ReportMXX reportMXX, Long memoriaId, Long formularioId) {
    AssertHelper.idNotNull(memoriaId, MemoriaDto.class);
    AssertHelper.idNotNull(formularioId, FormularioDto.class);

    MemoriaDto memoria = memoriaService.findById(memoriaId);
    List<RespuestaInput> respuestas = memoriaService.getRespuestas(memoriaId);
    boolean isMemoriaModificacion = memoria.getMemoriaOriginal() != null;
    List<RespuestaInput> respuestasMemoriaOriginal = isMemoriaModificacion
        ? memoriaService.getRespuestas(memoria.getMemoriaOriginal().getId())
        : Collections.emptyList();
    List<BloqueDto> bloques = bloqueService.findByFormularioId(formularioId);

    List<MemoriaPeticionEvaluacionDto> memoriasPeticionEvaluacion = peticionEvaluacionService
        .getMemorias(memoria.getPeticionEvaluacion().getId());

    PersonaDto tutor = null;
    if (StringUtils.isNotBlank(memoria.getPeticionEvaluacion().getTutorRef())) {
      tutor = personaService.findById(memoria.getPeticionEvaluacion().getTutorRef());
    }

    PersonaDto solicitante = personaService.findById(memoria.getPeticionEvaluacion().getPersonaRef());
    DatosContactoDto solicitanteDatosContacto = personaService.getDatosContacto(memoria.getPeticionEvaluacion()
        .getPersonaRef());
    VinculacionDto solicitanteVinculacion = personaService.getVinculacion(memoria.getPeticionEvaluacion()
        .getPersonaRef());

    List<ApartadoTreeDto> apartados = new ArrayList<>();
    bloques.forEach(bloque -> apartados.addAll(bloqueService.getApartados(bloque.getId())));

    try {
      Map<String, Object> dataReport = getReportModelFormulario(bloques, apartados, respuestas,
          isMemoriaModificacion, respuestasMemoriaOriginal, reportMXX.getLang());

      MXXReportData reportData = new MXXReportData(dataReport, reportMXX.getLang());
      reportData.setHeaderLogo(getImageHeaderLogo());
      reportData.setFormularioId(formularioId);
      reportData.setMemoria(memoria);
      reportData.setMemoriasPeticionEvaluacion(memoriasPeticionEvaluacion);
      reportData.setSolicitante(solicitante);
      reportData.setSolicitanteDatosContacto(solicitanteDatosContacto);
      reportData.setSolicitanteVinculacion(solicitanteVinculacion);
      reportData.setTutor(tutor);
      reportData.setZoneId(sgiConfigProperties.getTimeZone());

      FormularioDto formulario = getFormularioFromMemoria(memoria, formularioId);

      XWPFDocument document = getDocument(reportData.getDataReport(),
          getReportDefinitionStream(formulario, reportMXX.getLang()), reportMXX.getLang());

      ByteArrayOutputStream outputPdf = new ByteArrayOutputStream();
      PdfOptions pdfOptions = createCustomPdfOptions();

      PdfConverter.getInstance().convert(document, outputPdf, pdfOptions);

      return outputPdf.toByteArray();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException(e);
    }
  }

  private FormularioDto getFormularioFromMemoria(MemoriaDto memoria, Long formularioId) {
    if (memoria.getFormulario().getId().equals(formularioId)) {
      return memoria.getFormulario();
    }
    if (memoria.getFormularioSeguimientoAnual().getId().equals(formularioId)) {
      return memoria.getFormularioSeguimientoAnual();
    }
    if (memoria.getFormularioSeguimientoFinal().getId().equals(formularioId)) {
      return memoria.getFormularioSeguimientoFinal();
    }
    if (memoria.getFormularioRetrospectiva().getId().equals(formularioId)) {
      return memoria.getFormularioRetrospectiva();
    }
    return null;
  }

  private InputStream getReportDefinitionStream(FormularioDto formulario, Language language) {
    List<Language> otherLanguages = new ArrayList<>();
    for (Language lang : I18nConfig.get().getEnabledLanguages()) {
      if (lang != language) {
        otherLanguages.add(lang);
      }
    }
    try {
      if (formularioService.existsReport(formulario.getId(), language)) {
        byte[] reportDefinition = formularioService.getReport(formulario.getId(), language);
        return new ByteArrayInputStream(reportDefinition);
      } else {
        for (Language lang : otherLanguages) {
          if (formularioService.existsReport(formulario.getId(), lang)) {
            byte[] reportDefinition = formularioService.getReport(formulario.getId(), language);
            return new ByteArrayInputStream(reportDefinition);
          }
        }
      }

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException(e);
    }
    throw new GetDataReportException(
        new NotFoundException("No se ha encontrado ningún report para el formulario: " + formulario.getCodigo()));
  }

  protected XWPFDocument getDocument(Map<String, Object> modelReport, InputStream path, Language defaultLanguage) {
    Configure config = Configure.builder()
        .useSpringEL(false)
        .addPreRenderDataCastor(new GsonPreRenderDataCastor())
        .setRenderDataComputeFactory(
            model -> {
              try {
                return new CustomSpELRenderDataCompute(modelReport, model, false, Collections.emptyMap(),
                    defaultLanguage);
              } catch (NoSuchMethodException | SecurityException e) {
                log.error(e.getMessage(), e);
                return null;
              }
            })
        .addPlugin('<', new SgiHtmlRenderPolicy())
        .build();

    // Reduce el ratio a la mitad para evitar que se produzca una Zip Bomb exception
    // al procesar el docx
    ZipSecureFile.setMinInflateRatio(0.005d);

    return compileReportData(path, config, modelReport);
  }

  private Map<String, Object> getReportModelFormulario(List<BloqueDto> bloques, List<ApartadoTreeDto> apartados,
      List<RespuestaInput> respuestas, boolean isMemoriaModificacion, List<RespuestaInput> respuestasMemoriaOriginal,
      Language lang) {
    Map<String, Object> reportModel = new HashMap<>();

    bloques.stream().forEach(bloque -> {
      List<ApartadoTreeDto> apartadosBloque = apartados.stream()
          .filter(apartado -> apartado.getBloqueId().equals(bloque.getId()))
          .collect(Collectors.toList());
      List<RespuestaInput> respuestasApartadosBloque = respuestas.stream()
          .filter(respuesta -> apartadosBloque.stream().mapToLong(ApartadoTreeDto::getId)
              .anyMatch(id -> respuesta.getApartadoId().equals(id)))
          .collect(Collectors.toList());

      if (isMemoriaModificacion) {
        List<RespuestaInput> respuestasApartadosBloqueMemoriaOriginal = respuestasMemoriaOriginal.stream()
            .filter(respuesta -> apartadosBloque.stream().mapToLong(ApartadoTreeDto::getId)
                .anyMatch(id -> respuesta.getApartadoId().equals(id)))
            .collect(Collectors.toList());
        reportModel.putAll(getReportModelBloque(bloque, apartadosBloque, respuestasApartadosBloque,
            respuestasApartadosBloqueMemoriaOriginal, lang));
      } else {
        reportModel.putAll(getReportModelBloque(bloque, apartadosBloque, respuestasApartadosBloque, lang));
      }

    });

    return reportModel;
  }

  private Map<String, Object> getReportModelBloque(BloqueDto bloque, List<ApartadoTreeDto> apartados,
      List<RespuestaInput> respuestas, Language lang) {
    Map<String, Object> reportModel = new HashMap<>();
    Map<String, Object> bloqueModel = new HashMap<>();
    Optional<BloqueNombreDto> bloqueNombre = bloque.getNombre().stream()
        .filter(b -> b.getLang().equalsIgnoreCase(lang.getCode())).findFirst();
    bloqueModel.put("orden", bloque.getOrden());
    bloqueModel.put("nombre", bloqueNombre.get().getValue());
    bloqueModel.put("bloque", bloque);
    apartados.forEach(
        apartado -> bloqueModel.putAll(getReportModelApartado(apartado, respuestas, null, lang)));

    if (bloque.getFormulario().getId() <= 6) {
      reportModel.put("bloque_" + bloque.getId(), bloqueModel);
    } else {
      reportModel.put("bloque_" + bloque.getOrden(), bloqueModel);
    }

    return reportModel;
  }

  private Map<String, Object> getReportModelBloque(BloqueDto bloque, List<ApartadoTreeDto> apartados,
      List<RespuestaInput> respuestas, List<RespuestaInput> respuestasMemoriaOriginal, Language lang) {
    Map<String, Object> reportModel = new HashMap<>();
    Map<String, Object> bloqueModel = new HashMap<>();
    Optional<BloqueNombreDto> bloqueNombre = bloque.getNombre().stream()
        .filter(b -> b.getLang().equalsIgnoreCase(lang.getCode())).findFirst();
    bloqueModel.put("orden", bloque.getOrden());
    bloqueModel.put("nombre", bloqueNombre.get().getValue());
    bloqueModel.put("bloque", bloque);
    apartados.forEach(
        apartado -> bloqueModel
            .putAll(getReportModelApartado(apartado, respuestas, respuestasMemoriaOriginal, null, lang)));

    if (bloque.getFormulario().getId() <= 6) {
      reportModel.put("bloque_" + bloque.getId(), bloqueModel);
    } else {
      reportModel.put("bloque_" + bloque.getOrden(), bloqueModel);
    }

    return reportModel;
  }

  private Map<String, Object> getReportModelApartado(ApartadoTreeDto apartado, List<RespuestaInput> respuestas,
      JsonObject respuestaPadreJson, Language lang) {
    return getReportModelApartado(apartado, respuestas, null, respuestaPadreJson, lang);
  }

  /**
   * Obtiene el apartado con toda su jerarquia de subapartados
   * 
   * @param apartado                  un {@link ApartadoTreeDto}
   * @param respuestas                lista de {@link RespuestaInput} del apartado
   *                                  de
   *                                  primer nivel (contiene las de todos los
   *                                  subapartados)
   * @param respuestasMemoriaOriginal lista de {@link RespuestaInput} del apartado
   *                                  de
   *                                  primer nivel (contiene las de todos los
   *                                  subapartados) de la memoria original (null
   *                                  si no es una memoria de modificacion)
   * @param respuestaPadreJson        respuesta en formato json del apartado padre
   *                                  para
   *                                  poder recuperar la respuesta de los
   *                                  apartados a
   *                                  partir del 3 nivel
   * @return el model del apartado
   */
  private Map<String, Object> getReportModelApartado(ApartadoTreeDto apartado, List<RespuestaInput> respuestas,
      List<RespuestaInput> respuestasMemoriaOriginal,
      JsonObject respuestaPadreJson, Language lang) {
    Map<String, Object> reportModel = new HashMap<>();
    Map<String, Object> apartadoModel = new HashMap<>();

    GsonHandler provider = new DefaultGsonHandler();
    Optional<ApartadoDefinicionDto> apartadoDefinicion = apartado.getDefinicion().stream()
        .filter(d -> d.getLang().equalsIgnoreCase(lang.getCode()))
        .findFirst();
    JsonArray apartadoJson = new Gson().fromJson(apartadoDefinicion.get().getEsquema(), JsonArray.class);
    if (!ObjectUtils.isEmpty(apartadoJson.get(0).getAsJsonObject().get("key"))) {
      String apartadoKey = apartadoJson.get(0).getAsJsonObject().get("key").getAsString();
      apartadoModel.put("apartado", apartado);
      apartadoModel.put("esquema",
          provider.parser().fromJson(apartadoDefinicion.get().getEsquema(), ArrayList.class).get(0));

      Optional<RespuestaInput> respuesta = respuestas.stream()
          .filter(r -> r.getApartadoId().equals(apartado.getId())).findFirst();

      boolean isModificado = false;
      if (respuestasMemoriaOriginal != null) {
        Optional<RespuestaInput> respuestaMemoriaOriginal = respuestasMemoriaOriginal.stream()
            .filter(r -> r.getApartadoId().equals(apartado.getId())).findFirst();

        isModificado = respuesta.isPresent() != respuestaMemoriaOriginal.isPresent()
            || (respuesta.isPresent() && respuestaMemoriaOriginal.isPresent()
                && !respuesta.get().getValor().equals(respuestaMemoriaOriginal.get().getValor()));
      }

      apartadoModel.put("isModificado", isModificado);

      JsonObject respuestaApartadoJson = null;
      Object respuestaObject = null;
      if (respuesta.isPresent()) {
        respuestaObject = provider.parser().fromJson(respuesta.get().getValor(), Object.class);
      } else if (apartado.getPadreId() != null) {
        RespuestaInput respuestaApartado = respuestas.stream()
            .filter(r -> r.getApartadoId().equals(apartado.getPadreId())).findFirst().orElse(null);

        if (respuestaApartado != null) {
          JsonObject respuestaJson = new Gson().fromJson(respuestaApartado.getValor(), JsonObject.class);
          respuestaApartadoJson = respuestaJson.get(apartadoKey).getAsJsonObject();
          respuestaObject = new Gson().fromJson(respuestaJson.get(apartadoKey), Object.class);
        } else if (respuestaPadreJson != null) {
          JsonObject respuestaJson = respuestaPadreJson;
          respuestaApartadoJson = respuestaPadreJson.get(apartadoKey).getAsJsonObject();
          respuestaObject = new Gson().fromJson(respuestaJson.get(apartadoKey), Object.class);
        }
      }

      if (respuestaObject != null) {
        apartadoModel.put("respuesta", respuestaObject);
      }

      for (ApartadoTreeDto apartadoHijo : apartado.getHijos()) {
        apartadoModel.putAll(getReportModelApartado(apartadoHijo, respuestas, respuestaApartadoJson, lang));
      }

      reportModel.put(apartadoKey, apartadoModel);
    } else {
      apartadoModel.put("apartado", apartado);
      apartadoModel.put("esquema", "-");
      apartadoModel.put("isModificado", false);
    }

    return reportModel;
  }

}
