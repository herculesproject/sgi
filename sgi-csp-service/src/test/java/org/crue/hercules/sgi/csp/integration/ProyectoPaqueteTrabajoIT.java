package org.crue.hercules.sgi.csp.integration;

import java.time.LocalDate;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de ProyectoPaqueteTrabajo.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProyectoPaqueteTrabajoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectopaquetetrabajos";

  private HttpEntity<ProyectoPaqueteTrabajo> buildRequest(HttpHeaders headers, ProyectoPaqueteTrabajo entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "CSP-PRO-B", "CSP-PRO-C", "CSP-PRO-E", "CSP-PRO-V")));

    HttpEntity<ProyectoPaqueteTrabajo> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/proyecto.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsProyectoPaqueteTrabajo() throws Exception {
    // given: new ProyectoPaqueteTrabajo
    ProyectoPaqueteTrabajo newProyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    newProyectoPaqueteTrabajo.setId(null);

    // when: create ProyectoPaqueteTrabajo
    final ResponseEntity<ProyectoPaqueteTrabajo> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, newProyectoPaqueteTrabajo), ProyectoPaqueteTrabajo.class);

    // then: new ProyectoPaqueteTrabajo is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ProyectoPaqueteTrabajo responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getProyecto().getId()).as("getProyecto().getId()")
        .isEqualTo(newProyectoPaqueteTrabajo.getProyecto().getId());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(newProyectoPaqueteTrabajo.getNombre());
    Assertions.assertThat(responseData.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(newProyectoPaqueteTrabajo.getFechaInicio());
    Assertions.assertThat(responseData.getFechaFin()).as("getFechaFin()")
        .isEqualTo(newProyectoPaqueteTrabajo.getFechaFin());
    Assertions.assertThat(responseData.getPersonaMes()).as("getPersonaMes()")
        .isEqualTo(newProyectoPaqueteTrabajo.getPersonaMes());
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()")
        .isEqualTo(newProyectoPaqueteTrabajo.getDescripcion());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/proyecto.sql", "classpath:scripts/proyecto_paquete_trabajo.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsProyectoPaqueteTrabajo() throws Exception {
    Long idProyectoPaqueteTrabajo = 1L;
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setDescripcion("descripcion modificada");

    final ResponseEntity<ProyectoPaqueteTrabajo> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, proyectoPaqueteTrabajo),
        ProyectoPaqueteTrabajo.class, idProyectoPaqueteTrabajo);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoPaqueteTrabajo proyectoPaqueteTrabajoActualizado = response.getBody();
    Assertions.assertThat(proyectoPaqueteTrabajoActualizado.getId()).as("getId()").isEqualTo(idProyectoPaqueteTrabajo);
    Assertions.assertThat(proyectoPaqueteTrabajoActualizado.getProyecto().getId()).as("getProyecto().getId()")
        .isEqualTo(1L);
    Assertions.assertThat(proyectoPaqueteTrabajoActualizado.getNombre()).as("getNombre()")
        .isEqualTo("proyecto-paquete-trabajo-001");
    Assertions.assertThat(proyectoPaqueteTrabajoActualizado.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(LocalDate.of(2020, 01, 01));
    Assertions.assertThat(proyectoPaqueteTrabajoActualizado.getFechaFin()).as("getFechaFin()")
        .isEqualTo(LocalDate.of(2020, 01, 15));
    Assertions.assertThat(proyectoPaqueteTrabajoActualizado.getPersonaMes()).as("getPersonaMes()").isEqualTo(1D);
    Assertions.assertThat(proyectoPaqueteTrabajoActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(proyectoPaqueteTrabajo.getDescripcion());

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/proyecto.sql", "classpath:scripts/proyecto_paquete_trabajo.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    Long idProyectoPaqueteTrabajo = 1L;

    final ResponseEntity<ProyectoPaqueteTrabajo> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        ProyectoPaqueteTrabajo.class, idProyectoPaqueteTrabajo);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/proyecto.sql", "classpath:scripts/proyecto_paquete_trabajo.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void existsById_Returns200() throws Exception {
    // given: existing id
    Long id = 1L;
    // when: exists by id
    final ResponseEntity<Proyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.HEAD, buildRequest(null, null), Proyecto.class, id);
    // then: 200 OK
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void existsById_Returns204() throws Exception {
    // given: no existing id
    Long id = 1L;
    // when: exists by id
    final ResponseEntity<Proyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.HEAD, buildRequest(null, null), Proyecto.class, id);
    // then: 204 No Content
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/proyecto.sql", "classpath:scripts/proyecto_paquete_trabajo.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsProyectoPaqueteTrabajo() throws Exception {
    Long idProyectoPaqueteTrabajo = 1L;

    final ResponseEntity<ProyectoPaqueteTrabajo> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ProyectoPaqueteTrabajo.class, idProyectoPaqueteTrabajo);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = response.getBody();
    Assertions.assertThat(proyectoPaqueteTrabajo.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoPaqueteTrabajo.getId()).as("getId()").isEqualTo(idProyectoPaqueteTrabajo);
    Assertions.assertThat(proyectoPaqueteTrabajo.getProyecto().getId()).as("getProyecto().getId()").isEqualTo(1L);
    Assertions.assertThat(proyectoPaqueteTrabajo.getNombre()).as("getNombre()")
        .isEqualTo("proyecto-paquete-trabajo-001");
    Assertions.assertThat(proyectoPaqueteTrabajo.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(LocalDate.of(2020, 01, 01));
    Assertions.assertThat(proyectoPaqueteTrabajo.getFechaFin()).as("getFechaFin()")
        .isEqualTo(LocalDate.of(2020, 01, 15));
    Assertions.assertThat(proyectoPaqueteTrabajo.getPersonaMes()).as("getPersonaMes()").isEqualTo(1D);
    Assertions.assertThat(proyectoPaqueteTrabajo.getDescripcion()).as("getDescripcion()")
        .isEqualTo("descripcion-proyecto-equipo-trabajo-001");

  }

  /**
   * Función que devuelve un objeto ProyectoPaqueteTrabajo
   * 
   * @param id         id del ProyectoPaqueteTrabajo
   * @param proyectoId id del Proyecto
   * @return el objeto ProyectoPaqueteTrabajo
   */
  private ProyectoPaqueteTrabajo generarMockProyectoPaqueteTrabajo(Long id, Long proyectoId) {

    return ProyectoPaqueteTrabajo.builder()//
        .id(id)//
        .proyecto(Proyecto.builder().id(proyectoId).build())//
        .nombre("proyecto-paquete-trabajo-" + (id == null ? "" : String.format("%03d", id)))//
        .fechaInicio(LocalDate.of(2020, 01, 01))//
        .fechaFin(LocalDate.of(2020, 01, 15))//
        .personaMes(1D)//
        .descripcion("descripcion-proyecto-paquete-trabajo-" + (id == null ? "" : String.format("%03d", id)))//
        .build();
  }
}
