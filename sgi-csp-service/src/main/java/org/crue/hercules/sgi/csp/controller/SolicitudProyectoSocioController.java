package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoPago;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEquipoSocioService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPeriodoJustificacionService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPeriodoPagoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioService;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * SolicitudProyectoSocioController
 */
@RestController
@RequestMapping("/solicitudproyectosocio")
@Slf4j
public class SolicitudProyectoSocioController {

  /** SolicitudProyectoSocioService service */
  private final SolicitudProyectoSocioService service;

  /** SolicitudProyectoPeriodoPagoService service */
  private final SolicitudProyectoPeriodoPagoService solicitudProyectoPeriodoPagoService;
  /** SolicitudProyectoPeriodoJustificacionService service */
  private final SolicitudProyectoPeriodoJustificacionService solicitudProyectoPeriodoJustificacionService;

  /** SolicitudProyectoEquipoSocio service */
  private final SolicitudProyectoEquipoSocioService solicitudProyectoEquipoSocioService;

  /**
   * Instancia un nuevo SolicitudProyectoSocioController.
   * 
   * @param solicitudProyectoSocioService                {@link SolicitudProyectoSocioService}.
   * @param solicitudProyectoPeriodoPagoService          {@link SolicitudProyectoPeriodoPagoService}.
   * @param solicitudProyectoEquipoSocioService          {@link SolicitudProyectoEquipoSocioService}.
   * @param solicitudProyectoPeriodoJustificacionService {@link SolicitudProyectoPeriodoJustificacionService}.
   */
  public SolicitudProyectoSocioController(SolicitudProyectoSocioService solicitudProyectoSocioService,
      SolicitudProyectoPeriodoJustificacionService solicitudProyectoPeriodoJustificacionService,
      SolicitudProyectoPeriodoPagoService solicitudProyectoPeriodoPagoService,
      SolicitudProyectoEquipoSocioService solicitudProyectoEquipoSocioService) {
    this.service = solicitudProyectoSocioService;
    this.solicitudProyectoPeriodoPagoService = solicitudProyectoPeriodoPagoService;
    this.solicitudProyectoEquipoSocioService = solicitudProyectoEquipoSocioService;
    this.solicitudProyectoPeriodoJustificacionService = solicitudProyectoPeriodoJustificacionService;

  }

