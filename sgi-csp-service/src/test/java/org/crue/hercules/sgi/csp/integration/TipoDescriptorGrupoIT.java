package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.TipoDescriptorGrupoController;
import org.crue.hercules.sgi.csp.dto.TipoDescriptorGrupoInput;
import org.crue.hercules.sgi.csp.dto.TipoDescriptorGrupoOutput;
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
 * Test de integracion de TipoDescriptorGrupo.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TipoDescriptorGrupoIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = TipoDescriptorGrupoController.REQUEST_MAPPING;
  private static final String PATH_ID = TipoDescriptorGrupoController.PATH_ID;
  private static final String PATH_TODOS = TipoDescriptorGrupoController.PATH_TODOS;
  private static final String PATH_REACTIVAR = TipoDescriptorGrupoController.PATH_REACTIVAR;
  private static final String PATH_DESACTIVAR = TipoDescriptorGrupoController.PATH_DESACTIVAR;

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    return new HttpEntity<>(entity, headers);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsTipoDescriptorGrupo() throws Exception {
    // given: nuevo TipoDescriptorGrupo
    TipoDescriptorGrupoInput toCreate = buildMockTipoDescriptorGrupo();
    String roles = "CSP-TDESG-C";

    // when: create
    final ResponseEntity<TipoDescriptorGrupoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, toCreate, roles), TipoDescriptorGrupoOutput.class);

    // then: 201 Created y entidad persistida con activo a true por defecto
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    TipoDescriptorGrupoOutput created = response.getBody();
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).as("getId()").isNotNull();
    Assertions.assertThat(I18nHelper.getValueForLanguage(created.getNombre(), Language.ES)).as("getNombre()")
        .isEqualTo("Nuevo descriptor");
    Assertions.assertThat(created.getActivo()).as("getActivo()").isTrue();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      "classpath:scripts/tipo-descriptor-grupo.sql"
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsTipoDescriptorGrupo() throws Exception {
    // given: TipoDescriptorGrupo existente y nuevo nombre
    Long id = 1L;
    TipoDescriptorGrupoInput toUpdate = TipoDescriptorGrupoInput.builder()
        .nombre(List.of(new I18nFieldValueDto(Language.ES, "Oferta cientifica actualizada")))
        .build();
    String roles = "CSP-TDESG-E";

    // when: update
    final ResponseEntity<TipoDescriptorGrupoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_ID,
        HttpMethod.PUT, buildRequest(null, toUpdate, roles), TipoDescriptorGrupoOutput.class, id);

    // then: 200 OK con el nombre actualizado
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoDescriptorGrupoOutput updated = response.getBody();
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(I18nHelper.getValueForLanguage(updated.getNombre(), Language.ES)).as("getNombre()")
        .isEqualTo("Oferta cientifica actualizada");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      "classpath:scripts/tipo-descriptor-grupo.sql"
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsTipoDescriptorGrupo() throws Exception {
    // given: TipoDescriptorGrupo existente
    Long id = 1L;
    String roles = "CSP-TDESG-V";

    // when: findById
    final ResponseEntity<TipoDescriptorGrupoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_ID,
        HttpMethod.GET, buildRequest(null, null, roles), TipoDescriptorGrupoOutput.class, id);

    // then: 200 OK con la entidad
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoDescriptorGrupoOutput tipoDescriptorGrupo = response.getBody();
    Assertions.assertThat(tipoDescriptorGrupo).isNotNull();
    Assertions.assertThat(tipoDescriptorGrupo.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoDescriptorGrupo.getNombre(), Language.ES))
        .as("getNombre()").isEqualTo("Oferta cientifica");
    Assertions.assertThat(tipoDescriptorGrupo.getActivo()).as("getActivo()").isTrue();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      "classpath:scripts/tipo-descriptor-grupo.sql"
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void desactivar_ReturnsTipoDescriptorGrupo() throws Exception {
    // given: TipoDescriptorGrupo activo
    Long id = 1L;
    String roles = "CSP-TDESG-B";

    // when: desactivar
    final ResponseEntity<TipoDescriptorGrupoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_DESACTIVAR,
        HttpMethod.PATCH, buildRequest(null, null, roles), TipoDescriptorGrupoOutput.class, id);

    // then: 200 OK y activo a false
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoDescriptorGrupoOutput tipoDescriptorGrupo = response.getBody();
    Assertions.assertThat(tipoDescriptorGrupo).isNotNull();
    Assertions.assertThat(tipoDescriptorGrupo.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(tipoDescriptorGrupo.getActivo()).as("getActivo()").isFalse();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      "classpath:scripts/tipo-descriptor-grupo.sql"
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void reactivar_ReturnsTipoDescriptorGrupo() throws Exception {
    // given: TipoDescriptorGrupo inactivo
    Long id = 4L;
    String roles = "CSP-TDESG-R";

    // when: reactivar
    final ResponseEntity<TipoDescriptorGrupoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_REACTIVAR,
        HttpMethod.PATCH, buildRequest(null, null, roles), TipoDescriptorGrupoOutput.class, id);

    // then: 200 OK y activo a true
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoDescriptorGrupoOutput tipoDescriptorGrupo = response.getBody();
    Assertions.assertThat(tipoDescriptorGrupo).isNotNull();
    Assertions.assertThat(tipoDescriptorGrupo.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(tipoDescriptorGrupo.getActivo()).as("getActivo()").isTrue();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      "classpath:scripts/tipo-descriptor-grupo.sql"
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_OnlyActivos_ReturnsTipoDescriptorGrupoSubList() throws Exception {
    // given: 4 tipos en BD (3 activos, 1 inactivo)
    String[] roles = { "CSP-TDESG-V" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre.value,asc";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH)
        .queryParam("s", sort)
        .build(false).toUri();

    // when: findAll
    final ResponseEntity<List<TipoDescriptorGrupoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoDescriptorGrupoOutput>>() {
        });

    // then: solo se devuelven los activos
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoDescriptorGrupoOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
    Assertions.assertThat(responseData).allMatch(TipoDescriptorGrupoOutput::getActivo);
  }

  @Test
  void findAll_ReturnsStatusCode204() throws Exception {
    // given: BD vacia
    String[] roles = { "CSP-TDESG-V" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).build(false).toUri();

    // when: findAll
    final ResponseEntity<List<TipoDescriptorGrupoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoDescriptorGrupoOutput>>() {
        });

    // then: 204 No Content
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      "classpath:scripts/tipo-descriptor-grupo.sql"
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllTodos_ReturnsTipoDescriptorGrupoSubList() throws Exception {
    // given: 4 tipos en BD (3 activos, 1 inactivo)
    String[] roles = { "CSP-TDESG-V" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre.value,asc";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TODOS)
        .queryParam("s", sort)
        .build(false).toUri();

    // when: findAllTodos
    final ResponseEntity<List<TipoDescriptorGrupoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoDescriptorGrupoOutput>>() {
        });

    // then: devuelve activos e inactivos
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoDescriptorGrupoOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(4);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("4");
  }

  @Test
  void findAllTodos_ReturnsStatusCode204() throws Exception {
    // given: BD vacia
    String[] roles = { "CSP-TDESG-V" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TODOS).build(false).toUri();

    // when: findAllTodos
    final ResponseEntity<List<TipoDescriptorGrupoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoDescriptorGrupoOutput>>() {
        });

    // then: 204 No Content
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  private TipoDescriptorGrupoInput buildMockTipoDescriptorGrupo() {
    return TipoDescriptorGrupoInput.builder()
        .nombre(List.of(new I18nFieldValueDto(Language.ES, "Nuevo descriptor")))
        .build();
  }

}
