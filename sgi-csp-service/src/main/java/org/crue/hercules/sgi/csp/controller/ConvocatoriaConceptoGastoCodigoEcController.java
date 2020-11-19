package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.service.ConvocatoriaConceptoGastoCodigoEcService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ConvocatoriaConceptoGastoCodigoEcController
 */
@RestController
@RequestMapping("/convocatoriaconceptogastocodigoecs")
@Slf4j
public class ConvocatoriaConceptoGastoCodigoEcController {

  /** ConvocatoriaConceptoGastoCodigoEc service */
  private final ConvocatoriaConceptoGastoCodigoEcService service;

  /**
   * Instancia un nuevo ConvocatoriaConceptoGastoCodigoEcController.
   * 
   * @param service {@link ConvocatoriaConceptoGastoCodigoEcService}
   */
  public ConvocatoriaConceptoGastoCodigoEcController(ConvocatoriaConceptoGastoCodigoEcService service) {
    this.service = service;
  }

  /**
   * Devuelve el {@link ConvocatoriaConceptoGastoCodigoEc} con el id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaConceptoGastoCodigoEc}.
   * @return {@link ConvocatoriaConceptoGastoCodigoEc} correspondiente al id.
   */
  @GetMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CFAS-V')")
  ConvocatoriaConceptoGastoCodigoEc findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ConvocatoriaConceptoGastoCodigoEc returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea un nuevo {@link ConvocatoriaConceptoGastoCodigoEc}.
   * 
   * @param convocatoriaConceptoGastoCodigoEc {@link ConvocatoriaConceptoGastoCodigoEc}
   *                                          que se quiere crear.
   * @return Nuevo {@link ConvocatoriaConceptoGastoCodigoEc} creado.
   */
  @PostMapping
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CFAS-C')")
  ResponseEntity<ConvocatoriaConceptoGastoCodigoEc> create(
      @Valid @RequestBody ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc) {
    log.debug("create(ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc) - start");
    ConvocatoriaConceptoGastoCodigoEc returnValue = service.create(convocatoriaConceptoGastoCodigoEc);
    log.debug("create(ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ConvocatoriaConceptoGastoCodigoEc} con el id indicado.
   * 
   * @param convocatoriaConceptoGastoCodigoEc {@link ConvocatoriaConceptoGastoCodigoEc}
   *                                          a actualizar.
   * @param id                                id
   *                                          {@link ConvocatoriaConceptoGastoCodigoEc}
   *                                          a actualizar.
   * @return {@link ConvocatoriaConceptoGastoCodigoEc} actualizado.
   */
  @PutMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CFAS-E')")
  ConvocatoriaConceptoGastoCodigoEc update(
      @Validated({ Update.class,
          Default.class }) @RequestBody ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc,
      @PathVariable Long id) {
    log.debug("update(ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc, Long id) - start");
    convocatoriaConceptoGastoCodigoEc.setId(id);
    ConvocatoriaConceptoGastoCodigoEc returnValue = service.update(convocatoriaConceptoGastoCodigoEc);
    log.debug("update(ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ConvocatoriaConceptoGastoCodigoEc} con id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaConceptoGastoCodigoEc}.
   */
  @DeleteMapping("/{id}")
  // @PreAuthorize("hasAuthorityForAnyUO('CSP-CFAS-B')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

}
