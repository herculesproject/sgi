package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipoSocio;
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
 * Test de integracion de SolicitudProyectoEquipoSocio.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class SolicitudProyectoEquipoSocioIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectoequiposocio";

  private HttpEntity<SolicitudProyectoEquipoSocio> buildRequest(HttpHeaders headers,
      SolicitudProyectoEquipoSocio entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-C", "CSP-SOL-E")));

    HttpEntity<SolicitudProyectoEquipoSocio> request = new HttpEntity<>(entity, headers);
    return request;

  }

  private HttpEntity<List<SolicitudProyectoEquipoSocio>> buildRequestList(HttpHeaders headers,
      List<SolicitudProyectoEquipoSocio> entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CENTGES-C", "CSP-CENTGES-V", "CSP-SOL-C")));

    HttpEntity<List<SolicitudProyectoEquipoSocio>> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsListSolicitudProyectoEquipoSocio() throws Exception {

    // given: una lista con uno de los SolicitudProyectoEquipoSocio actualizado,
    // otro nuevo y sin los otros 3 proyecto equipo socio existentes
    Long solicitudProyectoSocioId = 1L;

    SolicitudProyectoEquipoSocio newsolicitudProyectoEquipoSocio = generarSolicitudProyectoEquipoSocio(null, 1L);

    SolicitudProyectoEquipoSocio updateSolicitudProyectoEquipoSocio = generarSolicitudProyectoEquipoSocio(103L, 1L);
    updateSolicitudProyectoEquipoSocio.setPersonaRef("user-002");
    updateSolicitudProyectoEquipoSocio.setMesInicio(3);
    updateSolicitudProyectoEquipoSocio.setMesFin(4);

    List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocioUpdate = new ArrayList<SolicitudProyectoEquipoSocio>();
    solicitudProyectoEquipoSocioUpdate.add(newsolicitudProyectoEquipoSocio);
    solicitudProyectoEquipoSocioUpdate.add(updateSolicitudProyectoEquipoSocio);

    // when: updateSolicitudProyectoEquipoSocio
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(solicitudProyectoSocioId).toUri();

    final ResponseEntity<List<SolicitudProyectoEquipoSocio>> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildRequestList(null, solicitudProyectoEquipoSocioUpdate),
        new ParameterizedTypeReference<List<SolicitudProyectoEquipoSocio>>() {
        });

    // then: Se crea el nuevo SolicitudProyectoEquipoSocio, se actualiza el
    // existente y se eliminan los otros
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    List<SolicitudProyectoEquipoSocio> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(2);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsSolicitudProyectoEquipoSocio() throws Exception {
    Long idSolicitudProyectoEquipoSocio = 1L;

    final ResponseEntity<SolicitudProyectoEquipoSocio> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        SolicitudProyectoEquipoSocio.class, idSolicitudProyectoEquipoSocio);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    SolicitudProyectoEquipoSocio solicitudProyectoEquipoSocio = response.getBody();
    Assertions.assertThat(solicitudProyectoEquipoSocio.getId()).as("getId()").isEqualTo(idSolicitudProyectoEquipoSocio);
    Assertions.assertThat(solicitudProyectoEquipoSocio.getRolProyecto().getId()).as("getRolProyecto().getId()")
        .isEqualTo(1);
    Assertions.assertThat(solicitudProyectoEquipoSocio.getPersonaRef()).as("getPersonaRef()").isEqualTo("user-001");
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoEquipoSocio
   * 
   * @param solicitudProyectoEquipoSocioId
   * @param entidadesRelacionadasId
   * @return el objeto SolicitudProyectoEquipoSocio
   */
  private SolicitudProyectoEquipoSocio generarSolicitudProyectoEquipoSocio(Long solicitudProyectoEquipoSocioId,
      Long entidadesRelacionadasId) {

    SolicitudProyectoEquipoSocio solicitudProyectoEquipoSocio = SolicitudProyectoEquipoSocio.builder()
        .id(solicitudProyectoEquipoSocioId)
        .solicitudProyectoSocio(SolicitudProyectoSocio.builder().id(entidadesRelacionadasId)
            .solicitudProyectoDatos(SolicitudProyectoDatos.builder().id(1L)
                .solicitud(Solicitud.builder().id(1L).activo(Boolean.TRUE).build()).build())
            .build())
        .rolProyecto(RolProyecto.builder().id(entidadesRelacionadasId).build())
        .personaRef("user-" + solicitudProyectoEquipoSocioId).mesInicio(1).mesFin(2).build();

    solicitudProyectoEquipoSocio.getSolicitudProyectoSocio().getSolicitudProyectoDatos().getSolicitud()
        .setEstado(new EstadoSolicitud());
    solicitudProyectoEquipoSocio.getSolicitudProyectoSocio().getSolicitudProyectoDatos().getSolicitud().getEstado()
        .setEstado(EstadoSolicitud.Estado.BORRADOR);

    return solicitudProyectoEquipoSocio;
  }

}
