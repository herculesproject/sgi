package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.ConvocatoriaConceptoGastoCodigoEcWithEnableAccion;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaConceptoGastoWithEnableAccion;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaDocumento;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.service.ConvocatoriaAreaTematicaService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaConceptoGastoCodigoEcService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaConceptoGastoService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaDocumentoService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEnlaceService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadConvocanteService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadFinanciadoraService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadGestoraService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaFaseService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaHitoService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPeriodoJustificacionService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPeriodoSeguimientoCientificoService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ConvocatoriaController
 */
@RestController
@RequestMapping("/convocatorias")
@Slf4j
public class ConvocatoriaController {

  /** ConvocatoriaService service */
  private final ConvocatoriaService service;

  /** ConvocatoriaEntidadFinanciadora service */
  private final ConvocatoriaEntidadFinanciadoraService convocatoriaEntidadFinanciadoraService;

  /** ConvocatoriaEntidadGestora service */
  private final ConvocatoriaEntidadGestoraService convocatoriaEntidadGestoraService;

  /** ConvocatoriaEntidadGestora service */
  private final ConvocatoriaAreaTematicaService convocatoriaAreaTematicaService;

  /** ConvocatoriaFase service */
  private final ConvocatoriaFaseService convocatoriaFaseService;

  /** ConvocatoriaDocumento service */
  private final ConvocatoriaDocumentoService convocatoriaDocumentoService;

  /** ConvocatoriaEnlace service */
  private final ConvocatoriaEnlaceService convocatoriaEnlaceService;

  /** ConvocatoriaEntidadConvocante service */
  private final ConvocatoriaEntidadConvocanteService convocatoriaEntidadConvocanteService;

  /** ConvocatoriaHitoservice */
  private final ConvocatoriaHitoService convocatoriaHitoService;

  /** ConvocatoriaPeriodoJustificacion service */
  private final ConvocatoriaPeriodoJustificacionService convocatoriaPeriodoJustificacionService;

  /** ConvocatoriaPeriodoSeguimientoCientifico service */
  private final ConvocatoriaPeriodoSeguimientoCientificoService convocatoriaPeriodoSeguimientoCientificoService;

  /** ConvocatoriaConceptoGastoService */
  private final ConvocatoriaConceptoGastoService convocatoriaConceptoGastoService;

  /** ConvocatoriaConceptoGastoCodigoEcService */
  private final ConvocatoriaConceptoGastoCodigoEcService convocatoriaConceptoGastoCodigoEcService;

