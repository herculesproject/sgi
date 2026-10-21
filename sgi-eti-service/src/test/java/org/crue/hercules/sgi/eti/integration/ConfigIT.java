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
  private static final String EMPTY_LIST_VALUE = "[]";
  private static final String TEXTO_AYUDA = "[{\"lang\":\"es\",\"value\":\"<p><strong>Ayuda</strong></p>\"}]";

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
  void timeZone_ReturnsConfiguredTimeZone() throws Exception {
    // given: la zona horaria configurada en la aplicación
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
    headers.set("Authorization", "bearer " + tokenBuilder.buildToken("user", "ETI-PEV-INV-C"));

    // when: se consulta la zona horaria
    final ResponseEntity<String> response = restTemplate.exchange(CONFIG_CONTROLLER_BASE_PATH + "/time-zone",
        HttpMethod.GET, new HttpEntity<>(headers), String.class);

    // then: se devuelve el identificador de la zona horaria
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo("Europe/Madrid");
  }

  @Test
  void get_WithParamAyudaAndInvAuthority_Returns200() throws Exception {
    // given: la configuración inicial y un usuario con solo autoridades de
    // investigador

    // when: se consultan el flag y el texto de una ayuda
    final ResponseEntity<ConfigParamOutput> responseFlag = restTemplate.exchange(
        CONFIG_CONTROLLER_BASE_PATH + PATH_NAME, HttpMethod.GET, buildRequest(null, "ETI-PEV-INV-C"),
        ConfigParamOutput.class, "peticionEvaluacionAyudaFechaInicioEnabled");
    final ResponseEntity<ConfigParamOutput> responseTexto = restTemplate.exchange(
        CONFIG_CONTROLLER_BASE_PATH + PATH_NAME, HttpMethod.GET, buildRequest(null, "ETI-PEV-INV-ER"),
        ConfigParamOutput.class, "peticionEvaluacionAyudaFechaInicio");

    // then: se devuelven los valores por defecto
    Assertions.assertThat(responseFlag.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseFlag.getBody().getName()).isEqualTo("peticionEvaluacionAyudaFechaInicioEnabled");
    Assertions.assertThat(responseFlag.getBody().getValue()).isEqualTo("false");
    Assertions.assertThat(responseTexto.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseTexto.getBody().getName()).isEqualTo("peticionEvaluacionAyudaFechaInicio");
    Assertions.assertThat(responseTexto.getBody().getValue()).isEqualTo(EMPTY_LIST_VALUE);
  }

  @Test
  void get_WithParamNoAyudaAndInvAuthority_Returns403() throws Exception {
    // given: un usuario con solo autoridades de investigador

    // when: se consulta un parámetro no legible desde el perfil de investigación
    final ResponseEntity<String> response = restTemplate.exchange(CONFIG_CONTROLLER_BASE_PATH + PATH_NAME,
        HttpMethod.GET, buildRequest(null, "ETI-PEV-INV-ER"), String.class,
        "peticionEvaluacionUnidadesVinculacionEnabled");

    // then: se deniega el acceso con el problema de acceso denegado
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    Assertions.assertThat(response.getBody()).contains("urn:problem-type:access-denied")
        .contains("peticionEvaluacionUnidadesVinculacionEnabled");
  }

  @Test
  void get_WithUnknownKeyAndInvAuthority_Returns400() throws Exception {
    // given: una clave inexistente y un usuario con solo autoridad de investigador

    // when: se consulta la clave
    final ResponseEntity<String> response = restTemplate.exchange(CONFIG_CONTROLLER_BASE_PATH + PATH_NAME,
        HttpMethod.GET, buildRequest(null, "ETI-PEV-INV-C"), String.class, "unknown");

    // then: se devuelve error de petición antes que el de acceso
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void updateValue_WithAyudaTexto_PersistsJsonValue() throws Exception {
    // given: la ayuda del resumen habilitada
    updateValue("peticionEvaluacionAyudaResumenEnabled", "true");

    // when: se guarda el texto de ayuda del resumen
    final ResponseEntity<ConfigParamOutput> response = updateValue("peticionEvaluacionAyudaResumen", TEXTO_AYUDA);

    // then: se devuelve el texto y un investigador lo puede consultar
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().getValue()).isEqualTo(TEXTO_AYUDA);
    final ResponseEntity<ConfigParamOutput> responseInv = restTemplate.exchange(
        CONFIG_CONTROLLER_BASE_PATH + PATH_NAME, HttpMethod.GET, buildRequest(null, "ETI-PEV-INV-C"),
        ConfigParamOutput.class, "peticionEvaluacionAyudaResumen");
    Assertions.assertThat(responseInv.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseInv.getBody().getValue()).isEqualTo(TEXTO_AYUDA);
  }

  @Test
  void updateValue_WithAyudaFlagDisabled_ClearsTexto() throws Exception {
    // given: la ayuda de Asignación de tareas habilitada y con texto
    updateValue("peticionEvaluacionAyudaTareasEnabled", "true");
    updateValue("peticionEvaluacionAyudaTareas", TEXTO_AYUDA);

    // when: se deshabilita la ayuda
    final ResponseEntity<ConfigParamOutput> response = updateValue("peticionEvaluacionAyudaTareasEnabled", "false");

    // then: el texto queda vacío
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().getValue()).isEqualTo("false");
    Assertions.assertThat(getValue("peticionEvaluacionAyudaTareas")).isEqualTo(EMPTY_LIST_VALUE);
  }

  @Test
  void updateValue_WithAyudaTextoAndFlagDisabled_Returns400() throws Exception {
    // given: la configuración inicial con la ayuda del diseño metodológico
    // deshabilitada

    // when: se intenta guardar el texto de ayuda
    final ResponseEntity<String> response = restTemplate.exchange(CONFIG_CONTROLLER_BASE_PATH + PATH_NAME,
        HttpMethod.PATCH, buildRequest(TEXTO_AYUDA, "ADM-CNF-E"), String.class,
        "peticionEvaluacionAyudaDisenioMetodologico");

    // then: se devuelve error de petición y el texto no cambia
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(getValue("peticionEvaluacionAyudaDisenioMetodologico")).isEqualTo(EMPTY_LIST_VALUE);
  }

  @Test
  void updateValue_WithAyudaTextoVacio_ReturnsEmptyI18nValue() throws Exception {
    // given: la ayuda de la fecha de inicio habilitada
    updateValue("peticionEvaluacionAyudaFechaInicioEnabled", "true");

    // when: se guarda un texto vacío y se envía la petición sin cuerpo
    final ResponseEntity<ConfigParamOutput> responseVacio = updateValue("peticionEvaluacionAyudaFechaInicio",
        EMPTY_LIST_VALUE);
    final ResponseEntity<ConfigParamOutput> responseSinCuerpo = updateValue("peticionEvaluacionAyudaFechaInicio",
        null);

    // then: se guarda como texto multi-idioma vacío
    Assertions.assertThat(responseVacio.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseVacio.getBody().getValue()).isEqualTo(EMPTY_LIST_VALUE);
    Assertions.assertThat(responseSinCuerpo.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseSinCuerpo.getBody().getValue()).isEqualTo(EMPTY_LIST_VALUE);
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
