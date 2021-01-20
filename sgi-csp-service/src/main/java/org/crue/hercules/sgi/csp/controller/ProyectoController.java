package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.service.ProyectoHitoService;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.service.ProyectoService;
import org.crue.hercules.sgi.csp.service.ProyectoSocioService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoController
 */
@RestController
@RequestMapping("/proyectos")
@Slf4j
public class ProyectoController {

  /** Proyecto service */
  private final ProyectoService service;

  /** ProyectoHitoservice */
  private final ProyectoHitoService proyectoHitoService;

  /** ProyectoSocio service */
  private final ProyectoSocioService proyectoSocioService;

  /**
   * Instancia un nuevo ProyectoController.
   * 
   * @param proyectoService      {@link ProyectoService}.
   * @param proyectoHitoService  {@link ProyectoHitoService}.
   * @param proyectoSocioService {@link ProyectoSocioService}.
   */
  public ProyectoController(ProyectoService proyectoService, ProyectoHitoService proyectoHitoService,
      ProyectoSocioService proyectoSocioService) {
    this.service = proyectoService;
    this.proyectoHitoService = proyectoHitoService;
    this.proyectoSocioService = proyectoSocioService;
  }

  /**
   * Crea nuevo {@link Proyecto}
   * 
   * @param proyecto       {@link Proyecto} que se quiere crear.
   * @param authentication {@link Authentication}.
   * @return Nuevo {@link Proyecto} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-C')")
  public ResponseEntity<Proyecto> create(@Valid @RequestBody Proyecto proyecto, Authentication authentication) {
    log.debug("create(Proyecto proyecto, Authentication authentication) - start");

    List<String> acronimosUnidadGestion = authentication.getAuthorities().stream().map(acronimo -> {
      if (acronimo.getAuthority().indexOf("_") > 0) {
        return acronimo.getAuthority().split("_")[1];
      }
      return null;
    }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

    Proyecto returnValue = service.create(proyecto, acronimosUnidadGestion);
    log.debug("create(Proyecto proyecto, Authentication authentication) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Proyecto}.
   * 
   * @param proyecto       {@link Proyecto} a actualizar.
   * @param id             Identificador {@link Proyecto} a actualizar.
   * @param authentication {@link Authentication}.
   * @return Proyecto {@link Proyecto} actualizado
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public Proyecto update(@Valid @RequestBody Proyecto proyecto, @PathVariable Long id, Authentication authentication) {
    log.debug("update(Proyecto proyecto, Long id, Authentication authentication) - start");

    List<String> unidadGestionRefs = authentication.getAuthorities().stream().map(authority -> {
      if (authority.getAuthority().indexOf("_") > 0) {

        return authority.getAuthority().split("_")[1];
      }
      return null;
    }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

    proyecto.setId(id);
    Proyecto returnValue = service.update(proyecto, unidadGestionRefs);
    log.debug("update(Proyecto proyecto, Long id, Authentication authentication) - end");
    return returnValue;
  }

  /**
   * Reactiva el {@link Proyecto} con id indicado.
   * 
   * @param id             Identificador de {@link Proyecto}.
   * @param authentication {@link Authentication}.
   * @return {@link Proyecto} actualizado.
   */
  @PatchMapping("/{id}/reactivar")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  Proyecto reactivar(@PathVariable Long id, Authentication authentication) {
    log.debug("reactivar(Long id, Authentication authentication) - start");
    List<String> unidadGestionRefs = authentication.getAuthorities().stream().map(authority -> {
      if (authority.getAuthority().indexOf("_") > 0) {
        return authority.getAuthority().split("_")[1];
      }
      return null;
    }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

    Proyecto returnValue = service.enable(id, unidadGestionRefs);
    log.debug("reactivar(Long id, Authentication authentication) - end");
    return returnValue;
  }

  /**
   * Desactiva la {@link Proyecto} con id indicado.
   * 
   * @param id             Identificador de {@link Proyecto}.
   * @param authentication {@link Authentication}.
   * @return {@link Proyecto} actualizado.
   */
  @PatchMapping("/{id}/desactivar")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-B')")
  Proyecto desactivar(@PathVariable Long id, Authentication authentication) {
    log.debug("desactivar(Long id, Authentication authentication) - start");
    List<String> unidadGestionRefs = authentication.getAuthorities().stream().map(authority -> {
      if (authority.getAuthority().indexOf("_") > 0) {
        return authority.getAuthority().split("_")[1];
      }
      return null;
    }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

    Proyecto returnValue = service.disable(id, unidadGestionRefs);
    log.debug("desactivar(Long id, Authentication authentication) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link Proyecto} con el id indicado.
   * 
   * @param id Identificador de {@link Proyecto}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-V')")
  public ResponseEntity<?> exists(@PathVariable Long id) {
    log.debug("Proyecto exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("Proyecto exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("Proyecto exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Obtiene el {@link ModeloEjecucion} asignada al {@link Proyecto}.
   * 
   * @param id Id del {@link Proyecto}.
   * @return {@link ModeloEjecucion} asignado
   */
  @GetMapping("/{id}/modeloejecucion")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-V')")
  public ModeloEjecucion getModeloEjecucion(@PathVariable Long id) {
    log.debug("getModeloEjecucion(Long id) - start");
    ModeloEjecucion returnValue = service.getModeloEjecucion(id);
    log.debug("getModeloEjecucion(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Proyecto} con el id indicado.
   * 
   * @param id             Identificador de {@link Proyecto}.
   * @param authentication {@link Authentication}.
   * @return Proyecto {@link Proyecto} correspondiente al id
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-V')")
  Proyecto findById(@PathVariable Long id, Authentication authentication) {
    log.debug("Proyecto findById(Long id, Authentication authentication) - start");
    List<String> unidadGestionRefs = authentication.getAuthorities().stream().map(authority -> {
      if (authority.getAuthority().indexOf("_") > 0) {
        return authority.getAuthority().split("_")[1];
      }
      return null;
    }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

    Proyecto returnValue = service.findById(id, unidadGestionRefs);
    log.debug("Proyecto findById(Long id, Authentication authentication) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Proyecto} activas que se
   * encuentren dentro de la unidad de gestión del usuario logueado
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Proyecto} activas paginadas y
   *         filtradas.
   */
  @GetMapping()
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-V')")
  ResponseEntity<Page<Proyecto>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging, Authentication authentication) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging, Authentication authentication) - start");

    List<String> unidadGestionRefs = authentication.getAuthorities().stream().map(authority -> {
      if (authority.getAuthority().indexOf("_") > 0) {
        return authority.getAuthority().split("_")[1];
      }
      return null;
    }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

    Page<Proyecto> page = service.findAllRestringidos(query, paging, unidadGestionRefs);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query, Pageable paging, Authentication authentication) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query, Pageable paging, Authentication authentication) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Proyecto} que se encuentren
   * dentro de la unidad de gestión del usuario logueado
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Proyecto} activas paginadas y
   *         filtradas.
   */
  @GetMapping("/todos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-V')")
  ResponseEntity<Page<Proyecto>> findAllTodos(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging, Authentication authentication) {
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging, Authentication authentication) - start");

    List<String> unidadGestionRefs = authentication.getAuthorities().stream().map(authority -> {
      if (authority.getAuthority().indexOf("_") > 0) {
        return authority.getAuthority().split("_")[1];
      }
      return null;
    }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

    Page<Proyecto> page = service.findAllTodosRestringidos(query, paging, unidadGestionRefs);

    if (page.isEmpty()) {
      log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging, Authentication authentication) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging, Authentication authentication) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * PROYECTO HITO
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoHito} de la
   * {@link Convocatoria}.
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectohitos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-THIT-V')")
  ResponseEntity<Page<ProyectoHito>> findAllProyectoHito(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoHito(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ProyectoHito> page = proyectoHitoService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoHito(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoHito(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * PROYECTO SOCIO
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoSocio} del
   * {@link Proyecto}.
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectosocios")
  // @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V')")
  ResponseEntity<Page<ProyectoSocio>> findAllProyectoSocio(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoSocio(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ProyectoSocio> page = proyectoSocioService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoSocio(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoSocio(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}
