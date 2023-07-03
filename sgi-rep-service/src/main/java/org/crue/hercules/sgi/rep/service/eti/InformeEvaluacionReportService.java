package org.crue.hercules.sgi.rep.service.eti;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.BloqueOutput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportInput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ComentarioDto;
import org.crue.hercules.sgi.rep.dto.eti.ComiteDto.Genero;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.FormularioDto;
import org.crue.hercules.sgi.rep.dto.eti.InformeEvaluacionEvaluadorReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeEvaluacion;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiSgpService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import com.deepoove.poi.data.Includes;
import com.deepoove.poi.data.RenderData;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe de evaluación de ética
 */
@Service
@Slf4j
@Validated
public class InformeEvaluacionReportService extends InformeEvaluacionBaseReportService {

  private final EvaluacionService evaluacionService;
  private final ConfiguracionService configuracionService;
  private final BaseApartadosRespuestasReportDocxService baseApartadosRespuestasService;

  private static final Long TIPO_COMENTARIO_GESTOR = 1L;
  private static final Long DICTAMEN_PENDIENTE_CORRECCIONES = 3L;
  private static final Long DICTAMEN_NO_PROCEDE_EVALUAR = 4L;
  public static final Long DICTAMEN_FAVORABLE_PENDIENTE_REVISION_MINIMA = 2L;
  public static final Long TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_ANUAL = 13L;
  public static final Long TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_FINAL = 19L;
  public static final Long TIPO_EVALUACION_RETROSPECTIVA = 1L;
  public static final Long TIPO_EVALUACION_SEGUIMIENTO_ANUAL = 3L;
  public static final Long TIPO_EVALUACION_SEGUIMIENTO_FINAL = 4L;

  public InformeEvaluacionReportService(SgiConfigProperties sgiConfigProperties,
      SgiApiConfService sgiApiConfService, SgiApiSgpService personaService,
      EvaluacionService evaluacionService,
      ConfiguracionService configuracionService,
      BaseApartadosRespuestasReportDocxService baseApartadosRespuestasService) {

    super(sgiConfigProperties, sgiApiConfService, personaService, evaluacionService);
    this.evaluacionService = evaluacionService;
    this.configuracionService = configuracionService;
    this.baseApartadosRespuestasService = baseApartadosRespuestasService;
  }

  protected XWPFDocument getDocument(EvaluacionDto evaluacion, HashMap<String, Object> dataReport, InputStream path) {

    Assert.notNull(
        evaluacion,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field",
                ApplicationContextSupport.getMessage("org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto.message"))
            .parameter("entity",
                ApplicationContextSupport.getMessage(EvaluacionDto.class))
            .build());

    dataReport.put("referenciaMemoria", evaluacion.getMemoria().getNumReferencia());

    dataReport.put("version", evaluacion.getVersion());

    addDataPersona(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef(), dataReport);

    dataReport.put("titulo", evaluacion.getMemoria().getPeticionEvaluacion().getTitulo());

