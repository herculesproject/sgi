package org.crue.hercules.sgi.pii.service;

import java.time.Instant;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.crue.hercules.sgi.pii.config.SgiConfigProperties;
import org.crue.hercules.sgi.pii.dto.com.EmailOutput;
import org.crue.hercules.sgi.pii.dto.com.PiiComMesesHastaFinPrioridadSolicitudProteccionData;
import org.crue.hercules.sgi.pii.dto.com.Recipient;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.crue.hercules.sgi.pii.repository.SolicitudProteccionRepository;
import org.crue.hercules.sgi.pii.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.pii.service.sgi.SgiApiComService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class SolicitudProteccionComService {

  private static final String UG_OTRI = "1";

  private final SolicitudProteccionRepository solicitudProteccionRepository;
  private final SgiApiCnfService configService;
  private final SgiApiComService emailService;
  private final SgiConfigProperties sgiConfigProperties;

  public void enviarComunicadoHastaFinPrioridadSolicitudProteccion(Integer months) {

    Instant fechaFinPrioridadFrom = Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
        .with(LocalTime.MIN).plusMonths(months).toInstant();

    Instant fechaFinPrioridadTo = Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
        .with(LocalTime.MAX).withNano(0).plusMonths(months).toInstant();

    List<SolicitudProteccion> solicitudes = this.solicitudProteccionRepository
        .findByfechaFinPriorPresFasNacRecBetweenAndViaProteccionExtensionInternacionalTrue(fechaFinPrioridadFrom,
            fechaFinPrioridadTo);

    solicitudes.forEach(solicitud -> {
      this.emailService
          .sendEmail(this.buildComunicadoMesesHastaFinPrioridadSolicitudProteccion(solicitud, months).getId());
    });
  }

  private EmailOutput buildComunicadoMesesHastaFinPrioridadSolicitudProteccion(SolicitudProteccion solicitud,
      Integer monthsBeforeFechaFinPrioridad) {
    PiiComMesesHastaFinPrioridadSolicitudProteccionData data = PiiComMesesHastaFinPrioridadSolicitudProteccionData
        .builder()
        .solicitudTitle(solicitud.getTitulo())
        .fechaFinPrioridad(solicitud.getFechaFinPriorPresFasNacRec())
        .monthsBeforeFechaFinPrioridad(monthsBeforeFechaFinPrioridad)
        .build();

    List<Recipient> recipients = getRecipientsUG();
    EmailOutput comunicado = null;
    try {
      comunicado = this.emailService.createComunicadoMesesHastaFinPrioridadSolicitudProteccion(data, recipients);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
    return comunicado;
  }

  private List<Recipient> getRecipientsUG() {
    List<String> destinatarios = null;
    try {
      destinatarios = configService
          .findStringListByName(
              ComunicadosService.CONFIG_PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_DESTINATARIOS
                  + UG_OTRI);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
    if (destinatarios == null) {
      return new LinkedList<>();
    }

    return destinatarios.stream()
        .map(destinatario -> Recipient.builder().name(destinatario).address(destinatario).build())
        .collect(Collectors.toList());
  }
}
