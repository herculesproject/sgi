package org.crue.hercules.sgi.csp.integration;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoPago;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer.TokenBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de SolicitudProyectoPeriodoPago.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class SolicitudProyectoPeriodoPagoIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectoperiodopago";

  private HttpEntity<SolicitudProyectoPeriodoPago> buildRequest(HttpHeaders headers,
      SolicitudProyectoPeriodoPago entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-C", "CSP-SOL-E")));

    HttpEntity<SolicitudProyectoPeriodoPago> request = new HttpEntity<>(entity, headers);
    return request;

  }

  private HttpEntity<List<SolicitudProyectoPeriodoPago>> buildRequestList(HttpHeaders headers,
      List<SolicitudProyectoPeriodoPago> entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-C", "CSP-SOL-E")));

    HttpEntity<List<SolicitudProyectoPeriodoPago>> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsSolicitudProyectoPeriodoPago() throws Exception {
    // given: una lista con uno de los SolicitudProyectoPeriodoPago actualizado,
    // otro nuevo y sin los otros 3 periodos existentes
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoPeriodoPago newSolicitudProyectoPeriodoPago = generarSolicitudProyectoPeriodoPago(null, 1L);
    SolicitudProyectoPeriodoPago updatedSolicitudProyectoPeriodoPago = generarSolicitudProyectoPeriodoPago(4L, 1L);

    updatedSolicitudProyectoPeriodoPago.setMes(6);

    List<SolicitudProyectoPeriodoPago> solicitudProyectoPeriodoPagos = Arrays.asList(newSolicitudProyectoPeriodoPago,
        updatedSolicitudProyectoPeriodoPago);

    // when: updateSolicitudProyectoPeriodoPago
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(solicitudProyectoSocioId).toUri();

    final ResponseEntity<List<SolicitudProyectoPeriodoPago>> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildRequestList(null, solicitudProyectoPeriodoPagos),
        new ParameterizedTypeReference<List<SolicitudProyectoPeriodoPago>>() {
        });

    // then: Se crea el nuevo SolicitudProyectoPeriodoPago, se actualiza el
    // existe y se eliminan los otros
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    List<SolicitudProyectoPeriodoPago> responseData = response.getBody();

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CENL-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "mes,asc";

    URI uriFindAllSolicitudProyectoPeriodoPago = UriComponentsBuilder
        .fromUriString("/solicitudproyectosocio" + PATH_PARAMETER_ID + CONTROLLER_BASE_PATH).queryParam("s", sort)
        .buildAndExpand(solicitudProyectoSocioId).toUri();

    final ResponseEntity<List<SolicitudProyectoPeriodoPago>> responseFindAllSolicitudProyectoPeriodoPago = restTemplate
        .exchange(uriFindAllSolicitudProyectoPeriodoPago, HttpMethod.GET, buildRequestList(headers, null),
            new ParameterizedTypeReference<List<SolicitudProyectoPeriodoPago>>() {
            });

    Assertions.assertThat(responseFindAllSolicitudProyectoPeriodoPago.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SolicitudProyectoPeriodoPago> responseDataFindAll = responseFindAllSolicitudProyectoPeriodoPago
        .getBody();
    Assertions.assertThat(responseDataFindAll.size()).as("size()").isEqualTo(solicitudProyectoPeriodoPagos.size());
    Assertions.assertThat(responseDataFindAll.get(0).getId()).as("responseDataFindAll.get(0).getId()")
        .isEqualTo(responseData.get(0).getId());
    Assertions.assertThat(responseDataFindAll.get(1).getId()).as("responseDataFindAll.get(1).getId()")
        .isEqualTo(responseData.get(1).getId());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsSolicitudProyectoPeriodoPago() throws Exception {
    Long idSolicitudProyectoPeriodoPago = 1L;

    final ResponseEntity<SolicitudProyectoPeriodoPago> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        SolicitudProyectoPeriodoPago.class, idSolicitudProyectoPeriodoPago);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    SolicitudProyectoPeriodoPago solicitudProyectoPeriodoPago = response.getBody();
    Assertions.assertThat(solicitudProyectoPeriodoPago.getId()).as("getId()").isEqualTo(idSolicitudProyectoPeriodoPago);
    Assertions.assertThat(solicitudProyectoPeriodoPago.getSolicitudProyectoSocio().getId())
        .as("getSolicitudProyectoSocio().getId()").isEqualTo(1);
    Assertions.assertThat(solicitudProyectoPeriodoPago.getNumPeriodo()).as("getNumPeriodo()").isEqualTo(3);
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoPeriodoPago
   * 
   * @param solicitudProyectoPeriodoPagoId
   * @param solicitudProyectoSocioId
   * @return el objeto SolicitudProyectoPeriodoPago
   */
  private SolicitudProyectoPeriodoPago generarSolicitudProyectoPeriodoPago(Long solicitudProyectoPeriodoPagoId,
      Long solicitudProyectoSocioId) {

    SolicitudProyectoPeriodoPago solicitudProyectoPeriodoPago = SolicitudProyectoPeriodoPago.builder()
        .id(solicitudProyectoPeriodoPagoId)
        .solicitudProyectoSocio(SolicitudProyectoSocio.builder().id(solicitudProyectoSocioId).build()).numPeriodo(3)
        .importe(new BigDecimal(789)).mes(3).build();

    solicitudProyectoPeriodoPago.getSolicitudProyectoSocio().setSolicitudProyectoDatos(new SolicitudProyectoDatos());
    solicitudProyectoPeriodoPago.getSolicitudProyectoSocio().getSolicitudProyectoDatos().setSolicitud(new Solicitud());
    solicitudProyectoPeriodoPago.getSolicitudProyectoSocio().getSolicitudProyectoDatos().getSolicitud()
        .setEstado(new EstadoSolicitud());
    solicitudProyectoPeriodoPago.getSolicitudProyectoSocio().getSolicitudProyectoDatos().getSolicitud().getEstado()
        .setEstado(EstadoSolicitud.Estado.BORRADOR);
    return solicitudProyectoPeriodoPago;
  }

}
