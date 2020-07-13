package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.service.EvaluadorService;
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
 * EvaluadorController
 */
@RestController
@RequestMapping(ConstantesEti.EVALUADOR_CONTROLLER_BASE_PATH)
@Slf4j
public class EvaluadorController {

  /** Evaluador service */
  private final EvaluadorService service;

  /**
   * Instancia un nuevo EvaluadorController.
   * 
   * @param service EvaluadorService
   */
  public EvaluadorController(EvaluadorService service) {
    log.debug("EvaluadorController(EvaluadorService service) - start");
    this.service = service;
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
    Page<Evaluador> page = service.findAll(query, paging);

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
    Evaluador returnValue = service.create(nuevoEvaluador);
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
  @PutMapping(ConstantesEti.PATH_PARAMETER_ID)
  Evaluador replaceEvaluador(@Valid @RequestBody Evaluador updatedEvaluador, @PathVariable Long id) {
    log.debug("replaceEvaluador(Evaluador updatedEvaluador, Long id) - start");
    updatedEvaluador.setId(id);
    Evaluador returnValue = service.update(updatedEvaluador);
    log.debug("replaceEvaluador(Evaluador updatedEvaluador, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Evaluador} con el id indicado.
   * 
   * @param id Identificador de {@link Evaluador}.
   * @return {@link Evaluador} correspondiente al id.
   */
  @GetMapping(ConstantesEti.PATH_PARAMETER_ID)
  Evaluador one(@PathVariable Long id) {
    log.debug("Evaluador one(Long id) - start");
    Evaluador returnValue = service.findById(id);
    log.debug("Evaluador one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link Evaluador} con id indicado.
   * 
   * @param id Identificador de {@link Evaluador}.
   */
  @DeleteMapping(ConstantesEti.PATH_PARAMETER_ID)
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    Evaluador evaluador = this.one(id);
    evaluador.setActivo(Boolean.FALSE);
    service.update(evaluador);
    log.debug("delete(Long id) - end");
  }

}
