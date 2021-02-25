package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
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
 * Test de integracion de SolicitudProyectoPeriodoJustificacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SolicitudProyectoPeriodoJustificacionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectoperiodojustificaciones";

  private HttpEntity<SolicitudProyectoPeriodoJustificacion> buildRequest(HttpHeaders headers,
      SolicitudProyectoPeriodoJustificacion entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CENTGES-C", "CSP-CENTGES-V", "CSP-SOL-C")));

    HttpEntity<SolicitudProyectoPeriodoJustificacion> request = new HttpEntity<>(entity, headers);
    return request;
  }

  private HttpEntity<List<SolicitudProyectoPeriodoJustificacion>> buildRequestList(HttpHeaders headers,
      List<SolicitudProyectoPeriodoJustificacion> entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CENTGES-C", "CSP-CENTGES-V", "CSP-SOL-C")));

    HttpEntity<List<SolicitudProyectoPeriodoJustificacion>> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void updateSolicitudProyectoPeriodoJustificacionesSolicitudProyectoSocio_ReturnsSolicitudProyectoPeriodoJustificacionList()
      throws Exception {

    // given: una lista con uno de los SolicitudProyectoPeriodoJustificacion
    // actualizado,
    // otro nuevo y sin los otros 3 periodos existentes
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoPeriodoJustificacion newSolicitudProyectoPeriodoJustificacion = generarMockSolicitudProyectoPeriodoJustificacion(
        null, 27, 30, 1L);
    SolicitudProyectoPeriodoJustificacion updatedSolicitudProyectoPeriodoJustificacion = generarMockSolicitudProyectoPeriodoJustificacion(
        103L, 24, 26, 1L);

    List<SolicitudProyectoPeriodoJustificacion> solicitudProyectoPeriodoJustificaciones = Arrays
        .asList(newSolicitudProyectoPeriodoJustificacion, updatedSolicitudProyectoPeriodoJustificacion);

    // when: updateSolicitudProyectoPeriodoJustificacionesSolicitudProyecto
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(solicitudProyectoSocioId).toUri();

    final ResponseEntity<List<SolicitudProyectoPeriodoJustificacion>> response = restTemplate.exchange(uri,
        HttpMethod.PATCH, buildRequestList(null, solicitudProyectoPeriodoJustificaciones),
        new ParameterizedTypeReference<List<SolicitudProyectoPeriodoJustificacion>>() {
        });

    // then: Se crea el nuevo SolicitudProyectoPeriodoJustificacion, se actualiza el
    // existe y se eliminan los otros
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    List<SolicitudProyectoPeriodoJustificacion> responseData = response.getBody();
    Assertions.assertThat(responseData.get(0).getId()).as("get(0).getId()")
        .isEqualTo(updatedSolicitudProyectoPeriodoJustificacion.getId());
    Assertions.assertThat(responseData.get(0).getSolicitudProyectoSocio().getId())
        .as("get(0).getSolicitudProyectoSocio().getId()").isEqualTo(solicitudProyectoSocioId);
    Assertions.assertThat(responseData.get(0).getMesInicial()).as("get(0).getMesInicial()")
        .isEqualTo(updatedSolicitudProyectoPeriodoJustificacion.getMesInicial());
    Assertions.assertThat(responseData.get(0).getMesFinal()).as("get(0).getMesFinal()")
        .isEqualTo(updatedSolicitudProyectoPeriodoJustificacion.getMesFinal());
    Assertions.assertThat(responseData.get(0).getFechaInicio()).as("get(0).getFechaInicio()")
        .isEqualTo(updatedSolicitudProyectoPeriodoJustificacion.getFechaInicio());
    Assertions.assertThat(responseData.get(0).getFechaFin()).as("get(0).getFechaFin()")
        .isEqualTo(updatedSolicitudProyectoPeriodoJustificacion.getFechaFin());
    Assertions.assertThat(responseData.get(0).getNumPeriodo()).as("get(0).getNumPeriodo()").isEqualTo(1);
    Assertions.assertThat(responseData.get(0).getObservaciones()).as("get(0).getObservaciones()")
        .isEqualTo(updatedSolicitudProyectoPeriodoJustificacion.getObservaciones());

    Assertions.assertThat(responseData.get(1).getSolicitudProyectoSocio().getId())
        .as("get(1).getSolicitudProyectoSocio().getId()").isEqualTo(solicitudProyectoSocioId);
    Assertions.assertThat(responseData.get(1).getMesInicial()).as("get(1).getMesInicial()")
        .isEqualTo(newSolicitudProyectoPeriodoJustificacion.getMesInicial());
    Assertions.assertThat(responseData.get(1).getMesFinal()).as("get(1).getMesFinal()")
        .isEqualTo(newSolicitudProyectoPeriodoJustificacion.getMesFinal());
    Assertions.assertThat(responseData.get(1).getFechaInicio()).as("get(1).getFechaInicio()")
        .isEqualTo(newSolicitudProyectoPeriodoJustificacion.getFechaInicio());
    Assertions.assertThat(responseData.get(1).getFechaFin()).as("get(1).getFechaFin()")
        .isEqualTo(newSolicitudProyectoPeriodoJustificacion.getFechaFin());
    Assertions.assertThat(responseData.get(1).getNumPeriodo()).as("get(1).getNumPeriodo()").isEqualTo(2);
    Assertions.assertThat(responseData.get(1).getObservaciones()).as("get(1).getObservaciones()")
        .isEqualTo(newSolicitudProyectoPeriodoJustificacion.getObservaciones());

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CENL-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "mesInicial,asc";

    URI uriFindAllSolicitudProyectoPeriodoJustificacion = UriComponentsBuilder
        .fromUriString("/solicitudproyectosocio" + PATH_PARAMETER_ID + CONTROLLER_BASE_PATH).queryParam("s", sort)
        .buildAndExpand(solicitudProyectoSocioId).toUri();

    final ResponseEntity<List<SolicitudProyectoPeriodoJustificacion>> responseFindAllSolicitudProyectoPeriodoJustificacion = restTemplate
        .exchange(uriFindAllSolicitudProyectoPeriodoJustificacion, HttpMethod.GET, buildRequestList(headers, null),
            new ParameterizedTypeReference<List<SolicitudProyectoPeriodoJustificacion>>() {
            });

    Assertions.assertThat(responseFindAllSolicitudProyectoPeriodoJustificacion.getStatusCode())
        .isEqualTo(HttpStatus.OK);
    final List<SolicitudProyectoPeriodoJustificacion> responseDataFindAll = responseFindAllSolicitudProyectoPeriodoJustificacion
        .getBody();
    Assertions.assertThat(responseDataFindAll.size()).as("size()")
        .isEqualTo(solicitudProyectoPeriodoJustificaciones.size());
    Assertions.assertThat(responseDataFindAll.get(0).getId()).as("responseDataFindAll.get(0).getId()")
        .isEqualTo(responseData.get(0).getId());
    Assertions.assertThat(responseDataFindAll.get(1).getId()).as("responseDataFindAll.get(1).getId()")
        .isEqualTo(responseData.get(1).getId());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsSolicitudProyectoPeriodoJustificacion() throws Exception {
    Long idSolicitudProyectoPeriodoJustificacion = 1L;

    final ResponseEntity<SolicitudProyectoPeriodoJustificacion> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        SolicitudProyectoPeriodoJustificacion.class, idSolicitudProyectoPeriodoJustificacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacion = response.getBody();
    Assertions.assertThat(solicitudProyectoPeriodoJustificacion.getId()).as("getId()")
        .isEqualTo(idSolicitudProyectoPeriodoJustificacion);
    Assertions.assertThat(solicitudProyectoPeriodoJustificacion.getSolicitudProyectoSocio().getId())
        .as("getSolicitudProyectoSocio().getId()").isEqualTo(1L);
    Assertions.assertThat(solicitudProyectoPeriodoJustificacion.getMesInicial()).as("getMesInicial()").isEqualTo(1);
    Assertions.assertThat(solicitudProyectoPeriodoJustificacion.getMesFinal()).as("getMesFinal()").isEqualTo(2);
    Assertions.assertThat(solicitudProyectoPeriodoJustificacion.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(LocalDate.of(2020, 10, 10));
    Assertions.assertThat(solicitudProyectoPeriodoJustificacion.getFechaFin()).as("getFechaFin()")
        .isEqualTo(LocalDate.of(2020, 11, 20));
    Assertions.assertThat(solicitudProyectoPeriodoJustificacion.getNumPeriodo()).as("getNumPeriodo()").isEqualTo(1);
    Assertions.assertThat(solicitudProyectoPeriodoJustificacion.getObservaciones()).as("getObservaciones()")
        .isEqualTo("observaciones-001");
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoPeriodoJustificacion
   * 
   * @param id                  id del SolicitudProyectoPeriodoJustificacion
   * @param mesInicial          Mes inicial
   * @param mesFinal            Mes final
   * @param solicitudProyectoId Id SolicitudProyecto
   * @return el objeto SolicitudProyectoPeriodoJustificacion
   */
  private SolicitudProyectoPeriodoJustificacion generarMockSolicitudProyectoPeriodoJustificacion(Long id,
      Integer mesInicial, Integer mesFinal, Long solicitudProyectoId) {
    SolicitudProyectoSocio solicitudProyectoSocio = new SolicitudProyectoSocio();
    solicitudProyectoSocio.setId(solicitudProyectoId == null ? 1 : solicitudProyectoId);
    SolicitudProyectoDatos solicitudProyectoDatos = new SolicitudProyectoDatos();
    solicitudProyectoDatos.setId(1L);
    Solicitud solicitud = new Solicitud();
    solicitud.setId(1L);
    solicitud.setActivo(Boolean.TRUE);
    solicitudProyectoDatos.setSolicitud(solicitud);
    solicitudProyectoSocio.setSolicitudProyectoDatos(solicitudProyectoDatos);

    SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacion = new SolicitudProyectoPeriodoJustificacion();
    solicitudProyectoPeriodoJustificacion.setId(id);
    solicitudProyectoPeriodoJustificacion.setSolicitudProyectoSocio(solicitudProyectoSocio);
    solicitudProyectoPeriodoJustificacion.setNumPeriodo(1);
    solicitudProyectoPeriodoJustificacion.setMesInicial(mesInicial);
    solicitudProyectoPeriodoJustificacion.setMesFinal(mesFinal);
    solicitudProyectoPeriodoJustificacion.setFechaInicio(LocalDate.of(2020, 10, 10));
    solicitudProyectoPeriodoJustificacion.setFechaFin(LocalDate.of(2020, 11, 20));
    solicitudProyectoPeriodoJustificacion.setObservaciones("observaciones-" + id);

    return solicitudProyectoPeriodoJustificacion;
  }

}
