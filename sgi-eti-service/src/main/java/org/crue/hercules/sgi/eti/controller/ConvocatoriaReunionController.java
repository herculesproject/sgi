package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.service.ConvocatoriaReunionService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

/**
 * ConvocatoriaReunionController
 */
@RestController
@RequestMapping("/convocatoriareuniones")
@Slf4j
public class ConvocatoriaReunionController {

  /** ConvocatoriaReunion service */
  private ConvocatoriaReunionService service;

  /**
   * Instancia un nuevo ConvocatoriaReunionController.
   * 
   * @param service ConvocatoriaReunionService.
   */
  public ConvocatoriaReunionController(ConvocatoriaReunionService service) {
    log.debug("ConvocatoriaReunionController(ConvocatoriaReunionService service) - start");
    this.service = service;
    log.debug("ConvocatoriaReunionController(ConvocatoriaReunionService service) - end");
  }

  /**
   * Crea {@link ConvocatoriaReunion}.
   *
   * @param convocatoriaReunion La entidad {@link ConvocatoriaReunion} a crear.
   * @return La entidad {@link ConvocatoriaReunion} creada.
   * @throws IllegalArgumentException Si la entidad {@link ConvocatoriaReunion}
   *                                  tiene id.
   * @return Nuevo {@link ConvocatoriaReunion} creado.
   */
  @PostMapping()
  public ResponseEntity<ConvocatoriaReunion> newConvocatoriaReunion(
      @Valid @RequestBody ConvocatoriaReunion convocatoriaReunion) {
    log.debug("newConvocatoriaReunion(ConvocatoriaReunion convocatoriaReunion) - start");
    ConvocatoriaReunion returnValue = service.create(convocatoriaReunion);
    log.debug("newConvocatoriaReunion(ConvocatoriaReunion convocatoriaReunion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza la entidad {@link ConvocatoriaReunion}.
   * 
   * @param convocatoriaReunion La entidad {@link ConvocatoriaReunion} a
   *                            actualizar.
   * @param id                  Identificador de la entidad
   *                            {@link ConvocatoriaReunion}.
   * @return Entidad {@link ConvocatoriaReunion} actualizada.
   * @throws NotFoundException        Si no existe ninguna entidad
   *                                  {@link ConvocatoriaReunion} con ese id.
   * @throws IllegalArgumentException Si la entidad {@link ConvocatoriaReunion} no
   *                                  tiene id.
   */
  @PutMapping("/{id}")
  ConvocatoriaReunion replaceConvocatoriaReunion(@Valid @RequestBody ConvocatoriaReunion convocatoriaReunion,
      @PathVariable Long id) {
    log.debug("replaceConvocatoriaReunion(ConvocatoriaReunion convocatoriaReunion, Long id) - start");
    convocatoriaReunion.setId(id);
    ConvocatoriaReunion returnValue = service.update(convocatoriaReunion);
    log.debug("replaceConvocatoriaReunion(ConvocatoriaReunion convocatoriaReunion, Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza {@link ConvocatoriaReunion} con el indicador de activo a false.
   *
   * @param id El id de la entidad {@link ConvocatoriaReunion}.
   * @throws NotFoundException        Si no existe ninguna entidad
   *                                  {@link ConvocatoriaReunion} con ese id.
   * @throws IllegalArgumentException Si la entidad {@link ConvocatoriaReunion} no
   *                                  tiene id.
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    ConvocatoriaReunion convocatoriaReunion = this.one(id);
    convocatoriaReunion.setActivo(Boolean.FALSE);
    service.update(convocatoriaReunion);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene las entidades {@link ConvocatoriaReunion} filtradas y paginadas según
   * los criterios de búsqueda.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   * @return el listado de entidades {@link ConvocatoriaReunion} paginadas y
   *         filtradas.
   */
  @GetMapping()
  ResponseEntity<Page<ConvocatoriaReunion>> findAll(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging - start");
    Page<ConvocatoriaReunion> page = service.findAll(query, paging);
    log.debug("findAll(List<QueryCriteria> query, Pageable paging - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene la entidad {@link ConvocatoriaReunion} por id.
   * 
   * @param id El id de la entidad {@link ConvocatoriaReunion}.
   * @return La entidad {@link ConvocatoriaReunion}.
   * @throws NotFoundException        Si no existe ninguna entidad
   *                                  {@link ConvocatoriaReunion} con ese id.
   * @throws IllegalArgumentException Si no se informa id.
   */
  @GetMapping("/{id}")
  ConvocatoriaReunion one(@PathVariable Long id) {
    log.debug("one(Long id) - start");
    ConvocatoriaReunion returnValue = service.findById(id);
    log.debug("one(Long id) - end");
    return returnValue;
  }

}
