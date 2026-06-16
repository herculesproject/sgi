package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.RolProyecto;
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
 * Test de integracion de ProyectoEquipo.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProyectoEquipoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectoequipos";
  private List<ProyectoEquipo> proyectoEquipos;

  private HttpEntity<ProyectoEquipo> buildRequest(HttpHeaders headers, ProyectoEquipo entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH")));

    return new HttpEntity<>(entity, headers);
  }

  private HttpEntity<List<ProyectoEquipo>> buildRequestList(HttpHeaders headers, List<ProyectoEquipo> entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-PRO-E")));

    return new HttpEntity<>(entity, headers);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_equipo.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void updateProyectoEquiposConvocatoria_ReturnsProyectoEquipoList() throws Exception {

    // given: una lista con uno de los ProyectoEquipo
    // actualizado,
    // otro nuevo y sin los otros 3 periodos existentes
    Long proyectoId = 1L;
    ProyectoEquipo newProyectoEquipo = generarMockProyectoEquipo(null, Instant.parse("2020-12-16T00:00:00Z"),
        Instant.parse("2020-12-18T23:59:59Z"), 1L);
    ProyectoEquipo updatedProyectoEquipo = generarMockProyectoEquipo(103L, Instant.parse("2020-04-02T00:00:00Z"),
        Instant.parse("2020-04-15T23:59:59Z"), 1L);

    proyectoEquipos = Arrays.asList(newProyectoEquipo, updatedProyectoEquipo);

    // when: updateProyectoEquiposConvocatoria
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID).buildAndExpand(proyectoId)
        .toUri();

    final ResponseEntity<List<ProyectoEquipo>> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildRequestList(null, proyectoEquipos), new ParameterizedTypeReference<List<ProyectoEquipo>>() {
        });

    // then: Se crea el nuevo ProyectoEquipo, se actualiza
    // el
    // existe y se eliminan los otros
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    List<ProyectoEquipo> responseData = response.getBody();
    Assertions.assertThat(responseData.get(0).getId()).as("get(0).getId()").isEqualTo(updatedProyectoEquipo.getId());
    Assertions.assertThat(responseData.get(0).getProyectoId()).as("get(0).getProyectoId()").isEqualTo(proyectoId);
    Assertions.assertThat(responseData.get(0).getFechaInicio()).as("get(0).getFechaInicio()")
        .isEqualTo(updatedProyectoEquipo.getFechaInicio());
    Assertions.assertThat(responseData.get(0).getFechaFin()).as("get(0).getFechaFin()")
        .isEqualTo(updatedProyectoEquipo.getFechaFin());
    Assertions.assertThat(responseData.get(0).getPersonaRef()).as("get(0).getPersonaRef()")
        .isEqualTo(updatedProyectoEquipo.getPersonaRef());
    Assertions.assertThat(responseData.get(0).getRolProyecto().getId()).as("get(0).getRolProyecto().getId()")
        .isEqualTo(updatedProyectoEquipo.getRolProyecto().getId());

    Assertions.assertThat(responseData.get(1).getProyectoId()).as("get(1).getProyectoId()").isEqualTo(proyectoId);
    Assertions.assertThat(responseData.get(1).getFechaInicio()).as("get(1).getFechaInicio()")
        .isEqualTo(newProyectoEquipo.getFechaInicio());
    Assertions.assertThat(responseData.get(1).getFechaFin()).as("get(1).getFechaFin()")
        .isEqualTo(newProyectoEquipo.getFechaFin());
    Assertions.assertThat(responseData.get(1).getPersonaRef()).as("get(1).getPersonaRef()")
        .isEqualTo(newProyectoEquipo.getPersonaRef());
    Assertions.assertThat(responseData.get(1).getRolProyecto().getId()).as("get(1).getRolProyecto().getId()")
        .isEqualTo(newProyectoEquipo.getRolProyecto().getId());

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "fechaInicio,asc";

    URI uriFindAllProyectoEquipo = UriComponentsBuilder
        .fromUriString("/proyectos" + PATH_PARAMETER_ID + CONTROLLER_BASE_PATH).queryParam("s", sort)
        .buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ProyectoEquipo>> responseFindAllProyectoEquipo = restTemplate.exchange(
        uriFindAllProyectoEquipo, HttpMethod.GET, buildRequestList(headers, null),
        new ParameterizedTypeReference<List<ProyectoEquipo>>() {
        });

    Assertions.assertThat(responseFindAllProyectoEquipo.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ProyectoEquipo> responseDataFindAll = responseFindAllProyectoEquipo.getBody();
    Assertions.assertThat(responseDataFindAll).as("size()").hasSize(proyectoEquipos.size());
    Assertions.assertThat(responseDataFindAll.get(0).getId()).as("responseDataFindAll.get(0).getId()")
        .isEqualTo(responseData.get(0).getId());
    Assertions.assertThat(responseDataFindAll.get(1).getId()).as("responseDataFindAll.get(1).getId()")
        .isEqualTo(responseData.get(1).getId());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_equipo.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsProyectoEquipo() throws Exception {
    Long id = 100L;

    final ResponseEntity<ProyectoEquipo> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ProyectoEquipo.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ProyectoEquipo responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(responseData.getProyectoId()).as("getProyectoId()").isEqualTo(1);
    Assertions.assertThat(responseData.getPersonaRef()).as("getPersonaRef()").isEqualTo("ref-001");
    Assertions.assertThat(responseData.getRolProyecto().getId()).as("getRolProyecto().getId()").isEqualTo(1);
    Assertions.assertThat(responseData.getFechaInicio()).as("getFechaInicio()").isEqualTo("2020-01-01T00:00:00Z");
    Assertions.assertThat(responseData.getFechaFin()).as("getFechaFin()").isEqualTo("2020-01-15T23:59:59Z");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_equipo.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_WithChanges_PreservaCamposAuditoria() throws Exception {
    // given: un ProyectoEquipo existente con campos de auditoría de creación y
    // una modificación en la fecha de fin
    Long proyectoId = 1L;
    ProyectoEquipo proyectoEquipoActualizar = generarMockProyectoEquipo(106L, Instant.parse("2020-03-01T00:00:00Z"),
        Instant.parse("2020-03-15T23:59:59Z"), proyectoId);

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(proyectoId).toUri();

    // when: PATCH con fechaFin modificada
    final ResponseEntity<List<ProyectoEquipo>> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildRequestList(null, Arrays.asList(proyectoEquipoActualizar)),
        new ParameterizedTypeReference<List<ProyectoEquipo>>() {
        });

    // then: se aplica el cambio y se preservan los campos de auditoría de creación
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    List<ProyectoEquipo> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(1);
    Assertions.assertThat(responseData.get(0).getFechaFin()).as("getFechaFin()")
        .isEqualTo(Instant.parse("2020-03-15T23:59:59Z"));
    Assertions.assertThat(responseData.get(0).getCreatedBy()).as("getCreatedBy()").isEqualTo("test-user");
    Assertions.assertThat(responseData.get(0).getCreationDate()).as("getCreationDate()")
        .isEqualTo(Instant.parse("2020-01-01T00:00:00Z"));
    Assertions.assertThat(responseData.get(0).getLastModifiedBy()).as("getLastModifiedBy()").isEqualTo("user");
    Assertions.assertThat(responseData.get(0).getLastModifiedDate()).as("getLastModifiedDate()")
        .isNotNull()
        .isNotEqualTo(Instant.parse("2020-01-01T00:00:00Z"));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_equipo.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_WithoutChanges_NoActualizaCamposAuditoria() throws Exception {
    // given: un ProyectoEquipo existente sin cambios
    Long proyectoId = 1L;
    ProyectoEquipo proyectoEquipoSinCambios = generarMockProyectoEquipo(106L, Instant.parse("2020-03-01T00:00:00Z"),
        Instant.parse("2020-03-31T23:59:59Z"), proyectoId);

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(proyectoId).toUri();

    // when: PATCH con datos idénticos a los existentes en BD
    final ResponseEntity<List<ProyectoEquipo>> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildRequestList(null, Arrays.asList(proyectoEquipoSinCambios)),
        new ParameterizedTypeReference<List<ProyectoEquipo>>() {
        });

    // then: se devuelve la entidad sin modificar
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    List<ProyectoEquipo> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(1);
    Assertions.assertThat(responseData.get(0).getCreatedBy()).as("getCreatedBy()").isEqualTo("test-user");
    Assertions.assertThat(responseData.get(0).getCreationDate()).as("getCreationDate()")
        .isEqualTo(Instant.parse("2020-01-01T00:00:00Z"));
    Assertions.assertThat(responseData.get(0).getLastModifiedDate()).as("getLastModifiedDate() no debe cambiar")
        .isEqualTo(Instant.parse("2020-01-01T00:00:00Z"));
  }

  /**
   * Función que devuelve un objeto ProyectoEquipo
   *
   * @param id         id del ProyectoEquipo
   * @param mesInicial Mes inicial
   * @param mesFinal   Mes final
   * @param proyectoId Id Proyecto
   * @return el objeto ProyectoEquipo
   */
  private ProyectoEquipo generarMockProyectoEquipo(Long id, Instant fechaInicio, Instant fechaFin, Long proyectoId) {

    return ProyectoEquipo.builder()
        .id(id)
        .proyectoId(proyectoId)
        .rolProyecto(RolProyecto.builder().id(1L).build())
        .fechaInicio(fechaInicio)
        .fechaFin(fechaFin)
        .personaRef("001")
        .build();
  }

}
