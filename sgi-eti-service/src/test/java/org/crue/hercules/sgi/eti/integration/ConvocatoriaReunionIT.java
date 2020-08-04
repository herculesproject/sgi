package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
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
 * Test de integracion de ConvocatoriaReunion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class ConvocatoriaReunionIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH = "/convocatoriareuniones";

  private HttpEntity<ConvocatoriaReunion> buildRequest(HttpHeaders headers, ConvocatoriaReunion entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    if (!headers.containsKey("Authorization")) {
      headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user")));
    }

    HttpEntity<ConvocatoriaReunion> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsConvocatoriaReunion() throws Exception {

    // given: Nueva entidad
    final ConvocatoriaReunion newConvocatoriaReunion = getMockData(1L, 1L, 1L);
    newConvocatoriaReunion.setId(null);

    // when: Se crea la entidad
    final ResponseEntity<ConvocatoriaReunion> response = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH, HttpMethod.POST, buildRequest(null, newConvocatoriaReunion),
        ConvocatoriaReunion.class);

    // then: La entidad se crea correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    final ConvocatoriaReunion convocatoriaReunion = response.getBody();
    Assertions.assertThat(convocatoriaReunion.getId()).isNotNull();
    newConvocatoriaReunion.setId(convocatoriaReunion.getId());
    Assertions.assertThat(convocatoriaReunion).isEqualTo(newConvocatoriaReunion);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_WithExistingId_ReturnsConvocatoriaReunion() throws Exception {

    // given: Entidad existente que se va a actualizar
    final ConvocatoriaReunion updatedConvocatoriaReunion = getMockData(2L, 1L, 2L);
    updatedConvocatoriaReunion.setId(1L);

    // when: Se actualiza la entidad
    final ResponseEntity<ConvocatoriaReunion> response = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, updatedConvocatoriaReunion), ConvocatoriaReunion.class, updatedConvocatoriaReunion.getId());

    // then: Los datos se actualizan correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(updatedConvocatoriaReunion);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_WithExistingId_Return204() throws Exception {

    // given: Entidad existente con la propiedad activo a true
    Long id = 1L;

    ResponseEntity<ConvocatoriaReunion> response = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ConvocatoriaReunion.class, id);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().getActivo()).isEqualTo(Boolean.TRUE);

    // when: Se elimina la entidad
    response = restTemplate.exchange(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE,
        buildRequest(null, null), ConvocatoriaReunion.class, id);

    // then: La entidad pasa a tener propiedad activo a false
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    response = restTemplate.exchange(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET,
        buildRequest(null, null), ConvocatoriaReunion.class, id);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().getActivo()).isEqualTo(Boolean.FALSE);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_WithExistingId_ReturnsConvocatoriaReunion() throws Exception {

    // given: Entidad con un determinado Id
    final ConvocatoriaReunion convocatoriaReunion = getMockData(1L, 1L, 1L);

    // when: Se busca la entidad por ese Id
    ResponseEntity<ConvocatoriaReunion> response = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ConvocatoriaReunion.class, convocatoriaReunion.getId());

    // then: Se recupera la entidad con el Id
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(convocatoriaReunion);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_WithNotExistingId_Returns404() throws Exception {

    // given: No existe entidad con el id indicado
    Long id = 1L;

    // when: Se busca la entidad por ese Id
    ResponseEntity<ConvocatoriaReunion> response = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ConvocatoriaReunion.class, id);

    // then: Se produce error porque no encuentra la entidad con ese Id
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_Unlimited_ReturnsFullConvocatoriaReunionList() throws Exception {

    // given: Datos existentes
    List<ConvocatoriaReunion> response = new LinkedList<>();
    response.add(getMockData(1L, 1L, 1L));
    response.add(getMockData(2L, 1L, 2L));

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-CNV-V")));

    // when: Se buscan todos los datos
    final ResponseEntity<List<ConvocatoriaReunion>> result = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<ConvocatoriaReunion>>() {
        });

    // then: Se recuperan todos los datos
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsConvocatoriaReunionSubList() throws Exception {

    // given: Datos existentes
    List<ConvocatoriaReunion> response = new LinkedList<>();
    response.add(getMockData(5L, 3L, 1L));

    // página 2 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "2");
    headers.add("X-Page-Size", "2");
    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-CNV-V")));

    // when: Se buscan los datos paginados
    final ResponseEntity<List<ConvocatoriaReunion>> result = restTemplate.exchange(
        CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<ConvocatoriaReunion>>() {
        });

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody().size()).isEqualTo(1);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).isEqualTo("3");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).isEqualTo("5");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredConvocatoriaReunionList() throws Exception {

    // given: Datos existentes
    List<ConvocatoriaReunion> response = new LinkedList<>();
    response.add(getMockData(3L, 2L, 1L));

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-CNV-V")));

    // search by codigo like, id equals
    Long id = 3L;
    String query = "numeroActa<4,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Se buscan los datos con el filtro indicado
    final ResponseEntity<List<ConvocatoriaReunion>> result = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaReunion>>() {
        });

    // then: Se recuperan los datos filtrados
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).isEqualTo("1");

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedConvocatoriaReunionList() throws Exception {

    // Authorization
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-CNV-V")));

    // sort by id desc
    String sort = "id-";

    URI uri = UriComponentsBuilder.fromUriString(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .build(false).toUri();

    // when: Se buscan los datos con la ordenación indicada
    final ResponseEntity<List<ConvocatoriaReunion>> result = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaReunion>>() {
        });

    // then: Se recuperan los datos filtrados, ordenados y paginados
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody().get(0).getId()).isEqualTo(5L);
    Assertions.assertThat(result.getBody().get(4).getId()).isEqualTo(1L);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("5");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).isEqualTo("5");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsConvocatoriaReunionSubList() throws Exception {

    // given: Datos existentes
    List<ConvocatoriaReunion> response = new LinkedList<>();
    response.add(getMockData(1L, 1L, 1L));

    // página 1 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    // Authorization
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "ETI-ACT-C", "ETI-CNV-V")));

    // sort
    String sort = "id-";

    // search
    String query = "numeroActa<4";

    URI uri = UriComponentsBuilder.fromUriString(CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", query).build(false).toUri();

    // when: Se buscan los datos paginados con el filtro y orden indicados
    final ResponseEntity<List<ConvocatoriaReunion>> result = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConvocatoriaReunion>>() {
        });

    // then: Se recuperan los datos filtrados, ordenados y paginados
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody().size()).as("size").isEqualTo(1);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).as("X-Page").isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).as("X-Page-Total-Count").isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).as("X-Page-Count").isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");
  }

  /**
   * Genera un objeto {@link ConvocatoriaReunion}
   * 
   * @param id
   * @param comiteId
   * @param tipoId
   * @return ConvocatoriaReunion
   */
  private ConvocatoriaReunion getMockData(Long id, Long comiteId, Long tipoId) {

    Comite comite = new Comite(comiteId, "Comite" + comiteId, Boolean.TRUE);

    String tipo_txt = (tipoId == 1L) ? "Ordinaria" : (tipoId == 2L) ? "Extraordinaria" : "Seguimiento";
    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(tipoId, tipo_txt, Boolean.TRUE);

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final ConvocatoriaReunion data = new ConvocatoriaReunion();
    data.setId(id);
    data.setComite(comite);
    data.setFechaEvaluacion(LocalDateTime.of(2020, 7, id.intValue(), 0, 0, 0));
    data.setFechaLimite(LocalDate.of(2020, 8, id.intValue()));
    data.setLugar("Lugar " + txt);
    data.setOrdenDia("Orden del día convocatoria reunión " + txt);
    data.setAnio(2020);
    data.setNumeroActa(id);
    data.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    data.setHoraInicio(7 + id.intValue());
    data.setMinutoInicio(30);
    data.setFechaEnvio(LocalDate.of(2020, 7, 13));
    data.setActivo(Boolean.TRUE);

    return data;
  }
}
