package org.crue.hercules.sgi.csp.integration;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer.TokenBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de ConfiguracionSolicitud.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class ConfiguracionSolicitudIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DOCUMENTOS = "/documentorequiridosolicitudes";
  private static final String CONTROLLER_BASE_PATH = "/convocatoria-configuracionsolicitudes";

  private HttpEntity<ConfiguracionSolicitud> buildRequest(HttpHeaders headers, ConfiguracionSolicitud entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "CSP-CONV-C", "CSP-CONV-E", "CSP-CONV-B", "CSP-CONV-V")));

    HttpEntity<ConfiguracionSolicitud> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsConfiguracionSolicitud() throws Exception {

    // given: new ConfiguracionSolicitud
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, 1L, 1L);
    configuracionSolicitud.setId(null);

    // when: create ConfiguracionSolicitud
    final ResponseEntity<ConfiguracionSolicitud> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, configuracionSolicitud), ConfiguracionSolicitud.class);

    // then: new ConfiguracionSolicitud is created
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ConfiguracionSolicitud responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(configuracionSolicitud.getConvocatoria().getId());
    Assertions.assertThat(responseData.getTramitacionSGI()).as("getTramitacionSGI()")
        .isEqualTo(configuracionSolicitud.getTramitacionSGI());
    Assertions.assertThat(responseData.getFasePresentacionSolicitudes().getId())
        .as("getFasePresentacionSolicitudes().getId()")
        .isEqualTo(configuracionSolicitud.getFasePresentacionSolicitudes().getId());
    Assertions.assertThat(responseData.getImporteMaximoSolicitud()).as("getImporteMaximoSolicitud()")
        .isEqualTo(configuracionSolicitud.getImporteMaximoSolicitud());
    Assertions.assertThat(responseData.getFormularioSolicitud()).as("getFormularioSolicitud()")
        .isEqualTo(configuracionSolicitud.getFormularioSolicitud());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsConfiguracionSolicitud() throws Exception {

    // given: existing ConfiguracionSolicitud to be updated
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, 1L, 1L);
    configuracionSolicitud.getFasePresentacionSolicitudes().setId(2L);
    configuracionSolicitud.setTramitacionSGI(Boolean.FALSE);
    configuracionSolicitud.setImporteMaximoSolicitud(BigDecimal.valueOf(54321));

    // when: update ConfiguracionSolicitud
    final ResponseEntity<ConfiguracionSolicitud> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, configuracionSolicitud),
        ConfiguracionSolicitud.class, configuracionSolicitud.getConvocatoria().getId());

    // then: ConfiguracionSolicitud is updated
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ConfiguracionSolicitud responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(configuracionSolicitud.getId());
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).isNotNull();
    Assertions.assertThat(responseData.getConvocatoria().getId()).as("getConvocatoria().getId()")
        .isEqualTo(configuracionSolicitud.getConvocatoria().getId());
    Assertions.assertThat(responseData.getTramitacionSGI()).as("getTramitacionSGI()").isEqualTo(Boolean.FALSE);
    Assertions.assertThat(responseData.getFasePresentacionSolicitudes().getId())
        .as("getFasePresentacionSolicitudes().getId()").isEqualTo(2L);
    Assertions.assertThat(responseData.getImporteMaximoSolicitud()).as("getImporteMaximoSolicitud()")
        .isEqualTo(BigDecimal.valueOf(54321));
    Assertions.assertThat(responseData.getFormularioSolicitud()).as("getFormularioSolicitud()")
        .isEqualTo(configuracionSolicitud.getFormularioSolicitud());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findByIdConvocatoria_ReturnsConfiguracionSolicitud() throws Exception {
    Long convocatoriaId = 2L;

    final ResponseEntity<ConfiguracionSolicitud> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ConfiguracionSolicitud.class, convocatoriaId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ConfiguracionSolicitud responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(2L);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseData.getConvocatoria().getId()).as("getConvocatoria().getId()").isEqualTo(2L);
    Assertions.assertThat(responseData.getTramitacionSGI()).as("getTramitacionSGI()").isEqualTo(Boolean.FALSE);
    Assertions.assertThat(responseData.getFasePresentacionSolicitudes().getId())
        .as("getFasePresentacionSolicitudes().getId()").isEqualTo(2L);
    Assertions.assertThat(responseData.getImporteMaximoSolicitud()).as("getImporteMaximoSolicitud()")
        .isEqualTo(new BigDecimal("54321.00"));
    Assertions.assertThat(responseData.getFormularioSolicitud()).as("getFormularioSolicitud()")
        .isEqualTo(FormularioSolicitud.ESTANDAR);

  }

  /*
   * DOCUMENTOS REQUERIDOS SOLICITUD
   * 
   */
  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllDocumentoRequeridoSolicitud_WithPagingSortingAndFiltering_ReturnsDocumentoRequeridoSolicitudSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CONV-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "observaciones=ke=-00";

    Long convocatoriaId = 2L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DOCUMENTOS)
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(convocatoriaId).toUri();

    final ResponseEntity<List<DocumentoRequeridoSolicitud>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<DocumentoRequeridoSolicitud>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<DocumentoRequeridoSolicitud> documentos = response.getBody();
    Assertions.assertThat(documentos.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
    Assertions.assertThat(documentos.get(0).getObservaciones()).as("get(0).getObservaciones()")
        .isEqualTo("observaciones-" + String.format("%03d", 8));
    Assertions.assertThat(documentos.get(1).getObservaciones()).as("get(1).getObservaciones())")
        .isEqualTo("observaciones-" + String.format("%03d", 7));
    Assertions.assertThat(documentos.get(2).getObservaciones()).as("get(2).getObservaciones()")
        .isEqualTo("observaciones-" + String.format("%03d", 6));

  }

  /**
   * Genera un objeto ConfiguracionSolicitud
   * 
   * @param configuracionSolicitudId
   * @param convocatoriaId
   * @param convocatoriaFaseId
   * @return
   */
  private ConfiguracionSolicitud generarMockConfiguracionSolicitud(Long configuracionSolicitudId, Long convocatoriaId,
      Long convocatoriaFaseId) {

    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);

    TipoFase tipoFase = TipoFase.builder()//
        .id(convocatoriaFaseId)//
        .nombre("nombre-1")//
        .activo(Boolean.TRUE)//
        .build();

    ConvocatoriaFase convocatoriaFase = ConvocatoriaFase.builder()//
        .id(convocatoriaFaseId)//
        .convocatoria(convocatoria)//
        .tipoFase(tipoFase)//
        .fechaInicio(LocalDateTime.of(2020, 10, 1, 17, 18, 19))//
        .fechaFin(LocalDateTime.of(2020, 10, 15, 17, 18, 19))//
        .observaciones("observaciones")//
        .build();

    ConfiguracionSolicitud configuracionSolicitud = ConfiguracionSolicitud.builder()//
        .id(configuracionSolicitudId)//
        .convocatoria(convocatoria)//
        .tramitacionSGI(Boolean.TRUE)//
        .fasePresentacionSolicitudes(convocatoriaFase)//
        .importeMaximoSolicitud(BigDecimal.valueOf(12345))//
        .formularioSolicitud(FormularioSolicitud.ESTANDAR)//
        .build();

    return configuracionSolicitud;
  }

  /**
   * Función que genera Convocatoria
   * 
   * @param convocatoriaId
   * @param unidadGestionId
   * @param modeloEjecucionId
   * @param modeloTipoFinalidadId
   * @param tipoRegimenConcurrenciaId
   * @param tipoAmbitoGeogragicoId
   * @param activo
   * @return la convocatoria
   */
  private Convocatoria generarMockConvocatoria(Long convocatoriaId, Long unidadGestionId, Long modeloEjecucionId,
      Long modeloTipoFinalidadId, Long tipoRegimenConcurrenciaId, Long tipoAmbitoGeogragicoId, Boolean activo) {

    ModeloEjecucion modeloEjecucion = (modeloEjecucionId == null) ? null
        : ModeloEjecucion.builder()//
            .id(modeloEjecucionId)//
            .nombre("nombreModeloEjecucion-" + String.format("%03d", modeloEjecucionId))//
            .activo(Boolean.TRUE)//
            .build();

    TipoFinalidad tipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : TipoFinalidad.builder()//
            .id(modeloTipoFinalidadId)//
            .nombre("nombreTipoFinalidad-" + String.format("%03d", modeloTipoFinalidadId))//
            .activo(Boolean.TRUE)//
            .build();

    ModeloTipoFinalidad modeloTipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : ModeloTipoFinalidad.builder()//
            .id(modeloTipoFinalidadId)//
            .modeloEjecucion(modeloEjecucion)//
            .tipoFinalidad(tipoFinalidad)//
            .activo(Boolean.TRUE)//
            .build();

    TipoRegimenConcurrencia tipoRegimenConcurrencia = (tipoRegimenConcurrenciaId == null) ? null
        : TipoRegimenConcurrencia.builder()//
            .id(tipoRegimenConcurrenciaId)//
            .nombre("nombreTipoRegimenConcurrencia-" + String.format("%03d", tipoRegimenConcurrenciaId))//
            .activo(Boolean.TRUE)//
            .build();

    TipoAmbitoGeografico tipoAmbitoGeografico = (tipoAmbitoGeogragicoId == null) ? null
        : TipoAmbitoGeografico.builder()//
            .id(tipoAmbitoGeogragicoId)//
            .nombre("nombreTipoAmbitoGeografico-" + String.format("%03d", tipoAmbitoGeogragicoId))//
            .activo(Boolean.TRUE)//
            .build();

    Convocatoria convocatoria = Convocatoria.builder()//
        .id(convocatoriaId)//
        .unidadGestionRef((unidadGestionId == null) ? null : "unidad-" + String.format("%03d", unidadGestionId))//
        .modeloEjecucion(modeloEjecucion)//
        .codigo("codigo-" + String.format("%03d", convocatoriaId))//
        .anio(2020)//
        .titulo("titulo-" + String.format("%03d", convocatoriaId))//
        .objeto("objeto-" + String.format("%03d", convocatoriaId))//
        .observaciones("observaciones-" + String.format("%03d", convocatoriaId))//
        .finalidad((modeloTipoFinalidad == null) ? null : modeloTipoFinalidad.getTipoFinalidad())//
        .regimenConcurrencia(tipoRegimenConcurrencia)//
        .destinatarios(Convocatoria.Destinatarios.INDIVIDUAL)//
        .colaborativos(Boolean.TRUE)//
        .estado(Convocatoria.Estado.REGISTRADA)//
        .duracion(12)//
        .ambitoGeografico(tipoAmbitoGeografico)//
        .clasificacionCVN(ClasificacionCVN.AYUDAS)//
        .activo(activo)//
        .build();

    return convocatoria;
  }
}
