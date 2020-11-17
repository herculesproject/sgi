package org.crue.hercules.sgi.csp.integration;

import java.time.LocalDate;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.csp.model.TipoHito;
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
 * Test de integracion de ConvocatoriaHito.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConvocatoriaHitoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriahitos";

  private HttpEntity<ConvocatoriaHito> buildRequest(HttpHeaders headers, ConvocatoriaHito entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "CSP-CHIT-B", "CSP-CHIT-C", "CSP-CHIT-E", "CSP-CHIT-V")));

    HttpEntity<ConvocatoriaHito> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsConvocatoriaHito() throws Exception {
    // given: new ConvocatoriaHito
    ConvocatoriaHito newConvocatoriaHito = generarMockConvocatoriaHito(null);
    // when: create ConvocatoriaHito
    final ResponseEntity<ConvocatoriaHito> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, newConvocatoriaHito), ConvocatoriaHito.class);

    // then: new ConvocatoriaHito is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ConvocatoriaHito responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(newConvocatoriaHito.getConvocatoria().getId());
    Assertions.assertThat(responseData.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(newConvocatoriaHito.getTipoHito().getId());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsConvocatoriaHito() throws Exception {
    Long idConvocatoriaHito = 1L;
    ConvocatoriaHito convocatoriaHito = generarMockConvocatoriaHito(1L);

    final ResponseEntity<ConvocatoriaHito> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, convocatoriaHito), ConvocatoriaHito.class, idConvocatoriaHito);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaHito convocatoriaHitoActualizado = response.getBody();
    Assertions.assertThat(convocatoriaHitoActualizado.getId()).as("getId()").isNotNull();

    Assertions.assertThat(convocatoriaHitoActualizado.getConvocatoria().getId()).as("getConvocatoria()")
        .isEqualTo(convocatoriaHito.getConvocatoria().getId());
    Assertions.assertThat(convocatoriaHitoActualizado.getFecha()).as("getFecha()")
        .isEqualTo(convocatoriaHito.getFecha());
    Assertions.assertThat(convocatoriaHitoActualizado.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(convocatoriaHito.getTipoHito().getId());
    Assertions.assertThat(convocatoriaHitoActualizado.getComentario()).as("getComentario()")
        .isEqualTo(convocatoriaHito.getComentario());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    Long idConvocatoriaHito = 1L;

    final ResponseEntity<ConvocatoriaHito> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), ConvocatoriaHito.class, idConvocatoriaHito);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsConvocatoriaHito() throws Exception {
    Long idConvocatoriaHito = 1L;

    final ResponseEntity<ConvocatoriaHito> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ConvocatoriaHito.class, idConvocatoriaHito);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaHito convocatoriaHito = response.getBody();
    Assertions.assertThat(convocatoriaHito.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaHito.getConvocatoria().getId()).as("getConvocatoria().getId()").isEqualTo(1L);
    Assertions.assertThat(convocatoriaHito.getComentario()).as("comentario")
        .isEqualTo(convocatoriaHito.getComentario());
    Assertions.assertThat(convocatoriaHito.getFecha()).as("getFecha()").isEqualTo("2021-10-22");

  }

  /**
   * Función que devuelve un objeto ConvocatoriaHito
   * 
   * @param id id del ConvocatoriaHito
   * @return el objeto ConvocatoriaHito
   */
  private ConvocatoriaHito generarMockConvocatoriaHito(Long id) {
    Convocatoria convocatoria = new Convocatoria();
    convocatoria.setId(id == null ? 1 : id);

    TipoHito tipoHito = new TipoHito();
    tipoHito.setId(id == null ? 1 : id);
    tipoHito.setActivo(true);

    ConvocatoriaHito convocatoriaHito = new ConvocatoriaHito();
    convocatoriaHito.setId(id);
    convocatoriaHito.setConvocatoria(convocatoria);
    convocatoriaHito.setFecha(LocalDate.of(2020, 10, 19));
    convocatoriaHito.setComentario("comentario" + id);
    convocatoriaHito.setGeneraAviso(true);
    convocatoriaHito.setTipoHito(tipoHito);

    return convocatoriaHito;
  }
}
