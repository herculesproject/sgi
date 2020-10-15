package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.BloqueFormulario;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer.TokenBuilder;
import org.crue.hercules.sgi.eti.model.ComiteFormulario;
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
 * Test de integracion de ComiteFormulario.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })

public class ComiteFormularioIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String COMITE_FORMULARIO_CONTROLLER_BASE_PATH = "/comiteformularios";

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private HttpEntity<ComiteFormulario> buildRequest(HttpHeaders headers, ComiteFormulario entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-COMITEFORMULARIO-EDITAR", "ETI-COMITEFORMULARIO-VER")));

    HttpEntity<ComiteFormulario> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getComiteFormulario_WithId_ReturnsComiteFormulario() throws Exception {

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);
    Formulario formulario = new Formulario(1L, "M10", "Descripcion", Boolean.TRUE);

    final ResponseEntity<ComiteFormulario> response = restTemplate.exchange(
        COMITE_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ComiteFormulario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final ComiteFormulario ComiteFormulario = response.getBody();

    Assertions.assertThat(ComiteFormulario.getId()).isEqualTo(1L);
    Assertions.assertThat(ComiteFormulario.getComite()).isEqualTo(comite);
    Assertions.assertThat(ComiteFormulario.getFormulario()).isEqualTo(formulario);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addComiteFormulario_ReturnsComiteFormulario() throws Exception {

    Comite comite = new Comite(1L, "Comite", Boolean.TRUE);
    Formulario formulario = new Formulario(1L, "M10", "Descripcion", Boolean.TRUE);

    ComiteFormulario nuevoComiteFormulario = new ComiteFormulario();
    nuevoComiteFormulario.setComite(comite);
    nuevoComiteFormulario.setFormulario(formulario);

    final ResponseEntity<ComiteFormulario> response = restTemplate.exchange(COMITE_FORMULARIO_CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, nuevoComiteFormulario), ComiteFormulario.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeComiteFormulario_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<ComiteFormulario> response = restTemplate.exchange(
        COMITE_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        ComiteFormulario.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeComiteFormulario_DoNotGetComiteFormulario() throws Exception {
    restTemplate.exchange(COMITE_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE,
        buildRequest(null, null), ComiteFormulario.class, 1L);

    final ResponseEntity<ComiteFormulario> response = restTemplate.exchange(
        COMITE_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ComiteFormulario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceComiteFormulario_ReturnsComiteFormulario() throws Exception {

    Comite comite = new Comite(1L, "Comite", Boolean.TRUE);
    Formulario formulario = new Formulario(1L, "M20", "Descripcion", Boolean.TRUE);

    ComiteFormulario replaceComiteFormulario = generarMockComiteFormulario(1L, comite, formulario);

    final ResponseEntity<ComiteFormulario> response = restTemplate.exchange(
        COMITE_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, replaceComiteFormulario), ComiteFormulario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final ComiteFormulario comiteFormulario = response.getBody();

    Assertions.assertThat(comiteFormulario.getId()).isNotNull();
    Assertions.assertThat(comiteFormulario.getComite()).isEqualTo(replaceComiteFormulario.getComite());
    Assertions.assertThat(comiteFormulario.getFormulario()).isEqualTo(replaceComiteFormulario.getFormulario());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsComiteFormularioSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(COMITE_FORMULARIO_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<ComiteFormulario>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ComiteFormulario>>() {
        });

    // then: Respuesta OK, ComiteFormularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ComiteFormulario> comiteFormularios = response.getBody();
    Assertions.assertThat(comiteFormularios.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de id='6' a '8'
    Assertions.assertThat(comiteFormularios.get(0).getId()).isEqualTo(6L);
    Assertions.assertThat(comiteFormularios.get(1).getId()).isEqualTo(7L);
    Assertions.assertThat(comiteFormularios.get(2).getId()).isEqualTo(8L);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredComiteFormularioList() throws Exception {
    // when: Búsqueda por id equals
    Long id = 5L;
    String query = "id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(COMITE_FORMULARIO_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<ComiteFormulario>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<ComiteFormulario>>() {
        });

    // then: Respuesta OK, ComiteFormularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ComiteFormulario> comiteFormularios = response.getBody();
    Assertions.assertThat(comiteFormularios.size()).isEqualTo(1);
    Assertions.assertThat(comiteFormularios.get(0).getId()).isEqualTo(id);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedComiteFormularioList() throws Exception {
    // when: Ordenación por id desc
    String query = "id-";

    URI uri = UriComponentsBuilder.fromUriString(COMITE_FORMULARIO_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<ComiteFormulario>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<ComiteFormulario>>() {
        });

    // then: Respuesta OK, TipoMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ComiteFormulario> comiteFormularios = response.getBody();
    Assertions.assertThat(comiteFormularios.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      ComiteFormulario comiteFormulario = comiteFormularios.get(i);
      Assertions.assertThat(comiteFormulario.getId()).isEqualTo(8 - i);
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsComiteFormularioSubList() throws Exception {
    // when: Obtiene page=1 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "1");
    Long id = 1L;

    // when: Ordena por id desc
    String sort = "id-";
    // when: Filtra por id equals
    String filter = "id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(COMITE_FORMULARIO_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<ComiteFormulario>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ComiteFormulario>>() {
        });

    // then: Respuesta OK, ComiteFormularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ComiteFormulario> comiteFormularios = response.getBody();
    Assertions.assertThat(comiteFormularios.size()).isEqualTo(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("1");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("1");

    // Contiene id='1'
    Assertions.assertThat(comiteFormularios.get(0).getId()).isEqualTo(1L);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findBloqueFormulariosUnlimited_ReturnsBloqueFormulario() throws Exception {

    // when: find unlimited bloques formulario para comité
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-EVC-EVAL", "ETI-EVC-EVALR", "ETI-EVC-EVALR-INV")));

    HttpEntity<ComiteFormulario> request = new HttpEntity<>(null, headers);

    final ResponseEntity<List<BloqueFormulario>> response = restTemplate.exchange(
        COMITE_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/bloque-formularios/{idTipoEvaluacion}",
        HttpMethod.GET, request, new ParameterizedTypeReference<List<BloqueFormulario>>() {
        }, 1L, 2L);

    // then: Obtiene los bloques formularios de un comité
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<BloqueFormulario> bloquesFormulario = response.getBody();
    Assertions.assertThat(bloquesFormulario.size()).isEqualTo(2);

    Assertions.assertThat(bloquesFormulario.get(0).getFormulario().getNombre()).isEqualTo("M10");
    Assertions.assertThat(bloquesFormulario.get(1).getFormulario().getNombre()).isEqualTo("M10");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findBloqueFormulariosUnlimited_ReturnsNotFound() throws Exception {

    // when: find unlimited bloques formulario para comité
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-EVC-EVAL", "ETI-EVC-EVALR", "ETI-EVC-EVALR-INV")));

    HttpEntity<ComiteFormulario> request = new HttpEntity<>(null, headers);

    final ResponseEntity<List<BloqueFormulario>> response = restTemplate.exchange(
        COMITE_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/bloque-formularios/{idTipoEvaluacion}",
        HttpMethod.GET, request, new ParameterizedTypeReference<List<BloqueFormulario>>() {
        }, 1L, 2L);

    // then: No existen bloques formulario para el comité
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  /**
   * Función que devuelve un objeto ComiteFormulario
   * 
   * @param id         id del ComiteFormulario
   * @param comite     el Comite de ComiteFormulario
   * @param formulario el Formulario de ComiteFormulario
   * @return el objeto ComiteFormulario
   */

  private ComiteFormulario generarMockComiteFormulario(Long id, Comite comite, Formulario formulario) {

    ComiteFormulario comiteFormulario = new ComiteFormulario();
    comiteFormulario.setId(id);
    comiteFormulario.setComite(comite);
    comiteFormulario.setFormulario(formulario);

    return comiteFormulario;
  }
}