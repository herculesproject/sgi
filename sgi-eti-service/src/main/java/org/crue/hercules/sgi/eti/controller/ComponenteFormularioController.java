package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.ComponenteFormulario;
import org.crue.hercules.sgi.eti.service.ComponenteFormularioService;
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
 * ComponenteFormularioController
 */
@RestController
@RequestMapping("/componenteformularios")
@Slf4j
public class ComponenteFormularioController {

  /** ComponenteFormulario service */
  private ComponenteFormularioService service;

  /**
   * Instancia un nuevo ComponenteFormularioController.
   * 
   * @param service ComponenteFormularioService.
   */
  public ComponenteFormularioController(ComponenteFormularioService service) {
    log.debug("ComponenteFormularioController(ComponenteFormularioService service) - start");
    this.service = service;
    log.debug("ComponenteFormularioController(ComponenteFormularioService service) - end");
  }

  /**
   * Crea {@link ComponenteFormulario}.
   *
   * @param componenteFormulario La entidad {@link ComponenteFormulario} a crear.
   * @return La entidad {@link ComponenteFormulario} creada.
   * @throws IllegalArgumentException Si la entidad {@link ComponenteFormulario}
   *                                  tiene id.
   * @return ResponseEntity<ComponenteFormulario>
   */
  @PostMapping()
  ResponseEntity<ComponenteFormulario> newComponenteFormulario(
      @Valid @RequestBody ComponenteFormulario componenteFormulario) {
    log.debug("newComponenteFormulario(ComponenteFormulario componenteFormulario) - start");
    ComponenteFormulario returnValue = service.create(componenteFormulario);
    log.debug("newComponenteFormulario(ComponenteFormulario componenteFormulario) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza la entidad {@link ComponenteFormulario}.
   * 
   * @param componenteFormulario La entidad {@link ComponenteFormulario} a
   *                             actualizar.
   * @param id                   Identificador de la entidad
   *                             {@link ComponenteFormulario}.
   * @return Entidad {@link ComponenteFormulario} actualizada.
   * @throws ComponenteFormularioNotFoundException Si no existe ninguna entidad
   *                                               {@link ComponenteFormulario}
   *                                               con ese id.
   * @throws IllegalArgumentException              Si la entidad
   *                                               {@link ComponenteFormulario} no
   *                                               tiene id.
   */
  @PutMapping("/{id}")
  ComponenteFormulario replaceComponenteFormulario(@Valid @RequestBody ComponenteFormulario componenteFormulario,
      @PathVariable Long id) {
    log.debug("replaceComponenteFormulario(ComponenteFormulario componenteFormulario, Long id) - start");
    componenteFormulario.setId(id);
    ComponenteFormulario returnValue = service.update(componenteFormulario);
    log.debug("replaceComponenteFormulario(ComponenteFormulario componenteFormulario, Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link ComponenteFormulario} por id.
   *
   * @param id El id de la entidad {@link ComponenteFormulario}.
   * @throws ComponenteFormularioNotFoundException Si no existe ninguna entidad
   *                                               {@link ComponenteFormulario}
   *                                               con ese id.
   * @throws IllegalArgumentException              Si la entidad
   *                                               {@link ComponenteFormulario} no
   *                                               tiene id.
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.delete(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene las entidades {@link ComponenteFormulario} filtradas y paginadas
   * según los criterios de búsqueda.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   * @return el listado de entidades {@link ComponenteFormulario} paginadas y
   *         filtradas.
   */
  @GetMapping()
  ResponseEntity<Page<ComponenteFormulario>> findAll(
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging - start");
    Page<ComponenteFormulario> page = service.findAll(query, paging);
    log.debug("findAll(List<QueryCriteria> query, Pageable paging - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene la entidad {@link ComponenteFormulario} por id.
   * 
   * @param id El id de la entidad {@link ComponenteFormulario}.
   * @return La entidad {@link ComponenteFormulario}.
   * @throws ComponenteFormularioNotFoundException Si no existe ninguna entidad
   *                                               {@link ComponenteFormulario}
   *                                               con ese id.
   * @throws IllegalArgumentException              Si no se informa id.
   */
  @GetMapping("/{id}")
  private ComponenteFormulario one(@PathVariable Long id) {
    log.debug("one(Long id) - start");
    ComponenteFormulario returnValue = service.findById(id);
    log.debug("one(Long id) - end");
    return returnValue;
  }

}
