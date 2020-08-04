package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer.TokenBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

/**
 * Test de integracion de Evaluacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class EvaluacionIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String EVALUACION_CONTROLLER_BASE_PATH = "/evaluaciones";

  private HttpEntity<Evaluacion> buildRequest(HttpHeaders headers, Evaluacion entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<Evaluacion> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getEvaluacion_WithId_ReturnsEvaluacion() throws Exception {
    final ResponseEntity<Evaluacion> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null), Evaluacion.class,
        1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Evaluacion evaluacion = response.getBody();

    Assertions.assertThat(evaluacion.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen1");
    Assertions.assertThat(evaluacion.getConvocatoriaReunion().getId()).isEqualTo(1L);
    Assertions.assertThat(evaluacion.getTipoEvaluacion().getNombre()).isEqualTo("TipoEvaluacion1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addEvaluacion_ReturnsEvaluacion() throws Exception {

    Evaluacion nuevoEvaluacion = generarMockEvaluacion(null, "1");

    final ResponseEntity<Evaluacion> response = restTemplate.exchange(EVALUACION_CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, nuevoEvaluacion), Evaluacion.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeEvaluacion_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<Evaluacion> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        Evaluacion.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeEvaluacion_DoNotGetEvaluacion() throws Exception {

    final ResponseEntity<Evaluacion> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        Evaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceEvaluacion_ReturnsEvaluacion() throws Exception {

    Evaluacion replaceEvaluacion = generarMockEvaluacion(1L, null);

    final ResponseEntity<Evaluacion> response = restTemplate.exchange(
        EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, replaceEvaluacion),
        Evaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Evaluacion evaluacion = response.getBody();

    Assertions.assertThat(evaluacion.getId()).isNotNull();
    Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen1");
    Assertions.assertThat(evaluacion.getConvocatoriaReunion().getId()).isEqualTo(1L);
    Assertions.assertThat(evaluacion.getTipoEvaluacion().getNombre()).isEqualTo("TipoEvaluacion1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsEvaluacionSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V")));

    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(EVALUACION_CONTROLLER_BASE_PATH,
        HttpMethod.GET, buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de memoria.titulo='Memoria6' a 'Memoria8'
    // Contiene de dictamen.nombre='Dictamen6' a 'Dictamen8'
    // Contiene de convocatoriaReunion.codigo='CR-6' a 'CR-8'
    Assertions.assertThat(evaluaciones.get(0).getMemoria().getTitulo()).isEqualTo("Memoria6");
    Assertions.assertThat(evaluaciones.get(0).getDictamen().getNombre()).isEqualTo("Dictamen6");
    Assertions.assertThat(evaluaciones.get(0).getConvocatoriaReunion().getId()).isEqualTo(6L);
    Assertions.assertThat(evaluaciones.get(1).getMemoria().getTitulo()).isEqualTo("Memoria7");
    Assertions.assertThat(evaluaciones.get(1).getDictamen().getNombre()).isEqualTo("Dictamen7");
    Assertions.assertThat(evaluaciones.get(1).getConvocatoriaReunion().getId()).isEqualTo(7L);
    Assertions.assertThat(evaluaciones.get(2).getMemoria().getTitulo()).isEqualTo("Memoria8");
    Assertions.assertThat(evaluaciones.get(2).getDictamen().getNombre()).isEqualTo("Dictamen8");
    Assertions.assertThat(evaluaciones.get(2).getConvocatoriaReunion().getId()).isEqualTo(8L);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredEvaluacionList() throws Exception {
    // when: Búsqueda por esRevMinima equals e id equals
    Long id = 5L;
    String query = "esRevMinima:true,id:" + id;

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V")));

    URI uri = UriComponentsBuilder.fromUriString(EVALUACION_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(1);
    Assertions.assertThat(evaluaciones.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(evaluaciones.get(0).getMemoria().getTitulo()).startsWith("Memoria");
    Assertions.assertThat(evaluaciones.get(0).getDictamen().getNombre()).startsWith("Dictamen");
    Assertions.assertThat(evaluaciones.get(0).getConvocatoriaReunion().getId()).isEqualTo(5L);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedEvaluacionList() throws Exception {
    // when: Ordenación por id desc
    String query = "id-";

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V")));

    URI uri = UriComponentsBuilder.fromUriString(EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      Evaluacion evaluacion = evaluaciones.get(i);
      Assertions.assertThat(evaluacion.getId()).isEqualTo(8 - i);
      Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", 8 - i));
      Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen" + String.format("%03d", 8 - i));
      Assertions.assertThat(evaluacion.getConvocatoriaReunion().getId()).isEqualTo(8 - i);
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsEvaluacionSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-V")));
    // when: Ordena por id desc
    String sort = "id-";
    // when: Filtra por version equals
    String filter = "version:2";

    URI uri = UriComponentsBuilder.fromUriString(EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Evaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<Evaluacion>>() {
        });

    // then: Respuesta OK, Evaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Evaluacion> evaluaciones = response.getBody();
    Assertions.assertThat(evaluaciones.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene memoria.titulo='Memoria001', 'Memoria002','Memoria003'
    // Contiene dictamen.nombre='Dictamen001', 'Dictamen002','Dictamen003'
    // Contiene convocatoriaReunion.codigo='CR-001', 'CR-002','CR-003'
    Assertions.assertThat(evaluaciones.get(0).getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", 3));
    Assertions.assertThat(evaluaciones.get(1).getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", 2));
    Assertions.assertThat(evaluaciones.get(2).getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", 1));

    Assertions.assertThat(evaluaciones.get(0).getDictamen().getNombre())
        .isEqualTo("Dictamen" + String.format("%03d", 3));
    Assertions.assertThat(evaluaciones.get(1).getDictamen().getNombre())
        .isEqualTo("Dictamen" + String.format("%03d", 2));
    Assertions.assertThat(evaluaciones.get(2).getDictamen().getNombre())
        .isEqualTo("Dictamen" + String.format("%03d", 1));
  }

  /**
   * Función que devuelve un objeto Evaluacion
   * 
   * @param id     id del Evaluacion
   * @param sufijo el sufijo para título y nombre
   * @return el objeto Evaluacion
   */

  public Evaluacion generarMockEvaluacion(Long id, String sufijo) {

    String sufijoStr = (sufijo == null ? id.toString() : sufijo);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setNombre("TipoEvaluacion1");
    tipoEvaluacion.setActivo(Boolean.TRUE);

    Dictamen dictamen = new Dictamen();
    dictamen.setId(1L);
    dictamen.setNombre("Dictamen" + sufijoStr);
    dictamen.setTipoEvaluacion(tipoEvaluacion);
    dictamen.setActivo(Boolean.TRUE);

    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(1L);
    peticionEvaluacion.setCodigo("Codigo1");
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico1");
    peticionEvaluacion.setExterno(Boolean.FALSE);
    peticionEvaluacion.setFechaFin(LocalDate.now());
    peticionEvaluacion.setFechaInicio(LocalDate.now());
    peticionEvaluacion.setFuenteFinanciacion("Fuente financiación");
    peticionEvaluacion.setObjetivos("Objetivos1");
    peticionEvaluacion.setOtroValorSocial("Otro valor social1");
    peticionEvaluacion.setResumen("Resumen");
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo("PeticionEvaluacion1");
    peticionEvaluacion.setUsuarioRef("user-001");
    peticionEvaluacion.setValorSocial(3);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(1L);
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    Memoria memoria = new Memoria(1L, "numRef-001", peticionEvaluacion, comite, "Memoria" + sufijoStr, "user-00" + id,
        tipoMemoria, new TipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), LocalDate.now(), Boolean.FALSE,
        new Retrospectiva(1L, new EstadoRetrospectiva(1L, "Pendiente", Boolean.TRUE), LocalDate.now()), 3,
        Boolean.TRUE);

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);

    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(1L);
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(LocalDateTime.now());
    convocatoriaReunion.setFechaLimite(LocalDate.now());
    convocatoriaReunion.setLugar("Lugar");
    convocatoriaReunion.setOrdenDia("Orden del día convocatoria reunión");
    convocatoriaReunion.setAnio(2020);
    convocatoriaReunion.setNumeroActa(100L);
    convocatoriaReunion.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    convocatoriaReunion.setHoraInicio(7);
    convocatoriaReunion.setMinutoInicio(30);
    convocatoriaReunion.setFechaEnvio(LocalDate.now());
    convocatoriaReunion.setActivo(Boolean.TRUE);

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    evaluacion.setDictamen(dictamen);
    evaluacion.setEsRevMinima(Boolean.TRUE);
    evaluacion.setFechaDictamen(LocalDate.now());
    evaluacion.setMemoria(memoria);
    evaluacion.setConvocatoriaReunion(convocatoriaReunion);
    evaluacion.setVersion(2);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);
    evaluacion.setActivo(Boolean.TRUE);

    return evaluacion;
  }

}