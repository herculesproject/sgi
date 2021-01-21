package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEquipoSocioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * SolicitudProyectoEquipoSocioController
 */
@RestController
@RequestMapping("/solicitudproyectoequiposocio")
@Slf4j
public class SolicitudProyectoEquipoSocioController {

  /** SolicitudProyectoEquipoSocioService service */
  private final SolicitudProyectoEquipoSocioService service;

  /**
   * Instancia un nuevo SolicitudProyectoEquipoSocioController.
   * 
   * @param solicitudProyectoEquipoSocioService {@link SolicitudProyectoEquipoSocioService}.
   */
  public SolicitudProyectoEquipoSocioController(
      SolicitudProyectoEquipoSocioService solicitudProyectoEquipoSocioService) {
    this.service = solicitudProyectoEquipoSocioService;
  }

  /**
   * Actualiza el listado de {@link SolicitudProyectoEquipoSocio} de la
   * {@link SolicitudProyectoSocio} con el listado solicitud proyecto equipo socio
   * añadiendo, editando o eliminando los elementos segun proceda.
   * 
   * @param proyectoSolictudSocioId       Id de la {@link SolicitudProyectoSocio}.
   * @param solicitudProyectoEquipoSocios lista con los nuevos
   *                                      {@link SolicitudProyectoEquipoSocio} a
   *                                      guardar.
   * @return Lista actualizada con los {@link SolicitudProyectoEquipoSocio}.
   */
  @PatchMapping("/{proyectoSolictudSocioId}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CENTGES-C')")
  public ResponseEntity<List<SolicitudProyectoEquipoSocio>> updateConvocatoriaPeriodoJustificacionesConvocatoria(
      @PathVariable Long proyectoSolictudSocioId,
      @Valid @RequestBody List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocios) {
    log.debug(
        "updateConvocatoriaPeriodoJustificacionesConvocatoria(Long proyectoSolictudSocioId, List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocios) - start");
    List<SolicitudProyectoEquipoSocio> returnValue = service.update(proyectoSolictudSocioId,
        solicitudProyectoEquipoSocios);
    log.debug(
        "updateConvocatoriaPeriodoJustificacionesConvocatoria(Long proyectoSolictudSocioId, List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocios) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Comprueba la existencia del {@link SolicitudProyectoEquipoSocio} con el id
   * indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoEquipoSocio}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-V')")
  public ResponseEntity<?> exists(@PathVariable Long id) {
    log.debug("SolicitudProyectoEquipoSocio exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("SolicitudProyectoEquipoSocio exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("SolicitudProyectoEquipoSocio exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el {@link SolicitudProyectoEquipoSocio} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoEquipoSocio}.
   * @return SolicitudProyectoEquipoSocio {@link SolicitudProyectoEquipoSocio}
   *         correspondiente al id
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-RSOC-V')")
  SolicitudProyectoEquipoSocio findById(@PathVariable Long id) {
    log.debug("SolicitudProyectoEquipoSocio findById(Long id) - start");
    SolicitudProyectoEquipoSocio returnValue = service.findById(id);
    log.debug("SolicitudProyectoEquipoSocio findById(Long id) - end");
    return returnValue;
  }

}