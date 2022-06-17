package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.SolicitudRrhhConverter;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhInput;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhOutput;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhTutorInput;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhTutorOutput;
import org.crue.hercules.sgi.csp.model.SolicitudRrhh;
import org.crue.hercules.sgi.csp.service.SolicitudRrhhService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SolicitudRrhhController
 */
@RestController
@RequestMapping(SolicitudRrhhController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
@Validated
public class SolicitudRrhhController {
  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "solicitudes-rrhh";
  public static final String PATH_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_TUTOR = PATH_ID + PATH_DELIMITER + "tutor";

  private final SolicitudRrhhService service;
  private final SolicitudRrhhConverter converter;

  /**
   * Crea nuevo {@link SolicitudRrhh}
   * 
   * @param solicitudRrhh {@link SolicitudRrhh} que se quiere crear.
   * @return Nuevo {@link SolicitudRrhh} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  public ResponseEntity<SolicitudRrhhOutput> create(
      @Valid @RequestBody SolicitudRrhhInput solicitudRrhh) {
    log.debug("create(SolicitudRrhhInput solicitudRrhh) - start");
    SolicitudRrhhOutput returnValue = converter.convert(service.create(converter.convert(solicitudRrhh)));
    log.debug("create(SolicitudRrhhInput solicitudRrhh) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link SolicitudRrhh}.
   * 
   * @param solicitudRrhh {@link SolicitudRrhh} a actualizar.
   * @param id            Identificador {@link SolicitudRrhh} a actualizar.
   * @return {@link SolicitudRrhh} actualizado
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  public SolicitudRrhhOutput update(@Valid @RequestBody SolicitudRrhhInput solicitudRrhh,
      @PathVariable Long id) {
    log.debug("update(SolicitudRrhhInput solicitudRrhh, Long id) - start");
    SolicitudRrhhOutput returnValue = converter.convert(service.update(converter.convert(id, solicitudRrhh)));
    log.debug("update(SolicitudRrhhInput solicitudRrhh, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link SolicitudRrhh} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudRrhh}.
   * @return {@link SolicitudRrhh} correspondiente al id
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-ER')")
  public SolicitudRrhhOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    SolicitudRrhhOutput returnValue = converter.convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link SolicitudRrhhTutorOutput} de {@link SolicitudRrhh} con el
   * id indicado.
   * 
   * @param id Identificador de {@link SolicitudRrhh}.
   * @return {@link SolicitudRrhhTutorOutput} correspondiente al id
   */
  @GetMapping(PATH_TUTOR)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-ER')")
  public SolicitudRrhhTutorOutput findTutorBySolicitudRrhhId(@PathVariable Long id) {
    log.debug("findTutorBySolicitudRrhhId(Long id) - start");
    SolicitudRrhhTutorOutput returnValue = converter.convertRrhhTutorOutput(service.findById(id));
    log.debug("findTutorBySolicitudRrhhId(Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza el tutor de la {@link SolicitudRrhh} con id indicado.
   * 
   * @param id    Identificador de {@link SolicitudRrhh}.
   * @param tutor el {@link SolicitudRrhhTutorInput}.
   * @return {@link SolicitudRrhhTutorOutput} actualizado.
   */
  @PatchMapping(PATH_TUTOR)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  public SolicitudRrhhTutorOutput updateTutor(@PathVariable Long id, @RequestBody SolicitudRrhhTutorInput tutor) {
    log.debug("updateTutor(Long id, SolicitudRrhhTutorInput tutor) - start");

    SolicitudRrhhTutorOutput returnValue = converter
        .convertRrhhTutorOutput(service.updateTutor(converter.convert(id, tutor)));
    log.debug("updateTutor(Long id, SolicitudRrhhTutorInput tutor) - end");
    return returnValue;
  }

}
