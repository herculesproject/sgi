package org.crue.hercules.sgi.rep.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.rep.dto.OutputReportType;
import org.crue.hercules.sgi.rep.dto.eti.InformeActaReportInput;
import org.crue.hercules.sgi.rep.dto.eti.InformeEvaluacionReportInput;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeActa;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeEvaluacion;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeEvaluacionRetrospectiva;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeEvaluador;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeFavorableMemoria;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeFavorableModificacion;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeFavorableRatificacion;
import org.crue.hercules.sgi.rep.dto.eti.ReportMXX;
import org.crue.hercules.sgi.rep.service.eti.InformeActaReportService;
import org.crue.hercules.sgi.rep.service.eti.InformeEvaluacionReportService;
import org.crue.hercules.sgi.rep.service.eti.InformeEvaluacionRetrospectivaReportService;
import org.crue.hercules.sgi.rep.service.eti.InformeEvaluadorReportService;
import org.crue.hercules.sgi.rep.service.eti.InformeFavorableMemoriaReportService;
import org.crue.hercules.sgi.rep.service.eti.InformeFavorableModificacionReportService;
import org.crue.hercules.sgi.rep.service.eti.InformeFavorableRatificacionReportService;
import org.crue.hercules.sgi.rep.service.eti.MXXReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller de informes de ETI
 */
@RestController
@RequestMapping(EtiReportController.MAPPING)
@Slf4j
public class EtiReportController {
  public static final String MAPPING = "/report/eti";

  private final MXXReportService mxxReportService;
  private final InformeEvaluacionReportService informeEvaluacionReportService;
  private final InformeEvaluadorReportService informeEvaluadorReportService;
  private final InformeFavorableMemoriaReportService informeFavorableMemoriaReportService;
  private final InformeActaReportService informeActaReportService;
  private final InformeEvaluacionRetrospectivaReportService informeEvaluacionRetrospectivaReportService;
  private final InformeFavorableModificacionReportService informeFavorableModificacionReportService;
  private final InformeFavorableRatificacionReportService informeFavorableRatificacionReportService;

  @Autowired
  public EtiReportController(MXXReportService mxxReportService,
      InformeEvaluacionReportService informeEvaluacionReportService,
      InformeEvaluadorReportService informeEvaluadorReportService,
      InformeFavorableMemoriaReportService informeFavorableMemoriaReportService,
      InformeActaReportService informeActaReportService,
      InformeEvaluacionRetrospectivaReportService informeEvaluacionRetrospectivaReportService,
      InformeFavorableModificacionReportService informeFavorableModificacionReportService,
      InformeFavorableRatificacionReportService informeFavorableRatificacionReportService) {
    this.mxxReportService = mxxReportService;
    this.informeEvaluacionReportService = informeEvaluacionReportService;
    this.informeEvaluadorReportService = informeEvaluadorReportService;
    this.informeFavorableMemoriaReportService = informeFavorableMemoriaReportService;
    this.informeActaReportService = informeActaReportService;
    this.informeEvaluacionRetrospectivaReportService = informeEvaluacionRetrospectivaReportService;
    this.informeFavorableModificacionReportService = informeFavorableModificacionReportService;
    this.informeFavorableRatificacionReportService = informeFavorableRatificacionReportService;
  }

  /**
   * Devuelve un informe M10, M20 o M30, Seguimiento anual, final y retrospectiva
   *
   * @param idMemoria    Identificador de la memoria
   * @param idFormulario Identificador del formulario
   * @return Resource
   */
  @GetMapping("/informe-mxx/{idMemoria}/{idFormulario}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-ESCR', 'ETI-MEM-INV-ERTR')")
  public ResponseEntity<Resource> getMXX(@PathVariable Long idMemoria, @PathVariable Long idFormulario) {

    log.debug("getMXX(idMemoria, idFormulario) - start");

    ReportMXX report = new ReportMXX();
    report.setOutputReportType(OutputReportType.PDF);

    mxxReportService.getReportMXX(report, idMemoria, idFormulario);
    ByteArrayResource archivo = new ByteArrayResource(report.getContent());

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, OutputReportType.PDF.getType());

    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

  /**
   * Devuelve un informe de evaluación
   *
   * @param idEvaluacion Identificador de la evaluación
   * @return Resource
   */
  @GetMapping("/informe-evaluacion/{idEvaluacion}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-EVC-EVAL')")
  public ResponseEntity<Resource> getInformeEvaluacion(@PathVariable Long idEvaluacion) {

    log.debug("getInformeEvaluacion(idEvaluacion) - start");

    ReportInformeEvaluacion report = new ReportInformeEvaluacion();
    report.setOutputReportType(OutputReportType.PDF);

    informeEvaluacionReportService.getReportInformEvaludorEvaluacion(report, idEvaluacion);
    ByteArrayResource archivo = new ByteArrayResource(report.getContent());

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, OutputReportType.PDF.getType());

    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

