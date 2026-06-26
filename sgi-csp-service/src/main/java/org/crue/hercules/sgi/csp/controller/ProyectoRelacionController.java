package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import org.crue.hercules.sgi.csp.dto.rel.ProyectoRelacionOutput;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.service.ProyectoRelacionService;
import org.crue.hercules.sgi.csp.util.SgiLogUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador de las relaciones (módulo REL) de un {@link Proyecto}.
 */
@RestController
@RequestMapping(ProyectoRelacionController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class ProyectoRelacionController {
  public static final String REQUEST_MAPPING = "/proyectos";
  public static final String PATH_RELACIONES = "/{id}/relaciones";

  private final ProyectoRelacionService proyectoRelacionService;

  /**
   * Devuelve las relaciones del {@link Proyecto} indicado, ya enriquecidas con
   * los datos mínimos de la entidad relacionada.
   *
   * @param id Identificador del {@link Proyecto}.
   * @return el listado de relaciones del {@link Proyecto}.
   */
  @GetMapping(PATH_RELACIONES)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E', 'CSP-PRO-INV-VR', 'CSP-PRO-V')")
  public ResponseEntity<List<ProyectoRelacionOutput>> findRelaciones(@PathVariable Long id) {
    log.debug("findRelaciones - id: {}", id);
    List<ProyectoRelacionOutput> relaciones = proyectoRelacionService.findRelacionesProyecto(id);
    log.debug("findRelaciones - response: {}", SgiLogUtils.collection(relaciones));
    return new ResponseEntity<>(relaciones, HttpStatus.OK);
  }

}
