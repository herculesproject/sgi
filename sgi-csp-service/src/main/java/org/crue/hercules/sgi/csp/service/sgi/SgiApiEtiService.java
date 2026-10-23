package org.crue.hercules.sgi.csp.service.sgi;

import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.eti.ChecklistOutput;
import org.crue.hercules.sgi.csp.dto.eti.EquipoTrabajo;
import org.crue.hercules.sgi.csp.dto.eti.EstadoFinanciacionDto;
import org.crue.hercules.sgi.csp.dto.eti.PeticionEvaluacionEstadoFinanciacionInput;
import org.crue.hercules.sgi.csp.dto.eti.PeticionEvaluacionOutput;
import org.crue.hercules.sgi.csp.dto.eti.PeticionEvaluacionSolicitudCspInput;
import org.crue.hercules.sgi.csp.enums.ServiceType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SgiApiEtiService extends SgiApiBaseService {

  protected SgiApiEtiService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  public ChecklistOutput getCheckList(String checklistRef) {
    log.debug("getCheckList(String checklistRef) - start");
    ServiceType serviceType = ServiceType.ETI;
    String relativeUrl = "/checklists/{checklistRef}";
    HttpMethod httpMethod = HttpMethod.GET;
    String mergedURL = buildUri(serviceType, relativeUrl);

    final ChecklistOutput response = super.<ChecklistOutput>callEndpoint(mergedURL, httpMethod,
        new ParameterizedTypeReference<ChecklistOutput>() {
        }, Long.parseLong(checklistRef)).getBody();

    log.debug("getCheckList(String checklistRef) - end");
    return response;
  }

  /**
   * Crea en ETI la petición de evaluación ética de una solicitud.
   *
   * @param peticionEvaluacion datos de la petición de evaluación a crear.
   * @return la {@link PeticionEvaluacionOutput} creada.
   */
  public PeticionEvaluacionOutput newPeticionEvaluacion(PeticionEvaluacionSolicitudCspInput peticionEvaluacion) {
    log.debug("newPeticionEvaluacion - peticionEvaluacion: {}", peticionEvaluacion);
    ServiceType serviceType = ServiceType.ETI;
    String relativeUrl = "/peticionevaluaciones/solicitud-csp";
    HttpMethod httpMethod = HttpMethod.POST;
    String mergedURL = buildUri(serviceType, relativeUrl);

    final PeticionEvaluacionOutput response = super.<PeticionEvaluacionSolicitudCspInput, PeticionEvaluacionOutput>callEndpoint(
        mergedURL, httpMethod,
        peticionEvaluacion, new ParameterizedTypeReference<PeticionEvaluacionOutput>() {
        }).getBody();

    log.debug("newPeticionEvaluacion - response: {}", response);
    return response;
  }

  /**
   * Actualiza el estado de financiación de una petición de evaluación de ETI.
   *
   * @param peticionEvaluacionRef identificador de la petición de evaluación.
   * @param estadoFinanciacion    nuevo {@link EstadoFinanciacionDto}.
   */
  public void updateEstadoFinanciacionPeticionEvaluacion(String peticionEvaluacionRef,
      EstadoFinanciacionDto estadoFinanciacion) {
    log.debug("updateEstadoFinanciacionPeticionEvaluacion - peticionEvaluacionRef: {}, estadoFinanciacion: {}",
        peticionEvaluacionRef, estadoFinanciacion);
    ServiceType serviceType = ServiceType.ETI;
    String relativeUrl = "/peticionevaluaciones/{peticionEvaluacionRef}/estado-financiacion";
    HttpMethod httpMethod = HttpMethod.PATCH;
    String mergedURL = buildUri(serviceType, relativeUrl);

    super.<PeticionEvaluacionEstadoFinanciacionInput, Void>callEndpoint(mergedURL, httpMethod,
        new PeticionEvaluacionEstadoFinanciacionInput(estadoFinanciacion), new ParameterizedTypeReference<Void>() {
        }, peticionEvaluacionRef);
  }

  public EquipoTrabajo newEquipoTrabajo(Long id,
      EquipoTrabajo equipoTrabajo) {
    log.debug("newEquipoTrabajo(String peticionEvaluacionRef, EquipoTrabajo equipoTrabajo) - start");
    ServiceType serviceType = ServiceType.ETI;
    String relativeUrl = "/peticionevaluaciones/{id}/equipos-trabajo";
    HttpMethod httpMethod = HttpMethod.POST;
    String mergedURL = buildUri(serviceType, relativeUrl);

    final EquipoTrabajo response = super.<EquipoTrabajo, EquipoTrabajo>callEndpoint(mergedURL, httpMethod,
        equipoTrabajo, new ParameterizedTypeReference<EquipoTrabajo>() {
        }, id).getBody();

    log.debug("newEquipoTrabajo(String peticionEvaluacionRef, EquipoTrabajo equipoTrabajo) - end");
    return response;
  }
}
