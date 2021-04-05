package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioPeriodoPagoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * SolicitudProyectoSocioPeriodoPagoController
 */
@RestController
@RequestMapping("/solicitudproyectosocioperiodopago")
@Slf4j
public class SolicitudProyectoSocioPeriodoPagoController {

  /** SolicitudProyectoSocioPeriodoPagoService service */
  private final SolicitudProyectoSocioPeriodoPagoService service;

  /**
   * Instancia un nuevo SolicitudProyectoSocioPeriodoPagoController.
   * 
   * @param solicitudProyectoSocioPeriodoPagoService {@link SolicitudProyectoSocioPeriodoPagoService}.
   */
  public SolicitudProyectoSocioPeriodoPagoController(
      SolicitudProyectoSocioPeriodoPagoService solicitudProyectoSocioPeriodoPagoService) {
    this.service = solicitudProyectoSocioPeriodoPagoService;
  }

  /**
   * Actualiza el listado de {@link SolicitudProyectoSocioPeriodoPago} de la
   * {@link SolicitudProyectoSocio} con el listado solicitudPeriodoPagos
   * añadiendo, editando o eliminando los elementos segun proceda.
   * 
   * @param solicitudPeriodoPagos    lista
   *                                 {@link SolicitudProyectoSocioPeriodoPago} a
   *                                 actualizar.
   * @param solicitudProyectoSocioId Identificador {@link SolicitudProyectoSocio}
   *                                 a actualizar.
   * @return Lista actualizada de {@link SolicitudProyectoSocioPeriodoPago}.
   */
  @PatchMapping("/{solicitudProyectoSocioId}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-E')")
  public ResponseEntity<List<SolicitudProyectoSocioPeriodoPago>> update(@PathVariable Long solicitudProyectoSocioId,
      @Valid @RequestBody List<SolicitudProyectoSocioPeriodoPago> solicitudPeriodoPagos) {
    log.debug(
        "updateConvocatoriaPeriodoJustificacionesConvocatoria(Long solicitudProyectoSocioId, List<SolicitudProyectoSocioPeriodoPago> solicitudPeriodoPagos) - start");
    List<SolicitudProyectoSocioPeriodoPago> returnValue = service.update(solicitudProyectoSocioId,
        solicitudPeriodoPagos);
    log.debug(
        "updateConvocatoriaPeriodoJustificacionesConvocatoria(Long solicitudProyectoSocioId, List<SolicitudProyectoSocioPeriodoPago> solicitudPeriodoPagos) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Comprueba la existencia del {@link SolicitudProyectoSocioPeriodoPago} con el
   * id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoSocioPeriodoPago}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-V')")
  public ResponseEntity<?> exists(@PathVariable Long id) {
    log.debug("SolicitudProyectoSocioPeriodoPago exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("SolicitudProyectoSocioPeriodoPago exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("SolicitudProyectoSocioPeriodoPago exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el {@link SolicitudProyectoSocioPeriodoPago} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoSocioPeriodoPago}.
   * @return SolicitudProyectoSocioPeriodoPago
   *         {@link SolicitudProyectoSocioPeriodoPago} correspondiente al id
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-V')")
  SolicitudProyectoSocioPeriodoPago findById(@PathVariable Long id) {
    log.debug("SolicitudProyectoSocioPeriodoPago findById(Long id) - start");
    SolicitudProyectoSocioPeriodoPago returnValue = service.findById(id);
    log.debug("SolicitudProyectoSocioPeriodoPago findById(Long id) - end");
    return returnValue;
  }

}