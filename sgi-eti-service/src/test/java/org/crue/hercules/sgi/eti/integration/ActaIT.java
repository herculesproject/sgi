package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.EstadoActa;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
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
 * Test de integracion de Acta.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class ActaIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String ACTA_CONTROLLER_BASE_PATH = "/actas";
  private static final String ESTADOACTA_CONTROLLER_BASE_PATH = "/estadoactas";

  private HttpEntity<Acta> buildRequest(HttpHeaders headers, Acta entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<Acta> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getActa_WithId_ReturnsActa() throws Exception {
    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V")));

    final ResponseEntity<Acta> response = restTemplate.exchange(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(headers, null), Acta.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Acta acta = response.getBody();

    Assertions.assertThat(acta.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(acta.getConvocatoriaReunion().getId()).as("convocatoriaReunion.id").isEqualTo(100L);
    Assertions.assertThat(acta.getHoraInicio()).as("horaInicio").isEqualTo(10);
    Assertions.assertThat(acta.getMinutoInicio()).as("minutoInicio").isEqualTo(15);
    Assertions.assertThat(acta.getHoraFin()).as("horaFin").isEqualTo(12);
    Assertions.assertThat(acta.getMinutoFin()).as("minutoFin").isEqualTo(0);
    Assertions.assertThat(acta.getResumen()).as("resumen").isEqualTo("Resumen123");
    Assertions.assertThat(acta.getNumero()).as("numero").isEqualTo(123);
    Assertions.assertThat(acta.getEstadoActual().getId()).as("estadoActual.id").isEqualTo(1L);
    Assertions.assertThat(acta.getInactiva()).as("inactiva").isEqualTo(true);
    Assertions.assertThat(acta.getActivo()).as("activo").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addActa_ReturnsActa() throws Exception {

    Acta nuevoActa = generarMockActa(null, 123);

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C")));

    final ResponseEntity<Acta> response = restTemplate.exchange(ACTA_CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(headers, nuevoActa), Acta.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    final Acta acta = response.getBody();

    Assertions.assertThat(acta.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(acta.getConvocatoriaReunion().getId()).as("convocatoriaReunion.id").isEqualTo(100L);
    Assertions.assertThat(acta.getHoraInicio()).as("horaInicio").isEqualTo(10);
    Assertions.assertThat(acta.getMinutoInicio()).as("minutoInicio").isEqualTo(15);
    Assertions.assertThat(acta.getHoraFin()).as("horaFin").isEqualTo(12);
    Assertions.assertThat(acta.getMinutoFin()).as("minutoFin").isEqualTo(0);
    Assertions.assertThat(acta.getResumen()).as("resumen").isEqualTo("Resumen123");
    Assertions.assertThat(acta.getNumero()).as("numero").isEqualTo(123);
    Assertions.assertThat(acta.getEstadoActual().getId()).as("estadoActual.id").isEqualTo(1L);
    Assertions.assertThat(acta.getInactiva()).as("inactiva").isEqualTo(true);
    Assertions.assertThat(acta.getActivo()).as("activo").isEqualTo(true);

    // When: se ha creado un nuevo acta
    // Then: deberá existir un EstadoActa inicial para ese acta creado

    // Acta creado
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V")));
    final ResponseEntity<Acta> fullDataActa = restTemplate.exchange(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(headers, null), Acta.class, 1L);

    String query = "acta.id:" + acta.getId();
    URI uri = UriComponentsBuilder.fromUriString(ESTADOACTA_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda EstadoEstado con el Acta creado
    final ResponseEntity<List<EstadoActa>> responseEstadoActa = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<EstadoActa>>() {
        });

    // then: Respuesta OK, retorna el EstadoActa inicial del Acta creado
    Assertions.assertThat(responseEstadoActa.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseEstadoActa.getBody().size()).as("size").isEqualTo(1);
    Assertions.assertThat(responseEstadoActa.getBody().get(0).getTipoEstadoActa()).as("tipoEstado")
        .isEqualTo(fullDataActa.getBody().getEstadoActual());
    Assertions.assertThat(responseEstadoActa.getBody().get(0).getActa()).as("acta").isEqualTo(fullDataActa.getBody());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeActa_Success() throws Exception {
    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-B")));

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<Acta> response = restTemplate.exchange(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(headers, null), Acta.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeActa_DoNotGetActa() throws Exception {
    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-B")));

    final ResponseEntity<Acta> response = restTemplate.exchange(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(headers, null), Acta.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceActa_ReturnsActa() throws Exception {

    Acta replaceActa = generarMockActa(1L, 456);

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-E")));

    final ResponseEntity<Acta> response = restTemplate.exchange(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(headers, replaceActa), Acta.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Acta acta = response.getBody();

    Assertions.assertThat(acta.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(acta.getConvocatoriaReunion().getId()).as("convocatoriaReunion.id").isEqualTo(100L);
    Assertions.assertThat(acta.getHoraInicio()).as("horaInicio").isEqualTo(10);
    Assertions.assertThat(acta.getMinutoInicio()).as("minutoInicio").isEqualTo(15);
    Assertions.assertThat(acta.getHoraFin()).as("horaFin").isEqualTo(12);
    Assertions.assertThat(acta.getMinutoFin()).as("minutoFin").isEqualTo(0);
    Assertions.assertThat(acta.getResumen()).as("resumen").isEqualTo("Resumen456");
    Assertions.assertThat(acta.getNumero()).as("numero").isEqualTo(456);
    Assertions.assertThat(acta.getEstadoActual().getId()).as("estadoActual.id").isEqualTo(1L);
    Assertions.assertThat(acta.getInactiva()).as("inactiva").isEqualTo(true);
    Assertions.assertThat(acta.getActivo()).as("activo").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsActaSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V")));

    final ResponseEntity<List<Acta>> response = restTemplate.exchange(ACTA_CONTROLLER_BASE_PATH, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Acta>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Acta> actas = response.getBody();
    Assertions.assertThat(actas.size()).as("size").isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).as("x-page").isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).as("x-page-size").isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).as("x-total-count").isEqualTo("8");

    // Contiene de id=6 a 8
    Assertions.assertThat(actas.get(0).getId()).as("0.id").isEqualTo(6);
    Assertions.assertThat(actas.get(1).getId()).as("1.id").isEqualTo(7);
    Assertions.assertThat(actas.get(2).getId()).as("2.id").isEqualTo(8);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredActaList() throws Exception {
    // when: Búsqueda por acta id equals
    Long id = 5L;
    String query = "id:" + id;

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V")));

    URI uri = UriComponentsBuilder.fromUriString(ACTA_CONTROLLER_BASE_PATH).queryParam("q", query).build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Acta>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Acta>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Acta> actas = response.getBody();
    Assertions.assertThat(actas.size()).as("size").isEqualTo(1);
    Assertions.assertThat(actas.get(0).getId()).as("id").isEqualTo(id);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedActaList() throws Exception {
    // when: Ordenación por id desc
    String sort = "id-";

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V")));

    URI uri = UriComponentsBuilder.fromUriString(ACTA_CONTROLLER_BASE_PATH).queryParam("s", sort).build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Acta>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Acta>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Acta> actas = response.getBody();
    Assertions.assertThat(actas.size()).as("size").isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      Acta acta = actas.get(i);
      Assertions.assertThat(acta.getId()).as((8 - i) + ".id").isEqualTo(8 - i);
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsActaSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=3
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V")));

    // when: Ordena por id desc
    String sort = "id-";
    // when: Filtra por id menor
    String filter = "id<4";

    URI uri = UriComponentsBuilder.fromUriString(ACTA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Acta>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Acta>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Acta> actas = response.getBody();
    Assertions.assertThat(actas.size()).as("size").isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("x-page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("x-page-size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("x-total-count").isEqualTo("3");

    // Contiene id=3, 2, 1
    Assertions.assertThat(actas.get(0).getId()).as("0.id").isEqualTo(3);
    Assertions.assertThat(actas.get(1).getId()).as("1.id").isEqualTo(2);
    Assertions.assertThat(actas.get(2).getId()).as("2.id").isEqualTo(1);
  }

  /**
   * Función que devuelve un objeto Acta
   * 
   * @param id     id del acta
   * @param numero numero del acta
   * @return el objeto Acta
   */
  public Acta generarMockActa(Long id, Integer numero) {
    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(100L);

    TipoEstadoActa tipoEstadoActa = new TipoEstadoActa();
    tipoEstadoActa.setId(1L);
    tipoEstadoActa.setNombre("En elaboración");
    tipoEstadoActa.setActivo(Boolean.TRUE);

    Acta acta = new Acta();
    acta.setId(id);
    acta.setConvocatoriaReunion(convocatoriaReunion);
    acta.setHoraInicio(10);
    acta.setMinutoInicio(15);
    acta.setHoraFin(12);
    acta.setMinutoFin(0);
    acta.setResumen("Resumen" + numero);
    acta.setNumero(numero);
    acta.setEstadoActual(tipoEstadoActa);
    acta.setInactiva(true);
    acta.setActivo(true);

    return acta;
  }

}