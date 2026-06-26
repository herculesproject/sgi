package org.crue.hercules.sgi.csp.service.sgi;

import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.pii.InvencionOutput;
import org.crue.hercules.sgi.csp.enums.ServiceType;
import org.crue.hercules.sgi.csp.exceptions.rel.GetRelacionesException;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Cliente del API REST del módulo PII.
 */
@Component
@Slf4j
public class SgiApiPiiService extends SgiApiBaseService {

  public SgiApiPiiService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  /**
   * Recupera la representación mínima de la {@link InvencionOutput} indicada.
   *
   * @param invencionId identificador de la invención
   * @return la invención recuperada de PII
   */
  public InvencionOutput findInvencionById(Long invencionId) {
    log.debug("findInvencionById(Long invencionId) - start");
    try {
      String relativeUrl = "/invenciones/{invencionId}";
      String mergedURL = buildUri(ServiceType.PII, relativeUrl);

      InvencionOutput result = super.<InvencionOutput>callEndpoint(mergedURL, HttpMethod.GET,
          new ParameterizedTypeReference<InvencionOutput>() {
          }, invencionId).getBody();

      log.debug("findInvencionById(Long invencionId) - end");
      return result;
    } catch (ProblemException e) {
      log.error(e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetRelacionesException();
    }
  }

}
