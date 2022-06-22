package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.dto.com.CspComCambioEstadoSolicitadaSolTipoRrhhData;
import org.crue.hercules.sgi.csp.dto.com.CspComCambioEstadoSolicitadaSolTipoRrhhData.CspComCambioEstadoSolicitadaSolTipoRrhhDataBuilder;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.sgp.PersonaOutput;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgpService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SolicitudRrhhComService {

  private static final String PATH_MENU_VALIDACION_TUTOR = "/inv/solicitudes/validacion-tutor";

  private final ConvocatoriaRepository convocatoriaRepository;
  private final SgiApiSgpService sgiApiSgpService;
  private final SgiApiComService emailService;
  private final SgiConfigProperties sgiConfigProperties;

  public void enviarComunicadoCambioEstadoSolicitadaSolTipoRrhh(Instant fechaEstado, Solicitud solicitud) {

    CspComCambioEstadoSolicitadaSolTipoRrhhDataBuilder dataBuilder = CspComCambioEstadoSolicitadaSolTipoRrhhData
        .builder()
        .fechaEstado(fechaEstado)
        .nombreApellidosSolicitante(getSolicitanteNombreApellidos(solicitud.getSolicitanteRef()))
        .codigoInternoSolicitud(solicitud.getCodigoRegistroInterno())
        .enlaceAplicacionMenuValidacionTutor(getEnlaceAplicacionMenuValidacionTutor());

    this.fillConvocatoriaData(dataBuilder, solicitud.getConvocatoriaId());

    try {
      log.debug(
          "Construyendo comunicado aviso cambio de estado SOLICITADA a la solicitud de tipo RRHH {} para enviarlo inmediatamente al solicitante",
          solicitud.getId());

      EmailOutput comunicado = this.emailService
          .createComunicadoCambioEstadoSolicitadaSolTipoRrhh(dataBuilder.build(),
              this.getSolicitanteRecipients(solicitud.getSolicitanteRef()));
      this.emailService.sendEmail(comunicado.getId());

    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
  }

  private void fillConvocatoriaData(CspComCambioEstadoSolicitadaSolTipoRrhhDataBuilder dataBuilder,
      Long convocatoriaId) {
    if (convocatoriaId != null) {
      Optional<Convocatoria> convocatoria = this.convocatoriaRepository.findById(convocatoriaId);
      if (convocatoria.isPresent()) {
        dataBuilder.tituloConvocatoria(convocatoria.get().getTitulo());
        dataBuilder.fechaProvisionalConvocatoria(convocatoria.get().getFechaProvisional());
      }
    }
  }

  private String getEnlaceAplicacionMenuValidacionTutor() {
    return this.sgiConfigProperties.getWebUrl() + PATH_MENU_VALIDACION_TUTOR;
  }

  private List<Recipient> getSolicitanteRecipients(String solicitanteRef) {
    PersonaOutput datosSolicitante = this.sgiApiSgpService.findById(solicitanteRef);

    return datosSolicitante.getEmails().stream().filter(email -> email.getPrincipal())
        .map(email -> Recipient
            .builder().name(email.getEmail()).address(email.getEmail())
            .build())
        .collect(Collectors.toList());
  }

  private String getSolicitanteNombreApellidos(String solicitanteRef) {
    return Optional.of(this.sgiApiSgpService.findById(solicitanteRef))
        .map(datos -> String.format("%s %s", datos.getNombre(), datos.getApellidos())).orElse(StringUtils.EMPTY);
  }

}
