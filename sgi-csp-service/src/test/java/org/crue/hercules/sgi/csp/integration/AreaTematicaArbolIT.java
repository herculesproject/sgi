package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.AreaTematicaArbol;
import org.crue.hercules.sgi.csp.model.ListadoAreaTematica;
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
 * Test de integracion de AreaTematicaArbol.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class AreaTematicaArbolIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/areatematicaarboles";

  private HttpEntity<AreaTematicaArbol> buildRequest(HttpHeaders headers, AreaTematicaArbol entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-PLAN-B")));

    HttpEntity<AreaTematicaArbol> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsAreaTematicaArbol() throws Exception {
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(null, "nombre-002", 1L, 9999L);

    final ResponseEntity<AreaTematicaArbol> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, areaTematicaArbol), AreaTematicaArbol.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    AreaTematicaArbol areaTematicaArbolCreado = response.getBody();
    Assertions.assertThat(areaTematicaArbolCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(areaTematicaArbolCreado.getNombre()).as("getNombre()")
        .isEqualTo(areaTematicaArbol.getNombre());
    Assertions.assertThat(areaTematicaArbolCreado.getAbreviatura()).as("getAbreviatura()")
        .isEqualTo(areaTematicaArbol.getAbreviatura());
    Assertions.assertThat(areaTematicaArbolCreado.getListadoAreaTematica().getId())
        .as("getListadoAreaTematica().getId()").isEqualTo(areaTematicaArbol.getListadoAreaTematica().getId());
    Assertions.assertThat(areaTematicaArbolCreado.getPadre().getId()).as("getPadre().getId()")
        .isEqualTo(areaTematicaArbol.getPadre().getId());
    Assertions.assertThat(areaTematicaArbolCreado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsAreaTematicaArbol() throws Exception {
    Long idAreaTematicaArbol = 2L;
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(idAreaTematicaArbol, "nombre-actualizado", 1L,
        1L);

    final ResponseEntity<AreaTematicaArbol> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, areaTematicaArbol), AreaTematicaArbol.class, idAreaTematicaArbol);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    AreaTematicaArbol areaTematicaArbolActualizado = response.getBody();
    Assertions.assertThat(areaTematicaArbolActualizado.getId()).as("getId()").isEqualTo(areaTematicaArbol.getId());
    Assertions.assertThat(areaTematicaArbolActualizado.getNombre()).as("getNombre()")
        .isEqualTo(areaTematicaArbol.getNombre());
    Assertions.assertThat(areaTematicaArbolActualizado.getAbreviatura()).as("getAbreviatura()")
        .isEqualTo(areaTematicaArbol.getAbreviatura());
    Assertions.assertThat(areaTematicaArbolActualizado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    Long idAreaTematicaArbol = 1L;

    final ResponseEntity<AreaTematicaArbol> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), AreaTematicaArbol.class, idAreaTematicaArbol);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsAreaTematicaArbol() throws Exception {
    Long idAreaTematicaArbol = 1L;

    final ResponseEntity<AreaTematicaArbol> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), AreaTematicaArbol.class, idAreaTematicaArbol);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    AreaTematicaArbol areaTematicaArbol = response.getBody();
    Assertions.assertThat(areaTematicaArbol.getId()).as("getId()").isEqualTo(idAreaTematicaArbol);
    Assertions.assertThat(areaTematicaArbol.getNombre()).as("getNombre()").isEqualTo("nombre-001");
    Assertions.assertThat(areaTematicaArbol.getAbreviatura()).as("getAbreviatura()").isEqualTo("A-1");
    Assertions.assertThat(areaTematicaArbol.getActivo()).as("getActivo()").isEqualTo(true);
  }

  /**
   * Función que devuelve un objeto ListadoAreaTematica
   * 
   * @param id id del ListadoAreaTematica
   * @return el objeto ListadoAreaTematica
   */
  private ListadoAreaTematica generarMockListadoAreaTematica(Long id) {
    return generarMockListadoAreaTematica(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto ListadoAreaTematica
   * 
   * @param id     id del ListadoAreaTematica
   * @param nombre nombre del ListadoAreaTematica
   * @return el objeto ListadoAreaTematica
   */
  private ListadoAreaTematica generarMockListadoAreaTematica(Long id, String nombre) {
    ListadoAreaTematica listadoAreaTematica = new ListadoAreaTematica();
    listadoAreaTematica.setId(id);
    listadoAreaTematica.setNombre(nombre);
    listadoAreaTematica.setDescripcion("descripcion-" + id);
    listadoAreaTematica.setActivo(true);

    return listadoAreaTematica;
  }

  /**
   * Función que devuelve un objeto AreaTematicaArbol
   * 
   * @param id id del AreaTematicaArbol
   * @return el objeto AreaTematicaArbol
   */
  private AreaTematicaArbol generarMockAreaTematicaArbol(Long id) {
    return generarMockAreaTematicaArbol(id, "nombre-" + id, id, null);
  }

  /**
   * Función que devuelve un objeto AreaTematicaArbol
   * 
   * @param id                       id del AreaTematicaArbol
   * @param nombre                   nombre del AreaTematicaArbol
   * @param idListadoAreaTematica    id del ListadoAreaTematica
   * @param idAreaTematicaArbolPadre id del AreaTematicaArbol padre
   * @return el objeto AreaTematicaArbol
   */
  private AreaTematicaArbol generarMockAreaTematicaArbol(Long id, String nombre, Long idListadoAreaTematica,
      Long idAreaTematicaArbolPadre) {
    AreaTematicaArbol areaTematicaArbol = new AreaTematicaArbol();
    areaTematicaArbol.setId(id);
    areaTematicaArbol.setNombre(nombre);
    areaTematicaArbol.setAbreviatura("A-" + (id == null ? 0 : id));
    areaTematicaArbol.setListadoAreaTematica(generarMockListadoAreaTematica(idListadoAreaTematica));
    if (idAreaTematicaArbolPadre != null) {
      areaTematicaArbol.setPadre(generarMockAreaTematicaArbol(idAreaTematicaArbolPadre));
    }
    areaTematicaArbol.setActivo(true);

    return areaTematicaArbol;
  }

}
