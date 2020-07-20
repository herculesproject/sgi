package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.service.FormularioService;
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
 * FormularioController
 */
@RestController
@RequestMapping("/formularios")
@Slf4j
public class FormularioController {

  /** Formulario service */
  private final FormularioService service;

  /**
   * Instancia un nuevo FormularioController.
   * 
   * @param service FormularioService
   */
  public FormularioController(FormularioService service) {
    log.debug("FormularioController(FormularioService service) - start");
    this.service = service;
    log.debug("FormularioController(FormularioService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Formulario}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<Formulario>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Page<Formulario> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link Formulario}.
   * 
   * @param nuevoFormulario {@link Formulario}. que se quiere crear.
   * @return Nuevo {@link Formulario} creado.
   */
  @PostMapping
  ResponseEntity<Formulario> newFormulario(@Valid @RequestBody Formulario nuevoFormulario) {
    log.debug("newFormulario(Formulario nuevoFormulario) - start");
    Formulario returnValue = service.create(nuevoFormulario);
    log.debug("newFormulario(Formulario nuevoFormulario) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Formulario}.
   * 
   * @param updatedFormulario {@link Formulario} a actualizar.
   * @param id                id {@link Formulario} a actualizar.
   * @return {@link Formulario} actualizado.
   */
  @PutMapping("/{id}")
  Formulario replaceFormulario(@Valid @RequestBody Formulario updatedFormulario, @PathVariable Long id) {
    log.debug("replaceFormulario(Formulario updatedFormulario, Long id) - start");
    updatedFormulario.setId(id);
    Formulario returnValue = service.update(updatedFormulario);
    log.debug("replaceFormulario(Formulario updatedFormulario, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Formulario} con el id indicado.
   * 
   * @param id Identificador de {@link Formulario}.
   * @return {@link Formulario} correspondiente al id.
   */
  @GetMapping("/{id}")
  Formulario one(@PathVariable Long id) {
    log.debug("Formulario one(Long id) - start");
    Formulario returnValue = service.findById(id);
    log.debug("Formulario one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link Formulario} con id indicado.
   * 
   * @param id Identificador de {@link Formulario}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    Formulario formulario = this.one(id);
    formulario.setActivo(Boolean.FALSE);
    service.update(formulario);
    log.debug("delete(Long id) - end");
  }

}
