package org.crue.hercules.sgi.eti.service.sgi;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.eti.config.RestApiProperties;
import org.crue.hercules.sgi.eti.dto.sgp.PersonaOutput;
import org.crue.hercules.sgi.eti.dto.sgp.VinculacionOutput;
import org.crue.hercules.sgi.eti.enums.ServiceType;
import org.crue.hercules.sgi.eti.exceptions.rep.MicroserviceCallException;
import org.crue.hercules.sgi.framework.util.AssertHelper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SgiApiSgpService extends SgiApiBaseService {

  public SgiApiSgpService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  /**
   * Obtiene los datos de una persona, de SGP, a través de su identificador
   * 
   * @param id Identificador de persona
   * @return {@link PersonaOutput}
   */
  public PersonaOutput findById(String id) {
    log.debug("findById({}) - start", id);

    AssertHelper.idNotNull(id, PersonaOutput.class);

    ServiceType serviceType = ServiceType.SGP;
    String relativeUrl = "/personas/{id}";
    HttpMethod httpMethod = HttpMethod.GET;
    String mergedURL = buildUri(serviceType, relativeUrl);

    final PersonaOutput response = super.<PersonaOutput>callEndpoint(mergedURL, httpMethod,
        new ParameterizedTypeReference<PersonaOutput>() {
        }, id).getBody();

    log.debug("findById({}) - end", id);
    return response;
  }

  /**
   * Obtienes los datos de varias personas, de SGP, a través de sus identificadoes
   * 
   * @param ids Listado de identificadores de persona
   * @return Listado de {@link PersonaOutput}
   */
  public List<PersonaOutput> findAllByIdIn(List<String> ids) {
    log.debug("findAllByIdIn({}) - start", ids);
    AssertHelper.fieldNotEmpty(ids, PersonaOutput.class, "id");
    AssertHelper.fieldNoNullElements(ids, PersonaOutput.class, "id");

    String in = ids.stream().map(id -> StringUtils.wrap(id, "\"")).collect(Collectors.joining(","));

    ServiceType serviceType = ServiceType.SGP;
    String relativeUrl = "/personas/?q=id=in=({in})";
    HttpMethod httpMethod = HttpMethod.GET;
    String mergedURL = buildUri(serviceType, relativeUrl);

    final List<PersonaOutput> response = super.<List<PersonaOutput>>callEndpoint(mergedURL,
        httpMethod,
        new ParameterizedTypeReference<List<PersonaOutput>>() {
        }, in).getBody();

    log.debug("findAllByIdIn({}) - end", ids);
    return response;
  }

  /**
   * Obtiene la vinculación de una persona en el SGP.
   * 
   * @param personaId Identificador de la persona
   * @return la {@link VinculacionOutput} de la persona
   * @throws MicroserviceCallException si se produce un error en la llamada
   */
  public Optional<VinculacionOutput> findVinculacionByPersonaId(String personaId) {
    log.debug("findVinculacionByPersonaId - personaId: {}", personaId);
    AssertHelper.idNotNull(personaId, PersonaOutput.class);

    try {
      String mergedURL = buildUri(ServiceType.SGP, "/vinculaciones/persona/{id}");
      VinculacionOutput response = super.<VinculacionOutput>callEndpoint(mergedURL, HttpMethod.GET,
          new ParameterizedTypeReference<VinculacionOutput>() {
          }, personaId).getBody();
      return Optional.ofNullable(response);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
  }

}
