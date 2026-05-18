package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.GrupoDescriptorConverter;
import org.crue.hercules.sgi.csp.dto.GrupoDescriptorInput;
import org.crue.hercules.sgi.csp.dto.GrupoDescriptorOutput;
import org.crue.hercules.sgi.csp.model.GrupoDescriptor;
import org.crue.hercules.sgi.csp.service.GrupoDescriptorService;
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
 * GrupoDescriptorController
 */
@RestController
@RequestMapping(GrupoDescriptorController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class GrupoDescriptorController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "grupodescriptores";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final GrupoDescriptorService service;
  private final GrupoDescriptorConverter converter;

  /**
   * Crea un nuevo {@link GrupoDescriptor}.
   *
   * @param grupoDescriptor {@link GrupoDescriptorInput} que se quiere crear.
   * @return Nuevo {@link GrupoDescriptorOutput} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-C', 'CSP-GIN-E')")
  public ResponseEntity<GrupoDescriptorOutput> create(
      @Valid @RequestBody GrupoDescriptorInput grupoDescriptor) {
    log.debug("create - data: {}", grupoDescriptor);
    GrupoDescriptorOutput output = converter.convert(service.create(converter.convert(grupoDescriptor)));
    log.debug("create - id: {}", output.getId());
    return new ResponseEntity<>(output, HttpStatus.CREATED);
  }

  /**
   * Actualiza un {@link GrupoDescriptor}.
   *
   * @param grupoDescriptor {@link GrupoDescriptorInput} a actualizar.
   * @param id              Identificador del {@link GrupoDescriptor} a
   *                        actualizar.
   * @return {@link GrupoDescriptorOutput} actualizado.
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-GIN-E')")
  public GrupoDescriptorOutput update(@Valid @RequestBody GrupoDescriptorInput grupoDescriptor,
      @PathVariable Long id) {
    log.debug("update - id: {}, data: {}", id, grupoDescriptor);
    return converter.convert(service.update(converter.convert(id, grupoDescriptor)));
  }

  /**
   * Elimina {@link GrupoDescriptor} con el id indicado.
   *
   * @param id Identificador del {@link GrupoDescriptor} a eliminar.
   */
  @DeleteMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-GIN-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById - id: {}", id);
    service.deleteById(id);
  }

  /**
   * Devuelve el {@link GrupoDescriptor} con el id indicado.
   *
   * @param id Identificador del {@link GrupoDescriptor} a recuperar.
   * @return {@link GrupoDescriptorOutput} correspondiente al id.
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-V', 'CSP-GIN-E', 'CSP-GIN-INV-VR')")
  public GrupoDescriptorOutput findById(@PathVariable Long id) {
    log.debug("findById - id: {}", id);
    return converter.convert(service.findById(id));
  }

}
