package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.service.TipoRegimenConcurrenciaService;
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

/**
 * TipoRegimenConcurrenciaController
 */

@RestController
@RequestMapping("/tiporegimenconcurrencias")
@Slf4j
public class TipoRegimenConcurrenciaController {

  /** TipoRegimenConcurrencia service */
  private final TipoRegimenConcurrenciaService service;

  public TipoRegimenConcurrenciaController(TipoRegimenConcurrenciaService tipoRegimenConcurrenciaService) {
    log.debug(
        "TipoRegimenConcurrenciaController(TipoRegimenConcurrenciaService tipoRegimenConcurrenciaService) - start");
    this.service = tipoRegimenConcurrenciaService;
    log.debug("TipoRegimenConcurrenciaController(TipoRegimenConcurrenciaService tipoRegimenConcurrenciaService) - end");
  }

  /**
   * Crea nuevo {@link TipoRegimenConcurrencia}.
   * 
   * @param tipoRegimenConcurrencia {@link TipoRegimenConcurrencia}. que se quiere
   *                                crear.
   * @return Nuevo {@link TipoRegimenConcurrencia} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('SYSADMIN')")
  public ResponseEntity<TipoRegimenConcurrencia> create(
      @Valid @RequestBody TipoRegimenConcurrencia tipoRegimenConcurrencia) {
    log.debug("create(TipoRegimenConcurrencia tipoRegimenConcurrencia) - start");
    TipoRegimenConcurrencia returnValue = service.create(tipoRegimenConcurrencia);
    log.debug("create(TipoRegimenConcurrencia tipoRegimenConcurrencia) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link TipoRegimenConcurrencia}.
   * 
   * @param tipoRegimenConcurrencia {@link TipoRegimenConcurrencia} a actualizar.
   * @param id                      Identificador {@link TipoRegimenConcurrencia}
   *                                a actualizar.
   * @return TipoRegimenConcurrencia {@link TipoRegimenConcurrencia} actualizado
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('SYSADMIN')")
  public TipoRegimenConcurrencia update(@Valid @RequestBody TipoRegimenConcurrencia tipoRegimenConcurrencia,
      @PathVariable Long id) {
    log.debug("update(TipoRegimenConcurrencia tipoRegimenConcurrencia, Long id) - start");
    tipoRegimenConcurrencia.setId(id);
    TipoRegimenConcurrencia returnValue = service.update(tipoRegimenConcurrencia);
    log.debug("update(TipoRegimenConcurrencia tipoRegimenConcurrencia, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link TipoRegimenConcurrencia} con id indicado.
   * 
   * @param id Identificador de {@link TipoRegimenConcurrencia}.
   */
  @DeleteMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('SYSADMIN')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.disable(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoRegimenConcurrencia}
   * activos.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link TipoRegimenConcurrencia} paginadas y
   *         filtradas.
   */
  @GetMapping()
  // @PreAuthorize("hasAuthorityForAnyUO('SYSADMIN')")
  ResponseEntity<Page<TipoRegimenConcurrencia>> findAll(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Page<TipoRegimenConcurrencia> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoRegimenConcurrencia}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link TipoRegimenConcurrencia} paginadas y
   *         filtradas.
   */
  @GetMapping("/todos")
  // @PreAuthorize("hasAuthorityForAnyUO('SYSADMIN')")
  ResponseEntity<Page<TipoRegimenConcurrencia>> findAllTodos(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(List<QueryCriteria> query,Pageable paging) - start");
    Page<TipoRegimenConcurrencia> page = service.findAllTodos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllTodos(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link TipoRegimenConcurrencia} con el id indicado.
   * 
   * @param id Identificador de {@link TipoRegimenConcurrencia}.
   * @return TipoRegimenConcurrencia {@link TipoRegimenConcurrencia}
   *         correspondiente al id
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('SYSADMIN')")
  TipoRegimenConcurrencia findById(@PathVariable Long id) {
    log.debug("TipoRegimenConcurrencia findById(Long id) - start");
    TipoRegimenConcurrencia returnValue = service.findById(id);
    log.debug("TipoRegimenConcurrencia findById(Long id) - end");
    return returnValue;
  }
}
