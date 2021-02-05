package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.SolicitudProyectoPresupuestoTotalConceptoGasto;
import org.crue.hercules.sgi.csp.dto.SolicitudProyectoPresupuestoTotales;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.crue.hercules.sgi.csp.model.SolicitudHito;
import org.crue.hercules.sgi.csp.model.SolicitudModalidad;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.service.EstadoSolicitudService;
import org.crue.hercules.sgi.csp.service.SolicitudDocumentoService;
import org.crue.hercules.sgi.csp.service.SolicitudHitoService;
import org.crue.hercules.sgi.csp.service.SolicitudModalidadService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoDatosService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEntidadFinanciadoraAjenaService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEquipoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPresupuestoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioService;
import org.crue.hercules.sgi.csp.service.SolicitudService;
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
 * SolicitudController
 */
@RestController
@RequestMapping("/solicitudes")
@Slf4j
public class SolicitudController {

  /** Solicitud service */
  private final SolicitudService service;

  /** SolicitudModalidad service */
  private final SolicitudModalidadService solicitudModalidadService;

  /** EstadoSolicitud service */
  private final EstadoSolicitudService estadoSolicitudService;

  /** SolicitudDocumento service */
  private final SolicitudDocumentoService solicitudDocumentoService;

  /** SolicitudHito service */
  private final SolicitudHitoService solicitudHitoService;

  /** SolicitudProyectoDatosService service */
  private final SolicitudProyectoDatosService solicitudProyectoDatosService;

  /** SolicitudProyectoSocioService service */
  private final SolicitudProyectoSocioService solicitudProyectoSocioService;

  /** SolicitudProyectoEquipoService service */
  private final SolicitudProyectoEquipoService solicitudProyectoEquipoService;

  /** SolicitudProyectoEntidadFinanciadoraAjena service */
  private final SolicitudProyectoEntidadFinanciadoraAjenaService solicitudProyectoEntidadFinanciadoraAjenaService;

  /** SolicitudProyectoPresupuesto service */
  private final SolicitudProyectoPresupuestoService solicitudProyectoPresupuestoService;

  /**
   * Instancia un nuevo SolicitudController.
   * 
   * @param solicitudService                                 {@link SolicitudService}.
   * @param solicitudModalidadService                        {@link SolicitudModalidadService}.
   * @param solicitudDocumentoService                        {@link SolicitudDocumentoService}
   * @param estadoSolicitudService                           {@link EstadoSolicitudService}.
   * @param solicitudHitoService                             {@link SolicitudHitoService}.
   * @param solicitudProyectoDatosService                    {@link SolicitudProyectoDatosService}
   * @param solicitudProyectoSocioService                    {@link SolicitudProyectoSocioService}
   * @param solicitudProyectoEquipoService                   {@link SolicitudProyectoEquipoService}
   * @param solicitudProyectoEntidadFinanciadoraAjenaService {@link SolicitudProyectoEntidadFinanciadoraAjenaService}.
   * @param solicitudProyectoPresupuestoService              {@link SolicitudProyectoPresupuestoService}.
   */
  public SolicitudController(SolicitudService solicitudService, SolicitudModalidadService solicitudModalidadService,
      EstadoSolicitudService estadoSolicitudService, SolicitudDocumentoService solicitudDocumentoService,
      SolicitudHitoService solicitudHitoService, SolicitudProyectoDatosService solicitudProyectoDatosService,
      SolicitudProyectoSocioService solicitudProyectoSocioService,
      SolicitudProyectoEquipoService solicitudProyectoEquipoService,
      SolicitudProyectoEntidadFinanciadoraAjenaService solicitudProyectoEntidadFinanciadoraAjenaService,
      SolicitudProyectoPresupuestoService solicitudProyectoPresupuestoService) {
    this.service = solicitudService;
    this.solicitudModalidadService = solicitudModalidadService;
    this.estadoSolicitudService = estadoSolicitudService;
    this.solicitudDocumentoService = solicitudDocumentoService;
    this.solicitudHitoService = solicitudHitoService;
    this.solicitudProyectoDatosService = solicitudProyectoDatosService;
    this.solicitudProyectoSocioService = solicitudProyectoSocioService;
    this.solicitudProyectoEquipoService = solicitudProyectoEquipoService;
    this.solicitudProyectoEntidadFinanciadoraAjenaService = solicitudProyectoEntidadFinanciadoraAjenaService;
    this.solicitudProyectoPresupuestoService = solicitudProyectoPresupuestoService;
  }

