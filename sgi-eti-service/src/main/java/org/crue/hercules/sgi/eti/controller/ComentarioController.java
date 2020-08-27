package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.service.ComentarioService;
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
 * ComentarioController
 */
@RestController
@RequestMapping("/comentarios")
@Slf4j
public class ComentarioController {

  /** Comentario service */
  private final ComentarioService service;

  /**
   * Instancia un nuevo ComentarioController.
   * 
   * @param service ComentarioService
   */
  public ComentarioController(ComentarioService service) {
    log.debug("ComentarioController(ComentarioService service) - start");
    this.service = service;
    log.debug("ComentarioController(ComentarioService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Comentario}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<Comentario>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - start");
    Page<Comentario> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link Comentario}.
   * 
   * @param nuevoComentario {@link Comentario} que se quiere crear.
   * @return Nueva {@link Comentario} creada.
   */
  @PostMapping
  ResponseEntity<Comentario> newComentario(@Valid @RequestBody Comentario nuevoComentario) {
    log.debug("newComentario(Comentario nuevoComentario) - start");
    Comentario returnValue = service.create(nuevoComentario);
    log.debug("newComentario(Comentario nuevoComentario) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Comentario}.
   * 
   * @param updatedComentario {@link Comentario} a actualizar.
   * @param id                id {@link Comentario} a actualizar.
   * @return {@link Comentario} actualizado.
   */
  @PutMapping("/{id}")
  Comentario replaceComentario(@Valid @RequestBody Comentario updatedComentario, @PathVariable Long id) {
    log.debug("replaceComentario(Comentario updatedComentario, Long id) - start");
    updatedComentario.setId(id);
    Comentario returnValue = service.update(updatedComentario);
    log.debug("replaceComentario(Comentario updatedComentario, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve la {@link Comentario} con el id indicado.
   * 
   * @param id Identificador de {@link Comentario}.
   * @return {@link Comentario} correspondiente al id.
   */
  @GetMapping("/{id}")
  Comentario one(@PathVariable Long id) {
    log.debug("Comentario one(Long id) - start");
    Comentario returnValue = service.findById(id);
    log.debug("Comentario one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link Comentario} con id indicado.
   * 
   * @param id Identificador de {@link Comentario}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.delete(id);
    log.debug("delete(Long id) - end");
  }
}
