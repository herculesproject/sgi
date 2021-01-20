package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.service.ProyectoSocioService;
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
 * ProyectoSocioController
 */
@RestController
@RequestMapping("/proyectosocios")
@Slf4j
public class ProyectoSocioController {

  /** ProyectoSocioService service */
  private final ProyectoSocioService service;

  /**
   * Instancia un nuevo ProyectoSocioController.
   * 
   * @param proyectoSocioService {@link ProyectoSocioService}.
   */
  public ProyectoSocioController(ProyectoSocioService proyectoSocioService) {
    this.service = proyectoSocioService;
  }

  /**
   * Crea nuevo {@link ProyectoSocio}
   * 
   * @param proyectoSocio {@link ProyectoSocio} que se quiere crear.
   * @return Nuevo {@link ProyectoSocio} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-C')")
  public ResponseEntity<ProyectoSocio> create(@Valid @RequestBody ProyectoSocio proyectoSocio) {
    log.debug("create(ProyectoSocio proyectoSocio) - start");
    ProyectoSocio returnValue = service.create(proyectoSocio);
    log.debug("create(ProyectoSocio proyectoSocio) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link ProyectoSocio}.
   * 
   * @param proyectoSocio {@link ProyectoSocio} a actualizar.
   * @param id            Identificador {@link ProyectoSocio} a actualizar.
   * @return {@link ProyectoSocio} actualizado
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ProyectoSocio update(@Valid @RequestBody ProyectoSocio proyectoSocio, @PathVariable Long id) {
    log.debug("update(ProyectoSocio proyectoSocio, Long id) - start");
    proyectoSocio.setId(id);
    ProyectoSocio returnValue = service.update(proyectoSocio);
    log.debug("update(ProyectoSocio proyectoSocio, Long id) - end");
    return returnValue;
  }

  /**
   * Elimina el {@link ProyectoSocio} con id indicado.
   * 
   * @param id Identificador de {@link ProyectoSocio}.
   */
  @DeleteMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Comprueba la existencia del {@link ProyectoSocio} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoSocio}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
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
   * Devuelve el {@link ProyectoSocio} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoSocio}.
   * @return {@link ProyectoSocio} correspondiente al id
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-V')")
  ProyectoSocio findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoSocio returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

}
