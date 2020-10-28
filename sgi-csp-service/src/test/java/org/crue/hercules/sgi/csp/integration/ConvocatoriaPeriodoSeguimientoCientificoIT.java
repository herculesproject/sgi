package org.crue.hercules.sgi.csp.integration;

import java.time.LocalDate;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer.TokenBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de ConvocatoriaPeriodoSeguimientoCientifico.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })

public class ConvocatoriaPeriodoSeguimientoCientificoIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaperiodoseguimientocientificos";

  private HttpEntity<ConvocatoriaPeriodoSeguimientoCientifico> buildRequest(HttpHeaders headers,
      ConvocatoriaPeriodoSeguimientoCientifico entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "CSP-CPSCI-B", "CSP-CPSCI-C", "CSP-CPSCI-E", "CSP-CPSCI-V")));

    HttpEntity<ConvocatoriaPeriodoSeguimientoCientifico> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsConvocatoriaPeriodoSeguimientoCientifico() throws Exception {

    // given: new ConvocatoriaPeriodoSeguimientoCientifico
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(12).build();
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico//
        .builder()//
        .convocatoria(convocatoria)//
        .mesInicial(1)//
        .mesFinal(2)//
        .fechaInicio(LocalDate.of(2020, 1, 1))//
        .fechaFin(LocalDate.of(2020, 2, 1))//
        .observaciones("observaciones")//
        .build();

    // when: create ConvocatoriaPeriodoSeguimientoCientifico
    final ResponseEntity<ConvocatoriaPeriodoSeguimientoCientifico> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH, HttpMethod.POST, buildRequest(null, convocatoriaPeriodoSeguimientoCientifico),
        ConvocatoriaPeriodoSeguimientoCientifico.class);

    // then: new ConvocatoriaPeriodoSeguimientoCientifico is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ConvocatoriaPeriodoSeguimientoCientifico responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getConvocatoria().getId());
    Assertions.assertThat(responseData.getNumPeriodo()).as("getNumPeriodo()").isEqualTo(1);
    Assertions.assertThat(responseData.getMesInicial()).as("getMesInicial()")
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getMesInicial());
    Assertions.assertThat(responseData.getMesFinal()).as("getMesFinal()")
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getMesFinal());
    Assertions.assertThat(responseData.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getFechaInicio());
    Assertions.assertThat(responseData.getFechaFin()).as("getFechaFin()")
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getFechaFin());
    Assertions.assertThat(responseData.getObservaciones()).as("getObservaciones()")
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getObservaciones());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsConvocatoriaPeriodoSeguimientoCientifico() throws Exception {
    // given: existing ConvocatoriaPeriodoSeguimientoCientifico to be updated
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(12).build();
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico//
        .builder()//
        .id(1L)//
        .convocatoria(convocatoria)//
        .numPeriodo(1)//
        .mesInicial(10)//
        .mesFinal(11)//
        .fechaInicio(LocalDate.of(2020, 10, 1))//
        .fechaFin(LocalDate.of(2020, 11, 1))//
        .observaciones("observaciones")//
        .build();

    // when: update ConvocatoriaPeriodoSeguimientoCientifico
    final ResponseEntity<ConvocatoriaPeriodoSeguimientoCientifico> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, convocatoriaPeriodoSeguimientoCientifico), ConvocatoriaPeriodoSeguimientoCientifico.class,
        convocatoriaPeriodoSeguimientoCientifico.getId());

    // then: ConvocatoriaPeriodoSeguimientoCientifico is updated
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ConvocatoriaPeriodoSeguimientoCientifico responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()")
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getId());
    Assertions.assertThat(responseData.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getConvocatoria().getId());
    Assertions.assertThat(responseData.getNumPeriodo()).as("getNumPeriodo()").isEqualTo(4);
    Assertions.assertThat(responseData.getMesInicial()).as("getMesInicial()")
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getMesInicial());
    Assertions.assertThat(responseData.getMesFinal()).as("getMesFinal()")
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getMesFinal());
    Assertions.assertThat(responseData.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getFechaInicio());
    Assertions.assertThat(responseData.getFechaFin()).as("getFechaFin()")
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getFechaFin());
    Assertions.assertThat(responseData.getObservaciones()).as("getObservaciones()")
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getObservaciones());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    // given: existing ConvocatoriaPeriodoSeguimientoCientifico to delete
    Long id = 1L;

    // when: disable ConvocatoriaPeriodoSeguimientoCientifico
    final ResponseEntity<ConvocatoriaPeriodoSeguimientoCientifico> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        ConvocatoriaPeriodoSeguimientoCientifico.class, id);

    // then: ConvocatoriaPeriodoSeguimientoCientifico is disabled
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsConvocatoriaPeriodoSeguimientoCientifico() throws Exception {
    Long id = 1L;

    final ResponseEntity<ConvocatoriaPeriodoSeguimientoCientifico> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ConvocatoriaPeriodoSeguimientoCientifico.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ConvocatoriaPeriodoSeguimientoCientifico responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(1);
    Assertions.assertThat(responseData.getConvocatoria().getId()).as("getConvocatoria().getId()").isEqualTo(1);
    Assertions.assertThat(responseData.getNumPeriodo()).as("getNumPeriodo()").isEqualTo(1);
    Assertions.assertThat(responseData.getMesInicial()).as("getMesInicial()").isEqualTo(1);
    Assertions.assertThat(responseData.getMesFinal()).as("getMesFinal()").isEqualTo(2);
    Assertions.assertThat(responseData.getFechaInicio()).as("getFechaInicio()").isEqualTo("2020-01-01");
    Assertions.assertThat(responseData.getFechaFin()).as("getFechaFin()").isEqualTo("2020-02-01");
    Assertions.assertThat(responseData.getObservaciones()).as("getObservaciones()")
        .isEqualTo("observaciones-meses-01-02");
  }
}