  /**
   * Instancia un nuevo ConvocatoriaController.
   * 
   * @param convocatoriaService                             {@link ConvocatoriaService}.
   * @param convocatoriaAreaTematicaService                 {@link ConvocatoriaAreaTematicaService}.
   * @param convocatoriaDocumentoService                    {@link ConvocatoriaDocumentoService}.
   * @param convocatoriaEnlaceService                       {@link ConvocatoriaEnlaceService}.
   * @param convocatoriaEntidadConvocanteService            {@link ConvocatoriaEntidadConvocanteService}.
   * @param convocatoriaEntidadFinanciadoraService          {@link ConvocatoriaEntidadFinanciadoraService}.
   * @param convocatoriaEntidadGestoraService               {@link ConvocatoriaEntidadGestoraService}.
   * @param convocatoriaFaseService                         {@link ConvocatoriaFaseService}
   * @param convocatoriaHitoService                         {@link ConvocatoriaHitoService}
   * @param convocatoriaPeriodoJustificacionService         {@link ConvocatoriaPeriodoJustificacionService}.
   * @param convocatoriaPeriodoSeguimientoCientificoService {@link ConvocatoriaPeriodoSeguimientoCientificoService}
   * @param convocatoriaConceptoGastoService                {@link ConvocatoriaConceptoGastoService}
   * @param convocatoriaConceptoGastoCodigoEcService        {@link ConvocatoriaConceptoGastoCodigoEcService}
   */
  public ConvocatoriaController(ConvocatoriaService convocatoriaService,
      ConvocatoriaAreaTematicaService convocatoriaAreaTematicaService,
      ConvocatoriaDocumentoService convocatoriaDocumentoService, ConvocatoriaEnlaceService convocatoriaEnlaceService,
      ConvocatoriaEntidadConvocanteService convocatoriaEntidadConvocanteService,
      ConvocatoriaEntidadFinanciadoraService convocatoriaEntidadFinanciadoraService,
      ConvocatoriaEntidadGestoraService convocatoriaEntidadGestoraService,
      ConvocatoriaFaseService convocatoriaFaseService, ConvocatoriaHitoService convocatoriaHitoService,
      ConvocatoriaPeriodoJustificacionService convocatoriaPeriodoJustificacionService,
      ConvocatoriaPeriodoSeguimientoCientificoService convocatoriaPeriodoSeguimientoCientificoService,
      ConvocatoriaConceptoGastoService convocatoriaConceptoGastoService,
      ConvocatoriaConceptoGastoCodigoEcService convocatoriaConceptoGastoCodigoEcService) {
    this.service = convocatoriaService;
    this.convocatoriaAreaTematicaService = convocatoriaAreaTematicaService;
    this.convocatoriaDocumentoService = convocatoriaDocumentoService;
    this.convocatoriaEnlaceService = convocatoriaEnlaceService;
    this.convocatoriaEntidadConvocanteService = convocatoriaEntidadConvocanteService;
    this.convocatoriaEntidadFinanciadoraService = convocatoriaEntidadFinanciadoraService;
    this.convocatoriaEntidadGestoraService = convocatoriaEntidadGestoraService;
    this.convocatoriaFaseService = convocatoriaFaseService;
    this.convocatoriaHitoService = convocatoriaHitoService;
    this.convocatoriaPeriodoJustificacionService = convocatoriaPeriodoJustificacionService;
    this.convocatoriaPeriodoSeguimientoCientificoService = convocatoriaPeriodoSeguimientoCientificoService;
    this.convocatoriaConceptoGastoService = convocatoriaConceptoGastoService;
    this.convocatoriaConceptoGastoCodigoEcService = convocatoriaConceptoGastoCodigoEcService;
  }

  /**
   * Crea nuevo {@link Convocatoria}
   * 
   * @param convocatoria {@link Convocatoria}. que se quiere crear.
   * @return Nuevo {@link Convocatoria} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CONV-C')")
  public ResponseEntity<Convocatoria> create(@Valid @RequestBody Convocatoria convocatoria,
      Authentication atuhentication) {
    log.debug("create(Convocatoria convocatoria) - start");

    List<String> acronimosUnidadGestion = atuhentication.getAuthorities().stream().map(acronimo -> {
      if (acronimo.getAuthority().indexOf("_") > 0) {
        return acronimo.getAuthority().split("_")[1];
      }
      return null;
    }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

    Convocatoria returnValue = service.create(convocatoria, acronimosUnidadGestion);
    log.debug("create(Convocatoria convocatoria) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Convocatoria}.
   * 
   * @param convocatoria {@link Convocatoria} a actualizar.
   * @param id           Identificador {@link Convocatoria} a actualizar.
   * @return Convocatoria {@link Convocatoria} actualizado
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CONV-E')")
  public Convocatoria update(@Valid @RequestBody Convocatoria convocatoria, @PathVariable Long id,
      Authentication atuhentication) {
    log.debug("update(Convocatoria convocatoria, Long id) - start");

    List<String> acronimosUnidadGestion = atuhentication.getAuthorities().stream().map(acronimo -> {
      if (acronimo.getAuthority().indexOf("_") > 0) {
        return acronimo.getAuthority().split("_")[1];
      }
      return null;
    }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

    convocatoria.setId(id);
    Convocatoria returnValue = service.update(convocatoria, acronimosUnidadGestion);
    log.debug("update(Convocatoria convocatoria, Long id) - end");
    return returnValue;
  }

  /**
   * Registra la {@link Convocatoria} con id indicado.
   * 
   * @param id Identificador de {@link Convocatoria}.
   * @return {@link Convocatoria} actualizado.
   */
  @PatchMapping("/{id}/registrar")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CONV-E')")
  public Convocatoria registrar(@PathVariable Long id) {
    log.debug("registrar(Long id) - start");
    Convocatoria returnValue = service.registrar(id);
    log.debug("registrar(Long id) - end");
    return returnValue;
  }

