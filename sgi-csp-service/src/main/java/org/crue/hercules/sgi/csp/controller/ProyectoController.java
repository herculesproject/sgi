package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadGestora;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.service.EstadoProyectoService;
import org.crue.hercules.sgi.csp.service.ProyectoDocumentoService;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadFinanciadoraService;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadGestoraService;
import org.crue.hercules.sgi.csp.service.ProyectoEquipoService;
import org.crue.hercules.sgi.csp.service.ProyectoFaseService;
import org.crue.hercules.sgi.csp.service.ProyectoHitoService;
import org.crue.hercules.sgi.csp.service.ProyectoPaqueteTrabajoService;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoSeguimientoService;
import org.crue.hercules.sgi.csp.service.ProyectoProrrogaService;
import org.crue.hercules.sgi.csp.service.ProyectoService;
import org.crue.hercules.sgi.csp.service.ProyectoSocioService;
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
 * ProyectoController
 */
@RestController
@RequestMapping(ProyectoController.REQUEST_MAPPING)
@Slf4j
public class ProyectoController {

  /** El path que gestiona este controlador */
  public static final String REQUEST_MAPPING = "/proyectos";

  /** Proyecto service */
  private final ProyectoService service;

  /** ProyectoHito service */
  private final ProyectoHitoService proyectoHitoService;

  /** ProyectoFaseService */
  private final ProyectoFaseService proyectoFaseService;

  /** ProyectoPaqueteTrabajo service */
  private final ProyectoPaqueteTrabajoService proyectoPaqueteTrabajoService;

  /** ProyectoSocio service */
  private final ProyectoSocioService proyectoSocioService;

  /** ProyectoPeriodoSeguimiento service */
  private final ProyectoPeriodoSeguimientoService proyectoPeriodoSeguimientoService;

  /** ConvocatoriaEntidadFinanciadora service */
  /** ProyectoEntidadFinanciadoraService service */
  private final ProyectoEntidadFinanciadoraService proyectoEntidadFinanciadoraService;

  /** ProyectoDocumentoService service */
  private final ProyectoDocumentoService proyectoDocumentoService;

  /** ProyectoEntidadGestoraService */
  private final ProyectoEntidadGestoraService proyectoEntidadGestoraService;

  /** ProyectoEquipo service */
  private final ProyectoEquipoService proyectoEquipoService;

  /** ProyectoProrrogaservice */
  private final ProyectoProrrogaService proyectoProrrogaService;

  /** EstadoProyecto service */
  private final EstadoProyectoService estadoProyectoService;

  /**
   * Instancia un nuevo ProyectoController.
   * 
   * @param proyectoService                    {@link ProyectoService}.
   * @param proyectoHitoService                {@link ProyectoHitoService}.
   * @param proyectoFaseService                {@link ProyectoFaseService}.
   * @param proyectoPaqueteTrabajoService      {@link ProyectoPaqueteTrabajoService}.
   * @param proyectoSocioService               {@link ProyectoSocioService}.
   * @param proyectoEntidadFinanciadoraService {@link ProyectoEntidadFinanciadoraService}.
   * @param proyectoPeriodoSeguimientoService  {@link ProyectoPeriodoSeguimientoService}
   * @param proyectoEntidadGestoraService      {@link ProyectoEntidadGestoraService}
   * @param proyectoEquipoService              {@link ProyectoEquipoService}.
   * @param proyectoProrrogaService            {@link ProyectoProrrogaService}.
   * @param estadoProyectoService              {@link EstadoProyectoService}.
   * @param proyectoProrrogaService            {@link ProyectoProrrogaService}.
   * @param proyectoDocumentoService           {@link ProyectoDocumentoService}.
   * @param proyectoDocumentoService           {@link ProyectoDocumentoService}.
   */
  public ProyectoController(ProyectoService proyectoService, ProyectoHitoService proyectoHitoService,
      ProyectoFaseService proyectoFaseService, ProyectoPaqueteTrabajoService proyectoPaqueteTrabajoService,
      ProyectoEquipoService proyectoEquipoService, ProyectoSocioService proyectoSocioService,
      ProyectoEntidadFinanciadoraService proyectoEntidadFinanciadoraService,
      ProyectoPeriodoSeguimientoService proyectoPeriodoSeguimientoService,
      ProyectoProrrogaService proyectoProrrogaService, ProyectoEntidadGestoraService proyectoEntidadGestoraService,
      ProyectoDocumentoService proyectoDocumentoService, EstadoProyectoService estadoProyectoService) {
    this.service = proyectoService;
    this.proyectoHitoService = proyectoHitoService;
    this.proyectoFaseService = proyectoFaseService;
    this.proyectoPaqueteTrabajoService = proyectoPaqueteTrabajoService;
    this.proyectoSocioService = proyectoSocioService;
    this.proyectoEntidadFinanciadoraService = proyectoEntidadFinanciadoraService;
    this.proyectoDocumentoService = proyectoDocumentoService;
    this.proyectoPeriodoSeguimientoService = proyectoPeriodoSeguimientoService;
    this.proyectoEntidadGestoraService = proyectoEntidadGestoraService;
    this.proyectoEquipoService = proyectoEquipoService;
    this.proyectoProrrogaService = proyectoProrrogaService;
    this.estadoProyectoService = estadoProyectoService;
  }

