package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.BloqueFormulario;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ComiteFormulario;
import org.crue.hercules.sgi.eti.service.BloqueFormularioService;
import org.crue.hercules.sgi.eti.service.ComiteFormularioService;
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
 * ComiteFormularioController
 */
@RestController
@RequestMapping("/comiteformularios")
@Slf4j
public class ComiteFormularioController {

  /** ComiteFormulario service */
  private final ComiteFormularioService service;

  /** BloqueFormulario service */
  private final BloqueFormularioService bloqueFormularioService;

  /**
   * Instancia un nuevo ComiteFormularioController.
   * 
   * @param service                 ComiteFormularioService.
   * @param bloqueFormularioService Bloque formulario service.
   */
  public ComiteFormularioController(ComiteFormularioService service, BloqueFormularioService bloqueFormularioService) {
    log.debug("ComiteFormularioController(ComiteFormularioService service) - start");
    this.service = service;
    this.bloqueFormularioService = bloqueFormularioService;
    log.debug("ComiteFormularioController(ComiteFormularioService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ComiteFormulario}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<ComiteFormulario>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Page<ComiteFormulario> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link ComiteFormulario}.
   * 
   * @param nuevoComiteFormulario {@link ComiteFormulario}. que se quiere crear.
   * @return Nuevo {@link ComiteFormulario} creado.
   */
  @PostMapping
  ResponseEntity<ComiteFormulario> newComiteFormulario(@Valid @RequestBody ComiteFormulario nuevoComiteFormulario) {
    log.debug("newComiteFormulario(ComiteFormulario nuevoComiteFormulario) - start");
    ComiteFormulario returnValue = service.create(nuevoComiteFormulario);
    log.debug("newComiteFormulario(ComiteFormulario nuevoComiteFormulario) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link ComiteFormulario}.
   * 
   * @param updatedComiteFormulario {@link ComiteFormulario} a actualizar.
   * @param id                      id {@link ComiteFormulario} a actualizar.
   * @return {@link ComiteFormulario} actualizado.
   */
  @PutMapping("/{id}")
  ComiteFormulario replaceComiteFormulario(@Valid @RequestBody ComiteFormulario updatedComiteFormulario,
      @PathVariable Long id) {
    log.debug("replaceComiteFormulario(ComiteFormulario updatedComiteFormulario, Long id) - start");
    updatedComiteFormulario.setId(id);
    ComiteFormulario returnValue = service.update(updatedComiteFormulario);
    log.debug("replaceComiteFormulario(ComiteFormulario updatedComiteFormulario, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link ComiteFormulario} con el id indicado.
   * 
   * @param id Identificador de {@link ComiteFormulario}.
   * @return {@link ComiteFormulario} correspondiente al id.
   */
  @GetMapping("/{id}")
  ComiteFormulario one(@PathVariable Long id) {
    log.debug("ComiteFormulario one(Long id) - start");
    ComiteFormulario returnValue = service.findById(id);
    log.debug("ComiteFormulario one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link ComiteFormulario} con id indicado.
   * 
   * @param id Identificador de {@link ComiteFormulario}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Recupera los datos de bloque formulario para un comité.
   * 
   * @param id     Identificador de {@link Comite} *
   * @param id     Identificador de {@link TipoEvaluacion}
   * @param paging datos de la paginación.
   * @return listado paginado de {@link BloqueFormulario}.
   */
  @GetMapping("/{id}/bloque-formularios/{idTipoEvaluacion}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  ResponseEntity<Page<BloqueFormulario>> findBloqueFormularios(@PathVariable Long id,
      @PathVariable Long idTipoEvaluacion, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findBloqueFormularios(Long id) - start");
    Page<BloqueFormulario> bloqueFormularios = bloqueFormularioService.findByComiteAndTipoEvaluacion(id, paging,
        idTipoEvaluacion);

    if (bloqueFormularios.isEmpty()) {
      log.debug("findBloqueFormularios(Long id) - start");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findBloqueFormularios(Long id) - start");
    return new ResponseEntity<>(bloqueFormularios, HttpStatus.OK);

  }
}