package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SocioPeriodoJustificacionDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
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
 * Test de integracion de SocioPeriodoJustificacionDocumento.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SocioPeriodoJustificacionDocumentoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/socioperiodojustificaciondocumentos";

  private HttpEntity<SocioPeriodoJustificacionDocumento> buildRequest(HttpHeaders headers,
      SocioPeriodoJustificacionDocumento entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CENTGES-C", "CSP-CENTGES-V", "CSP-CONV-C")));

    HttpEntity<SocioPeriodoJustificacionDocumento> request = new HttpEntity<>(entity, headers);
    return request;
  }

  private HttpEntity<List<SocioPeriodoJustificacionDocumento>> buildRequestList(HttpHeaders headers,
      List<SocioPeriodoJustificacionDocumento> entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CENTGES-C", "CSP-CENTGES-V", "CSP-CONV-C")));

    HttpEntity<List<SocioPeriodoJustificacionDocumento>> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/proyecto.sql", "classpath:scripts/rol_socio.sql", "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_socio.sql", "classpath:scripts/proyecto_socio_periodo_justificacion.sql",
      "classpath:scripts/tipo_documento.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsSocioPeriodoJustificacionDocumentoList() throws Exception {

    // given: una lista con uno nuevo y sin los otros 3 periodos existentes
    Long proyectoSocioId = 1L;
    SocioPeriodoJustificacionDocumento newSocioPeriodoJustificacionDocumento = generarMockSocioPeriodoJustificacionDocumento(
        null);

    List<SocioPeriodoJustificacionDocumento> convocatoriaPeriodoJustificaciones = Arrays
        .asList(newSocioPeriodoJustificacionDocumento);

    // when: updateSocioPeriodoJustificacionDocumentoesProyectoSocio
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(proyectoSocioId).toUri();

    final ResponseEntity<List<SocioPeriodoJustificacionDocumento>> response = restTemplate.exchange(uri,
        HttpMethod.PATCH, buildRequestList(null, convocatoriaPeriodoJustificaciones),
        new ParameterizedTypeReference<List<SocioPeriodoJustificacionDocumento>>() {
        });

    // then: Se crea el nuevo SocioPeriodoJustificacionDocumento y se eliminan los
    // otros
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    List<SocioPeriodoJustificacionDocumento> responseData = response.getBody();

    Assertions.assertThat(responseData.get(0).getProyectoSocioPeriodoJustificacion().getId())
        .as("get(0).getProyectoSocioPeriodoJustificacion().getId()").isEqualTo(proyectoSocioId);
    Assertions.assertThat(responseData.get(0).getNombre()).as("get(0).getNombre()")
        .isEqualTo(newSocioPeriodoJustificacionDocumento.getNombre());
    Assertions.assertThat(responseData.get(0).getDocumentoRef()).as("get(0).getDocumentoRef()")
        .isEqualTo(newSocioPeriodoJustificacionDocumento.getDocumentoRef());
    Assertions.assertThat(responseData.get(0).getVisible()).as("get(0).getVisible()")
        .isEqualTo(newSocioPeriodoJustificacionDocumento.getVisible());
    Assertions.assertThat(responseData.get(0).getTipoDocumento().getId()).as("get(0).getTipoDocumento().getId()")
        .isEqualTo(newSocioPeriodoJustificacionDocumento.getTipoDocumento().getId());
    Assertions.assertThat(responseData.get(0).getComentario()).as("get(0).getComentario()")
        .isEqualTo(newSocioPeriodoJustificacionDocumento.getComentario());

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CENL-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";

    URI uriFindAllSocioPeriodoJustificacionDocumento = UriComponentsBuilder
        .fromUriString("/proyectosocioperiodojustificaciones" + PATH_PARAMETER_ID + CONTROLLER_BASE_PATH)
        .queryParam("s", sort).buildAndExpand(proyectoSocioId).toUri();

    final ResponseEntity<List<SocioPeriodoJustificacionDocumento>> responseFindAllSocioPeriodoJustificacionDocumento = restTemplate
        .exchange(uriFindAllSocioPeriodoJustificacionDocumento, HttpMethod.GET, buildRequestList(headers, null),
            new ParameterizedTypeReference<List<SocioPeriodoJustificacionDocumento>>() {
            });

    Assertions.assertThat(responseFindAllSocioPeriodoJustificacionDocumento.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SocioPeriodoJustificacionDocumento> responseDataFindAll = responseFindAllSocioPeriodoJustificacionDocumento
        .getBody();
    Assertions.assertThat(responseDataFindAll.size()).as("size()").isEqualTo(convocatoriaPeriodoJustificaciones.size());
    Assertions.assertThat(responseDataFindAll.get(0).getId()).as("responseDataFindAll.get(0).getId()")
        .isEqualTo(responseData.get(0).getId());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/proyecto.sql", "classpath:scripts/rol_socio.sql", "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_socio.sql", "classpath:scripts/proyecto_socio_periodo_justificacion.sql",
      "classpath:scripts/tipo_documento.sql", "classpath:scripts/socio_periodo_justificacion_documento.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsSocioPeriodoJustificacionDocumento() throws Exception {
    Long idSocioPeriodoJustificacionDocumento = 1L;

    final ResponseEntity<SocioPeriodoJustificacionDocumento> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        SocioPeriodoJustificacionDocumento.class, idSocioPeriodoJustificacionDocumento);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SocioPeriodoJustificacionDocumento convocatoriaPeriodoJustificacion = response.getBody();
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getProyectoSocioPeriodoJustificacion().getId())
        .as("get(0).convocatoriaPeriodoJustificacion().getId()").isEqualTo(1L);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getNombre()).as("get(0).getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getDocumentoRef()).as("get(0).getDocumentoRef()")
        .isEqualTo("doc-001");
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getVisible()).as("get(0).getVisible()")
        .isEqualTo(Boolean.TRUE);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getTipoDocumento().getId())
        .as("get(0).getTipoDocumento().getId()").isEqualTo(1L);
    Assertions.assertThat(convocatoriaPeriodoJustificacion.getComentario()).as("get(0).getComentario()")
        .isEqualTo("comentario-1");

  }

  /**
   * Función que devuelve un objeto SocioPeriodoJustificacionDocumento
   * 
   * @param id id del SocioPeriodoJustificacionDocumento
   * @return el objeto SocioPeriodoJustificacionDocumento
   */
  private SocioPeriodoJustificacionDocumento generarMockSocioPeriodoJustificacionDocumento(Long id) {

    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = ProyectoSocioPeriodoJustificacion.builder()
        .id(1L).build();

    TipoDocumento tipoDocumento = TipoDocumento.builder().id(1L).activo(Boolean.TRUE).build();
    SocioPeriodoJustificacionDocumento socioPeriodoJustificacionDocumento = SocioPeriodoJustificacionDocumento.builder()
        .id(id).nombre("nombre-" + id).comentario("comentario").documentoRef("001")
        .proyectoSocioPeriodoJustificacion(proyectoSocioPeriodoJustificacion).tipoDocumento(tipoDocumento)
        .visible(Boolean.TRUE).build();

    return socioPeriodoJustificacionDocumento;
  }

}
