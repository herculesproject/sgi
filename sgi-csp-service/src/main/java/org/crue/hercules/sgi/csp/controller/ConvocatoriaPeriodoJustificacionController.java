package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPeriodoJustificacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ConvocatoriaPeriodoJustificacionController
 */

@RestController
@RequestMapping("/convocatoriaperiodojustificaciones")
@Slf4j
public class ConvocatoriaPeriodoJustificacionController {

  /** ConvocatoriaPeriodoJustificacion service */
  private final ConvocatoriaPeriodoJustificacionService service;

  public ConvocatoriaPeriodoJustificacionController(
      ConvocatoriaPeriodoJustificacionService convocatoriaPeriodoJustificacionService) {
    log.debug(
        "ConvocatoriaPeriodoJustificacionController(ConvocatoriaPeriodoJustificacionService convocatoriaPeriodoJustificacionService) - start");
    this.service = convocatoriaPeriodoJustificacionService;
    log.debug(
        "ConvocatoriaPeriodoJustificacionController(ConvocatoriaPeriodoJustificacionService convocatoriaPeriodoJustificacionService) - end");
  }

  /**
   * Devuelve el {@link ConvocatoriaPeriodoJustificacion} con el id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaPeriodoJustificacion}.
   * @return {@link ConvocatoriaPeriodoJustificacion} correspondiente al id.
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-V')")
  ConvocatoriaPeriodoJustificacion findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ConvocatoriaPeriodoJustificacion returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea nuevo {@link ConvocatoriaPeriodoJustificacion}.
   * 
   * @param convocatoriaPeriodoJustificacion {@link ConvocatoriaPeriodoJustificacion}.
   *                                         que se quiere crear.
   * @return Nuevo {@link ConvocatoriaPeriodoJustificacion} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CENTGES-C')")
  public ResponseEntity<ConvocatoriaPeriodoJustificacion> create(
      @Valid @RequestBody ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion) {
    log.debug("create(ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion) - start");
    ConvocatoriaPeriodoJustificacion returnValue = service.create(convocatoriaPeriodoJustificacion);
    log.debug("create(ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ConvocatoriaPeriodoJustificacion} con el id indicado.
   * 
   * @param convocatoriaPeriodoJustificacion {@link ConvocatoriaPeriodoJustificacion}
   *                                         a actualizar.
   * @param id                               id
   *                                         {@link ConvocatoriaPeriodoJustificacion}
   *                                         a actualizar.
   * @return {@link ConvocatoriaPeriodoJustificacion} actualizado.
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-E')")
  ConvocatoriaPeriodoJustificacion update(
      @Valid @RequestBody ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion, @PathVariable Long id) {
    log.debug("update(ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion, Long id) - start");
    convocatoriaPeriodoJustificacion.setId(id);
    ConvocatoriaPeriodoJustificacion returnValue = service.update(convocatoriaPeriodoJustificacion);
    log.debug("update(ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link ConvocatoriaPeriodoJustificacion} con id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaPeriodoJustificacion}.
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
