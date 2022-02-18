package org.crue.hercules.sgi.csp.service.sgp;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.sgp.PersonaOutput;
import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PersonaService {

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public PersonaService(RestTemplate restTemplate, RestApiProperties restApiProperties) {
    this.restTemplate = restTemplate;
    this.baseUrl = restApiProperties.getSgpUrl() + "/personas";
  }

  /**
   * Obtiene los datos de una persona, de SGP, a través de su identificador
   * 
   * @param id Identificador de persona
   * @return {@link PersonaOutput}
   */
  public PersonaOutput findById(String id) {
    log.debug("findById({}) - start", id);

    Assert.notNull(id, "ID is required");

    ResponseEntity<PersonaOutput> personaResponse = restTemplate.exchange(
        baseUrl + "/" + id, HttpMethod.GET,
        new HttpEntityBuilder<>().withClientAuthorization("csp-service").build(),
        PersonaOutput.class);

    log.debug("findById({}) - end", id);
    return personaResponse.getBody();
  }

  /**
   * Obtienes los datos de varias personas, de SGP, a través de sus identificadoes
   * 
   * @param ids Listado de identificadores de persona
   * @return Listado de {@link PersonaOutput}
   */
  public List<PersonaOutput> findAllByIdIn(List<String> ids) {
    log.debug("findAllByIdIn({}) - start", ids);

    Assert.notEmpty(ids, "At least one ID is required");
    Assert.noNullElements(ids, "The IDs list must not contain null elements");

    String in = ids.stream().map(id -> StringUtils.wrap(id, "\"")).collect(Collectors.joining(","));

    ResponseEntity<List<PersonaOutput>> personaResponse = restTemplate.exchange(
        baseUrl + "/?q=id=in=(" + in + ")", HttpMethod.GET,
        new HttpEntityBuilder<>().withClientAuthorization("csp-service").build(),
        new ParameterizedTypeReference<List<PersonaOutput>>() {
        });

    log.debug("findAllByIdIn({}) - end", ids);

    return personaResponse.getBody();
  }

}
