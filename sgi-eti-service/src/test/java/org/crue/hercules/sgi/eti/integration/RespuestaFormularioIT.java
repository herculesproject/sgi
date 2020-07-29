package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.ComponenteFormulario;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.FormularioMemoria;
import org.crue.hercules.sgi.eti.model.RespuestaFormulario;
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
 * Test de integracion de RespuestaFormulario.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class RespuestaFormularioIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String RESPUESTA_FORMULARIO_CONTROLLER_BASE_PATH = "/respuestaformularios";

  private HttpEntity<RespuestaFormulario> buildRequest(HttpHeaders headers, RespuestaFormulario entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-RESPUESTAFORMULARIO-EDITAR", "ETI-RESPUESTAFORMULARIO-VER")));

    HttpEntity<RespuestaFormulario> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getRespuestaFormulario_WithId_ReturnsRespuestaFormulario() throws Exception {
    final ResponseEntity<RespuestaFormulario> response = restTemplate.exchange(
        RESPUESTA_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        RespuestaFormulario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final RespuestaFormulario respuestaFormulario = response.getBody();

    Assertions.assertThat(respuestaFormulario.getId()).isEqualTo(1L);
    Assertions.assertThat(respuestaFormulario.getValor()).isEqualTo("Valor1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addRespuestaFormulario_ReturnsRespuestaFormulario() throws Exception {

    RespuestaFormulario nuevoRespuestaFormulario = new RespuestaFormulario();
    nuevoRespuestaFormulario.setValor("Valor1");

    restTemplate.exchange(RESPUESTA_FORMULARIO_CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, nuevoRespuestaFormulario), RespuestaFormulario.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeRespuestaFormulario_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<RespuestaFormulario> response = restTemplate.exchange(
        RESPUESTA_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        RespuestaFormulario.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeRespuestaFormulario_DoNotGetRespuestaFormulario() throws Exception {

    final ResponseEntity<RespuestaFormulario> response = restTemplate.exchange(
        RESPUESTA_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        RespuestaFormulario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceRespuestaFormulario_ReturnsRespuestaFormulario() throws Exception {

    RespuestaFormulario replaceRespuestaFormulario = generarMockRespuestaFormulario(1L);

    final ResponseEntity<RespuestaFormulario> response = restTemplate.exchange(
        RESPUESTA_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, replaceRespuestaFormulario), RespuestaFormulario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final RespuestaFormulario respuestaFormulario = response.getBody();

    Assertions.assertThat(respuestaFormulario.getId()).isNotNull();
    Assertions.assertThat(respuestaFormulario.getValor()).isEqualTo(replaceRespuestaFormulario.getValor());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsRespuestaFormularioSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    final ResponseEntity<List<RespuestaFormulario>> response = restTemplate.exchange(
        RESPUESTA_FORMULARIO_CONTROLLER_BASE_PATH, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<RespuestaFormulario>>() {
        });

    // then: Respuesta OK, RespuestaFormularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<RespuestaFormulario> respuestaFormularios = response.getBody();
    Assertions.assertThat(respuestaFormularios.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de valor='Valor6' a 'Valor8'
    Assertions.assertThat(respuestaFormularios.get(0).getValor()).isEqualTo("Valor6");
    Assertions.assertThat(respuestaFormularios.get(1).getValor()).isEqualTo("Valor7");
    Assertions.assertThat(respuestaFormularios.get(2).getValor()).isEqualTo("Valor8");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredRespuestaFormularioList() throws Exception {
    // when: Búsqueda por valor like e id equals
    Long id = 5L;
    String query = "valor~Valor%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(RESPUESTA_FORMULARIO_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<RespuestaFormulario>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<RespuestaFormulario>>() {
        });

    // then: Respuesta OK, RespuestaFormularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<RespuestaFormulario> respuestaFormularios = response.getBody();
    Assertions.assertThat(respuestaFormularios.size()).isEqualTo(1);
    Assertions.assertThat(respuestaFormularios.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(respuestaFormularios.get(0).getValor()).startsWith("Valor");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedRespuestaFormularioList() throws Exception {
    // when: Ordenación por valor desc
    String query = "valor-";

    URI uri = UriComponentsBuilder.fromUriString(RESPUESTA_FORMULARIO_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<RespuestaFormulario>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<RespuestaFormulario>>() {
        });

    // then: Respuesta OK, RespuestaFormularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<RespuestaFormulario> respuestaFormularios = response.getBody();
    Assertions.assertThat(respuestaFormularios.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      RespuestaFormulario respuestaFormulario = respuestaFormularios.get(i);
      Assertions.assertThat(respuestaFormulario.getId()).isEqualTo(8 - i);
      Assertions.assertThat(respuestaFormulario.getValor()).isEqualTo("Valor" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsRespuestaFormularioSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por valor desc
    String sort = "valor-";
    // when: Filtra por valor like e id equals
    String filter = "valor~%00%";

    URI uri = UriComponentsBuilder.fromUriString(RESPUESTA_FORMULARIO_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<RespuestaFormulario>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<RespuestaFormulario>>() {
        });

    // then: Respuesta OK, RespuestaFormularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<RespuestaFormulario> respuestaFormularios = response.getBody();
    Assertions.assertThat(respuestaFormularios.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene valor='Valor001', 'Valor002',
    // 'Valor003'
    Assertions.assertThat(respuestaFormularios.get(0).getValor()).isEqualTo("Valor" + String.format("%03d", 3));
    Assertions.assertThat(respuestaFormularios.get(1).getValor()).isEqualTo("Valor" + String.format("%03d", 2));
    Assertions.assertThat(respuestaFormularios.get(2).getValor()).isEqualTo("Valor" + String.format("%03d", 1));

  }

  /**
   * Función que devuelve un objeto RespuestaFormulario
   * 
   * @param id id del RespuestaFormulario
   * @return el objeto RespuestaFormulario
   */

  public RespuestaFormulario generarMockRespuestaFormulario(Long id) {
    Memoria memoria = new Memoria();
    memoria.setId(id);

    Formulario formulario = new Formulario();
    formulario.setId(id);

    FormularioMemoria formularioMemoria = new FormularioMemoria();
    formularioMemoria.setId(id);
    formularioMemoria.setMemoria(memoria);
    formularioMemoria.setFormulario(formulario);
    formularioMemoria.setActivo(true);

    ComponenteFormulario componenteFormulario = new ComponenteFormulario();
    componenteFormulario.setId(id);
    componenteFormulario.setEsquema("Esquema" + id);

    RespuestaFormulario respuestaFormulario = new RespuestaFormulario();
    respuestaFormulario.setId(id);
    respuestaFormulario.setFormularioMemoria(formularioMemoria);
    respuestaFormulario.setComponenteFormulario(componenteFormulario);
    respuestaFormulario.setValor("Valor" + id);

    return respuestaFormulario;
  }

}