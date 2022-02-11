package org.crue.hercules.sgi.com.service;

import java.net.URI;

import javax.activation.DataSource;

import org.crue.hercules.sgi.com.config.RestApiProperties;
import org.crue.hercules.sgi.com.dto.Document;
import org.crue.hercules.sgi.com.enums.ServiceType;
import org.crue.hercules.sgi.com.model.DocumentDataSource;
import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiApiSgdocService extends SgiApiBaseService {
  private final RestTemplate restTemplate;

  public SgiApiSgdocService(RestTemplate restTemplate, RestApiProperties restApiProperties) {
    super(restApiProperties);
    this.restTemplate = restTemplate;
  }

  public DataSource call(String documentRef) {
    ServiceType serviceType = ServiceType.SGDOC;
    String relativeUrl = "/documentos/" + documentRef;
    HttpMethod httpMethod = HttpMethod.GET;
    log.info("Calling SGI API Service: {} {} {}", serviceType, relativeUrl, httpMethod);
    URI mergedURI = buildUri(ServiceType.SGDOC, relativeUrl);
    HttpEntity<Void> request = buildHttpEntityRequest();

    final ResponseEntity<Document> response = restTemplate.exchange(mergedURI
        .toString(),
        httpMethod,
        request,
        Document.class);
    Document document = response.getBody();

    String resourceRelativeUrl = "/documentos/" + documentRef + "/archivo";
    URI resourceMergedURL = buildUri(ServiceType.SGDOC, resourceRelativeUrl);
    final ResponseEntity<Resource> resourceResponse = restTemplate.exchange(resourceMergedURL
        .toString(),
        httpMethod,
        request,
        Resource.class);
    Resource resource = resourceResponse.getBody();

    log.info("SGI API Service response: {}", response);
    return new DocumentDataSource(document, resource);
  }

  @Override
  protected HttpEntity<Void> buildHttpEntityRequest() {
    // TODO Implement B2B auth in ESB and delete this method
    log.debug("buildHttpEntityRequest() - start");
    HttpEntity<Void> request = new HttpEntityBuilder<Void>().withCurrentUserAuthorization().build();
    log.debug("buildHttpEntityRequest() - end");
    return request;
  }
}