  /**
   * Crea nuevo {@link Proyecto}
   * 
   * @param proyecto {@link Proyecto} que se quiere crear.
   * @return Nuevo {@link Proyecto} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-C')")
  public ResponseEntity<Proyecto> create(@Valid @RequestBody Proyecto proyecto) {
    log.debug("create(Proyecto proyecto) - start");

    Proyecto returnValue = service.create(proyecto);
    log.debug("create(Proyecto proyecto) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Proyecto}.
   * 
   * @param proyecto {@link Proyecto} a actualizar.
   * @param id       Identificador {@link Proyecto} a actualizar.
   * @return Proyecto {@link Proyecto} actualizado
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public Proyecto update(@Valid @RequestBody Proyecto proyecto, @PathVariable Long id) {
    log.debug("update(Proyecto proyecto, Long id) - start");

    proyecto.setId(id);
    Proyecto returnValue = service.update(proyecto);
    log.debug("update(Proyecto proyecto, Long id) - end");
    return returnValue;
  }

  /**
   * Reactiva el {@link Proyecto} con id indicado.
   * 
   * @param id Identificador de {@link Proyecto}.
   * @return {@link Proyecto} actualizado.
   */
  @PatchMapping("/{id}/reactivar")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  Proyecto reactivar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    Proyecto returnValue = service.enable(id);
    log.debug("reactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva la {@link Proyecto} con id indicado.
   * 
   * @param id Identificador de {@link Proyecto}.
   * @return {@link Proyecto} actualizado.
   */
  @PatchMapping("/{id}/desactivar")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-B')")
  Proyecto desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");

    Proyecto returnValue = service.disable(id);
    log.debug("desactivar(Long id) - end");
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
   * @param id Identificador de {@link Proyecto}.
   * @return Proyecto {@link Proyecto} correspondiente al id
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-V')")
  Proyecto findById(@PathVariable Long id) {
    log.debug("Proyecto findById(Long id) - start");

    Proyecto returnValue = service.findById(id);
    log.debug("Proyecto findById(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Proyecto} activas que se
   * encuentren dentro de la unidad de gestión del usuario logueado
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Proyecto} activas paginadas y
   *         filtradas.
   */
  @GetMapping()
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-V')")
  ResponseEntity<Page<Proyecto>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");

    Page<Proyecto> page = service.findAllRestringidos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Proyecto} que se encuentren
   * dentro de la unidad de gestión del usuario logueado
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Proyecto} activas paginadas y
   *         filtradas.
   */
  @GetMapping("/todos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-V')")
  ResponseEntity<Page<Proyecto>> findAllTodos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");

