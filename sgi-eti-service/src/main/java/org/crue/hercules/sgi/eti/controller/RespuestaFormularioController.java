package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.RespuestaFormulario;
import org.crue.hercules.sgi.eti.service.RespuestaFormularioService;
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
 * RespuestaFormularioController
 */
@RestController
@RequestMapping("/respuestaformularios")
@Slf4j
public class RespuestaFormularioController {

  /** RespuestaFormulario service */
  private final RespuestaFormularioService service;

  /**
   * Instancia un nuevo RespuestaFormularioController.
   * 
   * @param service RespuestaFormularioService
   */
  public RespuestaFormularioController(RespuestaFormularioService service) {
    log.debug("RespuestaFormularioController(RespuestaFormularioService service) - start");
    this.service = service;
    log.debug("RespuestaFormularioController(RespuestaFormularioService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link RespuestaFormulario}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<RespuestaFormulario>> findAll(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Page<RespuestaFormulario> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link RespuestaFormulario}.
   * 
   * @param nuevoRespuestaFormulario {@link RespuestaFormulario}. que se quiere
   *                                 crear.
   * @return Nuevo {@link RespuestaFormulario} creado.
   */
  @PostMapping
  public ResponseEntity<RespuestaFormulario> newRespuestaFormulario(
      @Valid @RequestBody RespuestaFormulario nuevoRespuestaFormulario) {
    log.debug("newRespuestaFormulario(RespuestaFormulario nuevoRespuestaFormulario) - start");
    RespuestaFormulario returnValue = service.create(nuevoRespuestaFormulario);
    log.debug("newRespuestaFormulario(RespuestaFormulario nuevoRespuestaFormulario) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link RespuestaFormulario}.
   * 
   * @param updatedRespuestaFormulario {@link RespuestaFormulario} a actualizar.
   * @param id                         id {@link RespuestaFormulario} a
   *                                   actualizar.
   * @return {@link RespuestaFormulario} actualizado.
   */
  @PutMapping("/{id}")
  RespuestaFormulario replaceRespuestaFormulario(@Valid @RequestBody RespuestaFormulario updatedRespuestaFormulario,
      @PathVariable Long id) {
    log.debug("replaceRespuestaFormulario(RespuestaFormulario updatedRespuestaFormulario, Long id) - start");
    updatedRespuestaFormulario.setId(id);
    RespuestaFormulario returnValue = service.update(updatedRespuestaFormulario);
    log.debug("replaceRespuestaFormulario(RespuestaFormulario updatedRespuestaFormulario, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link RespuestaFormulario} con el id indicado.
   * 
   * @param id Identificador de {@link RespuestaFormulario}.
   * @return {@link RespuestaFormulario} correspondiente al id.
   */
  @GetMapping("/{id}")
  RespuestaFormulario one(@PathVariable Long id) {
    log.debug("RespuestaFormulario one(Long id) - start");
    RespuestaFormulario returnValue = service.findById(id);
    log.debug("RespuestaFormulario one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link RespuestaFormulario} con id indicado.
   * 
   * @param id Identificador de {@link RespuestaFormulario}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.delete(id);
    log.debug("delete(Long id) - end");
  }

}
