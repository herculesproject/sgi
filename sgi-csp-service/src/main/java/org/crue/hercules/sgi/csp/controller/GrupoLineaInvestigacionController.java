package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.GrupoLineaInvestigacionConverter;
import org.crue.hercules.sgi.csp.dto.GrupoLineaInvestigacionInput;
import org.crue.hercules.sgi.csp.dto.GrupoLineaInvestigacionOutput;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion;
import org.crue.hercules.sgi.csp.service.GrupoLineaInvestigacionService;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * GrupoLineaInvestigacionController
 */
@RestController
@RequestMapping(GrupoLineaInvestigacionController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class GrupoLineaInvestigacionController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "gruposlineasinvestigacion";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final GrupoLineaInvestigacionService service;
  private final GrupoLineaInvestigacionConverter converter;

  /**
   * Crea nuevo {@link GrupoLineaInvestigacion}
   * 
   * @param grupoLineaInvestigacion {@link GrupoLineaInvestigacion} que se
   *                                quiere crear.
   * @return Nuevo {@link GrupoLineaInvestigacion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-GIN-E')")
  public ResponseEntity<GrupoLineaInvestigacionOutput> create(
      @Valid @RequestBody GrupoLineaInvestigacionInput grupoLineaInvestigacion) {
    log.debug("create(GrupoLineaInvestigacionInput grupoLineaInvestigacion) - start");
    GrupoLineaInvestigacionOutput returnValue = converter
        .convert(service.create(converter.convert(grupoLineaInvestigacion)));
    log.debug("create(GrupoLineaInvestigacionInput grupoLineaInvestigacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link GrupoLineaInvestigacion}.
   * 
   * @param grupo {@link GrupoLineaInvestigacion} a actualizar.
   * @param id    Identificador {@link GrupoLineaInvestigacion} a actualizar.
   * @return {@link GrupoLineaInvestigacion} actualizado
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-GIN-E')")
  public GrupoLineaInvestigacionOutput update(@Valid @RequestBody GrupoLineaInvestigacionInput grupo,
      @PathVariable Long id) {
    log.debug("update(GrupoLineaInvestigacionInput grupoLineaInvestigacion, Long id) - start");
    GrupoLineaInvestigacionOutput returnValue = converter.convert(service.update(converter.convert(id, grupo)));
    log.debug("update(GrupoLineaInvestigacionInput grupoLineaInvestigacion, Long id) - end");
    return returnValue;
  }

  /**
   * Elimina el {@link GrupoLineaInvestigacion} con id indicado.
   * 
   * @param id Identificador de {@link GrupoLineaInvestigacion}.
   */
  @DeleteMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-GIN-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve el {@link GrupoLineaInvestigacion} con el id indicado.
   * 
   * @param id Identificador de {@link GrupoLineaInvestigacion}.
   * @return {@link GrupoLineaInvestigacion} correspondiente al id
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public GrupoLineaInvestigacionOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    GrupoLineaInvestigacionOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link GrupoLineaInvestigacion} con el id
   * indicado.
   *
   * @param id Identificador de {@link GrupoLineaInvestigacion}.
   * @return {@link HttpStatus#OK} si existe y {@link HttpStatus#NO_CONTENT} si
   *         no.
   */
  @RequestMapping(path = PATH_ID, method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public ResponseEntity<Void> exists(@PathVariable Long id) {
    log.debug("GrupoLineaInvestigacion exists(Long id) - start");
    boolean exists = service.existsById(id);
    log.debug("GrupoLineaInvestigacion exists(Long id) - end");
    return exists ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Hace las comprobaciones necesarias para determinar si el
   * {@link GrupoLineaInvestigacion} puede ser modificado.
   * 
   * @param id Id del {@link GrupoLineaInvestigacion}.
   * @return HTTP-200 Si se permite modificación / HTTP-204 Si no se permite
   *         modificación
   */
  @RequestMapping(path = "/{id}/modificable", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-V', 'CSP-GIN-E', 'CSP-GIN-B')")
  public ResponseEntity<Void> modificable(@PathVariable Long id) {
    log.debug("modificable(Long id) - start");
    boolean returnValue = service.modificable();
    log.debug("modificable(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
