package org.crue.hercules.sgi.pii.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.pii.dto.SolicitudProteccionInput;
import org.crue.hercules.sgi.pii.dto.SolicitudProteccionOutput;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.crue.hercules.sgi.pii.service.SolicitudProteccionService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("solicitudesproteccion")
public class SolicitudProteccionController {

  private final SolicitudProteccionService solicitudProteccionService;
  private final ModelMapper modelMapper;

  /**
   * Crea un nuevo {@link SolicitudProteccion}.
   * 
   * @param viaProteccion {@link SolicitudProteccion} que se quiere crear.
   * @return Nuevo {@link SolicitudProteccion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('PII-INV-E')")
  ResponseEntity<SolicitudProteccionOutput> create(
      @Valid @RequestBody SolicitudProteccionInput solicitudProteccionInput) {

    return new ResponseEntity<>(
        convert(this.solicitudProteccionService.create(convert(null, solicitudProteccionInput))), HttpStatus.CREATED);
  }

  /**
   * Activa el {@link SolicitudProteccion} con id indicado.
   * 
   * @param id Identificador de {@link SolicitudProteccion}.
   * @return {@link SolicitudProteccion} actualizado.
   */
  @PatchMapping("/{id}/activar")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  SolicitudProteccionOutput activar(@PathVariable Long id) {

    return convert(this.solicitudProteccionService.activar(id));
  }

  /**
   * Desactiva el {@link SolicitudProteccion} con id indicado.
   * 
   * @param id Identificador de {@link SolicitudProteccion}.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthority('PII-INV-E')")
  SolicitudProteccionOutput desactivar(@PathVariable Long id) {

    return convert(this.solicitudProteccionService.desactivar(id));
  }

  /****************/
  /* CONVERTERS */
  /****************/

  private SolicitudProteccionOutput convert(SolicitudProteccion solicitudProteccion) {
    return this.modelMapper.map(solicitudProteccion, SolicitudProteccionOutput.class);
  }

  private SolicitudProteccion convert(Long id, SolicitudProteccionInput solicitudProteccionInput) {
    SolicitudProteccion solicitudProteccion = modelMapper.map(solicitudProteccionInput, SolicitudProteccion.class);
    solicitudProteccion.setId(id);
    return solicitudProteccion;
  }
}
