package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.exceptions.ApartadoFormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.ApartadoFormulario;
import org.crue.hercules.sgi.eti.service.ApartadoFormularioService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ApartadoFormularioController
 */
@RestController
@RequestMapping("/apartadoformularios")
@Slf4j
public class ApartadoFormularioController {

  /** ApartadoFormulario service */
  private ApartadoFormularioService service;

  /**
   * Instancia un nuevo ApartadoFormularioController.
   * 
   * @param service ApartadoFormularioService.
   */
  public ApartadoFormularioController(ApartadoFormularioService service) {
    log.debug("ApartadoFormularioController(ApartadoFormularioService service) - start");
    this.service = service;
    log.debug("ApartadoFormularioController(ApartadoFormularioService service) - end");
  }

  /**
   * Crea {@link ApartadoFormulario}.
   *
   * @param apartadoFormulario La entidad {@link ApartadoFormulario} a crear.
   * @return La entidad {@link ApartadoFormulario} creada.
   * @throws IllegalArgumentException Si la entidad {@link ApartadoFormulario}
   *                                  tiene id.
   * @return ResponseEntity<ApartadoFormulario>
   */
  @PostMapping()
  ResponseEntity<ApartadoFormulario> newApartadoFormulario(@Valid @RequestBody ApartadoFormulario apartadoFormulario) {
    log.debug("newApartadoFormulario(ApartadoFormulario apartadoFormulario) - start");
    ApartadoFormulario returnValue = service.create(apartadoFormulario);
    log.debug("newApartadoFormulario(ApartadoFormulario apartadoFormulario) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza la entidad {@link ApartadoFormulario}.
   * 
   * @param apartadoFormulario La entidad {@link ApartadoFormulario} a actualizar.
   * @param id                 Identificador de la entidad
   *                           {@link ApartadoFormulario}.
   * @return Entidad {@link ApartadoFormulario} actualizada.
   * @throws ApartadoFormularioNotFoundException Si no existe ninguna entidad
   *                                             {@link ApartadoFormulario} con
   *                                             ese id.
   * @throws IllegalArgumentException            Si la entidad
   *                                             {@link ApartadoFormulario} no
   *                                             tiene id.
   */
  @PutMapping("/{id}")
  ApartadoFormulario replaceApartadoFormulario(@Valid @RequestBody ApartadoFormulario apartadoFormulario,
      @PathVariable Long id) {
    log.debug("replaceApartadoFormulario(ApartadoFormulario apartadoFormulario, Long id) - start");
    apartadoFormulario.setId(id);
    ApartadoFormulario returnValue = service.update(apartadoFormulario);
    log.debug("replaceApartadoFormulario(ApartadoFormulario apartadoFormulario, Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza {@link ApartadoFormulario} con el indicador de activo a false.
   *
   * @param id El id de la entidad {@link ApartadoFormulario}.
   * @throws ApartadoFormularioNotFoundException Si no existe ninguna entidad
   *                                             {@link ApartadoFormulario} con
   *                                             ese id.
   * @throws IllegalArgumentException            Si la entidad
   *                                             {@link ApartadoFormulario} no
   *                                             tiene id.
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    ApartadoFormulario apartadoFormulario = this.one(id);
    apartadoFormulario.setActivo(Boolean.FALSE);
    service.update(apartadoFormulario);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene las entidades {@link ApartadoFormulario} filtradas y paginadas según
   * los criterios de búsqueda.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   * @return el listado de entidades {@link ApartadoFormulario} paginadas y
   *         filtradas.
   */
  @GetMapping()
  ResponseEntity<Page<ApartadoFormulario>> findAll(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging - start");
    Page<ApartadoFormulario> page = service.findAll(query, paging);
    log.debug("findAll(List<QueryCriteria> query, Pageable paging - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene la entidad {@link ApartadoFormulario} por id.
   * 
   * @param id El id de la entidad {@link ApartadoFormulario}.
   * @return La entidad {@link ApartadoFormulario}.
   * @throws ApartadoFormularioNotFoundException Si no existe ninguna entidad
   *                                             {@link ApartadoFormulario} con
   *                                             ese id.
   * @throws IllegalArgumentException            Si no se informa id.
   */
  @GetMapping("/{id}")
  ApartadoFormulario one(@PathVariable Long id) {
    log.debug("one(Long id) - start");
    ApartadoFormulario returnValue = service.findById(id);
    log.debug("one(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene las entidades {@link ApartadoFormulario} por el id de su padre
   * {@link ApartadoFormulario}.
   * 
   * @param id     El id de la entidad {@link ApartadoFormulario}.
   * @param paging pageable
   * @return el listado de entidades {@link ApartadoFormulario} paginadas y
   *         filtradas.
   */
  @GetMapping("/{id}/hijos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR')")
  ResponseEntity<Page<ApartadoFormulario>> getHijos(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("getHijos(Long id, Pageable paging - start");
    Page<ApartadoFormulario> page = service.findByApartadoFormularioPadreId(id, paging);
    log.debug("getHijos(Long id, Pageable paging - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }
}
