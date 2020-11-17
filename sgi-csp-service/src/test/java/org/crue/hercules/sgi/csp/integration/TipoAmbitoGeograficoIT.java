package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
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
 * Test de integracion de TipoAmbitoGeografico.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TipoAmbitoGeograficoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/tipoambitogeograficos";

  private HttpEntity<TipoAmbitoGeografico> buildRequest(HttpHeaders headers, TipoAmbitoGeografico entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "CSP-THIT-B", "CSP-THIT-C", "CSP-THIT-E", "CSP-THIT-V")));

    HttpEntity<TipoAmbitoGeografico> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsTipoAmbitoGeografico() throws Exception {
    TipoAmbitoGeografico tipoAmbitoGeografico = generarMockTipoAmbitoGeografico(null);

    final ResponseEntity<TipoAmbitoGeografico> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, tipoAmbitoGeografico), TipoAmbitoGeografico.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    TipoAmbitoGeografico tipoAmbitoGeograficoCreado = response.getBody();
    Assertions.assertThat(tipoAmbitoGeograficoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(tipoAmbitoGeograficoCreado.getNombre()).as("getNombre()")
        .isEqualTo(tipoAmbitoGeografico.getNombre());
    Assertions.assertThat(tipoAmbitoGeograficoCreado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsTipoAmbitoGeografico() throws Exception {
    Long idTipoAmbitoGeografico = 1L;
    TipoAmbitoGeografico tipoAmbitoGeografico = generarMockTipoAmbitoGeografico(idTipoAmbitoGeografico,
        "nombre-actualizado");

    final ResponseEntity<TipoAmbitoGeografico> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, tipoAmbitoGeografico),
        TipoAmbitoGeografico.class, idTipoAmbitoGeografico);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    TipoAmbitoGeografico tipoAmbitoGeograficoActualizado = response.getBody();
    Assertions.assertThat(tipoAmbitoGeograficoActualizado.getId()).as("getId()")
        .isEqualTo(tipoAmbitoGeografico.getId());
    Assertions.assertThat(tipoAmbitoGeograficoActualizado.getNombre()).as("getNombre()")
        .isEqualTo(tipoAmbitoGeografico.getNombre());
    Assertions.assertThat(tipoAmbitoGeograficoActualizado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    Long idTipoAmbitoGeografico = 1L;

    final ResponseEntity<TipoAmbitoGeografico> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        TipoAmbitoGeografico.class, idTipoAmbitoGeografico);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsTipoAmbitoGeografico() throws Exception {
    Long idTipoAmbitoGeografico = 1L;

    final ResponseEntity<TipoAmbitoGeografico> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null), TipoAmbitoGeografico.class,
        idTipoAmbitoGeografico);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoAmbitoGeografico tipoAmbitoGeografico = response.getBody();
    Assertions.assertThat(tipoAmbitoGeografico.getId()).as("getId()").isEqualTo(idTipoAmbitoGeografico);
    Assertions.assertThat(tipoAmbitoGeografico.getNombre()).as("getNombre()").isEqualTo("nombre-001");
    Assertions.assertThat(tipoAmbitoGeografico.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoAmbitoGeograficoSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-TDOC-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre-";
    String filter = "nombre~%00%";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();

    final ResponseEntity<List<TipoAmbitoGeografico>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoAmbitoGeografico>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoAmbitoGeografico> tipoAmbitoGeograficos = response.getBody();
    Assertions.assertThat(tipoAmbitoGeograficos.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(tipoAmbitoGeograficos.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(tipoAmbitoGeograficos.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(tipoAmbitoGeograficos.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllTodos_WithPagingSortingAndFiltering_ReturnsTipoAmbitoGeograficoSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-TDOC-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre-";
    String filter = "nombre~%00%";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + "/todos").queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoAmbitoGeografico>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoAmbitoGeografico>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoAmbitoGeografico> tipoAmbitoGeograficos = response.getBody();
    Assertions.assertThat(tipoAmbitoGeograficos.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(tipoAmbitoGeograficos.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(tipoAmbitoGeograficos.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(tipoAmbitoGeograficos.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  /**
   * Función que devuelve un objeto TipoAmbitoGeografico
   * 
   * @param id id del TipoAmbitoGeografico
   * @return el objeto TipoAmbitoGeografico
   */
  private TipoAmbitoGeografico generarMockTipoAmbitoGeografico(Long id) {
    return generarMockTipoAmbitoGeografico(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoAmbitoGeografico
   * 
   * @param id     id del TipoAmbitoGeografico
   * @param nombre nombre del TipoAmbitoGeografico
   * @return el objeto TipoAmbitoGeografico
   */
  private TipoAmbitoGeografico generarMockTipoAmbitoGeografico(Long id, String nombre) {
    TipoAmbitoGeografico tipoAmbitoGeografico = new TipoAmbitoGeografico();
    tipoAmbitoGeografico.setId(id);
    tipoAmbitoGeografico.setNombre(nombre);
    tipoAmbitoGeografico.setActivo(true);

    return tipoAmbitoGeografico;
  }

}
