package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.service.ProgramaService;
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
 * ProgramaController
 */
@RestController
@RequestMapping("/programas")
@Slf4j
public class ProgramaController {

  /** Programa service */
  private final ProgramaService service;

  /**
   * Instancia un nuevo ProgramaController.
   * 
   * @param service {@link ProgramaService}
   */
  public ProgramaController(ProgramaService service) {
    this.service = service;
  }

  /**
   * Devuelve el {@link Programa} con el id indicado.
   * 
   * @param id Identificador de {@link Programa}.
   * @return {@link Programa} correspondiente al id.
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-V')")
  Programa findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    Programa returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea un nuevo {@link Programa}.
   * 
   * @param programa {@link Programa} que se quiere crear.
   * @return Nuevo {@link Programa} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-C')")
  ResponseEntity<Programa> create(@Valid @RequestBody Programa programa) {
    log.debug("create(Programa programa) - start");
    Programa returnValue = service.create(programa);
    log.debug("create(Programa programa) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link Programa} con el id indicado.
   * 
   * @param programa {@link Programa} a actualizar.
   * @param id       id {@link Programa} a actualizar.
   * @return {@link Programa} actualizado.
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-E')")
  Programa update(@Validated({ Update.class, Default.class }) @RequestBody Programa programa, @PathVariable Long id) {
    log.debug("update(Programa programa, Long id) - start");
    programa.setId(id);
    Programa returnValue = service.update(programa);
    log.debug("update(Programa programa, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link Programa} con id indicado.
   * 
   * @param id Identificador de {@link Programa}.
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