  /**
   * Crea nuevo {@link Solicitud}
   * 
   * @param solicitud      {@link Solicitud} que se quiere crear.
   * @param authentication {@link Authentication}.
   * @return Nuevo {@link Solicitud} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-C')")
  public ResponseEntity<Solicitud> create(@Valid @RequestBody Solicitud solicitud, Authentication authentication) {
    log.debug("create(Solicitud solicitud) - start");
    List<String> unidadGestionRefs = authentication.getAuthorities().stream().map(authority -> {
      if (authority.getAuthority().indexOf("_") > 0) {
        return authority.getAuthority().split("_")[1];
      }
      return null;
    }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

    solicitud.setCreadorRef(authentication.getName());
    Solicitud returnValue = service.create(solicitud, unidadGestionRefs);
    log.debug("create(Solicitud solicitud) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Solicitud}.
   * 
   * @param solicitud      {@link Solicitud} a actualizar.
   * @param id             Identificador {@link Solicitud} a actualizar.
   * @param authentication {@link Authentication}.
   * @return Solicitud {@link Solicitud} actualizado
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public Solicitud update(@Valid @RequestBody Solicitud solicitud, @PathVariable Long id,
      Authentication authentication) {
    log.debug("update(Solicitud solicitud, Long id) - start");

    List<String> unidadGestionRefs = authentication.getAuthorities().stream().map(authority -> {
      if (authority.getAuthority().indexOf("_") > 0) {

        return authority.getAuthority().split("_")[1];
      }
      return null;
    }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

    solicitud.setId(id);
    Solicitud returnValue = service.update(solicitud, unidadGestionRefs);
    log.debug("update(Solicitud solicitud, Long id) - end");
    return returnValue;
  }

  /**
   * Reactiva el {@link Solicitud} con id indicado.
   * 
   * @param id             Identificador de {@link Solicitud}.
   * @param authentication {@link Authentication}.
   * @return {@link Solicitud} actualizado.
   */
  @PatchMapping("/{id}/reactivar")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  Solicitud reactivar(@PathVariable Long id, Authentication authentication) {
    log.debug("reactivar(Long id) - start");
    List<String> unidadGestionRefs = authentication.getAuthorities().stream().map(authority -> {
      if (authority.getAuthority().indexOf("_") > 0) {
        return authority.getAuthority().split("_")[1];
      }
      return null;
    }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

    Solicitud returnValue = service.enable(id, unidadGestionRefs);
    log.debug("reactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva la {@link Solicitud} con id indicado.
   * 
   * @param id             Identificador de {@link Solicitud}.
   * @param authentication {@link Authentication}.
   * @return {@link Solicitud} actualizado.
   */
  @PatchMapping("/{id}/desactivar")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-B')")
  Solicitud desactivar(@PathVariable Long id, Authentication authentication) {
    log.debug("desactivar(Long id) - start");
    List<String> unidadGestionRefs = authentication.getAuthorities().stream().map(authority -> {
      if (authority.getAuthority().indexOf("_") > 0) {
        return authority.getAuthority().split("_")[1];
      }
      return null;
    }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

    Solicitud returnValue = service.disable(id, unidadGestionRefs);
    log.debug("desactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Solicitud} con el id indicado.
   * 
   * @param id             Identificador de {@link Solicitud}.
   * @param authentication {@link Authentication}.
   * @return Solicitud {@link Solicitud} correspondiente al id
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-V')")
  Solicitud findById(@PathVariable Long id, Authentication authentication) {
    log.debug("Solicitud findById(Long id) - start");
    List<String> unidadGestionRefs = authentication.getAuthorities().stream().map(authority -> {
      if (authority.getAuthority().indexOf("_") > 0) {
        return authority.getAuthority().split("_")[1];
      }
      return null;
    }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

    Solicitud returnValue = service.findById(id, unidadGestionRefs);
    log.debug("Solicitud findById(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Solicitud} activas que se
   * encuentren dentro de la unidad de gestión del usuario logueado
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Solicitud} activas paginadas y
   *         filtradas.
   */
  @GetMapping()
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-V')")
  ResponseEntity<Page<Solicitud>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging, Authentication authentication) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - start");

    List<String> unidadGestionRefs = authentication.getAuthorities().stream().map(authority -> {
      if (authority.getAuthority().indexOf("_") > 0) {
        return authority.getAuthority().split("_")[1];
      }
      return null;
    }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

    Page<Solicitud> page = service.findAllRestringidos(query, paging, unidadGestionRefs);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Solicitud} que se encuentren
   * dentro de la unidad de gestión del usuario logueado
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Solicitud} activas paginadas y
   *         filtradas.
   */
  @GetMapping("/todos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-V')")
  ResponseEntity<Page<Solicitud>> findAllTodos(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging, Authentication authentication) {
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - start");

    List<String> unidadGestionRefs = authentication.getAuthorities().stream().map(authority -> {
      if (authority.getAuthority().indexOf("_") > 0) {
        return authority.getAuthority().split("_")[1];
      }
      return null;
    }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

    Page<Solicitud> page = service.findAllTodosRestringidos(query, paging, unidadGestionRefs);

    if (page.isEmpty()) {
      log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link SolicitudModalidad} de la
   * {@link Solicitud}.
   * 
   * @param id     Identificador de {@link Solicitud}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudmodalidades")
  // @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-C', 'CSP-SOL-E')")
  ResponseEntity<Page<SolicitudModalidad>> findAllSolicitudModalidad(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudModalidad(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<SolicitudModalidad> page = solicitudModalidadService.findAllBySolicitud(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudModalidad(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudModalidad(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link SolicitudHito} de la
   * {@link Solicitud}.
   * 
   * @param id     Identificador de {@link Solicitud}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/estadosolicitudes")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-V')")
  ResponseEntity<Page<EstadoSolicitud>> findAllEstadoSolicitud(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllEstadoSolicitud(Long id, Pageable paging) - start");
    Page<EstadoSolicitud> page = estadoSolicitudService.findAllBySolicitud(id, paging);

    if (page.isEmpty()) {
      log.debug("findAllEstadoSolicitud(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllEstadoSolicitud(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);

  }

  /**
   * Devuelve una lista paginada de {@link SolicitudHito}
   * 
   * @param id     Identificador de {@link Solicitud}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudhitos")
  // @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-C', 'CSP-SOL-E')")
  ResponseEntity<Page<SolicitudHito>> findAllSolicitudHito(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudHito(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<SolicitudHito> page = solicitudHitoService.findAllBySolicitud(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudHito(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudModalidad(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link SolicitudDocumento} de la
   * {@link Solicitud}.
   * 
   * @param id     Identificador de {@link Solicitud}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicituddocumentos")
  // @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-C', 'CSP-SOL-E')")
  ResponseEntity<Page<SolicitudDocumento>> findAllSolicitudDocumentos(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudDocumentos(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<SolicitudDocumento> page = solicitudDocumentoService.findAllBySolicitud(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudDocumentos(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudDocumentos(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Comprueba si la solicitud está asociada a una convocatoria SGI.
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return HTTP 200 si se encuentra asociada a convocatoria SGI y HTTP 204 si
   *         no.
   */
  @RequestMapping(path = "/{id}/convocatoria-sgi", method = RequestMethod.HEAD)
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-V')")
  public ResponseEntity<?> hasConvocatoriaSgi(@PathVariable Long id) {
    log.debug("hasConvocatoriaSgi(Long id) - start");
    Boolean returnValue = service.hasConvocatoriaSgi(id);
    log.debug("hasConvocatoriaSgi(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Recupera un {@link SolicitudProyectoDatos} de una solicitud
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link SolicitudProyectoDatos}
   */
  @RequestMapping(path = "/{id}/solicitudproyectodatos", method = RequestMethod.GET)
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-V')")
  public ResponseEntity<SolicitudProyectoDatos> findSolictudProyectoDatos(@PathVariable Long id) {
    log.debug("findSolictudProyectoDatos(Long id) - start");
    SolicitudProyectoDatos returnValue = solicitudProyectoDatosService.findBySolicitud(id);

    if (returnValue == null) {
      log.debug("SolicitudProyectoDatos findSolictudProyectoDatos(Long id) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("SolicitudProyectoDatos findSolictudProyectoDatos(Long id) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de {@link SolicitudProyectoSocio}
   * 
   * @param id     Identificador de {@link SolicitudProyectoSocio}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudproyectosocio")
  // @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-C', 'CSP-SOL-E')")
  ResponseEntity<Page<SolicitudProyectoSocio>> findAllSolicitudProyectoSocio(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudProyectoSocio(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<SolicitudProyectoSocio> page = solicitudProyectoSocioService.findAllBySolicitud(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudProyectoSocio(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudProyectoSocio(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de {@link SolicitudProyectoEquipo}
   * 
   * @param id     Identificador de {@link SolicitudProyectoDatos}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudproyectoequipo")
  // @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-C', 'CSP-SOL-E')")
  ResponseEntity<Page<SolicitudProyectoEquipo>> findAllSolicitudProyectoEquipo(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudProyectoEquipo(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<SolicitudProyectoEquipo> page = solicitudProyectoEquipoService.findAllBySolicitud(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudProyectoEquipo(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudProyectoEquipo(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de
   * {@link SolicitudProyectoEntidadFinanciadoraAjena}
   * 
   * @param id     Identificador de {@link Solicitud}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudproyectoentidadfinanciadoraajenas")
  // @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-C', 'CSP-SOL-E')")
  ResponseEntity<Page<SolicitudProyectoEntidadFinanciadoraAjena>> findAllSolicitudProyectoEntidadFinanciadoraAjena(
      @PathVariable Long id, @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug(
        "findAllSolicitudProyectoEntidadFinanciadoraAjena(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<SolicitudProyectoEntidadFinanciadoraAjena> page = solicitudProyectoEntidadFinanciadoraAjenaService
        .findAllBySolicitud(id, query, paging);

    if (page.isEmpty()) {
      log.debug(
          "findAllSolicitudProyectoEntidadFinanciadoraAjena(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug(
        "findAllSolicitudProyectoEntidadFinanciadoraAjena(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Recupera un {@link SolicitudProyectoDatos} de una solicitud
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link SolicitudProyectoDatos}
   */
  @RequestMapping(path = "/{id}/solicitudproyectodatos", method = RequestMethod.HEAD)
  // @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-C', 'CSP-SOL-E')")
  public ResponseEntity<?> existSolictudProyectoDatos(@PathVariable Long id) {
    log.debug("existSolictudProyectoDatos(Long id) - start");
    boolean returnValue = solicitudProyectoDatosService.existsBySolicitudId(id);

    log.debug("SolicitudProyectoDatos existSolictudProyectoDatos(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista paginada de {@link SolicitudProyectoPresupuesto}
   * 
   * @param id     Identificador de {@link Solicitud}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudproyectopresupuestos")
  // @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-C', 'CSP-SOL-E')")
  ResponseEntity<Page<SolicitudProyectoPresupuesto>> findAllSolicitudProyectoPresupuesto(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudProyectoPresupuesto(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<SolicitudProyectoPresupuesto> page = solicitudProyectoPresupuestoService.findAllBySolicitud(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudProyectoPresupuesto(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudProyectoPresupuesto(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de {@link SolicitudProyectoPresupuesto}
   * 
   * @param id     Identificador de {@link Solicitud}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudproyectopresupuestos/entidadconvocatoria/{entidadRef}")
  // @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-C', 'CSP-SOL-E')")
  ResponseEntity<Page<SolicitudProyectoPresupuesto>> findAllSolicitudProyectoPresupuestoEntidadConvocatoria(
      @PathVariable Long id, @PathVariable String entidadRef,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug(
        "findAllSolicitudProyectoPresupuestoEntidad(Long id, String entidadRef, List<QueryCriteria> query, Pageable paging) - start");
    Page<SolicitudProyectoPresupuesto> page = solicitudProyectoPresupuestoService.findAllBySolicitudAndEntidadRef(id,
        entidadRef, false, query, paging);

    if (page.isEmpty()) {
      log.debug(
          "findAllSolicitudProyectoPresupuestoEntidad(Long id, String entidadRef, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug(
        "findAllSolicitudProyectoPresupuestoEntidad(Long id, String entidadRef, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada de {@link SolicitudProyectoPresupuesto}
   * 
   * @param id     Identificador de {@link Solicitud}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/solicitudproyectopresupuestos/entidadajena/{entidadRef}")
  // @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-C', 'CSP-SOL-E')")
  ResponseEntity<Page<SolicitudProyectoPresupuesto>> findAllSolicitudProyectoPresupuestoEntidadAjena(
      @PathVariable Long id, @PathVariable String entidadRef,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug(
        "findAllSolicitudProyectoPresupuestoEntidad(Long id, String entidadRef, List<QueryCriteria> query, Pageable paging) - start");
    Page<SolicitudProyectoPresupuesto> page = solicitudProyectoPresupuestoService.findAllBySolicitudAndEntidadRef(id,
        entidadRef, true, query, paging);

    if (page.isEmpty()) {
      log.debug(
          "findAllSolicitudProyectoPresupuestoEntidad(Long id, String entidadRef, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug(
        "findAllSolicitudProyectoPresupuestoEntidad(Long id, String entidadRef, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Comprueba si el {@link SolicitudProyectoDatos} de una solicitud tiene
   * presupuesto por entidades.
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link SolicitudProyectoDatos}
   */
  @RequestMapping(path = "/{id}/presupuestoporentidades", method = RequestMethod.HEAD)
  // @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-C', 'CSP-SOL-E')")
  public ResponseEntity<?> hasPresupuestoPorEntidades(@PathVariable Long id) {
    log.debug("hasPresupuestoPorEntidades(Long id) - start");
    boolean returnValue = solicitudProyectoDatosService.hasPresupuestoPorEntidades(id);

    log.debug("hasPresupuestoPorEntidades(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Obtiene el {@link SolicitudProyectoPresupuestoTotales} de la
   * {@link Solicitud}.
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link SolicitudProyectoPresupuestoTotales}
   */
  @GetMapping("/{id}/solicitudproyectopresupuestos/totales")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-V')")
  SolicitudProyectoPresupuestoTotales getSolicitudProyectoPresupuestoTotales(@PathVariable Long id) {
    log.debug("getSolicitudProyectoPresupuestoTotales(Long id) - start");
    SolicitudProyectoPresupuestoTotales returnValue = solicitudProyectoPresupuestoService.getTotales(id);
    log.debug("getSolicitudProyectoPresupuestoTotales(Long id) - end");
    return returnValue;
  }

  @GetMapping("/{id}/solicitudproyectopresupuestos/totalesconceptogasto")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-V')")
  public ResponseEntity<List<SolicitudProyectoPresupuestoTotalConceptoGasto>> findAllSolicitudProyectoPresupuestoTotalConceptoGastos(
      @PathVariable Long id) {
    log.debug("findAllSolicitudProyectoPresupuestoTotalConceptoGastos(Long id) - start");
    List<SolicitudProyectoPresupuestoTotalConceptoGasto> returnValue = solicitudProyectoPresupuestoService
        .findAllSolicitudProyectoPresupuestoTotalConceptoGastos(id);
    log.debug("findAllSolicitudProyectoPresupuestoTotalConceptoGastos(Long id) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

}
