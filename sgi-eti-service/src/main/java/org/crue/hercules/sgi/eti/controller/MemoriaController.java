package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.dto.EvaluacionWithNumComentario;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.service.DocumentacionMemoriaService;
import org.crue.hercules.sgi.eti.service.EvaluacionService;
import org.crue.hercules.sgi.eti.service.MemoriaService;
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
 * MemoriaController
 */
@RestController
@RequestMapping("/memorias")
@Slf4j
public class MemoriaController {

  /** Memoria service */
  private final MemoriaService service;

  /** Evaluacion service */
  private final EvaluacionService evaluacionService;

  /** DocumentacionMemoria service */
  private final DocumentacionMemoriaService documentacionMemoriaService;

  /**
   * Instancia un nuevo MemoriaController.
   * 
   * @param service                     MemoriaService
   * @param evaluacionService           EvaluacionService
   * @param documentacionMemoriaService documentacionMemoriaService
   */
  public MemoriaController(MemoriaService service, EvaluacionService evaluacionService,
      DocumentacionMemoriaService documentacionMemoriaService) {
    log.debug(
        "MemoriaController(MemoriaService service, EvaluacionService evaluacionService, DocumentacionMemoriaService documentacionMemoriaService) - start");
    this.service = service;
    this.evaluacionService = evaluacionService;
    this.documentacionMemoriaService = documentacionMemoriaService;
    log.debug(
        "MemoriaController(MemoriaService service, EvaluacionService evaluacionService, DocumentacionMemoriaService documentacionMemoriaService) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Memoria}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<Memoria>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Page<Memoria> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nueva {@link Memoria}.
   * 
   * @param nuevaMemoria {@link Memoria}. que se quiere crear.
   * @return Nueva {@link Memoria} creada.
   */
  @PostMapping
  public ResponseEntity<Memoria> newMemoria(@Valid @RequestBody Memoria nuevaMemoria) {
    log.debug("newMemoria(Memoria nuevaMemoria) - start");
    Memoria returnValue = service.create(nuevaMemoria);
    log.debug("newMemoria(Memoria nuevaMemoria) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Memoria}.
   * 
   * @param updatedMemoria {@link Memoria} a actualizar.
   * @param id             id {@link Memoria} a actualizar.
   * @return {@link Memoria} actualizada.
   */
  @PutMapping("/{id}")
  Memoria replaceMemoria(@Valid @RequestBody Memoria updatedMemoria, @PathVariable Long id) {
    log.debug("replaceMemoria(Memoria updatedMemoria, Long id) - start");
    updatedMemoria.setId(id);
    Memoria returnValue = service.update(updatedMemoria);
    log.debug("replaceMemoria(Memoria updatedMemoria, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Memoria} con el id indicado.
   * 
   * @param id Identificador de {@link Memoria}.
   * @return {@link Memoria} correspondiente al id.
   */
  @GetMapping("/{id}")
  Memoria one(@PathVariable Long id) {
    log.debug("Memoria one(Long id) - start");
    Memoria returnValue = service.findById(id);
    log.debug("Memoria one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link Memoria} con id indicado.
   * 
   * @param id Identificador de {@link Memoria}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    Memoria memoria = this.one(id);
    memoria.setActivo(Boolean.FALSE);
    service.update(memoria);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtener todas las entidades paginadas {@link Evaluacion} activas para una
   * determinada {@link Memoria} anterior al id de evaluación recibido.
   *
   * @param id       Id de {@link Memoria}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas.
   */
  @GetMapping("/{id}/evaluaciones-anteriores/{idEvaluacion}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR')")
  ResponseEntity<Page<EvaluacionWithNumComentario>> getEvaluaciones(@PathVariable Long id,
      @PathVariable Long idEvaluacion, @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("getEvaluaciones(Long id, Pageable pageable) - start");
    Page<EvaluacionWithNumComentario> page = evaluacionService.findEvaluacionesAnterioresByMemoria(id, idEvaluacion,
        pageable);

    if (page.isEmpty()) {
      log.debug("getEvaluaciones(Long id, Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("getEvaluaciones(Long id, Pageable pageable) - end");
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
  @GetMapping("/{id}/documentaciones")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-VR', 'ETI-EVC-EVALR')")
  ResponseEntity<Page<DocumentacionMemoria>> getDocumentaciones(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("getDocumentaciones(Long id, Pageable pageable) - start");
    Page<DocumentacionMemoria> page = documentacionMemoriaService.findByMemoriaId(id, pageable);

    if (page.isEmpty()) {
      log.debug("getDocumentaciones(Long id, Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("getDocumentaciones(Long id, Pageable pageable) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }
}
