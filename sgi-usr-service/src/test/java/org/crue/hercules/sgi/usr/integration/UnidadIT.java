package org.crue.hercules.sgi.usr.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.usr.model.Unidad;
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
 * Test de integracion de Unidad.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UnidadIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/unidades";

  private HttpEntity<Unidad> buildRequest(HttpHeaders headers, Unidad entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "USR-UNI-B", "USR-UNI-C", "USR-UNI-E", "USR-UNI-V_OPE")));

    HttpEntity<Unidad> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsUnidad() throws Exception {
    Unidad unidad = generarMockUnidad(null);

    final ResponseEntity<Unidad> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, unidad), Unidad.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    Unidad unidadCreado = response.getBody();
    Assertions.assertThat(unidadCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(unidadCreado.getNombre()).as("getNombre()").isEqualTo(unidad.getNombre());
    Assertions.assertThat(unidadCreado.getAcronimo()).as("getAcronimo()").isEqualTo(unidad.getAcronimo());
    Assertions.assertThat(unidadCreado.getDescripcion()).as("getDescripcion()").isEqualTo(unidad.getDescripcion());
    Assertions.assertThat(unidadCreado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsUnidad() throws Exception {
    Long idUnidad = 1L;
    Unidad unidad = generarMockUnidad(idUnidad, "nombre-actualizado");

    final ResponseEntity<Unidad> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, unidad), Unidad.class, idUnidad);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Unidad unidadActualizado = response.getBody();
    Assertions.assertThat(unidadActualizado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(unidadActualizado.getNombre()).as("getNombre()").isEqualTo(unidad.getNombre());
    Assertions.assertThat(unidadActualizado.getDescripcion()).as("getDescripcion()").isEqualTo(unidad.getDescripcion());
    Assertions.assertThat(unidadActualizado.getActivo()).as("getActivo()").isEqualTo(unidad.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    Long idUnidad = 1L;

    final ResponseEntity<Unidad> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), Unidad.class, idUnidad);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsUnidad() throws Exception {
    Long idUnidad = 1L;

    final ResponseEntity<Unidad> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), Unidad.class, idUnidad);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Unidad unidad = response.getBody();
    Assertions.assertThat(unidad.getId()).as("getId()").isNotNull();
    Assertions.assertThat(unidad.getNombre()).as("getNombre()").isEqualTo("nombre-001");
    Assertions.assertThat(unidad.getAcronimo()).as("getAcronimo()").isEqualTo("acronimo-1");
    Assertions.assertThat(unidad.getDescripcion()).as("descripcion-001").isEqualTo(unidad.getDescripcion());
    Assertions.assertThat(unidad.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findByAcronimo_ReturnsUnidad() throws Exception {
    String acronimo = "OPE";

    final ResponseEntity<Unidad> response = restTemplate.exchange(CONTROLLER_BASE_PATH + "/acronimo/{acronimo}",
        HttpMethod.GET, buildRequest(null, null), Unidad.class, acronimo);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Unidad unidad = response.getBody();
    Assertions.assertThat(unidad.getId()).as("getId()").isNotNull();
    Assertions.assertThat(unidad.getNombre()).as("getNombre()").isEqualTo("nombre-001");
    Assertions.assertThat(unidad.getAcronimo()).as("getAcronimo()").isEqualTo("OPE");
    Assertions.assertThat(unidad.getDescripcion()).as("descripcion-001").isEqualTo(unidad.getDescripcion());
    Assertions.assertThat(unidad.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsUnidadSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-TDOC-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre-";
    String filter = "descripcion~%00%";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();

    final ResponseEntity<List<Unidad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Unidad>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Unidad> unidades = response.getBody();
    Assertions.assertThat(unidades.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(unidades.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(unidades.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(unidades.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllTodos_WithPagingSortingAndFiltering_ReturnsUnidadSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-TDOC-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre-";
    String filter = "descripcion~%00%";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + "/todos").queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Unidad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Unidad>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Unidad> unidades = response.getBody();
    Assertions.assertThat(unidades.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(unidades.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(unidades.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(unidades.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllTodosRestringidos_WithPagingSortingAndFiltering_ReturnsUnidadSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-TDOC-V_OPE")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre-";
    String filter = "descripcion~%00%";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + "/restringidos").queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Unidad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Unidad>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Unidad> unidades = response.getBody();
    Assertions.assertThat(unidades.size()).isEqualTo(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");

    Assertions.assertThat(unidades.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  /**
   * Función que devuelve un objeto Unidad
   * 
   * @param id id del Unidad
   * @return el objeto Unidad
   */
  private Unidad generarMockUnidad(Long id) {
    return generarMockUnidad(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto Unidad
   * 
   * @param id     id del Unidad
   * @param nombre nombre del Unidad
   * @return el objeto Unidad
   */
  private Unidad generarMockUnidad(Long id, String nombre) {
    Unidad unidad = new Unidad();
    unidad.setId(id);
    unidad.setNombre(nombre);
    unidad.setAcronimo("acronimo-" + id);
    unidad.setDescripcion("descripcion-" + id);
    unidad.setActivo(true);

    return unidad;
  }

}
