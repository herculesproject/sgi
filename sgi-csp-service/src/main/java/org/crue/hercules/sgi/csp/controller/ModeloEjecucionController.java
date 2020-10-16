package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.service.ModeloEjecucionService;
import org.crue.hercules.sgi.csp.service.ModeloTipoEnlaceService;
import org.crue.hercules.sgi.csp.service.ModeloTipoDocumentoService;
import org.crue.hercules.sgi.csp.service.ModeloTipoFaseService;
import org.crue.hercules.sgi.csp.service.ModeloTipoFinalidadService;
import org.crue.hercules.sgi.csp.service.ModeloTipoHitoService;
import org.crue.hercules.sgi.csp.service.ModeloUnidadService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * ModeloEjecucionController
 */
@RestController
@RequestMapping("/modeloejecuciones")
@Slf4j
public class ModeloEjecucionController {

  /** ModeloEjecucion service */
  private final ModeloEjecucionService modeloEjecucionService;

  /** ModeloTipoEnlace service */
  private final ModeloTipoEnlaceService modeloTipoEnlaceService;

  /** ModeloTipoFase service */
  private final ModeloTipoFaseService modeloTipoFaseService;

  /** ModeloTipoDocumento service */
  private final ModeloTipoDocumentoService modeloTipoDocumentoService;

  /** ModeloTipoFinalidad service */
  private final ModeloTipoFinalidadService modeloTipoFinalidadService;

  /** ModeloTipoHito service */
  private final ModeloTipoHitoService modeloTipoHitoService;

  /** ModeloUnidad service */
  private final ModeloUnidadService modeloUnidadService;