  /**
   * Devuelve un informe de evaluador
   *
   * @param idEvaluacion Identificador de la evaluación
   * @return Resource
   */
  @GetMapping("/informe-ficha-evaluador/{idEvaluacion}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-EVC-EVAL')")
  public ResponseEntity<Resource> getInformeEvaluador(@PathVariable Long idEvaluacion) {

    log.debug("getInformeEvaluador(idEvaluacion) - start");

    ReportInformeEvaluador report = new ReportInformeEvaluador();
    report.setOutputReportType(OutputReportType.PDF);

    informeEvaluadorReportService.getReportInformEvaludorEvaluacion(report, idEvaluacion);
    ByteArrayResource archivo = new ByteArrayResource(report.getContent());

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, OutputReportType.PDF.getType());

    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

  /**
   * Devuelve un informe favorable memoria
   *
   * @param input InformeEvaluacionReportInput con Identificador de la evaluación
   *              y fecha del informe
   * @return Resource
   */
  @PostMapping("/informe-favorable-memoria")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-EVC-EVAL')")
  public ResponseEntity<Resource> getInformeFavorableMemoria(@Valid @RequestBody InformeEvaluacionReportInput input) {

    log.debug("getInformeFavorableMemoria(input) - start");

    ReportInformeFavorableMemoria report = new ReportInformeFavorableMemoria();
    report.setOutputReportType(OutputReportType.PDF);

    informeFavorableMemoriaReportService.getReportInformeFavorableMemoria(report, input);
    ByteArrayResource archivo = new ByteArrayResource(report.getContent());

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, OutputReportType.PDF.getType());

    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

  /**
   * Devuelve un informe acta
   *
   * @param input InformeEvaluacionReportInput con Identificador del acta y fecha
   *              del informe
   * @return Resource
   */
  @PostMapping("/informe-acta")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-DES', 'ETI-ACT-DESR', 'ETI-ACT-INV-DESR')")
  public ResponseEntity<Resource> getInformeActa(@Valid @RequestBody InformeActaReportInput input) {

    log.debug("getInformeActa(input) - start");

    ReportInformeActa report = new ReportInformeActa();
    report.setOutputReportType(OutputReportType.PDF);

    informeActaReportService.getReportInformeActa(report, input);
    ByteArrayResource archivo = new ByteArrayResource(report.getContent());

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, OutputReportType.PDF.getType());

    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

  /**
   * Devuelve un informe evaluación retrospectiva
   *
   * @param input InformeEvaluacionReportInput con Identificador de la evaluación
   *              y fecha del informe
   * @return Resource
   */
  @PostMapping("/informe-evaluacion-retrospectiva")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-MEM-INV-ERTR')")
  public ResponseEntity<Resource> getInformeEvaluacionRetrospectiva(
      @Valid @RequestBody InformeEvaluacionReportInput input) {

    log.debug("getInformeEvaluacionRetrospectiva(input) - start");

    ReportInformeEvaluacionRetrospectiva report = new ReportInformeEvaluacionRetrospectiva();
    report.setOutputReportType(OutputReportType.PDF);

    informeEvaluacionRetrospectivaReportService.getReportInformeEvaluacionRetrospectiva(report, input);
    ByteArrayResource archivo = new ByteArrayResource(report.getContent());

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, OutputReportType.PDF.getType());

    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

  /**
   * Devuelve un informe favorable modificación
   *
   * @param input InformeEvaluacionReportInput con Identificador de la evaluación
   *              y fecha del informe
   * @return Resource
   */
  @PostMapping("/informe-favorable-modificacion")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-EVC-EVAL')")
  public ResponseEntity<Resource> getInformeFavorableModificacion(
      @Valid @RequestBody InformeEvaluacionReportInput input) {

    log.debug("getInformeFavorableModificacion(input) - start");

    ReportInformeFavorableModificacion report = new ReportInformeFavorableModificacion();
    report.setOutputReportType(OutputReportType.PDF);

    informeFavorableModificacionReportService.getReportInformeFavorableModificacion(report, input);
    ByteArrayResource archivo = new ByteArrayResource(report.getContent());

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, OutputReportType.PDF.getType());

    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

  /**
   * Devuelve un informe favorable ratificación
   *
   * @param input InformeEvaluacionReportInput con Identificador de la evaluación
   *              y fecha del informe
   * @return Resource
   */
  @PostMapping("/informe-favorable-ratificacion")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-EVC-EVAL')")
  public ResponseEntity<Resource> getInformeFavorableRatificacion(
      @Valid @RequestBody InformeEvaluacionReportInput input) {

    log.debug("getInformeFavorableRatificacion(input) - start");

    ReportInformeFavorableRatificacion report = new ReportInformeFavorableRatificacion();
    report.setOutputReportType(OutputReportType.PDF);

    informeFavorableRatificacionReportService.getReportInformeFavorableRatificacion(report, input);
    ByteArrayResource archivo = new ByteArrayResource(report.getContent());

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, OutputReportType.PDF.getType());

    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

}