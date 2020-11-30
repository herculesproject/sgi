package org.crue.hercules.sgi.usr.controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.usr.model.BaseEntity.Update;
import org.crue.hercules.sgi.usr.model.Unidad;
import org.crue.hercules.sgi.usr.service.UnidadService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
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
 * UnidadController
 */
@RestController
@RequestMapping("/unidades")
@Slf4j
public class UnidadController {

  /** Unidad service */
  private final UnidadService service;

  /**
   * Instancia un nuevo UnidadController.
   * 
   * @param service {@link UnidadService}
   */
  public UnidadController(UnidadService service) {
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Unidad}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping()
  // @PreAuthorize("hasAuthorityForAnyUO('USR-UNI-V')")
  ResponseEntity<Page<Unidad>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - start");
    Page<Unidad> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Unidad}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/todos")
  // @PreAuthorize("hasAuthorityForAnyUO('USR-UNI-V')")
  ResponseEntity<Page<Unidad>> findAllTodos(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - start");
    Page<Unidad> page = service.findAllTodos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Unidad} restringida por los
   * permisos del usuario logueado.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/restringidos")
  // @PreAuthorize("hasAuthorityForAnyUO('USR-UNI-V')")
  ResponseEntity<Page<Unidad>> findAllTodosRestringidos(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging, Authentication atuhentication) {
    log.debug(
        "findAllTodosRestringidos(List<QueryCriteria> query, Pageable paging, Authentication atuhentication) - start");

    List<String> acronimosUnidadGestion = atuhentication.getAuthorities().stream().map(acronimo -> {
      if (acronimo.getAuthority().indexOf("_") > 0) {
        return acronimo.getAuthority().split("_")[1];
      }
      return null;
    }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

    Page<Unidad> page = service.findAllRestringidos(query, acronimosUnidadGestion, paging);

    if (page.isEmpty()) {
      log.debug(
          "findAllTodosRestringidos(List<QueryCriteria> query, Pageable paging, Authentication atuhentication) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug(
        "findAllTodosRestringidos(List<QueryCriteria> query, Pageable paging, Authentication atuhentication) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link Unidad} con el id indicado.
   * 
   * @param id Identificador de {@link Unidad}.
   * @return {@link Unidad} correspondiente al id.
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('USR-UNI-V')")
  Unidad findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    Unidad returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Unidad} con el acrónimo indicado.
   * 
   * @param acronimo Acrónimo de {@link Unidad}.
   * @return {@link Unidad} correspondiente al acrónimo.
   */
  @GetMapping("/acronimo/{acronimo}")
  // @PreAuthorize("hasAuthorityForAnyUO('USR-UNI-V')")
  Unidad findByAcronimo(@PathVariable String acronimo) {
    log.debug("findByAcronimo(String id) - start");
    Unidad returnValue = service.findByAcronimo(acronimo);
    log.debug("findByAcronimo(String id) - end");
    return returnValue;
  }

  /**
   * Crea un nuevo {@link Unidad}.
   * 
   * @param unidad {@link Unidad} que se quiere crear.
   * @return Nuevo {@link Unidad} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('USR-UNI-C')")
  ResponseEntity<Unidad> create(@Valid @RequestBody Unidad unidad) {
    log.debug("create(Unidad unidad) - start");
    Unidad returnValue = service.create(unidad);
    log.debug("create(Unidad unidad) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link Unidad} con el id indicado.
   * 
   * @param unidad {@link Unidad} a actualizar.
   * @param id     id {@link Unidad} a actualizar.
   * @return {@link Unidad} actualizado.
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('USR-UNI-E')")
  Unidad update(@Validated({ Update.class, Default.class }) @RequestBody Unidad unidad, @PathVariable Long id) {
    log.debug("update(Unidad unidad, Long id) - start");
    unidad.setId(id);
    Unidad returnValue = service.update(unidad);
    log.debug("update(Unidad unidad, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link Unidad} con id indicado.
   * 
   * @param id Identificador de {@link Unidad}.
   */
  @DeleteMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('USR-UNI-B')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.disable(id);
    log.debug("deleteById(Long id) - end");
  }

}
