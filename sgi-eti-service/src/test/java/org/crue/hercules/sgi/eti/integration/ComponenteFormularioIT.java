package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.ComponenteFormulario;
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
 * Test de integracion de ComponenteFormulario.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ComponenteFormularioIT {

  @Autowired
  private TestRestTemplate restTemplate;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH = "/componenteformularios";

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsComponenteFormulario() throws Exception {

    // given: Nueva entidad
    final ComponenteFormulario newComponenteFormulario = getMockData(1L);
    newComponenteFormulario.setId(null);

    final String url = new StringBuilder(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH).toString();

    // when: Se crea la entidad
    final ResponseEntity<ComponenteFormulario> response = restTemplate.postForEntity(url, newComponenteFormulario,
        ComponenteFormulario.class);

    // then: La entidad se crea correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    final ComponenteFormulario componenteFormulario = response.getBody();
    Assertions.assertThat(componenteFormulario.getId()).isNotNull();
    newComponenteFormulario.setId(componenteFormulario.getId());
    Assertions.assertThat(componenteFormulario).isEqualTo(newComponenteFormulario);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_WithExistingId_ReturnsComponenteFormulario() throws Exception {

    // given: Entidad existente que se va a actualizar
    final ComponenteFormulario updatedComponenteFormulario = getMockData(2L);
    updatedComponenteFormulario.setId(1L);

    final String url = new StringBuilder(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();

    HttpEntity<ComponenteFormulario> request = new HttpEntity<>(updatedComponenteFormulario);

    // when: Se actualiza la entidad
    final ResponseEntity<ComponenteFormulario> response = restTemplate.exchange(url, HttpMethod.PUT, request,
        ComponenteFormulario.class, updatedComponenteFormulario.getId());

    // then: Los datos se actualizan correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(updatedComponenteFormulario);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_WithExistingId_Return204() throws Exception {

    // given: Entidad existente
    Long id = 1L;

    final String url = new StringBuilder(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();

    ResponseEntity<ComponenteFormulario> response = restTemplate.getForEntity(url, ComponenteFormulario.class, id);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    // when: Se elimina la entidad
    response = restTemplate.exchange(url, HttpMethod.DELETE, null, ComponenteFormulario.class, id);

    // then: La entidad se elimina correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    response = restTemplate.getForEntity(url, ComponenteFormulario.class, id);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_WithExistingId_ReturnsComponenteFormulario() throws Exception {

    // given: Entidad con un determinado Id
    final ComponenteFormulario componenteFormulario = getMockData(1L);

    final String url = new StringBuilder(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();

    // when: Se busca la entidad por ese Id
    ResponseEntity<ComponenteFormulario> response = restTemplate.getForEntity(url, ComponenteFormulario.class,
        componenteFormulario.getId());

    // then: Se recupera la entidad con el Id
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(componenteFormulario);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_WithNotExistingId_Returns404() throws Exception {

    // given: No existe entidad con el id indicado
    Long id = 1L;
    final String url = new StringBuilder(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();

    // when: Se busca la entidad por ese Id
    ResponseEntity<ComponenteFormulario> response = restTemplate.getForEntity(url, ComponenteFormulario.class, id);

    // then: Se produce error porque no encuentra la entidad con ese Id
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_Unlimited_ReturnsFullComponenteFormularioList() throws Exception {

    // given: Datos existentes
    List<ComponenteFormulario> response = new LinkedList<>();
    response.add(getMockData(1L));
    response.add(getMockData(2L));

    final String url = new StringBuilder(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH).toString();

    // when: Se buscan todos los datos
    final ResponseEntity<List<ComponenteFormulario>> result = restTemplate.exchange(url, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<ComponenteFormulario>>() {
        });

    // then: Se recuperan todos los datos
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsComponenteFormularioSubList() throws Exception {

    // given: Datos existentes
    List<ComponenteFormulario> response = new LinkedList<>();
    response.add(getMockData(5L));

    // página 2 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "2");
    headers.add("X-Page-Size", "2");

    final String url = new StringBuilder(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH).toString();

    // when: Se buscan los datos paginados
    final ResponseEntity<List<ComponenteFormulario>> result = restTemplate.exchange(url, HttpMethod.GET,
        new HttpEntity<>(headers), new ParameterizedTypeReference<List<ComponenteFormulario>>() {
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
  public void findAll_WithSearchQuery_ReturnsFilteredComponenteFormularioList() throws Exception {

    // given: Datos existentes
    List<ComponenteFormulario> response = new LinkedList<>();
    response.add(getMockData(3L));

    // search by codigo like, id equals
    Long id = 3L;
    String query = "esquema~EsquemaComponenteFormulario0%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Se buscan los datos con el filtro indicado
    final ResponseEntity<List<ComponenteFormulario>> result = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<ComponenteFormulario>>() {
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
  public void findAll_WithSortQuery_ReturnsOrderedComponenteFormularioList() throws Exception {

    // given: Datos existentes

    // sort by id desc
    String sort = "id-";

    URI uri = UriComponentsBuilder.fromUriString(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .build(false).toUri();

    // when: Se buscan los datos con la ordenación indicada
    final ResponseEntity<List<ComponenteFormulario>> result = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<ComponenteFormulario>>() {
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
  public void findAll_WithPagingSortingAndFiltering_ReturnsComponenteFormularioSubList() throws Exception {

    // given: Datos existentes
    List<ComponenteFormulario> response = new LinkedList<>();
    response.add(getMockData(1L));

    // página 1 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    // sort
    String sort = "id-";

    // search
    String query = "esquema~EsquemaComponenteFormulario0%";

    URI uri = UriComponentsBuilder.fromUriString(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", query).build(false).toUri();

    // when: Se buscan los datos paginados con el filtro y orden indicados
    final ResponseEntity<List<ComponenteFormulario>> result = restTemplate.exchange(uri, HttpMethod.GET,
        new HttpEntity<>(headers), new ParameterizedTypeReference<List<ComponenteFormulario>>() {
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
   * Genera un objeto {@link ComponenteFormulario}
   * 
   * @param id
   * @return ComponenteFormulario
   */
  private ComponenteFormulario getMockData(Long id) {

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final ComponenteFormulario data = new ComponenteFormulario();
    data.setId(id);
    data.setEsquema("EsquemaComponenteFormulario" + txt);

    return data;
  }
}
