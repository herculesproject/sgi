package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.service.ActaService;
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
 * ActaController
 */
@RestController
@RequestMapping("/actas")
@Slf4j
public class ActaController {

  /** Acta service */
  private final ActaService service;

  /**
   * Instancia un nuevo ActaController.
   * 
   * @param service ActaService
   */
  public ActaController(ActaService service) {
    log.debug("ActaController(ActaService service) - start");
    this.service = service;
    log.debug("ActaController(ActaService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Acta}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping()
  @PreAuthorize("hasAuthorityForAnyUO('ETI-ACTA-VER')")
  ResponseEntity<Page<Acta>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - start");
    Page<Acta> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link Acta}.
   * 
   * @param nuevoActa {@link Acta} que se quiere crear.
   * @return Nuevo {@link Acta} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('ETI-ACTA-EDITAR')")
  public ResponseEntity<Acta> newActa(@Valid @RequestBody Acta nuevoActa) {
    log.debug("newActa(Acta nuevoActa) - start");
    Acta returnValue = service.create(nuevoActa);
    log.debug("newActa(Acta nuevoActa) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Acta}.
   * 
   * @param updatedActa {@link Acta} a actualizar.
   * @param id          id {@link Acta} a actualizar.
   * @return {@link Acta} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-ACTA-EDITAR')")
  Acta replaceActa(@Valid @RequestBody Acta updatedActa, @PathVariable Long id) {
    log.debug("replaceActa(Acta updatedActa, Long id) - start");
    updatedActa.setId(id);
    Acta returnValue = service.update(updatedActa);
    log.debug("replaceActa(Acta updatedActa, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Acta} con el id indicado.
   * 
   * @param id Identificador de {@link Acta}.
   * @return {@link Acta} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-ACTA-VER')")
  Acta one(@PathVariable Long id) {
    log.debug("Acta one(Long id) - start");
    Acta returnValue = service.findById(id);
    log.debug("Acta one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link Acta} con id indicado.
   * 
   * @param id Identificador de {@link Acta}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-ACTA-EDITAR')")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    Acta acta = this.one(id);
    acta.setActivo(Boolean.FALSE);
    service.update(acta);
    log.debug("delete(Long id) - end");
  }

}
