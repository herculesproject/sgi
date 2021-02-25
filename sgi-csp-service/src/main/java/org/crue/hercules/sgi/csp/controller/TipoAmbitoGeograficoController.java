package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.service.TipoAmbitoGeograficoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * TipoAmbitoGeograficoController
 */
@RestController
@RequestMapping("/tipoambitogeograficos")
@Slf4j
public class TipoAmbitoGeograficoController {

  /** TipoAmbitoGeografico service */
  private final TipoAmbitoGeograficoService service;

  /**
   * Instancia un nuevo TipoAmbitoGeograficoController.
   * 
   * @param service {@link TipoAmbitoGeograficoService}
   */
  public TipoAmbitoGeograficoController(TipoAmbitoGeograficoService service) {
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoAmbitoGeografico} activos.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping()
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-V')")
  ResponseEntity<Page<TipoAmbitoGeografico>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<TipoAmbitoGeografico> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoAmbitoGeografico}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/todos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-V')")
  ResponseEntity<Page<TipoAmbitoGeografico>> findAllTodos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Page<TipoAmbitoGeografico> page = service.findAllTodos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link TipoAmbitoGeografico} con el id indicado.
   * 
   * @param id Identificador de {@link TipoAmbitoGeografico}.
   * @return {@link TipoAmbitoGeografico} correspondiente al id.
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-V')")
  TipoAmbitoGeografico findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    TipoAmbitoGeografico returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea un nuevo {@link TipoAmbitoGeografico}.
   * 
   * @param tipoAmbitoGeografico {@link TipoAmbitoGeografico} que se quiere crear.
   * @return Nuevo {@link TipoAmbitoGeografico} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-C')")
  ResponseEntity<TipoAmbitoGeografico> create(@Valid @RequestBody TipoAmbitoGeografico tipoAmbitoGeografico) {
    log.debug("create(TipoAmbitoGeografico tipoAmbitoGeografico) - start");
    TipoAmbitoGeografico returnValue = service.create(tipoAmbitoGeografico);
    log.debug("create(TipoAmbitoGeografico tipoAmbitoGeografico) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link TipoAmbitoGeografico} con el id indicado.
   * 
   * @param tipoAmbitoGeografico {@link TipoAmbitoGeografico} a actualizar.
   * @param id                   id {@link TipoAmbitoGeografico} a actualizar.
   * @return {@link TipoAmbitoGeografico} actualizado.
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-E')")
  TipoAmbitoGeografico update(
      @Validated({ Update.class, Default.class }) @RequestBody TipoAmbitoGeografico tipoAmbitoGeografico,
      @PathVariable Long id) {
    log.debug("update(TipoAmbitoGeografico tipoAmbitoGeografico, Long id) - start");
    tipoAmbitoGeografico.setId(id);
    TipoAmbitoGeografico returnValue = service.update(tipoAmbitoGeografico);
    log.debug("update(TipoAmbitoGeografico tipoAmbitoGeografico, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link TipoAmbitoGeografico} con id indicado.
   * 
   * @param id Identificador de {@link TipoAmbitoGeografico}.
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