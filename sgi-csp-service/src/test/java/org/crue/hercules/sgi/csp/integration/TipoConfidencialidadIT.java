package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.TipoConfidencialidadController;
import org.crue.hercules.sgi.csp.dto.TipoConfidencialidadInput;
import org.crue.hercules.sgi.csp.dto.TipoConfidencialidadOutput;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
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
 * Test de integracion de TipoConfidencialidad.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TipoConfidencialidadIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = TipoConfidencialidadController.REQUEST_MAPPING;
  private static final String PATH_ID = TipoConfidencialidadController.PATH_ID;
  private static final String PATH_TODOS = TipoConfidencialidadController.PATH_TODOS;
  private static final String PATH_REACTIVAR = TipoConfidencialidadController.PATH_REACTIVAR;
  private static final String PATH_DESACTIVAR = TipoConfidencialidadController.PATH_DESACTIVAR;

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    return new HttpEntity<>(entity, headers);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsTipoConfidencialidad() throws Exception {
    // given: nuevo TipoConfidencialidad
    TipoConfidencialidadInput toCreate = buildMockTipoConfidencialidad();
    String roles = "CSP-TCONF-C";

    // when: create
    final ResponseEntity<TipoConfidencialidadOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, toCreate, roles), TipoConfidencialidadOutput.class);

    // then: 201 Created y entidad persistida con activo a true por defecto
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    TipoConfidencialidadOutput created = response.getBody();
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).as("getId()").isNotNull();
    Assertions.assertThat(I18nHelper.getValueForLanguage(created.getNombre(), Language.ES)).as("getNombre()")
        .isEqualTo("Reservado");
    Assertions.assertThat(created.getActivo()).as("getActivo()").isTrue();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      "classpath:scripts/tipo-confidencialidad.sql"
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsTipoConfidencialidad() throws Exception {
    // given: TipoConfidencialidad existente y nuevo nombre
    Long id = 1L;
    TipoConfidencialidadInput toUpdate = TipoConfidencialidadInput.builder()
        .nombre(List.of(new I18nFieldValueDto(Language.ES, "Confidencial actualizado")))
        .build();
    String roles = "CSP-TCONF-E";

    // when: update
    final ResponseEntity<TipoConfidencialidadOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_ID,
        HttpMethod.PUT, buildRequest(null, toUpdate, roles), TipoConfidencialidadOutput.class, id);

    // then: 200 OK con el nombre actualizado
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoConfidencialidadOutput updated = response.getBody();
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(I18nHelper.getValueForLanguage(updated.getNombre(), Language.ES)).as("getNombre()")
        .isEqualTo("Confidencial actualizado");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      "classpath:scripts/tipo-confidencialidad.sql"
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void desactivar_ReturnsTipoConfidencialidad() throws Exception {
    // given: TipoConfidencialidad activo
    Long id = 1L;
    String roles = "CSP-TCONF-B";

    // when: desactivar
    final ResponseEntity<TipoConfidencialidadOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_DESACTIVAR,
        HttpMethod.PATCH, buildRequest(null, null, roles), TipoConfidencialidadOutput.class, id);

    // then: 200 OK y activo a false
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoConfidencialidadOutput tipoConfidencialidad = response.getBody();
    Assertions.assertThat(tipoConfidencialidad).isNotNull();
    Assertions.assertThat(tipoConfidencialidad.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(tipoConfidencialidad.getActivo()).as("getActivo()").isFalse();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      "classpath:scripts/tipo-confidencialidad.sql"
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void reactivar_ReturnsTipoConfidencialidad() throws Exception {
    // given: TipoConfidencialidad inactivo
    Long id = 3L;
    String roles = "CSP-TCONF-R";

    // when: reactivar
    final ResponseEntity<TipoConfidencialidadOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_REACTIVAR,
        HttpMethod.PATCH, buildRequest(null, null, roles), TipoConfidencialidadOutput.class, id);

    // then: 200 OK y activo a true
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoConfidencialidadOutput tipoConfidencialidad = response.getBody();
    Assertions.assertThat(tipoConfidencialidad).isNotNull();
    Assertions.assertThat(tipoConfidencialidad.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(tipoConfidencialidad.getActivo()).as("getActivo()").isTrue();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      "classpath:scripts/tipo-confidencialidad.sql"
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_OnlyActivos_ReturnsTipoConfidencialidadSubList() throws Exception {
    // given: 3 tipos en BD (2 activos, 1 inactivo)
    String[] roles = { "CSP-PRO-V" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre.value,asc";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH)
        .queryParam("s", sort)
        .build(false).toUri();

    // when: findAll
    final ResponseEntity<List<TipoConfidencialidadOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoConfidencialidadOutput>>() {
        });

    // then: solo se devuelven los activos
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoConfidencialidadOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(2);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");
    Assertions.assertThat(responseData).allMatch(TipoConfidencialidadOutput::getActivo);
  }

  @Test
  void findAll_ReturnsStatusCode204() throws Exception {
    // given: BD vacia
    String[] roles = { "CSP-PRO-V" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).build(false).toUri();

    // when: findAll
    final ResponseEntity<List<TipoConfidencialidadOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoConfidencialidadOutput>>() {
        });

    // then: 204 No Content
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      "classpath:scripts/tipo-confidencialidad.sql"
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllTodos_ReturnsTipoConfidencialidadSubList() throws Exception {
    // given: 3 tipos en BD (2 activos, 1 inactivo)
    String[] roles = { "CSP-TCONF-V" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre.value,asc";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TODOS)
        .queryParam("s", sort)
        .build(false).toUri();

    // when: findAllTodos
    final ResponseEntity<List<TipoConfidencialidadOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoConfidencialidadOutput>>() {
        });

    // then: devuelve activos e inactivos
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoConfidencialidadOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
  }

  @Test
  void findAllTodos_ReturnsStatusCode204() throws Exception {
    // given: BD vacia
    String[] roles = { "CSP-TCONF-V" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TODOS).build(false).toUri();

    // when: findAllTodos
    final ResponseEntity<List<TipoConfidencialidadOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoConfidencialidadOutput>>() {
        });

    // then: 204 No Content
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  private TipoConfidencialidadInput buildMockTipoConfidencialidad() {
    return TipoConfidencialidadInput.builder()
        .nombre(List.of(new I18nFieldValueDto(Language.ES, "Reservado")))
        .build();
  }

}