  /**
   * Reactiva el {@link Convocatoria} con id indicado.
   * 
   * @param id Identificador de {@link Convocatoria}.
   * @return {@link Convocatoria} actualizado.
   */
  @PatchMapping("/{id}/reactivar")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CONV-R')")
  public Convocatoria reactivar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    Convocatoria returnValue = service.enable(id);
    log.debug("reactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva {@link Convocatoria} con id indicado.
   * 
   * @param id Identificador de {@link Convocatoria}.
   * @return {@link Convocatoria} actualizado.
   */
  @PatchMapping("/{id}/desactivar")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CONV-B')")
  public Convocatoria desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    Convocatoria returnValue = service.disable(id);
    log.debug("desactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Convocatoria} con el id indicado.
   * 
   * @param id Identificador de {@link Convocatoria}.
   * @return Convocatoria {@link Convocatoria} correspondiente al id
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CONV-V')")
  Convocatoria findById(@PathVariable Long id) {
    log.debug("Convocatoria findById(Long id) - start");
    Convocatoria returnValue = service.findById(id);
    log.debug("Convocatoria findById(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Convocatoria} activas.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Convocatoria} activas paginadas y
   *         filtradas.
   */
  @GetMapping()
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CONV-V')")
  ResponseEntity<Page<Convocatoria>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Page<Convocatoria> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Convocatoria}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Convocatoria} paginadas y filtradas.
   */
  @GetMapping("/todos")
  // @PreAuthorize("hasAuthorityForAnyUO('SYSADMIN')")
  ResponseEntity<Page<Convocatoria>> findAllTodos(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(List<QueryCriteria> query,Pageable paging) - start");
    Page<Convocatoria> page = service.findAllTodos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllTodos(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Convocatoria}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Convocatoria} paginadas y filtradas.
   */
  @GetMapping("/todos/restringidos")
  // @PreAuthorize("hasAuthorityForAnyUO('SYSADMIN')")
  ResponseEntity<Page<Convocatoria>> findAllTodosRestringidos(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging, Authentication atuhentication) {
    log.debug(
        "findAllTodosRestringidos(List<QueryCriteria> query,Pageable paging, Authentication atuhentication) - start");

    List<String> acronimosUnidadGestion = atuhentication.getAuthorities().stream().map(acronimo -> {
      if (acronimo.getAuthority().indexOf("_") > 0) {
        return acronimo.getAuthority().split("_")[1];
      }
      return null;
    }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

    Page<Convocatoria> page = service.findAllTodosRestringidos(query, paging, acronimosUnidadGestion);

    if (page.isEmpty()) {
      log.debug(
          "findAllTodosRestringidos(List<QueryCriteria> query,Pageable paging, Authentication atuhentication) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug(
        "findAllTodosRestringidos(List<QueryCriteria> query,Pageable paging Authentication atuhentication) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * CONVOCATORIA HITO
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaHito} de la
   * {@link Convocatoria}.
   * 
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriahitos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CENTGES-V')")
  ResponseEntity<Page<ConvocatoriaHito>> findAllConvocatoriaHito(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaHito(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ConvocatoriaHito> page = convocatoriaHitoService.findAllByConvocatoria(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaHito(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaHito(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * CONVOCATORIA ENTIDAD FINANCIADORA
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ConvocatoriaEntidadFinanciadora} de la {@link Convocatoria}.
   * 
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriaentidadfinanciadoras")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CATEM-V')")
  ResponseEntity<Page<ConvocatoriaEntidadFinanciadora>> findAllConvocatoriaEntidadFinanciadora(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaEntidadFinanciadora(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ConvocatoriaEntidadFinanciadora> page = convocatoriaEntidadFinanciadoraService.findAllByConvocatoria(id, query,
        paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaEntidadFinanciadora(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaEntidadFinanciadora(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);

  }

  /**
   * 
   * CONVOCATORIA ENTIDAD GESTORA
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaEntidadGestora}
   * de la {@link Convocatoria}.
   * 
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriaentidadgestoras")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CENTGES-V')")
  ResponseEntity<Page<ConvocatoriaEntidadGestora>> findAllConvocatoriaEntidadGestora(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaEntidadGestora(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ConvocatoriaEntidadGestora> page = convocatoriaEntidadGestoraService.findAllByConvocatoria(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaEntidadGestora(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaEntidadGestora(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * CONVOCATORIA FASE
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaFase} de la
   * {@link Convocatoria}.
   * 
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriafases")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CFAS-V')")
  ResponseEntity<Page<ConvocatoriaFase>> findAllConvocatoriaFases(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaFase(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ConvocatoriaFase> page = convocatoriaFaseService.findAllByConvocatoria(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaFase(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaFase(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * CONVOCATORIA AREA TEMATICA
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaAreaTematica} de
   * la {@link Convocatoria}.
   * 
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriaareatematicas")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CATEM-V')")
  ResponseEntity<Page<ConvocatoriaAreaTematica>> findAllConvocatoriaAreaTematica(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaAreaTematica(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ConvocatoriaAreaTematica> page = convocatoriaAreaTematicaService.findAllByConvocatoria(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaAreaTematica(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaAreaTematica(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * CONVOCATORIA DOCUMENTO
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaDocumento} de la
   * {@link Convocatoria}.
   * 
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriadocumentos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CENTGES-V')")
  ResponseEntity<Page<ConvocatoriaDocumento>> findAllConvocatoriaDocumento(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaDocumento(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ConvocatoriaDocumento> page = convocatoriaDocumentoService.findAllByConvocatoria(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaDocumento(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaDocumento(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * CONVOCATORIA ENLACE
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaEnlace} de la
   * {@link Convocatoria}.
   * 
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriaenlaces")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CENTGES-V')")
  ResponseEntity<Page<ConvocatoriaEnlace>> findAllConvocatoriaEnlace(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaEnlace(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ConvocatoriaEnlace> page = convocatoriaEnlaceService.findAllByConvocatoria(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaEnlace(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaEnlace(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * CONVOCATORIA ENTIDAD CONVOCANTE
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ConvocatoriaEntidadConvocante} de la {@link Convocatoria}.
   * 
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriaentidadconvocantes")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CENTGES-V')")
  ResponseEntity<Page<ConvocatoriaEntidadConvocante>> findAllConvocatoriaEntidadConvocantes(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaEntidadConvocantes(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ConvocatoriaEntidadConvocante> page = convocatoriaEntidadConvocanteService.findAllByConvocatoria(id, query,
        paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaEntidadConvocantes(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaEntidadConvocantes(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * CONVOCATORIA PERIODO JUSTIFICACION
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ConvocatoriaPeriodoJustificacion} de la {@link Convocatoria}.
   * 
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriaperiodojustificaciones")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CATEM-V')")
  ResponseEntity<Page<ConvocatoriaPeriodoJustificacion>> findAllConvocatoriaPeriodoJustificacion(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaPeriodoJustificacion(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ConvocatoriaPeriodoJustificacion> page = convocatoriaPeriodoJustificacionService.findAllByConvocatoria(id,
        query, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaPeriodoJustificacion(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaPeriodoJustificacion(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * CONVOCATORIA PERIODO SEGUIMIENTO CIENTIFICO
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link convocatoriaPeriodoSeguimientoCientifico} de la {@link Convocatoria}.
   * 
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriaperiodoseguimientocientificos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CPSCI-V')")
  ResponseEntity<Page<ConvocatoriaPeriodoSeguimientoCientifico>> findAllConvocatoriaPeriodoSeguimientoCientifico(
      @PathVariable Long id, @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug(
        "findAllConvocatoriaPeriodoSeguimientoCientifico(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ConvocatoriaPeriodoSeguimientoCientifico> page = convocatoriaPeriodoSeguimientoCientificoService
        .findAllByConvocatoria(id, query, paging);

    if (page.isEmpty()) {
      log.debug(
          "findAllConvocatoriaPeriodoSeguimientoCientifico(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug(
        "findAllConvocatoriaPeriodoSeguimientoCientifico(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * CONVOCATORIA GASTOS
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaConceptoGasto}
   * permitidos de la {@link Convocatoria}.
   * 
   * @param id     Identificador de {@link Convocatoria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriagastos/permitidos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CPSCI-V')")
  ResponseEntity<Page<ConvocatoriaConceptoGastoWithEnableAccion>> findAllConvocatoriaGastosPermitidos(
      @PathVariable Long id, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaGastosPermitidos(Long id, Pageable paging) - start");
    Page<ConvocatoriaConceptoGastoWithEnableAccion> page = convocatoriaConceptoGastoService
        .findAllByConvocatoriaAndPermitidoTrue(id, paging);
    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaGastosPermitidos(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaGastosPermitidos(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaConceptoGasto}
   * no permitidos de la {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriagastos/nopermitidos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CPSCI-V')")
  ResponseEntity<Page<ConvocatoriaConceptoGastoWithEnableAccion>> findAllConvocatoriaGastosNoPermitidos(
      @PathVariable Long id, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaGastosNoPermitidos(Long id, Pageable paging) - start");
    Page<ConvocatoriaConceptoGastoWithEnableAccion> page = convocatoriaConceptoGastoService
        .findAllByConvocatoriaAndPermitidoFalse(id, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaGastosNoPermitidos(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaGastosNoPermitidos(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * CONVOCATORIA GASTOS CÓDIGO ECONÓMICO
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ConvocatoriaConceptoGastoCodigoEc} permitidos de la
   * {@link Convocatoria}.
   * 
   * @param id     Identificador de {@link Convocatoria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriagastocodigoec/permitidos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CCOD-V')")
  ResponseEntity<Page<ConvocatoriaConceptoGastoCodigoEcWithEnableAccion>> findAllConvocatoriaGastosCodigoEcPermitidos(
      @PathVariable Long id, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaGastosCodigoEcPermitidos(Long id, Pageable paging) - start");
    Page<ConvocatoriaConceptoGastoCodigoEcWithEnableAccion> page = convocatoriaConceptoGastoCodigoEcService
        .findAllByConvocatoriaAndPermitidoTrue(id, paging);
    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaGastosCodigoEcPermitidos(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaGastosCodigoEcPermitidos(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ConvocatoriaConceptoGastoCodigoEc} no permitidos de la
   * {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriagastocodigoec/nopermitidos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CCOD-V')")
  ResponseEntity<Page<ConvocatoriaConceptoGastoCodigoEcWithEnableAccion>> findAllConvocatoriaGastosCodigoEcNoPermitidos(
      @PathVariable Long id, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaGastosCodigoEcNoPermitidos(Long id, Pageable paging) - start");
    Page<ConvocatoriaConceptoGastoCodigoEcWithEnableAccion> page = convocatoriaConceptoGastoCodigoEcService
        .findAllByConvocatoriaAndPermitidoFalse(id, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaGastosCodigoEcNoPermitidos(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaGastosCodigoEcNoPermitidos(Long id, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}
