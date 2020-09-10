package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.service.EvaluacionService;
import org.crue.hercules.sgi.eti.service.EvaluadorService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
 * EvaluadorController
 */
@RestController
@RequestMapping("/evaluadores")
@Slf4j
public class EvaluadorController {

  /** Evaluador service */
  private final EvaluadorService evaluadorService;

  /** Evaluación service. */
  private final EvaluacionService evaluacionService;

  /**
   * Instancia un nuevo EvaluadorController. x
   * 
   * @param evaluadorService  EvaluadorService
   * @param evaluacionService EvaluacionService
   */
  public EvaluadorController(EvaluadorService evaluadorService, EvaluacionService evaluacionService) {
    log.debug("EvaluadorController(EvaluadorService service) - start");
    this.evaluadorService = evaluadorService;
    this.evaluacionService = evaluacionService;
    log.debug("EvaluadorController(EvaluadorService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Evaluador}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<Evaluador>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Page<Evaluador> page = evaluadorService.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link Evaluador}.
   * 
   * @param nuevoEvaluador {@link Evaluador}. que se quiere crear.
   * @return Nuevo {@link Evaluador} creado.
   */
  @PostMapping
  ResponseEntity<Evaluador> newEvaluador(@Valid @RequestBody Evaluador nuevoEvaluador) {
    log.debug("newEvaluador(Evaluador nuevoEvaluador) - start");
    Evaluador returnValue = evaluadorService.create(nuevoEvaluador);
    log.debug("newEvaluador(Evaluador nuevoEvaluador) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Evaluador}.
   * 
   * @param updatedEvaluador {@link Evaluador} a actualizar.
   * @param id               id {@link Evaluador} a actualizar.
   * @return {@link Evaluador} actualizado.
   */
  @PutMapping("/{id}")
  Evaluador replaceEvaluador(@Valid @RequestBody Evaluador updatedEvaluador, @PathVariable Long id) {
    log.debug("replaceEvaluador(Evaluador updatedEvaluador, Long id) - start");
    updatedEvaluador.setId(id);
    Evaluador returnValue = evaluadorService.update(updatedEvaluador);
    log.debug("replaceEvaluador(Evaluador updatedEvaluador, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Evaluador} con el id indicado.
   * 
   * @param id Identificador de {@link Evaluador}.
   * @return {@link Evaluador} correspondiente al id.
   */
  @GetMapping("/{id}")
  Evaluador one(@PathVariable Long id) {
    log.debug("Evaluador one(Long id) - start");
    Evaluador returnValue = evaluadorService.findById(id);
    log.debug("Evaluador one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link Evaluador} con id indicado.
   * 
   * @param id Identificador de {@link Evaluador}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    Evaluador evaluador = this.one(id);
    evaluador.setActivo(Boolean.FALSE);
    evaluadorService.update(evaluador);
    log.debug("delete(Long id) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Evaluacion} según su
   * {@link Evaluador}.
   * 
   * @param personaRef Identificador del {@link Evaluacion}
   * @param query      filtro de {@link QueryCriteria}.
   * @param pageable   pageable
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @GetMapping("/{personaRef}/evaluaciones")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-VR', 'ETI-EVC-EVALR')")
  ResponseEntity<Page<Evaluacion>> getEvaluaciones(@PathVariable String personaRef,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("getEvaluaciones(String personaRef, List<QueryCriteria> query, Pageable pageable) - start");
    Page<Evaluacion> page = evaluacionService.findByEvaluador(personaRef, query, pageable);
    log.debug("getEvaluaciones(String personaRef, List<QueryCriteria> query, Pageable pageable) - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene todas las entidades {@link Evaluacion}, en estado "En evaluación
   * seguimiento anual" (id = 11), "En evaluación seguimiento final" (id = 12) o
   * "En secretaría seguimiento final aclaraciones" (id = 13), paginadas asociadas
   * a un evaluador
   * 
   * @param personaRef Persona Ref del {@link Evaluador}
   * @param query      filtro de {@link QueryCriteria}.
   * @param pageable   pageable
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  @GetMapping("/{personaRef}/evaluaciones-seguimiento")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-VR', 'ETI-EVC-EVALR')")
  ResponseEntity<Page<Evaluacion>> findEvaluacionesEnSeguimiento(@PathVariable String personaRef,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable pageable) {

    log.debug("findEvaluacionesEnSeguimiento(String personaRef, List<QueryCriteria> query, Pageable pageable) - start");
    Page<Evaluacion> page = evaluacionService.findEvaluacionesEnSeguimientosByEvaluador(personaRef, query, pageable);

    log.debug("findEvaluacionesEnSeguimiento(String personaRef, List<QueryCriteria> query, Pageable pageable) - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}
