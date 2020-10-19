package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.crue.hercules.sgi.csp.service.ConvocatoriaAreaTematicaService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEnlaceService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadGestoraService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ConvocatoriaController
 */
@RestController
@RequestMapping("/convocatorias")
@Slf4j
public class ConvocatoriaController {

  /** ConvocatoriaEntidadGestora service */
  private final ConvocatoriaEntidadGestoraService convocatoriaEntidadGestoraService;
  /** ConvocatoriaEntidadGestora service */
  private final ConvocatoriaAreaTematicaService convocatoriaAreaTematicaService;

  /** ConvocatoriaEntlace service */
  private final ConvocatoriaEnlaceService convocatoriaEnlaceService;

  /**
   * Instancia un nuevo ConvocatoriaController.
   * 
   * @param convocatoriaEntidadGestoraService {@link ConvocatoriaEntidadGestoraService}.
   * @param convocatoriaEnlaceService         {@link ConvocatoriaEnlaceService}
   */
  public ConvocatoriaController(ConvocatoriaEntidadGestoraService convocatoriaEntidadGestoraService,
      ConvocatoriaAreaTematicaService convocatoriaAreaTematicaService,
      ConvocatoriaEnlaceService convocatoriaEnlaceService) {
    this.convocatoriaEntidadGestoraService = convocatoriaEntidadGestoraService;
    this.convocatoriaAreaTematicaService = convocatoriaAreaTematicaService;
    this.convocatoriaEnlaceService = convocatoriaEnlaceService;
  }

  /**
   * 
   * CONVOCATORIA ENLACE
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaEnlace} de la
   * {@link Convocatoria}.
   * 
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriaenlaces")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CENTGES-V')")
  ResponseEntity<Page<ConvocatoriaEnlace>> findAllConvocatoriaEnlace(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaEnlace(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ConvocatoriaEnlace> page = convocatoriaEnlaceService.findAllByConvocatoria(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaEnlace(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaEnlace(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * CONVOCATORIA ENTIDAD GESTORA
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaEntidadGestora}
   * de la {@link Convocatoria}.
   * 
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriaentidadgestoras")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CENTGES-V')")
  ResponseEntity<Page<ConvocatoriaEntidadGestora>> findAllConvocatoriaEntidadGestora(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaEntidadGestora(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ConvocatoriaEntidadGestora> page = convocatoriaEntidadGestoraService.findAllByConvocatoria(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaEntidadGestora(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaEntidadGestora(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * 
   * CONVOCATORIA AREA TEMATICA
   * 
   */

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaAreaTematica} de
   * la {@link Convocatoria}.
   * 
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable.
   */
  @GetMapping("/{id}/convocatoriaareatematicas")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CATEM-V')")
  ResponseEntity<Page<ConvocatoriaAreaTematica>> findAllConvocatoriaAreaTematica(@PathVariable Long id,
      @RequestParam(name = "q", required = false) List<QueryCriteria> query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaAreaTematica(Long id, List<QueryCriteria> query, Pageable paging) - start");
    Page<ConvocatoriaAreaTematica> page = convocatoriaAreaTematicaService.findAllByConvocatoria(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaAreaTematica(Long id, List<QueryCriteria> query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaAreaTematica(Long id, List<QueryCriteria> query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }
}
