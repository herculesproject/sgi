package org.crue.hercules.sgi.csp.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
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
 * Test de integracion de ConvocatoriaConceptoGasto.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConvocatoriaConceptoGastoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaconceptogastos";

  private HttpEntity<ConvocatoriaConceptoGasto> buildRequest(HttpHeaders headers, ConvocatoriaConceptoGasto entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "CSP-CGAS-B", "CSP-CGAS-C", "CSP-CGAS-E", "CSP-CGAS-V", "CSP-CONV-C")));

    HttpEntity<ConvocatoriaConceptoGasto> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsConvocatoriaConceptoGasto() throws Exception {
    // given: new ConvocatoriaConceptoGasto
    ConvocatoriaConceptoGasto newConvocatoriaConceptoGasto = generarMockConvocatoriaConceptoGasto(null, true);
    // when: create ConvocatoriaConceptoGasto
    final ResponseEntity<ConvocatoriaConceptoGasto> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, newConvocatoriaConceptoGasto), ConvocatoriaConceptoGasto.class);

    // then: new ConvocatoriaConceptoGasto is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ConvocatoriaConceptoGasto responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(newConvocatoriaConceptoGasto.getConvocatoria().getId());
    Assertions.assertThat(responseData.getConceptoGasto().getId()).as("getConceptoGasto().getId()")
        .isEqualTo(newConvocatoriaConceptoGasto.getConceptoGasto().getId());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsConvocatoriaConceptoGasto() throws Exception {
    Long idConvocatoriaConceptoGasto = 1L;
    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = generarMockConvocatoriaConceptoGasto(
        idConvocatoriaConceptoGasto, true);

    final ResponseEntity<ConvocatoriaConceptoGasto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, convocatoriaConceptoGasto),
        ConvocatoriaConceptoGasto.class, idConvocatoriaConceptoGasto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaConceptoGasto convocatoriaConceptoGastoActualizado = response.getBody();
    Assertions.assertThat(convocatoriaConceptoGastoActualizado.getId()).as("getId()").isNotNull();

    Assertions.assertThat(convocatoriaConceptoGastoActualizado.getConvocatoria().getId()).as("getConvocatoria()")
        .isEqualTo(convocatoriaConceptoGasto.getConvocatoria().getId());
    Assertions.assertThat(convocatoriaConceptoGastoActualizado.getConceptoGasto().getId())
        .as("getConceptoGasto().getId()").isEqualTo(convocatoriaConceptoGasto.getConceptoGasto().getId());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    Long idConvocatoriaConceptoGasto = 1L;

    final ResponseEntity<ConvocatoriaConceptoGasto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        ConvocatoriaConceptoGasto.class, idConvocatoriaConceptoGasto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsConvocatoriaConceptoGasto() throws Exception {
    Long idConvocatoriaConceptoGasto = 1L;

    final ResponseEntity<ConvocatoriaConceptoGasto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ConvocatoriaConceptoGasto.class, idConvocatoriaConceptoGasto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = response.getBody();
    Assertions.assertThat(convocatoriaConceptoGasto.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaConceptoGasto.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(1L);
  }

  /**
   * Función que devuelve un objeto ConvocatoriaConceptoGasto
   * 
   * @param id        id del ConvocatoriaConceptoGasto
   * @param permitido boolean permitido
   * @return el objeto ConvocatoriaConceptoGasto
   */
  private ConvocatoriaConceptoGasto generarMockConvocatoriaConceptoGasto(Long id, Boolean permitido) {

    Convocatoria convocatoria = new Convocatoria();
    convocatoria.setId(id == null ? 1 : id);

    ConceptoGasto conceptoGasto = new ConceptoGasto();
    conceptoGasto.setId(id == null ? 1 : id);
    conceptoGasto.setActivo(true);
    conceptoGasto.setDescripcion("descripcion-00" + (id == null ? 1 : id));
    conceptoGasto.setNombre("nombre-00" + (id == null ? 1 : id));

    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = new ConvocatoriaConceptoGasto();
    convocatoriaConceptoGasto.setId(id);
    convocatoriaConceptoGasto.setConvocatoria(convocatoria);
    convocatoriaConceptoGasto.setConceptoGasto(conceptoGasto);
    convocatoriaConceptoGasto.setPermitido(permitido);

    return convocatoriaConceptoGasto;
  }
}
