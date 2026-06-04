package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.SolicitudProyectoController;
import org.crue.hercules.sgi.csp.dto.SolicitudProyectoUnidadVinculacionInput;
import org.crue.hercules.sgi.csp.dto.SolicitudProyectoUnidadVinculacionOutput;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto.TipoPresupuesto;
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
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de SolicitudProyecto.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SolicitudProyectoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyecto";
  private static final String PATH_SOLICITUD_PRESUPUESTO = "/solicitudpresupuesto";
  private static final String PATH_SOLICITUD_SOCIO = "/solicitudsocio";
  private static final String PATH_PARAMETER_SOLICITUD_PROYECTO_ID = "/{solicitudProyectoId}";
  private static final String PATH_SOLICITUD_PROYECTO_SOCIOS = "/solicitudproyectosocios";
  private static final String PATH_PERIODOS_PAGO = "/periodospago";
  private static final String PATH_PERIODOS_JUSTIFICACION = "/periodosjustificacion";
  private static final String PATH_COORDINADOR = "/coordinador";
  private static final String PATH_UNIDADES_VINCULACION = SolicitudProyectoController.PATH_UNIDADES_VINCULACION;

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    return new HttpEntity<>(entity, headers);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsSolicitudProyecto() throws Exception {
    String[] roles = { "CSP-SOL-E" };

    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(1L);

    final ResponseEntity<SolicitudProyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, solicitudProyecto, roles), SolicitudProyecto.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    SolicitudProyecto solicitudProyectoCreado = response.getBody();
    Assertions.assertThat(solicitudProyectoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(solicitudProyectoCreado.getColaborativo()).as("getColaborativo()")
        .isEqualTo(solicitudProyecto.getColaborativo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsSolicitudProyecto() throws Exception {
    String[] roles = { "CSP-SOL-E" };

    Long idSolicitudProyecto = 1L;
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(1L);

    final ResponseEntity<SolicitudProyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, solicitudProyecto, roles), SolicitudProyecto.class, idSolicitudProyecto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SolicitudProyecto solicitudProyectoActualizado = response.getBody();
    Assertions.assertThat(solicitudProyectoActualizado.getId()).as("getId()").isEqualTo(solicitudProyecto.getId());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsSolicitudProyecto() throws Exception {
    String[] roles = { "CSP-PRO-E" };

    Long idSolicitudProyecto = 1L;

    final ResponseEntity<SolicitudProyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null, roles), SolicitudProyecto.class, idSolicitudProyecto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    SolicitudProyecto solicitudProyecto = response.getBody();
    Assertions.assertThat(solicitudProyecto.getId()).as("getId()").isEqualTo(idSolicitudProyecto);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/convocatoria_entidad_financiadora.sql",
    "classpath:scripts/convocatoria_entidad_gestora.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/solicitud_proyecto_entidad_financiadora_ajena.sql",
    "classpath:scripts/solicitud_proyecto_entidad.sql",
    "classpath:scripts/solicitud_proyecto_presupuesto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasSolicitudPresupuesto_ReturnsHttpStatus200() throws Exception {
    String[] roles = { "CSP-SOL-E" };

    Long solicitudProyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_PRESUPUESTO)
        .buildAndExpand(solicitudProyectoId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/rol_socio.sql",
    "classpath:scripts/solicitud_proyecto_socio.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasSolicitudSocio_ReturnsHttpStatus200() throws Exception {
    String[] roles = { "CSP-SOL-E" };

    Long solicitudProyectoId = 2L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_SOLICITUD_SOCIO)
        .buildAndExpand(solicitudProyectoId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/rol_socio.sql",
    "classpath:scripts/solicitud_proyecto_socio.sql",
    "classpath:scripts/solicitud_proyecto_socio_periodo_pago.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasSolicitudProyectoSocioPeriodosPago_ReturnsHttpStatus200() throws Exception {
    String[] roles = { "CSP-SOL-E" };

    Long solicitudProyectoId = 2L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_SOLICITUD_PROYECTO_ID + PATH_SOLICITUD_PROYECTO_SOCIOS
            + PATH_PERIODOS_PAGO)
        .buildAndExpand(solicitudProyectoId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/rol_socio.sql",
    "classpath:scripts/solicitud_proyecto_socio.sql",
    "classpath:scripts/solicitud_proyecto_socio_periodo_justificacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasSolicitudProyectoSocioPeriodosJustificacion_ReturnsHttpStatus200() throws Exception {
    String[] roles = { "CSP-SOL-E" };

    Long solicitudProyectoId = 2L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_SOLICITUD_PROYECTO_ID + PATH_SOLICITUD_PROYECTO_SOCIOS
            + PATH_PERIODOS_JUSTIFICACION)
        .buildAndExpand(solicitudProyectoId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/rol_socio.sql",
    "classpath:scripts/solicitud_proyecto_socio.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void hasAnySolicitudProyectoSocioWithRolCoordinador_ReturnsHttpStatus200() throws Exception {
    String[] roles = { "CSP-SOL-E" };

    Long solicitudProyectoId = 2L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_SOLICITUD_PROYECTO_ID + PATH_SOLICITUD_PROYECTO_SOCIOS
            + PATH_COORDINADOR)
        .buildAndExpand(solicitudProyectoId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.HEAD,
        buildRequest(null, null, roles), Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/solicitud.sql",
      "classpath:scripts/estado_solicitud.sql",
      "classpath:scripts/solicitud_proyecto.sql",
      "classpath:scripts/solicitud_proyecto_unidad_vinculacion.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findUnidadesVinculacion_WithPagingSortingAndFiltering_ReturnsSolicitudProyectoUnidadVinculacionOutputSubList()
      throws Exception {
    // given: un SolicitudProyecto con varias SolicitudProyectoUnidadVinculacion y
    // filtros de paginacion
    String[] roles = { "CSP-SOL-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";
    String filter = "id>=1;id<=3";

    Long solicitudProyectoId = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_UNIDADES_VINCULACION)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(solicitudProyectoId).toUri();

    // when: se buscan las SolicitudProyectoUnidadVinculacion del SolicitudProyecto
    final ResponseEntity<List<SolicitudProyectoUnidadVinculacionOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, roles),
        new ParameterizedTypeReference<List<SolicitudProyectoUnidadVinculacionOutput>>() {
        });

    // then: 200 OK con la sublista paginada y los headers de paginacion
    final String expectedSize = "3";
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo(expectedSize);

    Assertions.assertThat(response.getBody()).isNotNull();

    final List<SolicitudProyectoUnidadVinculacionOutput> responseData = response.getBody().stream()
        .sorted(new Comparator<SolicitudProyectoUnidadVinculacionOutput>() {
          @Override
          public int compare(SolicitudProyectoUnidadVinculacionOutput o1, SolicitudProyectoUnidadVinculacionOutput o2) {
            return o1.getId().compareTo(o2.getId());
          }
        })
        .toList();
    Assertions.assertThat(responseData).hasSize(Integer.valueOf(expectedSize));

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();
    Assertions.assertThat(responseData.get(2)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(2);
    Assertions.assertThat(responseData.get(2).getId()).isEqualTo(3);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/solicitud.sql",
      "classpath:scripts/estado_solicitud.sql",
      "classpath:scripts/solicitud_proyecto.sql",
      "classpath:scripts/solicitud_proyecto_unidad_vinculacion.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void updateUnidadesVinculacion_WithNewRefs_ReplacesAll() throws Exception {
    // given: un SolicitudProyecto con unidades previas y una lista con nuevas
    // referencias
    String[] roles = { "CSP-SOL-E" };

    Long solicitudProyectoId = 1L;
    List<SolicitudProyectoUnidadVinculacionInput> toUpdate = Arrays.asList(
        buildMockSolicitudProyectoUnidadVinculacionInput("updated-01"),
        buildMockSolicitudProyectoUnidadVinculacionInput("updated-02"));

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_UNIDADES_VINCULACION)
        .buildAndExpand(solicitudProyectoId).toUri();

    // when: se actualizan las SolicitudProyectoUnidadVinculacion del
    // SolicitudProyecto con refs distintas
    final ResponseEntity<List<SolicitudProyectoUnidadVinculacionOutput>> response = restTemplate.exchange(uri,
        HttpMethod.PATCH,
        buildRequest(null, toUpdate, roles),
        new ParameterizedTypeReference<List<SolicitudProyectoUnidadVinculacionOutput>>() {
        });

    // then: 200 OK con solo las nuevas referencias
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    final List<SolicitudProyectoUnidadVinculacionOutput> responseData = response.getBody().stream()
        .sorted(Comparator.comparing(SolicitudProyectoUnidadVinculacionOutput::getUnidadVinculacionRef))
        .toList();
    Assertions.assertThat(responseData).hasSize(2);
    Assertions.assertThat(responseData.get(0).getUnidadVinculacionRef()).isEqualTo("updated-01");
    Assertions.assertThat(responseData.get(1).getUnidadVinculacionRef()).isEqualTo("updated-02");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/solicitud.sql",
      "classpath:scripts/estado_solicitud.sql",
      "classpath:scripts/solicitud_proyecto.sql",
      "classpath:scripts/solicitud_proyecto_unidad_vinculacion.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void updateUnidadesVinculacion_WithSameRefs_PreservesIds() throws Exception {
    // given: un SolicitudProyecto con unidades existentes y se envía la misma lista
    String[] roles = { "CSP-SOL-E" };

    Long solicitudProyectoId = 1L;
    // unidad-01..unidad-05 corresponden a IDs 1..5 en el script SQL del grupo 1
    List<SolicitudProyectoUnidadVinculacionInput> toUpdate = Arrays.asList(
        buildMockSolicitudProyectoUnidadVinculacionInput("unidad-01"),
        buildMockSolicitudProyectoUnidadVinculacionInput("unidad-02"),
        buildMockSolicitudProyectoUnidadVinculacionInput("unidad-03"),
        buildMockSolicitudProyectoUnidadVinculacionInput("unidad-04"),
        buildMockSolicitudProyectoUnidadVinculacionInput("unidad-05"));

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_UNIDADES_VINCULACION)
        .buildAndExpand(solicitudProyectoId).toUri();

    // when: se actualizan con la misma lista (ningún cambio real)
    final ResponseEntity<List<SolicitudProyectoUnidadVinculacionOutput>> response = restTemplate.exchange(uri,
        HttpMethod.PATCH,
        buildRequest(null, toUpdate, roles),
        new ParameterizedTypeReference<List<SolicitudProyectoUnidadVinculacionOutput>>() {
        });

    // then: 200 OK con los mismos items preservando sus IDs originales
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    final List<SolicitudProyectoUnidadVinculacionOutput> responseData = response.getBody().stream()
        .sorted(Comparator.comparing(SolicitudProyectoUnidadVinculacionOutput::getId))
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

  private SolicitudProyectoUnidadVinculacionInput buildMockSolicitudProyectoUnidadVinculacionInput(
      String unidadVinculacionRef) {
    return SolicitudProyectoUnidadVinculacionInput.builder()
        .unidadVinculacionRef(unidadVinculacionRef)
        .build();
  }

  /**
   * Función que devuelve un objeto SolicitudProyecto
   * 
   * @param solicitudProyectoId
   * @return el objeto SolicitudProyecto
   */
  private SolicitudProyecto generarSolicitudProyecto(Long solicitudProyectoId) {
    return SolicitudProyecto.builder()
        .id(solicitudProyectoId)
        .acronimo("acronimo-" + solicitudProyectoId).colaborativo(Boolean.TRUE).tipoPresupuesto(TipoPresupuesto.GLOBAL)
        .coordinado(Boolean.TRUE)
        .build();
  }

}
