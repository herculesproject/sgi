package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.service.ProyectoProyectoSgeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoProyectoSgeController
 */

@RestController
@RequestMapping("/proyecto-proyectos-sge")
@Slf4j
public class ProyectoProyectoSgeController {

  /** ProyectoProyectoSge service */
  private final ProyectoProyectoSgeService service;

  public ProyectoProyectoSgeController(ProyectoProyectoSgeService proyectoProyectoSgeService) {
    this.service = proyectoProyectoSgeService;
  }

  /**
   * Crea nuevo {@link ProyectoProyectoSge}.
   * 
   * @param proyectoProyectoSge {@link ProyectoProyectoSge} que se quiere crear.
   * @return Nuevo {@link ProyectoProyectoSge} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProyectoProyectoSge> create(@Valid @RequestBody ProyectoProyectoSge proyectoProyectoSge) {
    log.debug("create(ProyectoProyectoSge proyectoProyectoSge) - start");
    ProyectoProyectoSge returnValue = service.create(proyectoProyectoSge);
    log.debug("create(ProyectoProyectoSge proyectoProyectoSge) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Elimina el {@link ProyectoProyectoSge} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoProyectoSge}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }
}
