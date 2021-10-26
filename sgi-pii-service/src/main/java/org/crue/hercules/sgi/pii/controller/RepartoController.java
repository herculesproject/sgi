package org.crue.hercules.sgi.pii.controller;

import org.crue.hercules.sgi.pii.dto.RepartoOutput;
import org.crue.hercules.sgi.pii.model.Reparto;
import org.crue.hercules.sgi.pii.service.RepartoService;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * RepartoController {
 * 
 */
@RestController
@RequestMapping(RepartoController.MAPPING)
@Slf4j
public class RepartoController {
  public static final String MAPPING = "/repartos";

  private ModelMapper modelMapper;

  /** Reparto service */
  private final RepartoService service;

  public RepartoController(ModelMapper modelMapper, RepartoService repartoService) {
    this.modelMapper = modelMapper;
    this.service = repartoService;
  }

  /**
   * Devuelve la entidad {@link Reparto} con el id indicado.
   * 
   * @param id Identificador de la entidad {@link Reparto}.
   * @return {@link Reparto} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PII-INV-V', 'PII-INV-E')")
  RepartoOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    final Reparto returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  private RepartoOutput convert(Reparto reparto) {
    return modelMapper.map(reparto, RepartoOutput.class);
  }
}