    String i18nDe = ApplicationContextSupport.getMessage("common.de");
    String pattern = String.format("EEEE dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);
    dataReport.put("fechaDictamen",
        formatInstantToString(evaluacion.getConvocatoriaReunion().getFechaEvaluacion(), pattern));

    dataReport.put("numeroActa", evaluacion.getConvocatoriaReunion().getNumeroActa());

    dataReport.put("idComite", evaluacion.getMemoria().getComite().getId());

    dataReport.put("comite", evaluacion.getMemoria().getComite().getComite());

    dataReport.put("nombreInvestigacion", evaluacion.getMemoria().getComite().getNombreInvestigacion());

    if (ObjectUtils.isNotEmpty(evaluacion.getMemoria().getEstadoActual())) {
      dataReport.put("retrospectiva", !evaluacion.getMemoria().getEstadoActual().getId().equals(
          TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_ANUAL)
          && !evaluacion.getMemoria().getEstadoActual().getId()
              .equals(TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_FINAL)
          && ObjectUtils.isNotEmpty(evaluacion.getMemoria().getRetrospectiva())
          && evaluacion.getMemoria().getRetrospectiva().getEstadoRetrospectiva().getId() > 1
          && evaluacion.getTipoEvaluacion().getId().equals(TIPO_EVALUACION_RETROSPECTIVA));
    } else {
      dataReport.put("retrospectiva", false);
    }

    if (ObjectUtils.isNotEmpty(evaluacion.getTipoEvaluacion())) {
      dataReport.put("seguimientoAnual",
          evaluacion.getTipoEvaluacion().getId().equals(TIPO_EVALUACION_SEGUIMIENTO_ANUAL));
    } else {
      dataReport.put("seguimientoAnual", false);
    }

    if (ObjectUtils.isNotEmpty(evaluacion.getTipoEvaluacion())) {
      dataReport.put("seguimientoFinal",
          evaluacion.getTipoEvaluacion().getId().equals(TIPO_EVALUACION_SEGUIMIENTO_FINAL));
    } else {
      dataReport.put("seguimientoFinal", false);
    }

    if (evaluacion.getMemoria().getComite().getGenero().equals(Genero.M)) {
      dataReport.put("preposicionComite", ApplicationContextSupport.getMessage("common.del"));
    } else {
      dataReport.put("preposicionComite", ApplicationContextSupport.getMessage("common.dela"));
    }

    if (evaluacion.getMemoria().getComite().getGenero().equals(Genero.M)) {
      dataReport.put("comisionComite", ApplicationContextSupport.getMessage("comite.comision.masculino"));
    } else {
      dataReport.put("comisionComite", ApplicationContextSupport.getMessage("comite.comision.femenino"));
    }

    dataReport.put("idDictamenPendienteCorrecciones", DICTAMEN_PENDIENTE_CORRECCIONES);

    dataReport.put("idDictamenNoProcedeEvaluar", DICTAMEN_NO_PROCEDE_EVALUAR);

    dataReport.put("idDictamenPendienteRevisionMinima", DICTAMEN_FAVORABLE_PENDIENTE_REVISION_MINIMA);

    dataReport.put("idDictamen", evaluacion.getDictamen().getId());

    dataReport.put("dictamen", evaluacion.getDictamen().getNombre());

    dataReport.put("comentarioNoProcedeEvaluar", null != evaluacion.getComentario() ? evaluacion.getComentario() : "");

    Integer mesesArchivadaPendienteCorrecciones = configuracionService.findConfiguracion()
        .getMesesArchivadaPendienteCorrecciones();
    dataReport.put("mesesArchivadaPendienteCorrecciones", mesesArchivadaPendienteCorrecciones);

    Integer diasArchivadaPendienteCorrecciones = configuracionService.findConfiguracion().getDiasArchivadaInactivo();
    dataReport.put("diasArchivadaPendienteCorrecciones", diasArchivadaPendienteCorrecciones);

    addDataActividad(evaluacion, dataReport);

    dataReport.put("bloqueApartados",
        generarBloqueApartados(evaluacion.getDictamen().getId(),
            this.getInformeEvaluadorEvaluacion(evaluacion.getId())));

    return compileReportData(path, dataReport);
  }

  private RenderData generarBloqueApartados(Long idDictamen,
      InformeEvaluacionEvaluadorReportOutput informeEvaluacionEvaluadorReportOutput) {
    Map<String, Object> subDataBloqueApartado = new HashMap<>();
    subDataBloqueApartado.put("idDictamen", idDictamen);
    subDataBloqueApartado.put("idDictamenNoProcedeEvaluar", DICTAMEN_NO_PROCEDE_EVALUAR);
    if (ObjectUtils.isNotEmpty(informeEvaluacionEvaluadorReportOutput)
        && ObjectUtils.isNotEmpty(informeEvaluacionEvaluadorReportOutput.getBloques())
        && informeEvaluacionEvaluadorReportOutput.getBloques().size() > 0) {
      if (informeEvaluacionEvaluadorReportOutput.getBloques().stream().findAny().isPresent()
          && informeEvaluacionEvaluadorReportOutput.getBloques().stream().findAny().get().getApartados().stream()
              .findAny().isPresent()) {
        subDataBloqueApartado.put("numComentarios",
            informeEvaluacionEvaluadorReportOutput.getBloques().stream().findAny().get().getApartados().stream()
                .findAny().get().getNumeroComentariosGestor());
      }
      subDataBloqueApartado.put("bloques", informeEvaluacionEvaluadorReportOutput.getBloques());
    } else {
      subDataBloqueApartado.put("numComentarios", null);
      subDataBloqueApartado.put("bloques", null);
    }
    return Includes.ofStream(getReportDefinitionStream("rep-eti-bloque-apartado-docx"))
        .setRenderModel(subDataBloqueApartado).create();
  }