  /**
   * Instancia un nuevo ModeloEjecucionController.
   * 
   * @param modeloEjecucionService     {@link ModeloEjecucionService}.
   * @param modeloTipoEnlaceService    {@link ModeloTipoEnlaceService}.
   * @param modeloTipoFaseService      {@link ModeloTipoFaseService}.
   * @param modeloTipoDocumentoService {@link ModeloTipoDocumentoService}.
   * @param modeloTipoFinalidadService {@link ModeloTipoFinalidadService}.
   * @param modeloTipoHitoService      {@link ModeloTipoHitoService}.
   * @param modeloUnidadService        {@link ModeloUnidadService}.
   */
  public ModeloEjecucionController(ModeloEjecucionService modeloEjecucionService,
      ModeloTipoEnlaceService modeloTipoEnlaceService, ModeloTipoFaseService modeloTipoFaseService,
      ModeloTipoDocumentoService modeloTipoDocumentoService, ModeloTipoFinalidadService modeloTipoFinalidadService,
      ModeloTipoHitoService modeloTipoHitoService, ModeloUnidadService modeloUnidadService) {
    this.modeloEjecucionService = modeloEjecucionService;
    this.modeloTipoEnlaceService = modeloTipoEnlaceService;
    this.modeloTipoFaseService = modeloTipoFaseService;
    this.modeloTipoDocumentoService = modeloTipoDocumentoService;
    this.modeloTipoFinalidadService = modeloTipoFinalidadService;
    this.modeloTipoHitoService = modeloTipoHitoService;
    this.modeloUnidadService = modeloUnidadService;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ModeloEjecucion} activos.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping()
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-V')")
  ResponseEntity<Page<ModeloEjecucion>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - start");
    Page<ModeloEjecucion> page = modeloEjecucionService.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ModeloEjecucion}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/todos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-V')")
  ResponseEntity<Page<ModeloEjecucion>> findAllTodos(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - start");
    Page<ModeloEjecucion> page = modeloEjecucionService.findAllTodos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link ModeloEjecucion} con el id indicado.
   * 
   * @param id Identificador de {@link ModeloEjecucion}.
   * @return {@link ModeloEjecucion} correspondiente al id.
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-V')")
  ModeloEjecucion findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ModeloEjecucion returnValue = modeloEjecucionService.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea un nuevo {@link ModeloEjecucion}.
   * 
   * @param modeloEjecucion {@link ModeloEjecucion} que se quiere crear.
   * @return Nuevo {@link ModeloEjecucion} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-C')")
  ResponseEntity<ModeloEjecucion> create(@Valid @RequestBody ModeloEjecucion modeloEjecucion) {
    log.debug("create(ModeloEjecucion modeloEjecucion) - start");
    ModeloEjecucion returnValue = modeloEjecucionService.create(modeloEjecucion);
    log.debug("create(ModeloEjecucion modeloEjecucion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ModeloEjecucion} con el id indicado.
   * 
   * @param modeloEjecucion {@link ModeloEjecucion} a actualizar.
   * @param id              id {@link ModeloEjecucion} a actualizar.
   * @return {@link ModeloEjecucion} actualizado.
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-E')")
  ModeloEjecucion update(@Validated({ Update.class, Default.class }) @RequestBody ModeloEjecucion modeloEjecucion,
      @PathVariable Long id) {
    log.debug("update(ModeloEjecucion modeloEjecucion, Long id) - start");
    modeloEjecucion.setId(id);
    ModeloEjecucion returnValue = modeloEjecucionService.update(modeloEjecucion);
    log.debug("update(ModeloEjecucion modeloEjecucion, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ModeloEjecucion} con id indicado.
   * 
   * @param id Identificador de {@link ModeloEjecucion}.
   */
  @DeleteMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-B')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    modeloEjecucionService.disable(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * 
   * MODELO TIPO ENLACE
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ModeloTipoEnlace} del
   * {@link ModeloEjecucion}.
   * 
   * @param id     Identificador de {@link ModeloEjecucion}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/modelotipoenlaces")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-V')")
  ResponseEntity<Page<ModeloTipoEnlace>> findAllModeloTipoEnlaces(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllModeloTipoEnlaces(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ModeloTipoEnlace> page = modeloTipoEnlaceService.findAllByModeloEjecucion(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllModeloTipoEnlaces(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllModeloTipoEnlaces(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * MODELO TIPO FASE
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ModeloTipoFase} del
   * {@link ModeloEjecucion}.
   * 
   * @param id     Identificador de {@link ModeloEjecucion}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/modelotipofases")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-V')")
  ResponseEntity<Page<ModeloTipoFase>> findAllModeloTipoFases(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllModeloTipoFases(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ModeloTipoFase> page = modeloTipoFaseService.findAllByModeloEjecucion(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllModeloTipoFases(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllModeloTipoFases(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link ModeloTipoFase} del
   * {@link ModeloEjecucion}.
   * 
   * @param id     Identificador de {@link ModeloEjecucion}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/modelotipofases/convocatoria")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-V')")
  ResponseEntity<Page<ModeloTipoFase>> findAllModeloTipoFasesConvocatoria(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllModeloTipoFasesConvocatoria(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ModeloTipoFase> page = modeloTipoFaseService.findAllByModeloEjecucionActivosConvocatoria(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllModeloTipoFasesConvocatoria(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllModeloTipoFasesConvocatoria(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link TipoFase} del
   * {@link ModeloEjecucion}.
   * 
   * @param id     Identificador de {@link ModeloEjecucion}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/modelotipofases/proyecto")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-V')")
  ResponseEntity<Page<ModeloTipoFase>> findAllModeloTipoFasesProyecto(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllModeloTipoFasesProyecto(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ModeloTipoFase> page = modeloTipoFaseService.findAllByModeloEjecucionActivosProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllModeloTipoFasesProyecto(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllModeloTipoFasesProyecto(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * MODELO TIPO FASE DOCUMENTO
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ModeloTipoDocumento} del
   * {@link ModeloEjecucion}.
   * 
   * @param id     Identificador de {@link ModeloEjecucion}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/modelotipodocumentos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-V')")
  ResponseEntity<Page<ModeloTipoDocumento>> findAllModeloTipoDocumentos(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllModeloTipoDocumentos(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ModeloTipoDocumento> page = modeloTipoDocumentoService.findAllByModeloEjecucion(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllModeloTipoDocumentos(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllModeloTipoDocumentos(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * MODELO TIPO FINALIDAD
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link TipoFinalidad} del
   * {@link ModeloEjecucion}.
   * 
   * @param id     Identificador de {@link ModeloEjecucion}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/modelotipofinalidades")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-V')")
  ResponseEntity<Page<ModeloTipoFinalidad>> findAllModeloTipoFinalidades(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllModeloTipoFinalidades(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ModeloTipoFinalidad> page = modeloTipoFinalidadService.findAllByModeloEjecucion(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllModeloTipoFinalidades(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllModeloTipoFinalidades(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * MODELO TIPO HITO
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ModeloTipoHito} del
   * {@link ModeloEjecucion}.
   * 
   * @param id     Identificador de {@link ModeloEjecucion}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/modelotipohitos")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-V')")
  ResponseEntity<Page<ModeloTipoHito>> findAllModeloTipoHitos(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllModeloTipoHitos(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ModeloTipoHito> page = modeloTipoHitoService.findAllByModeloEjecucion(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllModeloTipoHitos(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllModeloTipoHitos(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link ModeloTipoFase} del
   * {@link ModeloEjecucion}.
   * 
   * @param id     Identificador de {@link ModeloEjecucion}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/modelotipohitos/convocatoria")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-V')")
  ResponseEntity<Page<ModeloTipoHito>> findAllModeloTipoHitosConvocatoria(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllModeloTipoHitosConvocatoria(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ModeloTipoHito> page = modeloTipoHitoService.findAllByModeloEjecucionActivosConvocatoria(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllModeloTipoHitosConvocatoria(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllModeloTipoHitosConvocatoria(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link TipoFase} del
   * {@link ModeloEjecucion}.
   * 
   * @param id     Identificador de {@link ModeloEjecucion}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/modelotipohitos/proyecto")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-V')")
  ResponseEntity<Page<ModeloTipoHito>> findAllModeloTipoHitosProyecto(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllModeloTipoHitosProyecto(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ModeloTipoHito> page = modeloTipoHitoService.findAllByModeloEjecucionActivosProyecto(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllModeloTipoHitosProyecto(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllModeloTipoHitosProyecto(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link TipoFase} del
   * {@link ModeloEjecucion}.
   * 
   * @param id     Identificador de {@link ModeloEjecucion}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/modelotipohitos/solicitud")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-V')")
  ResponseEntity<Page<ModeloTipoHito>> findAllModeloTipoHitosSolicitud(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllModeloTipoHitosSolicitud(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ModeloTipoHito> page = modeloTipoHitoService.findAllByModeloEjecucionActivosSolicitud(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllModeloTipoHitosSolicitud(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllModeloTipoHitosSolicitud(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * MODELO UNIDAD
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ModeloUnidad} del
   * {@link ModeloEjecucion}.
   * 
   * @param id     Identificador de {@link ModeloEjecucion}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/modelounidades")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-ME-V')")
  ResponseEntity<Page<ModeloUnidad>> findAllModeloUnidades(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllUnidades(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ModeloUnidad> page = modeloUnidadService.findAllByModeloEjecucion(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllUnidades(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllUnidades(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}
