package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.service.AutorizacionService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * AutorizacionController
 */
@RestController
@RequestMapping(AutorizacionController.REQUEST_MAPPING)
@Slf4j
public class AutorizacionController {

  /** El path que gestiona este controlador */
  public static final String REQUEST_MAPPING = "/autorizaciones";

  private final AutorizacionService service;

  public AutorizacionController(AutorizacionService service) {
    this.service = service;
  }

  /**
   * Crea nuevo {@link Autorizacion}
   * 
   * @param autorizacion {@link Autorizacion} que se quiere crear.
   * @return Nuevo {@link Autorizacion}creado.
   */

  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-INV-C')")
  public ResponseEntity<Autorizacion> create(@Valid @RequestBody Autorizacion autorizacion) {
    log.debug("create(Autorizacion autorizacion) - start");

    Autorizacion returnValue = service.create(autorizacion);

    log.debug("create(Autorizacion autorizacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Autorizacion}.
   * 
   * @param autorizacion {@link Autorizacion} a actualizar.
   * @param id           Identificador {@link Autorizacion} a actualizar.
   * @return Proyecto {@link Autorizacion} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-INV-ER')")
  public Autorizacion update(@Valid @RequestBody Autorizacion autorizacion, @PathVariable Long id) {
    log.debug("update(Autorizacion autorizacion, Long id) - start");

    autorizacion.setId(id);
    Autorizacion returnValue = service.update(autorizacion);
    log.debug("update(Autorizacion aroyecto, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Autorizacion} con el id indicado.
   * 
   * @param id Identificador de {@link Autorizacion}.
   * @return Autorizacion {@link Autorizacion} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-E','CSP-AUT-B','CSP-AUT-INV-C', 'CSP-AUT-INV-ER', 'CSP-AUT-INV-BR')")
  Autorizacion findById(@PathVariable Long id) {
    log.debug("Autorizacion findById(Long id) - start");
    Autorizacion returnValue = service.findById(id);
    log.debug("Autorizacion findById(Long id) - end");
    return returnValue;
  }

  /**
   * presenta una autorizacion".
   * 
   * @param id Identificador de {@link Autorizacion}.
   * @return {@link Autorizacion} actualizado.
   */
  @PatchMapping("/{id}/presentar")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-INV-C', 'CSP-AUT-INV-ER', 'CSP-AUT-INV-BR')")
  Autorizacion presentar(@PathVariable Long id) {
    log.debug("presentar(Long id)) - start");

    Autorizacion returnValue = service.presentar(id);

    log.debug("presentar(Long id) - end");
    return returnValue;
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Autorizacion}
   * puede pasar a estado 'Presentada'.
   *
   * @param id Id del {@link Autorizacion}.
   * @return HTTP-200 si puede ser presentada / HTTP-204 si no puede ser
   *         presentada
   */
  @RequestMapping(path = "/{id}/presentable", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-E','CSP-AUT-B','CSP-AUT-INV-C', 'CSP-AUT-INV-ER', 'CSP-AUT-INV-BR')")
  ResponseEntity<Autorizacion> presentable(@PathVariable Long id) {
    log.debug("presentable(Long id) - start");
    boolean returnValue = service.presentable(id);
    log.debug("presentable(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Autorizacion} activas.
   *
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Autorizacion} activas paginadas y
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-E','CSP-AUT-B','CSP-AUT-INV-C', 'CSP-AUT-INV-ER', 'CSP-AUT-INV-BR')")
  public ResponseEntity<Page<Autorizacion>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<Autorizacion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-AUT-B','CSP-AUT-INV-BR')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }
}
