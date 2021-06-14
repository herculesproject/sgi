package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.service.ProyectoConceptoGastoCodigoEcService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoConceptoGastoCodigoEcController
 */
@RestController
@RequestMapping("/proyectoconceptogastocodigosec")
@Slf4j
public class ProyectoConceptoGastoCodigoEcController {

  /** ProyectoConceptoGastoCodigoEc service */
  private final ProyectoConceptoGastoCodigoEcService service;

  /**
   * Instancia un nuevo ProyectoConceptoGastoCodigoEcController.
   * 
   * @param service {@link ProyectoConceptoGastoCodigoEcService}
   */
  public ProyectoConceptoGastoCodigoEcController(ProyectoConceptoGastoCodigoEcService service) {
    this.service = service;
  }

  /**
   * Devuelve el {@link ProyectoConceptoGastoCodigoEc} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoConceptoGastoCodigoEc}.
   * @return {@link ProyectoConceptoGastoCodigoEc} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  ProyectoConceptoGastoCodigoEc findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoConceptoGastoCodigoEc returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link ProyectoConceptoGastoCodigoEc} del
   * {@link ProyectoConceptoGasto} con el listado codigosEconomicos añadiendo,
   * editando o eliminando los elementos segun proceda.
   * 
   * @param proyectoConceptoGastoId Id del {@link ProyectoConceptoGasto}.
   * @param codigosEconomicos       lista con los nuevos
   *                                {@link ProyectoConceptoGastoCodigoEc} a
   *                                guardar.
   * @return Lista actualizada con los {@link ProyectoConceptoGastoCodigoEc}.
   */
  @PatchMapping("/{proyectoConceptoGastoId}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public ResponseEntity<List<ProyectoConceptoGastoCodigoEc>> update(@PathVariable Long proyectoConceptoGastoId,
      @Valid @RequestBody List<ProyectoConceptoGastoCodigoEc> codigosEconomicos) {
    log.debug("update(List<ProyectoConceptoGastoCodigoEc> codigosEconomicos, proyectoConceptoGastoId) - start");
    List<ProyectoConceptoGastoCodigoEc> returnValue = service.update(proyectoConceptoGastoId, codigosEconomicos);
    log.debug("update(List<ProyectoConceptoGastoCodigoEc> codigosEconomicos, proyectoConceptoGastoId) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

}
