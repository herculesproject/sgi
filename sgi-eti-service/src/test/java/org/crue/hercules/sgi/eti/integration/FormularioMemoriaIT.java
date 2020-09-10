package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.FormularioMemoria;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer.TokenBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

/**
 * Test de integracion de FormularioMemoria.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class FormularioMemoriaIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH = "/formulariomemorias";

  private HttpEntity<FormularioMemoria> buildRequest(HttpHeaders headers, FormularioMemoria entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-FORMULARIOMEMORIA-EDITAR", "ETI-FORMULARIOMEMORIA-VER")));

    HttpEntity<FormularioMemoria> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getFormularioMemoria_WithId_ReturnsFormularioMemoria() throws Exception {
    final ResponseEntity<FormularioMemoria> response = restTemplate.exchange(
        FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        FormularioMemoria.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final FormularioMemoria formularioMemoria = response.getBody();

    Assertions.assertThat(formularioMemoria.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(formularioMemoria.getMemoria()).as("memoria").isNotNull();
    Assertions.assertThat(formularioMemoria.getMemoria().getId()).as("memoria.id").isEqualTo(100L);
    Assertions.assertThat(formularioMemoria.getFormulario()).as("formulario").isNotNull();
    Assertions.assertThat(formularioMemoria.getFormulario().getId()).as("formulario.id").isEqualTo(200L);
    Assertions.assertThat(formularioMemoria.getActivo()).as("activo").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addFormularioMemoria_ReturnsFormularioMemoria() throws Exception {

    FormularioMemoria nuevoFormularioMemoria = generarMockFormularioMemoria(null);

    final ResponseEntity<FormularioMemoria> response = restTemplate.exchange(FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, nuevoFormularioMemoria), FormularioMemoria.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    final FormularioMemoria formularioMemoria = response.getBody();

    Assertions.assertThat(formularioMemoria.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(formularioMemoria.getMemoria()).as("memoria").isNotNull();
    Assertions.assertThat(formularioMemoria.getMemoria().getId()).as("memoria.id").isEqualTo(100L);
    Assertions.assertThat(formularioMemoria.getFormulario()).as("formulario").isNotNull();
    Assertions.assertThat(formularioMemoria.getFormulario().getId()).as("formulario.id").isEqualTo(200L);
    Assertions.assertThat(formularioMemoria.getActivo()).as("activo").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeFormularioMemoria_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<FormularioMemoria> response = restTemplate.exchange(
        FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        FormularioMemoria.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeFormularioMemoria_DoNotGetFormularioMemoria() throws Exception {

    final ResponseEntity<FormularioMemoria> response = restTemplate.exchange(
        FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        FormularioMemoria.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceFormularioMemoria_ReturnsFormularioMemoria() throws Exception {

    FormularioMemoria replaceFormularioMemoria = generarMockFormularioMemoria(1L);

    final ResponseEntity<FormularioMemoria> response = restTemplate.exchange(
        FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, replaceFormularioMemoria), FormularioMemoria.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final FormularioMemoria formularioMemoria = response.getBody();

    Assertions.assertThat(formularioMemoria.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(formularioMemoria.getMemoria()).as("memoria").isNotNull();
    Assertions.assertThat(formularioMemoria.getMemoria().getId()).as("memoria.id").isEqualTo(100L);
    Assertions.assertThat(formularioMemoria.getFormulario()).as("formulario").isNotNull();
    Assertions.assertThat(formularioMemoria.getFormulario().getId()).as("formulario.id").isEqualTo(200L);
    Assertions.assertThat(formularioMemoria.getActivo()).as("activo").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsFormularioMemoriaSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    final ResponseEntity<List<FormularioMemoria>> response = restTemplate.exchange(
        FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<FormularioMemoria>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<FormularioMemoria> formulariosMemorias = response.getBody();
    Assertions.assertThat(formulariosMemorias.size()).as("size").isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).as("x-page").isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).as("x-page-size").isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).as("x-total-count").isEqualTo("8");

    // Contiene de id=6 a 8
    Assertions.assertThat(formulariosMemorias.get(0).getId()).as("0.id").isEqualTo(6);
    Assertions.assertThat(formulariosMemorias.get(1).getId()).as("1.id").isEqualTo(7);
    Assertions.assertThat(formulariosMemorias.get(2).getId()).as("2.id").isEqualTo(8);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredFormularioMemoriaList() throws Exception {
    // when: Búsqueda por formulario memoria id equals
    Long id = 5L;
    String query = "id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<FormularioMemoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<FormularioMemoria>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<FormularioMemoria> formulariosMemorias = response.getBody();
    Assertions.assertThat(formulariosMemorias.size()).as("size").isEqualTo(1);
    Assertions.assertThat(formulariosMemorias.get(0).getId()).as("id").isEqualTo(id);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedFormularioMemoriaList() throws Exception {
    // when: Ordenación por id desc
    String sort = "id-";

    URI uri = UriComponentsBuilder.fromUriString(FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<FormularioMemoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<FormularioMemoria>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<FormularioMemoria> formulariosMemorias = response.getBody();
    Assertions.assertThat(formulariosMemorias.size()).as("size").isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      FormularioMemoria formularioMemoria = formulariosMemorias.get(i);
      Assertions.assertThat(formularioMemoria.getId()).as((8 - i) + ".id").isEqualTo(8 - i);
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsFormularioMemoriaSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=3
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por id desc
    String sort = "id-";
    // when: Filtra por id menor
    String filter = "id<4";

    URI uri = UriComponentsBuilder.fromUriString(FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<FormularioMemoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<FormularioMemoria>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<FormularioMemoria> formulariosMemorias = response.getBody();
    Assertions.assertThat(formulariosMemorias.size()).as("size").isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("x-page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("x-page-size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("x-total-count").isEqualTo("3");

    // Contiene id=3, 2, 1
    Assertions.assertThat(formulariosMemorias.get(0).getId()).as("0.id").isEqualTo(3);
    Assertions.assertThat(formulariosMemorias.get(1).getId()).as("1.id").isEqualTo(2);
    Assertions.assertThat(formulariosMemorias.get(2).getId()).as("2.id").isEqualTo(1);
  }

  /**
   * Función que devuelve un objeto FormularioMemoria
   * 
   * @param id id del formulario memoria
   * @return el objeto FormularioMemoria
   */
  public FormularioMemoria generarMockFormularioMemoria(Long id) {
    Memoria memoria = new Memoria();
    memoria.setId(100L);

    Formulario formulario = new Formulario();
    formulario.setId(200L);

    FormularioMemoria formularioMemoria = new FormularioMemoria();
    formularioMemoria.setId(id);
    formularioMemoria.setMemoria(memoria);
    formularioMemoria.setFormulario(formulario);
    formularioMemoria.setActivo(true);

    return formularioMemoria;
  }

}