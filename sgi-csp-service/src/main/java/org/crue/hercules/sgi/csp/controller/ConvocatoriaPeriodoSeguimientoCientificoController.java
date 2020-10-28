package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPeriodoSeguimientoCientificoService;
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
 * ConvocatoriaPeriodoSeguimientoCientificoController
 */

@RestController
@RequestMapping("/convocatoriaperiodoseguimientocientificos")
@Slf4j
public class ConvocatoriaPeriodoSeguimientoCientificoController {

  /** ConvocatoriaPeriodoSeguimientoCientifico service */
  private final ConvocatoriaPeriodoSeguimientoCientificoService service;

  public ConvocatoriaPeriodoSeguimientoCientificoController(
      ConvocatoriaPeriodoSeguimientoCientificoService convocatoriaPeriodoSeguimientoCientificoService) {
    log.debug(
        "ConvocatoriaPeriodoSeguimientoCientificoController(ConvocatoriaPeriodoSeguimientoCientificoService convocatoriaPeriodoSeguimientoCientificoService) - start");
    this.service = convocatoriaPeriodoSeguimientoCientificoService;
    log.debug(
        "ConvocatoriaPeriodoSeguimientoCientificoController(ConvocatoriaPeriodoSeguimientoCientificoService convocatoriaPeriodoSeguimientoCientificoService) - end");
  }

  /**
   * Crea nuevo {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   * 
   * @param convocatoriaPeriodoSeguimientoCientifico {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   *                                                 que se quiere crear.
   * @return Nuevo {@link ConvocatoriaPeriodoSeguimientoCientifico} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CPSCI-C')")
  public ResponseEntity<ConvocatoriaPeriodoSeguimientoCientifico> create(
      @Valid @RequestBody ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico) {
    log.debug("create(ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico) - start");
    ConvocatoriaPeriodoSeguimientoCientifico returnValue = service.create(convocatoriaPeriodoSeguimientoCientifico);
    log.debug("create(ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   * 
   * @param convocatoriaPeriodoSeguimientoCientifico {@link ConvocatoriaPeriodoSeguimientoCientifico}
   *                                                 a actualizar.
   * @param id                                       Identificador
   *                                                 {@link ConvocatoriaPeriodoSeguimientoCientifico}
   *                                                 a actualizar.
   * @return ConvocatoriaPeriodoSeguimientoCientifico
   *         {@link ConvocatoriaPeriodoSeguimientoCientifico} actualizado
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CPSCI-E')")
  public ConvocatoriaPeriodoSeguimientoCientifico update(
      @Valid @RequestBody ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico,
      @PathVariable Long id) {
    log.debug(
        "update(ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico, Long id) - start");
    convocatoriaPeriodoSeguimientoCientifico.setId(id);
    ConvocatoriaPeriodoSeguimientoCientifico returnValue = service.update(convocatoriaPeriodoSeguimientoCientifico);
    log.debug(
        "update(ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico, Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link ConvocatoriaPeriodoSeguimientoCientifico} con id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   */
  @DeleteMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CPSCI-B')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve el {@link ConvocatoriaPeriodoSeguimientoCientifico} con el id
   * indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   * @return ConvocatoriaPeriodoSeguimientoCientifico
   *         {@link ConvocatoriaPeriodoSeguimientoCientifico} correspondiente al
   *         id
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CPSCI-V')")
  ConvocatoriaPeriodoSeguimientoCientifico findById(@PathVariable Long id) {
    log.debug("ConvocatoriaPeriodoSeguimientoCientifico findById(Long id) - start");
    ConvocatoriaPeriodoSeguimientoCientifico returnValue = service.findById(id);
    log.debug("ConvocatoriaPeriodoSeguimientoCientifico findById(Long id) - end");
    return returnValue;
  }
}
