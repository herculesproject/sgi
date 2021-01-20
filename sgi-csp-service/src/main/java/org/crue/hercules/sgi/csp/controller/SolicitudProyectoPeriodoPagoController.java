package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoPago;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPeriodoPagoService;
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
 * SolicitudProyectoPeriodoPagoController
 */
@RestController
@RequestMapping("/solicitudproyectoperiodopago")
@Slf4j
public class SolicitudProyectoPeriodoPagoController {

  /** SolicitudProyectoPeriodoPagoService service */
  private final SolicitudProyectoPeriodoPagoService service;

  /**
   * Instancia un nuevo SolicitudProyectoPeriodoPagoController.
   * 
   * @param solicitudProyectoPeriodoPagoService {@link SolicitudProyectoPeriodoPagoService}.
   */
  public SolicitudProyectoPeriodoPagoController(
      SolicitudProyectoPeriodoPagoService solicitudProyectoPeriodoPagoService) {
    this.service = solicitudProyectoPeriodoPagoService;
  }

  /**
   * Actualiza el listado de {@link SolicitudProyectoPeriodoPago} de la
   * {@link SolicitudProyectoSocio} con el listado solicitudPeriodoPagos
   * añadiendo, editando o eliminando los elementos segun proceda.
   * 
   * @param solicitudPeriodoPagos    lista {@link SolicitudProyectoPeriodoPago} a
   *                                 actualizar.
   * @param solicitudProyectoSocioId Identificador {@link SolicitudProyectoSocio}
   *                                 a actualizar.
   * @return Lista actualizada de {@link SolicitudProyectoPeriodoPago}.
   */
  @PatchMapping("/{solicitudProyectoSocioId}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-E')")
  public ResponseEntity<List<SolicitudProyectoPeriodoPago>> update(@PathVariable Long solicitudProyectoSocioId,
      @Valid @RequestBody List<SolicitudProyectoPeriodoPago> solicitudPeriodoPagos) {
    log.debug(
        "updateConvocatoriaPeriodoJustificacionesConvocatoria(Long solicitudProyectoSocioId, List<SolicitudProyectoPeriodoPago> solicitudPeriodoPagos) - start");
    List<SolicitudProyectoPeriodoPago> returnValue = service.update(solicitudProyectoSocioId, solicitudPeriodoPagos);
    log.debug(
        "updateConvocatoriaPeriodoJustificacionesConvocatoria(Long solicitudProyectoSocioId, List<SolicitudProyectoPeriodoPago> solicitudPeriodoPagos) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Comprueba la existencia del {@link SolicitudProyectoPeriodoPago} con el id
   * indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoPeriodoPago}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-V')")
  public ResponseEntity<?> exists(@PathVariable Long id) {
    log.debug("SolicitudProyectoPeriodoPago exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("SolicitudProyectoPeriodoPago exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("SolicitudProyectoPeriodoPago exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el {@link SolicitudProyectoPeriodoPago} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoPeriodoPago}.
   * @return SolicitudProyectoPeriodoPago {@link SolicitudProyectoPeriodoPago}
   *         correspondiente al id
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-V')")
  SolicitudProyectoPeriodoPago findById(@PathVariable Long id) {
    log.debug("SolicitudProyectoPeriodoPago findById(Long id) - start");
    SolicitudProyectoPeriodoPago returnValue = service.findById(id);
    log.debug("SolicitudProyectoPeriodoPago findById(Long id) - end");
    return returnValue;
  }

}