package org.crue.hercules.sgi.csp.integration;

import java.time.LocalDate;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.TipoJustificacionEnum;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
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
 * Test de integracion de ConvocatoriaPeriodoJustificacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class ConvocatoriaPeriodoJustificacionIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaperiodojustificaciones";

  private HttpEntity<ConvocatoriaPeriodoJustificacion> buildRequest(HttpHeaders headers,
      ConvocatoriaPeriodoJustificacion entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CENTGES-C", "CSP-CENTGES-V")));

    HttpEntity<ConvocatoriaPeriodoJustificacion> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsConvocatoriaPeriodoJustificacion() throws Exception {

    // given: new ConvocatoriaPeriodoJustificacion
    ConvocatoriaPeriodoJustificacion newConvocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(
        null);

    // when: create ConvocatoriaPeriodoJustificacion
    final ResponseEntity<ConvocatoriaPeriodoJustificacion> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, newConvocatoriaPeriodoJustificacion),
        ConvocatoriaPeriodoJustificacion.class);

    // then: new ConvocatoriaPeriodoJustificacion is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ConvocatoriaPeriodoJustificacion responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(newConvocatoriaPeriodoJustificacion.getConvocatoria().getId());
    Assertions.assertThat(responseData.getMesInicial()).as("getMesInicial()")
        .isEqualTo(newConvocatoriaPeriodoJustificacion.getMesInicial());
    Assertions.assertThat(responseData.getMesFinal()).as("getMesFinal()")
        .isEqualTo(newConvocatoriaPeriodoJustificacion.getMesFinal());
    Assertions.assertThat(responseData.getFechaInicioPresentacion()).as("getFechaInicioPresentacion()")
        .isEqualTo(newConvocatoriaPeriodoJustificacion.getFechaInicioPresentacion());
    Assertions.assertThat(responseData.getFechaFinPresentacion()).as("getFechaFinPresentacion()")
        .isEqualTo(newConvocatoriaPeriodoJustificacion.getFechaFinPresentacion());
    Assertions.assertThat(responseData.getNumPeriodo()).as("getNumPeriodo()")
        .isEqualTo(newConvocatoriaPeriodoJustificacion.getNumPeriodo());
    Assertions.assertThat(responseData.getObservaciones()).as("getObservaciones()")
        .isEqualTo(newConvocatoriaPeriodoJustificacion.getObservaciones());
    Assertions.assertThat(responseData.getTipoJustificacion()).as("getTipoJustificacion()")
        .isEqualTo(newConvocatoriaPeriodoJustificacion.getTipoJustificacion());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsConvocatoriaPeriodoJustificacion() throws Exception {
    Long idConvocatoriaPeriodoJustificacion = 2L;
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(
        idConvocatoriaPeriodoJustificacion);
    convocatoriaPeriodoJustificacion.getConvocatoria().setId(1L);
    convocatoriaPeriodoJustificacion.setMesInicial(24);
    convocatoriaPeriodoJustificacion.setMesFinal(25);

    final ResponseEntity<ConvocatoriaPeriodoJustificacion> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, convocatoriaPeriodoJustificacion),
        ConvocatoriaPeriodoJustificacion.class, idConvocatoriaPeriodoJustificacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionActualizado = response.getBody();
    Assertions.assertThat(convocatoriaPeriodoJustificacionActualizado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaPeriodoJustificacionActualizado.getConvocatoria().getId())
        .as("getConvocatoria().getId()").isEqualTo(convocatoriaPeriodoJustificacion.getConvocatoria().getId());
    Assertions.assertThat(convocatoriaPeriodoJustificacionActualizado.getMesInicial()).as("getMesInicial()")
        .isEqualTo(convocatoriaPeriodoJustificacion.getMesInicial());
    Assertions.assertThat(convocatoriaPeriodoJustificacionActualizado.getMesFinal()).as("getMesFinal()")
        .isEqualTo(convocatoriaPeriodoJustificacion.getMesFinal());
    Assertions.assertThat(convocatoriaPeriodoJustificacionActualizado.getFechaInicioPresentacion())
        .as("getFechaInicioPresentacion()").isEqualTo(convocatoriaPeriodoJustificacion.getFechaInicioPresentacion());
    Assertions.assertThat(convocatoriaPeriodoJustificacionActualizado.getFechaFinPresentacion())
        .as("getFechaFinPresentacion()").isEqualTo(convocatoriaPeriodoJustificacion.getFechaFinPresentacion());
    Assertions.assertThat(convocatoriaPeriodoJustificacionActualizado.getNumPeriodo()).as("getNumPeriodo()")
        .isEqualTo(3);
    Assertions.assertThat(convocatoriaPeriodoJustificacionActualizado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(convocatoriaPeriodoJustificacion.getObservaciones());
    Assertions.assertThat(convocatoriaPeriodoJustificacionActualizado.getTipoJustificacion())
        .as("getTipoJustificacion()").isEqualTo(convocatoriaPeriodoJustificacion.getTipoJustificacion());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    // given: existing ConvocatoriaPeriodoJustificacion to be deleted
    Long id = 1L;

    // when: delete ConvocatoriaPeriodoJustificacion
    final ResponseEntity<ConvocatoriaPeriodoJustificacion> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        ConvocatoriaPeriodoJustificacion.class, id);

    // then: ConvocatoriaPeriodoJustificacion deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsConvocatoriaPeriodoJustificacion() throws Exception {
    Long idConvocatoriaPeriodoJustificacion = 1L;

    final ResponseEntity<ConvocatoriaPeriodoJustificacion> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ConvocatoriaPeriodoJustificacion.class, idConvocatoriaPeriodoJustificacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = response.getBody();
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getId()).as("getId()")
        .isEqualTo(idConvocatoriaPeriodoJustificacion);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(1L);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getMesInicial()).as("getMesInicial()").isEqualTo(1);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getMesFinal()).as("getMesFinal()").isEqualTo(2);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getFechaInicioPresentacion())
        .as("getFechaInicioPresentacion()").isEqualTo(LocalDate.of(2020, 10, 10));
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getFechaFinPresentacion()).as("getFechaFinPresentacion()")
        .isEqualTo(LocalDate.of(2020, 11, 20));
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getNumPeriodo()).as("getNumPeriodo()").isEqualTo(1);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getObservaciones()).as("getObservaciones()")
        .isEqualTo("observaciones-001");
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getTipoJustificacion()).as("getTipoJustificacion()")
        .isEqualTo(TipoJustificacionEnum.PERIODICA);
  }

  /**
   * Función que devuelve un objeto ConvocatoriaPeriodoJustificacion
   * 
   * @param id id del ConvocatoriaPeriodoJustificacion
   * @return el objeto ConvocatoriaPeriodoJustificacion
   */
  private ConvocatoriaPeriodoJustificacion generarMockConvocatoriaPeriodoJustificacion(Long id) {
    Convocatoria convocatoria = new Convocatoria();
    convocatoria.setId(id == null ? 1 : id);

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = new ConvocatoriaPeriodoJustificacion();
    convocatoriaPeriodoJustificacion.setId(id);
    convocatoriaPeriodoJustificacion.setConvocatoria(convocatoria);
    convocatoriaPeriodoJustificacion.setNumPeriodo(1);
    convocatoriaPeriodoJustificacion.setMesInicial(1);
    convocatoriaPeriodoJustificacion.setMesFinal(2);
    convocatoriaPeriodoJustificacion.setFechaInicioPresentacion(LocalDate.of(2020, 10, 10));
    convocatoriaPeriodoJustificacion.setFechaFinPresentacion(LocalDate.of(2020, 11, 20));
    convocatoriaPeriodoJustificacion.setObservaciones("observaciones-" + id);
    convocatoriaPeriodoJustificacion.setTipoJustificacion(TipoJustificacionEnum.PERIODICA);

    return convocatoriaPeriodoJustificacion;
  }

}
