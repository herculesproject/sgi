package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.GrupoEnlaceController;
import org.crue.hercules.sgi.csp.dto.GrupoEnlaceInput;
import org.crue.hercules.sgi.csp.dto.GrupoEnlaceOutput;
import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de GrupoEnlace.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GrupoEnlaceIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = GrupoEnlaceController.REQUEST_MAPPING;
  private static final String PATH_ID = GrupoEnlaceController.PATH_ID;

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
      "classpath:scripts/tipo_enlace.sql",
      "classpath:scripts/grupo_enlace.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsGrupoEnlaceOutputWithTipoEnlace() throws Exception {
    // given: GrupoEnlace existente con tipoEnlace asociado
    String roles = "CSP-GIN-V";
    Long grupoEnlaceId = 1L;

    // when: findById
    final ResponseEntity<GrupoEnlaceOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.GET,
        buildRequest(null, null, roles), GrupoEnlaceOutput.class, grupoEnlaceId);

    // then: 200 OK con el GrupoEnlace y su tipoEnlace
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    GrupoEnlaceOutput body = response.getBody();
    Assertions.assertThat(body).isNotNull();
    Assertions.assertThat(body.getId()).isEqualTo(grupoEnlaceId);
    Assertions.assertThat(body.getEnlace()).isEqualTo("https://www.example");
    Assertions.assertThat(body.getGrupoId()).isEqualTo(1L);
    Assertions.assertThat(body.getTipoEnlaceId()).isEqualTo(1L);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/tipo-grupo.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/tipo_enlace.sql",
      "classpath:scripts/grupo_enlace.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_WithoutTipoEnlace_ReturnsGrupoEnlaceOutput() throws Exception {
    // given: GrupoEnlace existente sin tipoEnlace
    String roles = "CSP-GIN-V";
    Long grupoEnlaceId = 3L;

    // when: findById
    final ResponseEntity<GrupoEnlaceOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.GET,
        buildRequest(null, null, roles), GrupoEnlaceOutput.class, grupoEnlaceId);

    // then: 200 OK con tipoEnlace null
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    GrupoEnlaceOutput body = response.getBody();
    Assertions.assertThat(body).isNotNull();
    Assertions.assertThat(body.getId()).isEqualTo(grupoEnlaceId);
    Assertions.assertThat(body.getTipoEnlaceId()).isNull();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/tipo-grupo.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/tipo_enlace.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsGrupoEnlaceOutput() throws Exception {
    // given: input con tipoEnlaceId existente y activo
    String roles = "CSP-GIN-E";

    GrupoEnlaceInput input = GrupoEnlaceInput.builder()
        .enlace("https://create.example")
        .grupoId(1L)
        .tipoEnlaceId(2L)
        .build();

    // when: create GrupoEnlace
    final ResponseEntity<GrupoEnlaceOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, input, roles), GrupoEnlaceOutput.class);

    // then: 201 Created con el GrupoEnlace persistido y su tipoEnlace
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    GrupoEnlaceOutput body = response.getBody();
    Assertions.assertThat(body).isNotNull();
    Assertions.assertThat(body.getId()).isNotNull();
    Assertions.assertThat(body.getEnlace()).isEqualTo("https://create.example");
    Assertions.assertThat(body.getGrupoId()).isEqualTo(1L);
    Assertions.assertThat(body.getTipoEnlaceId()).isEqualTo(2L);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/tipo-grupo.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/tipo_enlace.sql",
      "classpath:scripts/grupo_enlace.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsGrupoEnlaceOutputWithUpdatedTipoEnlace() throws Exception {
    // given: GrupoEnlace existente y update cambiando enlace y tipoEnlaceId a otro
    // activo
    String roles = "CSP-GIN-E";
    Long grupoEnlaceId = 1L;
    GrupoEnlaceInput input = GrupoEnlaceInput.builder()
        .id(grupoEnlaceId)
        .enlace("https://updated.example")
        .grupoId(1L)
        .tipoEnlaceId(3L)
        .build();

    // when: update GrupoEnlace
    final ResponseEntity<GrupoEnlaceOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.PUT,
        buildRequest(null, input, roles), GrupoEnlaceOutput.class, grupoEnlaceId);

    // then: 200 OK con enlace y tipoEnlace actualizados
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    GrupoEnlaceOutput body = response.getBody();
    Assertions.assertThat(body).isNotNull();
    Assertions.assertThat(body.getId()).isEqualTo(grupoEnlaceId);
    Assertions.assertThat(body.getEnlace()).isEqualTo("https://updated.example");
    Assertions.assertThat(body.getTipoEnlaceId()).isEqualTo(3L);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/tipo-grupo.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/tipo_enlace.sql",
      "classpath:scripts/grupo_enlace.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ClearTipoEnlace_ReturnsGrupoEnlaceOutputWithNullTipoEnlace() throws Exception {
    // given: GrupoEnlace existente y update con tipoEnlaceId null
    String roles = "CSP-GIN-E";
    Long grupoEnlaceId = 1L;
    GrupoEnlaceInput input = GrupoEnlaceInput.builder()
        .id(grupoEnlaceId)
        .enlace("https://www.example")
        .grupoId(1L)
        .tipoEnlaceId(null)
        .build();

    // when: update GrupoEnlace
    final ResponseEntity<GrupoEnlaceOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.PUT,
        buildRequest(null, input, roles), GrupoEnlaceOutput.class, grupoEnlaceId);

    // then: 200 OK con tipoEnlace null
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    GrupoEnlaceOutput body = response.getBody();
    Assertions.assertThat(body).isNotNull();
    Assertions.assertThat(body.getTipoEnlaceId()).isNull();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/tipo-grupo.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/tipo_enlace.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_WithNonExistingTipoEnlace_Returns404() throws Exception {
    // given: input con un tipoEnlaceId que no existe
    String roles = "CSP-GIN-E";
    Long nonExistingTipoEnlaceId = 999L;

    GrupoEnlaceInput input = GrupoEnlaceInput.builder()
        .enlace("https://create.example")
        .grupoId(1L)
        .tipoEnlaceId(nonExistingTipoEnlaceId)
        .build();

    // when: create GrupoEnlace
    final ResponseEntity<String> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, input, roles), String.class);

    // then: 404 Not Found provocado por el TipoEnlace que no existe
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    Assertions.assertThat(response.getBody())
        .contains(NotFoundException.NOT_FOUND_PROBLEM_TYPE.toString())
        .contains(String.valueOf(nonExistingTipoEnlaceId));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/tipo-grupo.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/tipo_enlace.sql",
      "classpath:scripts/grupo_enlace.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_KeepingInactiveTipoEnlace_ReturnsGrupoEnlaceOutput() throws Exception {
    // given: GrupoEnlace existente con tipoEnlace inactivo y update sin cambiar el
    // tipoEnlaceId
    String roles = "CSP-GIN-E";
    Long grupoEnlaceId = 4L;

    GrupoEnlaceInput input = GrupoEnlaceInput.builder()
        .id(grupoEnlaceId)
        .enlace("https://inactive-updated.example")
        .grupoId(1L)
        .tipoEnlaceId(4L)
        .build();

    // when: update GrupoEnlace
    final ResponseEntity<GrupoEnlaceOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.PUT,
        buildRequest(null, input, roles), GrupoEnlaceOutput.class, grupoEnlaceId);

    // then: 200 OK, no se valida que el tipoEnlace esté activo porque no cambia
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    GrupoEnlaceOutput body = response.getBody();
    Assertions.assertThat(body).isNotNull();
    Assertions.assertThat(body.getEnlace()).isEqualTo("https://inactive-updated.example");
    Assertions.assertThat(body.getTipoEnlaceId()).isEqualTo(4L);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/tipo-grupo.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/tipo_enlace.sql",
      "classpath:scripts/grupo_enlace.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ChangingToInactiveTipoEnlace_ReturnsBadRequest() throws Exception {
    // given: update cambiando el tipoEnlaceId a uno inactivo
    String roles = "CSP-GIN-E";
    Long grupoEnlaceId = 1L;
    Long inactiveTipoEnlaceId = 4L;

    GrupoEnlaceInput input = GrupoEnlaceInput.builder()
        .id(grupoEnlaceId)
        .enlace("https://www.example")
        .grupoId(1L)
        .tipoEnlaceId(inactiveTipoEnlaceId)
        .build();

    // when: update GrupoEnlace
    final ResponseEntity<String> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.PUT,
        buildRequest(null, input, roles), String.class, grupoEnlaceId);

    // then: 400 Bad Request provocado por el TipoEnlace inactivo
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(response.getBody()).contains(String.valueOf(inactiveTipoEnlaceId));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/tipo-grupo.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/tipo_enlace.sql",
      "classpath:scripts/grupo_enlace.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_ReturnsNoContent() throws Exception {
    // given: GrupoEnlace existente
    String roles = "CSP-GIN-E";
    Long grupoEnlaceId = 1L;

    // when: deleteById
    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.DELETE,
        buildRequest(null, null, roles), Void.class, grupoEnlaceId);

    // then: 204 No Content
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

}
