package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEquipoService;
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
 * SolicitudProyectoEquipoController
 */
@RestController
@RequestMapping("/solicitudproyectoequipo")
@Slf4j
public class SolicitudProyectoEquipoController {

  /** SolicitudProyectoEquipoService service */
  private final SolicitudProyectoEquipoService service;

  /**
   * Instancia un nuevo SolicitudProyectoEquipoController.
   * 
   * @param solicitudProyectoEquipoService {@link SolicitudProyectoEquipoService}.
   */
  public SolicitudProyectoEquipoController(SolicitudProyectoEquipoService solicitudProyectoEquipoService) {
    this.service = solicitudProyectoEquipoService;
  }

  /**
   * Crea nuevo {@link SolicitudProyectoEquipo}
   * 
   * @param solicitudProyectoEquipo {@link SolicitudProyectoEquipo}. que se quiere
   *                                crear.
   * @return Nuevo {@link SolicitudProyectoEquipo} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-C')")
  public ResponseEntity<SolicitudProyectoEquipo> create(
      @Valid @RequestBody SolicitudProyectoEquipo solicitudProyectoEquipo) {
    log.debug("create(SolicitudProyectoEquipo solicitudProyectoEquipo) - start");
    SolicitudProyectoEquipo returnValue = service.create(solicitudProyectoEquipo);
    log.debug("create(SolicitudProyectoEquipo solicitudProyectoEquipo) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link SolicitudProyectoEquipo}.
   * 
   * @param solicitudProyectoEquipo {@link SolicitudProyectoEquipo} a actualizar.
   * @param id                      Identificador {@link SolicitudProyectoEquipo}
   *                                a actualizar.
   * @param authentication          Datos autenticación.
   * @return SolicitudProyectoEquipo {@link SolicitudProyectoEquipo} actualizado
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-E')")
  public SolicitudProyectoEquipo update(@Valid @RequestBody SolicitudProyectoEquipo solicitudProyectoEquipo,
      @PathVariable Long id, Authentication authentication) {
    log.debug("update(SolicitudProyectoEquipo solicitudProyectoEquipo, Long id) - start");
    solicitudProyectoEquipo.setId(id);
    SolicitudProyectoEquipo returnValue = service.update(solicitudProyectoEquipo);
    log.debug("update(SolicitudProyectoEquipo solicitudProyectoEquipo, Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link SolicitudProyectoEquipo} con el id
   * indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoEquipo}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-V')")
  public ResponseEntity<?> exists(@PathVariable Long id) {
    log.debug("SolicitudProyectoEquipo exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("SolicitudProyectoEquipo exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("SolicitudProyectoEquipo exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el {@link SolicitudProyectoEquipo} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoEquipo}.
   * @return SolicitudProyectoEquipo {@link SolicitudProyectoEquipo}
   *         correspondiente al id
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-V')")
  SolicitudProyectoEquipo findById(@PathVariable Long id) {
    log.debug("SolicitudProyectoEquipo findById(Long id) - start");
    SolicitudProyectoEquipo returnValue = service.findById(id);
    log.debug("SolicitudProyectoEquipo findById(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link SolicitudProyectoEquipo} con id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoEquipo}.
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