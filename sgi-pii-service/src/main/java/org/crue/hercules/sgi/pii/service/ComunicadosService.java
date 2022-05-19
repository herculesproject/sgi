package org.crue.hercules.sgi.pii.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ComunicadosService {
  public static final String CONFIG_PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_DESTINATARIOS = "pii-com-fecha-fin-pri-sol-prot-destinatarios-";

  private final SolicitudProteccionComService solicitudProteccionComService;

  public void enviarComunicadoMesesHastaFinPrioridadSolicitudProteccion(Integer meses) {
    this.solicitudProteccionComService.enviarComunicadoHastaFinPrioridadSolicitudProteccion(meses);
  }

}
