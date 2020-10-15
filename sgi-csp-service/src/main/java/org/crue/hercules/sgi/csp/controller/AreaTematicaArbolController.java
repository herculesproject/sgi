package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.AreaTematicaArbol;
import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.service.AreaTematicaArbolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
 * AreaTematicaArbolController
 */
@RestController
@RequestMapping("/areatematicaarboles")
@Slf4j
public class AreaTematicaArbolController {

  /** AreaTematicaArbol service */
  private final AreaTematicaArbolService service;

  /**
   * Instancia un nuevo AreaTematicaArbolController.
   * 
   * @param service {@link AreaTematicaArbolService}
   */
  public AreaTematicaArbolController(AreaTematicaArbolService service) {
    this.service = service;
  }

  /**
   * Devuelve el {@link AreaTematicaArbol} con el id indicado.
   * 
   * @param id Identificador de {@link AreaTematicaArbol}.
   * @return {@link AreaTematicaArbol} correspondiente al id.
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-V')")
  AreaTematicaArbol findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    AreaTematicaArbol returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea un nuevo {@link AreaTematicaArbol}.
   * 
   * @param areaTematicaArbol {@link AreaTematicaArbol} que se quiere crear.
   * @return Nuevo {@link AreaTematicaArbol} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-C')")
  ResponseEntity<AreaTematicaArbol> create(@Valid @RequestBody AreaTematicaArbol areaTematicaArbol) {
    log.debug("create(AreaTematicaArbol areaTematicaArbol) - start");
    AreaTematicaArbol returnValue = service.create(areaTematicaArbol);
    log.debug("create(AreaTematicaArbol areaTematicaArbol) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link AreaTematicaArbol} con el id indicado.
   * 
   * @param areaTematicaArbol {@link AreaTematicaArbol} a actualizar.
   * @param id                id {@link AreaTematicaArbol} a actualizar.
   * @return {@link AreaTematicaArbol} actualizado.
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-E')")
  AreaTematicaArbol update(@Validated({ Update.class, Default.class }) @RequestBody AreaTematicaArbol areaTematicaArbol,
      @PathVariable Long id) {
    log.debug("update(AreaTematicaArbol areaTematicaArbol, Long id) - start");
    areaTematicaArbol.setId(id);
    AreaTematicaArbol returnValue = service.update(areaTematicaArbol);
    log.debug("update(AreaTematicaArbol areaTematicaArbol, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link AreaTematicaArbol} con id indicado.
   * 
   * @param id Identificador de {@link AreaTematicaArbol}.
   */
  @DeleteMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-B')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.disable(id);
    log.debug("deleteById(Long id) - end");
  }

}