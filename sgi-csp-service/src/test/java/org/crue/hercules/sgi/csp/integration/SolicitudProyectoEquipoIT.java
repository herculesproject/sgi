package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
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
 * Test de integracion de SolicitudProyectoEquipoIT.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class SolicitudProyectoEquipoIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectoequipo";

  private HttpEntity<SolicitudProyectoEquipo> buildRequest(HttpHeaders headers, SolicitudProyectoEquipo entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-C", "CSP-SOL-E")));

    HttpEntity<SolicitudProyectoEquipo> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsSolicitudProyectoEquipo() throws Exception {
    SolicitudProyectoEquipo solicitudProyectoEquipo = generarSolicitudProyectoEquipo(null, 1L, 1L);

    final ResponseEntity<SolicitudProyectoEquipo> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, solicitudProyectoEquipo), SolicitudProyectoEquipo.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    SolicitudProyectoEquipo solicitudProyectoEquipoCreado = response.getBody();
    Assertions.assertThat(solicitudProyectoEquipoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(solicitudProyectoEquipoCreado.getSolicitudProyectoId()).as("getSolicitudProyectoId()")
        .isEqualTo(solicitudProyectoEquipo.getSolicitudProyectoId());
    Assertions.assertThat(solicitudProyectoEquipoCreado.getRolProyecto().getId()).as("getRolProyecto().getId()")
        .isEqualTo(solicitudProyectoEquipo.getRolProyecto().getId());
    Assertions.assertThat(solicitudProyectoEquipoCreado.getPersonaRef()).as("getPersonaRef()")
        .isEqualTo(solicitudProyectoEquipo.getPersonaRef());
    Assertions.assertThat(solicitudProyectoEquipoCreado.getMesInicio()).as("getMesInicio()")
        .isEqualTo(solicitudProyectoEquipo.getMesInicio());
    Assertions.assertThat(solicitudProyectoEquipoCreado.getMesFin()).as("getMesFin()")
        .isEqualTo(solicitudProyectoEquipo.getMesFin());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsSolicitudProyectoEquipo() throws Exception {
    Long idSolicitudProyectoEquipo = 1L;
    SolicitudProyectoEquipo solicitudProyectoEquipo = generarSolicitudProyectoEquipo(1L, 1L, 1L);
    solicitudProyectoEquipo.setMesFin(10);

    final ResponseEntity<SolicitudProyectoEquipo> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, solicitudProyectoEquipo),
        SolicitudProyectoEquipo.class, idSolicitudProyectoEquipo);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SolicitudProyectoEquipo solicitudProyectoEquipoActualizado = response.getBody();
    Assertions.assertThat(solicitudProyectoEquipoActualizado.getId()).as("getId()")
        .isEqualTo(solicitudProyectoEquipo.getId());
    Assertions.assertThat(solicitudProyectoEquipoActualizado.getMesFin()).as("getMesFin()")
        .isEqualTo(solicitudProyectoEquipo.getMesFin());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    // given: existing SolicitudProyectoEquipo to be deleted
    Long id = 1L;

    // when: delete SolicitudProyectoEquipo
    final ResponseEntity<SolicitudProyectoEquipo> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        SolicitudProyectoEquipo.class, id);

    // then: SolicitudProyectoEquipo deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsSolicitudProyectoEquipo() throws Exception {
    Long idSolicitudProyectoEquipo = 1L;

    final ResponseEntity<SolicitudProyectoEquipo> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        SolicitudProyectoEquipo.class, idSolicitudProyectoEquipo);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    SolicitudProyectoEquipo solicitudProyectoEquipo = response.getBody();
    Assertions.assertThat(solicitudProyectoEquipo.getId()).as("getId()").isEqualTo(idSolicitudProyectoEquipo);
    Assertions.assertThat(solicitudProyectoEquipo.getSolicitudProyectoId()).as("getSolicitudProyectoId()").isEqualTo(1);
    Assertions.assertThat(solicitudProyectoEquipo.getPersonaRef()).as("getPersonaRef()").isEqualTo("personaRef-001");
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoEquipo
   * 
   * @param solicitudProyectoEquipoId
   * @param solicitudProyectoId
   * @param tipoDocumentoId
   * @return el objeto SolicitudProyectoEquipo
   */
  private SolicitudProyectoEquipo generarSolicitudProyectoEquipo(Long solicitudProyectoEquipoId,
      Long solicitudProyectoId, Long rolProyectoId) {

    SolicitudProyectoEquipo solicitudProyectoEquipo = SolicitudProyectoEquipo.builder().id(solicitudProyectoEquipoId)
        .solicitudProyectoId(solicitudProyectoId).personaRef("personaRef-001")
        .rolProyecto(RolProyecto.builder().id(rolProyectoId).build()).mesInicio(1).mesFin(5).build();

    return solicitudProyectoEquipo;
  }

}
