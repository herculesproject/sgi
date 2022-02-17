package org.crue.hercules.sgi.prc.service.sgp;

import java.util.Optional;

import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.prc.config.RestApiProperties;
import org.crue.hercules.sgi.prc.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.prc.exceptions.MicroserviceCallException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PersonaService {
  private static final String URL_API = "/personas";

  private final RestApiProperties restApiProperties;
  private final RestTemplate restTemplate;

  public PersonaService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    this.restApiProperties = restApiProperties;
    this.restTemplate = restTemplate;
  }

  /**
   * Devuelve datos de una persona a través de una consulta al ESB SGP
   *
   * @param personaRef String
   * @return Optional de {@link PersonaDto}
   */
  public Optional<PersonaDto> findById(String personaRef) {
    log.debug("findById(personaRef)- start");
    Optional<PersonaDto> persona = Optional.empty();

    if (StringUtils.hasText(personaRef)) {
      try {
        StringBuilder url = new StringBuilder();
        url.append(restApiProperties.getSgpUrl());
        url.append(URL_API);
        url.append("/");
        url.append(personaRef);

        final ResponseEntity<PersonaDto> response = restTemplate.exchange(url.toString(), HttpMethod.GET,
            new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), PersonaDto.class);

        persona = Optional.of(response.getBody());
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        throw new MicroserviceCallException();
      }
    }

    log.debug("findById(personaRef)- end");
    return persona;
  }

}
