package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SocioPeriodoJustificacionDocumento;
import org.crue.hercules.sgi.csp.service.SocioPeriodoJustificacionDocumentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * SocioPeriodoJustificacionDocumentoController
 */

@RestController
@RequestMapping("/socioperiodojustificaciondocumentos")
@Slf4j
public class SocioPeriodoJustificacionDocumentoController {

  /** SocioPeriodoJustificacionDocumento service */
  private final SocioPeriodoJustificacionDocumentoService service;

  public SocioPeriodoJustificacionDocumentoController(
      SocioPeriodoJustificacionDocumentoService socioPeriodoJustificacionDocumentoService) {
    log.debug(
        "SocioPeriodoJustificacionDocumentoController(SocioPeriodoJustificacionDocumentoService socioPeriodoJustificacionDocumentoService) - start");
    this.service = socioPeriodoJustificacionDocumentoService;
    log.debug(
        "SocioPeriodoJustificacionDocumentoController(SocioPeriodoJustificacionDocumentoService socioPeriodoJustificacionDocumentoService) - end");
  }

  /**
   * Devuelve el {@link SocioPeriodoJustificacionDocumento} con el id indicado.
   * 
   * @param id Identificador de {@link SocioPeriodoJustificacionDocumento}.
   * @return {@link SocioPeriodoJustificacionDocumento} correspondiente al id.
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-V')")
  SocioPeriodoJustificacionDocumento findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    SocioPeriodoJustificacionDocumento returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link SocioPeriodoJustificacionDocumento} de la
   * {@link ProyectoSocioPeriodoJustificacion} con el listado
   * socioPeriodoJustificacionDocumentoes añadiendo, editando o eliminando los
   * elementos segun proceda.
   * 
   * @param proyectoSocioPeriodoJustificacionId  Id de la
   *                                             {@link ProyectoSocioPeriodoJustificacion}.
   * @param socioPeriodoJustificacionDocumentoes lista con los nuevos
   *                                             {@link SocioPeriodoJustificacionDocumento}
   *                                             a guardar.
   * @return Lista actualizada con los {@link SocioPeriodoJustificacionDocumento}.
   */
  @PatchMapping("/{proyectoSocioPeriodoJustificacionId}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CENTGES-C')")
  public ResponseEntity<List<SocioPeriodoJustificacionDocumento>> update(
      @PathVariable Long proyectoSocioPeriodoJustificacionId,
      @Valid @RequestBody List<SocioPeriodoJustificacionDocumento> socioPeriodoJustificacionDocumentoes) {
    log.debug(
        "update(Long proyectoSocioPeriodoJustificacionId, List<SocioPeriodoJustificacionDocumento> socioPeriodoJustificacionDocumentoes) - start");
    List<SocioPeriodoJustificacionDocumento> returnValue = service.update(proyectoSocioPeriodoJustificacionId,
        socioPeriodoJustificacionDocumentoes);
    log.debug(
        "update(Long proyectoSocioPeriodoJustificacionId, List<SocioPeriodoJustificacionDocumento> socioPeriodoJustificacionDocumentoes) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

}