  /**
   * Devuelve un informe pdf del informe de evaluación
   *
   * @param idEvaluacion Id de la evaluación
   * @return EtiInformeEvaluacionEvaluadorReportOutput Datos a presentar en el
   *         informe
   */
  protected InformeEvaluacionEvaluadorReportOutput getInformeEvaluadorEvaluacion(Long idEvaluacion) {
    log.debug("getInformeEvaluacion(idEvaluacion)- start");

    Assert.notNull(idEvaluacion,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity",
                ApplicationContextSupport.getMessage(EvaluacionDto.class))
            .build());

    InformeEvaluacionEvaluadorReportOutput informeEvaluacionEvaluadorReportOutput = new InformeEvaluacionEvaluadorReportOutput();
    informeEvaluacionEvaluadorReportOutput.setBloques(new ArrayList<>());

    try {

      EvaluacionDto evaluacion = evaluacionService.findById(idEvaluacion);
      informeEvaluacionEvaluadorReportOutput.setEvaluacion(evaluacion);

      Integer numComentariosGestor = evaluacionService.countByEvaluacionIdAndTipoComentarioId(evaluacion.getId(),
          TIPO_COMENTARIO_GESTOR);

      List<ComentarioDto> comentarios = evaluacionService.findByEvaluacionIdGestor(idEvaluacion);
      if (null != comentarios && !comentarios.isEmpty()) {
        final Set<Long> apartados = new HashSet<>();
        comentarios.forEach(
            c -> baseApartadosRespuestasService.getApartadoService().findTreeApartadosById(apartados, c.getApartado()));

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
        .idMemoria(idEvaluacion)
        .idFormulario(idFormulario)
        .mostrarRespuestas(false)
        .mostrarContenidoApartado(false)
        .comentarios(comentarios)
        .apartados(apartados)
        .numeroComentariosGestor(numComentariosGestor)
        .build();
        // @formatter:on

        BloquesReportOutput reportOutput = baseApartadosRespuestasService
            .getDataFromApartadosAndRespuestas(etiBloquesReportInput);

        final int orden = informeEvaluacionEvaluadorReportOutput.getBloques().size();
        for (BloqueOutput bloque : reportOutput.getBloques()) {
          bloque.setOrden(bloque.getOrden() + orden);
        }

        informeEvaluacionEvaluadorReportOutput.getBloques().addAll(reportOutput.getBloques());
      } else {

        BloquesReportInput etiBloquesReportInput = BloquesReportInput.builder()
            .idMemoria(idEvaluacion)
            .idFormulario(0L)
            .mostrarRespuestas(false)
            .mostrarContenidoApartado(false)
            .comentarios(null)
            .apartados(null)
            .numeroComentariosGestor(numComentariosGestor)
            .build();

        BloquesReportOutput reportOutput = baseApartadosRespuestasService
            .getDataFromApartadosAndRespuestas(etiBloquesReportInput);

        if (informeEvaluacionEvaluadorReportOutput.getBloques().isEmpty() && ObjectUtils.isNotEmpty(reportOutput)) {
          informeEvaluacionEvaluadorReportOutput.setBloques(reportOutput.getBloques());
        } else if (ObjectUtils.isNotEmpty(reportOutput)) {
          informeEvaluacionEvaluadorReportOutput.getBloques().addAll(reportOutput.getBloques());
        }

      }

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return informeEvaluacionEvaluadorReportOutput;
  }

  public byte[] getReportInformeEvaluacion(ReportInformeEvaluacion sgiReport, Long idEvaluacion) {
    getReportFromIdEvaluacion(sgiReport, idEvaluacion);
    return sgiReport.getContent();
  }

}