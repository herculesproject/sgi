package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
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
 * Test de integracion de SolicitudProyectoDatos.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class SolicitudProyectoDatosIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectodatos";

  private HttpEntity<SolicitudProyectoDatos> buildRequest(HttpHeaders headers, SolicitudProyectoDatos entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-C", "CSP-SOL-E")));

    HttpEntity<SolicitudProyectoDatos> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsSolicitudProyectoDatos() throws Exception {
    SolicitudProyectoDatos solicitudProyectoDatos = generarSolicitudProyectoDatos(null, 1L);

    final ResponseEntity<SolicitudProyectoDatos> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, solicitudProyectoDatos), SolicitudProyectoDatos.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    SolicitudProyectoDatos solicitudProyectoDatosCreado = response.getBody();
    Assertions.assertThat(solicitudProyectoDatosCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(solicitudProyectoDatosCreado.getSolicitud().getId()).as("getSolicitud().getId()")
        .isEqualTo(solicitudProyectoDatos.getSolicitud().getId());
    Assertions.assertThat(solicitudProyectoDatosCreado.getTitulo()).as("getTitulo()")
        .isEqualTo(solicitudProyectoDatos.getTitulo());
    Assertions.assertThat(solicitudProyectoDatosCreado.getColaborativo()).as("getColaborativo()")
        .isEqualTo(solicitudProyectoDatos.getColaborativo());
    Assertions.assertThat(solicitudProyectoDatosCreado.getPresupuestoPorEntidades()).as("getPresupuestoPorEntidades()")
        .isEqualTo(solicitudProyectoDatos.getPresupuestoPorEntidades());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsSolicitudProyectoDatos() throws Exception {
    Long idSolicitudProyectoDatos = 1L;
    SolicitudProyectoDatos solicitudProyectoDatos = generarSolicitudProyectoDatos(1L, 1L);
    solicitudProyectoDatos.setTitulo("titulo-modificado");

    final ResponseEntity<SolicitudProyectoDatos> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, solicitudProyectoDatos),
        SolicitudProyectoDatos.class, idSolicitudProyectoDatos);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SolicitudProyectoDatos solicitudProyectoDatosActualizado = response.getBody();
    Assertions.assertThat(solicitudProyectoDatosActualizado.getId()).as("getId()")
        .isEqualTo(solicitudProyectoDatos.getId());
    Assertions.assertThat(solicitudProyectoDatosActualizado.getTitulo()).as("getTitulo()")
        .isEqualTo(solicitudProyectoDatos.getTitulo());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    // given: existing SolicitudProyectoDatos to be deleted
    Long id = 1L;

    // when: delete SolicitudProyectoDatos
    final ResponseEntity<SolicitudProyectoDatos> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        SolicitudProyectoDatos.class, id);

    // then: SolicitudProyectoDatos deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsSolicitudProyectoDatos() throws Exception {
    Long idSolicitudProyectoDatos = 1L;

    final ResponseEntity<SolicitudProyectoDatos> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        SolicitudProyectoDatos.class, idSolicitudProyectoDatos);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    SolicitudProyectoDatos solicitudProyectoDatos = response.getBody();
    Assertions.assertThat(solicitudProyectoDatos.getId()).as("getId()").isEqualTo(idSolicitudProyectoDatos);
    Assertions.assertThat(solicitudProyectoDatos.getSolicitud().getId()).as("getSolicitud().getId()").isEqualTo(1);
    Assertions.assertThat(solicitudProyectoDatos.getTitulo()).as("getTitulo()").isEqualTo("titulo-1");
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoDatos
   * 
   * @param solicitudProyectoDatosId
   * @param solicitudId
   * @return el objeto SolicitudProyectoDatos
   */
  private SolicitudProyectoDatos generarSolicitudProyectoDatos(Long solicitudProyectoDatosId, Long solicitudId) {

    SolicitudProyectoDatos solicitudProyectoDatos = SolicitudProyectoDatos.builder().id(solicitudProyectoDatosId)
        .solicitud(Solicitud.builder().id(solicitudId).build()).titulo("titulo-" + solicitudProyectoDatosId)
        .acronimo("acronimo-" + solicitudProyectoDatosId).colaborativo(Boolean.TRUE)
        .presupuestoPorEntidades(Boolean.TRUE).build();

    solicitudProyectoDatos.getSolicitud().setEstado(new EstadoSolicitud());
    solicitudProyectoDatos.getSolicitud().getEstado().setEstado(EstadoSolicitud.Estado.BORRADOR);
    return solicitudProyectoDatos;
  }

}
