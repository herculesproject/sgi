package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.service.ModeloUnidadService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ModeloUnidadController
 */
@RestController
@RequestMapping("/modelounidades")
@Slf4j
public class ModeloUnidadController {

  /** ModeloUnidad service */
  private final ModeloUnidadService service;

  public ModeloUnidadController(ModeloUnidadService service) {
    log.debug("ModeloUnidadController(ModeloUnidadService service) - start");
    this.service = service;
    log.debug("ModeloUnidadController(ModeloUnidadService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ModeloUnidad} activos.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping()
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-V')")
  ResponseEntity<Page<ModeloUnidad>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<ModeloUnidad> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ModeloUnidad}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/todos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-V')")
  ResponseEntity<Page<ModeloUnidad>> findAllTodos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Page<ModeloUnidad> page = service.findAllTodos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link ModeloUnidad}.
   * 
   * @param modeloUnidad {@link ModeloUnidad} que se quiere crear.
   * @return Nuevo {@link ModeloUnidad} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TENL-C')")
  public ResponseEntity<ModeloUnidad> create(@Valid @RequestBody ModeloUnidad modeloUnidad) {
    log.debug("create( ModeloUnidad modeloUnidad) - start");
    ModeloUnidad returnValue = service.create(modeloUnidad);
    log.debug("create( ModeloUnidad modeloUnidad) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Desactiva {@link ModeloUnidad} con id indicado.
   * 
   * @param id Identificador de {@link ModeloUnidad}.
   */
  @DeleteMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TENL-B')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.disable(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve el {@link ModeloTipoEnlace} con el id indicado.
   * 
   * @param id Identificador de {@link ModeloTipoEnlace}.
   * @return ModeloTipoEnlace {@link ModeloTipoEnlace} correspondiente al id
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TENL-V')")
  ModeloUnidad findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ModeloUnidad returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }
}
