package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import org.crue.hercules.sgi.csp.dto.RelacionEjecucionEconomica;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.service.GrupoService;
import org.crue.hercules.sgi.csp.service.ProyectoService;
import org.crue.hercules.sgi.csp.service.RelacionEjecucionEconomicaService;
import org.crue.hercules.sgi.csp.util.SgiLogUtils;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RelacionEjecucionEconomicaController
 */
@RestController
@RequestMapping(RelacionEjecucionEconomicaController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class RelacionEjecucionEconomicaController {
  public static final String REQUEST_MAPPING = "/relaciones-ejecucion-economica";
  public static final String PATH_GRUPOS = "/grupos";
  public static final String PATH_PROYECTOS = "/proyectos";
  public static final String PATH_INVESTIGADOR_GRUPOS = "/investigador/grupos";
  public static final String PATH_INVESTIGADOR_PROYECTOS = "/investigador/proyectos";
  public static final String PATH_INVESTIGADOR_PROYECTO_SGE_REF = "/investigador/proyecto-sge-ref/{proyectoSgeRef}";

  private final ProyectoService proyectoService;
  private final GrupoService grupoService;
  private final RelacionEjecucionEconomicaService relacionEjecucionEconomicaService;

  /**
   * Devuelve una lista paginada y filtrada {@link RelacionEjecucionEconomica} de
   * {@link Grupo}
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link RelacionEjecucionEconomica} de los
   *         {@link Grupo}
   *         activos paginadas y filtradas.
   */
  @GetMapping(PATH_GRUPOS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-EJEC-V', 'CSP-EJEC-E')")
  public ResponseEntity<Page<RelacionEjecucionEconomica>> findRelacionesGrupos(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findRelacionesGrupos(String query, Pageable paging) - start");

    Page<RelacionEjecucionEconomica> page = grupoService.findRelacionesEjecucionEconomicaGrupos(query, paging);

    log.debug("findRelacionesGrupos(String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link RelacionEjecucionEconomica} de
   * {@link Proyecto} que se encuentren dentro de la unidad de gestión del usuario
   * logueado
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link RelacionEjecucionEconomica} de
   *         {@link Proyecto} activos paginadas y filtradas.
   */
  @GetMapping(PATH_PROYECTOS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-EJEC-V', 'CSP-EJEC-E')")
  public ResponseEntity<Page<RelacionEjecucionEconomica>> findRelacionesProyectos(
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findRelacionesProyectos(String query, Pageable paging) - start");

    Page<RelacionEjecucionEconomica> page = proyectoService.findRelacionesEjecucionEconomicaProyectos(query, paging);

    log.debug("findRelacionesProyectos(String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link RelacionEjecucionEconomica} de
   * {@link Grupo} en los que el usuario logueado figura como responsable (rol
   * principal).
   *
   * @param query                       filtro de búsqueda.
   * @param onlyWithParticipacionActual si solo se incluyen los grupos con
   *                                    participación como responsable en la fecha
   *                                    actual.
   * @param paging                      {@link Pageable}.
   * @return el listado de entidades {@link RelacionEjecucionEconomica} de los
   *         {@link Grupo} activos paginadas y filtradas.
   */
  @GetMapping(PATH_INVESTIGADOR_GRUPOS)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-EJEC-INV-VR')")
  public ResponseEntity<Page<RelacionEjecucionEconomica>> findRelacionesGruposInvestigador(
      @RequestParam(name = "q", required = false) String query,
      @RequestParam(required = false, defaultValue = "false") boolean onlyWithParticipacionActual,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findRelacionesGruposInvestigador - query: {}, onlyWithParticipacionActual: {}, paging: {}", query,
        onlyWithParticipacionActual, SgiLogUtils.pageable(paging));

    Page<RelacionEjecucionEconomica> page = grupoService.findRelacionesEjecucionEconomicaGruposInvestigador(query,
        onlyWithParticipacionActual, paging);

    log.debug("findRelacionesGruposInvestigador - response: {}", SgiLogUtils.page(page));
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link RelacionEjecucionEconomica} de
   * {@link Proyecto} en los que el usuario logueado figura como investigador
   * principal (rol principal).
   *
   * @param query                       filtro de búsqueda.
   * @param onlyWithParticipacionActual si solo se incluyen los proyectos con
   *                                    participación como investigador principal
   *                                    en
   *                                    la fecha actual.
   * @param paging                      {@link Pageable}.
   * @return el listado de entidades {@link RelacionEjecucionEconomica} de
   *         {@link Proyecto} activos paginadas y filtradas.
   */
  @GetMapping(PATH_INVESTIGADOR_PROYECTOS)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-EJEC-INV-VR')")
  public ResponseEntity<Page<RelacionEjecucionEconomica>> findRelacionesProyectosInvestigador(
      @RequestParam(name = "q", required = false) String query,
      @RequestParam(name = "onlyWithParticipacionActual", required = false, defaultValue = "false") boolean onlyWithParticipacionActual,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findRelacionesProyectosInvestigador - query: {}, onlyWithParticipacionActual: {}, paging: {}", query,
        onlyWithParticipacionActual, SgiLogUtils.pageable(paging));

    Page<RelacionEjecucionEconomica> page = proyectoService.findRelacionesEjecucionEconomicaProyectosInvestigador(query,
        onlyWithParticipacionActual, paging);

    log.debug("findRelacionesProyectosInvestigador - response: {}", SgiLogUtils.page(page));
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve las {@link RelacionEjecucionEconomica} (proyectos y grupos)
   * asociadas a un {@code proyectoSgeRef} en los que el usuario logueado figura
   * como investigador principal (rol principal).
   *
   * @param proyectoSgeRef identificador del proyecto en el SGE.
   * @return el listado de {@link RelacionEjecucionEconomica} de la referencia.
   */
  @GetMapping(PATH_INVESTIGADOR_PROYECTO_SGE_REF)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-EJEC-INV-VR')")
  public ResponseEntity<List<RelacionEjecucionEconomica>> findRelacionesByProyectoSgeRefInvestigador(
      @PathVariable String proyectoSgeRef) {
    log.debug("findRelacionesByProyectoSgeRefInvestigador - proyectoSgeRef: {}", proyectoSgeRef);

    List<RelacionEjecucionEconomica> relaciones = relacionEjecucionEconomicaService
        .findByProyectoSgeRefInvestigador(proyectoSgeRef);

    log.debug("findRelacionesByProyectoSgeRefInvestigador - response: {}", SgiLogUtils.collection(relaciones));
    return relaciones.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(relaciones, HttpStatus.OK);
  }

}
