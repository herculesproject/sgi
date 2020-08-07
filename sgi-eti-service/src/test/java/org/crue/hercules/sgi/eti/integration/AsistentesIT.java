package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Asistentes;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer.TokenBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
 * Test de integracion de Asistentes.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class AsistentesIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_BY_CONVOCATORIA_REUNION = "/convocatoriareunion";
  private static final String ASISTENTE_CONTROLLER_BASE_PATH = "/asistentes";

  private HttpEntity<Asistentes> buildRequest(HttpHeaders headers, Asistentes entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<Asistentes> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getAsistentes_WithId_ReturnsAsistentes() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ASISTENTES-EDITAR", "ETI-ASISTENTES-VER")));

    final ResponseEntity<Asistentes> response = restTemplate.exchange(
        ASISTENTE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(headers, null),
        Asistentes.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Asistentes asistente = response.getBody();

    Assertions.assertThat(asistente.getId()).isEqualTo(1L);
    Assertions.assertThat(asistente.getMotivo()).isEqualTo("Motivo1");
    Assertions.assertThat(asistente.getAsistencia()).isTrue();
    Assertions.assertThat(asistente.getConvocatoriaReunion().getId()).isEqualTo(1L);
    Assertions.assertThat(asistente.getEvaluador().getId()).isEqualTo(1L);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addAsistentes_ReturnsAsistentes() throws Exception {

    Asistentes nuevoAsistente = new Asistentes();
    nuevoAsistente.setMotivo("Motivo 1");
    nuevoAsistente.setAsistencia(Boolean.TRUE);
    nuevoAsistente.setConvocatoriaReunion(new ConvocatoriaReunion());
    nuevoAsistente.getConvocatoriaReunion().setId(1L);
    nuevoAsistente.setEvaluador(new Evaluador());
    nuevoAsistente.getEvaluador().setId(1L);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ASISTENTES-EDITAR", "ETI-ASISTENTES-VER")));

    restTemplate.exchange(ASISTENTE_CONTROLLER_BASE_PATH, HttpMethod.POST, buildRequest(headers, nuevoAsistente),
        Asistentes.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeAsistentes_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ASISTENTES-EDITAR", "ETI-ASISTENTES-VER")));
    final ResponseEntity<Asistentes> response = restTemplate.exchange(
        ASISTENTE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(headers, null),
        Asistentes.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeAsistentes_DoNotGetAsistentes() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ASISTENTES-EDITAR", "ETI-ASISTENTES-VER")));
    final ResponseEntity<Asistentes> response = restTemplate.exchange(
        ASISTENTE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(headers, null),
        Asistentes.class, 2L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceAsistentes_ReturnsAsistentes() throws Exception {

    Asistentes replaceAsistente = new Asistentes();
    replaceAsistente.setMotivo("Motivo 1");
    replaceAsistente.setAsistencia(Boolean.TRUE);
    replaceAsistente.setConvocatoriaReunion(new ConvocatoriaReunion());
    replaceAsistente.getConvocatoriaReunion().setId(1L);
    replaceAsistente.setEvaluador(new Evaluador());
    replaceAsistente.getEvaluador().setId(1L);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ASISTENTES-EDITAR", "ETI-ASISTENTES-VER")));

    final ResponseEntity<Asistentes> response = restTemplate.exchange(
        ASISTENTE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(headers, replaceAsistente),
        Asistentes.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Asistentes asistente = response.getBody();

    Assertions.assertThat(asistente.getId()).isNotNull();
    Assertions.assertThat(asistente.getId()).isEqualTo(1L);
    Assertions.assertThat(asistente.getMotivo()).isEqualTo("Motivo 1");
    Assertions.assertThat(asistente.getAsistencia()).isTrue();
    Assertions.assertThat(asistente.getConvocatoriaReunion().getId()).isEqualTo(1L);
    Assertions.assertThat(asistente.getEvaluador().getId()).isEqualTo(1L);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsAsistentesSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ASISTENTES-EDITAR", "ETI-ASISTENTES-VER")));
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "3");

    final ResponseEntity<List<Asistentes>> response = restTemplate.exchange(ASISTENTE_CONTROLLER_BASE_PATH,
        HttpMethod.GET, buildRequest(headers, null), new ParameterizedTypeReference<List<Asistentes>>() {
        });

    // then: Respuesta OK, Asistentes retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Asistentes> asistentes = response.getBody();
    Assertions.assertThat(asistentes.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("6");

    // Contiene de motivo='Motivo4' a
    // 'Motivo6'
    Assertions.assertThat(asistentes.get(0).getMotivo()).isEqualTo("Motivo4");
    Assertions.assertThat(asistentes.get(1).getMotivo()).isEqualTo("Motivo5");
    Assertions.assertThat(asistentes.get(2).getMotivo()).isEqualTo("Motivo6");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredAsistentesList() throws Exception {
    // when: Búsqueda por motivo like e id equals
    Long id = 5L;
    String query = "motivo~motivo%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(ASISTENTE_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ASISTENTES-EDITAR", "ETI-ASISTENTES-VER")));

    final ResponseEntity<List<Asistentes>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Asistentes>>() {
        });

    // then: Respuesta OK, Asistentes retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Asistentes> asistentes = response.getBody();
    Assertions.assertThat(asistentes.size()).isEqualTo(1);
    Assertions.assertThat(asistentes.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(asistentes.get(0).getMotivo()).startsWith("Motivo5");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedAsistentesList() throws Exception {
    // when: Ordenación por motivo desc
    String query = "motivo-";

    URI uri = UriComponentsBuilder.fromUriString(ASISTENTE_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ASISTENTES-EDITAR", "ETI-ASISTENTES-VER")));

    final ResponseEntity<List<Asistentes>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Asistentes>>() {
        });

    // then: Respuesta OK, Asistentes retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Asistentes> asistentes = response.getBody();
    Assertions.assertThat(asistentes.size()).isEqualTo(6);
    for (int i = 0; i < 6; i++) {
      Asistentes asistente = asistentes.get(i);
      Assertions.assertThat(asistente.getId()).isEqualTo(6 - i);
      Assertions.assertThat(asistente.getMotivo()).isEqualTo("Motivo" + String.valueOf(6 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsAsistentesSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ASISTENTES-EDITAR", "ETI-ASISTENTES-VER")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "motivo-";
    // when: Filtra por motivo like
    String filter = "motivo~%motivo%";

    URI uri = UriComponentsBuilder.fromUriString(ASISTENTE_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Asistentes>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Asistentes>>() {
        });

    // then: Respuesta OK, Asistentes retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Asistentes> asistentes = response.getBody();
    Assertions.assertThat(asistentes.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("6");

    // Contiene de motivo='Motivo6' a
    // 'Motivo4'
    Assertions.assertThat(asistentes.get(0).getMotivo()).isEqualTo("Motivo6");
    Assertions.assertThat(asistentes.get(1).getMotivo()).isEqualTo("Motivo5");
    Assertions.assertThat(asistentes.get(2).getMotivo()).isEqualTo("Motivo4");

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllByConvocatoriaReunionId_Unlimited_ReturnsFullAsistentesList() throws Exception {

    // given: Datos existentes
    Long convocatoriaReunionId = 1L;
    final String url = new StringBuilder(ASISTENTE_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_BY_CONVOCATORIA_REUNION)//
        .append(PATH_PARAMETER_ID).toString();

    List<Asistentes> result = new LinkedList<>();
    result.add(generarMockAsistentes(1L, 1L, convocatoriaReunionId));
    result.add(generarMockAsistentes(2L, 1L, convocatoriaReunionId));
    result.add(generarMockAsistentes(3L, 1L, convocatoriaReunionId));

    // when: Se buscan todos los datos

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-ACT-E")));

    final ResponseEntity<List<Asistentes>> response = restTemplate.exchange(url, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Asistentes>>() {
        }, convocatoriaReunionId);

    // then: Se recuperan todos los datos
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(result);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllByConvocatoriaReunionId_WithPaging_ReturnsAsistentesSubList() throws Exception {

    Long convocatoriaReunionId = 1L;
    final String url = new StringBuilder(ASISTENTE_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_BY_CONVOCATORIA_REUNION)//
        .append(PATH_PARAMETER_ID).toString();

    List<Asistentes> result = new LinkedList<>();
    result.add(generarMockAsistentes(1L, 1L, convocatoriaReunionId));
    result.add(generarMockAsistentes(2L, 1L, convocatoriaReunionId));
    result.add(generarMockAsistentes(3L, 1L, convocatoriaReunionId));

    // página 1 con 2 elementos por página

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-ACT-E")));
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    Pageable pageable = PageRequest.of(1, 2);
    Page<Asistentes> pageResult = new PageImpl<>(result.subList(2, 3), pageable, result.size());

    // when: Se buscan los datos paginados
    final ResponseEntity<List<Asistentes>> response = restTemplate.exchange(url, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Asistentes>>() {
        }, convocatoriaReunionId);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page"))
        .isEqualTo(String.valueOf(pageResult.getPageable().getPageNumber()));
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size"))
        .isEqualTo(String.valueOf(pageResult.getPageable().getPageSize()));
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Total-Count"))
        .isEqualTo(String.valueOf(pageResult.getNumberOfElements()));
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Count"))
        .isEqualTo(String.valueOf(pageResult.getTotalPages()));
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count"))
        .isEqualTo(String.valueOf(pageResult.getTotalElements()));
    Assertions.assertThat(response.getBody().size()).isEqualTo(pageResult.getContent().size());
    Assertions.assertThat(response.getBody()).isEqualTo(pageResult.getContent());

  }

  /**
   * Función que devuelve un objeto Asistentes
   * 
   * @param id         id del asistentes
   * @param motivo     motivo
   * @param asistencia asistencia
   * @return el objeto Asistentes
   */

  private Asistentes generarMockAsistentes(Long id, Long evaluadorId, Long convocatoriaReunionId) {

    Asistentes asistentes = new Asistentes();
    asistentes.setId(id);
    asistentes.setEvaluador(generarMockEvaluador(evaluadorId));
    asistentes.setConvocatoriaReunion(getMockConvocatoriaReunion(convocatoriaReunionId));
    asistentes.setMotivo("Motivo" + id);
    asistentes.setAsistencia(Boolean.TRUE);

    return asistentes;
  }

  /**
   * Función que devuelve un objeto Evaluador
   * 
   * @param id      id del Evaluador
   * @param resumen el resumen de Evaluador
   * @return el objeto Evaluador
   */

  private Evaluador generarMockEvaluador(Long id) {
    CargoComite cargoComite = new CargoComite();
    cargoComite.setId(1L);
    cargoComite.setNombre("CargoComite1");
    cargoComite.setActivo(Boolean.TRUE);

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    Evaluador evaluador = new Evaluador();
    evaluador.setId(id);
    evaluador.setCargoComite(cargoComite);
    evaluador.setComite(comite);
    evaluador.setFechaAlta(LocalDate.of(2020, 7, 1));
    evaluador.setFechaBaja(LocalDate.of(2021, 7, 1));
    evaluador.setResumen("Evaluador1");
    evaluador.setUsuarioRef("user-001");
    evaluador.setActivo(Boolean.TRUE);

    return evaluador;
  }

  /**
   * Genera un objeto {@link ConvocatoriaReunion}
   * 
   * @param id       id de la convocatoria reunión
   * @param comiteId comite id
   * @return un objeto {@link ConvocatoriaReunion}
   */
  private ConvocatoriaReunion getMockConvocatoriaReunion(Long id) {

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);

    final ConvocatoriaReunion data = new ConvocatoriaReunion();
    data.setId(id);
    data.setComite(comite);
    data.setFechaEvaluacion(LocalDateTime.of(2020, 7, 1, 10, 10, 10));
    data.setFechaLimite(LocalDate.of(2020, 8, 1));
    data.setLugar("Lugar 01");
    data.setOrdenDia("Orden del día convocatoria reunión 01");
    data.setAnio(2020);
    data.setNumeroActa(1L);
    data.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    data.setHoraInicio(8);
    data.setMinutoInicio(30);
    data.setFechaEnvio(LocalDate.of(2020, 7, 13));
    data.setActivo(Boolean.TRUE);

    return data;
  }

}
