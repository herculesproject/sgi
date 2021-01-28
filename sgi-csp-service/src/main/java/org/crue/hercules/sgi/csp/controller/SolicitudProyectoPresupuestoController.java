package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPresupuestoService;
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
 * SolicitudProyectoPresupuestoController
 */
@RestController
@RequestMapping("/solicitudproyectopresupuestos")
@Slf4j
public class SolicitudProyectoPresupuestoController {

  /** SolicitudProyectoPresupuestoService service */
  private final SolicitudProyectoPresupuestoService service;

  /**
   * Instancia un nuevo SolicitudProyectoPresupuestoController.
   * 
   * @param solicitudProyectoPresupuestoService {@link SolicitudProyectoPresupuestoService}.
   */
  public SolicitudProyectoPresupuestoController(
      SolicitudProyectoPresupuestoService solicitudProyectoPresupuestoService) {
    this.service = solicitudProyectoPresupuestoService;
  }

  /**
   * Crea nuevo {@link SolicitudProyectoPresupuesto}
   * 
   * @param solicitudProyectoPresupuesto {@link SolicitudProyectoPresupuesto} que
   *                                     se quiere crear.
   * @return Nuevo {@link SolicitudProyectoPresupuesto} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-C')")
  public ResponseEntity<SolicitudProyectoPresupuesto> create(
      @Valid @RequestBody SolicitudProyectoPresupuesto solicitudProyectoPresupuesto) {
    log.debug("create(SolicitudProyectoPresupuesto solicitudProyectoPresupuesto) - start");
    SolicitudProyectoPresupuesto returnValue = service.create(solicitudProyectoPresupuesto);
    log.debug("create(SolicitudProyectoPresupuesto solicitudProyectoPresupuesto) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link SolicitudProyectoPresupuesto}.
   * 
   * @param solicitudProyectoPresupuesto {@link SolicitudProyectoPresupuesto} a
   *                                     actualizar.
   * @param id                           Identificador
   *                                     {@link SolicitudProyectoPresupuesto} a
   *                                     actualizar.
   * @param authentication               Datos autenticación.
   * @return SolicitudProyectoPresupuesto {@link SolicitudProyectoPresupuesto}
   *         actualizado
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public SolicitudProyectoPresupuesto update(
      @Valid @RequestBody SolicitudProyectoPresupuesto solicitudProyectoPresupuesto, @PathVariable Long id,
      Authentication authentication) {
    log.debug("update(SolicitudProyectoPresupuesto solicitudProyectoPresupuesto, Long id) - start");

    solicitudProyectoPresupuesto.setId(id);
    SolicitudProyectoPresupuesto returnValue = service.update(solicitudProyectoPresupuesto);
    log.debug("update(SolicitudProyectoPresupuesto solicitudProyectoPresupuesto, Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link SolicitudProyectoPresupuesto} con el id
   * indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoPresupuesto}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-V')")
  public ResponseEntity<?> exists(@PathVariable Long id) {
    log.debug("exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el {@link SolicitudProyectoPresupuesto} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoPresupuesto}.
   * @return SolicitudProyectoPresupuesto {@link SolicitudProyectoPresupuesto}
   *         correspondiente al id
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-V')")
  SolicitudProyectoPresupuesto findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    SolicitudProyectoPresupuesto returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link SolicitudProyectoPresupuesto} con id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoPresupuesto}.
   */
  @DeleteMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-B')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

}