package org.crue.hercules.sgi.eti.integration;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.ConfigParamOutput;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;

/**
 * Test de integracion de los parámetros de configuración por clave.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off
  "classpath:scripts/configuracion_param.sql"
// @formatter:on
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
class ConfigIT extends BaseIT {

  private static final String CONFIG_CONTROLLER_BASE_PATH = "/config";
  private static final String PATH_NAME = "/{name}";

  private HttpEntity<String> buildRequest(String value, String... roles) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", "bearer " + tokenBuilder.buildToken("user", roles));
    return new HttpEntity<>(value, headers);
  }

  private ResponseEntity<ConfigParamOutput> updateValue(String name, String value) throws Exception {
    return restTemplate.exchange(CONFIG_CONTROLLER_BASE_PATH + PATH_NAME, HttpMethod.PATCH,
        buildRequest(value, "ADM-CNF-E"), ConfigParamOutput.class, name);
  }

  private String getValue(String name) throws Exception {
    return restTemplate.exchange(CONFIG_CONTROLLER_BASE_PATH + PATH_NAME, HttpMethod.GET,
        buildRequest(null, "ADM-CNF-E"), ConfigParamOutput.class, name).getBody().getValue();
  }

  @Test
  void get_WithParamKey_ReturnsDefaultValue() throws Exception {
    // given: la configuración inicial y un usuario con autoridad de memorias

    // when: se consulta el flag del apartado de unidades de vinculación
    final ResponseEntity<ConfigParamOutput> response = restTemplate.exchange(
        CONFIG_CONTROLLER_BASE_PATH + PATH_NAME, HttpMethod.GET, buildRequest(null, "ETI-MEM-V"),
        ConfigParamOutput.class, "peticionEvaluacionUnidadesVinculacionEnabled");

    // then: se devuelve el valor por defecto
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().getName()).isEqualTo("peticionEvaluacionUnidadesVinculacionEnabled");
    Assertions.assertThat(response.getBody().getValue()).isEqualTo("false");
  }

  @Test
  void get_WithParamNotInformed_ReturnsEmptyValue() throws Exception {
    // given: la configuración inicial sin tipo de unidad configurado

    // when: se consulta el tipo de unidad que actúa como departamento
    final ResponseEntity<ConfigParamOutput> response = restTemplate.exchange(
        CONFIG_CONTROLLER_BASE_PATH + PATH_NAME, HttpMethod.GET, buildRequest(null, "ETI-PEV-V"),
        ConfigParamOutput.class, "tipoUnidadVinculacionDepartamentoRef");

    // then: se devuelve un valor vacío
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().getValue()).isEmpty();
  }

  @Test
  void get_WithUnknownKey_Returns400() throws Exception {
    // given: una clave inexistente

    // when: se consulta la clave
    final ResponseEntity<String> response = restTemplate.exchange(CONFIG_CONTROLLER_BASE_PATH + PATH_NAME,
        HttpMethod.GET, buildRequest(null, "ADM-CNF-E"), String.class, "unknown");

    // then: se devuelve error de petición
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void get_WithoutAuthority_Returns403() throws Exception {
    // given: un usuario sin autoridades de ETI ni de administración

    // when: se consulta un parámetro
    final ResponseEntity<String> response = restTemplate.exchange(CONFIG_CONTROLLER_BASE_PATH + PATH_NAME,
        HttpMethod.GET, buildRequest(null, "ETI-ACT-V"), String.class, "peticionEvaluacionUnidadesVinculacionEnabled");

    // then: se deniega el acceso
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void updateValue_WithParamKey_ReturnsUpdatedValue() throws Exception {
    // given: un administrador de configuración con el apartado y la
    // inicialización habilitados
    updateValue("peticionEvaluacionUnidadesVinculacionEnabled", "true");
    updateValue("peticionEvaluacionInicializarUnidadVinculacionEnabled", "true");

    // when: se actualiza el tipo de unidad que actúa como departamento
    final ResponseEntity<ConfigParamOutput> response = restTemplate.exchange(
        CONFIG_CONTROLLER_BASE_PATH + PATH_NAME, HttpMethod.PATCH, buildRequest("TIPO-DEP", "ADM-CNF-E"),
        ConfigParamOutput.class, "tipoUnidadVinculacionDepartamentoRef");

    // then: se devuelve el valor actualizado
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().getName()).isEqualTo("tipoUnidadVinculacionDepartamentoRef");
    Assertions.assertThat(response.getBody().getValue()).isEqualTo("TIPO-DEP");
  }

  @Test
  void updateValue_WithBoolean_PersistsValue() throws Exception {
    // given: un administrador de configuración que activa el apartado
    restTemplate.exchange(CONFIG_CONTROLLER_BASE_PATH + PATH_NAME, HttpMethod.PATCH,
        buildRequest("true", "ADM-CNF-E"), ConfigParamOutput.class, "peticionEvaluacionUnidadesVinculacionEnabled");

    // when: se consulta el flag
    final ResponseEntity<ConfigParamOutput> response = restTemplate.exchange(
        CONFIG_CONTROLLER_BASE_PATH + PATH_NAME, HttpMethod.GET, buildRequest(null, "ETI-PEV-V"),
        ConfigParamOutput.class, "peticionEvaluacionUnidadesVinculacionEnabled");

    // then: el valor persistido es true
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().getValue()).isEqualTo("true");
  }

  @Test
  void updateValue_WithApartadoDisabled_DisablesDependentParams() throws Exception {
    // given: el apartado, la inicialización y el tipo de unidad habilitados
    updateValue("peticionEvaluacionUnidadesVinculacionEnabled", "true");
    updateValue("peticionEvaluacionInicializarUnidadVinculacionEnabled", "true");
    updateValue("tipoUnidadVinculacionDepartamentoRef", "TIPO-DEP");

    // when: se deshabilita el apartado
    final ResponseEntity<ConfigParamOutput> response = updateValue("peticionEvaluacionUnidadesVinculacionEnabled",
        "false");

    // then: la inicialización y el tipo de unidad quedan deshabilitados
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(getValue("peticionEvaluacionInicializarUnidadVinculacionEnabled")).isEqualTo("false");
    Assertions.assertThat(getValue("tipoUnidadVinculacionDepartamentoRef")).isEmpty();
  }

  @Test
  void updateValue_WithDependencyDisabled_Returns400() throws Exception {
    // given: la configuración inicial con el apartado deshabilitado

    // when: se intenta habilitar la inicialización
    final ResponseEntity<String> response = restTemplate.exchange(CONFIG_CONTROLLER_BASE_PATH + PATH_NAME,
        HttpMethod.PATCH, buildRequest("true", "ADM-CNF-E"), String.class,
        "peticionEvaluacionInicializarUnidadVinculacionEnabled");

    // then: se devuelve error de petición y el valor no cambia
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(getValue("peticionEvaluacionInicializarUnidadVinculacionEnabled")).isEqualTo("false");
  }

  @Test
  void updateValue_WithoutAdminAuthority_Returns403() throws Exception {
    // given: un usuario de gestión sin autoridad de administración

    // when: se intenta actualizar un parámetro
    final ResponseEntity<String> response = restTemplate.exchange(CONFIG_CONTROLLER_BASE_PATH + PATH_NAME,
        HttpMethod.PATCH, buildRequest("true", "ETI-PEV-E"), String.class, "peticionEvaluacionUnidadesVinculacionEnabled");

    // then: se deniega el acceso
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

}
