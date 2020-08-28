package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoActividad;
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
 * Test de integracion de PeticionEvaluacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })

public class PeticionEvaluacionIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PETICION_EVALUACION_CONTROLLER_BASE_PATH = "/peticionevaluaciones";

  private HttpEntity<PeticionEvaluacion> buildRequest(HttpHeaders headers, PeticionEvaluacion entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-PETICIONEVALUACION-EDITAR", "ETI-PETICIONEVALUACION-VER")));

    HttpEntity<PeticionEvaluacion> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getPeticionEvaluacion_WithId_ReturnsPeticionEvaluacion() throws Exception {
    final ResponseEntity<PeticionEvaluacion> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        PeticionEvaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final PeticionEvaluacion tipoActividad = response.getBody();

    Assertions.assertThat(tipoActividad.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoActividad.getTitulo()).isEqualTo("PeticionEvaluacion1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addPeticionEvaluacion_ReturnsPeticionEvaluacion() throws Exception {

    PeticionEvaluacion nuevoPeticionEvaluacion = generarMockPeticionEvaluacion(1L, "titulo");
    nuevoPeticionEvaluacion.setId(null);

    final ResponseEntity<PeticionEvaluacion> response = restTemplate.exchange(PETICION_EVALUACION_CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, nuevoPeticionEvaluacion), PeticionEvaluacion.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody().getId()).isNotNull();
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removePeticionEvaluacion_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<PeticionEvaluacion> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        PeticionEvaluacion.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removePeticionEvaluacion_DoNotGetPeticionEvaluacion() throws Exception {
    restTemplate.exchange(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE,
        buildRequest(null, null), PeticionEvaluacion.class, 1L);

    final ResponseEntity<PeticionEvaluacion> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        PeticionEvaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replacePeticionEvaluacion_ReturnsPeticionEvaluacion() throws Exception {

    PeticionEvaluacion replacePeticionEvaluacion = generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1");

    final ResponseEntity<PeticionEvaluacion> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, replacePeticionEvaluacion), PeticionEvaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final PeticionEvaluacion tipoActividad = response.getBody();

    Assertions.assertThat(tipoActividad.getId()).isNotNull();
    Assertions.assertThat(tipoActividad.getTitulo()).isEqualTo(replacePeticionEvaluacion.getTitulo());
    Assertions.assertThat(tipoActividad.getActivo()).isEqualTo(replacePeticionEvaluacion.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsPeticionEvaluacionSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(PETICION_EVALUACION_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<PeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<PeticionEvaluacion>>() {
        });

    // then: Respuesta OK, PeticionEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PeticionEvaluacion> peticionEvaluaciones = response.getBody();
    Assertions.assertThat(peticionEvaluaciones.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de titulo='PeticionEvaluacion6' a 'PeticionEvaluacion8'
    Assertions.assertThat(peticionEvaluaciones.get(0).getTitulo()).isEqualTo("PeticionEvaluacion6");
    Assertions.assertThat(peticionEvaluaciones.get(1).getTitulo()).isEqualTo("PeticionEvaluacion7");
    Assertions.assertThat(peticionEvaluaciones.get(2).getTitulo()).isEqualTo("PeticionEvaluacion8");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredPeticionEvaluacionList() throws Exception {
    // when: Búsqueda por titulo like e id equals
    Long id = 5L;
    String query = "titulo~PeticionEvaluacion%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(PETICION_EVALUACION_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<PeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<PeticionEvaluacion>>() {
        });

    // then: Respuesta OK, PeticionEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PeticionEvaluacion> peticionEvaluaciones = response.getBody();
    Assertions.assertThat(peticionEvaluaciones.size()).isEqualTo(1);
    Assertions.assertThat(peticionEvaluaciones.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(peticionEvaluaciones.get(0).getTitulo()).startsWith("PeticionEvaluacion");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedPeticionEvaluacionList() throws Exception {
    // when: Ordenación por titulo desc
    String query = "titulo-";

    URI uri = UriComponentsBuilder.fromUriString(PETICION_EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<PeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<PeticionEvaluacion>>() {
        });

    // then: Respuesta OK, PeticionEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PeticionEvaluacion> peticionEvaluaciones = response.getBody();
    Assertions.assertThat(peticionEvaluaciones.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      PeticionEvaluacion tipoActividad = peticionEvaluaciones.get(i);
      Assertions.assertThat(tipoActividad.getId()).isEqualTo(8 - i);
      Assertions.assertThat(tipoActividad.getTitulo()).isEqualTo("PeticionEvaluacion" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsPeticionEvaluacionSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por titulo desc
    String sort = "titulo-";
    // when: Filtra por titulo like e id equals
    String filter = "titulo~%00%";

    URI uri = UriComponentsBuilder.fromUriString(PETICION_EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<PeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<PeticionEvaluacion>>() {
        });

    // then: Respuesta OK, PeticionEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PeticionEvaluacion> peticionEvaluaciones = response.getBody();
    Assertions.assertThat(peticionEvaluaciones.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene titulo='PeticionEvaluacion001', 'PeticionEvaluacion002',
    // 'PeticionEvaluacion003'
    Assertions.assertThat(peticionEvaluaciones.get(0).getTitulo())
        .isEqualTo("PeticionEvaluacion" + String.format("%03d", 3));
    Assertions.assertThat(peticionEvaluaciones.get(1).getTitulo())
        .isEqualTo("PeticionEvaluacion" + String.format("%03d", 2));
    Assertions.assertThat(peticionEvaluaciones.get(2).getTitulo())
        .isEqualTo("PeticionEvaluacion" + String.format("%03d", 1));

  }

  /**
   * Función que devuelve un objeto PeticionEvaluacion
   * 
   * @param id     id del PeticionEvaluacion
   * @param titulo el título de PeticionEvaluacion
   * @return el objeto PeticionEvaluacion
   */

  public PeticionEvaluacion generarMockPeticionEvaluacion(Long id, String titulo) {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo" + id);
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico" + id);
    peticionEvaluacion.setExterno(Boolean.FALSE);
    peticionEvaluacion.setFechaFin(LocalDate.now());
    peticionEvaluacion.setFechaInicio(LocalDate.now());
    peticionEvaluacion.setFuenteFinanciacion("Fuente financiación" + id);
    peticionEvaluacion.setObjetivos("Objetivos" + id);
    peticionEvaluacion.setOtroValorSocial("Otro valor social" + id);
    peticionEvaluacion.setResumen("Resumen" + id);
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria" + id);
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo(titulo);
    peticionEvaluacion.setPersonaRef("user-00" + id);
    peticionEvaluacion.setValorSocial(3);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    return peticionEvaluacion;
  }

}