package org.crue.hercules.sgi.csp.integration;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.TipoEstadoProyectoEnum;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.RolSocio;
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

/**
 * Test de integracion de ProyectoSocio.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProyectoSocioIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectosocios";

  private HttpEntity<ProyectoSocio> buildRequest(HttpHeaders headers, ProyectoSocio entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "SYSADMIN", "CSP-PRO-C", "CSP-PRO-E", "CSP-PRO-V")));

    HttpEntity<ProyectoSocio> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsProyectoSocio() throws Exception {

    // given: new ProyectoSocio
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(1L);
    proyectoSocio.setId(null);

    // when: create ProyectoSocio
    final ResponseEntity<ProyectoSocio> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, proyectoSocio), ProyectoSocio.class);

    // then: new ProyectoSocio is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ProyectoSocio responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getProyecto()).as("getProyecto()").isNotNull();
    Assertions.assertThat(responseData.getProyecto().getId()).as("getProyecto().getId()")
        .isEqualTo(proyectoSocio.getProyecto().getId());
    Assertions.assertThat(responseData.getEmpresaRef()).as("getEmpresaRef()").isEqualTo(proyectoSocio.getEmpresaRef());
    Assertions.assertThat(responseData.getRolSocio()).as("getRolSocio()").isNotNull();
    Assertions.assertThat(responseData.getRolSocio().getId()).as("getRolSocio().getId()")
        .isEqualTo(proyectoSocio.getRolSocio().getId());
    Assertions.assertThat(responseData.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(proyectoSocio.getFechaInicio());
    Assertions.assertThat(responseData.getFechaFin()).as("getFechaFin()").isEqualTo(proyectoSocio.getFechaFin());
    Assertions.assertThat(responseData.getNumInvestigadores()).as("getNumInvestigadores()")
        .isEqualTo(proyectoSocio.getNumInvestigadores());
    Assertions.assertThat(responseData.getImporteConcedido()).as("getImporteConcedido()")
        .isEqualTo(proyectoSocio.getImporteConcedido());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsProyectoSocio() throws Exception {

    // given: existing ProyectoSocio to be updated
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(1L);
    proyectoSocio.setNumInvestigadores(10);

    // when: update ProyectoSocio
    final ResponseEntity<ProyectoSocio> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, proyectoSocio), ProyectoSocio.class, proyectoSocio.getId());

    // then: ProyectoSocio is updated
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ProyectoSocio responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getProyecto()).as("getProyecto()").isNotNull();
    Assertions.assertThat(responseData.getProyecto().getId()).as("getProyecto().getId()")
        .isEqualTo(proyectoSocio.getProyecto().getId());
    Assertions.assertThat(responseData.getEmpresaRef()).as("getEmpresaRef()").isEqualTo(proyectoSocio.getEmpresaRef());
    Assertions.assertThat(responseData.getRolSocio()).as("getRolSocio()").isNotNull();
    Assertions.assertThat(responseData.getRolSocio().getId()).as("getRolSocio().getId()")
        .isEqualTo(proyectoSocio.getRolSocio().getId());
    Assertions.assertThat(responseData.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(proyectoSocio.getFechaInicio());
    Assertions.assertThat(responseData.getFechaFin()).as("getFechaFin()").isEqualTo(proyectoSocio.getFechaFin());
    Assertions.assertThat(responseData.getNumInvestigadores()).as("getNumInvestigadores()")
        .isEqualTo(proyectoSocio.getNumInvestigadores());
    Assertions.assertThat(responseData.getImporteConcedido()).as("getImporteConcedido()")
        .isEqualTo(proyectoSocio.getImporteConcedido());

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_Return204() throws Exception {
    // given: existing ProyectoSocio to be deleted
    Long id = 1L;

    // when: delete ProyectoSocio
    final ResponseEntity<ProyectoSocio> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null), ProyectoSocio.class, id);

    // then: ProyectoSocio deleted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void existsById_Returns200() throws Exception {
    // given: existing id
    Long id = 1L;
    // when: exists by id
    final ResponseEntity<ProyectoSocio> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.HEAD, buildRequest(null, null), ProyectoSocio.class, id);
    // then: 200 OK
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsProyectoSocio() throws Exception {
    Long id = 1L;

    final ResponseEntity<ProyectoSocio> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ProyectoSocio.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ProyectoSocio responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(responseData.getProyecto().getId()).as("getProyecto()").isEqualTo(1L);
    Assertions.assertThat(responseData.getEmpresaRef()).as("getEmpresaRef()").isEqualTo("empresa-001");
    Assertions.assertThat(responseData.getRolSocio().getId()).as("getRolSocio().getId()").isEqualTo(1L);
    Assertions.assertThat(responseData.getFechaInicio()).as("getFechaInicio()").isEqualTo(LocalDate.of(2021, 1, 11));
    Assertions.assertThat(responseData.getFechaFin()).as("getFechaFin()").isEqualTo(LocalDate.of(2022, 1, 11));
    Assertions.assertThat(responseData.getNumInvestigadores()).as("getNumInvestigadores()").isEqualTo(5);
    Assertions.assertThat(responseData.getImporteConcedido()).as("getImporteConcedido()")
        .isEqualTo(new BigDecimal("1000.00"));

  }

  /*
   *
   * PROYECTO PERIODO EQUIPO
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllProyectoSocioEquipo_WithPagingSortingAndFiltering_ReturnsProyectoSocioEquipoSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CPSCI-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id-";
    String filter = "personaRef~%-00%";

    Long convocatoriaId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/proyectosocioequipos")
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<ProyectoSocioEquipo>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ProyectoSocioEquipo>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ProyectoSocioEquipo> proyectoSocioEquipos = response.getBody();
    Assertions.assertThat(proyectoSocioEquipos.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(proyectoSocioEquipos.get(0).getPersonaRef()).as("get(0).getPersonaRef()")
        .isEqualTo("personaRef-" + String.format("%03d", 3));
    Assertions.assertThat(proyectoSocioEquipos.get(1).getPersonaRef()).as("get(1).getPersonaRef())")
        .isEqualTo("personaRef-" + String.format("%03d", 2));
    Assertions.assertThat(proyectoSocioEquipos.get(2).getPersonaRef()).as("get(2).getPersonaRef()")
        .isEqualTo("personaRef-" + String.format("%03d", 1));
  }

  /**
   * Función que genera un ProyectoSocio
   * 
   * @param proyectoSocioId Identificador del {@link ProyectoSocio}
   * @return el ProyectoSocio
   */
  private ProyectoSocio generarMockProyectoSocio(Long proyectoSocioId) {

    String suffix = String.format("%03d", proyectoSocioId);

    ProyectoSocio proyectoSocio = ProyectoSocio.builder()//
        .id(proyectoSocioId)//
        .proyecto(Proyecto.builder()//
            .id(1L)//
            .estado(//
                EstadoProyecto.builder()//
                    .id(1L)//
                    .estado(TipoEstadoProyectoEnum.BORRADOR)//
                    .build())
            .build())//
        .empresaRef("empresa-" + suffix)//
        .rolSocio(RolSocio.builder().id(1L).coordinador(true).build())//
        .fechaInicio(LocalDate.of(2021, 1, 11))//
        .fechaFin(LocalDate.of(2022, 1, 11))//
        .numInvestigadores(5)//
        .importeConcedido(BigDecimal.valueOf(1000))//
        .build();

    return proyectoSocio;
  }

}
