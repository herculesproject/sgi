package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.InformeFormulario;
import org.crue.hercules.sgi.eti.service.InformeFormularioService;
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
 * InformeFormularioController
 */
@RestController
@RequestMapping("/informeformularios")
@Slf4j
public class InformeFormularioController {

  /** InformeFormulario service */
  private final InformeFormularioService service;

  /**
   * Instancia un nuevo InformeFormularioController.
   * 
   * @param service InformeFormularioService
   */
  public InformeFormularioController(InformeFormularioService service) {
    log.debug("InformeFormularioController(InformeFormularioService service) - start");
    this.service = service;
    log.debug("InformeFormularioController(InformeFormularioService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link InformeFormulario}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<InformeFormulario>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Page<InformeFormulario> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link InformeFormulario}.
   * 
   * @param nuevoInformeFormulario {@link InformeFormulario}. que se quiere crear.
   * @return Nuevo {@link InformeFormulario} creado.
   */
  @PostMapping
  public ResponseEntity<InformeFormulario> newInformeFormulario(
      @Valid @RequestBody InformeFormulario nuevoInformeFormulario) {
    log.debug("newInformeFormulario(InformeFormulario nuevoInformeFormulario) - start");
    InformeFormulario returnValue = service.create(nuevoInformeFormulario);
    log.debug("newInformeFormulario(InformeFormulario nuevoInformeFormulario) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link InformeFormulario}.
   * 
   * @param updatedInformeFormulario {@link InformeFormulario} a actualizar.
   * @param id                       id {@link InformeFormulario} a actualizar.
   * @return {@link InformeFormulario} actualizado.
   */
  @PutMapping("/{id}")
  InformeFormulario replaceInformeFormulario(@Valid @RequestBody InformeFormulario updatedInformeFormulario,
      @PathVariable Long id) {
    log.debug("replaceInformeFormulario(InformeFormulario updatedInformeFormulario, Long id) - start");
    updatedInformeFormulario.setId(id);
    InformeFormulario returnValue = service.update(updatedInformeFormulario);
    log.debug("replaceInformeFormulario(InformeFormulario updatedInformeFormulario, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link InformeFormulario} con el id indicado.
   * 
   * @param id Identificador de {@link InformeFormulario}.
   * @return {@link InformeFormulario} correspondiente al id.
   */
  @GetMapping("/{id}")
  InformeFormulario one(@PathVariable Long id) {
    log.debug("InformeFormulario one(Long id) - start");
    InformeFormulario returnValue = service.findById(id);
    log.debug("InformeFormulario one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link InformeFormulario} con id indicado.
   * 
   * @param id Identificador de {@link InformeFormulario}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.delete(id);
    log.debug("delete(Long id) - end");
  }

}
