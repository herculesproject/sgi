package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * SolicitudProyectoSocioController
 */
@RestController
@RequestMapping("/solicitudproyectosocio")
@Slf4j
public class SolicitudProyectoSocioController {

  /** SolicitudProyectoSocioService service */
  private final SolicitudProyectoSocioService service;

  /**
   * Instancia un nuevo SolicitudProyectoSocioController.
   * 
   * @param solicitudProyectoSocioService {@link SolicitudProyectoSocioService}.
   */
  public SolicitudProyectoSocioController(SolicitudProyectoSocioService solicitudProyectoSocioService) {
    this.service = solicitudProyectoSocioService;
  }

  /**
   * Crea nuevo {@link SolicitudProyectoSocio}
   * 
   * @param solicitudProyectoSocio {@link SolicitudProyectoSocio}. que se quiere
   *                               crear.
   * @return Nuevo {@link SolicitudProyectoSocio} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-C')")
  public ResponseEntity<SolicitudProyectoSocio> create(
      @Valid @RequestBody SolicitudProyectoSocio solicitudProyectoSocio) {
    log.debug("create(SolicitudProyectoSocio solicitudProyectoSocio) - start");
    SolicitudProyectoSocio returnValue = service.create(solicitudProyectoSocio);
    log.debug("create(SolicitudProyectoSocio solicitudProyectoSocio) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link SolicitudProyectoSocio}.
   * 
   * @param solicitudProyectoSocio {@link SolicitudProyectoSocio} a actualizar.
   * @param id                     Identificador {@link SolicitudProyectoSocio} a
   *                               actualizar.
   * @return SolicitudProyectoSocio {@link SolicitudProyectoSocio} actualizado
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-E')")
  public SolicitudProyectoSocio update(@Valid @RequestBody SolicitudProyectoSocio solicitudProyectoSocio,
      @PathVariable Long id) {
    log.debug("update(SolicitudProyectoSocio solicitudProyectoSocio, Long id) - start");

    solicitudProyectoSocio.setId(id);
    SolicitudProyectoSocio returnValue = service.update(solicitudProyectoSocio);
    log.debug("update(SolicitudProyectoSocio solicitudProyectoSocio, Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link SolicitudProyectoSocio} con el id
   * indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoSocio}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-V')")
  public ResponseEntity<?> exists(@PathVariable Long id) {
    log.debug("SolicitudProyectoSocio exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("SolicitudProyectoSocio exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("SolicitudProyectoSocio exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el {@link SolicitudProyectoSocio} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoSocio}.
   * @return SolicitudProyectoSocio {@link SolicitudProyectoSocio} correspondiente
   *         al id
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-V')")
  SolicitudProyectoSocio findById(@PathVariable Long id) {
    log.debug("SolicitudProyectoSocio findById(Long id) - start");
    SolicitudProyectoSocio returnValue = service.findById(id);
    log.debug("SolicitudProyectoSocio findById(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link SolicitudProyectoSocio} con id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoSocio}.
   */
  @DeleteMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CENTGES-B')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

}