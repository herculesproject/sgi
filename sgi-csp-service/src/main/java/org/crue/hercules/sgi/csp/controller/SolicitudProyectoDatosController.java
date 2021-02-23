package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoDatosService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEntidadFinanciadoraAjenaService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPresupuestoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * SolicitudProyectoDatosController
 */
@RestController
@RequestMapping("/solicitudproyectodatos")
@Slf4j
public class SolicitudProyectoDatosController {

  /** SolicitudProyectoDatosService service */
  private final SolicitudProyectoDatosService service;

  /** SolicitudProyectoPresupuestoService service */
  private final SolicitudProyectoPresupuestoService solicitudProyectoPresupuestoService;

  /** SolicitudProyectoPresupuestoService service */
  private final SolicitudProyectoEntidadFinanciadoraAjenaService solicitudProyectoEntidadFinanciadoraAjenaService;

  /** SolicitudProyectoPresupuestoService service */
  private final SolicitudProyectoSocioService solicitudProyectoSocioService;

  /**
   * Instancia un nuevo SolicitudProyectoDatosController.
   * 
   * @param solicitudProyectoDatosService                    {@link SolicitudProyectoDatosService}.
   * @param solicitudProyectoPresupuestoService              {@link SolicitudProyectoPresupuestoService}.
   * @param solicitudProyectoSocioService                    {@link SolicitudProyectoSocioService}.
   * @param solicitudProyectoEntidadFinanciadoraAjenaService {@link SolicitudProyectoEntidadFinanciadoraAjenaService}.
   */
  public SolicitudProyectoDatosController(SolicitudProyectoDatosService solicitudProyectoDatosService,
      SolicitudProyectoPresupuestoService solicitudProyectoPresupuestoService,
      SolicitudProyectoSocioService solicitudProyectoSocioService,
      SolicitudProyectoEntidadFinanciadoraAjenaService solicitudProyectoEntidadFinanciadoraAjenaService) {
    this.service = solicitudProyectoDatosService;
    this.solicitudProyectoPresupuestoService = solicitudProyectoPresupuestoService;
    this.solicitudProyectoSocioService = solicitudProyectoSocioService;
    this.solicitudProyectoEntidadFinanciadoraAjenaService = solicitudProyectoEntidadFinanciadoraAjenaService;
  }

  /**
   * Crea nuevo {@link SolicitudProyectoDatos}
   * 
   * @param solicitudProyectoDatos {@link SolicitudProyectoDatos}. que se quiere
   *                               crear.
   * @return Nuevo {@link SolicitudProyectoDatos} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-C')")
  public ResponseEntity<SolicitudProyectoDatos> create(
      @Valid @RequestBody SolicitudProyectoDatos solicitudProyectoDatos) {
    log.debug("create(SolicitudProyectoDatos solicitudProyectoDatos) - start");
    SolicitudProyectoDatos returnValue = service.create(solicitudProyectoDatos);
    log.debug("create(SolicitudProyectoDatos solicitudProyectoDatos) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link SolicitudProyectoDatos}.
   * 
   * @param solicitudProyectoDatos {@link SolicitudProyectoDatos} a actualizar.
   * @param id                     Identificador {@link SolicitudProyectoDatos} a
   *                               actualizar.
   * @param authentication         Datos autenticación.
   * @return SolicitudProyectoDatos {@link SolicitudProyectoDatos} actualizado
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-E')")
  public SolicitudProyectoDatos update(@Valid @RequestBody SolicitudProyectoDatos solicitudProyectoDatos,
      @PathVariable Long id, Authentication authentication) {
    log.debug("update(SolicitudProyectoDatos solicitudProyectoDatos, Long id) - start");
    solicitudProyectoDatos.setId(id);
    SolicitudProyectoDatos returnValue = service.update(solicitudProyectoDatos);
    log.debug("update(SolicitudProyectoDatos solicitudProyectoDatos, Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link SolicitudProyectoDatos} con el id
   * indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoDatos}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-V')")
  public ResponseEntity<?> exists(@PathVariable Long id) {
    log.debug("SolicitudProyectoDatos exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("SolicitudProyectoDatos exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("SolicitudProyectoDatos exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el {@link SolicitudProyectoDatos} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoDatos}.
   * @return SolicitudProyectoDatos {@link SolicitudProyectoDatos} correspondiente
   *         al id
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-V')")
  SolicitudProyectoDatos findById(@PathVariable Long id) {
    log.debug("SolicitudProyectoDatos findById(Long id) - start");
    SolicitudProyectoDatos returnValue = service.findById(id);
    log.debug("SolicitudProyectoDatos findById(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link SolicitudProyectoDatos} con id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoDatos}.
   */
  @DeleteMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CENTGES-B')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Comprueba si existen datos vinculados a la {@link SolicitudProyectoDatos} de
   * {@link SolicitudProyectoEntidadFinanciadoraAjena}
   *
   * @param id Id del {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   * @return
   */
  @RequestMapping(path = "/{id}/solicitudentidadfinanciadora", method = RequestMethod.HEAD)
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CONV-V')")
  ResponseEntity<SolicitudProyectoDatos> hasSolicitudEntidadFinanciadora(@PathVariable Long id) {
    log.debug("hasSolicitudEntidadFinanciadora(Long id) - start");
    Boolean returnValue = solicitudProyectoEntidadFinanciadoraAjenaService.hasSolicitudEntidadFinanciadora(id);
    log.debug("hasSolicitudEntidadFinanciadora(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba si existen datos vinculados a la {@link SolicitudProyectoDatos} de
   * {@link SolicitudProyectoPresupuesto}
   *
   * @param id Id del {@link SolicitudProyectoPresupuesto}.
   * @return
   */
  @RequestMapping(path = "/{id}/solicitudpresupuesto", method = RequestMethod.HEAD)
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CONV-V')")
  ResponseEntity<SolicitudProyectoDatos> hasSolicitudPresupuesto(@PathVariable Long id) {
    log.debug("hasSolicitudPresupuesto(Long id) - start");
    Boolean returnValue = solicitudProyectoPresupuestoService.hasSolicitudPresupuesto(id);
    log.debug("hasSolicitudPresupuesto(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba si existen datos vinculados a la {@link SolicitudProyectoDatos} de
   * {@link SolicitudProyectoSocio}
   *
   * @param id Id del {@link SolicitudProyectoSocio}.
   * @return
   */
  @RequestMapping(path = "/{id}/solicitudsocio", method = RequestMethod.HEAD)
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CONV-V')")
  ResponseEntity<SolicitudProyectoDatos> hasSolicitudSocio(@PathVariable Long id) {
    log.debug("hasSolicitudSocio(Long id) - start");
    Boolean returnValue = solicitudProyectoSocioService.hasSolicitudSocio(id);
    log.debug("hasSolicitudSocio(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}