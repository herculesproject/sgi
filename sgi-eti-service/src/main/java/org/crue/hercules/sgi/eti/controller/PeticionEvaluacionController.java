package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.service.PeticionEvaluacionService;
import org.crue.hercules.sgi.eti.util.ConstantesEti;
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

import lombok.extern.slf4j.Slf4j;

/**
 * PeticionEvaluacionController
 */
@RestController
@RequestMapping(ConstantesEti.PETICION_EVALUACION_CONTROLLER_BASE_PATH)
@Slf4j
public class PeticionEvaluacionController {

  /** PeticionEvaluacion service */
  private final PeticionEvaluacionService service;

  /**
   * Instancia un nuevo PeticionEvaluacionController.
   * 
   * @param service PeticionEvaluacionService
   */
  public PeticionEvaluacionController(PeticionEvaluacionService service) {
    log.debug("PeticionEvaluacionController(PeticionEvaluacionService service) - start");
    this.service = service;
    log.debug("PeticionEvaluacionController(PeticionEvaluacionService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link PeticionEvaluacion}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<PeticionEvaluacion>> findAll(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Page<PeticionEvaluacion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link PeticionEvaluacion}.
   * 
   * @param nuevoPeticionEvaluacion {@link PeticionEvaluacion}. que se quiere
   *                                crear.
   * @return Nuevo {@link PeticionEvaluacion} creado.
   */
  @PostMapping
  ResponseEntity<PeticionEvaluacion> newPeticionEvaluacion(
      @Valid @RequestBody PeticionEvaluacion nuevoPeticionEvaluacion) {
    log.debug("newPeticionEvaluacion(PeticionEvaluacion nuevoPeticionEvaluacion) - start");
    PeticionEvaluacion returnValue = service.create(nuevoPeticionEvaluacion);
    log.debug("newPeticionEvaluacion(PeticionEvaluacion nuevoPeticionEvaluacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link PeticionEvaluacion}.
   * 
   * @param updatedPeticionEvaluacion {@link PeticionEvaluacion} a actualizar.
   * @param id                        id {@link PeticionEvaluacion} a actualizar.
   * @return {@link PeticionEvaluacion} actualizado.
   */
  @PutMapping(ConstantesEti.PATH_PARAMETER_ID)
  PeticionEvaluacion replacePeticionEvaluacion(@Valid @RequestBody PeticionEvaluacion updatedPeticionEvaluacion,
      @PathVariable Long id) {
    log.debug("replacePeticionEvaluacion(PeticionEvaluacion updatedPeticionEvaluacion, Long id) - start");
    updatedPeticionEvaluacion.setId(id);
    PeticionEvaluacion returnValue = service.update(updatedPeticionEvaluacion);
    log.debug("replacePeticionEvaluacion(PeticionEvaluacion updatedPeticionEvaluacion, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link PeticionEvaluacion} con el id indicado.
   * 
   * @param id Identificador de {@link PeticionEvaluacion}.
   * @return {@link PeticionEvaluacion} correspondiente al id.
   */
  @GetMapping(ConstantesEti.PATH_PARAMETER_ID)
  PeticionEvaluacion one(@PathVariable Long id) {
    log.debug("PeticionEvaluacion one(Long id) - start");
    PeticionEvaluacion returnValue = service.findById(id);
    log.debug("PeticionEvaluacion one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link PeticionEvaluacion} con id indicado.
   * 
   * @param id Identificador de {@link PeticionEvaluacion}.
   */
  @DeleteMapping(ConstantesEti.PATH_PARAMETER_ID)
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    PeticionEvaluacion peticionEvaluacion = this.one(id);
    peticionEvaluacion.setActivo(Boolean.FALSE);
    service.update(peticionEvaluacion);
    log.debug("delete(Long id) - end");
  }

}
