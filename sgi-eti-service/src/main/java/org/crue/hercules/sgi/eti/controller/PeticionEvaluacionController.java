package org.crue.hercules.sgi.eti.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.service.EquipoTrabajoService;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.crue.hercules.sgi.eti.service.PeticionEvaluacionService;
import org.crue.hercules.sgi.eti.service.TareaService;
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
 * PeticionEvaluacionController
 */
@RestController
@RequestMapping("/peticionevaluaciones")
@Slf4j
public class PeticionEvaluacionController {

  /** EquipoTrabajo service */
  private final EquipoTrabajoService equipoTrabajoService;

  /** Memoria service */
  private final MemoriaService memoriaService;

  /** PeticionEvaluacion service */
  private final PeticionEvaluacionService service;

  /** Tarea service */
  private final TareaService tareaService;

  /**
   * Instancia un nuevo PeticionEvaluacionController.
   * 
   * @param service              PeticionEvaluacionService
   * @param equipoTrabajoService EquipoTrabajoService
   * @param memoriaService       MemoriaService
   * @param tareaService         TareaService
   */
  public PeticionEvaluacionController(PeticionEvaluacionService service, EquipoTrabajoService equipoTrabajoService,
      MemoriaService memoriaService, TareaService tareaService) {
    log.debug(
        "PeticionEvaluacionController(PeticionEvaluacionService service, EquipoTrabajoService equipoTrabajoService, MemoriaService memoriaService, TareaService tareaService) - start");
    this.service = service;
    this.equipoTrabajoService = equipoTrabajoService;
    this.memoriaService = memoriaService;
    this.tareaService = tareaService;
    log.debug(
        "PeticionEvaluacionController(PeticionEvaluacionService service, EquipoTrabajoService equipoTrabajoService, MemoriaService memoriaService, TareaService tareaService)) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link PeticionEvaluacion}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-VR-INV', 'ETI-PEV-V')")
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-C-INV')")
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
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-ER-INV')")
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
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-ER-INV', 'ETI-PEV-V')")
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
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-BR-INV')")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    PeticionEvaluacion peticionEvaluacion = this.one(id);
    peticionEvaluacion.setActivo(Boolean.FALSE);
    service.update(peticionEvaluacion);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtener todas las entidades paginadas {@link EquipoTrabajo} para una
   * determinada {@link PeticionEvaluacion}.
   *
   * @param id       Id de {@link PeticionEvaluacion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link EquipoTrabajo} paginadas.
   */
  @GetMapping("/{id}/equipo-investigador")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-V', 'ETI-PEV-VR-INV', 'ETI-PEV-C-INV', 'ETI-PEV-ER-INV')")
  ResponseEntity<Page<EquipoTrabajo>> findEquipoInvestigador(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("findEquipoInvestigador(Long id, Pageable pageable) - start");
    List<EquipoTrabajo> equipoTrabajosNoEliminables = tareaService.findAllTareasNoEliminablesPeticionEvaluacion(id)
        .stream().map(Tarea::getEquipoTrabajo).collect(Collectors.toList());

    Page<EquipoTrabajo> page = equipoTrabajoService.findAllByPeticionEvaluacionId(id, pageable).map(equipoTrabajo -> {
      boolean isEliminable = !equipoTrabajosNoEliminables.stream()
          .anyMatch(equipoTrabajoEliminable -> equipoTrabajoEliminable.getId().equals(equipoTrabajo.getId()));
      equipoTrabajo.setEliminable(isEliminable);
      return equipoTrabajo;
    });

    if (page.isEmpty()) {
      log.debug("findEquipoInvestigador(Long id, Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findEquipoInvestigador(Long id, Pageable pageable) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtener todas las entidades paginadas {@link EquipoTrabajo} para una
   * determinada {@link PeticionEvaluacion}.
   *
   * @param id       Id de {@link PeticionEvaluacion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Memoria} paginadas.
   */
  @GetMapping("/{id}/memorias")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-C-INV', 'ETI-PEV-ER-INV')")
  ResponseEntity<Page<MemoriaPeticionEvaluacion>> findMemorias(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("findMemorias(Long id, Pageable pageable) - start");

    Page<MemoriaPeticionEvaluacion> page = memoriaService.findMemoriaByPeticionEvaluacionMaxVersion(id, pageable);

    if (page.isEmpty()) {
      log.debug("findMemorias(Long id, Pageable pageable) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findMemorias(Long id, Pageable pageable) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}
