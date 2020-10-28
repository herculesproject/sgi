package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.service.FuenteFinanciacionService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * FuenteFinanciacionController
 */
@RestController
@RequestMapping("/fuentefinanciaciones")
@Slf4j
public class FuenteFinanciacionController {

  /** FuenteFinanciacion service */
  private final FuenteFinanciacionService service;

  /**
   * Instancia un nuevo FuenteFinanciacionController.
   * 
   * @param service {@link FuenteFinanciacionService}
   */
  public FuenteFinanciacionController(FuenteFinanciacionService service) {
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link FuenteFinanciacion} activos.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping()
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-V')")
  ResponseEntity<Page<FuenteFinanciacion>> findAll(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - start");
    Page<FuenteFinanciacion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link FuenteFinanciacion}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/todos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-V')")
  ResponseEntity<Page<FuenteFinanciacion>> findAllTodos(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - start");
    Page<FuenteFinanciacion> page = service.findAllTodos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link FuenteFinanciacion} con el id indicado.
   * 
   * @param id Identificador de {@link FuenteFinanciacion}.
   * @return {@link FuenteFinanciacion} correspondiente al id.
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-V')")
  FuenteFinanciacion findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    FuenteFinanciacion returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea un nuevo {@link FuenteFinanciacion}.
   * 
   * @param fuenteFinanciacion {@link FuenteFinanciacion} que se quiere crear.
   * @return Nuevo {@link FuenteFinanciacion} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-C')")
  ResponseEntity<FuenteFinanciacion> create(@Valid @RequestBody FuenteFinanciacion fuenteFinanciacion) {
    log.debug("create(FuenteFinanciacion fuenteFinanciacion) - start");
    FuenteFinanciacion returnValue = service.create(fuenteFinanciacion);
    log.debug("create(FuenteFinanciacion fuenteFinanciacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link FuenteFinanciacion} con el id indicado.
   * 
   * @param fuenteFinanciacion {@link FuenteFinanciacion} a actualizar.
   * @param id                 id {@link FuenteFinanciacion} a actualizar.
   * @return {@link FuenteFinanciacion} actualizado.
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-E')")
  FuenteFinanciacion update(
      @Validated({ Update.class, Default.class }) @RequestBody FuenteFinanciacion fuenteFinanciacion,
      @PathVariable Long id) {
    log.debug("update(FuenteFinanciacion fuenteFinanciacion, Long id) - start");
    fuenteFinanciacion.setId(id);
    FuenteFinanciacion returnValue = service.update(fuenteFinanciacion);
    log.debug("update(FuenteFinanciacion fuenteFinanciacion, Long id) - end");
    return returnValue;
  }

  /**
   * Reactiva el {@link FuenteFinanciacion} con id indicado.
   * 
   * @param id Identificador de {@link FuenteFinanciacion}.
   * @return {@link FuenteFinanciacion} actualizado.
   */
  @PatchMapping("/{id}/reactivar")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CONGAS-R')")
  FuenteFinanciacion reactivar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    FuenteFinanciacion returnValue = service.enable(id);
    log.debug("reactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link FuenteFinanciacion} con id indicado.
   * 
   * @param id Identificador de {@link FuenteFinanciacion}.
   */
  @PatchMapping("/{id}/desactivar")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-B')")
  FuenteFinanciacion desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    FuenteFinanciacion returnValue = service.disable(id);
    log.debug("desactivar(Long id) - end");
    return returnValue;
  }

}