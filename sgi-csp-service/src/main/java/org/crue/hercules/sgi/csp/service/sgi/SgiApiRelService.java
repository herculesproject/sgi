package org.crue.hercules.sgi.csp.service.sgi;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.rel.RelacionOutput;
import org.crue.hercules.sgi.csp.enums.ServiceType;
import org.crue.hercules.sgi.csp.exceptions.rel.GetRelacionesException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Cliente del API REST del módulo REL.
 */
@Component
@Slf4j
public class SgiApiRelService extends SgiApiBaseService {

  public SgiApiRelService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  /**
   * Recupera todas las {@link RelacionOutput} en las que participa el
   * {@link Proyecto} indicado (como origen o como destino).
   *
   * @param proyectoId identificador del proyecto
   * @return la lista de relaciones del proyecto
   */
  public List<RelacionOutput> findRelacionesProyecto(Long proyectoId) {
    log.debug("findRelacionesProyecto - proyectoId: {}", proyectoId);
    List<RelacionOutput> result = new ArrayList<>();

    try {
      String relativeUrl = "/relaciones?s=id,asc&q=proyectoRef==" + proyectoId;
      String mergedURL = buildUri(ServiceType.REL, relativeUrl);

      result = super.<List<RelacionOutput>>callEndpoint(mergedURL, HttpMethod.GET,
          new ParameterizedTypeReference<List<RelacionOutput>>() {
          }).getBody();
    } catch (ProblemException e) {
      log.error(e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetRelacionesException();
    }

    return ObjectUtils.defaultIfNull(result, new ArrayList<>());
  }

}
