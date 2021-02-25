package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.service.RolSocioService;
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
 * RolSocioController
 */
@RestController
@RequestMapping("/rolsocios")
@Slf4j
public class RolSocioController {

  /** RolSocioService service */
  private final RolSocioService service;

  /**
   * Instancia un nuevo RolSocioController.
   * 
   * @param rolSocioService {@link RolSocioService}.
   */
  public RolSocioController(RolSocioService rolSocioService) {
    this.service = rolSocioService;
  }

  /**
   * Crea nuevo {@link RolSocio}
   * 
   * @param rolSocio {@link RolSocio}. que se quiere crear.
   * @return Nuevo {@link RolSocio} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-C')")
  public ResponseEntity<RolSocio> create(@Valid @RequestBody RolSocio rolSocio) {
    log.debug("create(RolSocio rolSocio) - start");
    RolSocio returnValue = service.create(rolSocio);
    log.debug("create(RolSocio rolSocio) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link RolSocio}.
   * 
   * @param rolSocio {@link RolSocio} a actualizar.
   * @param id       Identificador {@link RolSocio} a actualizar.
   * @return RolSocio {@link RolSocio} actualizado
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-E')")
  public RolSocio update(@Valid @RequestBody RolSocio rolSocio, @PathVariable Long id) {
    log.debug("update(RolSocio rolSocio, Long id) - start");
    rolSocio.setId(id);
    RolSocio returnValue = service.update(rolSocio);
    log.debug("update(RolSocio rolSocio, Long id) - end");
    return returnValue;
  }

  /**
   * Reactiva el {@link RolSocio} con id indicado.
   * 
   * @param id Identificador de {@link RolSocio}.
   * @return {@link RolSocio} actualizado.
   */
  @PatchMapping("/{id}/reactivar")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-R')")
  public RolSocio reactivar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    RolSocio returnValue = service.enable(id);
    log.debug("reactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link RolSocio} con id indicado.
   * 
   * @param id Identificador de {@link RolSocio}.
   * @return {@link RolSocio} actualizado.
   */
  @PatchMapping("/{id}/desactivar")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-B')")
  public RolSocio desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    RolSocio returnValue = service.disable(id);
    log.debug("desactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link RolSocio} con el id indicado.
   * 
   * @param id Identificador de {@link RolSocio}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-V')")
  public ResponseEntity<?> exists(@PathVariable Long id) {
    log.debug("RolSocio exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("RolSocio exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("RolSocio exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el {@link RolSocio} con el id indicado.
   * 
   * @param id Identificador de {@link RolSocio}.
   * @return RolSocio {@link RolSocio} correspondiente al id
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-V')")
  RolSocio findById(@PathVariable Long id) {
    log.debug("RolSocio findById(Long id) - start");
    RolSocio returnValue = service.findById(id);
    log.debug("RolSocio findById(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link RolSocio} activas.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link RolSocio} activas paginadas y
   *         filtradas.
   */
  @GetMapping()
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-V')")
  ResponseEntity<Page<RolSocio>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<RolSocio> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link RolSocio}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link RolSocio} paginadas y filtradas.
   */
  @GetMapping("/todos")
  // @PreAuthorize("hasAuthorityForAnyUO('SYSADMIN')")
  ResponseEntity<Page<RolSocio>> findAllTodos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(String query,Pageable paging) - start");
    Page<RolSocio> page = service.findAllTodos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllTodos(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}
