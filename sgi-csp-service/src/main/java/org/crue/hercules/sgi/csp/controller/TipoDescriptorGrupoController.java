package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.TipoDescriptorGrupoConverter;
import org.crue.hercules.sgi.csp.dto.TipoDescriptorGrupoInput;
import org.crue.hercules.sgi.csp.dto.TipoDescriptorGrupoOutput;
import org.crue.hercules.sgi.csp.model.TipoDescriptorGrupo;
import org.crue.hercules.sgi.csp.service.TipoDescriptorGrupoService;
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
 * TipoDescriptorGrupoController
 */
@RestController
@RequestMapping(TipoDescriptorGrupoController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class TipoDescriptorGrupoController {

  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "tiposdescriptoresgrupo";

  public static final String PATH_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_DESACTIVAR = PATH_ID + "/desactivar";
  public static final String PATH_REACTIVAR = PATH_ID + "/reactivar";
  public static final String PATH_TODOS = PATH_DELIMITER + "todos";

  private final TipoDescriptorGrupoConverter converter;
  private final TipoDescriptorGrupoService service;

  /**
   * Crea un nuevo {@link TipoDescriptorGrupo}.
   *
   * @param tipoDescriptorGrupo {@link TipoDescriptorGrupoInput} que se quiere
   *                            crear.
   * @return Nuevo {@link TipoDescriptorGrupoOutput} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TDESG-C')")
  public ResponseEntity<TipoDescriptorGrupoOutput> create(
      @Valid @RequestBody TipoDescriptorGrupoInput tipoDescriptorGrupo) {
    log.debug("create - data: {}", tipoDescriptorGrupo);
    TipoDescriptorGrupoOutput output = converter.convert(service.create(converter.convert(tipoDescriptorGrupo)));
    log.debug("create - id: {}", output.getId());
    return new ResponseEntity<>(output, HttpStatus.CREATED);
  }

  /**
   * Actualiza un {@link TipoDescriptorGrupo}.
   *
   * @param tipoDescriptorGrupo {@link TipoDescriptorGrupoInput} a actualizar.
   * @param id                  Identificador del {@link TipoDescriptorGrupo} a
   *                            actualizar.
   * @return {@link TipoDescriptorGrupoOutput} actualizado.
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TDESG-E')")
  public TipoDescriptorGrupoOutput update(@Valid @RequestBody TipoDescriptorGrupoInput tipoDescriptorGrupo,
      @PathVariable Long id) {
    log.debug("update - id: {}, data: {}", id, tipoDescriptorGrupo);
    return converter.convert(service.update(converter.convert(id, tipoDescriptorGrupo)));
  }

  /**
   * Reactiva el {@link TipoDescriptorGrupo} con el id indicado.
   *
   * @param id Identificador del {@link TipoDescriptorGrupo} a reactivar.
   * @return {@link TipoDescriptorGrupoOutput} reactivado.
   */
  @PatchMapping(PATH_REACTIVAR)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TDESG-R')")
  public TipoDescriptorGrupoOutput reactivar(@PathVariable Long id) {
    log.debug("reactivar - id: {}", id);
    return converter.convert(service.enable(id));
  }

  /**
   * Desactiva el {@link TipoDescriptorGrupo} con el id indicado.
   *
   * @param id Identificador del {@link TipoDescriptorGrupo} a desactivar.
   * @return {@link TipoDescriptorGrupoOutput} desactivado.
   */
  @PatchMapping(PATH_DESACTIVAR)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TDESG-B')")
  public TipoDescriptorGrupoOutput desactivar(@PathVariable Long id) {
    log.debug("desactivar - id: {}", id);
    return converter.convert(service.disable(id));
  }

  /**
   * Devuelve una página de {@link TipoDescriptorGrupo} activos.
   *
   * @param query  filtro de búsqueda.
   * @param paging paginación.
   * @return página de {@link TipoDescriptorGrupoOutput} activos.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-TDESG-C', 'CSP-TDESG-E', 'CSP-TDESG-B', 'CSP-TDESG-R', 'CSP-TDESG-V', 'CSP-GIN-V', 'CSP-GIN-E', 'CSP-GIN-INV-VR')")
  public ResponseEntity<Page<TipoDescriptorGrupoOutput>> findAll(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll - query: {}, paging: {}", query, SgiLogUtils.pageable(paging));
    Page<TipoDescriptorGrupo> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll - response: {}", SgiLogUtils.page(page));
    return new ResponseEntity<>(converter.convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve una página de {@link TipoDescriptorGrupo} independientemente de su
   * estado.
   *
   * @param query  filtro de búsqueda.
   * @param paging paginación.
   * @return página de {@link TipoDescriptorGrupoOutput}.
   */
  @GetMapping(PATH_TODOS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-TDESG-C', 'CSP-TDESG-E', 'CSP-TDESG-B', 'CSP-TDESG-R', 'CSP-TDESG-V')")
  public ResponseEntity<Page<TipoDescriptorGrupoOutput>> findAllTodos(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos - query: {}, paging: {}", query, SgiLogUtils.pageable(paging));
    Page<TipoDescriptorGrupo> page = service.findAllTodos(query, paging);

    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllTodos - response: {}", SgiLogUtils.page(page));
    return new ResponseEntity<>(converter.convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve el {@link TipoDescriptorGrupo} con el id indicado.
   *
   * @param id Identificador del {@link TipoDescriptorGrupo} a recuperar.
   * @return {@link TipoDescriptorGrupoOutput} correspondiente al id.
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-TDESG-C', 'CSP-TDESG-E', 'CSP-TDESG-B', 'CSP-TDESG-R', 'CSP-TDESG-V', 'CSP-GIN-V', 'CSP-GIN-E', 'CSP-GIN-INV-VR')")
  public TipoDescriptorGrupoOutput findById(@PathVariable Long id) {
    log.debug("findById - id: {}", id);
    return converter.convert(service.findById(id));
  }

}
