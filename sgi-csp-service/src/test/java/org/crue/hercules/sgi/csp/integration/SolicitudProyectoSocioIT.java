package org.crue.hercules.sgi.csp.integration;

import java.math.BigDecimal;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
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
 * Test de integracion de SolicitudProyectoSocio.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class SolicitudProyectoSocioIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectosocio";

  private HttpEntity<SolicitudProyectoSocio> buildRequest(HttpHeaders headers, SolicitudProyectoSocio entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-C", "CSP-SOL-E")));

    HttpEntity<SolicitudProyectoSocio> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsSolicitudProyectoSocio() throws Exception {
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(null, 1L, 1L);

    final ResponseEntity<SolicitudProyectoSocio> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, solicitudProyectoSocio), SolicitudProyectoSocio.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    SolicitudProyectoSocio solicitudProyectoSocioCreado = response.getBody();
    Assertions.assertThat(solicitudProyectoSocioCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(solicitudProyectoSocioCreado.getRolSocio().getId()).as("getRolSocio().getId()")
        .isEqualTo(solicitudProyectoSocio.getRolSocio().getId());
    Assertions.assertThat(solicitudProyectoSocioCreado.getSolicitudProyectoDatos().getId())
        .as("getSolicitudProyectoDatos().getId()")
        .isEqualTo(solicitudProyectoSocio.getSolicitudProyectoDatos().getId());
    Assertions.assertThat(solicitudProyectoSocioCreado.getImporteSolicitado()).as("getImporteSolicitado()")
        .isEqualTo(solicitudProyectoSocio.getImporteSolicitado());
    Assertions.assertThat(solicitudProyectoSocioCreado.getNumInvestigadores()).as("getNumInvestigadores()")
        .isEqualTo(solicitudProyectoSocio.getNumInvestigadores());
    Assertions.assertThat(solicitudProyectoSocioCreado.getMesInicio()).as("getMesInicio()")
        .isEqualTo(solicitudProyectoSocio.getMesInicio());
    Assertions.assertThat(solicitudProyectoSocioCreado.getMesFin()).as("getMesFin()")
        .isEqualTo(solicitudProyectoSocio.getMesFin());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsSolicitudProyectoSocio() throws Exception {
    Long idSolicitudProyectoSocio = 1L;
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);
    solicitudProyectoSocio.setMesFin(10);

    final ResponseEntity<SolicitudProyectoSocio> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, solicitudProyectoSocio),
        SolicitudProyectoSocio.class, idSolicitudProyectoSocio);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SolicitudProyectoSocio solicitudProyectoSocioActualizado = response.getBody();
    Assertions.assertThat(solicitudProyectoSocioActualizado.getId()).as("getId()")
        .isEqualTo(solicitudProyectoSocio.getId());
    Assertions.assertThat(solicitudProyectoSocioActualizado.getMesFin()).as("getMesFin()")
        .isEqualTo(solicitudProyectoSocio.getMesFin());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    // given: existing SolicitudProyectoSocio to be deleted
    Long id = 1L;

    // when: delete SolicitudProyectoSocio
    final ResponseEntity<SolicitudProyectoSocio> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        SolicitudProyectoSocio.class, id);

    // then: SolicitudProyectoSocio deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsSolicitudProyectoSocio() throws Exception {
    Long idSolicitudProyectoSocio = 1L;

    final ResponseEntity<SolicitudProyectoSocio> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        SolicitudProyectoSocio.class, idSolicitudProyectoSocio);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    SolicitudProyectoSocio solicitudProyectoSocio = response.getBody();
    Assertions.assertThat(solicitudProyectoSocio.getId()).as("getId()").isEqualTo(idSolicitudProyectoSocio);
    Assertions.assertThat(solicitudProyectoSocio.getSolicitudProyectoDatos().getId())
        .as("getSolicitudProyectoDatos().getId()").isEqualTo(1);
    Assertions.assertThat(solicitudProyectoSocio.getRolSocio().getId()).as("getRolSocio().getId()")
        .isEqualTo(solicitudProyectoSocio.getRolSocio().getId());
    Assertions.assertThat(solicitudProyectoSocio.getMesFin()).as("getMesFin()")
        .isEqualTo(solicitudProyectoSocio.getMesFin());
    Assertions.assertThat(solicitudProyectoSocio.getMesInicio()).as("getMesInicio()")
        .isEqualTo(solicitudProyectoSocio.getMesInicio());
    Assertions.assertThat(solicitudProyectoSocio.getNumInvestigadores()).as("getNumInvestigadores()")
        .isEqualTo(solicitudProyectoSocio.getNumInvestigadores());
    Assertions.assertThat(solicitudProyectoSocio.getImporteSolicitado()).as("getImporteSolicitado()")
        .isEqualTo(solicitudProyectoSocio.getImporteSolicitado());
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoSocio
   * 
   * @param solicitudProyectoSocioId Id de solicitud proyecto socio
   * @param solicitudProyectoDatosId Id de solicitud proyecto datos
   * @param rolSocioId               Id de solicitud rol socio
   * @return el objeto SolicitudProyectoSocio
   */
  private SolicitudProyectoSocio generarSolicitudProyectoSocio(Long solicitudProyectoSocioId,
      Long solicitudProyectoDatosId, Long rolSocioId) {

    SolicitudProyectoSocio solicitudProyectoSocio = SolicitudProyectoSocio.builder().id(solicitudProyectoSocioId)
        .solicitudProyectoDatos(SolicitudProyectoDatos.builder().id(solicitudProyectoDatosId).build())
        .rolSocio(RolSocio.builder().id(rolSocioId).build()).mesInicio(1).mesFin(5).numInvestigadores(7)
        .importeSolicitado(new BigDecimal(1000)).empresaRef("002").build();

    return solicitudProyectoSocio;
  }

}
