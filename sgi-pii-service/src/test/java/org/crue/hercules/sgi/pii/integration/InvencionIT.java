package org.crue.hercules.sgi.pii.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.dto.InvencionInput;
import org.crue.hercules.sgi.pii.dto.InvencionOutput;
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
 * Test de integracion de Invencion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class InvencionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/invenciones";
  private static final String PATH_TODOS = "/todos";
  private static final String PATH_ACTIVAR = "/activar";
  private static final String PATH_DESACTIVAR = "/desactivar";

  private HttpEntity<InvencionInput> buildRequest(HttpHeaders headers,
      InvencionInput entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<InvencionInput> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findActivos_WithPagingSortingAndFiltering_ReturnInvencionSubList() throws Exception {

    String[] roles = { "PII-INV-V", "PII-INV-C", "PII-INV-E", "PII-INV-B", "PII-INV-R", "PII-INV-MOD-V" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "descripcion=ke=-00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<InvencionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<InvencionOutput>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InvencionOutput> tramoRepartoOutput = response.getBody();
    Assertions.assertThat(tramoRepartoOutput.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");

    Assertions.assertThat(tramoRepartoOutput.get(0).getId()).as("id").isEqualTo(3);
    Assertions.assertThat(tramoRepartoOutput.get(1).getId()).as("id").isEqualTo(2);
    Assertions.assertThat(tramoRepartoOutput.get(2).getId()).as("id").isEqualTo(1);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnInvencionSubList() throws Exception {

    String[] roles = { "PII-INV-V", "PII-INV-C", "PII-INV-E", "PII-INV-B", "PII-INV-R" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "descripcion=ke=-00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TODOS).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<InvencionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<InvencionOutput>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InvencionOutput> tramoRepartoOutput = response.getBody();
    Assertions.assertThat(tramoRepartoOutput.size()).isEqualTo(4);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("4");

    Assertions.assertThat(tramoRepartoOutput.get(0).getId()).as("id").isEqualTo(4);
    Assertions.assertThat(tramoRepartoOutput.get(1).getId()).as("id").isEqualTo(3);
    Assertions.assertThat(tramoRepartoOutput.get(2).getId()).as("id").isEqualTo(2);
    Assertions.assertThat(tramoRepartoOutput.get(3).getId()).as("id").isEqualTo(1);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnInvencionOutput() throws Exception {

    String[] roles = { "PII-INV-V", "PII-INV-C", "PII-INV-E", "PII-INV-B", "PII-INV-R", "PII-INV-MOD-V" };
    Long invencionOutputId = 1L;

    final ResponseEntity<InvencionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH
        + PATH_PARAMETER_ID, HttpMethod.GET,
        buildRequest(null, null, roles), InvencionOutput.class, invencionOutputId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final InvencionOutput invencionOutput = response.getBody();

    Assertions.assertThat(invencionOutput.getId()).as("id").isEqualTo(1);
    Assertions.assertThat(invencionOutput.getTitulo()).as("titulo").isEqualTo("titulo-invencion-001");
    Assertions.assertThat(invencionOutput.getDescripcion()).as("descripcion").isEqualTo("descripcion-invencion-001");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnInvencionOutput() throws Exception {

    String[] roles = { "PII-INV-C" };
    InvencionInput invencionInput = generaMockInvencionInput();

    final ResponseEntity<InvencionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, invencionInput, roles), InvencionOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    final InvencionOutput invencionOutput = response.getBody();

    Assertions.assertThat(invencionOutput.getId()).as("id").isEqualTo(5);
    Assertions.assertThat(invencionOutput.getTitulo()).as("titulo").isEqualTo("titulo-invencion");
    Assertions.assertThat(invencionOutput.getDescripcion()).as("descripcion").isEqualTo("descripcion-invencion");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnInvencionOutput() throws Exception {

    String[] roles = { "PII-INV-E" };
    Long invencionId = 1L;
    InvencionInput invencionInput = generaMockInvencionInput();
    invencionInput.setComentarios("comentarios-invencion-modificado");

    final ResponseEntity<InvencionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT,
        buildRequest(null, invencionInput, roles), InvencionOutput.class, invencionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final InvencionOutput invencionOutput = response.getBody();

    Assertions.assertThat(invencionOutput.getId()).as("id").isEqualTo(1);
    Assertions.assertThat(invencionOutput.getTitulo()).as("titulo").isEqualTo("titulo-invencion");
    Assertions.assertThat(invencionOutput.getComentarios()).as("comentarios")
        .isEqualTo("comentarios-invencion-modificado");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void activar_ReturnInvencionOutput() throws Exception {

    String[] roles = { "PII-INV-R" };
    Long invencionId = 4L;

    final ResponseEntity<InvencionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ACTIVAR,
        HttpMethod.PATCH,
        buildRequest(null, null, roles), InvencionOutput.class, invencionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final InvencionOutput invencionOutput = response.getBody();

    Assertions.assertThat(invencionOutput.getId()).as("id").isEqualTo(4);
    Assertions.assertThat(invencionOutput.getTitulo()).as("titulo").isEqualTo("titulo-invencion-004");
    Assertions.assertThat(invencionOutput.getComentarios()).as("comentarios")
        .isEqualTo("comentarios-invencion-004");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void desactivar_ReturnInvencionOutput() throws Exception {

    String[] roles = { "PII-INV-B" };
    Long invencionId = 1L;

    final ResponseEntity<InvencionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_DESACTIVAR,
        HttpMethod.PATCH,
        buildRequest(null, null, roles), InvencionOutput.class, invencionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final InvencionOutput invencionOutput = response.getBody();

    Assertions.assertThat(invencionOutput.getId()).as("id").isEqualTo(1);
    Assertions.assertThat(invencionOutput.getTitulo()).as("titulo").isEqualTo("titulo-invencion-001");
    Assertions.assertThat(invencionOutput.getComentarios()).as("comentarios")
        .isEqualTo("comentarios-invencion-001");

  }

  /**
   * Función que devuelve un objeto InvencionInput
   * 
   * @return el objeto InvencionInput
   */
  private InvencionInput generaMockInvencionInput() {

    InvencionInput invencionInput = new InvencionInput();
    invencionInput.setTitulo("titulo-invencion");
    invencionInput.setDescripcion("descripcion-invencion");
    invencionInput.setComentarios("comentarios-invencion");
    invencionInput.setFechaComunicacion(Instant.parse("2020-10-19T00:00:00Z"));
    invencionInput.setTipoProteccionId(1L);

    return invencionInput;
  }

}
