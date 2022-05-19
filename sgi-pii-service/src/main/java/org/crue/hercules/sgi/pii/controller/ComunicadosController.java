package org.crue.hercules.sgi.pii.controller;

import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.crue.hercules.sgi.pii.service.ComunicadosService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@RequestMapping(ComunicadosController.REQUEST_MAPPING)
@Slf4j
public class ComunicadosController {
  /** El path que gestiona este controlador */
  public static final String REQUEST_MAPPING = "/comunicados";

  private final ComunicadosService comunicadoService;

  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-pii'))")
  @GetMapping("/meses-hasta-fecha-fin-prioridad-solicitud-proteccion/{meses}")
  public void enviarComunicadoMesesHastaFinPrioridadSolicitudProteccion(
      @Valid @PathVariable(name = "meses", required = true) Integer meses) throws JsonProcessingException {
    comunicadoService.enviarComunicadoMesesHastaFinPrioridadSolicitudProteccion(meses);
  }
}
