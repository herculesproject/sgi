package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.ProyectoAnualidadController;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadInput;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadOutput;
import org.junit.jupiter.api.Test;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProyectoAnualidadIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = ProyectoAnualidadController.MAPPING;

  private HttpEntity<ProyectoAnualidadInput> buildRequest(HttpHeaders headers,
      ProyectoAnualidadInput entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<ProyectoAnualidadInput> request = new HttpEntity<>(entity, headers);

    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/proyecto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProyectoAnualidadOutput() throws Exception {
    ProyectoAnualidadInput toCreate = buildMockProyectoAnualidadInput();

    final ResponseEntity<ProyectoAnualidadOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, toCreate, "CSP-PRO-E"),
        ProyectoAnualidadOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProyectoAnualidadOutput proyectoAnualidadCreated = response.getBody();

    Assertions.assertThat(proyectoAnualidadCreated.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoAnualidadCreated.getAnio()).as("getAnio()")
        .isEqualTo(toCreate.getAnio());
    Assertions.assertThat(proyectoAnualidadCreated.getProyectoId())
        .as("getProyectoId()").isEqualTo(toCreate.getProyectoId());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off    
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/proyecto_anualidad.sql"
    // @formatter:on   
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_Return204() throws Exception {
    // given: existing id
    Long toDeleteId = 2L;
    // when: exists by id
    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null, "CSP-PRO-E"), Void.class, toDeleteId);
    // then: 204 NO CONTENT
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off    
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/proyecto_anualidad.sql" 
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsProyectoAnualidadOutputSubList() throws Exception {
    // first page, 2 elements per page sorted by id asc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,asc";
    String filter = "id<=3";

    // when: find ProyectoAgrupacionGastoOutput
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter)
        .build(false).toUri();

    final ResponseEntity<List<ProyectoAnualidadOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, "CSP-EJEC-E", "CSP-EJEC-V", "CSP-EJEC-INV-VR"),
        new ParameterizedTypeReference<List<ProyectoAnualidadOutput>>() {
        });

    // given: ProyectoAnualidadOutput data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ProyectoAnualidadOutput> responseData = response.getBody();

    Assertions.assertThat(responseData.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getProyectoId()).as("get(0).getProyectoId())")
        .isEqualTo(1L);
    Assertions.assertThat(responseData.get(0).getAnio()).as("get(0).getAnio())")
        .isEqualTo(2021);
    Assertions.assertThat(responseData.get(1).getProyectoId()).as("get(1).getProyectoId())")
        .isEqualTo(1);
    Assertions.assertThat(responseData.get(1).getAnio()).as("get(1).getAnio())")
        .isEqualTo(2022);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off    
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/proyecto_anualidad.sql"
    // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnProyectoAnualidadOutput() throws Exception {

    Long proyectoAnualidadId = 5L;

    final ResponseEntity<ProyectoAnualidadOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null, "CSP-PRO-V",
            "CSP-PRO-E"),
        ProyectoAnualidadOutput.class,
        proyectoAnualidadId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoAnualidadOutput proyectoAnualidadOutput = response.getBody();

    Assertions.assertThat(proyectoAnualidadOutput).isNotNull();
    Assertions.assertThat(proyectoAnualidadOutput.getId()).as("getId()")
        .isEqualTo(5L);
    Assertions.assertThat(proyectoAnualidadOutput.getAnio()).as("getAnio()")
        .isEqualTo(2023);

  }

  ProyectoAnualidadInput buildMockProyectoAnualidadInput() {
    return ProyectoAnualidadInput.builder()
        .anio(2022)
        .enviadoSge(Boolean.TRUE)
        .fechaInicio(Instant.now())
        .fechaFin(Instant.now().plusSeconds(360000000))
        .presupuestar(Boolean.TRUE)
        .proyectoId(1L)
        .build();
  }
}