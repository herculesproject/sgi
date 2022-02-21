package org.crue.hercules.sgi.com.service.sgi;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hc.core5.net.URIBuilder;
import org.crue.hercules.sgi.com.config.RestApiProperties;
import org.crue.hercules.sgi.com.enums.ServiceType;
import org.crue.hercules.sgi.com.exceptions.UnknownServiceTypeException;
import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SgiApiBaseService {
  public static final String CLIENT_REGISTRATION_ID = "com-service";
  private final RestApiProperties restApiProperties;
  private final RestTemplate restTemplate;

  protected SgiApiBaseService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    this.restApiProperties = restApiProperties;
    this.restTemplate = restTemplate;
  }

  protected URI buildUri(
      ServiceType serviceType, String relativeUrl) {
    log.debug("buildUrl(ServiceType serviceType, String relativeUrl) - start");
    String serviceURL = null;
    switch (serviceType) {
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
      case SGDOC:
        serviceURL = restApiProperties.getSgdocUrl();
        break;
      case TP:
        serviceURL = restApiProperties.getTpUrl();
        break;
      case USR:
        serviceURL = restApiProperties.getUsrUrl();
        break;
      default:
        throw new UnknownServiceTypeException(serviceType.name());
    }
    URI mergedURL;
    try {
      mergedURL = new URIBuilder(serviceURL).appendPath(relativeUrl).build();
    } catch (URISyntaxException e) {
      e.printStackTrace();
      throw new IllegalArgumentException(e);
    }
    log.debug("buildUrl(ServiceType serviceType, String relativeUrl) - end");
    return mergedURL;
  }

  protected <T> ResponseEntity<T> callEndpoint(String endPoint, HttpMethod httpMethod) {
    log.info("Calling SGI API endpoint: {}", endPoint);
    HttpEntity<Void> request = new HttpEntityBuilder<Void>().withClientAuthorization(CLIENT_REGISTRATION_ID).build();

    ResponseEntity<T> response = restTemplate.exchange(endPoint, httpMethod, request,
        new ParameterizedTypeReference<T>() {
        });

    log.info("Endpoint response: {}", response);
    return response;
  }
}
