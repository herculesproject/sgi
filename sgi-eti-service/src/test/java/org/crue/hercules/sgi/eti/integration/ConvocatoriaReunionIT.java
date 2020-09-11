package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.*;
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
 * Test de integracion de ConvocatoriaReunion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class ConvocatoriaReunionIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH = "/convocatoriareuniones";
  private static final String PATH_PARAMETER_BY_EVALUACIONES = "/evaluaciones";

  private HttpEntity<ConvocatoriaReunion> buildRequest(HttpHeaders headers, ConvocatoriaReunion entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization",
          String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-CNV-C", "ETI-CNV-E", "ETI-CNV-V")));
    }

    HttpEntity<ConvocatoriaReunion> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsConvocatoriaReunion() throws Exception {

    // given: Nueva entidad
    final ConvocatoriaReunion newConvocatoriaReunion = getMockData(1L, 1L, 1L);
    newConvocatoriaReunion.setId(null);

    // when: Se crea la entidad
    final ResponseEntity<ConvocatoriaReunion> response = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH, HttpMethod.POST, buildRequest(null, newConvocatoriaReunion),
        ConvocatoriaReunion.class);

    // then: La entidad se crea correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    final ConvocatoriaReunion convocatoriaReunion = response.getBody();
    Assertions.assertThat(convocatoriaReunion.getId()).isNotNull();
    newConvocatoriaReunion.setId(convocatoriaReunion.getId());
    Assertions.assertThat(convocatoriaReunion).isEqualTo(newConvocatoriaReunion);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_WithExistingId_ReturnsConvocatoriaReunion() throws Exception {

    // given: Entidad existente que se va a actualizar
    final ConvocatoriaReunion updatedConvocatoriaReunion = getMockData(2L, 1L, 2L);
    updatedConvocatoriaReunion.setId(1L);

    // when: Se actualiza la entidad
    final ResponseEntity<ConvocatoriaReunion> response = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, updatedConvocatoriaReunion), ConvocatoriaReunion.class, updatedConvocatoriaReunion.getId());

    // then: Los datos se actualizan correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(updatedConvocatoriaReunion);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_WithExistingId_Return204() throws Exception {

    // given: Entidad existente con la propiedad activo a true
    Long id = 1L;

    ResponseEntity<ConvocatoriaReunion> response = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ConvocatoriaReunion.class, id);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().getActivo()).isEqualTo(Boolean.TRUE);

    // when: Se elimina la entidad
    response = restTemplate.exchange(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE,
        buildRequest(null, null), ConvocatoriaReunion.class, id);

    // then: La entidad pasa a tener propiedad activo a false
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    response = restTemplate.exchange(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET,
        buildRequest(null, null), ConvocatoriaReunion.class, id);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().getActivo()).isEqualTo(Boolean.FALSE);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_WithExistingId_ReturnsConvocatoriaReunion() throws Exception {

    // given: Entidad con un determinado Id
    final ConvocatoriaReunion convocatoriaReunion = getMockData(1L, 1L, 1L);

    // when: Se busca la entidad por ese Id
    ResponseEntity<ConvocatoriaReunion> response = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ConvocatoriaReunion.class, convocatoriaReunion.getId());

    // then: Se recupera la entidad con el Id
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(convocatoriaReunion);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_WithNotExistingId_Returns404() throws Exception {

    // given: No existe entidad con el id indicado
    Long id = 1L;

    // when: Se busca la entidad por ese Id
    ResponseEntity<ConvocatoriaReunion> response = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ConvocatoriaReunion.class, id);

    // then: Se produce error porque no encuentra la entidad con ese Id
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_Unlimited_ReturnsFullConvocatoriaReunionList() throws Exception {

    // given: Datos existentes
    List<ConvocatoriaReunion> response = new LinkedList<>();
    response.add(getMockData(1L, 1L, 1L));
    response.add(getMockData(2L, 1L, 2L));

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-CNV-V")));

    // when: Se buscan todos los datos
    final ResponseEntity<List<ConvocatoriaReunion>> result = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<ConvocatoriaReunion>>() {
        });

    // then: Se recuperan todos los datos
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsConvocatoriaReunionSubList() throws Exception {

    // given: Datos existentes
    List<ConvocatoriaReunion> response = new LinkedList<>();
    response.add(getMockData(5L, 3L, 1L));

    // página 2 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "2");
    headers.add("X-Page-Size", "2");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-CNV-V")));

    // when: Se buscan los datos paginados
    final ResponseEntity<List<ConvocatoriaReunion>> result = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<ConvocatoriaReunion>>() {
        });

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody().size()).isEqualTo(1);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).isEqualTo("3");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).isEqualTo("5");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredConvocatoriaReunionList() throws Exception {

    // given: Datos existentes
    List<ConvocatoriaReunion> response = new LinkedList<>();
    response.add(getMockData(3L, 2L, 1L));

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-CNV-V")));

    // search by codigo like, id equals
    Long id = 3L;
    String query = "numeroActa<4,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Se buscan los datos con el filtro indicado
    final ResponseEntity<List<ConvocatoriaReunion>> result = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaReunion>>() {
        });

    // then: Se recuperan los datos filtrados
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).isEqualTo("1");

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedConvocatoriaReunionList() throws Exception {

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-CNV-V")));

    // sort by id desc
    String sort = "id-";

    URI uri = UriComponentsBuilder.fromUriString(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .build(false).toUri();

    // when: Se buscan los datos con la ordenación indicada
    final ResponseEntity<List<ConvocatoriaReunion>> result = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaReunion>>() {
        });

    // then: Se recuperan los datos filtrados, ordenados y paginados
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody().get(0).getId()).isEqualTo(5L);
    Assertions.assertThat(result.getBody().get(4).getId()).isEqualTo(1L);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("5");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).isEqualTo("5");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsConvocatoriaReunionSubList() throws Exception {

    // given: Datos existentes
    List<ConvocatoriaReunion> response = new LinkedList<>();
    response.add(getMockData(1L, 1L, 1L));

    // página 1 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-CNV-V")));

    // sort
    String sort = "id-";

    // search
    String query = "numeroActa<4";

    URI uri = UriComponentsBuilder.fromUriString(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", query).build(false).toUri();

    // when: Se buscan los datos paginados con el filtro y orden indicados
    final ResponseEntity<List<ConvocatoriaReunion>> result = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaReunion>>() {
        });

    // then: Se recuperan los datos filtrados, ordenados y paginados
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody().size()).as("size").isEqualTo(1);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).as("X-Page").isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).as("X-Page-Total-Count").isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).as("X-Page-Count").isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAsistentes_Unlimited_ReturnsFullAsistentesList() throws Exception {

    // given: Datos existentes
    Long convocatoriaReunionId = 1L;
    final String url = new StringBuilder(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .append("/asistentes").toString();

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
  public void findAsistentes_WithPaging_ReturnsAsistentesSubList() throws Exception {

    Long convocatoriaReunionId = 1L;
    final String url = new StringBuilder(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .append("/asistentes").toString();

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

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findEvaluacionesActivas_Unlimited_ReturnsFullEvaluacionList() throws Exception {

    // given: Datos existentes
    Long convocatoriaReunionId = 1L;
    final String url = new StringBuilder(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .append("/evaluaciones-activas").toString();

    List<Evaluacion> result = new ArrayList<>();
    result.add(generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1)));
    result.add(generarMockEvaluacion(Long.valueOf(3), String.format("%03d", 3)));
    result.add(generarMockEvaluacion(Long.valueOf(5), String.format("%03d", 5)));

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-ACT-E")));

    // when: Se buscan todos los datos
    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(url, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        }, convocatoriaReunionId);

    // then: Se recuperan todos los datos
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(result);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findEvaluacionesActivas_WithPaging_ReturnsEvaluacionSubList() throws Exception {

    Long convocatoriaReunionId = 1L;
    final String url = new StringBuilder(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .append("/evaluaciones-activas").toString();

    List<Evaluacion> result = new LinkedList<>();
    result.add(generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1)));
    result.add(generarMockEvaluacion(Long.valueOf(3), String.format("%03d", 3)));
    result.add(generarMockEvaluacion(Long.valueOf(5), String.format("%03d", 5)));

    // página 1 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-ACT-E")));
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    Pageable pageable = PageRequest.of(1, 2);
    Page<Evaluacion> pageResult = new PageImpl<>(result.subList(2, 3), pageable, result.size());

    // when: Se buscan los datos paginados
    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(url, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
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

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getEvaluaciones_Unlimited_ReturnsFullEvaluacionList() throws Exception {

    // given: Datos existentes
    Long convocatoriaReunionId = 1L;
    final String url = new StringBuilder(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID).append(PATH_PARAMETER_BY_EVALUACIONES)//
        .toString();

    List<Evaluacion> result = new ArrayList<>();
    result.add(generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1), convocatoriaReunionId));
    result.add(generarMockEvaluacion(Long.valueOf(3), String.format("%03d", 3), convocatoriaReunionId));
    result.add(generarMockEvaluacion(Long.valueOf(5), String.format("%03d", 5), convocatoriaReunionId));

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));

    // when: Se buscan todos los datos
    ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(url, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<Evaluacion>>() {
        }, convocatoriaReunionId);

    // then: Se recuperan todos los datos
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(result);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getEvaluaciones_WithPaging_ReturnsEvaluacionSubList() throws Exception {

    Long convocatoriaReunionId = 1L;
    final String url = new StringBuilder(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID).append(PATH_PARAMETER_BY_EVALUACIONES)//
        .toString();

    List<Evaluacion> result = new LinkedList<>();
    result.add(generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1), convocatoriaReunionId));
    result.add(generarMockEvaluacion(Long.valueOf(3), String.format("%03d", 3), convocatoriaReunionId));
    result.add(generarMockEvaluacion(Long.valueOf(5), String.format("%03d", 5), convocatoriaReunionId));

    // página 1 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-EVC-V")));
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    Pageable pageable = PageRequest.of(1, 2);
    Page<Evaluacion> pageResult = new PageImpl<>(result.subList(2, 3), pageable, result.size());

    // when: Se buscan los datos paginados
    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(url, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
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
   * Genera un objeto {@link ConvocatoriaReunion}
   *
   * @param id
   * @param comiteId
   * @param tipoId
   * @return ConvocatoriaReunion
   */
  private ConvocatoriaReunion getMockData(Long id, Long comiteId, Long tipoId) {

    Comite comite = new Comite(comiteId, "Comite" + comiteId, Boolean.TRUE);

    String tipo_txt = (tipoId == 1L) ? "Ordinaria" : (tipoId == 2L) ? "Extraordinaria" : "Seguimiento";
    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(tipoId, tipo_txt, Boolean.TRUE);

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final ConvocatoriaReunion data = new ConvocatoriaReunion();
    data.setId(id);
    data.setComite(comite);
    data.setFechaEvaluacion(LocalDateTime.of(2020, 7, id.intValue(), 0, 0, 0));
    data.setFechaLimite(LocalDate.of(2020, 8, id.intValue()));
    data.setLugar("Lugar " + txt);
    data.setOrdenDia("Orden del día convocatoria reunión " + txt);
    data.setAnio(2020);
    data.setNumeroActa(id);
    data.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    data.setHoraInicio(7 + id.intValue());
    data.setMinutoInicio(30);
    data.setFechaEnvio(LocalDate.of(2020, 7, 13));
    data.setActivo(Boolean.TRUE);

    return data;
  }

  /**
   * Función que devuelve un objeto Asistentes
   *
   * @param id                    id del asistentes
   * @param evaluadorId           id del evaluador
   * @param convocatoriaReunionId id de la convocatoria
   * @return el objeto Asistentes
   */

  private Asistentes generarMockAsistentes(Long id, Long evaluadorId, Long convocatoriaReunionId) {

    Asistentes asistentes = new Asistentes();
    asistentes.setId(id);
    asistentes.setEvaluador(generarMockEvaluador(evaluadorId));
    asistentes.setConvocatoriaReunion(getMockData(convocatoriaReunionId, convocatoriaReunionId, 1L));
    asistentes.setMotivo("Motivo" + id);
    asistentes.setAsistencia(Boolean.TRUE);

    return asistentes;
  }

  /**
   * Función que devuelve un objeto Evaluador
   *
   * @param id id del Evaluador
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
    evaluador.setResumen("Evaluador" + id);
    evaluador.setPersonaRef("user-00" + id);
    evaluador.setActivo(Boolean.TRUE);

    return evaluador;
  }

  /**
   * Función que devuelve un objeto Evaluacion
   *
   * @param id     id del Evaluacion
   * @param sufijo el sufijo para título y nombre
   * @return el objeto Evaluacion
   */

  public Evaluacion generarMockEvaluacion(Long id, String sufijo) {

    Dictamen dictamen = new Dictamen();
    dictamen.setId(1L);
    dictamen.setNombre("Dictamen1");
    dictamen.setActivo(Boolean.TRUE);

    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(1L);
    peticionEvaluacion.setCodigo("Codigo1");
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico1");
    peticionEvaluacion.setExterno(Boolean.FALSE);
    peticionEvaluacion.setFechaFin(LocalDate.of(2020, 8, 1));
    peticionEvaluacion.setFechaInicio(LocalDate.of(2020, 8, 1));
    peticionEvaluacion.setFuenteFinanciacion("Fuente financiación");
    peticionEvaluacion.setObjetivos("Objetivos1");
    peticionEvaluacion.setOtroValorSocial("Otro valor social1");
    peticionEvaluacion.setResumen("Resumen");
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo("PeticionEvaluacion1");
    peticionEvaluacion.setPersonaRef("user-001");
    peticionEvaluacion.setValorSocial(3);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(1L);
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    Memoria memoria = new Memoria(1L, "numRef-001", peticionEvaluacion, comite, "Memoria001", "user-001", tipoMemoria,
        new TipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), LocalDate.of(2020, 8, 1), Boolean.FALSE,
        new Retrospectiva(1L, new EstadoRetrospectiva(1L, "Pendiente", Boolean.TRUE), LocalDate.of(2020, 8, 1)), 3,
        Boolean.TRUE);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setNombre("TipoEvaluacion1");
    tipoEvaluacion.setActivo(Boolean.TRUE);

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    evaluacion.setDictamen(dictamen);
    evaluacion.setEsRevMinima(Boolean.TRUE);
    evaluacion.setFechaDictamen(LocalDate.of(2020, 8, 1));
    evaluacion.setMemoria(memoria);
    evaluacion.setConvocatoriaReunion(getMockData(1L, 1L, 1L));
    evaluacion.setTipoEvaluacion(tipoEvaluacion);
    evaluacion.setEvaluador1(generarMockEvaluador(1L));
    evaluacion.setEvaluador2(generarMockEvaluador(2L));
    evaluacion.setVersion(2);
    evaluacion.setActivo(Boolean.TRUE);

    return evaluacion;
  }

  /**
   * Función que devuelve un objeto Evaluacion
   *
   * @param id                    id del Evaluacion
   * @param sufijo                el sufijo para título y nombre
   * @param convocatoriaReunionId el sufijo para título y nombre
   * @return el objeto Evaluacion
   */

  public Evaluacion generarMockEvaluacion(Long id, String sufijo, Long convocatoriaReunionId) {

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setNombre("TipoEvaluacion1");
    tipoEvaluacion.setActivo(Boolean.TRUE);

    Dictamen dictamen = new Dictamen();
    dictamen.setId(1L);
    dictamen.setNombre("Dictamen1");
    dictamen.setTipoEvaluacion(tipoEvaluacion);
    dictamen.setActivo(Boolean.TRUE);

    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(1L);
    peticionEvaluacion.setCodigo("Codigo1");
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico1");
    peticionEvaluacion.setExterno(Boolean.FALSE);
    peticionEvaluacion.setFechaFin(LocalDate.of(2020, 8, 1));
    peticionEvaluacion.setFechaInicio(LocalDate.of(2020, 8, 1));
    peticionEvaluacion.setFuenteFinanciacion("Fuente financiación");
    peticionEvaluacion.setObjetivos("Objetivos1");
    peticionEvaluacion.setOtroValorSocial("Otro valor social1");
    peticionEvaluacion.setResumen("Resumen");
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo("PeticionEvaluacion1");
    peticionEvaluacion.setPersonaRef("user-001");
    peticionEvaluacion.setValorSocial(3);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(1L);
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    Memoria memoria = new Memoria(1L, "numRef-001", peticionEvaluacion, comite, "Memoria001", "user-001", tipoMemoria,
        new TipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), LocalDate.of(2020, 8, 1), Boolean.FALSE,
        new Retrospectiva(1L, new EstadoRetrospectiva(1L, "Pendiente", Boolean.TRUE), LocalDate.of(2020, 8, 1)), 3,
        Boolean.TRUE);

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);

    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(convocatoriaReunionId);
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(LocalDateTime.of(2020, 8, 1, 10, 10, 10));
    convocatoriaReunion.setFechaLimite(LocalDate.of(2020, 8, 1));
    convocatoriaReunion.setLugar("Lugar");
    convocatoriaReunion.setOrdenDia("Orden del día convocatoria reunión");
    convocatoriaReunion.setAnio(2020);
    convocatoriaReunion.setNumeroActa(100L);
    convocatoriaReunion.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    convocatoriaReunion.setHoraInicio(7);
    convocatoriaReunion.setMinutoInicio(30);
    convocatoriaReunion.setFechaEnvio(LocalDate.of(2020, 8, 1));
    convocatoriaReunion.setActivo(Boolean.TRUE);

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    evaluacion.setDictamen(dictamen);
    evaluacion.setEsRevMinima(Boolean.TRUE);
    evaluacion.setFechaDictamen(LocalDate.of(2020, 8, 1));
    evaluacion.setMemoria(memoria);
    evaluacion.setConvocatoriaReunion(convocatoriaReunion);
    evaluacion.setEvaluador1(generarMockEvaluador(1L));
    evaluacion.setEvaluador2(generarMockEvaluador(2L));
    evaluacion.setVersion(2);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);
    evaluacion.setActivo(Boolean.TRUE);

    return evaluacion;
  }
}
