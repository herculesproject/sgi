package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.service.RolProyectoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * RolProyectoController
 */
@RestController
@RequestMapping("/rolproyectos")
@Slf4j
public class RolProyectoController {

  /** RolProyectoService service */
  private final RolProyectoService service;

  /**
   * Instancia un nuevo RolProyectoController.
   * 
   * @param rolProyectoService {@link RolProyectoService}.
   */
  public RolProyectoController(RolProyectoService rolProyectoService) {
    this.service = rolProyectoService;
  }

  /**
   * Crea nuevo {@link RolProyecto}
   * 
   * @param rolProyecto {@link RolProyecto}. que se quiere crear.
   * @return Nuevo {@link RolProyecto} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RPRO-C')")
  public ResponseEntity<RolProyecto> create(@Valid @RequestBody RolProyecto rolProyecto) {
    log.debug("create(RolProyecto rolProyecto) - start");
    RolProyecto returnValue = service.create(rolProyecto);
    log.debug("create(RolProyecto rolProyecto) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link RolProyecto}.
   * 
   * @param rolProyecto {@link RolProyecto} a actualizar.
   * @param id          Identificador {@link RolProyecto} a actualizar.
   * @return RolProyecto {@link RolProyecto} actualizado
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RPRO-E')")
  public RolProyecto update(@Valid @RequestBody RolProyecto rolProyecto, @PathVariable Long id) {
    log.debug("update(RolProyecto rolProyecto, Long id) - start");
    rolProyecto.setId(id);
    RolProyecto returnValue = service.update(rolProyecto);
    log.debug("update(RolProyecto rolProyecto, Long id) - end");
    return returnValue;
  }

  /**
   * Reactiva el {@link RolProyecto} con id indicado.
   * 
   * @param id Identificador de {@link RolProyecto}.
   * @return {@link RolProyecto} actualizado.
   */
  @PatchMapping("/{id}/reactivar")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RPRO-R')")
  public RolProyecto reactivar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    RolProyecto returnValue = service.enable(id);
    log.debug("reactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link RolProyecto} con id indicado.
   * 
   * @param id Identificador de {@link RolProyecto}.
   * @return {@link RolProyecto} actualizado.
   */
  @PatchMapping("/{id}/desactivar")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RPRO-B')")
  public RolProyecto desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    RolProyecto returnValue = service.disable(id);
    log.debug("desactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link RolProyecto} con el id indicado.
   * 
   * @param id Identificador de {@link RolProyecto}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RPRO-V')")
  public ResponseEntity<?> exists(@PathVariable Long id) {
    log.debug("RolProyecto exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("RolProyecto exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("RolProyecto exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el {@link RolProyecto} con el id indicado.
   * 
   * @param id Identificador de {@link RolProyecto}.
   * @return RolProyecto {@link RolProyecto} correspondiente al id
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RPRO-V')")
  RolProyecto findById(@PathVariable Long id) {
    log.debug("RolProyecto findById(Long id) - start");
    RolProyecto returnValue = service.findById(id);
    log.debug("RolProyecto findById(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link RolProyecto} activas.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link RolProyecto} activas paginadas y
   *         filtradas.
   */
  @GetMapping()
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RPRO-V')")
  ResponseEntity<Page<RolProyecto>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<RolProyecto> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link RolProyecto}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link RolProyecto} paginadas y filtradas.
   */
  @GetMapping("/todos")
  // @PreAuthorize("hasAuthorityForAnyUO('SYSADMIN')")
  ResponseEntity<Page<RolProyecto>> findAllTodos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(String query,Pageable paging) - start");
    Page<RolProyecto> page = service.findAllTodos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllTodos(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}
