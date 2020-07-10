package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.util.ConstantesEti;
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

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getDictamen_WithId_ReturnsDictamen() throws Exception {
    final ResponseEntity<Dictamen> response = restTemplate.getForEntity(
        ConstantesEti.DICTAMEN_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, Dictamen.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Dictamen dictamen = response.getBody();

    Assertions.assertThat(dictamen.getId()).isEqualTo(1L);
    Assertions.assertThat(dictamen.getNombre()).isEqualTo("Dictamen1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addDictamen_ReturnsDictamen() throws Exception {

    Dictamen nuevoDictamen = new Dictamen();
    nuevoDictamen.setNombre("Dictamen1");
    nuevoDictamen.setActivo(Boolean.TRUE);

    restTemplate.postForEntity(ConstantesEti.DICTAMEN_CONTROLLER_BASE_PATH, nuevoDictamen, Dictamen.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeDictamen_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<Dictamen> response = restTemplate.exchange(
        ConstantesEti.DICTAMEN_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, HttpMethod.DELETE, null,
        Dictamen.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeDictamen_DoNotGetDictamen() throws Exception {
    restTemplate.delete(ConstantesEti.DICTAMEN_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, 1L);

    final ResponseEntity<Dictamen> response = restTemplate.getForEntity(
        ConstantesEti.DICTAMEN_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, Dictamen.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceDictamen_ReturnsDictamen() throws Exception {

    Dictamen replaceDictamen = generarMockDictamen(1L, "Dictamen1");

    final HttpEntity<Dictamen> requestEntity = new HttpEntity<Dictamen>(replaceDictamen, new HttpHeaders());

    final ResponseEntity<Dictamen> response = restTemplate.exchange(

        ConstantesEti.DICTAMEN_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, HttpMethod.PUT, requestEntity,
        Dictamen.class, 1L);

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
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.DICTAMEN_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<Dictamen>> response = restTemplate.exchange(uri, HttpMethod.GET,
        new HttpEntity<>(headers), new ParameterizedTypeReference<List<Dictamen>>() {
        });

    // then: Respuesta OK, Dictamens retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Dictamen> dictamens = response.getBody();
    Assertions.assertThat(dictamens.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de nombre='Dictamen6' a 'Dictamen8'
    Assertions.assertThat(dictamens.get(0).getNombre()).isEqualTo("Dictamen6");
    Assertions.assertThat(dictamens.get(1).getNombre()).isEqualTo("Dictamen7");
    Assertions.assertThat(dictamens.get(2).getNombre()).isEqualTo("Dictamen8");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredDictamenList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 5L;
    String query = "nombre~Dictamen%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.DICTAMEN_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

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
    Assertions.assertThat(dictamens.get(0).getNombre()).startsWith("Dictamen");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedDictamenList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre-";

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.DICTAMEN_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Dictamen>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<Dictamen>>() {
        });

    // then: Respuesta OK, Dictamens retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Dictamen> dictamens = response.getBody();
    Assertions.assertThat(dictamens.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      Dictamen dictamen = dictamens.get(i);
      Assertions.assertThat(dictamen.getId()).isEqualTo(8 - i);
      Assertions.assertThat(dictamen.getNombre()).isEqualTo("Dictamen" + String.format("%03d", 8 - i));
    }
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
    // when: Filtra por nombre like e id equals
    String filter = "nombre~%00%";

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.DICTAMEN_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Dictamen>> response = restTemplate.exchange(uri, HttpMethod.GET,
        new HttpEntity<>(headers), new ParameterizedTypeReference<List<Dictamen>>() {
        });

    // then: Respuesta OK, Dictamens retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Dictamen> dictamens = response.getBody();
    Assertions.assertThat(dictamens.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene nombre='Dictamen001', 'Dictamen002', 'Dictamen003'
    Assertions.assertThat(dictamens.get(0).getNombre()).isEqualTo("Dictamen" + String.format("%03d", 3));
    Assertions.assertThat(dictamens.get(1).getNombre()).isEqualTo("Dictamen" + String.format("%03d", 2));
    Assertions.assertThat(dictamens.get(2).getNombre()).isEqualTo("Dictamen" + String.format("%03d", 1));

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