package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ListadoAreaTematica;
import org.crue.hercules.sgi.csp.service.ListadoAreaTematicaService;
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
 * ListadoAreaTematicaController
 */

@RestController
@RequestMapping("/listadoareatematicas")
@Slf4j
public class ListadoAreaTematicaController {

  /** ListadoAreaTematica service */
  private final ListadoAreaTematicaService service;

  public ListadoAreaTematicaController(ListadoAreaTematicaService listadoAreaTematicaService) {
    log.debug("ListadoAreaTematicaController(ListadoAreaTematicaService listadoAreaTematicaService) - start");
    this.service = listadoAreaTematicaService;
    log.debug("ListadoAreaTematicaController(ListadoAreaTematicaService listadoAreaTematicaService) - end");
  }

  /**
   * Crea nuevo {@link ListadoAreaTematica}.
   * 
   * @param listadoAreaTematica {@link ListadoAreaTematica}. que se quiere crear.
   * @return Nuevo {@link ListadoAreaTematica} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-LATEM-C')")
  public ResponseEntity<ListadoAreaTematica> create(@Valid @RequestBody ListadoAreaTematica listadoAreaTematica) {
    log.debug("create(ListadoAreaTematica listadoAreaTematica) - start");
    ListadoAreaTematica returnValue = service.create(listadoAreaTematica);
    log.debug("create(ListadoAreaTematica listadoAreaTematica) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link ListadoAreaTematica}.
   * 
   * @param listadoAreaTematica {@link ListadoAreaTematica} a actualizar.
   * @param id                  Identificador {@link ListadoAreaTematica} a
   *                            actualizar.
   * @return ListadoAreaTematica {@link ListadoAreaTematica} actualizado
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-LATEM-E')")
  public ListadoAreaTematica update(@Valid @RequestBody ListadoAreaTematica listadoAreaTematica,
      @PathVariable Long id) {
    log.debug("update(ListadoAreaTematica listadoAreaTematica, Long id) - start");
    listadoAreaTematica.setId(id);
    ListadoAreaTematica returnValue = service.update(listadoAreaTematica);
    log.debug("update(ListadoAreaTematica listadoAreaTematica, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link ListadoAreaTematica} con id indicado.
   * 
   * @param id Identificador de {@link ListadoAreaTematica}.
   */
  @DeleteMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-LATEM-B')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.disable(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ListadoAreaTematica}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link ListadoAreaTematica} paginadas y
   *         filtradas.
   */
  @GetMapping()
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-LATEM-V')")
  ResponseEntity<Page<ListadoAreaTematica>> findAll(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Page<ListadoAreaTematica> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link ListadoAreaTematica} con el id indicado.
   * 
   * @param id Identificador de {@link ListadoAreaTematica}.
   * @return ListadoAreaTematica {@link ListadoAreaTematica} correspondiente al id
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-LATEM-V')")
  ListadoAreaTematica findById(@PathVariable Long id) {
    log.debug("ListadoAreaTematica findById(Long id) - start");
    ListadoAreaTematica returnValue = service.findById(id);
    log.debug("ListadoAreaTematica findById(Long id) - end");
    return returnValue;
  }
}
