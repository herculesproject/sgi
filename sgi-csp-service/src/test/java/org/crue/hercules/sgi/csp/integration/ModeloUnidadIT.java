package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
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
 * Test de integracion de ModeloUnidad.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })

public class ModeloUnidadIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/modelounidades";

  private HttpEntity<ModeloUnidad> buildRequest(HttpHeaders headers, ModeloUnidad entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "CSP-TENL-B", "CSP-TENL-C", "CSP-TENL-E", "CSP-TENL-V")));

    HttpEntity<ModeloUnidad> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsModeloUnidad() throws Exception {

    // given: new ModeloUnidad
    ModeloUnidad modeloUnidad = generarMockModeloUnidad(null, "unidad-1");

    // when: create ModeloUnidad
    final ResponseEntity<ModeloUnidad> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, modeloUnidad), ModeloUnidad.class);

    // then: new ModeloUnidad is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ModeloUnidad modeloUnidadResponse = response.getBody();
    Assertions.assertThat(modeloUnidadResponse).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloUnidadResponse.getId()).as("getId()").isNotNull();
    Assertions.assertThat(modeloUnidadResponse.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloUnidadResponse.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(modeloUnidad.getModeloEjecucion().getId());
    Assertions.assertThat(modeloUnidadResponse.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(modeloUnidad.getUnidadGestionRef());
    Assertions.assertThat(modeloUnidadResponse.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    // given: existing ModeloUnidad to be disabled
    Long id = 1L;

    // when: disable ModeloUnidad
    final ResponseEntity<ModeloUnidad> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), ModeloUnidad.class, id);

    // then: ModeloUnidad is disabled
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsModeloUnidad() throws Exception {
    Long id = 1L;

    final ResponseEntity<ModeloUnidad> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ModeloUnidad.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ModeloUnidad modeloUnidadResponse = response.getBody();

    Assertions.assertThat(modeloUnidadResponse).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloUnidadResponse.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(modeloUnidadResponse.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloUnidadResponse.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(1L);
    Assertions.assertThat(modeloUnidadResponse.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo("unidad-001");
    Assertions.assertThat(modeloUnidadResponse.getActivo()).as("getActivo()").isEqualTo(true);
  }

  /**
   * Función que devuelve un objeto ModeloUnidad
   * 
   * @param id               id del ModeloUnidad
   * @param unidadGestionRef unidadGestionRef
   * @return el objeto ModeloUnidad
   */
  private ModeloUnidad generarMockModeloUnidad(Long id, String unidadGestionRef) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(id);
    modeloUnidad.setModeloEjecucion(modeloEjecucion);
    modeloUnidad.setUnidadGestionRef(unidadGestionRef);
    modeloUnidad.setActivo(true);

    return modeloUnidad;
  }

}
