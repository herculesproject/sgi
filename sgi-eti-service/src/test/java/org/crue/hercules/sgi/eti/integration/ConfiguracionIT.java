package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Configuracion;
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
 * Test de integracion de Configuracion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class ConfiguracionIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONFIGURACION_CONTROLLER_BASE_PATH = "/configuraciones";

  private HttpEntity<Configuracion> buildRequest(HttpHeaders headers, Configuracion entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-CONFIGURACION-EDITAR", "ETI-CONFIGURACION-VER")));

    HttpEntity<Configuracion> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getConfiguracion_WithId_ReturnsConfiguracion() throws Exception {
    final ResponseEntity<Configuracion> response = restTemplate.exchange(
        CONFIGURACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        Configuracion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Configuracion configuracion = response.getBody();

    Assertions.assertThat(configuracion.getId()).isEqualTo(1L);
    Assertions.assertThat(configuracion.getClave()).isEqualTo("Configuracion1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addConfiguracion_ReturnsConfiguracion() throws Exception {

    Configuracion nuevoConfiguracion = new Configuracion();
    nuevoConfiguracion.setClave("Configuracion1");

    restTemplate.exchange(CONFIGURACION_CONTROLLER_BASE_PATH, HttpMethod.POST, buildRequest(null, nuevoConfiguracion),
        Configuracion.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeConfiguracion_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<Configuracion> response = restTemplate.exchange(
        CONFIGURACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        Configuracion.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeConfiguracion_DoNotGetConfiguracion() throws Exception {

    final ResponseEntity<Configuracion> response = restTemplate.exchange(
        CONFIGURACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        Configuracion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceConfiguracion_ReturnsConfiguracion() throws Exception {

    Configuracion replaceConfiguracion = generarMockConfiguracion(1L, "Configuracion1");

    final ResponseEntity<Configuracion> response = restTemplate.exchange(
        CONFIGURACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, replaceConfiguracion), Configuracion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Configuracion configuracion = response.getBody();

    Assertions.assertThat(configuracion.getId()).isNotNull();
    Assertions.assertThat(configuracion.getClave()).isEqualTo("Configuracion1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsConfiguracionSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    final ResponseEntity<List<Configuracion>> response = restTemplate.exchange(CONFIGURACION_CONTROLLER_BASE_PATH,
        HttpMethod.GET, buildRequest(headers, null), new ParameterizedTypeReference<List<Configuracion>>() {
        });

    // then: Respuesta OK, Configuraciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Configuracion> configuraciones = response.getBody();
    Assertions.assertThat(configuraciones.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de clave='Configuracion6' a 'Configuracion8'
    Assertions.assertThat(configuraciones.get(0).getClave()).isEqualTo("Configuracion6");
    Assertions.assertThat(configuraciones.get(1).getClave()).isEqualTo("Configuracion7");
    Assertions.assertThat(configuraciones.get(2).getClave()).isEqualTo("Configuracion8");

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredConfiguracionList() throws Exception {
    // when: Búsqueda por clave like e id equals
    Long id = 5L;
    String query = "clave~Configuracion%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(CONFIGURACION_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Configuracion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<Configuracion>>() {
        });

    // then: Respuesta OK, Configuraciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Configuracion> configuraciones = response.getBody();
    Assertions.assertThat(configuraciones.size()).isEqualTo(1);
    Assertions.assertThat(configuraciones.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(configuraciones.get(0).getClave()).startsWith("Configuracion");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedConfiguracionList() throws Exception {
    // when: Ordenación por id desc
    String query = "id-";

    URI uri = UriComponentsBuilder.fromUriString(CONFIGURACION_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Configuracion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<Configuracion>>() {
        });

    // then: Respuesta OK, Configuraciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Configuracion> configuraciones = response.getBody();
    Assertions.assertThat(configuraciones.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      Configuracion configuracion = configuraciones.get(i);
      Assertions.assertThat(configuracion.getId()).isEqualTo(8 - i);
      Assertions.assertThat(configuracion.getClave()).isEqualTo("Configuracion" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsConfiguracionSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por clave desc
    String sort = "clave-";
    // when: Filtra por clave like
    String filter = "clave~%00%";

    URI uri = UriComponentsBuilder.fromUriString(CONFIGURACION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Configuracion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Configuracion>>() {
        });

    // then: Respuesta OK, Configuraciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Configuracion> configuraciones = response.getBody();
    Assertions.assertThat(configuraciones.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene clave='Configuracion001', 'Configuracion002','Configuracion003'
    Assertions.assertThat(configuraciones.get(0).getClave()).isEqualTo("Configuracion" + String.format("%03d", 3));
    Assertions.assertThat(configuraciones.get(1).getClave()).isEqualTo("Configuracion" + String.format("%03d", 2));
    Assertions.assertThat(configuraciones.get(2).getClave()).isEqualTo("Configuracion" + String.format("%03d", 1));

  }

  /**
   * Función que devuelve un objeto Configuracion
   * 
   * @param id    id del Configuracion
   * @param clave la clave de la Configuracion
   * @return el objeto Configuracion
   */

  public Configuracion generarMockConfiguracion(Long id, String clave) {

    Configuracion configuracion = new Configuracion();
    configuracion.setId(id);
    configuracion.setClave(clave);
    configuracion.setDescripcion("Descripcion" + id);
    configuracion.setValor("Valor" + id);

    return configuracion;
  }

}