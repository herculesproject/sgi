package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.service.DocumentacionMemoriaService;
import org.crue.hercules.sgi.eti.util.ConstantesEti;
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
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * DocumentacionMemoriaController
 */
@RestController
@RequestMapping(ConstantesEti.DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH)
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
   * Crea nuevo {@link DocumentacionMemoria}.
   * 
   * @param nuevoDocumentacionMemoria {@link DocumentacionMemoria}. que se quiere
   *                                  crear.
   * @return Nuevo {@link DocumentacionMemoria} creado.
   */
  @PostMapping
  ResponseEntity<DocumentacionMemoria> newDocumentacionMemoria(
      @Valid @RequestBody DocumentacionMemoria nuevoDocumentacionMemoria) {
    log.debug("newDocumentacionMemoria(DocumentacionMemoria nuevoDocumentacionMemoria) - start");
    DocumentacionMemoria returnValue = service.create(nuevoDocumentacionMemoria);
    log.debug("newDocumentacionMemoria(DocumentacionMemoria nuevoDocumentacionMemoria) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link DocumentacionMemoria}.
   * 
   * @param updatedDocumentacionMemoria {@link DocumentacionMemoria} a actualizar.
   * @param id                          id {@link DocumentacionMemoria} a
   *                                    actualizar.
   * @return {@link DocumentacionMemoria} actualizado.
   */
  @PutMapping(ConstantesEti.PATH_PARAMETER_ID)
  DocumentacionMemoria replaceDocumentacionMemoria(@Valid @RequestBody DocumentacionMemoria updatedDocumentacionMemoria,
      @PathVariable Long id) {
    log.debug("replaceDocumentacionMemoria(DocumentacionMemoria updatedDocumentacionMemoria, Long id) - start");
    updatedDocumentacionMemoria.setId(id);
    DocumentacionMemoria returnValue = service.update(updatedDocumentacionMemoria);
    log.debug("replaceDocumentacionMemoria(DocumentacionMemoria updatedDocumentacionMemoria, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link DocumentacionMemoria} con el id indicado.
   * 
   * @param id Identificador de {@link DocumentacionMemoria}.
   * @return {@link DocumentacionMemoria} correspondiente al id.
   */
  @GetMapping(ConstantesEti.PATH_PARAMETER_ID)
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
  @DeleteMapping(ConstantesEti.PATH_PARAMETER_ID)
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.delete(id);
    log.debug("delete(Long id) - end");
  }

}