    Page<Proyecto> page = service.findAllTodosRestringidos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllTodos(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * PROYECTO HITO
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoHito} del
   * {@link Proyecto}.
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectohitos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-THIT-V')")
  ResponseEntity<Page<ProyectoHito>> findAllProyectoHito(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoHito(Long id, String query, Pageable paging) - start");
    Page<ProyectoHito> page = proyectoHitoService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoHito(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoHito(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * PROYECTO FASE
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoFase} del
   * {@link Proyecto}.
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectofases")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-THIT-V')")
  ResponseEntity<Page<ProyectoFase>> findAllProyectoFase(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoFase(Long id, String query, Pageable paging) - start");
    Page<ProyectoFase> page = proyectoFaseService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoFase(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoFase(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * PROYECTO PAQUETE TRABAJO
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoPaqueteTrabajo} del
   * {@link Proyecto}.
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectopaquetetrabajos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-V')")
  ResponseEntity<Page<ProyectoPaqueteTrabajo>> findAllProyectoPaqueteTrabajo(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoPaqueteTrabajo(Long id, String query, Pageable paging) - start");
    Page<ProyectoPaqueteTrabajo> page = proyectoPaqueteTrabajoService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoPaqueteTrabajo(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoPaqueteTrabajo(Long id, String query, Pageable paging) - end");
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
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectosocios")
  // @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V')")
  ResponseEntity<Page<ProyectoSocio>> findAllProyectoSocio(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoSocio(Long id, String query, Pageable paging) - start");
    Page<ProyectoSocio> page = proyectoSocioService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoSocio(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoSocio(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * PROYECTO ENTIDAD FINANCIADORA
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoEntidadFinanciadora}
   * del {@link Proyecto}.
   * 
   * @param id     Identificador del {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectoentidadfinanciadoras")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CATEM-V')")
  ResponseEntity<Page<ProyectoEntidadFinanciadora>> findAllProyectoEntidadFinanciadora(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoEntidadFinanciadora(Long id, String query, Pageable paging) - start");
    Page<ProyectoEntidadFinanciadora> page = proyectoEntidadFinanciadoraService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoEntidadFinanciadora(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoEntidadFinanciadora(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);

  }

  /**
   * 
   * PROYECTO DOCUMENTOS
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoDocumento} del
   * {@link Proyecto}.
   * 
   * @param id Identificador del {@link Proyecto}.
   */
  @GetMapping("/{id}/documentos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CATEM-V')")
  ResponseEntity<Page<ProyectoDocumento>> findAllDocumentos(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllDocumentos(Long id String query, Pageable paging) - start");

    Page<ProyectoDocumento> page = proyectoDocumentoService.findAllByProyectoId(id, query, paging);
    if (page.isEmpty()) {
      log.debug("findAllDocumentos(Long id String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllDocumentos(Long id String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);

  }

  /**
   * 
   * PROYECTO PERIODO SEGUIMIENTO
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoPeriodoSeguimiento}
   * de la {@link Convocatoria}.
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectoperiodoseguimientos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-V')")
  ResponseEntity<Page<ProyectoPeriodoSeguimiento>> findAllProyectoPeriodoSeguimiento(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoPeriodoSeguimiento(Long id, String query, Pageable paging) - start");
    Page<ProyectoPeriodoSeguimiento> page = proyectoPeriodoSeguimientoService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoPeriodoSeguimiento(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoPeriodoSeguimiento(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * PROYECTO ENTIDAD GESTORA
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoEntidadGestora} del
   * {@link Proyecto}.
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectoentidadgestoras")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-V')")
  ResponseEntity<Page<ProyectoEntidadGestora>> findAllProyectoEntidadGestora(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoEntidadGestora(Long id, String query, Pageable paging) - start");
    Page<ProyectoEntidadGestora> page = proyectoEntidadGestoraService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoEntidadGestora(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoEntidadGestora(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * PROYECTO EQUIPO
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoEquipo} del
   * {@link Proyecto}.
   * 
   * @param id     Identificador del {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectoequipos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CATEM-V')")
  ResponseEntity<Page<ProyectoEquipo>> findAllProyectoEquipo(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoEquipo(Long id, String query, Pageable paging) - start");
    Page<ProyectoEquipo> page = proyectoEquipoService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoEquipo(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoEquipo(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);

  }

  /**
   * 
   * PROYECTO PRÓRROGA
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ProyectoProrroga} del
   * {@link Proyecto}.
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/{id}/proyectoprorrogas")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-V')")
  ResponseEntity<Page<ProyectoProrroga>> findAllProyectoProrroga(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoProrroga(Long id, String query, Pageable paging) - start");
    Page<ProyectoProrroga> page = proyectoProrrogaService.findAllByProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoProrroga(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoProrroga(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * PROYECTO ESTADO
   * 
   */

  /**
   * Devuelve una lista de EstadoProyecto paginada y filtrada de {@link Proyecto}.
   * 
   * @param id     Identificador de {@link Proyecto}.
   * @param paging pageable.
   */

  @GetMapping("/{id}/estadoproyectos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-V')")
  ResponseEntity<Page<EstadoProyecto>> findAllEstadoProyecto(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllEstadoProyecto(Long id, Pageable paging) - start");
    Page<EstadoProyecto> page = estadoProyectoService.findAllByProyecto(id, paging);

    if (page.isEmpty()) {
      log.debug("findAllEstadoProyecto(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllEstadoProyecto(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);

  }

  /**
   * 
   * SOLICITUD
   * 
   */

  /**
   * Crea nuevo {@link Proyecto} a partir de los datos de una {@link Solicitud}
   * 
   * @param id       identificador de la {@link Solicitud}
   * @param proyecto {@link Proyecto} a crear
   * @return Nuevo {@link Proyecto} creado.
   */
  @PostMapping("/{id}/solicitud")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-C')")
  public ResponseEntity<Proyecto> createProyectoBySolicitud(@PathVariable Long id, @RequestBody Proyecto proyecto) {
    log.debug("createProyectoBySolicitud(@PathVariable Long id) - start");

    Proyecto returnValue = service.createProyectoBySolicitud(id, proyecto);
    log.debug("createProyectoBySolicitud(@PathVariable Long id) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

}
