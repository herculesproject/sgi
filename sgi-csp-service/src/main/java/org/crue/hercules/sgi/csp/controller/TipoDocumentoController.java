package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.service.TipoDocumentoService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
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
 * TipoDocumentoController
 */
@RestController
@RequestMapping("/tipodocumentos")
@Slf4j
public class TipoDocumentoController {

  /** TipoDocumento service */
  private final TipoDocumentoService tipoDocumentoService;

  /**
   * Instancia un nuevo TipoDocumentoController.
   * 
   * @param tipoDocumentoService {@link TipoDocumentoService}
   */
  public TipoDocumentoController(TipoDocumentoService tipoDocumentoService) {
    log.debug("TipoDocumentoController(TipoDocumentoService tipoDocumentoService) - start");
    this.tipoDocumentoService = tipoDocumentoService;
    log.debug("TipoDocumentoController(TipoDocumentoService tipoDocumentoService) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoDocumento} activos.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping()
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-V')")
  ResponseEntity<Page<TipoDocumento>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - start");
    Page<TipoDocumento> page = tipoDocumentoService.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoDocumento}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/todos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-V')")
  ResponseEntity<Page<TipoDocumento>> findAllTodos(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - start");
    Page<TipoDocumento> page = tipoDocumentoService.findAllTodos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link TipoDocumento} con el id indicado.
   * 
   * @param id Identificador de {@link TipoDocumento}.
   * @return {@link TipoDocumento} correspondiente al id.
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-V')")
  TipoDocumento findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    TipoDocumento returnValue = tipoDocumentoService.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea un nuevo {@link TipoDocumento}.
   * 
   * @param tipoDocumento {@link TipoDocumento} que se quiere crear.
   * @return Nuevo {@link TipoDocumento} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-C')")
  ResponseEntity<TipoDocumento> create(@Valid @RequestBody TipoDocumento tipoDocumento) {
    log.debug("create(TipoDocumento tipoDocumento) - start");
    TipoDocumento returnValue = tipoDocumentoService.create(tipoDocumento);
    log.debug("create(TipoDocumento tipoDocumento) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link TipoDocumento} con el id indicado.
   * 
   * @param tipoDocumento {@link TipoDocumento} a actualizar.
   * @param id            id {@link TipoDocumento} a actualizar.
   * @return {@link TipoDocumento} actualizado.
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-E')")
  TipoDocumento update(@Validated({ Update.class, Default.class }) @RequestBody TipoDocumento tipoDocumento,
      @PathVariable Long id) {
    log.debug("update(TipoDocumento tipoDocumento, Long id) - start");
    tipoDocumento.setId(id);
    TipoDocumento returnValue = tipoDocumentoService.update(tipoDocumento);
    log.debug("update(TipoDocumento tipoDocumento, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link TipoDocumento} con id indicado.
   * 
   * @param id Identificador de {@link TipoDocumento}.
   */
  @DeleteMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-B')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    tipoDocumentoService.disable(id);
    log.debug("deleteById(Long id) - end");
  }

}