package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.crue.hercules.sgi.csp.service.ConvocatoriaAreaTematicaService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEnlaceService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadConvocanteService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadFinanciadoraService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadGestoraService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

  /** ConvocatoriaEntlace service */
  private final ConvocatoriaEnlaceService convocatoriaEnlaceService;

  /** ConvocatoriaEntidadConvocante service */
  private final ConvocatoriaEntidadConvocanteService convocatoriaEntidadConvocanteService;

  /**
   * Instancia un nuevo ConvocatoriaController.
   * 
   * @param convocatoriaService                    {@link ConvocatoriaService}.
   * @param convocatoriaAreaTematicaService        {@link ConvocatoriaAreaTematicaService}.
   * @param convocatoriaEnlaceService              {@link ConvocatoriaEnlaceService}.
   * @param convocatoriaEntidadConvocanteService   {@link ConvocatoriaEntidadConvocanteService}.
   * @param convocatoriaEntidadFinanciadoraService {@link ConvocatoriaEntidadFinanciadoraService}.
   * @param convocatoriaEntidadGestoraService      {@link ConvocatoriaEntidadGestoraService}.
   */
  public ConvocatoriaController(ConvocatoriaService convocatoriaService,
      ConvocatoriaAreaTematicaService convocatoriaAreaTematicaService,
      ConvocatoriaEnlaceService convocatoriaEnlaceService,
      ConvocatoriaEntidadConvocanteService convocatoriaEntidadConvocanteService,
      ConvocatoriaEntidadFinanciadoraService convocatoriaEntidadFinanciadoraService,
      ConvocatoriaEntidadGestoraService convocatoriaEntidadGestoraService) {
    this.service = convocatoriaService;
    this.convocatoriaAreaTematicaService = convocatoriaAreaTematicaService;
    this.convocatoriaEnlaceService = convocatoriaEnlaceService;
    this.convocatoriaEntidadConvocanteService = convocatoriaEntidadConvocanteService;
    this.convocatoriaEntidadFinanciadoraService = convocatoriaEntidadFinanciadoraService;
    this.convocatoriaEntidadGestoraService = convocatoriaEntidadGestoraService;
  }

  /**
   * Crea nuevo {@link Convocatoria}.
   * 
   * @param convocatoria {@link Convocatoria}. que se quiere crear.
   * @return Nuevo {@link Convocatoria} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CONV-C')")
  public ResponseEntity<Convocatoria> create(@Valid @RequestBody Convocatoria convocatoria) {
    log.debug("create(Convocatoria convocatoria) - start");
    Convocatoria returnValue = service.create(convocatoria);
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
  public Convocatoria update(@Valid @RequestBody Convocatoria convocatoria, @PathVariable Long id) {
    log.debug("update(Convocatoria convocatoria, Long id) - start");
    convocatoria.setId(id);
    Convocatoria returnValue = service.update(convocatoria);
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
   * Desactiva {@link Convocatoria} con id indicado.
   * 
   * @param id Identificador de {@link Convocatoria}.
   */
  @DeleteMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CONV-B')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.disable(id);
    log.debug("deleteById(Long id) - end");
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

}
