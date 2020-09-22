package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.service.ComentarioService;
import org.crue.hercules.sgi.eti.service.EvaluacionService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * EvaluacionController
 */
@RestController
@RequestMapping("/evaluaciones")
@Slf4j
public class EvaluacionController {

  /** Evaluacion service */
  private final EvaluacionService service;

  /** Comentario service */
  private final ComentarioService comentarioService;

  /**
   * Instancia un nuevo EvaluacionController.
   * 
   * @param service           EvaluacionService
   * @param comentarioService ComentarioService
   */
  public EvaluacionController(EvaluacionService service, ComentarioService comentarioService) {
    log.debug("EvaluacionController(EvaluacionService service, ComentarioService comentarioService) - start");
    this.service = service;
    this.comentarioService = comentarioService;
    log.debug("EvaluacionController(EvaluacionService service, ComentarioService comentarioService) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Evaluacion}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<Evaluacion>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Page<Evaluacion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtener todas las entidades paginadas {@link Evaluacion} activas para una
   * determinada {@link ConvocatoriaReunion}.
   *
   * @param id       Id de {@link ConvocatoriaReunion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @GetMapping("/convocatoriareunion/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-C', 'ETI-ACT-E')")
  ResponseEntity<Page<Evaluacion>> findAllActivasByConvocatoriaReunionId(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("findAllActivasByConvocatoriaReunionId(Long id, Pageable pageable) - start");
    Page<Evaluacion> page = service.findAllActivasByConvocatoriaReunionId(id, pageable);

    if (page.isEmpty()) {
      log.debug("findAllActivasByConvocatoriaReunionId(Long id, Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllActivasByConvocatoriaReunionId(Long id, Pageable pageable) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene la lista de evaluaciones activas de una convocatoria reunion que no
   * estan en revisión mínima.
   *
   * @param id     Id de {@link ConvocatoriaReunion}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @GetMapping("/convocatoriareunionnorevminima/{idConvocatoriaReunion}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-C', 'ETI-CNV-E')")
  ResponseEntity<Page<Evaluacion>> findAllByConvocatoriaReunionIdAndNoEsRevMinima(
      @PathVariable Long idConvocatoriaReunion, @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllByConvocatoriaReunionIdAndNoEsRevMinima({}, {}, {}) - start", idConvocatoriaReunion, query,
        paging);
    Page<Evaluacion> page = service.findAllByConvocatoriaReunionIdAndNoEsRevMinima(idConvocatoriaReunion, query,
        paging);

    if (page.isEmpty()) {
      log.debug("findAllByConvocatoriaReunionIdAndNoEsRevMinima({}, {}, {}) - end", idConvocatoriaReunion, query,
          paging);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllByConvocatoriaReunionIdAndNoEsRevMinima({}, {}, {}) - end", idConvocatoriaReunion, query, paging);
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /*
   * Devuelve una lista paginada y filtrada {@link Evaluacion}.
   * 
   * @param query filtro de {@link QueryCriteria}.
   * 
   * @param paging pageable
   */
  @GetMapping("/evaluables")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V', 'ETI-EVC-VR', 'ETI-EVC-EVAL')")
  ResponseEntity<Page<Evaluacion>> findAllByMemoriaAndRetrospectivaEnEvaluacion(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Page<Evaluacion> page = service.findAllByMemoriaAndRetrospectivaEnEvaluacion(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link Evaluacion}.
   * 
   * @param nuevoEvaluacion {@link Evaluacion}. que se quiere crear.
   * @return Nuevo {@link Evaluacion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-C','ETI-CNV-C', 'ETI-CNV-E')")
  public ResponseEntity<Evaluacion> newEvaluacion(@Valid @RequestBody Evaluacion nuevoEvaluacion) {
    log.debug("newEvaluacion(Evaluacion nuevoEvaluacion) - start");
    Evaluacion returnValue = service.create(nuevoEvaluacion);
    log.debug("newEvaluacion(Evaluacion nuevoEvaluacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Evaluacion}.
   * 
   * @param updatedEvaluacion {@link Evaluacion} a actualizar.
   * @param id                id {@link Evaluacion} a actualizar.
   * @return {@link Evaluacion} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-EVC-E')")
  public Evaluacion replaceEvaluacion(@Valid @RequestBody Evaluacion updatedEvaluacion, @PathVariable Long id) {
    log.debug("replaceEvaluacion(Evaluacion updatedEvaluacion, Long id) - start");
    updatedEvaluacion.setId(id);
    Evaluacion returnValue = service.update(updatedEvaluacion);
    log.debug("replaceEvaluacion(Evaluacion updatedEvaluacion, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Evaluacion} con el id indicado.
   * 
   * @param id Identificador de {@link Evaluacion}.
   * @return {@link Evaluacion} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V', 'ETI-EVC-VR', 'ETI-EVC-VR-INV', 'ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  Evaluacion one(@PathVariable Long id) {
    log.debug("Evaluacion one(Long id) - start");
    Evaluacion returnValue = service.findById(id);
    log.debug("Evaluacion one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link Evaluacion} con id indicado.
   * 
   * @param id Identificador de {@link Evaluacion}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-EVC-B')")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    Evaluacion evaluacion = this.one(id);
    evaluacion.setActivo(Boolean.FALSE);
    service.update(evaluacion);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtener todas las entidades paginadas {@link Comentario} activas para una
   * determinada {@link Evaluacion}.
   *
   * @param id       Id de {@link Evaluacion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Comentario} paginadas.
   */
  @GetMapping("/{id}/comentarios")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  ResponseEntity<Page<Comentario>> getComentarios(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("getComentarios(Long id, Pageable pageable) - start");
    Page<Comentario> page = comentarioService.findByEvaluacionId(id, pageable);
    log.debug("getComentarios(Long id, Pageable pageable) - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene el número total de {@link Comentario} para un determinado
   * {@link Evaluacion}.
   * 
   * @param id Id de {@link Evaluacion}.
   * @return número de entidades {@link Comentario}
   */
  @GetMapping("/{id}/comentarios/count")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  ResponseEntity<Integer> countComentarios(@PathVariable Long id) {
    log.debug("countByEvaluacionId(Long id, Pageable pageable) - start");
    int count = comentarioService.countByEvaluacionId(id);
    log.debug("countByEvaluacionId(Long id, Pageable pageable) - end");
    return new ResponseEntity<>(count, HttpStatus.OK);
  }

  /**
   * Crea un nuevo listado de {@link Comentario}.
   * 
   * @param id          Id de {@link Evaluacion}.
   * @param comentarios {@link Comentario} que se quieren crear.
   * @return Nuevo listado {@link Comentario} creado.
   */
  @PostMapping("/{id}/comentarios")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  ResponseEntity<List<Comentario>> createComentarios(@PathVariable Long id,
      @Valid @RequestBody List<Comentario> comentarios) {
    log.debug("createComentarios(List<Comentario> comentarios) - start");
    List<Comentario> returnValues = comentarioService.createAll(id, comentarios);
    log.debug("createComentarios(List<Comentario> comentarios) - end");
    return new ResponseEntity<>(returnValues, HttpStatus.CREATED);
  }

  /**
   * Actualiza un listado de {@link Comentario}.
   * 
   * @param id          Id de {@link Evaluacion}.
   * @param comentarios {@link Comentario} a actualizar.
   * @return Listado de {@link Comentario} actualizado.
   */
  @PutMapping("/{id}/comentarios")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  ResponseEntity<List<Comentario>> updateComentarios(@PathVariable Long id,
      @Valid @RequestBody List<Comentario> comentarios) {
    log.debug("updateComentarios(List<Comentario> comentarios) - start");
    List<Comentario> returnValues = comentarioService.updateAll(id, comentarios);
    log.debug("updateComentarios(List<Comentario> comentarios) - end");
    return new ResponseEntity<>(returnValues, HttpStatus.CREATED);
  }

  /**
   * Elimina un listado de {@link Comentario} de una evaluación.
   * 
   * @param id  Id de {@link Evaluacion}.
   * @param ids Listado de identificadores de {@link Comentario}.
   */
  @DeleteMapping("/{id}/comentarios")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  void deleteComentarios(@PathVariable Long id, @RequestParam(value = "ids") List<Long> ids) {
    log.debug("updateComentarios(List<Comentario> comentarios) - start");
    comentarioService.deleteAll(id, ids);
    log.debug("updateComentarios(List<Comentario> comentarios) - end");
  }

  /**
   * Obtiene un listado de {@link Evaluacion} con un * determinados tipos de
   * seguimiento final
   * 
   * @param query    filtro de {@link QueryCriteria}.
   * @param pageable pageable
   */

  @GetMapping("/memorias-seguimiento-final")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-V', 'ETI-EVC-EVAL')")
  ResponseEntity<Page<Evaluacion>> findByEvaluacionesEnSeguimientoFinal(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Page<Evaluacion> page = service.findByEvaluacionesEnSeguimientoFinal(query, pageable);
    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}
