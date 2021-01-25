package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPeriodoJustificacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * SolicitudProyectoPeriodoJustificacionController
 */

@RestController
@RequestMapping("/solicitudproyectoperiodojustificaciones")
@Slf4j
public class SolicitudProyectoPeriodoJustificacionController {

  /** SolicitudProyectoPeriodoJustificacion service */
  private final SolicitudProyectoPeriodoJustificacionService service;

  public SolicitudProyectoPeriodoJustificacionController(
      SolicitudProyectoPeriodoJustificacionService solicitudProyectoPeriodoJustificacionService) {
    log.debug(
        "SolicitudProyectoPeriodoJustificacionController(SolicitudProyectoPeriodoJustificacionService solicitudProyectoPeriodoJustificacionService) - start");
    this.service = solicitudProyectoPeriodoJustificacionService;
    log.debug(
        "SolicitudProyectoPeriodoJustificacionController(SolicitudProyectoPeriodoJustificacionService solicitudProyectoPeriodoJustificacionService) - end");
  }

  /**
   * Devuelve el {@link SolicitudProyectoPeriodoJustificacion} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoPeriodoJustificacion}.
   * @return {@link SolicitudProyectoPeriodoJustificacion} correspondiente al id.
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-TDOC-V')")
  SolicitudProyectoPeriodoJustificacion findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    SolicitudProyectoPeriodoJustificacion returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link SolicitudProyectoPeriodoJustificacion} de la
   * {@link SolicitudProyectoSocio} con el listado
   * solicitudProyectoPeriodoJustificaciones añadiendo, editando o eliminando los
   * elementos segun proceda.
   * 
   * @param solicitudProyectoSocioId                Id de la
   *                                                {@link SolicitudProyectoSocio}.
   * @param solicitudProyectoPeriodoJustificaciones lista con los nuevos
   *                                                {@link SolicitudProyectoPeriodoJustificacion}
   *                                                a guardar.
   * @return Lista actualizada con los
   *         {@link SolicitudProyectoPeriodoJustificacion}.
   */
  @PatchMapping("/{solicitudProyectoSocioId}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CENTGES-C')")
  public ResponseEntity<List<SolicitudProyectoPeriodoJustificacion>> update(@PathVariable Long solicitudProyectoSocioId,
      @Valid @RequestBody List<SolicitudProyectoPeriodoJustificacion> solicitudProyectoPeriodoJustificaciones) {
    log.debug(
        "update(Long solicitudProyectoId, List<SolicitudProyectoPeriodoJustificacion> solicitudProyectoPeriodoJustificaciones) - start");
    List<SolicitudProyectoPeriodoJustificacion> returnValue = service.update(solicitudProyectoSocioId,
        solicitudProyectoPeriodoJustificaciones);
    log.debug(
        "update(Long solicitudProyectoId, List<SolicitudProyectoPeriodoJustificacion> solicitudProyectoPeriodoJustificaciones) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

}
