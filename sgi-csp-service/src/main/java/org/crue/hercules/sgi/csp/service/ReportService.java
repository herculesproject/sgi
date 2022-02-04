package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.exceptions.rep.GetDataReportException;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Service de llamada a microservicio de reporting
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class ReportService {

  private static final String URL_API = "/report/csp";

  private static final String ID = "id";
  private static final String ENTITY = "entity";
  private static final String FIELD = "field";
  private static final String NOT_NULL = "notNull";

  private final RestApiProperties restApiProperties;
  private final RestTemplate restTemplate;

  public ReportService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    this.restApiProperties = restApiProperties;
    this.restTemplate = restTemplate;
  }

  /**
   * Devuelve un informe Autorizacion en pdf
   *
   * @param idAutorizacion Identificador de la Autorizacion
   * @return Resource informe
   */
  public Resource getInformeAutorizacion(Long idAutorizacion) {
    log.debug("getInformeAutorizacion(idActa)- start");
    Assert.notNull(
        idAutorizacion,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, NOT_NULL)
            .parameter(FIELD, ApplicationContextSupport.getMessage(ID))
            .parameter(ENTITY, ApplicationContextSupport.getMessage(Autorizacion.class)).build());
    Resource informe = null;
    try {

      final ResponseEntity<Resource> response = restTemplate.exchange(
          restApiProperties.getRepUrl() + URL_API + "/autorizacion-proyecto-externo/" + idAutorizacion, HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), Resource.class);

      informe = response.getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    log.debug("getInformeAutorizacion(idActa)- end");
    return informe;
  }

}