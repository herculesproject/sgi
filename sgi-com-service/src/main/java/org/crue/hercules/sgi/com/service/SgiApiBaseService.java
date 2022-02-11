package org.crue.hercules.sgi.com.service;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hc.core5.net.URIBuilder;
import org.crue.hercules.sgi.com.config.RestApiProperties;
import org.crue.hercules.sgi.com.enums.ServiceType;
import org.crue.hercules.sgi.com.exceptions.UnknownServiceTypeException;
import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.springframework.http.HttpEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SgiApiBaseService {
  public static final String CLIENT_REGISTRATION_ID = "com-service";
  private final RestApiProperties restApiProperties;

  protected SgiApiBaseService(RestApiProperties restApiProperties) {
    this.restApiProperties = restApiProperties;
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

  protected HttpEntity<Void> buildHttpEntityRequest() {
    log.debug("buildHttpEntityRequest() - start");
    HttpEntity<Void> request = new HttpEntityBuilder<Void>().withClientAuthorization(CLIENT_REGISTRATION_ID).build();
    log.debug("buildHttpEntityRequest() - end");
    return request;
  }
}
