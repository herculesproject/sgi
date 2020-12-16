package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer.TokenBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de Proyecto.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class ProyectoIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/proyectos";
  private static final String PATH_TODOS = "/todos";

  private HttpEntity<Proyecto> buildRequest(HttpHeaders headers, Proyecto entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-PRO-C", "CSP-PRO-E", "CSP-PRO-V_OPE")));

    HttpEntity<Proyecto> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void desactivar_ReturnProyecto() throws Exception {
    Long idProyecto = 1L;

    final ResponseEntity<Proyecto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null), Proyecto.class, idProyecto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Proyecto proyecto = response.getBody();
    Assertions.assertThat(proyecto.getId()).as("getId()").isEqualTo(idProyecto);
    Assertions.assertThat(proyecto.getActivo()).as("getActivo()").isEqualTo(false);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void reactivar_ReturnProyecto() throws Exception {
    Long idProyecto = 1L;

    final ResponseEntity<Proyecto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, HttpMethod.PATCH, buildRequest(null, null),
        Proyecto.class, idProyecto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Proyecto proyecto = response.getBody();
    Assertions.assertThat(proyecto.getId()).as("getId()").isEqualTo(idProyecto);
    Assertions.assertThat(proyecto.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsProyecto() throws Exception {
    Long idProyecto = 1L;

    final ResponseEntity<Proyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), Proyecto.class, idProyecto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Proyecto proyecto = response.getBody();
    Assertions.assertThat(proyecto.getId()).as("getId()").isEqualTo(idProyecto);
    Assertions.assertThat(proyecto.getEstado().getId()).as("getEstado().getId()").isEqualTo(1);
    Assertions.assertThat(proyecto.getObservaciones()).as("getObservaciones()").isEqualTo("observaciones 1");
    Assertions.assertThat(proyecto.getUnidadGestionRef()).as("getUnidadGestionRef()").isEqualTo("OPE");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsProyectoSubList() throws Exception {

    // given: data for Proyecto

    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-PRO-V_OPE")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "observaciones-";
    String filter = "unidadGestionRef:OPE";

    // when: find Convocatoria
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();
    final ResponseEntity<List<Proyecto>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Proyecto>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Proyecto> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getObservaciones()).as("get(0).getObservaciones())")
        .isEqualTo("observaciones" + String.format("%03d", 3));
    Assertions.assertThat(responseData.get(1).getObservaciones()).as("get(1).getObservaciones())")
        .isEqualTo("observaciones" + String.format("%03d", 2));
    Assertions.assertThat(responseData.get(2).getObservaciones()).as("get(2).getObservaciones())")
        .isEqualTo("observaciones" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllTodos_WithPagingSortingAndFiltering_ReturnsProyectoSubList() throws Exception {

    // given: data for Proyecto

    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-PRO-V_OPE")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "observaciones-";
    String filter = "unidadGestionRef:OPE";

    // when: find Convocatoria
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TODOS).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();
    final ResponseEntity<List<Proyecto>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Proyecto>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Proyecto> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getObservaciones()).as("get(0).getObservaciones())")
        .isEqualTo("observaciones" + String.format("%03d", 3));
    Assertions.assertThat(responseData.get(1).getObservaciones()).as("get(1).getObservaciones())")
        .isEqualTo("observaciones" + String.format("%03d", 2));
    Assertions.assertThat(responseData.get(2).getObservaciones()).as("get(2).getObservaciones())")
        .isEqualTo("observaciones" + String.format("%03d", 1));
  }
}
