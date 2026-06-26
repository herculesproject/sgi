package org.crue.hercules.sgi.csp.service.sgi;

import java.util.Collections;
import java.util.List;

import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.sge.ColumnaOutput;
import org.crue.hercules.sgi.csp.enums.ServiceType;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.SgiLogUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Cliente del API REST del módulo SGE.
 */
@Component
@Slf4j
public class SgiApiSgeService extends SgiApiBaseService {

  public SgiApiSgeService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  /**
   * Obtiene las columnas de un tipo de operación de la ejecución económica del
   * SGE.
   *
   * @param query filtro RSQL.
   * @return la lista de {@link ColumnaOutput}.
   */
  public List<ColumnaOutput> findColumnasEjecucionEconomica(String query) {
    log.debug("findColumnasEjecucionEconomica - query: {}", query);

    ServiceType serviceType = ServiceType.SGE;
    String relativeUrl = "/ejecucion-economica/columnas?q={query}";
    HttpMethod httpMethod = HttpMethod.GET;
    String mergedURL = buildUri(serviceType, relativeUrl);

    List<ColumnaOutput> response = super.<List<ColumnaOutput>>callEndpoint(mergedURL, httpMethod,
        new ParameterizedTypeReference<List<ColumnaOutput>>() {
        }, query).getBody();

    log.debug("findColumnasEjecucionEconomica - response: {}", SgiLogUtils.collection(response));
    return response != null ? response : Collections.emptyList();
  }

  /**
   * Obtiene el detalle de una operación de la ejecución económica del SGE.
   *
   * @param id            identificador del dato económico en el SGE.
   * @param tipoOperacion tipo de operación.
   * @return el detalle de la operación (estructura del SGE).
   */
  public Object findDatoEconomicoDetalle(String id, String tipoOperacion) {
    log.debug("findDatoEconomicoDetalle - id: {}, tipoOperacion: {}", id, tipoOperacion);

    AssertHelper.fieldNotNull(id, ColumnaOutput.class, AssertHelper.MESSAGE_KEY_ID);

    ServiceType serviceType = ServiceType.SGE;
    String relativeUrl = "/ejecucion-economica/{id}?tipoOperacion={tipoOperacion}";
    HttpMethod httpMethod = HttpMethod.GET;
    String mergedURL = buildUri(serviceType, relativeUrl);

    return super.<Object>callEndpoint(mergedURL, httpMethod,
        new ParameterizedTypeReference<Object>() {
        }, id, tipoOperacion).getBody();
  }

  /**
   * Obtiene los datos económicos paginados de la ejecución económica del SGE
   * Reenvía la paginación al SGE mediante las cabeceras {@code X-Page} y
   * {@code X-Page-Size}, y reconstruye un {@link Page} a partir del total
   * devuelto en la cabecera {@code X-Total-Count}.
   *
   * @param query    filtro RSQL.
   * @param sort     criterio de ordenación.
   * @param pageable paginación solicitada.
   * @return la página de datos económicos (estructura del SGE).
   */
  public Page<Object> findDatosEconomicos(String query, String sort, Pageable pageable) {
    log.debug("findDatosEconomicos - query: {}, sort: {}, pageable: {}", query, sort, SgiLogUtils.pageable(pageable));

    HttpHeaders headers = new HttpHeaders();
    if (pageable != null && pageable.isPaged()) {
      headers.add("X-Page", String.valueOf(pageable.getPageNumber()));
      headers.add("X-Page-Size", String.valueOf(pageable.getPageSize()));
    }

    String relativeUrl = "/ejecucion-economica?q={query}&s={sort}";
    String mergedURL = buildUri(ServiceType.SGE, relativeUrl);

    ResponseEntity<List<Object>> response = super.<Void, List<Object>>callEndpoint(mergedURL, HttpMethod.GET, null,
        headers, new ParameterizedTypeReference<List<Object>>() {
        }, query != null ? query : "", sort != null ? sort : "");

    List<Object> content = response.getBody() != null ? response.getBody() : Collections.emptyList();
    String totalCount = response.getHeaders().getFirst("X-Total-Count");
    long total = StringUtils.hasText(totalCount) ? Long.parseLong(totalCount) : content.size();

    Page<Object> page = new PageImpl<>(content, pageable != null ? pageable : Pageable.unpaged(), total);
    log.debug("findDatosEconomicos - response: {}", SgiLogUtils.page(page));
    return page;
  }

  /**
   * Obtiene los datos de un proyecto del SGE por su referencia.
   *
   * @param proyectoSgeRef identificador del proyecto en el SGE.
   * @return los datos del proyecto en el SGE (estructura del SGE).
   */
  public Object findProyectoSge(String proyectoSgeRef) {
    log.debug("findProyectoSge - proyectoSgeRef: {}", proyectoSgeRef);

    AssertHelper.fieldNotNull(proyectoSgeRef, ColumnaOutput.class, AssertHelper.MESSAGE_KEY_ID);

    ServiceType serviceType = ServiceType.SGE;
    String relativeUrl = "/proyectos/{id}";
    HttpMethod httpMethod = HttpMethod.GET;
    String mergedURL = buildUri(serviceType, relativeUrl);

    return super.<Object>callEndpoint(mergedURL, httpMethod, new ParameterizedTypeReference<Object>() {
    }, proyectoSgeRef).getBody();
  }

}
