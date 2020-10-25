package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.exceptions.ComiteNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.service.ComiteService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ComiteController
 */
@RestController
@RequestMapping("/comites")
@Slf4j
public class ComiteController {

  /** Comité service */
  private ComiteService service;

  /**
   * Instancia un nuevo ComiteController.
   * 
   * @param service {@link ComiteService}.
   */
  public ComiteController(ComiteService service) {
    log.debug("ComiteController(ComiteService service) - start");
    this.service = service;
    log.debug("ComiteController(ComiteService service) - end");
  }

  /**
   * Crea nuevo {@link Comite}.
   * 
   * @param nuevoComite {@link Comite} que se quiere crear.
   * @return Nuevo {@link Comite} creado.
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Comite> newComite(@Valid @RequestBody Comite nuevoComite) {
    log.debug("newComite(Comite nuevoComite) - start");
    Comite returnValue = service.create(nuevoComite);
    log.debug("newComite(Comite nuevoComite) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Comite}.
   * 
   * @param updatedComite {@link Comite} a actualizar.
   * @param id            id {@link Comite} a actualizar.
   * @return {@link Comite} actualizado.
   */
  @PutMapping("/{id}")
  Comite replaceComite(@Valid @RequestBody Comite updatedComite, @PathVariable Long id) {
    log.debug("replaceComite(Comite updatedComite, Long id) - start");
    updatedComite.setId(id);
    Comite returnValue = service.update(updatedComite);
    log.debug("replaceComite(Comite updatedComite, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Comite} con el id indicado.
   * 
   * @param id Identificador de {@link Comite}.
   * @return {@link Comite} correspondiente al id.
   */
  @GetMapping("/{id}")
  Comite one(@PathVariable Long id) {
    log.debug("one(Long id) - start");
    Comite returnValue = service.findById(id);
    log.debug("one(Long id) - end");
    return returnValue;
  }

  /**
   * Se actualiza el {@link Comite} con el indicador de activo a false.
   * 
   * @param id Identificador de {@link Comite}.
   * @throws ComiteNotFoundException
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) throws ComiteNotFoundException {
    log.debug("deleteComite(Long id) - start");
    Comite comite = this.one(id);
    comite.setActivo(Boolean.FALSE);
    service.update(comite);
    log.debug("deleteComite(Long id) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Comite}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-V', 'ETI-CONV-V', 'ETI-EVC-VR', 'ETI-EVC-VR-INV', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV', 'ETI-PEV-VR-INV','ETI-PEV-V')")
  ResponseEntity<Page<Comite>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllComite(List<QueryCriteria> query,Pageable paging) - start");
    Page<Comite> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllComite(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllComite(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}