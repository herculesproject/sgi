package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import org.crue.hercules.sgi.csp.dto.sge.ColumnaOutput;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgeService;
import org.crue.hercules.sgi.csp.util.EjecucionEconomicaInvestigadorAuthorityHelper;
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
 * EjecucionEconomicaInvestigadorController
 * <p>
 * Expone la ejecución económica del SGE al perfil de investigador. CSP aplica
 * la autorización por persona (vía
 * {@link EjecucionEconomicaInvestigadorAuthorityHelper}) y, una vez validado el
 * acceso, reenvía la petición al SGE a través de {@link SgiApiSgeService}.
 */
@RestController
@RequestMapping(EjecucionEconomicaInvestigadorController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class EjecucionEconomicaInvestigadorController {
  public static final String REQUEST_MAPPING = "/ejecucion-economica-investigador";
  public static final String PATH_COLUMNAS = "/columnas";
  public static final String PATH_FACTURAS_EMITIDAS = "/facturas-emitidas";
  public static final String PATH_FACTURAS_EMITIDAS_COLUMNAS = "/facturas-emitidas/columnas";
  public static final String PATH_FACTURAS_EMITIDAS_ID = "/facturas-emitidas/{id}";
  public static final String PATH_ID = "/{id}";
  public static final String PATH_PROYECTO_SGE = "/proyectos-sge/{proyectoSgeRef}";

  private final SgiApiSgeService sgiApiSgeService;
  private final EjecucionEconomicaInvestigadorAuthorityHelper authorityHelper;

  /**
   * Devuelve las columnas de la ejecución económica.
   *
   * @param query filtro de búsqueda.
   * @return la lista de {@link ColumnaOutput}.
   */
  @GetMapping(PATH_COLUMNAS)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-EJEC-INV-VR')")
  public ResponseEntity<List<ColumnaOutput>> findColumnas(@RequestParam(name = "q", required = false) String query) {
    log.debug("findColumnas - query: {}", query);

    List<ColumnaOutput> columnas = sgiApiSgeService.findColumnasEjecucionEconomica(query);

    log.debug("findColumnas - response: {}", SgiLogUtils.collection(columnas));
    return columnas.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(columnas, HttpStatus.OK);
  }

  /**
   * Devuelve el detalle de una operación de la ejecución económica.
   *
   * @param id             identificador de la operación en el SGE.
   * @param proyectoSgeRef identificador del proyecto en el SGE sobre el que se
   *                       comprueba el acceso.
   * @param tipoOperacion  tipo de operación.
   * @return el detalle de la operación.
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-EJEC-INV-VR')")
  public ResponseEntity<Object> findDatoEconomicoDetalle(@PathVariable String id,
      @RequestParam String proyectoSgeRef,
      @RequestParam(required = false) String tipoOperacion) {
    log.debug("findDatoEconomicoDetalle - id: {}, proyectoSgeRef: {}, tipoOperacion: {}", id, proyectoSgeRef,
        tipoOperacion);

    authorityHelper.checkAccessProyectoSgeRef(proyectoSgeRef);

    Object detalle = sgiApiSgeService.findDatoEconomicoDetalle(id, tipoOperacion);

    return detalle == null ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(detalle, HttpStatus.OK);
  }

  /**
   * Devuelve los datos económicos paginados de la ejecución económica.
   *
   * @param proyectoSgeRef identificador del proyecto en el SGE sobre el que se
   *                       comprueba el acceso.
   * @param query          filtro de búsqueda.
   * @param sort           criterio de ordenación.
   * @param paging         {@link Pageable}.
   * @return la página de datos económicos.
   */
  @GetMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-EJEC-INV-VR')")
  public ResponseEntity<Page<Object>> findDatosEconomicos(
      @RequestParam String proyectoSgeRef,
      @RequestParam(name = "q", required = false) String query,
      @RequestParam(name = "s", required = false) String sort,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findDatosEconomicos - proyectoSgeRef: {}, query: {}, sort: {}, paging: {}", proyectoSgeRef, query, sort,
        SgiLogUtils.pageable(paging));

    authorityHelper.checkAccessProyectoSgeRef(proyectoSgeRef);

    Page<Object> page = sgiApiSgeService.findDatosEconomicos(query, sort, paging);

    log.debug("findDatosEconomicos - response: {}", SgiLogUtils.page(page));
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve los datos de un proyecto del SGE.
   *
   * @param proyectoSgeRef identificador del proyecto en el SGE.
   * @return los datos del proyecto en el SGE.
   */
  @GetMapping(PATH_PROYECTO_SGE)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-EJEC-INV-VR')")
  public ResponseEntity<Object> findProyectoSge(@PathVariable String proyectoSgeRef) {
    log.debug("findProyectoSge - proyectoSgeRef: {}", proyectoSgeRef);

    authorityHelper.checkAccessProyectoSgeRef(proyectoSgeRef);

    Object proyectoSge = sgiApiSgeService.findProyectoSge(proyectoSgeRef);

    return proyectoSge == null ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(proyectoSge, HttpStatus.OK);
  }

  /**
   * Devuelve las columnas de las facturas emitidas.
   *
   * @param query filtro de búsqueda.
   * @return la lista de {@link ColumnaOutput}.
   */
  @GetMapping(PATH_FACTURAS_EMITIDAS_COLUMNAS)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-EJEC-INV-VR')")
  public ResponseEntity<List<ColumnaOutput>> findColumnasFacturasEmitidas(
      @RequestParam(name = "q", required = false) String query) {
    log.debug("findColumnasFacturasEmitidas - query: {}", query);

    List<ColumnaOutput> columnas = sgiApiSgeService.findColumnasFacturasEmitidas(query);

    log.debug("findColumnasFacturasEmitidas - response: {}", SgiLogUtils.collection(columnas));
    return columnas.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(columnas, HttpStatus.OK);
  }

  /**
   * Devuelve las facturas emitidas paginadas.
   *
   * @param proyectoSgeRef identificador del proyecto en el SGE sobre el que se
   *                       comprueba el acceso.
   * @param query          filtro de búsqueda.
   * @param sort           criterio de ordenación.
   * @param paging         {@link Pageable}.
   * @return la página de facturas emitidas.
   */
  @GetMapping(PATH_FACTURAS_EMITIDAS)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-EJEC-INV-VR')")
  public ResponseEntity<Page<Object>> findFacturasEmitidas(
      @RequestParam String proyectoSgeRef,
      @RequestParam(name = "q", required = false) String query,
      @RequestParam(name = "s", required = false) String sort,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findFacturasEmitidas - proyectoSgeRef: {}, query: {}, sort: {}, paging: {}", proyectoSgeRef, query,
        sort, SgiLogUtils.pageable(paging));

    authorityHelper.checkAccessProyectoSgeRef(proyectoSgeRef);

    Page<Object> page = sgiApiSgeService.findFacturasEmitidas(query, sort, paging);

    log.debug("findFacturasEmitidas - response: {}", SgiLogUtils.page(page));
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el detalle de una factura emitida.
   *
   * @param id             identificador de la factura emitida en el SGE.
   * @param proyectoSgeRef identificador del proyecto en el SGE sobre el que se
   *                       comprueba el acceso.
   * @return el detalle de la factura emitida.
   */
  @GetMapping(PATH_FACTURAS_EMITIDAS_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-EJEC-INV-VR')")
  public ResponseEntity<Object> findFacturaEmitidaDetalle(@PathVariable String id,
      @RequestParam String proyectoSgeRef) {
    log.debug("findFacturaEmitidaDetalle - id: {}, proyectoSgeRef: {}", id, proyectoSgeRef);

    authorityHelper.checkAccessProyectoSgeRef(proyectoSgeRef);

    Object detalle = sgiApiSgeService.findFacturaEmitidaDetalle(id);

    return detalle == null ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(detalle, HttpStatus.OK);
  }

}
