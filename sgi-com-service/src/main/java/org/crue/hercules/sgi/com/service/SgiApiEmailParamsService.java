package org.crue.hercules.sgi.com.service;

import java.net.URI;
import java.util.List;

import org.crue.hercules.sgi.com.config.RestApiProperties;
import org.crue.hercules.sgi.com.dto.EmailParam;
import org.crue.hercules.sgi.com.enums.ServiceType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiApiEmailParamsService extends SgiApiBaseService {
  private final RestTemplate restTemplate;

  public SgiApiEmailParamsService(RestTemplate restTemplate, RestApiProperties restApiProperties) {
    super(restApiProperties);
    this.restTemplate = restTemplate;
  }

  public List<EmailParam> call(
      ServiceType serviceType, String relativeUrl,
      HttpMethod httpMethod) {
    log.info("Calling SGI API Service: {} {} {}", serviceType, relativeUrl, httpMethod);
    URI mergedURI = buildUri(serviceType, relativeUrl);
    HttpEntity<Void> request = buildHttpEntityRequest();

    final ResponseEntity<List<EmailParam>> response = restTemplate.exchange(mergedURI
        .toString(),
        httpMethod,
        request,
        new ParameterizedTypeReference<List<EmailParam>>() {
        });

    log.info("SGI API Service response: {}", response);
    return response.getBody();
  }

}
