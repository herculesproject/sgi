package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de RolSocio.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RolSocioIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String PATH_PARAMETER_TODOS = "/todos";
  private static final String CONTROLLER_BASE_PATH = "/rolsocios";

  private HttpEntity<RolSocio> buildRequest(HttpHeaders headers, RolSocio entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "SYSADMIN", "CSP-RSOC-B",
        "CSP-RSOC-C", "CSP-RSOC-E", "CSP-RSOC-V", "CSP-RSOC-X", "CSP-RSOC-V_OPE")));

    HttpEntity<RolSocio> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsRolSocio() throws Exception {

    // given: new RolSocio
    RolSocio rolSocio = generarMockRolSocio(1L);
    rolSocio.setId(null);

    // when: create RolSocio
    final ResponseEntity<RolSocio> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, rolSocio), RolSocio.class);

    // then: new RolSocio is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    RolSocio responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()").isEqualTo(rolSocio.getAbreviatura());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(rolSocio.getNombre());
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()").isEqualTo(rolSocio.getDescripcion());
    Assertions.assertThat(responseData.getCoordinador()).as("getCoordinador()").isEqualTo(rolSocio.getCoordinador());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsRolSocio() throws Exception {

    // given: existing RolSocio to be updated
    RolSocio rolSocio = generarMockRolSocio(1L);
    rolSocio.setDescripcion("descripcion-modificada");

    // when: update RolSocio
    final ResponseEntity<RolSocio> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, rolSocio), RolSocio.class, rolSocio.getId());

    // then: RolSocio is updated
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    RolSocio responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(rolSocio.getId());
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()").isEqualTo(rolSocio.getAbreviatura());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(rolSocio.getNombre());
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()").isEqualTo(rolSocio.getDescripcion());
    Assertions.assertThat(responseData.getCoordinador()).as("getCoordinador()").isEqualTo(rolSocio.getCoordinador());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void enable_ReturnsRolSocio() throws Exception {
    // given: existing RolSocio to be enabled
    Long id = 1L;

    // when: disable RolSocio
    final ResponseEntity<RolSocio> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, HttpMethod.PATCH, buildRequest(null, null),
        RolSocio.class, id);

    // then: RolSocio is disabled
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    RolSocio rolSocio = response.getBody();
    Assertions.assertThat(rolSocio.getId()).as("getId()").isNotNull();
    Assertions.assertThat(rolSocio.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(rolSocio.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void disable_ReturnsRolSocio() throws Exception {
    // given: existing RolSocio to be disabled
    Long id = 1L;

    // when: disable RolSocio
    final ResponseEntity<RolSocio> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null), RolSocio.class, id);

    // then: RolSocio is disabled
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    RolSocio rolSocio = response.getBody();
    Assertions.assertThat(rolSocio.getId()).as("getId()").isNotNull();
    Assertions.assertThat(rolSocio.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(rolSocio.getActivo()).as("getActivo()").isEqualTo(Boolean.FALSE);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void existsById_Returns200() throws Exception {
    // given: existing id
    Long id = 1L;
    // when: exists by id
    final ResponseEntity<RolSocio> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.HEAD, buildRequest(null, null), RolSocio.class, id);
    // then: 200 OK
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void existsById_Returns204() throws Exception {
    // given: no existing id
    Long id = 1L;
    // when: exists by id
    final ResponseEntity<RolSocio> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.HEAD, buildRequest(null, null), RolSocio.class, id);
    // then: 204 No Content
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsRolSocio() throws Exception {
    Long id = 1L;

    final ResponseEntity<RolSocio> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), RolSocio.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    RolSocio responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()").isEqualTo("001");
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo("nombre-001");
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-001");
    Assertions.assertThat(responseData.getCoordinador()).as("getCoordinador()").isEqualTo(Boolean.FALSE);
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsRolSocioSubList() throws Exception {

    // given: data for RolSocio

    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-RSOC-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";
    String filter = "descripcion=ke=00";

    // when: find RolSocio
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();
    final ResponseEntity<List<RolSocio>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<RolSocio>>() {
        });

    // given: RolSocio data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<RolSocio> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getDescripcion()).as("get(0).getDescripcion())")
        .isEqualTo("descripcion-" + String.format("%03d", 3));
    Assertions.assertThat(responseData.get(1).getDescripcion()).as("get(1).getDescripcion())")
        .isEqualTo("descripcion-" + String.format("%03d", 2));
    Assertions.assertThat(responseData.get(2).getDescripcion()).as("get(2).getDescripcion())")
        .isEqualTo("descripcion-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllTodos_WithPagingSortingAndFiltering_ReturnsRolSocioSubList() throws Exception {

    // given: data for RolSocio

    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-RSOC-V_OPE")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";
    String filter = "descripcion=ke=00";

    // when: find RolSocio
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_TODOS).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();
    final ResponseEntity<List<RolSocio>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<RolSocio>>() {
        });

    // given: RolSocio data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<RolSocio> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getDescripcion()).as("get(0).getDescripcion())")
        .isEqualTo("descripcion-" + String.format("%03d", 8));
    Assertions.assertThat(responseData.get(1).getDescripcion()).as("get(1).getDescripcion())")
        .isEqualTo("descripcion-" + String.format("%03d", 2));
    Assertions.assertThat(responseData.get(2).getDescripcion()).as("get(2).getDescripcion())")
        .isEqualTo("descripcion-" + String.format("%03d", 1));
  }

  /**
   * Función que genera RolSocio
   * 
   * @param rolSocioId
   * @return el rolSocio
   */
  private RolSocio generarMockRolSocio(Long rolSocioId) {

    String suffix = String.format("%03d", rolSocioId);

    RolSocio rolSocio = RolSocio.builder()//
        .id(rolSocioId)//
        .abreviatura(suffix)//
        .nombre("nombre-" + suffix)//
        .descripcion("descripcion-" + suffix)//
        .coordinador(Boolean.FALSE)//
        .activo(Boolean.TRUE)//
        .build();

    return rolSocio;
  }

}
