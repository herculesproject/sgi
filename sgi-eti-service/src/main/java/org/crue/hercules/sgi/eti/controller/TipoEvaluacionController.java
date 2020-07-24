package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.service.TipoEvaluacionService;
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
import org.springframework.security.access.prepost.PreAuthorize;

import lombok.extern.slf4j.Slf4j;

/**
 * TipoEvaluacionController
 */
@RestController
@RequestMapping("/tipoEvaluaciones")
@Slf4j
public class TipoEvaluacionController {

  /** TipoEvaluacion service */
  private final TipoEvaluacionService service;

  /**
   * Instancia un nuevo TipoEvaluacionController.
   * 
   * @param service TipoEvaluacionService
   */
  public TipoEvaluacionController(TipoEvaluacionService service) {
    log.debug("TipoEvaluacionController(TipoEvaluacionService service) - start");
    this.service = service;
    log.debug("TipoEvaluacionController(TipoEvaluacionService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoEvaluacion}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping()
  @PreAuthorize("hasAuthorityForAnyUO('ETI-TIPOEVALUACION-VER')")
  ResponseEntity<Page<TipoEvaluacion>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Page<TipoEvaluacion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link TipoEvaluacion}.
   * 
   * @param nuevoTipoEvaluacion {@link TipoEvaluacion}. que se quiere crear.
   * @return Nuevo {@link TipoEvaluacion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('ETI-TIPOEVALUACION-EDITAR')")
  public ResponseEntity<TipoEvaluacion> newTipoEvaluacion(@Valid @RequestBody TipoEvaluacion nuevoTipoEvaluacion) {
    log.debug("newTipoEvaluacion(TipoEvaluacion nuevoTipoEvaluacion) - start");
    TipoEvaluacion returnValue = service.create(nuevoTipoEvaluacion);
    log.debug("newTipoEvaluacion(TipoEvaluacion nuevoTipoEvaluacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link TipoEvaluacion}.
   * 
   * @param updatedTipoEvaluacion {@link TipoEvaluacion} a actualizar.
   * @param id                    id {@link TipoEvaluacion} a actualizar.
   * @return {@link TipoEvaluacion} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-TIPOEVALUACION-EDITAR')")
  TipoEvaluacion replaceTipoEvaluacion(@Valid @RequestBody TipoEvaluacion updatedTipoEvaluacion,
      @PathVariable Long id) {
    log.debug("replaceTipoEvaluacion(TipoEvaluacion updatedTipoEvaluacion, Long id) - start");
    updatedTipoEvaluacion.setId(id);
    TipoEvaluacion returnValue = service.update(updatedTipoEvaluacion);
    log.debug("replaceTipoEvaluacion(TipoEvaluacion updatedTipoEvaluacion, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link TipoEvaluacion} con el id indicado.
   * 
   * @param id Identificador de {@link TipoEvaluacion}.
   * @return {@link TipoEvaluacion} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-TIPOEVALUACION-VER')")
  TipoEvaluacion one(@PathVariable Long id) {
    log.debug("TipoEvaluacion one(Long id) - start");
    TipoEvaluacion returnValue = service.findById(id);
    log.debug("TipoEvaluacion one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link TipoEvaluacion} con id indicado.
   * 
   * @param id Identificador de {@link TipoEvaluacion}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-TIPOEVALUACION-EDITAR')")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    TipoEvaluacion tipoEvaluacion = this.one(id);
    tipoEvaluacion.setActivo(Boolean.FALSE);
    service.update(tipoEvaluacion);
    log.debug("delete(Long id) - end");
  }

}
