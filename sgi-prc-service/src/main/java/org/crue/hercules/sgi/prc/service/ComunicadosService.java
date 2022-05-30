package org.crue.hercules.sgi.prc.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.crue.hercules.sgi.prc.dto.com.EmailOutput;
import org.crue.hercules.sgi.prc.dto.com.PrcComProcesoBaremacionErrorData;
import org.crue.hercules.sgi.prc.dto.com.PrcComProcesoBaremacionFinData;
import org.crue.hercules.sgi.prc.dto.com.Recipient;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiComService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ComunicadosService {

  private static final String DESTINATARIOS_PROCESO_BAREMACION = "prc-proceso-baremacion-destinatarios";

  private final SgiApiCnfService configService;
  private final SgiApiComService emailService;

  /**
   * Envia el comunicado de error en el proceso de baremacion en la
   * {@link ConvocatoriaBaremacion}
   * 
   * @param convocatoriaBaremacion la {@link ConvocatoriaBaremacion}
   * @param error                  texto error
   */
  public void enviarComunicadoErrorProcesoBaremacion(ConvocatoriaBaremacion convocatoriaBaremacion, String error) {
    log.debug(
        "enviarComunicadoErrorProcesoBaremacion(ConvocatoriaBaremacion convocatoriaBaremacion, String error) - start");
    try {
      this.buildComunicadoErrorProcesoBaremacion(convocatoriaBaremacion, error)
          .ifPresent(email -> this.emailService.sendEmail(email.getId()));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    log.debug(
        "enviarComunicadoErrorProcesoBaremacion(ConvocatoriaBaremacion convocatoriaBaremacion, String error) - end");
  }

  /**
   * Envia el comunicado de fin del proceso de baremacion de la
   * {@link ConvocatoriaBaremacion}
   * 
   * @param convocatoriaBaremacion la {@link ConvocatoriaBaremacion}
   */
  public void enviarComunicadoFinProcesoBaremacion(ConvocatoriaBaremacion convocatoriaBaremacion) {
    log.debug("enviarComunicadoFinProcesoBaremacion(ConvocatoriaBaremacion convocatoriaBaremacion) - start");
    try {
      this.buildComunicadoFinProcesoBaremacion(convocatoriaBaremacion)
          .ifPresent(email -> this.emailService.sendEmail(email.getId()));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    log.debug("enviarComunicadoFinProcesoBaremacion(ConvocatoriaBaremacion convocatoriaBaremacion) - end");
  }

  private Optional<EmailOutput> buildComunicadoErrorProcesoBaremacion(ConvocatoriaBaremacion convocatoriaBaremacion,
      String error) {
    PrcComProcesoBaremacionErrorData data = PrcComProcesoBaremacionErrorData
        .builder()
        .anio(convocatoriaBaremacion.getAnio().toString())
        .error(error)
        .build();

    EmailOutput comunicado = null;
    try {
      List<Recipient> recipients = getRecipientsProcesoBaremacion();
      comunicado = this.emailService.createComunicadoErrorProcesoBaremacion(data, recipients);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
    return comunicado != null ? Optional.of(comunicado) : Optional.empty();
  }

  private Optional<EmailOutput> buildComunicadoFinProcesoBaremacion(ConvocatoriaBaremacion convocatoriaBaremacion) {
    PrcComProcesoBaremacionFinData data = PrcComProcesoBaremacionFinData
        .builder()
        .anio(convocatoriaBaremacion.getAnio().toString())
        .build();

    EmailOutput comunicado = null;
    try {
      List<Recipient> recipients = getRecipientsProcesoBaremacion();
      comunicado = this.emailService.createComunicadoFinProcesoBaremacion(data, recipients);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
    return comunicado != null ? Optional.of(comunicado) : Optional.empty();
  }

  private List<Recipient> getRecipientsProcesoBaremacion() throws JsonProcessingException {
    List<String> destinatarios = configService
        .findStringListByName(DESTINATARIOS_PROCESO_BAREMACION);

    return destinatarios.stream()
        .map(destinatario -> Recipient.builder().name(destinatario).address(destinatario).build())
        .collect(Collectors.toList());
  }

}
