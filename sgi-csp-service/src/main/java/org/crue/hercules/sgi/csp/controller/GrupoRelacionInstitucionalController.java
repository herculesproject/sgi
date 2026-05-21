package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.GrupoRelacionInstitucionalConverter;
import org.crue.hercules.sgi.csp.dto.GrupoRelacionInstitucionalInput;
import org.crue.hercules.sgi.csp.dto.GrupoRelacionInstitucionalOutput;
import org.crue.hercules.sgi.csp.model.GrupoRelacionInstitucional;
import org.crue.hercules.sgi.csp.service.GrupoRelacionInstitucionalService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * GrupoRelacionInstitucionalController
 */
@RestController
@RequestMapping(GrupoRelacionInstitucionalController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class GrupoRelacionInstitucionalController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "gruporelacionesinstitucionales";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final GrupoRelacionInstitucionalService service;
  private final GrupoRelacionInstitucionalConverter converter;

  /**
   * Crea nuevo {@link GrupoRelacionInstitucional}
   *
   * @param grupoRelacionInstitucional {@link GrupoRelacionInstitucional} que se
   *                                   quiere crear.
   * @return Nuevo {@link GrupoRelacionInstitucional} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-C', 'CSP-GIN-E')")
  public ResponseEntity<GrupoRelacionInstitucionalOutput> create(
      @Valid @RequestBody GrupoRelacionInstitucionalInput grupoRelacionInstitucional) {
    log.debug("create - data: {}", grupoRelacionInstitucional);
    GrupoRelacionInstitucionalOutput output = converter
        .convert(service.create(converter.convert(grupoRelacionInstitucional)));
    log.debug("create - id: {}", output.getId());
    return new ResponseEntity<>(output, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link GrupoRelacionInstitucional}.
   *
   * @param grupoRelacionInstitucional {@link GrupoRelacionInstitucional} a
   *                                   actualizar.
   * @param id                         Identificador
   *                                   {@link GrupoRelacionInstitucional} a
   *                                   actualizar.
   * @return {@link GrupoRelacionInstitucional} actualizado
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-GIN-E')")
  public GrupoRelacionInstitucionalOutput update(
      @Valid @RequestBody GrupoRelacionInstitucionalInput grupoRelacionInstitucional,
      @PathVariable Long id) {
    log.debug("update - id: {}, data: {}", id, grupoRelacionInstitucional);
    return converter.convert(service.update(converter.convert(id, grupoRelacionInstitucional)));
  }

  /**
   * Elimina {@link GrupoRelacionInstitucional} con id indicado.
   *
   * @param id Identificador de {@link GrupoRelacionInstitucional}.
   */
  @DeleteMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-GIN-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById - id: {}", id);
    service.deleteById(id);
  }

  /**
   * Devuelve el {@link GrupoRelacionInstitucional} con el id indicado.
   *
   * @param id Identificador de {@link GrupoRelacionInstitucional}.
   * @return {@link GrupoRelacionInstitucional} correspondiente al id
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public GrupoRelacionInstitucionalOutput findById(@PathVariable Long id) {
    log.debug("findById - id: {}", id);
    return converter.convert(service.findById(id));
  }

}
