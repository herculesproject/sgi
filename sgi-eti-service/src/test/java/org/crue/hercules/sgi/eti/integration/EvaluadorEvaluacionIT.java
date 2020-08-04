package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.EvaluadorEvaluacion;
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
 * Test de integracion de EvaluadorEvaluacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class EvaluadorEvaluacionIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH = "/evaluadorevaluaciones";

  private HttpEntity<EvaluadorEvaluacion> buildRequest(HttpHeaders headers, EvaluadorEvaluacion entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-EVALUADOREVALUACION-EDITAR", "ETI-EVALUADOREVALUACION-VER")));

    HttpEntity<EvaluadorEvaluacion> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getEvaluadorEvaluacion_WithId_ReturnsEvaluadorEvaluacion() throws Exception {
    final ResponseEntity<EvaluadorEvaluacion> response = restTemplate.exchange(
        EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        EvaluadorEvaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final EvaluadorEvaluacion EvaluadorEvaluacion = response.getBody();

    Assertions.assertThat(EvaluadorEvaluacion.getId()).isEqualTo(1L);
    Assertions.assertThat(EvaluadorEvaluacion.getEvaluador().getResumen()).isEqualTo("Evaluador1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addEvaluadorEvaluacion_ReturnsEvaluadorEvaluacion() throws Exception {

    EvaluadorEvaluacion nuevoEvaluadorEvaluacion = generarMockEvaluadorEvaluacion(null);

    final ResponseEntity<EvaluadorEvaluacion> response = restTemplate.exchange(
        EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH, HttpMethod.POST, buildRequest(null, nuevoEvaluadorEvaluacion),
        EvaluadorEvaluacion.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeEvaluadorEvaluacion_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<EvaluadorEvaluacion> response = restTemplate.exchange(
        EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        EvaluadorEvaluacion.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeEvaluadorEvaluacion_DoNotGetEvaluadorEvaluacion() throws Exception {

    final ResponseEntity<EvaluadorEvaluacion> response = restTemplate.exchange(
        EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        EvaluadorEvaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceEvaluadorEvaluacion_ReturnsEvaluadorEvaluacion() throws Exception {

    EvaluadorEvaluacion replaceEvaluadorEvaluacion = generarMockEvaluadorEvaluacion(1L);

    final ResponseEntity<EvaluadorEvaluacion> response = restTemplate.exchange(
        EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, replaceEvaluadorEvaluacion), EvaluadorEvaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final EvaluadorEvaluacion evaluadorEvaluacion = response.getBody();

    Assertions.assertThat(evaluadorEvaluacion.getId()).isNotNull();
    Assertions.assertThat(evaluadorEvaluacion.getEvaluador()).isEqualTo(replaceEvaluadorEvaluacion.getEvaluador());
    Assertions.assertThat(evaluadorEvaluacion.getEvaluacion()).isEqualTo(replaceEvaluadorEvaluacion.getEvaluacion());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsEvaluadorEvaluacionSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    final ResponseEntity<List<EvaluadorEvaluacion>> response = restTemplate.exchange(
        EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<EvaluadorEvaluacion>>() {
        });

    // then: Respuesta OK, EvaluadorEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EvaluadorEvaluacion> EvaluadorEvaluaciones = response.getBody();
    Assertions.assertThat(EvaluadorEvaluaciones.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de id='6' a '8'
    Assertions.assertThat(EvaluadorEvaluaciones.get(0).getId()).isEqualTo(6);
    Assertions.assertThat(EvaluadorEvaluaciones.get(1).getId()).isEqualTo(7);
    Assertions.assertThat(EvaluadorEvaluaciones.get(2).getId()).isEqualTo(8);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredEvaluadorEvaluacionList() throws Exception {
    // when: Búsqueda por resumen like e id equals
    Long id = 5L;
    String query = "id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<EvaluadorEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<EvaluadorEvaluacion>>() {
        });

    // then: Respuesta OK, EvaluadorEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EvaluadorEvaluacion> EvaluadorEvaluaciones = response.getBody();
    Assertions.assertThat(EvaluadorEvaluaciones.size()).isEqualTo(1);
    Assertions.assertThat(EvaluadorEvaluaciones.get(0).getId()).isEqualTo(id);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedEvaluadorEvaluacionList() throws Exception {
    // when: Ordenación por id desc
    String query = "id-";

    URI uri = UriComponentsBuilder.fromUriString(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<EvaluadorEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<EvaluadorEvaluacion>>() {
        });

    // then: Respuesta OK, EvaluadorEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EvaluadorEvaluacion> EvaluadorEvaluaciones = response.getBody();
    Assertions.assertThat(EvaluadorEvaluaciones.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      EvaluadorEvaluacion evaluadorEvaluacion = EvaluadorEvaluaciones.get(i);
      Assertions.assertThat(evaluadorEvaluacion.getId()).isEqualTo(8 - i);
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsEvaluadorEvaluacionSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "1");
    // when: Ordena por resumen desc
    String sort = "id-";
    // when: Filtra por id equals
    Long id = 5L;
    String filter = "id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<EvaluadorEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<EvaluadorEvaluacion>>() {
        });

    // then: Respuesta OK, EvaluadorEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EvaluadorEvaluacion> EvaluadorEvaluaciones = response.getBody();
    Assertions.assertThat(EvaluadorEvaluaciones.size()).isEqualTo(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("1");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("1");

    // Contiene id='1'
    Assertions.assertThat(EvaluadorEvaluaciones.get(0).getId()).isEqualTo(5);
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

  /**
   * Función que devuelve un objeto EvaluadorEvaluacion
   * 
   * @param id id del EvaluadorEvaluacion
   * @return el objeto EvaluadorEvaluacion
   */

  public EvaluadorEvaluacion generarMockEvaluadorEvaluacion(Long id) {
    CargoComite cargoComite = new CargoComite();
    cargoComite.setId(1L);
    cargoComite.setNombre("CargoComite1");
    cargoComite.setActivo(Boolean.TRUE);

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    Evaluador evaluador = new Evaluador();
    evaluador.setId(1L);
    evaluador.setCargoComite(cargoComite);
    evaluador.setComite(comite);
    evaluador.setFechaAlta(LocalDate.now());
    evaluador.setFechaBaja(LocalDate.now());
    evaluador.setResumen("Evaluador");
    evaluador.setUsuarioRef("user-001");
    evaluador.setActivo(Boolean.TRUE);

    Evaluacion evaluacion = generarMockEvaluacion(1L, "1");

    EvaluadorEvaluacion evaluadorEvaluacion = new EvaluadorEvaluacion(id, evaluador, evaluacion);

    return evaluadorEvaluacion;
  }

}