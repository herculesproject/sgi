package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudModalidad;
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
 * Test de integracion de SolicitudModalidad.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class SolicitudModalidadIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudmodalidades";

  private HttpEntity<SolicitudModalidad> buildRequest(HttpHeaders headers, SolicitudModalidad entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-C", "CSP-SOL-E")));

    HttpEntity<SolicitudModalidad> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsSolicitudModalidad() throws Exception {
    SolicitudModalidad solicitudModalidad = generarMockSolicitudModalidad(null);

    final ResponseEntity<SolicitudModalidad> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, solicitudModalidad), SolicitudModalidad.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    SolicitudModalidad solicitudModalidadCreado = response.getBody();
    Assertions.assertThat(solicitudModalidadCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(solicitudModalidadCreado.getSolicitud().getId()).as("getSolicitud().getId()")
        .isEqualTo(solicitudModalidad.getSolicitud().getId());
    Assertions.assertThat(solicitudModalidadCreado.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(solicitudModalidad.getEntidadRef());
    Assertions.assertThat(solicitudModalidadCreado.getPrograma().getId()).as("getPrograma().getId()")
        .isEqualTo(solicitudModalidad.getPrograma().getId());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsSolicitudModalidad() throws Exception {
    Long idSolicitudModalidad = 1L;
    SolicitudModalidad solicitudModalidad = generarMockSolicitudModalidad(1L);
    solicitudModalidad.getPrograma().setId(3L);

    final ResponseEntity<SolicitudModalidad> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, solicitudModalidad), SolicitudModalidad.class, idSolicitudModalidad);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SolicitudModalidad solicitudModalidadActualizado = response.getBody();
    Assertions.assertThat(solicitudModalidadActualizado.getId()).as("getId()").isEqualTo(solicitudModalidad.getId());
    Assertions.assertThat(solicitudModalidadActualizado.getPrograma().getId()).as("getPrograma().getId()")
        .isEqualTo(solicitudModalidad.getPrograma().getId());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    // given: existing SolicitudModalidad to be deleted
    Long id = 1L;

    // when: delete SolicitudModalidad
    final ResponseEntity<SolicitudModalidad> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), SolicitudModalidad.class, id);

    // then: SolicitudModalidad deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsSolicitudModalidad() throws Exception {
    Long idSolicitudModalidad = 1L;

    final ResponseEntity<SolicitudModalidad> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), SolicitudModalidad.class, idSolicitudModalidad);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    SolicitudModalidad solicitudModalidad = response.getBody();
    Assertions.assertThat(solicitudModalidad.getId()).as("getId()").isEqualTo(idSolicitudModalidad);
    Assertions.assertThat(solicitudModalidad.getSolicitud().getId()).as("getSolicitud().getId()").isEqualTo(1);
    Assertions.assertThat(solicitudModalidad.getEntidadRef()).as("getEntidadRef()").isEqualTo("entidad-001");
    Assertions.assertThat(solicitudModalidad.getPrograma().getId()).as("getPrograma().getId()").isEqualTo(2);
  }

  /**
   * Función que devuelve un objeto SolicitudModalidad
   * 
   * @param id id del SolicitudModalidad
   * @return el objeto SolicitudModalidad
   */
  private SolicitudModalidad generarMockSolicitudModalidad(Long id) {
    Solicitud solicitud = new Solicitud();
    solicitud.setId(1L);

    Programa programa = new Programa();
    programa.setId(2L);

    SolicitudModalidad solicitudModalidad = new SolicitudModalidad();
    solicitudModalidad.setId(id);
    solicitudModalidad.setEntidadRef("entidad-001");
    solicitudModalidad.setSolicitud(solicitud);
    solicitudModalidad.setPrograma(programa);

    return solicitudModalidad;
  }

}
