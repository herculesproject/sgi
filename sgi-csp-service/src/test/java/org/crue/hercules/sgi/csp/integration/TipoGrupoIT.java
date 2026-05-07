package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.TipoGrupoController;
import org.crue.hercules.sgi.csp.dto.TipoGrupoInput;
import org.crue.hercules.sgi.csp.dto.TipoGrupoOutput;
import org.crue.hercules.sgi.csp.model.TipoGrupoNombre;
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
 * Test de integracion de TipoGrupo.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TipoGrupoIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = TipoGrupoController.REQUEST_MAPPING;
  private static final String PATH_ID = TipoGrupoController.PATH_ID;
  private static final String PATH_TODOS = TipoGrupoController.PATH_TODOS;
  private static final String PATH_REACTIVAR = TipoGrupoController.PATH_REACTIVAR;
  private static final String PATH_DESACTIVAR = TipoGrupoController.PATH_DESACTIVAR;

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", roles)));

    return new HttpEntity<>(entity, headers);
  }

  private TipoGrupoInput buildMockTipoGrupo() {
    return TipoGrupoInput.builder()
        .nombre(List.of(new TipoGrupoNombre(Language.ES, "Nuevo tipo")))
        .build();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/tipo-grupo.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsTipoGrupo() throws Exception {
    TipoGrupoInput toCreate = buildMockTipoGrupo();
    String roles = "CSP-TGIN-C";

    final ResponseEntity<TipoGrupoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, toCreate, roles), TipoGrupoOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    TipoGrupoOutput created = response.getBody();
    Assertions.assertThat(created.getId()).as("getId()").isNotNull();
    Assertions.assertThat(I18nHelper.getValueForLanguage(created.getNombre(), Language.ES))
        .as("getNombre()").isEqualTo("Nuevo tipo");
    Assertions.assertThat(created.getActivo()).as("getActivo()").isTrue();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/tipo-grupo.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsTipoGrupo() throws Exception {
    Long id = 1L;
    TipoGrupoInput toUpdate = TipoGrupoInput.builder()
        .nombre(List.of(new TipoGrupoNombre(Language.ES, "Emergente actualizado")))
        .build();
    String roles = "CSP-TGIN-E";

    final ResponseEntity<TipoGrupoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_ID,
        HttpMethod.PUT, buildRequest(null, toUpdate, roles), TipoGrupoOutput.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    TipoGrupoOutput updated = response.getBody();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(I18nHelper.getValueForLanguage(updated.getNombre(), Language.ES))
        .as("getNombre()").isEqualTo("Emergente actualizado");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/tipo-grupo.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsTipoGrupo() throws Exception {
    Long id = 1L;
    String roles = "CSP-TGIN-V";

    final ResponseEntity<TipoGrupoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_ID,
        HttpMethod.GET, buildRequest(null, null, roles), TipoGrupoOutput.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    TipoGrupoOutput tipoGrupo = response.getBody();
    Assertions.assertThat(tipoGrupo.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoGrupo.getNombre(), Language.ES))
        .as("getNombre()").isEqualTo("Emergente");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/tipo-grupo.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void desactivar_ReturnsTipoGrupo() throws Exception {
    Long id = 1L;
    String roles = "CSP-TGIN-B";

    final ResponseEntity<TipoGrupoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_DESACTIVAR,
        HttpMethod.PATCH, buildRequest(null, null, roles), TipoGrupoOutput.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    TipoGrupoOutput tipoGrupo = response.getBody();
    Assertions.assertThat(tipoGrupo.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(tipoGrupo.getActivo()).as("getActivo()").isFalse();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/tipo-grupo.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
      statements = "UPDATE test.tipo_grupo SET activo = false WHERE id = 1")
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void reactivar_ReturnsTipoGrupo() throws Exception {
    Long id = 1L;
    String roles = "CSP-TGIN-R";

    final ResponseEntity<TipoGrupoOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_REACTIVAR,
        HttpMethod.PATCH, buildRequest(null, null, roles), TipoGrupoOutput.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    TipoGrupoOutput tipoGrupo = response.getBody();
    Assertions.assertThat(tipoGrupo.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(tipoGrupo.getActivo()).as("getActivo()").isTrue();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/tipo-grupo.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsTipoGrupoSubList() throws Exception {
    String[] roles = { "CSP-TGIN-V" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre.value,asc";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH)
        .queryParam("s", sort)
        .build(false).toUri();

    final ResponseEntity<List<TipoGrupoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoGrupoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoGrupoOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(4);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("4");
  }

  @Test
  void findAll_ReturnsStatusCode204() throws Exception {
    String[] roles = { "CSP-TGIN-V" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH)
        .build(false).toUri();

    final ResponseEntity<List<TipoGrupoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoGrupoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/tipo-grupo.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllTodos_WithPagingSortingAndFiltering_ReturnsTipoGrupoSubList() throws Exception {
    String[] roles = { "CSP-TGIN-V", "CSP-TGIN-C", "CSP-TGIN-E", "CSP-TGIN-B", "CSP-TGIN-R" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre.value,asc";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TODOS)
        .queryParam("s", sort)
        .build(false).toUri();

    final ResponseEntity<List<TipoGrupoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoGrupoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoGrupoOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(4);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("4");

    Assertions.assertThat(I18nHelper.getValueForLanguage(responseData.get(0).getNombre(), Language.ES))
        .as("get(0).getNombre()").isEqualTo("Consolidado");
    Assertions.assertThat(I18nHelper.getValueForLanguage(responseData.get(1).getNombre(), Language.ES))
        .as("get(1).getNombre()").isEqualTo("Emergente");
    Assertions.assertThat(I18nHelper.getValueForLanguage(responseData.get(2).getNombre(), Language.ES))
        .as("get(2).getNombre()").isEqualTo("Grupo de alto rendimiento");
    Assertions.assertThat(I18nHelper.getValueForLanguage(responseData.get(3).getNombre(), Language.ES))
        .as("get(3).getNombre()").isEqualTo("Precompetitivo");
  }

  @Test
  void findAllTodos_ReturnsStatusCode204() throws Exception {
    String[] roles = { "CSP-TGIN-V", "CSP-TGIN-C", "CSP-TGIN-E", "CSP-TGIN-B", "CSP-TGIN-R" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TODOS)
        .build(false).toUri();

    final ResponseEntity<List<TipoGrupoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<TipoGrupoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

}
