package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
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
 * Test de integracion de Memoria.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class MemoriaIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String MEMORIA_CONTROLLER_BASE_PATH = "/memorias";

  private HttpEntity<Memoria> buildRequest(HttpHeaders headers, Memoria entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-MEMORIA-EDITAR", "ETI-MEMORIA-VER")));

    HttpEntity<Memoria> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getMemoria_WithId_ReturnsMemoria() throws Exception {
    final ResponseEntity<Memoria> response = restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), Memoria.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Memoria tipoMemoria = response.getBody();

    Assertions.assertThat(tipoMemoria.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoMemoria.getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(tipoMemoria.getNumReferencia()).isEqualTo("ref-5588");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addMemoria_ReturnsMemoria() throws Exception {

    Memoria nuevaMemoria = generarMockMemoria(1L, "ref-5588", "Memoria1", 1);

    restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH, HttpMethod.POST, buildRequest(null, nuevaMemoria),
        Memoria.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeMemoria_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<Memoria> response = restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), Memoria.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeMemoria_DoNotGetMemoria() throws Exception {

    final ResponseEntity<Memoria> response = restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), Memoria.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceMemoria_ReturnsMemoria() throws Exception {

    Memoria replaceMemoria = generarMockMemoria(1L, "ref-5588", "Memoria1", 1);

    final ResponseEntity<Memoria> response = restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, replaceMemoria), Memoria.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Memoria tipoMemoria = response.getBody();

    Assertions.assertThat(tipoMemoria.getId()).isNotNull();
    Assertions.assertThat(tipoMemoria.getTitulo()).isEqualTo(replaceMemoria.getTitulo());
    Assertions.assertThat(tipoMemoria.getNumReferencia()).isEqualTo(replaceMemoria.getNumReferencia());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsMemoriaSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(MEMORIA_CONTROLLER_BASE_PATH, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Memoria>>() {
        });

    // then: Respuesta OK, Memorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Memoria> tipoMemorias = response.getBody();
    Assertions.assertThat(tipoMemorias.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de titulo='Memoria6' a 'Memoria8'
    Assertions.assertThat(tipoMemorias.get(0).getTitulo()).isEqualTo("Memoria6");
    Assertions.assertThat(tipoMemorias.get(1).getTitulo()).isEqualTo("Memoria7");
    Assertions.assertThat(tipoMemorias.get(2).getTitulo()).isEqualTo("Memoria8");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredMemoriaList() throws Exception {
    // when: Búsqueda por titulo like e id equals
    Long id = 5L;
    String query = "titulo~Memoria%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(MEMORIA_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(null, null),
        new ParameterizedTypeReference<List<Memoria>>() {
        });

    // then: Respuesta OK, Memorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Memoria> tipoMemorias = response.getBody();
    Assertions.assertThat(tipoMemorias.size()).isEqualTo(1);
    Assertions.assertThat(tipoMemorias.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(tipoMemorias.get(0).getTitulo()).startsWith("Memoria");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedMemoriaList() throws Exception {
    // when: Ordenación por titulo desc
    String query = "titulo-";

    URI uri = UriComponentsBuilder.fromUriString(MEMORIA_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(null, null),
        new ParameterizedTypeReference<List<Memoria>>() {
        });

    // then: Respuesta OK, Memorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Memoria> tipoMemorias = response.getBody();
    Assertions.assertThat(tipoMemorias.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      Memoria tipoMemoria = tipoMemorias.get(i);
      Assertions.assertThat(tipoMemoria.getId()).isEqualTo(8 - i);
      Assertions.assertThat(tipoMemoria.getTitulo()).isEqualTo("Memoria" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsMemoriaSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por titulo desc
    String sort = "titulo-";
    // when: Filtra por titulo like e id equals
    String filter = "titulo~%00%";

    URI uri = UriComponentsBuilder.fromUriString(MEMORIA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Memoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Memoria>>() {
        });

    // then: Respuesta OK, Memorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Memoria> tipoMemorias = response.getBody();
    Assertions.assertThat(tipoMemorias.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene titulo='Memoria001', 'Memoria002',
    // 'Memoria003'
    Assertions.assertThat(tipoMemorias.get(0).getTitulo()).isEqualTo("Memoria" + String.format("%03d", 3));
    Assertions.assertThat(tipoMemorias.get(1).getTitulo()).isEqualTo("Memoria" + String.format("%03d", 2));
    Assertions.assertThat(tipoMemorias.get(2).getTitulo()).isEqualTo("Memoria" + String.format("%03d", 1));

  }

  /**
   * Función que devuelve un objeto Memoria.
   * 
   * @param id            id del memoria.
   * @param numReferencia número de la referencia de la memoria.
   * @param titulo        titulo de la memoria.
   * @param version       version de la memoria.
   * @return el objeto tipo Memoria
   */

  private Memoria generarMockMemoria(Long id, String numReferencia, String titulo, Integer version) {

    return new Memoria(id, numReferencia, generarMockPeticionEvaluacion(id, titulo + " PeticionEvaluacion" + id),
        generarMockComite(id, "comite" + id, true), titulo, "user-00" + id,
        generarMockTipoMemoria(1L, "TipoMemoria1", true),
        generarMockTipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), LocalDate.now(), Boolean.TRUE,
        generarMockRetrospectiva(1L), version, Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto PeticionEvaluacion
   * 
   * @param id     id del PeticionEvaluacion
   * @param titulo el título de PeticionEvaluacion
   * @return el objeto PeticionEvaluacion
   */
  private PeticionEvaluacion generarMockPeticionEvaluacion(Long id, String titulo) {
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
    peticionEvaluacion.setUsuarioRef("user-00" + id);
    peticionEvaluacion.setValorSocial(3);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    return peticionEvaluacion;
  }

  /**
   * Función que devuelve un objeto comité.
   * 
   * @param id     identificador del comité.
   * @param comite comité.
   * @param activo indicador de activo.
   */
  private Comite generarMockComite(Long id, String comite, Boolean activo) {
    return new Comite(id, comite, activo);

  }

  /**
   * Función que devuelve un objeto tipo memoria.
   * 
   * @param id     identificador del tipo memoria.
   * @param nombre nobmre.
   * @param activo indicador de activo.
   */
  private TipoMemoria generarMockTipoMemoria(Long id, String nombre, Boolean activo) {
    return new TipoMemoria(id, nombre, activo);

  }

  /**
   * Función que devuelve un objeto TipoEstadoMemoria.
   * 
   * @param id     identificador del TipoEstadoMemoria.
   * @param nombre nombre.
   * @param activo indicador de activo.
   */
  private TipoEstadoMemoria generarMockTipoEstadoMemoria(Long id, String nombre, Boolean activo) {
    return new TipoEstadoMemoria(id, nombre, activo);

  }

  /**
   * Genera un objeto {@link Retrospectiva}
   * 
   * @param id
   * @return Retrospectiva
   */
  private Retrospectiva generarMockRetrospectiva(Long id) {

    final Retrospectiva data = new Retrospectiva();
    data.setId(id);
    data.setEstadoRetrospectiva(generarMockDataEstadoRetrospectiva((id % 2 == 0) ? 2L : 1L));
    data.setFechaRetrospectiva(LocalDate.of(2020, 7, id.intValue()));

    return data;
  }

  /**
   * Genera un objeto {@link EstadoRetrospectiva}
   * 
   * @param id
   * @return EstadoRetrospectiva
   */
  private EstadoRetrospectiva generarMockDataEstadoRetrospectiva(Long id) {

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final EstadoRetrospectiva data = new EstadoRetrospectiva();
    data.setId(id);
    data.setNombre("NombreEstadoRetrospectiva" + txt);
    data.setActivo(Boolean.TRUE);

    return data;
  }

}