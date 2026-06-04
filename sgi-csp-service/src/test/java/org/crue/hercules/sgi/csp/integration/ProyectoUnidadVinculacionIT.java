package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.ProyectoController;
import org.crue.hercules.sgi.csp.dto.ProyectoUnidadVinculacionInput;
import org.crue.hercules.sgi.csp.dto.ProyectoUnidadVinculacionOutput;
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
 * Test de integración para {@link ProyectoController}, apartado unidades de
 * vinculación.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProyectoUnidadVinculacionIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = ProyectoController.REQUEST_MAPPING;
  private static final String PATH_UNIDADES_VINCULACION = ProyectoController.PATH_PROYECTO_UNIDADES_VINCULACION;

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", roles)));
    return new HttpEntity<>(entity, headers);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/proyecto_unidad_vinculacion.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findUnidadesVinculacion_WithPagingSortingAndFiltering_ReturnsSubList() throws Exception {
    // given: un Proyecto con varias ProyectoUnidadVinculacion y filtros de
    // paginacion
    String[] roles = { "CSP-PRO-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String filter = "id>=1;id<=3";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_UNIDADES_VINCULACION)
        .queryParam("s", "id,desc").queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    // when: se buscan las ProyectoUnidadVinculacion del Proyecto
    final ResponseEntity<List<ProyectoUnidadVinculacionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ProyectoUnidadVinculacionOutput>>() {
        });

    // then: 200 OK con la sublista paginada y los headers de paginacion
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(response.getBody()).isNotNull();

    final List<ProyectoUnidadVinculacionOutput> responseData = response.getBody().stream()
        .sorted(Comparator.comparing(ProyectoUnidadVinculacionOutput::getId))
        .toList();
    Assertions.assertThat(responseData).hasSize(3);

    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1L);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(2L);
    Assertions.assertThat(responseData.get(2).getId()).isEqualTo(3L);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/proyecto_unidad_vinculacion.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void updateUnidadesVinculacion_WithNewRefs_ReplacesAll() throws Exception {
    // given: un Proyecto con unidades previas y una lista con nuevas referencias
    String[] roles = { "CSP-PRO-E" };

    Long proyectoId = 1L;
    List<ProyectoUnidadVinculacionInput> toUpdate = Arrays.asList(
        buildMockInput("updated-01"),
        buildMockInput("updated-02"));

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_UNIDADES_VINCULACION)
        .buildAndExpand(proyectoId).toUri();

    // when: se actualizan las ProyectoUnidadVinculacion con refs distintas
    final ResponseEntity<List<ProyectoUnidadVinculacionOutput>> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildRequest(null, toUpdate, roles), new ParameterizedTypeReference<List<ProyectoUnidadVinculacionOutput>>() {
        });

    // then: 200 OK con solo las nuevas referencias
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    final List<ProyectoUnidadVinculacionOutput> responseData = response.getBody().stream()
        .sorted(Comparator.comparing(ProyectoUnidadVinculacionOutput::getUnidadVinculacionRef))
        .toList();
    Assertions.assertThat(responseData).hasSize(2);
    Assertions.assertThat(responseData.get(0).getUnidadVinculacionRef()).isEqualTo("updated-01");
    Assertions.assertThat(responseData.get(1).getUnidadVinculacionRef()).isEqualTo("updated-02");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/proyecto_unidad_vinculacion.sql",
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void updateUnidadesVinculacion_WithSameRefs_PreservesIds() throws Exception {
    // given: un Proyecto con unidades existentes y se envía la misma lista
    String[] roles = { "CSP-PRO-E" };

    Long proyectoId = 1L;
    // unidad-01..unidad-05 corresponden a IDs 1..5 en el script SQL del proyecto 1
    List<ProyectoUnidadVinculacionInput> toUpdate = Arrays.asList(
        buildMockInput("unidad-01"),
        buildMockInput("unidad-02"),
        buildMockInput("unidad-03"),
        buildMockInput("unidad-04"),
        buildMockInput("unidad-05"));

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_UNIDADES_VINCULACION)
        .buildAndExpand(proyectoId).toUri();

    // when: se actualizan con la misma lista (ningún cambio real)
    final ResponseEntity<List<ProyectoUnidadVinculacionOutput>> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildRequest(null, toUpdate, roles), new ParameterizedTypeReference<List<ProyectoUnidadVinculacionOutput>>() {
        });

    // then: 200 OK con los mismos items preservando sus IDs originales
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    final List<ProyectoUnidadVinculacionOutput> responseData = response.getBody().stream()
        .sorted(Comparator.comparing(ProyectoUnidadVinculacionOutput::getId))
        .toList();
    Assertions.assertThat(responseData).hasSize(5);
    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1L);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(2L);
    Assertions.assertThat(responseData.get(2).getId()).isEqualTo(3L);
    Assertions.assertThat(responseData.get(3).getId()).isEqualTo(4L);
    Assertions.assertThat(responseData.get(4).getId()).isEqualTo(5L);
    Assertions.assertThat(responseData.get(0).getUnidadVinculacionRef()).isEqualTo("unidad-01");
    Assertions.assertThat(responseData.get(1).getUnidadVinculacionRef()).isEqualTo("unidad-02");
    Assertions.assertThat(responseData.get(2).getUnidadVinculacionRef()).isEqualTo("unidad-03");
    Assertions.assertThat(responseData.get(3).getUnidadVinculacionRef()).isEqualTo("unidad-04");
    Assertions.assertThat(responseData.get(4).getUnidadVinculacionRef()).isEqualTo("unidad-05");
  }

  private ProyectoUnidadVinculacionInput buildMockInput(String unidadVinculacionRef) {
    return ProyectoUnidadVinculacionInput.builder()
        .unidadVinculacionRef(unidadVinculacionRef)
        .build();
  }

}
