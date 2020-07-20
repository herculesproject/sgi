package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Dictamen;
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
 * Test de integracion de Dictamen.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DictamenIT {

  @Autowired
  private TestRestTemplate restTemplate;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String DICTAMEN_CONTROLLER_BASE_PATH = "/dictamenes";

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getDictamen_WithId_ReturnsDictamen() throws Exception {
    final ResponseEntity<Dictamen> response = restTemplate
        .getForEntity(DICTAMEN_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, Dictamen.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Dictamen dictamen = response.getBody();

    Assertions.assertThat(dictamen.getId()).isEqualTo(1L);
    Assertions.assertThat(dictamen.getNombre()).isEqualTo("Favorable");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addDictamen_ReturnsDictamen() throws Exception {

    Dictamen nuevoDictamen = new Dictamen();
    nuevoDictamen.setNombre("Dictamen1");
    nuevoDictamen.setActivo(Boolean.TRUE);

    restTemplate.postForEntity(DICTAMEN_CONTROLLER_BASE_PATH, nuevoDictamen, Dictamen.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeDictamen_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<Dictamen> response = restTemplate.exchange(DICTAMEN_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE, null, Dictamen.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeDictamen_DoNotGetDictamen() throws Exception {
    restTemplate.delete(DICTAMEN_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L);

    final ResponseEntity<Dictamen> response = restTemplate
        .getForEntity(DICTAMEN_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, Dictamen.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceDictamen_ReturnsDictamen() throws Exception {

    Dictamen replaceDictamen = generarMockDictamen(1L, "Dictamen1");

    final HttpEntity<Dictamen> requestEntity = new HttpEntity<Dictamen>(replaceDictamen, new HttpHeaders());

    final ResponseEntity<Dictamen> response = restTemplate.exchange(

        DICTAMEN_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, requestEntity, Dictamen.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Dictamen dictamen = response.getBody();

    Assertions.assertThat(dictamen.getId()).isNotNull();
    Assertions.assertThat(dictamen.getNombre()).isEqualTo(replaceDictamen.getNombre());
    Assertions.assertThat(dictamen.getActivo()).isEqualTo(replaceDictamen.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsDictamenSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    URI uri = UriComponentsBuilder.fromUriString(DICTAMEN_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<Dictamen>> response = restTemplate.exchange(uri, HttpMethod.GET,
        new HttpEntity<>(headers), new ParameterizedTypeReference<List<Dictamen>>() {
        });

    // then: Respuesta OK, Dictamens retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Dictamen> dictamens = response.getBody();
    Assertions.assertThat(dictamens.size()).isEqualTo(2);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("4");

    // Contiene de nombre='Pendiente de correcciones' a 'Dictamen8'
    Assertions.assertThat(dictamens.get(0).getNombre()).isEqualTo("Pendiente de correcciones");
    Assertions.assertThat(dictamens.get(1).getNombre()).isEqualTo("No procede evaluar");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredDictamenList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 2L;
    String query = "nombre~favorable%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(DICTAMEN_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Dictamen>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<Dictamen>>() {
        });

    // then: Respuesta OK, Dictamens retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Dictamen> dictamens = response.getBody();
    Assertions.assertThat(dictamens.size()).isEqualTo(1);
    Assertions.assertThat(dictamens.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(dictamens.get(0).getNombre()).startsWith("Favorable");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedDictamenList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre-";

    URI uri = UriComponentsBuilder.fromUriString(DICTAMEN_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Dictamen>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<Dictamen>>() {
        });

    // then: Respuesta OK, Dictamens retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Dictamen> dictamens = response.getBody();
    Assertions.assertThat(dictamens.size()).isEqualTo(4);
    Assertions.assertThat(dictamens.get(0).getId()).isEqualTo(3);
    Assertions.assertThat(dictamens.get(0).getNombre()).isEqualTo("Pendiente de correcciones");
    Assertions.assertThat(dictamens.get(3).getId()).isEqualTo(1);
    Assertions.assertThat(dictamens.get(3).getNombre()).isEqualTo("Favorable");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsDictamenSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "nombre-";
    // when: Filtra por nombre like
    String filter = "nombre~%pendiente%";

    URI uri = UriComponentsBuilder.fromUriString(DICTAMEN_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Dictamen>> response = restTemplate.exchange(uri, HttpMethod.GET,
        new HttpEntity<>(headers), new ParameterizedTypeReference<List<Dictamen>>() {
        });

    // then: Respuesta OK, Dictamens retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Dictamen> dictamens = response.getBody();
    Assertions.assertThat(dictamens.size()).isEqualTo(2);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("2");

    // Contiene nombre='Favorable pendiente de revisión mínima', 'Favorable'
    Assertions.assertThat(dictamens.get(0).getNombre()).isEqualTo("Pendiente de correcciones");
    Assertions.assertThat(dictamens.get(1).getNombre()).isEqualTo("Favorable pendiente de revisión mínima");

  }

  /**
   * Función que devuelve un objeto Dictamen
   * 
   * @param id     id del dictamen
   * @param nombre la descripción del dictamen
   * @return el objeto dictamen
   */

  public Dictamen generarMockDictamen(Long id, String nombre) {

    Dictamen dictamen = new Dictamen();
    dictamen.setId(id);
    dictamen.setNombre(nombre);
    dictamen.setActivo(Boolean.TRUE);

    return dictamen;
  }

}