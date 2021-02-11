package org.crue.hercules.sgi.csp.integration;

import java.time.LocalDateTime;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.TipoFase;
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
 * Test de integracion de ConvocatoriaFase.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConvocatoriaFaseIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriafases";

  private HttpEntity<ConvocatoriaFase> buildRequest(HttpHeaders headers, ConvocatoriaFase entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CENTGES-C", "CSP-CENTGES-V", "CSP-CONV-C")));

    HttpEntity<ConvocatoriaFase> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsConvocatoriaFase() throws Exception {

    // given: new ConvocatoriaFase
    ConvocatoriaFase newConvocatoriaFase = generarMockConvocatoriaFase(null);

    // when: create ConvocatoriaFase
    final ResponseEntity<ConvocatoriaFase> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, newConvocatoriaFase), ConvocatoriaFase.class);

    // then: new ConvocatoriaFase is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ConvocatoriaFase responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(newConvocatoriaFase.getConvocatoria().getId());
    Assertions.assertThat(responseData.getFechaInicio()).as("getFechaIncio()")
        .isEqualTo(newConvocatoriaFase.getFechaInicio());
    Assertions.assertThat(responseData.getFechaFin()).as("getFechaFin()").isEqualTo(newConvocatoriaFase.getFechaFin());
    Assertions.assertThat(responseData.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(newConvocatoriaFase.getTipoFase().getId());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsConvocatoriaFase() throws Exception {
    Long idConvocatoriaFase = 1L;
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);

    final ResponseEntity<ConvocatoriaFase> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, convocatoriaFase), ConvocatoriaFase.class, idConvocatoriaFase);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaFase convocatoriaFaseActualizado = response.getBody();
    Assertions.assertThat(convocatoriaFaseActualizado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaFaseActualizado.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(convocatoriaFase.getConvocatoria().getId());
    Assertions.assertThat(convocatoriaFaseActualizado.getObservaciones()).as("getObservacion()")
        .isEqualTo(convocatoriaFase.getObservaciones());
    Assertions.assertThat(convocatoriaFaseActualizado.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(convocatoriaFase.getTipoFase().getId());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    // given: existing ConvocatoriaFase to be deleted
    Long id = 1L;

    // when: delete ConvocatoriaFase
    final ResponseEntity<ConvocatoriaFase> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), ConvocatoriaFase.class, id);

    // then: ConvocatoriaFase deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsConvocatoriaFase() throws Exception {
    Long idConvocatoriaFase = 1L;

    final ResponseEntity<ConvocatoriaFase> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ConvocatoriaFase.class, idConvocatoriaFase);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaFase convocatoriaFase = response.getBody();
    Assertions.assertThat(convocatoriaFase.getId()).as("getId()").isEqualTo(idConvocatoriaFase);
    Assertions.assertThat(convocatoriaFase.getConvocatoria().getId()).as("getConvocatoria().getId()").isEqualTo(1L);
    Assertions.assertThat(convocatoriaFase.getFechaInicio()).as("getFechaInicio()").isEqualTo("2020-10-18T00:00");
    Assertions.assertThat(convocatoriaFase.getFechaFin()).as("getFechaFin()").isEqualTo("2020-11-01T00:00");
    Assertions.assertThat(convocatoriaFase.getTipoFase().getId()).as("getTipoFase().getId()").isEqualTo(1L);

  }

  /**
   * Función que devuelve un objeto ConvocatoriaFase
   * 
   * @param id id del ConvocatoriaFase
   * @return el objeto ConvocatoriaFase
   */
  private ConvocatoriaFase generarMockConvocatoriaFase(Long id) {
    Convocatoria convocatoria = new Convocatoria();
    convocatoria.setId(id == null ? 1 : id);

    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id == null ? 1 : id);
    tipoFase.setActivo(true);

    ConvocatoriaFase convocatoriaFase = new ConvocatoriaFase();
    convocatoriaFase.setId(id);
    convocatoriaFase.setConvocatoria(convocatoria);
    convocatoriaFase.setFechaInicio(LocalDateTime.of(2020, 10, 19, 17, 18, 19));
    convocatoriaFase.setFechaFin(LocalDateTime.of(2020, 10, 28, 17, 18, 19));
    convocatoriaFase.setTipoFase(tipoFase);
    convocatoriaFase.setObservaciones("observaciones" + id);

    return convocatoriaFase;
  }

}
