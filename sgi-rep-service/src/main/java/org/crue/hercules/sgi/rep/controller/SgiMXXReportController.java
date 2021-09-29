package org.crue.hercules.sgi.rep.controller;

import org.crue.hercules.sgi.rep.dto.OutputReportType;
import org.crue.hercules.sgi.rep.dto.SgiReportMXX;
import org.crue.hercules.sgi.rep.service.SgiMXXReportService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller de informes M10, M20 y M30 de ETI
 * 
 */
@RestController
@RequestMapping(SgiMXXReportController.MAPPING)
@Slf4j
public class SgiMXXReportController {
  public static final String MAPPING = "/reports";

  private final SgiMXXReportService service;

  /**
   * Instancia un nuevo SgiMXXReportController.
   * 
   * @param sgiReportService {@link SgiMXXReportService}.
   */
  public SgiMXXReportController(SgiMXXReportService sgiReportService) {
    this.service = sgiReportService;
  }

  /**
   * Devuelve un informe M10, M20 o M30
   *
   * @param idMemoria Identificador de la memoria
   * @return Resource
   */
  @GetMapping("/mxx/{idMemoria}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-INV-ER')")
  ResponseEntity<Resource> getMXX(@PathVariable Long idMemoria) {

    log.debug("getMXX(idMemoria) - start");

    SgiReportMXX report = new SgiReportMXX();
    report.setOutputReportType(OutputReportType.PDF);

    service.getReportMXX(report, idMemoria);
    ByteArrayResource archivo = new ByteArrayResource(report.getContent());

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", OutputReportType.PDF.type);

    log.debug("getMXX(idMemoria) - end");

    return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
  }

}