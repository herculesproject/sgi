package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.TipoConfidencialidadConverter;
import org.crue.hercules.sgi.csp.dto.TipoConfidencialidadInput;
import org.crue.hercules.sgi.csp.dto.TipoConfidencialidadOutput;
import org.crue.hercules.sgi.csp.model.TipoConfidencialidad;
import org.crue.hercules.sgi.csp.service.TipoConfidencialidadService;
import org.crue.hercules.sgi.csp.util.SgiLogUtils;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TipoConfidencialidadController
 */
@RestController
@RequestMapping(TipoConfidencialidadController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class TipoConfidencialidadController {

  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "tiposconfidencialidad";

  public static final String PATH_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_DESACTIVAR = PATH_ID + "/desactivar";
  public static final String PATH_REACTIVAR = PATH_ID + "/reactivar";
  public static final String PATH_TODOS = PATH_DELIMITER + "todos";

  private final TipoConfidencialidadConverter converter;
  private final TipoConfidencialidadService service;

  /**
   * Crea un nuevo {@link TipoConfidencialidad}.
   *
   * @param tipoConfidencialidad {@link TipoConfidencialidadInput} que se quiere
   *                             crear.
   * @return Nuevo {@link TipoConfidencialidadOutput} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TCONF-C')")
  public ResponseEntity<TipoConfidencialidadOutput> create(
      @Valid @RequestBody TipoConfidencialidadInput tipoConfidencialidad) {
    log.debug("create - data: {}", tipoConfidencialidad);
    TipoConfidencialidadOutput output = converter.convert(service.create(converter.convert(tipoConfidencialidad)));
    log.debug("create - id: {}", output.getId());
    return new ResponseEntity<>(output, HttpStatus.CREATED);
  }

  /**
   * Actualiza un {@link TipoConfidencialidad}.
   *
   * @param tipoConfidencialidad {@link TipoConfidencialidadInput} a actualizar.
   * @param id                   Identificador del {@link TipoConfidencialidad} a
   *                             actualizar.
   * @return {@link TipoConfidencialidadOutput} actualizado.
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TCONF-E')")
  public TipoConfidencialidadOutput update(@Valid @RequestBody TipoConfidencialidadInput tipoConfidencialidad,
      @PathVariable Long id) {
    log.debug("update - id: {}, data: {}", id, tipoConfidencialidad);
    return converter.convert(service.update(converter.convert(id, tipoConfidencialidad)));
  }

  /**
   * Reactiva el {@link TipoConfidencialidad} con el id indicado.
   *
   * @param id Identificador del {@link TipoConfidencialidad} a reactivar.
   * @return {@link TipoConfidencialidadOutput} reactivado.
   */
  @PatchMapping(PATH_REACTIVAR)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TCONF-R')")
  public TipoConfidencialidadOutput reactivar(@PathVariable Long id) {
    log.debug("reactivar - id: {}", id);
    return converter.convert(service.enable(id));
  }

  /**
   * Desactiva el {@link TipoConfidencialidad} con el id indicado.
   *
   * @param id Identificador del {@link TipoConfidencialidad} a desactivar.
   * @return {@link TipoConfidencialidadOutput} desactivado.
   */
  @PatchMapping(PATH_DESACTIVAR)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TCONF-B')")
  public TipoConfidencialidadOutput desactivar(@PathVariable Long id) {
    log.debug("desactivar - id: {}", id);
    return converter.convert(service.disable(id));
  }

  /**
   * Devuelve una página de {@link TipoConfidencialidad} activos.
   *
   * @param query  filtro de búsqueda.
   * @param paging paginación.
   * @return página de {@link TipoConfidencialidadOutput} activos.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E', 'CSP-PRO-C')")
  public ResponseEntity<Page<TipoConfidencialidadOutput>> findAll(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll - query: {}, paging: {}", query, SgiLogUtils.pageable(paging));
    Page<TipoConfidencialidadOutput> page = converter.convert(service.findAll(query, paging));
    log.debug("findAll - response: {}", SgiLogUtils.page(page));
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una página de {@link TipoConfidencialidad}.
   *
   * @param query  filtro de búsqueda.
   * @param paging paginación.
   * @return página de {@link TipoConfidencialidadOutput}.
   */
  @GetMapping(PATH_TODOS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-TCONF-C', 'CSP-TCONF-E', 'CSP-TCONF-B', 'CSP-TCONF-R', 'CSP-TCONF-V')")
  public ResponseEntity<Page<TipoConfidencialidadOutput>> findAllTodos(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos - query: {}, paging: {}", query, SgiLogUtils.pageable(paging));
    Page<TipoConfidencialidadOutput> page = converter.convert(service.findAllTodos(query, paging));
    log.debug("findAllTodos - response: {}", SgiLogUtils.page(page));
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link TipoConfidencialidad} con el id indicado.
   *
   * @param id Identificador del {@link TipoConfidencialidad} a recuperar.
   * @return {@link TipoConfidencialidadOutput} correspondiente al id.
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-E', 'CSP-PRO-C')")
  public TipoConfidencialidadOutput findById(@PathVariable Long id) {
    log.debug("findById - id: {}", id);
    return converter.convert(service.findById(id));
  }

}