  /**
   * Crea nuevo {@link SolicitudProyectoSocio}
   * 
   * @param solicitudProyectoSocio {@link SolicitudProyectoSocio}. que se quiere
   *                               crear.
   * @return Nuevo {@link SolicitudProyectoSocio} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-C')")
  public ResponseEntity<SolicitudProyectoSocio> create(
      @Valid @RequestBody SolicitudProyectoSocio solicitudProyectoSocio) {
    log.debug("create(SolicitudProyectoSocio solicitudProyectoSocio) - start");
    SolicitudProyectoSocio returnValue = service.create(solicitudProyectoSocio);
    log.debug("create(SolicitudProyectoSocio solicitudProyectoSocio) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link SolicitudProyectoSocio}.
   * 
   * @param solicitudProyectoSocio {@link SolicitudProyectoSocio} a actualizar.
   * @param id                     Identificador {@link SolicitudProyectoSocio} a
   *                               actualizar.
   * @return SolicitudProyectoSocio {@link SolicitudProyectoSocio} actualizado
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-E')")
  public SolicitudProyectoSocio update(@Valid @RequestBody SolicitudProyectoSocio solicitudProyectoSocio,
      @PathVariable Long id) {
    log.debug("update(SolicitudProyectoSocio solicitudProyectoSocio, Long id) - start");

    solicitudProyectoSocio.setId(id);
    SolicitudProyectoSocio returnValue = service.update(solicitudProyectoSocio);
    log.debug("update(SolicitudProyectoSocio solicitudProyectoSocio, Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link SolicitudProyectoSocio} con el id
   * indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoSocio}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-V')")
  public ResponseEntity<?> exists(@PathVariable Long id) {
    log.debug("SolicitudProyectoSocio exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("SolicitudProyectoSocio exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("SolicitudProyectoSocio exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el {@link SolicitudProyectoSocio} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoSocio}.
   * @return SolicitudProyectoSocio {@link SolicitudProyectoSocio} correspondiente
   *         al id
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-V')")
  SolicitudProyectoSocio findById(@PathVariable Long id) {
    log.debug("SolicitudProyectoSocio findById(Long id) - start");
    SolicitudProyectoSocio returnValue = service.findById(id);
    log.debug("SolicitudProyectoSocio findById(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link SolicitudProyectoSocio} con id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoSocio}.
   */
  @DeleteMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CENTGES-B')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve una lista paginada de {@link SolicitudProyectoPeriodoPago}
   * 
   * @param id     Identificador de {@link SolicitudProyectoPeriodoPago}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudproyectoperiodopago")
  // @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-C', 'CSP-SOL-E')")
  ResponseEntity<Page<SolicitudProyectoPeriodoPago>> findAllSolicitudProyectoPeriodoPago(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudProyectoPeriodoPago(Long id, String query, Pageable paging) - start");
    Page<SolicitudProyectoPeriodoPago> page = solicitudProyectoPeriodoPagoService.findAllBySolicitudProyectoSocio(id,
        query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudProyectoPeriodoPago(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudProyectoPeriodoPago(Long id, String query, Pageable paging) - end");

    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /*
   * Devuelve una lista paginada de {@link SolicitudProyectoEquipoSocio}
   * 
   * @param id Identificador de {@link SolicitudProyectoEquipoSocio}.
   * 
   * @param query filtro de búsqueda.
   * 
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudproyectoequiposocio")
  // @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-C', 'CSP-SOL-E')")
  ResponseEntity<Page<SolicitudProyectoEquipoSocio>> findAllSolicitudProyectoEquipoSocio(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudProyectoEquipoSocio(Long id, String query, Pageable paging) - start");
    Page<SolicitudProyectoEquipoSocio> page = solicitudProyectoEquipoSocioService.findAllBySolicitudProyectoSocio(id,
        query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudProyectoEquipoSocio(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudProyectoEquipoSocio(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /*
   * Devuelve una lista paginada de {@link SolicitudProyectoPeriodoJustificacion}
   * 
   * @param id Identificador de {@link SolicitudProyectoPeriodoJustificacion}.
   * 
   * @param query filtro de búsqueda.
   * 
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudproyectoperiodojustificaciones")
  // @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-C', 'CSP-SOL-E')")
  ResponseEntity<Page<SolicitudProyectoPeriodoJustificacion>> findAllSolicitudProyectoPeriodoJustificacion(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudProyectoPeriodoJustificacion(Long id, String query, Pageable paging) - start");
    Page<SolicitudProyectoPeriodoJustificacion> page = solicitudProyectoPeriodoJustificacionService
        .findAllBySolicitudProyectoSocio(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudProyectoPeriodoJustificacion(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudProyectoPeriodoJustificacion(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Indica si {@link SolicitudProyectoSocio} tiene
   * {@link SolicitudProyectoPeriodoJustificacion},
   * {@link SolicitudProyectoPeriodoPago} y/o {@link SolicitudProyectoEquipoSocio}
   * relacionadas.
   *
   * @param id Id de la {@link SolicitudProyectoSocio}.
   * @return True si tiene {@link SolicitudProyectoPeriodoJustificacion},
   *         {@link SolicitudProyectoPeriodoPago} y/o
   *         {@link SolicitudProyectoEquipoSocio} relacionadas. En caso contrario
   *         false
   */
  @RequestMapping(path = "/{id}/vinculaciones", method = RequestMethod.HEAD)
  ResponseEntity<Boolean> vinculaciones(@PathVariable Long id) {
    log.debug("vinculaciones(Long id) - start");
    Boolean returnValue = service.vinculaciones(id);
    log.debug("vinculaciones(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}