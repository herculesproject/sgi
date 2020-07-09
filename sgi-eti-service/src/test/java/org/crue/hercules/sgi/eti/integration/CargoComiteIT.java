package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.CargoComite;
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
 * Test de integracion de CargoComite.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CargoComiteIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getCargoComite_WithId_ReturnsCargoComite() throws Exception {
    final ResponseEntity<CargoComite> response = restTemplate.getForEntity(
        ConstantesEti.CARGO_COMITE_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, CargoComite.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final CargoComite cargoComite = response.getBody();

    Assertions.assertThat(cargoComite.getId()).isEqualTo(1L);
    Assertions.assertThat(cargoComite.getNombre()).isEqualTo("CargoComite1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addCargoComite_ReturnsCargoComite() throws Exception {

    CargoComite nuevoCargoComite = new CargoComite();
    nuevoCargoComite.setNombre("CargoComite1");
    nuevoCargoComite.setActivo(Boolean.TRUE);

    restTemplate.postForEntity(ConstantesEti.CARGO_COMITE_CONTROLLER_BASE_PATH, nuevoCargoComite, CargoComite.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeCargoComite_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<CargoComite> response = restTemplate.exchange(
        ConstantesEti.CARGO_COMITE_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, HttpMethod.DELETE, null,
        CargoComite.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeCargoComite_DoNotGetCargoComite() throws Exception {
    restTemplate.delete(ConstantesEti.CARGO_COMITE_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, 1L);

    final ResponseEntity<CargoComite> response = restTemplate.getForEntity(
        ConstantesEti.CARGO_COMITE_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, CargoComite.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceCargoComite_ReturnsCargoComite() throws Exception {

    CargoComite replaceCargoComite = generarMockCargoComite(1L, "CargoComite1");

    final HttpEntity<CargoComite> requestEntity = new HttpEntity<CargoComite>(replaceCargoComite, new HttpHeaders());

    final ResponseEntity<CargoComite> response = restTemplate.exchange(

        ConstantesEti.CARGO_COMITE_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, HttpMethod.PUT,
        requestEntity, CargoComite.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final CargoComite cargoComite = response.getBody();

    Assertions.assertThat(cargoComite.getId()).isNotNull();
    Assertions.assertThat(cargoComite.getNombre()).isEqualTo(replaceCargoComite.getNombre());
    Assertions.assertThat(cargoComite.getActivo()).isEqualTo(replaceCargoComite.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsCargoComiteSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.CARGO_COMITE_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<CargoComite>> response = restTemplate.exchange(uri, HttpMethod.GET,
        new HttpEntity<>(headers), new ParameterizedTypeReference<List<CargoComite>>() {
        });

    // then: Respuesta OK, CargoComites retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<CargoComite> cargoComites = response.getBody();
    Assertions.assertThat(cargoComites.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de nombre='CargoComite6' a 'CargoComite8'
    Assertions.assertThat(cargoComites.get(0).getNombre()).isEqualTo("CargoComite6");
    Assertions.assertThat(cargoComites.get(1).getNombre()).isEqualTo("CargoComite7");
    Assertions.assertThat(cargoComites.get(2).getNombre()).isEqualTo("CargoComite8");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredCargoComiteList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 5L;
    String query = "nombre~CargoComite%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.CARGO_COMITE_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<CargoComite>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<CargoComite>>() {
        });

    // then: Respuesta OK, CargoComites retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<CargoComite> cargoComites = response.getBody();
    Assertions.assertThat(cargoComites.size()).isEqualTo(1);
    Assertions.assertThat(cargoComites.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(cargoComites.get(0).getNombre()).startsWith("CargoComite");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedCargoComiteList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre-";

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.CARGO_COMITE_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<CargoComite>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<CargoComite>>() {
        });

    // then: Respuesta OK, CargoComites retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<CargoComite> cargoComites = response.getBody();
    Assertions.assertThat(cargoComites.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      CargoComite cargoComite = cargoComites.get(i);
      Assertions.assertThat(cargoComite.getId()).isEqualTo(8 - i);
      Assertions.assertThat(cargoComite.getNombre()).isEqualTo("CargoComite" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsCargoComiteSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "nombre-";
    // when: Filtra por nombre like e id equals
    String filter = "nombre~%00%";

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.CARGO_COMITE_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<CargoComite>> response = restTemplate.exchange(uri, HttpMethod.GET,
        new HttpEntity<>(headers), new ParameterizedTypeReference<List<CargoComite>>() {
        });

    // then: Respuesta OK, CargoComites retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<CargoComite> cargoComites = response.getBody();
    Assertions.assertThat(cargoComites.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene nombre='CargoComite001', 'CargoComite002',
    // 'CargoComite003'
    Assertions.assertThat(cargoComites.get(0).getNombre()).isEqualTo("CargoComite" + String.format("%03d", 3));
    Assertions.assertThat(cargoComites.get(1).getNombre()).isEqualTo("CargoComite" + String.format("%03d", 2));
    Assertions.assertThat(cargoComites.get(2).getNombre()).isEqualTo("CargoComite" + String.format("%03d", 1));

  }

  /**
   * Función que devuelve un objeto CargoComite
   * 
   * @param id     id del cargoComite
   * @param nombre el nombre del cargo comité
   * @return el objeto cargo comité
   */

  public CargoComite generarMockCargoComite(Long id, String nombre) {

    CargoComite cargoComite = new CargoComite();
    cargoComite.setId(id);
    cargoComite.setNombre(nombre);
    cargoComite.setActivo(Boolean.TRUE);

    return cargoComite;
  }

}