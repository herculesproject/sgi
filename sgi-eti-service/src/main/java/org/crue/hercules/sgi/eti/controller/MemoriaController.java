package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.dto.EvaluacionWithNumComentario;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
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
   * Devuelve una lista paginada de {@link Memoria} asignables para una
   * convocatoria determinada
   * 
   * Si la convocatoria es de tipo "Seguimiento" devuelve las memorias en estado
   * "En secretaría seguimiento anual" y "En secretaría seguimiento final" con la
   * fecha de envío es igual o menor a la fecha límite de la convocatoria de
   * reunión.
   * 
   * Si la convocatoria es de tipo "Ordinaria" o "Extraordinaria" devuelve las
   * memorias en estado "En secretaria" con la fecha de envío es igual o menor a
   * la fecha límite de la convocatoria de reunión y las que tengan una
   * retrospectiva en estado "En secretaría".
   * 
   * @param idConvocatoria identificador de la {@link ConvocatoriaReunion}.
   * @param paging         pageable
   */
  @GetMapping("/asignables/{idConvocatoria}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-E')")
  ResponseEntity<Page<Memoria>> findAllMemoriasAsignablesConvocatoria(@PathVariable Long idConvocatoria,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(Long idConvocatoria, Pageable paging) - start");
    Page<Memoria> page = service.findAllMemoriasAsignablesConvocatoria(idConvocatoria, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada con las entidades {@link Memoria}
   * asignables a una Convocatoria de tipo "Ordinaria" o "Extraordinaria".
   * 
   * Para determinar si es asignable es necesario especificar en el filtro el
   * Comité Fecha Límite de la convocatoria.
   * 
   * Si la convocatoria es de tipo "Ordinaria" o "Extraordinaria" devuelve las
   * memorias en estado "En secretaria" con la fecha de envío es igual o menor a
   * la fecha límite de la convocatoria de reunión y las que tengan una
   * retrospectiva en estado "En secretaría".
   * 
   * @param query    filtro de {@link QueryCriteria}.
   * @param pageable pageable
   */
  @GetMapping("/tipo-convocatoria-ord-ext")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-C')")
  ResponseEntity<Page<Memoria>> findAllAsignablesTipoConvocatoriaOrdExt(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("findAllAsignablesTipoConvocatoriaOrdExt(List<QueryCriteria> query,Pageable pageable) - start");
    Page<Memoria> page = service.findAllAsignablesTipoConvocatoriaOrdExt(query, pageable);

    if (page.isEmpty()) {
      log.debug("findAllAsignablesTipoConvocatoriaOrdExt(List<QueryCriteria> query,Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllAsignablesTipoConvocatoriaOrdExt(List<QueryCriteria> query,Pageable pageable) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada con las entidades {@link Memoria}
   * asignables a una Convocatoria de tipo "Seguimiento".
   * 
   * Para determinar si es asignable es necesario especificar en el filtro el
   * Comité y Fecha Límite de la convocatoria.
   * 
   * Si la convocatoria es de tipo "Seguimiento" devuelve las memorias en estado
   * "En secretaría seguimiento anual" y "En secretaría seguimiento final" con la
   * fecha de envío es igual o menor a la fecha límite de la convocatoria de
   * reunión.
   * 
   * @param query    filtro de {@link QueryCriteria}.
   * @param pageable pageable
   */
  @GetMapping("/tipo-convocatoria-seg")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-C')")
  ResponseEntity<Page<Memoria>> findAllAsignablesTipoConvocatoriaSeguimiento(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("findAllAsignablesTipoConvocatoriaSeguimiento(List<QueryCriteria> query,Pageable pageable) - start");
    Page<Memoria> page = service.findAllAsignablesTipoConvocatoriaSeguimiento(query, pageable);

    if (page.isEmpty()) {
      log.debug("findAllAsignablesTipoConvocatoriaSeguimiento(List<QueryCriteria> query,Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllAsignablesTipoConvocatoriaSeguimiento(List<QueryCriteria> query,Pageable pageable) - end");
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
