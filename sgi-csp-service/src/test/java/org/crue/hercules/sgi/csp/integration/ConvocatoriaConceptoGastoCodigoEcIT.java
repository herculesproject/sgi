package org.crue.hercules.sgi.csp.integration;

import java.time.LocalDate;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
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
 * Test de integracion de ConvocatoriaConceptoGastoCodigoEc.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })

public class ConvocatoriaConceptoGastoCodigoEcIT {
  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaconceptogastocodigoecs";

  private HttpEntity<ConvocatoriaConceptoGastoCodigoEc> buildRequest(HttpHeaders headers,
      ConvocatoriaConceptoGastoCodigoEc entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "CSP-CGAS-B", "CSP-CGAS-C", "CSP-CGAS-E", "CSP-CGAS-V")));

    HttpEntity<ConvocatoriaConceptoGastoCodigoEc> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsConvocatoriaConceptoGastoCodigoEc() throws Exception {
    // given: new ConvocatoriaConceptoGastoCodigoEc
    ConvocatoriaConceptoGastoCodigoEc newConvocatoriaConceptoGastoCodigoEc = generarMockConvocatoriaConceptoGastoCodigoEc(
        null, true);
    // when: create ConvocatoriaConceptoGastoCodigoEc
    final ResponseEntity<ConvocatoriaConceptoGastoCodigoEc> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, newConvocatoriaConceptoGastoCodigoEc),
        ConvocatoriaConceptoGastoCodigoEc.class);

    // then: new ConvocatoriaConceptoGastoCodigoEc is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ConvocatoriaConceptoGastoCodigoEc responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getConvocatoriaConceptoGasto().getId())
        .as("getConvocatoriaConceptoGasto().getId()")
        .isEqualTo(newConvocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto().getId());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsConvocatoriaConceptoGastoCodigoEc() throws Exception {
    Long idConvocatoriaConceptoGastoCodigoEc = 1L;
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGasto = generarMockConvocatoriaConceptoGastoCodigoEc(
        idConvocatoriaConceptoGastoCodigoEc, true);

    final ResponseEntity<ConvocatoriaConceptoGastoCodigoEc> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, convocatoriaConceptoGasto),
        ConvocatoriaConceptoGastoCodigoEc.class, idConvocatoriaConceptoGastoCodigoEc);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoActualizado = response.getBody();
    Assertions.assertThat(convocatoriaConceptoGastoActualizado.getId()).as("getId()").isNotNull();

    Assertions.assertThat(convocatoriaConceptoGastoActualizado.getConvocatoriaConceptoGasto().getId())
        .as("getConvocatoriaConceptoGasto().getId()")
        .isEqualTo(convocatoriaConceptoGasto.getConvocatoriaConceptoGasto().getId());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    Long idConvocatoriaConceptoGastoCodigoEc = 1L;

    final ResponseEntity<ConvocatoriaConceptoGastoCodigoEc> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        ConvocatoriaConceptoGastoCodigoEc.class, idConvocatoriaConceptoGastoCodigoEc);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsConvocatoriaConceptoGastoCodigoEc() throws Exception {
    Long idConvocatoriaConceptoGastoCodigoEc = 1L;

    final ResponseEntity<ConvocatoriaConceptoGastoCodigoEc> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ConvocatoriaConceptoGastoCodigoEc.class, idConvocatoriaConceptoGastoCodigoEc);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGasto = response.getBody();
    Assertions.assertThat(convocatoriaConceptoGasto.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaConceptoGasto.getConvocatoriaConceptoGasto().getId())
        .as("getConvocatoria().getId()").isEqualTo(1L);
  }

  /**
   * Función que devuelve un objeto ConvocatoriaConceptoGastoCodigoEc
   * 
   * @param id        id del ConvocatoriaConceptoGastoCodigoEc
   * @param permitido boolean permitido
   * @return el objeto ConvocatoriaConceptoGastoCodigoEc
   */
  private ConvocatoriaConceptoGastoCodigoEc generarMockConvocatoriaConceptoGastoCodigoEc(Long id, Boolean permitido) {

    Convocatoria convocatoria = new Convocatoria();
    convocatoria.setId(id == null ? 1 : id);

    ConceptoGasto conceptoGasto = new ConceptoGasto();
    conceptoGasto.setId(id == null ? 1 : id);
    conceptoGasto.setActivo(true);
    conceptoGasto.setDescripcion("descripcion-00" + (id == null ? 1 : id));
    conceptoGasto.setNombre("nombre-00" + (id == null ? 1 : id));

    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = new ConvocatoriaConceptoGasto();
    convocatoriaConceptoGasto.setId((id == null ? 1 : id));
    convocatoriaConceptoGasto.setConvocatoria(convocatoria);
    convocatoriaConceptoGasto.setConceptoGasto(conceptoGasto);
    convocatoriaConceptoGasto.setPermitido(permitido);

    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = new ConvocatoriaConceptoGastoCodigoEc();
    convocatoriaConceptoGastoCodigoEc.setId(id);
    convocatoriaConceptoGastoCodigoEc.setConvocatoriaConceptoGasto(convocatoriaConceptoGasto);
    convocatoriaConceptoGastoCodigoEc.setCodigoEconomicoRef("cod-" + (id == null ? 1 : id));
    convocatoriaConceptoGastoCodigoEc.setFechaInicio(LocalDate.now());
    convocatoriaConceptoGastoCodigoEc.setFechaFin(LocalDate.now());

    return convocatoriaConceptoGastoCodigoEc;
  }
}
