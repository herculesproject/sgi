package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.EjecucionEconomicaInvestigadorController;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgeService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EjecucionEconomicaInvestigadorIT extends BaseIT {
  private static final String CONTROLLER_BASE_PATH = EjecucionEconomicaInvestigadorController.REQUEST_MAPPING;
  private static final String PROYECTO_SGE_REF = "proyecto-sge-ref-001";

  private static final String[] ROLES_INVESTIGADOR = { "CSP-EJEC-INV-VR" };

  @MockBean
  private SgiApiSgeService sgiApiSgeService;

  private HttpEntity<Object> buildRequest(String personaRef, String... roles) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken(personaRef, roles)));

    return new HttpEntity<>(null, headers);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/proyecto_proyecto_sge.sql",
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_equipo.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findDatoEconomicoDetalle_refAjena_returns403() throws Exception {
    // given: "ajeno" no es investigador principal de ningun proyecto con esa ref
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + "/oper-1")
        .queryParam("proyectoSgeRef", PROYECTO_SGE_REF)
        .queryParam("tipoOperacion", "EPG").build().toUri();

    // when: consulta el detalle de una operacion de un proyecto SGE ajeno
    final ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest("ajeno", ROLES_INVESTIGADOR), new ParameterizedTypeReference<Object>() {
        });

    // then: acceso denegado y el SGE NO se invoca (la autorizacion falla antes)
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    Mockito.verify(sgiApiSgeService, Mockito.never()).findDatoEconomicoDetalle(ArgumentMatchers.anyString(),
        ArgumentMatchers.any());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/proyecto_proyecto_sge.sql",
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_equipo.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findDatoEconomicoDetalle_refPropia_reenviaAlSge() throws Exception {
    // given: "user" es investigador principal del proyecto 1 (proyecto-sge-ref-001)
    Mockito.when(sgiApiSgeService.findDatoEconomicoDetalle(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
        .thenReturn(Collections.singletonMap("id", "oper-1"));

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + "/oper-1")
        .queryParam("proyectoSgeRef", PROYECTO_SGE_REF)
        .queryParam("tipoOperacion", "EPG").build().toUri();

    // when: consulta el detalle de una operacion de un proyecto SGE propio
    final ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest("user", ROLES_INVESTIGADOR), new ParameterizedTypeReference<Object>() {
        });

    // then: acceso permitido y la peticion se reenvia al SGE con los argumentos
    // recibidos
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Mockito.verify(sgiApiSgeService, Mockito.times(1)).findDatoEconomicoDetalle(ArgumentMatchers.eq("oper-1"),
        ArgumentMatchers.eq("EPG"));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/proyecto_proyecto_sge.sql",
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_equipo.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findDatosEconomicos_refAjena_returns403() throws Exception {
    // given: "ajeno" no es investigador principal de ningun proyecto con esa ref
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH)
        .queryParam("proyectoSgeRef", PROYECTO_SGE_REF).build().toUri();

    // when: consulta los datos economicos de un proyecto SGE ajeno
    final ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest("ajeno", ROLES_INVESTIGADOR), new ParameterizedTypeReference<Object>() {
        });

    // then: acceso denegado y el SGE NO se invoca (la autorizacion falla antes)
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    Mockito.verify(sgiApiSgeService, Mockito.never()).findDatosEconomicos(ArgumentMatchers.any(),
        ArgumentMatchers.any(), ArgumentMatchers.any());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/proyecto_proyecto_sge.sql",
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_equipo.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findDatosEconomicos_refPropia_reenviaAlSge() throws Exception {
    // given: "user" es investigador principal del proyecto 1 (proyecto-sge-ref-001)
    Mockito.when(sgiApiSgeService.findDatosEconomicos(ArgumentMatchers.any(), ArgumentMatchers.any(),
        ArgumentMatchers.any()))
        .thenReturn(new PageImpl<>(Collections.singletonList(Collections.singletonMap("id", "oper-1")),
            Pageable.unpaged(), 1));

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH)
        .queryParam("proyectoSgeRef", PROYECTO_SGE_REF).build().toUri();

    // when: consulta los datos economicos de un proyecto SGE propio
    final ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest("user", ROLES_INVESTIGADOR), new ParameterizedTypeReference<Object>() {
        });

    // then: acceso permitido y la peticion se reenvia al SGE
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Mockito.verify(sgiApiSgeService, Mockito.times(1)).findDatosEconomicos(ArgumentMatchers.any(),
        ArgumentMatchers.any(), ArgumentMatchers.any());
  }

}
