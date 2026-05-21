package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.GrupoController;
import org.crue.hercules.sgi.csp.controller.GrupoRelacionInstitucionalController;
import org.crue.hercules.sgi.csp.dto.GrupoRelacionInstitucionalInput;
import org.crue.hercules.sgi.csp.dto.GrupoRelacionInstitucionalOutput;
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
 * Test de integracion de GrupoRelacionInstitucional.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GrupoRelacionInstitucionalIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = GrupoRelacionInstitucionalController.REQUEST_MAPPING;
  private static final String PATH_ID = GrupoRelacionInstitucionalController.PATH_ID;
  private static final String GRUPO_RELACIONES_PATH = GrupoController.REQUEST_MAPPING
      + GrupoController.PATH_GRUPO_RELACION_INSTITUCIONAL;

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set(
        "Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    return new HttpEntity<>(entity, headers);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/tipo-grupo.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/grupo_relacion_institucional.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsGrupoRelacionInstitucionalOutput() throws Exception {
    // given: GrupoRelacionInstitucional existente con entidadRef
    String roles = "CSP-GIN-V";
    Long id = 1L;

    // when: findById
    final ResponseEntity<GrupoRelacionInstitucionalOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.GET,
        buildRequest(null, null, roles), GrupoRelacionInstitucionalOutput.class, id);

    // then: 200 OK con los datos esperados
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    GrupoRelacionInstitucionalOutput body = response.getBody();
    Assertions.assertThat(body).isNotNull();
    Assertions.assertThat(body.getId()).isEqualTo(id);
    Assertions.assertThat(body.getGrupoId()).isEqualTo(1L);
    Assertions.assertThat(body.getEntidadRef()).isEqualTo("ENT-001");
    Assertions.assertThat(body.getInstitucion()).isNull();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/tipo-grupo.sql",
      "classpath:scripts/grupo.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_WithEntidadRef_ReturnsGrupoRelacionInstitucionalOutput() throws Exception {
    // given: input con entidadRef
    String roles = "CSP-GIN-E";

    GrupoRelacionInstitucionalInput input = GrupoRelacionInstitucionalInput.builder()
        .grupoId(1L)
        .entidadRef("ENT-NEW")
        .build();

    // when: create
    final ResponseEntity<GrupoRelacionInstitucionalOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, input, roles), GrupoRelacionInstitucionalOutput.class);

    // then: 201 Created con el GrupoRelacionInstitucional persistido
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    GrupoRelacionInstitucionalOutput body = response.getBody();
    Assertions.assertThat(body).isNotNull();
    Assertions.assertThat(body.getId()).isNotNull();
    Assertions.assertThat(body.getGrupoId()).isEqualTo(1L);
    Assertions.assertThat(body.getEntidadRef()).isEqualTo("ENT-NEW");
    Assertions.assertThat(body.getInstitucion()).isNull();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/tipo-grupo.sql",
      "classpath:scripts/grupo.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_WithInstitucion_ReturnsGrupoRelacionInstitucionalOutput() throws Exception {
    // given: input con institucion manual
    String roles = "CSP-GIN-E";

    GrupoRelacionInstitucionalInput input = GrupoRelacionInstitucionalInput.builder()
        .grupoId(1L)
        .institucion("Universidad Manual")
        .build();

    // when: create
    final ResponseEntity<GrupoRelacionInstitucionalOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, input, roles), GrupoRelacionInstitucionalOutput.class);

    // then: 201 Created con institucion
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    GrupoRelacionInstitucionalOutput body = response.getBody();
    Assertions.assertThat(body).isNotNull();
    Assertions.assertThat(body.getInstitucion()).isEqualTo("Universidad Manual");
    Assertions.assertThat(body.getEntidadRef()).isNull();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/tipo-grupo.sql",
      "classpath:scripts/grupo.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_WithBothEntidadRefAndInstitucion_ReturnsBadRequest() throws Exception {
    // given: input con ambos campos informados
    String roles = "CSP-GIN-E";

    GrupoRelacionInstitucionalInput input = GrupoRelacionInstitucionalInput.builder()
        .grupoId(1L)
        .entidadRef("ENT-001")
        .institucion("Otra")
        .build();

    // when: create
    final ResponseEntity<String> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, input, roles), String.class);

    // then: 400 Bad Request por la validación XOR
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/tipo-grupo.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/grupo_relacion_institucional.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ChangeFromEntidadRefToInstitucion_ReturnsUpdatedOutput() throws Exception {
    // given: GrupoRelacionInstitucional existente (entidadRef) y update a
    // institucion
    String roles = "CSP-GIN-E";
    Long id = 1L;
    GrupoRelacionInstitucionalInput input = GrupoRelacionInstitucionalInput.builder()
        .grupoId(1L)
        .institucion("Universidad Actualizada")
        .build();

    // when: update
    final ResponseEntity<GrupoRelacionInstitucionalOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.PUT,
        buildRequest(null, input, roles), GrupoRelacionInstitucionalOutput.class, id);

    // then: 200 OK con institucion actualizada
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    GrupoRelacionInstitucionalOutput body = response.getBody();
    Assertions.assertThat(body).isNotNull();
    Assertions.assertThat(body.getId()).isEqualTo(id);
    Assertions.assertThat(body.getEntidadRef()).isNull();
    Assertions.assertThat(body.getInstitucion()).isEqualTo("Universidad Actualizada");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/tipo-grupo.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/grupo_relacion_institucional.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_ReturnsNoContent() throws Exception {
    // given: GrupoRelacionInstitucional existente
    String roles = "CSP-GIN-E";
    Long id = 1L;

    // when: deleteById
    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.DELETE,
        buildRequest(null, null, roles), Void.class, id);

    // then: 204 No Content
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/tipo-grupo.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/grupo_relacion_institucional.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllByGrupo_WithPaging_ReturnsList() throws Exception {
    // given: grupo 1 tiene 2 relaciones, grupo 2 tiene 1
    String roles = "CSP-GIN-V";
    Long grupoId = 1L;

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");

    URI uri = UriComponentsBuilder
        .fromUriString(GRUPO_RELACIONES_PATH)
        .queryParam("s", "id,desc")
        .buildAndExpand(grupoId).toUri();

    // when: findAll
    final ResponseEntity<List<GrupoRelacionInstitucionalOutput>> response = restTemplate.exchange(
        uri, HttpMethod.GET,
        buildRequest(headers, null, roles),
        new ParameterizedTypeReference<List<GrupoRelacionInstitucionalOutput>>() {
        });

    // then: 200 OK con las relaciones del grupo
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull().hasSize(2);
    Assertions.assertThat(response.getBody().get(0).getGrupoId()).isEqualTo(grupoId);
  }

  @Test
  void findById_WithoutAuthorization_Returns401() {
    // given: petición sin token
    Long id = 1L;

    // when: findById
    final ResponseEntity<String> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.GET,
        new HttpEntity<>(new HttpHeaders()), String.class, id);

    // then: 401 Unauthorized
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

}
