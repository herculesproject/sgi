package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.FormularioMemoria;
import org.crue.hercules.sgi.eti.service.FormularioMemoriaService;
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
 * FormularioMemoriaController
 */
@RestController
@RequestMapping(ConstantesEti.FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH)
@Slf4j
public class FormularioMemoriaController {

  /** FormularioMemoria service */
  private final FormularioMemoriaService service;

  /**
   * Instancia un nuevo FormularioMemoriaController.
   * 
   * @param service FormularioMemoriaService
   */
  public FormularioMemoriaController(FormularioMemoriaService service) {
    log.debug("FormularioMemoriaController(FormularioMemoriaService service) - start");
    this.service = service;
    log.debug("FormularioMemoriaController(FormularioMemoriaService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link FormularioMemoria}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<FormularioMemoria>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - start");
    Page<FormularioMemoria> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link FormularioMemoria}.
   * 
   * @param nuevoFormularioMemoria {@link FormularioMemoria} que se quiere crear.
   * @return Nuevo {@link FormularioMemoria} creado.
   */
  @PostMapping
  ResponseEntity<FormularioMemoria> newFormularioMemoria(@Valid @RequestBody FormularioMemoria nuevoFormularioMemoria) {
    log.debug("newFormularioMemoria(FormularioMemoria nuevoFormularioMemoria) - start");
    FormularioMemoria returnValue = service.create(nuevoFormularioMemoria);
    log.debug("newFormularioMemoria(FormularioMemoria nuevoFormularioMemoria) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link FormularioMemoria}.
   * 
   * @param updatedFormularioMemoria {@link FormularioMemoria} a actualizar.
   * @param id                       id {@link FormularioMemoria} a actualizar.
   * @return {@link FormularioMemoria} actualizado.
   */
  @PutMapping(ConstantesEti.PATH_PARAMETER_ID)
  FormularioMemoria replaceFormularioMemoria(@Valid @RequestBody FormularioMemoria updatedFormularioMemoria,
      @PathVariable Long id) {
    log.debug("replaceFormularioMemoria(FormularioMemoria updatedFormularioMemoria, Long id) - start");
    updatedFormularioMemoria.setId(id);
    FormularioMemoria returnValue = service.update(updatedFormularioMemoria);
    log.debug("replaceFormularioMemoria(FormularioMemoria updatedFormularioMemoria, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link FormularioMemoria} con el id indicado.
   * 
   * @param id Identificador de {@link FormularioMemoria}.
   * @return {@link FormularioMemoria} correspondiente al id.
   */
  @GetMapping(ConstantesEti.PATH_PARAMETER_ID)
  FormularioMemoria one(@PathVariable Long id) {
    log.debug("FormularioMemoria one(Long id) - start");
    FormularioMemoria returnValue = service.findById(id);
    log.debug("FormularioMemoria one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link FormularioMemoria} con id indicado.
   * 
   * @param id Identificador de {@link FormularioMemoria}.
   */
  @DeleteMapping(ConstantesEti.PATH_PARAMETER_ID)
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    FormularioMemoria formularioMemoria = this.one(id);
    formularioMemoria.setActivo(Boolean.FALSE);
    service.update(formularioMemoria);
    log.debug("delete(Long id) - end");
  }

}
