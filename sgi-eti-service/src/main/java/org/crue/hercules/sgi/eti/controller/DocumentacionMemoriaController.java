package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.service.DocumentacionMemoriaService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

/**
 * DocumentacionMemoriaController
 */
@RestController
@RequestMapping("/documentacionmemorias")
@Slf4j
public class DocumentacionMemoriaController {

  /** DocumentacionMemoria service */
  private final DocumentacionMemoriaService service;

  /**
   * Instancia un nuevo DocumentacionMemoriaController.
   * 
   * @param service DocumentacionMemoriaService
   */
  public DocumentacionMemoriaController(DocumentacionMemoriaService service) {
    log.debug("DocumentacionMemoriaController(DocumentacionMemoriaService service) - start");
    this.service = service;
    log.debug("DocumentacionMemoriaController(DocumentacionMemoriaService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link DocumentacionMemoria}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<DocumentacionMemoria>> findAll(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Page<DocumentacionMemoria> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link DocumentacionMemoria} con el id indicado.
   * 
   * @param id Identificador de {@link DocumentacionMemoria}.
   * @return {@link DocumentacionMemoria} correspondiente al id.
   */
  @GetMapping("/{id}")
  DocumentacionMemoria one(@PathVariable Long id) {
    log.debug("DocumentacionMemoria one(Long id) - start");
    DocumentacionMemoria returnValue = service.findById(id);
    log.debug("DocumentacionMemoria one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link DocumentacionMemoria} con id indicado.
   * 
   * @param id Identificador de {@link DocumentacionMemoria}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.delete(id);
    log.debug("delete(Long id) - end");
  }
}
