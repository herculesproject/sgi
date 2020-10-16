package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.service.TipoFaseService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/tipofases")
@Slf4j
public class TipoFaseController {

  private final TipoFaseService tipoFaseService;

  public TipoFaseController(TipoFaseService tipoFaseService) {
    log.debug("TipoFaseController(TipoFaseService tipoFaseService) - start");
    this.tipoFaseService = tipoFaseService;
    log.debug("TipoFaseController(TipoFaseService tipoFaseService) - start");
  }

  /**
   * Devuelve todas las entidades {@link TipoFase} activos paginadas
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link TipoFase} paginadas
   */
  @GetMapping()
  // @PreAuthorize("hasAuthorityForAnyUO ('CSP-TFAS-V')")
  ResponseEntity<Page<TipoFase>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - start");
    Page<TipoFase> page = tipoFaseService.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve todas las entidades {@link TipoFase} paginadas
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link TipoFase} paginadas
   */
  @GetMapping("/todos")
  // @PreAuthorize("hasAuthorityForAnyUO ('CSP-TFAS-V')")
  ResponseEntity<Page<TipoFase>> findAllTodos(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - start");
    Page<TipoFase> page = tipoFaseService.findAllTodos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link TipoFase}.
   * 
   * @param nuevoTipoFase {@link TipoFase}. que se quiere crear.
   * @return returnTipoFase {@link TipoFase} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO ('CSP-TFAS-C')")
  public ResponseEntity<TipoFase> create(@Valid @RequestBody TipoFase nuevoTipoFase) {
    log.debug("createTipoFase(TipoFase nuevoTipoFase) - start");
    TipoFase returnTipoFase = tipoFaseService.create(nuevoTipoFase);
    log.debug("createTipoFase(TipoFase nuevoTipoFase) - end");
    return new ResponseEntity<>(returnTipoFase, HttpStatus.CREATED);
  }

  /**
   * Devuelve el {@link TipoFase} con el id indicado.
   * 
   * @param id Identificador de {@link TipoFase}.
   * @return returnTipoFase {@link TipoFase} correspondiente al id.
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO ('CSP-TFAS-V')")
  TipoFase findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    TipoFase returnTipoFase = tipoFaseService.findById(id);
    log.debug("findById(Long id) - end");
    return returnTipoFase;
  }

  /**
   * Actualiza {@link TipoFase}.
   * 
   * @param updatedTipoFase {@link TipoFase} a actualizar.
   * @param id              id {@link TipoFase} a actualizar.
   * @return {@link TipoFase} actualizado.
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO ('CSP-TFAS-E')")
  TipoFase update(@PathVariable Long id, @Valid @RequestBody TipoFase updatedTipoFase) {
    log.debug("updateTipoFase(Long id, TipoFase updatedTipoFase) - start");
    updatedTipoFase.setId(id);
    TipoFase returnTipoFase = tipoFaseService.update(updatedTipoFase);
    log.debug("updateTipoFase(Long id, TipoFase updatedTipoFase) - end");
    return returnTipoFase;
  }

  /**
   * Establece el campo activo como false en el {@link TipoFase} con id indicado.
   * 
   * @param id Identificador de {@link Tipo}.
   */
  @DeleteMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO ('CSP-TFAS-B')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    tipoFaseService.disable(id);
    log.debug("deleteById(Long id) - start");
  }

}
