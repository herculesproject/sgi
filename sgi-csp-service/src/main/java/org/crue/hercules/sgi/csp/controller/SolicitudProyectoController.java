package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.converter.SolicitudProyectoUnidadVinculacionConverter;
import org.crue.hercules.sgi.csp.dto.SolicitudProyectoUnidadVinculacionInput;
import org.crue.hercules.sgi.csp.dto.SolicitudProyectoUnidadVinculacionOutput;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoUnidadVinculacion;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPresupuestoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoUnidadVinculacionService;
import org.crue.hercules.sgi.csp.util.SgiLogUtils;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SolicitudProyectoController
 */
@RestController
@RequestMapping(SolicitudProyectoController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class SolicitudProyectoController {

  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "solicitudproyecto";

  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  public static final String PATH_SOCIOS_COORDINADOR = PATH_ID + "/solicitudproyectosocios/coordinador";
  public static final String PATH_SOCIOS_PERIODOS_JUSTIFICACION = PATH_ID
      + "/solicitudproyectosocios/periodosjustificacion";
  public static final String PATH_SOCIOS_PERIODOS_PAGO = PATH_ID + "/solicitudproyectosocios/periodospago";
  public static final String PATH_SOLICITUD_PRESUPUESTO = PATH_ID + "/solicitudpresupuesto";
  public static final String PATH_SOLICITUD_SOCIO = PATH_ID + "/solicitudsocio";
  public static final String PATH_UNIDADES_VINCULACION = PATH_ID + "/unidades-vinculacion";

  private final SolicitudProyectoService service;
  private final SolicitudProyectoPresupuestoService solicitudProyectoPresupuestoService;
  private final SolicitudProyectoSocioService solicitudProyectoSocioService;
  private final SolicitudProyectoUnidadVinculacionService solicitudProyectoUnidadVinculacionService;

  private final SolicitudProyectoUnidadVinculacionConverter solicitudProyectoUnidadVinculacionConverter;

  /**
   * Crea nuevo {@link SolicitudProyecto}
   * 
   * @param solicitudProyecto {@link SolicitudProyecto}. que se quiere crear.
   * @return Nuevo {@link SolicitudProyecto} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  public ResponseEntity<SolicitudProyecto> create(@Valid @RequestBody SolicitudProyecto solicitudProyecto) {
    log.debug("create(SolicitudProyecto solicitudProyecto) - start");
    SolicitudProyecto returnValue = service.create(solicitudProyecto);
    log.debug("create(SolicitudProyecto solicitudProyecto) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link SolicitudProyecto}.
   * 
   * @param solicitudProyecto {@link SolicitudProyecto} a actualizar.
   * @param id                Identificador {@link SolicitudProyecto} a
   *                          actualizar.
   * @param authentication    Datos autenticación.
   * @return SolicitudProyecto {@link SolicitudProyecto} actualizado
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E','CSP-SOL-INV-C', 'CSP-SOL-INV-ER')")
  public SolicitudProyecto update(@Valid @RequestBody SolicitudProyecto solicitudProyecto, @PathVariable Long id,
      Authentication authentication) {
    log.debug("update(SolicitudProyecto solicitudProyecto, Long id) - start");
    solicitudProyecto.setId(id);
    SolicitudProyecto returnValue = service.update(solicitudProyecto);
    log.debug("update(SolicitudProyecto solicitudProyecto, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link SolicitudProyecto} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyecto}.
   * @return SolicitudProyecto {@link SolicitudProyecto} correspondiente al id
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public SolicitudProyecto findById(@PathVariable Long id) {
    log.debug("SolicitudProyecto findById(Long id) - start");
    SolicitudProyecto returnValue = service.findById(id);
    log.debug("SolicitudProyecto findById(Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba si existen datos vinculados a la {@link SolicitudProyecto} de
   * {@link SolicitudProyectoPresupuesto}
   *
   * @param id Id del {@link SolicitudProyecto}.
   * @return {@link HttpStatus#OK} si tiene {@link SolicitudProyectoPresupuesto},
   *         {@link HttpStatus#NO_CONTENT} en cualquier otro caso
   */
  @RequestMapping(path = PATH_SOLICITUD_PRESUPUESTO, method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-ER')")
  public ResponseEntity<Void> hasSolicitudPresupuesto(@PathVariable Long id) {
    log.debug("hasSolicitudPresupuesto(Long id) - start");
    Boolean returnValue = solicitudProyectoPresupuestoService.hasSolicitudPresupuesto(id);
    log.debug("hasSolicitudPresupuesto(Long id) - end");
    return returnValue.booleanValue() ? new ResponseEntity<>(HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba si existen datos vinculados a la {@link SolicitudProyecto} de
   * {@link SolicitudProyectoSocio}
   *
   * @param id Id del {@link SolicitudProyectoSocio}.
   * @return {@link HttpStatus#OK} si tiene {@link SolicitudProyectoSocio},
   *         {@link HttpStatus#NO_CONTENT} en cualquier otro caso
   */
  @RequestMapping(path = PATH_SOLICITUD_SOCIO, method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-ER')")
  public ResponseEntity<SolicitudProyecto> hasSolicitudSocio(@PathVariable Long id) {
    log.debug("hasSolicitudSocio(Long id) - start");
    Boolean returnValue = solicitudProyectoSocioService.hasSolicitudSocio(id);
    log.debug("hasSolicitudSocio(Long id) - end");
    return returnValue.booleanValue() ? new ResponseEntity<>(HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @RequestMapping(path = PATH_SOCIOS_PERIODOS_PAGO, method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-ER')")
  public ResponseEntity<Object> hasSolicitudProyectoSocioPeriodosPago(
      @PathVariable(required = true) Long id) {

    return this.solicitudProyectoSocioService.existsSolicitudProyectoSocioPeriodoPagoBySolicitudProyectoSocioId(id)
        ? ResponseEntity.ok().build()
        : ResponseEntity.noContent().build();
  }

  @RequestMapping(path = PATH_SOCIOS_PERIODOS_JUSTIFICACION, method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-ER')")
  public ResponseEntity<Object> hasSolicitudProyectoSocioPeriodosJustificacion(
      @PathVariable(required = true) Long id) {

    return this.solicitudProyectoSocioService
        .existsSolicitudProyectoSocioPeriodoJustificacionBySolicitudProyectoSocioId(id)
            ? ResponseEntity.ok().build()
            : ResponseEntity.noContent().build();
  }

  @RequestMapping(path = PATH_SOCIOS_COORDINADOR, method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-ER')")
  public ResponseEntity<Object> hasAnySolicitudProyectoSocioWithRolCoordinador(
      @PathVariable(required = true) Long id) {

    return this.solicitudProyectoSocioService.hasAnySolicitudProyectoSocioWithRolCoordinador(id)
        ? ResponseEntity.ok().build()
        : ResponseEntity.noContent().build();
  }

  /**
   * Devuelve las {@link SolicitudProyectoUnidadVinculacion} asociadas al
   * {@link SolicitudProyecto} con el id indicado.
   *
   * @param id     identificador del {@link SolicitudProyecto}.
   * @param query  filtro de búsqueda.
   * @param paging información de la paginación.
   * @return {@link SolicitudProyectoUnidadVinculacion} correspondientes al
   *         {@link SolicitudProyecto}.
   */
  @GetMapping(PATH_UNIDADES_VINCULACION)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-ER')")
  public Page<SolicitudProyectoUnidadVinculacionOutput> findUnidadesVinculacion(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findUnidadesVinculacion - id: {}, query: {}, paging: {}", id, query, SgiLogUtils.pageable(paging));
    Page<SolicitudProyectoUnidadVinculacionOutput> output = solicitudProyectoUnidadVinculacionConverter
        .convert(solicitudProyectoUnidadVinculacionService.findBySolicitudProyectoId(id, query, paging));
    log.debug("findUnidadesVinculacion - response: {}", SgiLogUtils.page(output));
    return output;
  }

  /**
   * Actualiza la lista de {@link SolicitudProyectoUnidadVinculacion} asociadas al
   * {@link SolicitudProyecto} con el id indicado.
   *
   * @param id                  identificador del {@link SolicitudProyecto}.
   * @param unidadesVinculacion nueva lista de
   *                            {@link SolicitudProyectoUnidadVinculacion} del
   *                            {@link SolicitudProyecto}.
   * @return la nueva lista de {@link SolicitudProyectoUnidadVinculacion}
   *         asociadas al {@link SolicitudProyecto}.
   */
  @PatchMapping(PATH_UNIDADES_VINCULACION)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  public ResponseEntity<List<SolicitudProyectoUnidadVinculacionOutput>> updateUnidadesVinculacion(@PathVariable Long id,
      @Valid @RequestBody List<SolicitudProyectoUnidadVinculacionInput> unidadesVinculacion) {
    log.debug("updateUnidadesVinculacion - id: {}, unidades: {}", id, unidadesVinculacion);
    List<SolicitudProyectoUnidadVinculacionOutput> output = solicitudProyectoUnidadVinculacionConverter
        .convertSolicitudProyectoUnidades(
            solicitudProyectoUnidadVinculacionService.updateUnidadesVinculacion(id,
                solicitudProyectoUnidadVinculacionConverter
                    .convertSolicitudProyectoUnidadesInput(unidadesVinculacion)));
    return new ResponseEntity<>(output, HttpStatus.OK);
  }

}