package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.service.TipoEnlaceService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * TipoEnlaceController
 */

@RestController
@RequestMapping("/tipoenlaces")
@Slf4j
public class TipoEnlaceController {

  /** TipoEnlace service */
  private final TipoEnlaceService service;

  public TipoEnlaceController(TipoEnlaceService tipoEnlaceService) {
    log.debug("TipoEnlaceController(TipoEnlaceService tipoEnlaceService) - start");
    this.service = tipoEnlaceService;
    log.debug("TipoEnlaceController(TipoEnlaceService tipoEnlaceService) - end");
  }

  /**
   * Crea nuevo {@link TipoEnlace}.
   * 
   * @param tipoEnlace {@link TipoEnlace}. que se quiere crear.
   * @return Nuevo {@link TipoEnlace} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TENL-C')")
  public ResponseEntity<TipoEnlace> create(@Valid @RequestBody TipoEnlace tipoEnlace) {
    log.debug("create(TipoEnlace tipoEnlace) - start");
    TipoEnlace returnValue = service.create(tipoEnlace);
    log.debug("create(TipoEnlace tipoEnlace) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link TipoEnlace}.
   * 
   * @param tipoEnlace {@link TipoEnlace} a actualizar.
   * @param id         Identificador {@link TipoEnlace} a actualizar.
   * @return TipoEnlace {@link TipoEnlace} actualizado
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TENL-E')")
  public TipoEnlace update(@Valid @RequestBody TipoEnlace tipoEnlace, @PathVariable Long id) {
    log.debug("update(TipoEnlace tipoEnlace, Long id) - start");
    tipoEnlace.setId(id);
    TipoEnlace returnValue = service.update(tipoEnlace);
    log.debug("update(TipoEnlace tipoEnlace, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link TipoEnlace} con id indicado.
   * 
   * @param id Identificador de {@link TipoEnlace}.
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
   * Devuelve una lista paginada y filtrada {@link TipoEnlace} activos.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link TipoEnlace} paginadas y filtradas.
   */
  @GetMapping()
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TENL-V')")
  ResponseEntity<Page<TipoEnlace>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Page<TipoEnlace> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoEnlace}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link TipoEnlace} paginadas y filtradas.
   */
  @GetMapping("/todos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-V')")
  ResponseEntity<Page<TipoEnlace>> findAllTodos(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - start");
    Page<TipoEnlace> page = service.findAllTodos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link TipoEnlace} con el id indicado.
   * 
   * @param id Identificador de {@link TipoEnlace}.
   * @return TipoEnlace {@link TipoEnlace} correspondiente al id
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TENL-V')")
  TipoEnlace findById(@PathVariable Long id) {
    log.debug("TipoEnlace findById(Long id) - start");
    TipoEnlace returnValue = service.findById(id);
    log.debug("TipoEnlace findById(Long id) - end");
    return returnValue;
  }
}
