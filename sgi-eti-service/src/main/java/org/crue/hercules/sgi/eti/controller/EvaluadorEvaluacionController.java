package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.EvaluadorEvaluacion;
import org.crue.hercules.sgi.eti.service.EvaluadorEvaluacionService;
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
 * EstatoActaController
 */
@RestController
@RequestMapping("/evaluadorevaluaciones")
@Slf4j
public class EvaluadorEvaluacionController {

  /** EvaluadorEvaluacion service */
  private final EvaluadorEvaluacionService service;

  /**
   * Instancia un nuevo EvaluadorEvaluacionController.
   * 
   * @param service EvaluadorEvaluacionService
   */
  public EvaluadorEvaluacionController(EvaluadorEvaluacionService service) {
    log.debug("EvaluadorEvaluacionController(EvaluadorEvaluacionService service) - start");
    this.service = service;
    log.debug("EvaluadorEvaluacionController(EvaluadorEvaluacionService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link EvaluadorEvaluacion}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<EvaluadorEvaluacion>> findAll(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - start");
    Page<EvaluadorEvaluacion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link EvaluadorEvaluacion}.
   * 
   * @param nuevoEvaluadorEvaluacion {@link EvaluadorEvaluacion} que se quiere
   *                                 crear.
   * @return Nuevo {@link EvaluadorEvaluacion} creado.
   */
  @PostMapping
  ResponseEntity<EvaluadorEvaluacion> newEvaluadorEvaluacion(
      @Valid @RequestBody EvaluadorEvaluacion nuevoEvaluadorEvaluacion) {
    log.debug("newEvaluadorEvaluacion(EvaluadorEvaluacion nuevoEvaluadorEvaluacion) - start");
    EvaluadorEvaluacion returnValue = service.create(nuevoEvaluadorEvaluacion);
    log.debug("newEvaluadorEvaluacion(EvaluadorEvaluacion nuevoEvaluadorEvaluacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link EvaluadorEvaluacion}.
   * 
   * @param updatedEvaluadorEvaluacion {@link EvaluadorEvaluacion} a actualizar.
   * @param id                         id {@link EvaluadorEvaluacion} a
   *                                   actualizar.
   * @return {@link EvaluadorEvaluacion} actualizado.
   */
  @PutMapping("/{id}")
  EvaluadorEvaluacion replaceEvaluadorEvaluacion(@Valid @RequestBody EvaluadorEvaluacion updatedEvaluadorEvaluacion,
      @PathVariable Long id) {
    log.debug("replaceEvaluadorEvaluacion(EvaluadorEvaluacion updatedEvaluadorEvaluacion, Long id) - start");
    updatedEvaluadorEvaluacion.setId(id);
    EvaluadorEvaluacion returnValue = service.update(updatedEvaluadorEvaluacion);
    log.debug("replaceEvaluadorEvaluacion(EvaluadorEvaluacion updatedEvaluadorEvaluacion, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link EvaluadorEvaluacion} con el id indicado.
   * 
   * @param id Identificador de {@link EvaluadorEvaluacion}.
   * @return {@link EvaluadorEvaluacion} correspondiente al id.
   */
  @GetMapping("/{id}")
  EvaluadorEvaluacion one(@PathVariable Long id) {
    log.debug("EvaluadorEvaluacion one(Long id) - start");
    EvaluadorEvaluacion returnValue = service.findById(id);
    log.debug("EvaluadorEvaluacion one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link EvaluadorEvaluacion} con id indicado.
   * 
   * @param id Identificador de {@link EvaluadorEvaluacion}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.delete(id);
    log.debug("delete(Long id) - end");
  }

}
