package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
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
 * Test de integracion de TipoConvocatoriaReunion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TipoConvocatoriaReunionIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getTipoConvocatoriaReunion_WithId_ReturnsTipoConvocatoriaReunion() throws Exception {
    final ResponseEntity<TipoConvocatoriaReunion> response = restTemplate.getForEntity(
        ConstantesEti.TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID,
        TipoConvocatoriaReunion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoConvocatoriaReunion tipoConvocatoriaReunion = response.getBody();

    Assertions.assertThat(tipoConvocatoriaReunion.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoConvocatoriaReunion.getNombre()).isEqualTo("TipoConvocatoriaReunion1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addTipoConvocatoriaReunion_ReturnsTipoConvocatoriaReunion() throws Exception {

    TipoConvocatoriaReunion nuevoTipoConvocatoriaReunion = new TipoConvocatoriaReunion();
    nuevoTipoConvocatoriaReunion.setNombre("TipoConvocatoriaReunion1");
    nuevoTipoConvocatoriaReunion.setActivo(Boolean.TRUE);

    restTemplate.postForEntity(ConstantesEti.TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH,
        nuevoTipoConvocatoriaReunion, TipoConvocatoriaReunion.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoConvocatoriaReunion_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<TipoConvocatoriaReunion> response = restTemplate.exchange(
        ConstantesEti.TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID,
        HttpMethod.DELETE, null, TipoConvocatoriaReunion.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoConvocatoriaReunion_DoNotGetTipoConvocatoriaReunion() throws Exception {
    restTemplate.delete(ConstantesEti.TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID,
        1L);

    final ResponseEntity<TipoConvocatoriaReunion> response = restTemplate.getForEntity(
        ConstantesEti.TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID,
        TipoConvocatoriaReunion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceTipoConvocatoriaReunion_ReturnsTipoConvocatoriaReunion() throws Exception {

    TipoConvocatoriaReunion replaceTipoConvocatoriaReunion = generarMockTipoConvocatoriaReunion(1L,
        "TipoConvocatoriaReunion1");

    final HttpEntity<TipoConvocatoriaReunion> requestEntity = new HttpEntity<TipoConvocatoriaReunion>(
        replaceTipoConvocatoriaReunion, new HttpHeaders());

    final ResponseEntity<TipoConvocatoriaReunion> response = restTemplate.exchange(

        ConstantesEti.TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, HttpMethod.PUT,
        requestEntity, TipoConvocatoriaReunion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoConvocatoriaReunion tipoConvocatoriaReunion = response.getBody();

    Assertions.assertThat(tipoConvocatoriaReunion.getId()).isNotNull();
    Assertions.assertThat(tipoConvocatoriaReunion.getNombre()).isEqualTo(replaceTipoConvocatoriaReunion.getNombre());
    Assertions.assertThat(tipoConvocatoriaReunion.getActivo()).isEqualTo(replaceTipoConvocatoriaReunion.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsTipoConvocatoriaReunionSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .build(false).toUri();

    final ResponseEntity<List<TipoConvocatoriaReunion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        new HttpEntity<>(headers), new ParameterizedTypeReference<List<TipoConvocatoriaReunion>>() {
        });

    // then: Respuesta OK, TipoConvocatoriaReunions retorna la información de la
    // página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoConvocatoriaReunion> tipoConvocatoriaReunions = response.getBody();
    Assertions.assertThat(tipoConvocatoriaReunions.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de nombre='TipoConvocatoriaReunion6' a 'TipoConvocatoriaReunion8'
    Assertions.assertThat(tipoConvocatoriaReunions.get(0).getNombre()).isEqualTo("TipoConvocatoriaReunion6");
    Assertions.assertThat(tipoConvocatoriaReunions.get(1).getNombre()).isEqualTo("TipoConvocatoriaReunion7");
    Assertions.assertThat(tipoConvocatoriaReunions.get(2).getNombre()).isEqualTo("TipoConvocatoriaReunion8");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoConvocatoriaReunionList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 5L;
    String query = "nombre~TipoConvocatoriaReunion%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .queryParam("q", query).build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoConvocatoriaReunion>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<TipoConvocatoriaReunion>>() {
        });

    // then: Respuesta OK, TipoConvocatoriaReunions retorna la información de la
    // página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoConvocatoriaReunion> tipoConvocatoriaReunions = response.getBody();
    Assertions.assertThat(tipoConvocatoriaReunions.size()).isEqualTo(1);
    Assertions.assertThat(tipoConvocatoriaReunions.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(tipoConvocatoriaReunions.get(0).getNombre()).startsWith("TipoConvocatoriaReunion");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoConvocatoriaReunionList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre-";

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .queryParam("s", query).build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoConvocatoriaReunion>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<TipoConvocatoriaReunion>>() {
        });

    // then: Respuesta OK, TipoConvocatoriaReunions retorna la información de la
    // página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoConvocatoriaReunion> tipoConvocatoriaReunions = response.getBody();
    Assertions.assertThat(tipoConvocatoriaReunions.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      TipoConvocatoriaReunion tipoConvocatoriaReunion = tipoConvocatoriaReunions.get(i);
      Assertions.assertThat(tipoConvocatoriaReunion.getId()).isEqualTo(8 - i);
      Assertions.assertThat(tipoConvocatoriaReunion.getNombre())
          .isEqualTo("TipoConvocatoriaReunion" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoConvocatoriaReunionSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "nombre-";
    // when: Filtra por nombre like e id equals
    String filter = "nombre~%00%";

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.TIPO_CONVOCATORIA_REUNION_CONTROLLER_BASE_PATH)
        .queryParam("s", sort).queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoConvocatoriaReunion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        new HttpEntity<>(headers), new ParameterizedTypeReference<List<TipoConvocatoriaReunion>>() {
        });

    // then: Respuesta OK, TipoConvocatoriaReunions retorna la información de la
    // página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoConvocatoriaReunion> tipoConvocatoriaReunions = response.getBody();
    Assertions.assertThat(tipoConvocatoriaReunions.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene nombre='TipoConvocatoriaReunion001', 'TipoConvocatoriaReunion002',
    // 'TipoConvocatoriaReunion003'
    Assertions.assertThat(tipoConvocatoriaReunions.get(0).getNombre())
        .isEqualTo("TipoConvocatoriaReunion" + String.format("%03d", 3));
    Assertions.assertThat(tipoConvocatoriaReunions.get(1).getNombre())
        .isEqualTo("TipoConvocatoriaReunion" + String.format("%03d", 2));
    Assertions.assertThat(tipoConvocatoriaReunions.get(2).getNombre())
        .isEqualTo("TipoConvocatoriaReunion" + String.format("%03d", 1));

  }

  /**
   * Función que devuelve un objeto TipoConvocatoriaReunion
   * 
   * @param id     id del TipoConvocatoriaReunion
   * @param nombre la descripción del TipoConvocatoriaReunion
   * @return el objeto TipoConvocatoriaReunion
   */

  public TipoConvocatoriaReunion generarMockTipoConvocatoriaReunion(Long id, String nombre) {

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion();
    tipoConvocatoriaReunion.setId(id);
    tipoConvocatoriaReunion.setNombre(nombre);
    tipoConvocatoriaReunion.setActivo(Boolean.TRUE);

    return tipoConvocatoriaReunion;
  }

}