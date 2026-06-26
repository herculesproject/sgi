package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.RelacionEjecucionEconomicaController;
import org.crue.hercules.sgi.csp.dto.RelacionEjecucionEconomica;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RelacionEjecucionEconomicaInvestigadorIT extends BaseIT {
  private static final String CONTROLLER_BASE_PATH = RelacionEjecucionEconomicaController.REQUEST_MAPPING;
  private static final String PATH_INVESTIGADOR_PROYECTOS = RelacionEjecucionEconomicaController.PATH_INVESTIGADOR_PROYECTOS;
  private static final String PATH_INVESTIGADOR_GRUPOS = RelacionEjecucionEconomicaController.PATH_INVESTIGADOR_GRUPOS;
  private static final String PATH_INVESTIGADOR_PROYECTO_SGE_REF = "/investigador/proyecto-sge-ref";

  private static final String[] ROLES_INVESTIGADOR = { "CSP-EJEC-INV-VR" };

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String personaRef, String... roles)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken(personaRef, roles)));

    return new HttpEntity<>(entity, headers);
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
  void findRelacionesProyectosInvestigador_participacionCualquierFecha_returnsSoloProyectosDondeEsIp()
      throws Exception {
    // given: la peticion del investigador "user" sin restringir a participacion
    // actual
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page-Size", "10");

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_INVESTIGADOR_PROYECTOS)
        .queryParam("onlyWithParticipacionActual", false).build().toUri();

    // when: se recuperan las relaciones de los proyectos del investigador
    final ResponseEntity<List<RelacionEjecucionEconomica>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, "user", ROLES_INVESTIGADOR),
        new ParameterizedTypeReference<List<RelacionEjecucionEconomica>>() {
        });

    // then: solo se devuelven los proyectos donde es IP, sin los ajenos
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    List<String> refs = response.getBody().stream().map(RelacionEjecucionEconomica::getProyectoSgeRef)
        .collect(Collectors.toList());
    Assertions.assertThat(refs).as("refs").containsExactlyInAnyOrder("proyecto-sge-ref-001", "proyecto-sge-ref-002");
    Assertions.assertThat(refs).as("no incluye ajenos").doesNotContain("proyecto-sge-ref-003");
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
  void findRelacionesProyectosInvestigador_onlyWithParticipacionActual_returnsSoloProyectosConParticipacionVigente()
      throws Exception {
    // given: la peticion del investigador "user" restringida a participacion actual
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page-Size", "10");

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_INVESTIGADOR_PROYECTOS)
        .queryParam("onlyWithParticipacionActual", true).build().toUri();

    // when: se recuperan las relaciones de los proyectos del investigador
    final ResponseEntity<List<RelacionEjecucionEconomica>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, "user", ROLES_INVESTIGADOR),
        new ParameterizedTypeReference<List<RelacionEjecucionEconomica>>() {
        });

    // then: solo se devuelven los proyectos con participacion vigente en la fecha
    // actual
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    List<String> refs = response.getBody().stream().map(RelacionEjecucionEconomica::getProyectoSgeRef)
        .collect(Collectors.toList());
    Assertions.assertThat(refs).as("refs vigentes").containsExactly("proyecto-sge-ref-001");
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
  void findRelacionesProyectosInvestigador_sinParticipacion_returnsNoContent() throws Exception {
    // given: la persona "ajeno" no participa en ningun proyecto
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page-Size", "10");

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_INVESTIGADOR_PROYECTOS)
        .queryParam("onlyWithParticipacionActual", false).build().toUri();

    // when: se recuperan las relaciones de los proyectos del investigador
    final ResponseEntity<List<RelacionEjecucionEconomica>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, "ajeno", ROLES_INVESTIGADOR),
        new ParameterizedTypeReference<List<RelacionEjecucionEconomica>>() {
        });

    // then: no hay contenido que devolver
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off
      "classpath:scripts/tipo-grupo.sql",
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/grupo_equipo.sql"
      // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findRelacionesGruposInvestigador_participacionCualquierFecha_returnsSoloGruposDondeEsResponsable()
      throws Exception {
    // given: la peticion de la persona "22932567" sin restringir a participacion
    // actual
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page-Size", "10");

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_INVESTIGADOR_GRUPOS)
        .queryParam("onlyWithParticipacionActual", false).build().toUri();

    // when: se recuperan las relaciones de los grupos del investigador
    final ResponseEntity<List<RelacionEjecucionEconomica>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, "22932567", ROLES_INVESTIGADOR),
        new ParameterizedTypeReference<List<RelacionEjecucionEconomica>>() {
        });

    // then: solo se devuelven los grupos donde es responsable
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    List<String> refs = response.getBody().stream().map(RelacionEjecucionEconomica::getProyectoSgeRef)
        .collect(Collectors.toList());
    Assertions.assertThat(refs).as("refs grupos").containsExactlyInAnyOrder("34123", "33939");
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
  void findRelacionesByProyectoSgeRefInvestigador_refPropia_returnsRelaciones() throws Exception {
    // given: "user" es IP del proyecto 1 (proyecto-sge-ref-001)
    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_INVESTIGADOR_PROYECTO_SGE_REF + "/proyecto-sge-ref-001").build()
        .toUri();

    // when: se recuperan las relaciones de un proyecto SGE propio
    final ResponseEntity<List<RelacionEjecucionEconomica>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, "user", ROLES_INVESTIGADOR),
        new ParameterizedTypeReference<List<RelacionEjecucionEconomica>>() {
        });

    // then: se devuelven las relaciones de ese proyecto SGE
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    List<String> refs = response.getBody().stream().map(RelacionEjecucionEconomica::getProyectoSgeRef)
        .collect(Collectors.toList());
    Assertions.assertThat(refs).as("refs").containsExactly("proyecto-sge-ref-001");
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
  void findRelacionesByProyectoSgeRefInvestigador_refAjena_returns403() throws Exception {
    // given: "ajeno" no participa en el proyecto-sge-ref-001
    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_INVESTIGADOR_PROYECTO_SGE_REF + "/proyecto-sge-ref-001").build()
        .toUri();

    // when: se intentan recuperar las relaciones de un proyecto SGE ajeno
    final ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, "ajeno", ROLES_INVESTIGADOR), new ParameterizedTypeReference<Object>() {
        });

    // then: el acceso esta prohibido
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

}
