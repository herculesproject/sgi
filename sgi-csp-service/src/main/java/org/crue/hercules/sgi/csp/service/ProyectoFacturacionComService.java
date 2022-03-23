package org.crue.hercules.sgi.csp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crue.hercules.sgi.csp.dto.com.CspComCalendarioFacturacionValidarIPData;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.sgp.PersonaOutput;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoValidacionIP.TipoEstadoValidacion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoFacturacion;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.repository.ProyectoProyectoSgeRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProyectoFacturacionComService {
  private static final String CONFIG_CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_DESTINATARIOS = "csp-com-cal-fact-validarip-destinatarios-";

  private final ProyectoRepository proyectoRepository;
  private final ProyectoProyectoSgeRepository proyectoProyectoSgeRepository;
  private final SgiApiCnfService configService;
  private final SgiApiComService emailService;
  private final SgiApiSgpService sgiApiSgpService;
  private final ProyectoHelper proyectoHelper;

  public void enviarComunicado(ProyectoFacturacion proyectoFacturacion) {
    CspComCalendarioFacturacionValidarIPData data = null;

    if (!TipoEstadoValidacion.VALIDADA.equals(proyectoFacturacion.getEstadoValidacionIP().getEstado())
        && !TipoEstadoValidacion.RECHAZADA
            .equals(proyectoFacturacion.getEstadoValidacionIP().getEstado())) {
      return;
    }

    if ((!proyectoHelper.hasUserAuthorityInvestigador()
        && !proyectoHelper.checkIfUserIsInvestigadorPrincipal(proyectoFacturacion
            .getProyectoId()))
        && !proyectoHelper.checkUserIsResponsableEconomico(proyectoFacturacion.getProyectoId())) {
      return;
    }

    Proyecto proyecto = proyectoRepository.findById(proyectoFacturacion.getProyectoId())
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoFacturacion.getProyectoId()));

    data = buildCspComCalendarioFacturacionValidarIpData(proyectoFacturacion, proyecto);

    if (TipoEstadoValidacion.VALIDADA.equals(proyectoFacturacion.getEstadoValidacionIP().getEstado())) {
      this.enviarComunicadoValidarIpValidada(data, proyecto.getUnidadGestionRef());
    } else if (TipoEstadoValidacion.RECHAZADA.equals(proyectoFacturacion.getEstadoValidacionIP().getEstado())) {
      data.setMotivoRechazo(proyectoFacturacion.getEstadoValidacionIP().getComentario());
      this.enviarComunicadoValidarIpRechazada(data, proyecto.getUnidadGestionRef());
    }
  }

  private void enviarComunicadoValidarIpValidada(CspComCalendarioFacturacionValidarIPData data, String unidadGestion) {
    EmailOutput output;
    try {
      output = this.emailService.createComunicadoCalendarioFacturacionValidarIpValidada(data,
          this.getRecipientsUG(unidadGestion));
      this.emailService.sendEmail(output.getId());
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }

  }

  private void enviarComunicadoValidarIpRechazada(CspComCalendarioFacturacionValidarIPData data, String unidadGestion) {
    EmailOutput output;
    try {
      output = this.emailService.createComunicadoCalendarioFacturacionValidarIpRechazada(data,
          this.getRecipientsUG(unidadGestion));
      this.emailService.sendEmail(output.getId());
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
  }

  private CspComCalendarioFacturacionValidarIPData buildCspComCalendarioFacturacionValidarIpData(
      ProyectoFacturacion proyectoFacturacion, Proyecto proyecto) {

    String personaRef = SecurityContextHolder.getContext().getAuthentication().getName();

    PersonaOutput persona = this.sgiApiSgpService.findById(personaRef);

    List<String> codigosSge = this.proyectoProyectoSgeRepository.findByProyectoId(proyecto.getId()).stream()
        .map(ProyectoProyectoSge::getProyectoSgeRef).collect(Collectors.toList());

    return CspComCalendarioFacturacionValidarIPData.builder()
        .tituloProyecto(proyecto.getTitulo())
        .numPrevision(proyectoFacturacion.getNumeroPrevision())
        .codigosSge(codigosSge)
        .motivoRechazo("")
        .nombreApellidosValidador(persona.getNombre() + " " + persona.getApellidos())
        .build();
  }

  private List<Recipient> getRecipientsUG(String unidadGestionRef) throws JsonProcessingException {
    return configService
        .findStringListByName(
            CONFIG_CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_DESTINATARIOS + unidadGestionRef)
        .stream()
        .map(destinatario -> Recipient.builder().name(destinatario).address(destinatario).build())
        .collect(Collectors.toList());
  }
}
