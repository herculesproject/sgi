package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.AutorizacionController;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion;
import org.crue.hercules.sgi.csp.repository.EstadoAutorizacionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AutorizacionIT extends BaseIT {

  @Autowired
  private EstadoAutorizacionRepository estadoAutorizacionRepository;

  private static final String DEFAULT_TITULO_PROYECTO = "proyecto 1";
  private static final String DEFAULT_SOLICITUD_REF = "39878833";
  private static final String DEFAULT_RESPONSABLE_REF = "27333555";
  private static final String DEFAULT_OBSERVACIONES = "autorizacion nueva";
  private static final int DEFAULT_HORAS_DEDICADAS = 24;
  private static final String DEFAULT_ENTIDAD_REF = "34444333";
  private static final String DEFAULT_DATOS_RESPONSABLES = "datos responsable";
  private static final String DEFAULT_DATOS_ENTIDAD = "datos entidad creada";
  private static final String DEFAULT_DATOS_CONVOCATORIA = "datos convocatoria creada";
  private static final long DEFAULT_CONVOCATORIA_ID = 1L;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = AutorizacionController.REQUEST_MAPPING;
  private static final String PATH_PRESENTAR = "/presentar";
  private static final String PATH_PRESENTABLE = "/presentable";

  private HttpEntity<Autorizacion> buildRequest(HttpHeaders headers, Autorizacion entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", roles)));

    HttpEntity<Autorizacion> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsAutorizacion() throws Exception {
    Autorizacion toCreate = buildMockAutorizacion(null);
    String roles = "CSP-AUT-INV-C";

    final ResponseEntity<Autorizacion> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, toCreate, roles), Autorizacion.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    Autorizacion created = response.getBody();
    Assertions.assertThat(created.getId()).as("getId()").isNotNull();
    Assertions.assertThat(created.getConvocaoriaId()).as("getConvocaoriaId()")
    .isEqualTo(toCreate.getConvocaoriaId());
    Assertions.assertThat(created.getObservaciones()).as("getObservaciones()")
        .isEqualTo(toCreate.getObservaciones());
    Assertions.assertThat(created.getDatosConvocatoria()).as("getDatosConvocatoria()")
        .isEqualTo(toCreate.getDatosConvocatoria());
    Assertions.assertThat(created.getDatosEntidad()).as("getDatosEntidad()")
        .isEqualTo(toCreate.getDatosEntidad());
    Assertions.assertThat(created.getDatosResponsable()).as("getDatosResponsable()")
        .isEqualTo(toCreate.getDatosResponsable());
    Assertions.assertThat(created.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(toCreate.getEntidadRef());
    Assertions.assertThat(created.getHorasDedicacion()).as("getHorasDedicacion()")
        .isEqualTo(toCreate.getHorasDedicacion());
    Assertions.assertThat(created.getResponsableRef()).as("getResponsableRef()")
        .isEqualTo(toCreate.getResponsableRef());
    Assertions.assertThat(created.getSolicitanteRef()).as("getSolitanteRef()")
        .isEqualTo(toCreate.getSolicitanteRef());
    Assertions.assertThat(created.getTituloProyecto()).as("getTituloProyecto()")
        .isEqualTo(toCreate.getTituloProyecto());
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsAutorizacion() throws Exception {
    String roles = "CSP-AUT-INV-ER";
    Long idAutorizacion = 1L;
    Autorizacion toUpdate = buildMockAutorizacion(1L);
    toUpdate.setObservaciones("observaciones actualizadas");

    final ResponseEntity<Autorizacion> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, toUpdate, roles), Autorizacion.class, idAutorizacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Autorizacion updated = response.getBody();

    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(toUpdate.getId());
    Assertions.assertThat(updated.getConvocaoriaId()).as("getConvocaoriaId()")
    .isEqualTo(toUpdate.getConvocaoriaId());
    Assertions.assertThat(updated.getObservaciones()).as("getObservaciones()")
        .isEqualTo(toUpdate.getObservaciones());
    Assertions.assertThat(updated.getDatosConvocatoria()).as("getDatosConvocatoria()")
        .isEqualTo(toUpdate.getDatosConvocatoria());
    Assertions.assertThat(updated.getDatosEntidad()).as("getDatosEntidad()")
        .isEqualTo(toUpdate.getDatosEntidad());
    Assertions.assertThat(updated.getDatosResponsable()).as("getDatosResponsable()")
        .isEqualTo(toUpdate.getDatosResponsable());
    Assertions.assertThat(updated.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(toUpdate.getEntidadRef());
    Assertions.assertThat(updated.getHorasDedicacion()).as("getHorasDedicacion()")
        .isEqualTo(toUpdate.getHorasDedicacion());
    Assertions.assertThat(updated.getResponsableRef()).as("getResponsableRef()")
        .isEqualTo(toUpdate.getResponsableRef());
    Assertions.assertThat(updated.getSolicitanteRef()).as("getSolitanteRef()")
        .isEqualTo(toUpdate.getSolicitanteRef());
    Assertions.assertThat(updated.getTituloProyecto()).as("getTituloProyecto()")
        .isEqualTo(toUpdate.getTituloProyecto());

  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsAutorizacion() throws Exception {
    String roles = "CSP-AUT-E";
    Long idAutorizacion = 1L;

    final ResponseEntity<Autorizacion> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null, roles), Autorizacion.class, idAutorizacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Autorizacion autorizacion = response.getBody();

    Assertions.assertThat(autorizacion.getId()).as("getId()").isEqualTo(idAutorizacion);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql",
    "classpath:scripts/estado_autorizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void presentar_ReturnsAutorizacion() throws Exception {
    String roles = "CSP-AUT-INV-C";
    Long idAutorizacion = 1L;

    final ResponseEntity<Autorizacion> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PRESENTAR,
        HttpMethod.PATCH, buildRequest(null, null, roles), Autorizacion.class, idAutorizacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Autorizacion autorizacion = response.getBody();

    Assertions.assertThat(autorizacion.getId()).as("getId()").isEqualTo(idAutorizacion);
    
    EstadoAutorizacion estadoAutorizacion = this.estadoAutorizacionRepository.findById(autorizacion.getEstadoId()).orElse(null);

    Assertions.assertThat(estadoAutorizacion).isNotNull();
    Assertions.assertThat(estadoAutorizacion.getEstado()).isEqualTo(EstadoAutorizacion.Estado.PRESENTADA);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql",
    "classpath:scripts/estado_autorizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void presentable_ReturnsStatusCode200() throws Exception {
    String roles = "CSP-AUT-INV-C";
    Long idAutorizacion = 2L;

    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PRESENTABLE,
        HttpMethod.HEAD, buildRequest(null, null, roles), Void.class, idAutorizacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsAutorizacionSubList() throws Exception {
    String[] roles = {"CSP-AUT-E","CSP-AUT-B","CSP-AUT-INV-C", "CSP-AUT-INV-ER", "CSP-AUT-INV-BR"};
    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";
    String filter = "";

    // when: find Convocatoria
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();
    final ResponseEntity<List<Autorizacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<Autorizacion>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Autorizacion> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0)).as("get(0)").isNotNull();
    Assertions.assertThat(responseData.get(0).getId()).as("get(0).getId()").isEqualTo(3);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_ReturnsStatusCode204() throws Exception {
    // given: existing id
    Long toDeleteId = 1L;
    // when: exists by id
    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, buildRequest(null, null, "CSP-AUT-B"), Void.class, toDeleteId);
    // then: 204 NO CONTENT
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  private Autorizacion buildMockAutorizacion(Long id) {
    return Autorizacion.builder()
    .convocaoriaId(DEFAULT_CONVOCATORIA_ID)
    .datosConvocatoria(DEFAULT_DATOS_CONVOCATORIA)
    .datosEntidad(DEFAULT_DATOS_ENTIDAD)
    .datosResponsable(DEFAULT_DATOS_RESPONSABLES)
    .entidadRef(DEFAULT_ENTIDAD_REF)
    .horasDedicacion(DEFAULT_HORAS_DEDICADAS)
    .id(id)
    .observaciones(DEFAULT_OBSERVACIONES)
    .responsableRef(DEFAULT_RESPONSABLE_REF)
    .solicitanteRef(DEFAULT_SOLICITUD_REF)
    .tituloProyecto(DEFAULT_TITULO_PROYECTO)
    .build();
  }

}