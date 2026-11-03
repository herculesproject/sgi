package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
 * Test de integracion de TipoRegimenConcurrencia.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TipoRegimenConcurrenciaIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/tiporegimenconcurrencias";

  private HttpEntity<TipoRegimenConcurrencia> buildRequest(HttpHeaders headers, TipoRegimenConcurrencia entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s",
            tokenBuilder.buildToken("user", "AUTH", "CSP-TRCO-C", "CSP-TRCO-V", "CSP-TRCO-E", "CSP-TRCO-R",
                "CSP-CON-C")));

    return new HttpEntity<>(entity, headers);
  }

  private HttpEntity<TipoRegimenConcurrencia> buildRequestWithRoles(String... roles) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    return new HttpEntity<>(null, headers);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsTipoRegimenConcurrencia() throws Exception {
    Long id = 1L;

    final ResponseEntity<TipoRegimenConcurrencia> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        TipoRegimenConcurrencia.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TipoRegimenConcurrencia responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(I18nHelper.getValueForLanguage(responseData.getNombre(), Language.ES)).as("getNombre()")
        .isEqualTo("nombre-1");
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_regimen_concurrencia.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @ValueSource(strings = { "CSP-PRO-V", "CSP-PRO-V_1", "CSP-PRO-E_1" })
  void findById_WithAuthorityProyecto_ReturnsTipoRegimenConcurrencia(String authority) throws Exception {
    // given: TipoRegimenConcurrencia existente y token solo con una autoridad de
    // proyectos
    Long id = 1L;

    // when: findById
    final ResponseEntity<TipoRegimenConcurrencia> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequestWithRoles(authority),
        TipoRegimenConcurrencia.class, id);

    // then: 200 OK con el TipoRegimenConcurrencia solicitado
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(I18nHelper.getValueForLanguage(response.getBody().getNombre(), Language.ES))
        .as("getNombre()").isEqualTo("nombre-001");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_regimen_concurrencia.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_WithoutAuthority_ReturnsForbidden() throws Exception {
    // given: TipoRegimenConcurrencia existente y token solo con CSP-PRO-INV-VR, sin
    // ninguna autoridad admitida
    Long id = 1L;

    // when: findById
    final ResponseEntity<TipoRegimenConcurrencia> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequestWithRoles("CSP-PRO-INV-VR"),
        TipoRegimenConcurrencia.class, id);

    // then: 403 Forbidden
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsTipoRegimenConcurrenciaSubList() throws Exception {

    // given: data for TipoRegimenConcurrencia

    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";
    String filter = "nombre.value=ke=-0";

    // when: find TipoRegimenConcurrencia
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter)
        .build(false).toUri();
    final ResponseEntity<List<TipoRegimenConcurrencia>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<TipoRegimenConcurrencia>>() {
        });

    // given: TipoRegimenConcurrencia data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoRegimenConcurrencia> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(I18nHelper.getValueForLanguage(responseData.get(0).getNombre(), Language.ES))
        .as("get(0).getNombre())").isEqualTo("nombre-03");
    Assertions.assertThat(I18nHelper.getValueForLanguage(responseData.get(1).getNombre(), Language.ES))
        .as("get(1).getNombre())").isEqualTo("nombre-02");
    Assertions.assertThat(I18nHelper.getValueForLanguage(responseData.get(2).getNombre(),
        Language.ES)).as("get(2).getNombre())").isEqualTo("nombre-01");
  }
}
