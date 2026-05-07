package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.TipoGrupoConverter;
import org.crue.hercules.sgi.csp.dto.TipoGrupoInput;
import org.crue.hercules.sgi.csp.dto.TipoGrupoOutput;
import org.crue.hercules.sgi.csp.model.TipoGrupo;
import org.crue.hercules.sgi.csp.service.TipoGrupoService;
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

@RestController
@RequestMapping(TipoGrupoController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class TipoGrupoController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "tiposgrupo";

  public static final String PATH_TODOS = PATH_DELIMITER + "todos";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_REACTIVAR = PATH_ID + "/reactivar";
  public static final String PATH_DESACTIVAR = PATH_ID + "/desactivar";

  private final TipoGrupoConverter converter;
  private final TipoGrupoService service;

  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TGIN-C')")
  public ResponseEntity<TipoGrupoOutput> create(@Valid @RequestBody TipoGrupoInput tipoGrupo) {
    log.debug("create({}) - start", tipoGrupo);
    TipoGrupo returnValue = service.create(converter.convert(tipoGrupo));
    log.debug("create({}) - end", tipoGrupo);
    return new ResponseEntity<>(converter.convert(returnValue), HttpStatus.CREATED);
  }

  @PutMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TGIN-E')")
  public TipoGrupoOutput update(@Valid @RequestBody TipoGrupoInput tipoGrupo, @PathVariable Long id) {
    log.debug("update({}, {}) - start", tipoGrupo, id);
    TipoGrupoOutput returnValue = converter.convert(service.update(converter.convert(id, tipoGrupo)));
    log.debug("update({}, {} - end", tipoGrupo, id);
    return returnValue;
  }

  @PatchMapping(PATH_REACTIVAR)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TGIN-R')")
  public TipoGrupoOutput reactivar(@PathVariable Long id) {
    log.debug("reactivar({}) - start", id);
    TipoGrupoOutput returnValue = converter.convert(service.enable(id));
    log.debug("reactivar({}) - end", id);
    return returnValue;
  }

  @PatchMapping(PATH_DESACTIVAR)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TGIN-B')")
  public TipoGrupoOutput desactivar(@PathVariable Long id) {
    log.debug("desactivar({}) - start", id);
    TipoGrupoOutput returnValue = converter.convert(service.disable(id));
    log.debug("desactivar({}) - end", id);
    return returnValue;
  }

  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-TGIN-V', 'CSP-TGIN-C', 'CSP-TGIN-E', 'CSP-TGIN-B', 'CSP-GIN-E', 'CSP-GIN-V')")
  public ResponseEntity<Page<TipoGrupoOutput>> findAll(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll({}, {}) - start", query, paging);
    Page<TipoGrupoOutput> page = converter.convert(service.findAll(query, paging));
    log.debug("findAll({}, {}) - end", query, paging);
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  @GetMapping(PATH_TODOS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-TGIN-V', 'CSP-TGIN-C', 'CSP-TGIN-E', 'CSP-TGIN-B', 'CSP-TGIN-R')")
  public ResponseEntity<Page<TipoGrupoOutput>> findAllTodos(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos({}, {}) - start");
    Page<TipoGrupoOutput> page = converter.convert(service.findAllTodos(query, paging));
    log.debug("findAllTodos({}, {}) - end", query, paging);
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-TGIN-V', 'CSP-TGIN-E', 'CSP-GIN-E', 'CSP-GIN-V')")
  public TipoGrupoOutput findById(@PathVariable Long id) {
    log.debug("findById({}) - start", id);
    TipoGrupoOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById({}) - end", id);
    return returnValue;
  }

}
