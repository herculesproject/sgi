package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.BloqueFormulario;
import org.crue.hercules.sgi.eti.service.BloqueFormularioService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import lombok.extern.slf4j.Slf4j;

/**
 * BloqueFormularioController
 */
@RestController
@RequestMapping("/bloqueformularios")
@Slf4j
public class BloqueFormularioController {

  /** BloqueFormulario service */
  private final BloqueFormularioService service;

  /**
   * Instancia un nuevo BloqueFormularioController.
   * 
   * @param service BloqueFormularioService
   */
  public BloqueFormularioController(BloqueFormularioService service) {
    log.debug("BloqueFormularioController(BloqueFormularioService service) - start");
    this.service = service;
    log.debug("BloqueFormularioController(BloqueFormularioService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link BloqueFormulario}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping()
  @PreAuthorize("hasAuthorityForAnyUO('ETI-BLOQUEFORMULARIO-VER')")
  ResponseEntity<Page<BloqueFormulario>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Page<BloqueFormulario> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link BloqueFormulario}.
   * 
   * @param nuevoBloqueFormulario {@link BloqueFormulario}. que se quiere crear.
   * @return Nuevo {@link BloqueFormulario} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('ETI-BLOQUEFORMULARIO-EDITAR')")
  public ResponseEntity<BloqueFormulario> newBloqueFormulario(
      @Valid @RequestBody BloqueFormulario nuevoBloqueFormulario) {
    log.debug("newBloqueFormulario(BloqueFormulario nuevoBloqueFormulario) - start");
    BloqueFormulario returnValue = service.create(nuevoBloqueFormulario);
    log.debug("newBloqueFormulario(BloqueFormulario nuevoBloqueFormulario) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link BloqueFormulario}.
   * 
   * @param updatedBloqueFormulario {@link BloqueFormulario} a actualizar.
   * @param id                      id {@link BloqueFormulario} a actualizar.
   * @return {@link BloqueFormulario} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-BLOQUEFORMULARIO-EDITAR')")
  BloqueFormulario replaceBloqueFormulario(@Valid @RequestBody BloqueFormulario updatedBloqueFormulario,
      @PathVariable Long id) {
    log.debug("replaceBloqueFormulario(BloqueFormulario updatedBloqueFormulario, Long id) - start");
    updatedBloqueFormulario.setId(id);
    BloqueFormulario returnValue = service.update(updatedBloqueFormulario);
    log.debug("replaceBloqueFormulario(BloqueFormulario updatedBloqueFormulario, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link BloqueFormulario} con el id indicado.
   * 
   * @param id Identificador de {@link BloqueFormulario}.
   * @return {@link BloqueFormulario} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-BLOQUEFORMULARIO-VER')")
  BloqueFormulario one(@PathVariable Long id) {
    log.debug("BloqueFormulario one(Long id) - start");
    BloqueFormulario returnValue = service.findById(id);
    log.debug("BloqueFormulario one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link BloqueFormulario} con id indicado.
   * 
   * @param id Identificador de {@link BloqueFormulario}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-BLOQUEFORMULARIO-EDITAR')")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    BloqueFormulario bloqueFormulario = this.one(id);
    bloqueFormulario.setActivo(Boolean.FALSE);
    service.update(bloqueFormulario);
    log.debug("delete(Long id) - end");
  }

}
