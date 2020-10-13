package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Plan;
import org.crue.hercules.sgi.csp.model.Programa;
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
 * Test de integracion de Programa.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class ProgramaIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/programas";

  private HttpEntity<Programa> buildRequest(HttpHeaders headers, Programa entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-PLAN-B")));

    HttpEntity<Programa> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsPrograma() throws Exception {
    Programa programa = generarMockPrograma(null, "nombre-002", 1L, 9999L);

    final ResponseEntity<Programa> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, programa), Programa.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    Programa programaCreado = response.getBody();
    Assertions.assertThat(programaCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(programaCreado.getNombre()).as("getNombre()").isEqualTo(programa.getNombre());
    Assertions.assertThat(programaCreado.getDescripcion()).as("getDescripcion()").isEqualTo(programa.getDescripcion());
    Assertions.assertThat(programaCreado.getPlan().getId()).as("getPlan().getId()")
        .isEqualTo(programa.getPlan().getId());
    Assertions.assertThat(programaCreado.getPadre().getId()).as("getPadre().getId()")
        .isEqualTo(programa.getPadre().getId());
    Assertions.assertThat(programaCreado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsPrograma() throws Exception {
    Long idPrograma = 2L;
    Programa programa = generarMockPrograma(idPrograma, "nombre-actualizado", 1L, 1L);

    final ResponseEntity<Programa> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, programa), Programa.class, idPrograma);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Programa programaActualizado = response.getBody();
    Assertions.assertThat(programaActualizado.getId()).as("getId()").isEqualTo(programa.getId());
    Assertions.assertThat(programaActualizado.getNombre()).as("getNombre()").isEqualTo(programa.getNombre());
    Assertions.assertThat(programaActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(programa.getDescripcion());
    Assertions.assertThat(programaActualizado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    Long idPrograma = 1L;

    final ResponseEntity<Programa> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), Programa.class, idPrograma);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsPrograma() throws Exception {
    Long idPrograma = 1L;

    final ResponseEntity<Programa> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), Programa.class, idPrograma);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Programa programa = response.getBody();
    Assertions.assertThat(programa.getId()).as("getId()").isEqualTo(idPrograma);
    Assertions.assertThat(programa.getNombre()).as("getNombre()").isEqualTo("nombre-001");
    Assertions.assertThat(programa.getDescripcion()).as("getNombre()").isEqualTo("descripcion-001");
    Assertions.assertThat(programa.getActivo()).as("getActivo()").isEqualTo(true);
  }

  /**
   * Función que devuelve un objeto Plan
   * 
   * @param id id del Plan
   * @return el objeto Plan
   */
  private Plan generarMockPlan(Long id) {
    return generarMockPlan(id, "nombre-" + String.format("%03d", id));
  }

  /**
   * Función que devuelve un objeto Plan
   * 
   * @param id     id del Plan
   * @param nombre nombre del Plan
   * @return el objeto Plan
   */
  private Plan generarMockPlan(Long id, String nombre) {
    Plan plan = new Plan();
    plan.setId(id);
    plan.setNombre(nombre);
    plan.setDescripcion("descripcion-" + String.format("%03d", id));
    plan.setActivo(true);

    return plan;
  }

  /**
   * Función que devuelve un objeto Programa
   * 
   * @param id id del Programa
   * @return el objeto Programa
   */
  private Programa generarMockPrograma(Long id) {
    return generarMockPrograma(id, "nombre-" + String.format("%03d", id), id, null);
  }

  /**
   * Función que devuelve un objeto Programa
   * 
   * @param id              id del Programa
   * @param nombre          nombre del Programa
   * @param idPlan          id del plan
   * @param idProgramaPadre id del Programa padre
   * @return el objeto Programa
   */
  private Programa generarMockPrograma(Long id, String nombre, Long idPlan, Long idProgramaPadre) {
    Programa programa = new Programa();
    programa.setId(id);
    programa.setNombre(nombre);
    programa.setDescripcion("descripcion-" + String.format("%03d", id));
    programa.setPlan(generarMockPlan(idPlan));
    if (idProgramaPadre != null) {
      programa.setPadre(generarMockPrograma(idProgramaPadre));
    }
    programa.setActivo(true);

    return programa;
  }

}
