package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.Configuracion;
import org.crue.hercules.sgi.eti.service.ConfiguracionService;
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
 * ConfiguracionController
 */
@RestController
@RequestMapping("/configuraciones")
@Slf4j
public class ConfiguracionController {

  /** Configuracion service */
  private final ConfiguracionService service;

  /**
   * Instancia un nuevo ConfiguracionController.
   * 
   * @param service ConfiguracionService
   */
  public ConfiguracionController(ConfiguracionService service) {
    log.debug("ConfiguracionController(ConfiguracionService service) - start");
    this.service = service;
    log.debug("ConfiguracionController(ConfiguracionService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Configuracion}.
   * 
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<Configuracion>> findAll(@RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - start");
    Page<Configuracion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(List<QueryCriteria> query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link Configuracion}.
   * 
   * @param nuevaConfiguracion {@link Configuracion}. que se quiere crear.
   * @return Nuevo {@link Configuracion} creado.
   */
  @PostMapping
  public ResponseEntity<Configuracion> newConfiguracion(@Valid @RequestBody Configuracion nuevaConfiguracion) {
    log.debug("newConfiguracion(Configuracion nuevaConfiguracion) - start");
    Configuracion returnValue = service.create(nuevaConfiguracion);
    log.debug("newConfiguracion(Configuracion nuevaConfiguracion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Configuracion}.
   * 
   * @param updatedConfiguracion {@link Configuracion} a actualizar.
   * @param id                   id {@link Configuracion} a actualizar.
   * @return {@link Configuracion} actualizado.
   */
  @PutMapping("/{id}")
  Configuracion replaceConfiguracion(@Valid @RequestBody Configuracion updatedConfiguracion, @PathVariable Long id) {
    log.debug("replaceConfiguracion(Configuracion updatedConfiguracion, Long id) - start");
    updatedConfiguracion.setId(id);
    Configuracion returnValue = service.update(updatedConfiguracion);
    log.debug("replaceConfiguracion(Configuracion updatedConfiguracion, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Configuracion} con el id indicado.
   * 
   * @param id Identificador de {@link Configuracion}.
   * @return {@link Configuracion} correspondiente al id.
   */
  @GetMapping("/{id}")
  Configuracion one(@PathVariable Long id) {
    log.debug("Configuracion one(Long id) - start");
    Configuracion returnValue = service.findById(id);
    log.debug("Configuracion one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link Configuracion} con id indicado.
   * 
   * @param id Identificador de {@link Configuracion}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.delete(id);
    log.debug("delete(Long id) - end");
  }

}
