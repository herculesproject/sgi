package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.GrupoDescriptorController;
import org.crue.hercules.sgi.csp.dto.GrupoDescriptorInput;
import org.crue.hercules.sgi.csp.dto.GrupoDescriptorOutput;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
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
 * Test de integracion de GrupoDescriptor.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GrupoDescriptorIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = GrupoDescriptorController.REQUEST_MAPPING;
  private static final String PATH_ID = GrupoDescriptorController.PATH_ID;

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    return new HttpEntity<>(entity, headers);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/tipo-grupo.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/tipo-descriptor-grupo.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsGrupoDescriptor() throws Exception {
    // given: input con grupo y tipoDescriptor existentes y activos
    String roles = "CSP-GIN-E";
    GrupoDescriptorInput input = GrupoDescriptorInput.builder()
        .grupoId(1L)
        .tipoDescriptorGrupoId(1L)
        .texto(List.of(new I18nFieldValueDto(Language.ES, "Descripcion nueva")))
        .build();

    // when: create
    final ResponseEntity<GrupoDescriptorOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, input, roles), GrupoDescriptorOutput.class);

    // then: 201 Created con la entidad persistida
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    GrupoDescriptorOutput created = response.getBody();
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).as("getId()").isNotNull();
    Assertions.assertThat(created.getGrupoId()).as("getGrupoId()").isEqualTo(1L);
    Assertions.assertThat(created.getTipoDescriptorGrupoId()).as("getTipoDescriptorGrupoId()").isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(created.getTexto(), Language.ES)).as("getTexto()")
        .isEqualTo("Descripcion nueva");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/tipo-grupo.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/tipo-descriptor-grupo.sql",
      "classpath:scripts/grupo_descriptor.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsGrupoDescriptor() throws Exception {
    // given: GrupoDescriptor existente y update cambiando tipo y texto
    String roles = "CSP-GIN-E";
    Long id = 1L;
    GrupoDescriptorInput input = GrupoDescriptorInput.builder()
        .id(id)
        .grupoId(1L)
        .tipoDescriptorGrupoId(2L)
        .texto(List.of(new I18nFieldValueDto(Language.ES, "Descripcion actualizada")))
        .build();

    // when: update
    final ResponseEntity<GrupoDescriptorOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_ID,
        HttpMethod.PUT, buildRequest(null, input, roles), GrupoDescriptorOutput.class, id);

    // then: 200 OK con tipo y texto actualizados
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    GrupoDescriptorOutput updated = response.getBody();
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(updated.getTipoDescriptorGrupoId()).as("getTipoDescriptorGrupoId()").isEqualTo(2L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(updated.getTexto(), Language.ES)).as("getTexto()")
        .isEqualTo("Descripcion actualizada");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/tipo-grupo.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/tipo-descriptor-grupo.sql",
      "classpath:scripts/grupo_descriptor.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsGrupoDescriptor() throws Exception {
    // given: GrupoDescriptor existente
    String roles = "CSP-GIN-V";
    Long id = 1L;

    // when: findById
    final ResponseEntity<GrupoDescriptorOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_ID,
        HttpMethod.GET, buildRequest(null, null, roles), GrupoDescriptorOutput.class, id);

    // then: 200 OK con la entidad y su tipoDescriptorGrupo
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    GrupoDescriptorOutput body = response.getBody();
    Assertions.assertThat(body).isNotNull();
    Assertions.assertThat(body.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(body.getGrupoId()).as("getGrupoId()").isEqualTo(1L);
    Assertions.assertThat(body.getTipoDescriptorGrupoId()).as("getTipoDescriptorGrupoId()").isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(body.getTexto(), Language.ES)).as("getTexto()")
        .isEqualTo("Descripcion oferta cientifica grupo 1");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/tipo-grupo.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/tipo-descriptor-grupo.sql",
      "classpath:scripts/grupo_descriptor.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_ReturnsNoContent() throws Exception {
    // given: GrupoDescriptor existente
    String roles = "CSP-GIN-E";
    Long id = 1L;

    // when: deleteById
    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.DELETE,
        buildRequest(null, null, roles), Void.class, id);

    // then: 204 No Content
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

}
