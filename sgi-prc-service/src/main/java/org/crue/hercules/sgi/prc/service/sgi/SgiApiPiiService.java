package org.crue.hercules.sgi.prc.service.sgi;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.prc.config.RestApiProperties;
import org.crue.hercules.sgi.prc.dto.csp.InvencionDto;
import org.crue.hercules.sgi.prc.enums.ServiceType;
import org.crue.hercules.sgi.prc.exceptions.MicroserviceCallException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SgiApiPiiService extends SgiApiBaseService {

  public SgiApiPiiService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  /**
   * Devuelve una lista de {@link InvencionDto} que se incorporarán a la
   * baremación
   * de producción científica
   * 
   * @param anio año de baremación
   * @return lista de {@link InvencionDto}
   */
  public List<InvencionDto> findInvencionesProduccionCientifica(Integer anio) {
    List<InvencionDto> result = new ArrayList<>();
    log.debug("findInvencionesProduccionCientifica(anio)- start");

    try {
      ServiceType serviceType = ServiceType.PII;
      String relativeUrl = "/invenciones/produccioncientifica/{anio}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<List<InvencionDto>>callEndpointWithCurrentUserAuthorization(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<InvencionDto>>() {
          }, anio).getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    log.debug("findInvencionesProduccionCientifica(anio)- end");

    return ObjectUtils.defaultIfNull(result, new ArrayList<>());
  }

  /**
   * Devuelve una lista de personas que se incorporarán a la baremación
   * de producción científica
   *
   * @param invencionId id de {@link InvencionDto}
   * @param anio        año de baremación
   * @return lista de personaRef
   */
  public List<String> findInvencionInventorByInvencionIdAndAnio(Long invencionId, Integer anio) {
    List<String> result = new ArrayList<>();
    log.debug("findInvencionInventorByInvencionIdAndAnio(proyectoId, anio)- start");

    try {
      ServiceType serviceType = ServiceType.PII;
      String relativeUrl = "/invenciones/produccioncientifica/equipo/{invencionId}/{anio}";
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<List<String>>callEndpointWithCurrentUserAuthorization(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<String>>() {
          }, invencionId, anio).getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    log.debug("findInvencionInventorByInvencionIdAndAnio(proyectoId, anio)- end");

    return ObjectUtils.defaultIfNull(result, new ArrayList<>());
  }

}
