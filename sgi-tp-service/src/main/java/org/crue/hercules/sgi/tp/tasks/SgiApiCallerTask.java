package org.crue.hercules.sgi.tp.tasks;

import java.net.MalformedURLException;
import java.net.URL;

import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.tp.config.RestApiProperties;
import org.crue.hercules.sgi.tp.enums.ServiceType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiApiCallerTask {
  public static final String CLIENT_REGISTRATION_ID = "tp-service";
  private final RestTemplate restTemplate;
  private final RestApiProperties restApiProperties;

  public SgiApiCallerTask(RestTemplate restTemplate, RestApiProperties restApiProperties) {
    this.restTemplate = restTemplate;
    this.restApiProperties = restApiProperties;
  }

  public String call(String serviceType, String relativePath) throws MalformedURLException {
    log.info("Calling SGI API Service {} path: {}", serviceType, relativePath);
    String serviceURL = null;
    switch (ServiceType.valueOf(serviceType)) {
      case COM:
        serviceURL = restApiProperties.getComUrl();
        break;
      case CSP:
        serviceURL = restApiProperties.getCspUrl();
        break;
      case ETI:
        serviceURL = restApiProperties.getEtiUrl();
        break;
      case PII:
        serviceURL = restApiProperties.getPiiUrl();
        break;
      case REL:
        serviceURL = restApiProperties.getRelUrl();
        break;
      case REP:
        serviceURL = restApiProperties.getRepUrl();
        break;
      case USR:
        serviceURL = restApiProperties.getUsrUrl();
        break;
    }
    HttpEntity<?> request = new HttpEntityBuilder<>().withClientAuthorization(CLIENT_REGISTRATION_ID).build();

    String endPoint = (new URL(new URL(serviceURL), relativePath)).toString();
    ResponseEntity<String> response = restTemplate.exchange(endPoint, HttpMethod.PATCH, request, String.class);

    String result = response.getBody();
    log.info("SGI API Service response: {}", result);
    return result;
  }
}
