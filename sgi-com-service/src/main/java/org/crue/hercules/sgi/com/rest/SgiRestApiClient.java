package org.crue.hercules.sgi.com.rest;

import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiRestApiClient {
  public static final String CLIENT_REGISTRATION_ID = "com-service";
  private final RestTemplate restTemplate;

  public SgiRestApiClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public ResponseEntity<String> callEndpoint(String endPoint, HttpMethod httpMethod) {
    log.info("Calling SGI API endpoint: {}", endPoint);
    HttpEntity<?> request = new HttpEntityBuilder<>().withClientAuthorization(CLIENT_REGISTRATION_ID).build();

    ResponseEntity<String> response = restTemplate.exchange(endPoint, httpMethod, request, String.class);

    log.info("Endpoint response: {}", response);
    return response;
  }
}
