package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoActividad;
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
 * Test de integracion de TipoActividad.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TipoActividadIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getTipoActividad_WithId_ReturnsTipoActividad() throws Exception {
    final ResponseEntity<TipoActividad> response = restTemplate.getForEntity(
        ConstantesEti.TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, TipoActividad.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoActividad tipoActividad = response.getBody();

    Assertions.assertThat(tipoActividad.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoActividad.getNombre()).isEqualTo("Proyecto de investigación");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addTipoActividad_ReturnsTipoActividad() throws Exception {

    TipoActividad nuevoTipoActividad = new TipoActividad();
    nuevoTipoActividad.setNombre("TipoActividad1");
    nuevoTipoActividad.setActivo(Boolean.TRUE);

    restTemplate.postForEntity(ConstantesEti.TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH, nuevoTipoActividad,
        TipoActividad.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoActividad_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<TipoActividad> response = restTemplate.exchange(
        ConstantesEti.TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, HttpMethod.DELETE, null,
        TipoActividad.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoActividad_DoNotGetTipoActividad() throws Exception {
    restTemplate.delete(ConstantesEti.TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, 1L);

    final ResponseEntity<TipoActividad> response = restTemplate.getForEntity(
        ConstantesEti.TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, TipoActividad.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceTipoActividad_ReturnsTipoActividad() throws Exception {

    TipoActividad replaceTipoActividad = generarMockTipoActividad(1L, "TipoActividad1");

    final HttpEntity<TipoActividad> requestEntity = new HttpEntity<TipoActividad>(replaceTipoActividad,
        new HttpHeaders());

    final ResponseEntity<TipoActividad> response = restTemplate.exchange(

        ConstantesEti.TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, HttpMethod.PUT,
        requestEntity, TipoActividad.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoActividad tipoActividad = response.getBody();

    Assertions.assertThat(tipoActividad.getId()).isNotNull();
    Assertions.assertThat(tipoActividad.getNombre()).isEqualTo(replaceTipoActividad.getNombre());
    Assertions.assertThat(tipoActividad.getActivo()).isEqualTo(replaceTipoActividad.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsTipoActividadSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "3");

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH).build(false)
        .toUri();

    final ResponseEntity<List<TipoActividad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        new HttpEntity<>(headers), new ParameterizedTypeReference<List<TipoActividad>>() {
        });

    // then: Respuesta OK, TipoActividades retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoActividad> tipoActividades = response.getBody();
    Assertions.assertThat(tipoActividades.size()).isEqualTo(2);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("5");

    // Contiene de nombre='Trabajo Fin de Máster' y 'Trabajo Fin de Grado'
    Assertions.assertThat(tipoActividades.get(0).getNombre()).isEqualTo("Trabajo Fin de Máster");
    Assertions.assertThat(tipoActividades.get(1).getNombre()).isEqualTo("Trabajo Fin de Grado");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoActividadList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 3L;
    String query = "nombre~Tesis%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH)
        .queryParam("q", query).build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoActividad>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<TipoActividad>>() {
        });

    // then: Respuesta OK, TipoActividades retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoActividad> tipoActividades = response.getBody();
    Assertions.assertThat(tipoActividades.size()).isEqualTo(1);
    Assertions.assertThat(tipoActividades.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(tipoActividades.get(0).getNombre()).startsWith("Tesis doctoral");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoActividadList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre-";

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH)
        .queryParam("s", query).build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoActividad>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<TipoActividad>>() {
        });

    // then: Respuesta OK, TipoActividades retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoActividad> tipoActividades = response.getBody();
    Assertions.assertThat(tipoActividades.size()).isEqualTo(5);
    Assertions.assertThat(tipoActividades.get(0).getId()).isEqualTo(4);
    Assertions.assertThat(tipoActividades.get(0).getNombre()).isEqualTo("Trabajo Fin de Máster");
    Assertions.assertThat(tipoActividades.get(4).getId()).isEqualTo(1);
    Assertions.assertThat(tipoActividades.get(4).getNombre()).isEqualTo("Proyecto de investigación");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoActividadSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "4");
    // when: Ordena por nombre desc
    String sort = "nombre-";
    // when: Filtra por nombre like
    String filter = "nombre~%Trabajo%";

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH)
        .queryParam("s", sort).queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoActividad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        new HttpEntity<>(headers), new ParameterizedTypeReference<List<TipoActividad>>() {
        });

    // then: Respuesta OK, TipoActividades retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoActividad> tipoActividades = response.getBody();
    Assertions.assertThat(tipoActividades.size()).isEqualTo(2);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("4");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("2");

    // Contiene nombre='Trabajo Fin de Grado', 'Trabajo Fin de Máster'
    Assertions.assertThat(tipoActividades.get(0).getNombre()).isEqualTo("Trabajo Fin de Máster");
    Assertions.assertThat(tipoActividades.get(1).getNombre()).isEqualTo("Trabajo Fin de Grado");

  }

  /**
   * Función que devuelve un objeto TipoActividad
   * 
   * @param id     id del tipoActividad
   * @param nombre la descripción del tipo de actividad
   * @return el objeto tipo actividad
   */

  public TipoActividad generarMockTipoActividad(Long id, String nombre) {

    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(id);
    tipoActividad.setNombre(nombre);
    tipoActividad.setActivo(Boolean.TRUE);

    return tipoActividad;
  }

}