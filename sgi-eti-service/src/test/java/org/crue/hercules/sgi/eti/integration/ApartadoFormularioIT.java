package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.ApartadoFormulario;
import org.crue.hercules.sgi.eti.model.BloqueFormulario;
import org.crue.hercules.sgi.eti.model.ComponenteFormulario;
import org.crue.hercules.sgi.eti.model.Formulario;
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

/**
 * Test de integracion de ApartadoFormulario.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApartadoFormularioIT {

  @Autowired
  private TestRestTemplate restTemplate;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String APARTADO_FORMULARIO_CONTROLLER_BASE_PATH = "/apartadoformularios";

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsApartadoFormulario() throws Exception {

    // given: Nueva entidad
    final ApartadoFormulario newApartadoFormulario = getMockData(2L, 1L, 1L, null);
    newApartadoFormulario.setId(null);

    final String url = new StringBuilder(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH).toString();

    // when: Se crea la entidad
    final ResponseEntity<ApartadoFormulario> response = restTemplate.postForEntity(url, newApartadoFormulario,
        ApartadoFormulario.class);

    // then: La entidad se crea correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    final ApartadoFormulario apartadoFormulario = response.getBody();
    Assertions.assertThat(apartadoFormulario.getId()).isNotNull();
    newApartadoFormulario.setId(apartadoFormulario.getId());
    Assertions.assertThat(apartadoFormulario).isEqualTo(newApartadoFormulario);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_WithExistingId_ReturnsApartadoFormulario() throws Exception {

    // given: Entidad existente que se va a actualizar
    final ApartadoFormulario updatedApartadoFormulario = getMockData(3L, 1L, 1L, 1L);
    updatedApartadoFormulario.setId(2L);

    final String url = new StringBuilder(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();

    HttpEntity<ApartadoFormulario> request = new HttpEntity<>(updatedApartadoFormulario);

    // when: Se actualiza la entidad
    final ResponseEntity<ApartadoFormulario> response = restTemplate.exchange(url, HttpMethod.PUT, request,
        ApartadoFormulario.class, updatedApartadoFormulario.getId());

    // then: Los datos se actualizan correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(updatedApartadoFormulario);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_WithExistingId_Return204() throws Exception {

    // given: Entidad existente con la propiedad activo a true
    Long id = 1L;

    final String url = new StringBuilder(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();

    ResponseEntity<ApartadoFormulario> response = restTemplate.getForEntity(url, ApartadoFormulario.class, id);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().getActivo()).isEqualTo(Boolean.TRUE);

    // when: Se elimina la entidad
    response = restTemplate.exchange(url, HttpMethod.DELETE, null, ApartadoFormulario.class, id);

    // then: La entidad pasa a tener propiedad activo a false
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    response = restTemplate.getForEntity(url, ApartadoFormulario.class, id);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody().getActivo()).isEqualTo(Boolean.FALSE);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_WithExistingId_ReturnsApartadoFormulario() throws Exception {

    // given: Entidad con un determinado Id
    final ApartadoFormulario apartadoFormulario = getMockData(1L, 1L, 1L, null);

    final String url = new StringBuilder(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();

    // when: Se busca la entidad por ese Id
    ResponseEntity<ApartadoFormulario> response = restTemplate.getForEntity(url, ApartadoFormulario.class,
        apartadoFormulario.getId());

    // then: Se recupera la entidad con el Id
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(apartadoFormulario);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_WithNotExistingId_Returns404() throws Exception {

    // given: No existe entidad con el id indicado
    Long id = 1L;
    final String url = new StringBuilder(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();

    // when: Se busca la entidad por ese Id
    ResponseEntity<ApartadoFormulario> response = restTemplate.getForEntity(url, ApartadoFormulario.class, id);

    // then: Se produce error porque no encuentra la entidad con ese Id
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_Unlimited_ReturnsFullApartadoFormularioList() throws Exception {

    // given: Datos existentes
    List<ApartadoFormulario> response = new LinkedList<>();
    response.add(getMockData(1L, 1L, 1L, null));
    response.add(getMockData(2L, 1L, 1L, 1L));

    final String url = new StringBuilder(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH).toString();

    // when: Se buscan todos los datos
    final ResponseEntity<List<ApartadoFormulario>> result = restTemplate.exchange(url, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<ApartadoFormulario>>() {
        });

    // then: Se recuperan todos los datos
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsApartadoFormularioSubList() throws Exception {

    // given: Datos existentes
    List<ApartadoFormulario> response = new LinkedList<>();
    response.add(getMockData(5L, 2L, 2L, 4L));

    // página 2 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "2");
    headers.add("X-Page-Size", "2");

    final String url = new StringBuilder(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH).toString();

    // when: Se buscan los datos paginados
    final ResponseEntity<List<ApartadoFormulario>> result = restTemplate.exchange(url, HttpMethod.GET,
        new HttpEntity<>(headers), new ParameterizedTypeReference<List<ApartadoFormulario>>() {
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
  public void findAll_WithSearchQuery_ReturnsFilteredApartadoFormularioList() throws Exception {

    // given: Datos existentes
    List<ApartadoFormulario> response = new LinkedList<>();
    response.add(getMockData(3L, 1L, 1L, 1L));

    // search by codigo like, id equals
    Long id = 3L;
    String query = "nombre~ApartadoFormulario0%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Se buscan los datos con el filtro indicado
    final ResponseEntity<List<ApartadoFormulario>> result = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<ApartadoFormulario>>() {
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
  public void findAll_WithSortQuery_ReturnsOrderedApartadoFormularioList() throws Exception {

    // given: Datos existentes

    // sort by id desc
    String sort = "id-";

    URI uri = UriComponentsBuilder.fromUriString(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .build(false).toUri();

    // when: Se buscan los datos con la ordenación indicada
    final ResponseEntity<List<ApartadoFormulario>> result = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<ApartadoFormulario>>() {
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
  public void findAll_WithPagingSortingAndFiltering_ReturnsApartadoFormularioSubList() throws Exception {

    // given: Datos existentes
    List<ApartadoFormulario> response = new LinkedList<>();
    response.add(getMockData(1L, 1L, 1L, null));

    // página 1 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    // sort
    String sort = "id-";

    // search
    String query = "nombre~ApartadoFormulario0%";

    URI uri = UriComponentsBuilder.fromUriString(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", query).build(false).toUri();

    // when: Se buscan los datos paginados con el filtro y orden indicados
    final ResponseEntity<List<ApartadoFormulario>> result = restTemplate.exchange(uri, HttpMethod.GET,
        new HttpEntity<>(headers), new ParameterizedTypeReference<List<ApartadoFormulario>>() {
        });

    // then: Se recuperan los datos filtrados, ordenados y paginados
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody().size()).isEqualTo(1);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");
  }

  /**
   * Genera un objeto {@link ApartadoFormulario}
   * 
   * @param id
   * @param bloqueFormularioId
   * @param componenteFormularioId
   * @param apartadoFormularioPadreId
   * @return ApartadoFormulario
   */
  private ApartadoFormulario getMockData(Long id, Long bloqueFormularioId, Long componenteFormularioId,
      Long apartadoFormularioPadreId) {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion1", Boolean.TRUE);
    BloqueFormulario bloqueFormulario = new BloqueFormulario(bloqueFormularioId, formulario,
        "BloqueFormulario" + bloqueFormularioId, bloqueFormularioId.intValue(), Boolean.TRUE);
    ComponenteFormulario componenteFormulario = new ComponenteFormulario(componenteFormularioId,
        "EsquemaComponenteFormulario" + componenteFormularioId);

    ApartadoFormulario apartadoFormularioPadre = (apartadoFormularioPadreId != null)
        ? getMockData(apartadoFormularioPadreId, bloqueFormularioId, componenteFormularioId, null)
        : null;

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final ApartadoFormulario data = new ApartadoFormulario();
    data.setId(id);
    data.setBloqueFormulario(bloqueFormulario);
    data.setNombre("ApartadoFormulario" + txt);
    data.setApartadoFormularioPadre(apartadoFormularioPadre);
    data.setOrden(id.intValue());
    data.setComponenteFormulario(componenteFormulario);
    data.setActivo(Boolean.TRUE);

    return data;
  }
}
