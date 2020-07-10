package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoComentario;
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
 * Test de integracion de TipoComentario.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TipoComentarioIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getTipoComentario_WithId_ReturnsTipoComentario() throws Exception {
    final ResponseEntity<TipoComentario> response = restTemplate.getForEntity(
        ConstantesEti.TIPO_COMENTARIO_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, TipoComentario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoComentario tipoComentario = response.getBody();

    Assertions.assertThat(tipoComentario.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoComentario.getNombre()).isEqualTo("TipoComentario1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addTipoComentario_ReturnsTipoComentario() throws Exception {

    TipoComentario nuevoTipoComentario = new TipoComentario();
    nuevoTipoComentario.setNombre("TipoComentario1");
    nuevoTipoComentario.setActivo(Boolean.TRUE);

    restTemplate.postForEntity(ConstantesEti.TIPO_COMENTARIO_CONTROLLER_BASE_PATH, nuevoTipoComentario,
        TipoComentario.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoComentario_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<TipoComentario> response = restTemplate.exchange(
        ConstantesEti.TIPO_COMENTARIO_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, HttpMethod.DELETE, null,
        TipoComentario.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoComentario_DoNotGetTipoComentario() throws Exception {
    restTemplate.delete(ConstantesEti.TIPO_COMENTARIO_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, 1L);

    final ResponseEntity<TipoComentario> response = restTemplate.getForEntity(
        ConstantesEti.TIPO_COMENTARIO_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, TipoComentario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceTipoComentario_ReturnsTipoComentario() throws Exception {

    TipoComentario replaceTipoComentario = generarMockTipoComentario(1L, "TipoComentario1");

    final HttpEntity<TipoComentario> requestEntity = new HttpEntity<TipoComentario>(replaceTipoComentario,
        new HttpHeaders());

    final ResponseEntity<TipoComentario> response = restTemplate.exchange(

        ConstantesEti.TIPO_COMENTARIO_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, HttpMethod.PUT,
        requestEntity, TipoComentario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoComentario tipoComentario = response.getBody();

    Assertions.assertThat(tipoComentario.getId()).isNotNull();
    Assertions.assertThat(tipoComentario.getNombre()).isEqualTo(replaceTipoComentario.getNombre());
    Assertions.assertThat(tipoComentario.getActivo()).isEqualTo(replaceTipoComentario.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsTipoComentarioSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.TIPO_COMENTARIO_CONTROLLER_BASE_PATH).build(false)
        .toUri();

    final ResponseEntity<List<TipoComentario>> response = restTemplate.exchange(uri, HttpMethod.GET,
        new HttpEntity<>(headers), new ParameterizedTypeReference<List<TipoComentario>>() {
        });

    // then: Respuesta OK, TipoComentarios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoComentario> tipoComentarios = response.getBody();
    Assertions.assertThat(tipoComentarios.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de nombre='TipoComentario6' a 'TipoComentario8'
    Assertions.assertThat(tipoComentarios.get(0).getNombre()).isEqualTo("TipoComentario6");
    Assertions.assertThat(tipoComentarios.get(1).getNombre()).isEqualTo("TipoComentario7");
    Assertions.assertThat(tipoComentarios.get(2).getNombre()).isEqualTo("TipoComentario8");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoComentarioList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 5L;
    String query = "nombre~TipoComentario%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.TIPO_COMENTARIO_CONTROLLER_BASE_PATH)
        .queryParam("q", query).build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoComentario>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<TipoComentario>>() {
        });

    // then: Respuesta OK, TipoComentarios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoComentario> tipoComentarios = response.getBody();
    Assertions.assertThat(tipoComentarios.size()).isEqualTo(1);
    Assertions.assertThat(tipoComentarios.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(tipoComentarios.get(0).getNombre()).startsWith("TipoComentario");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoComentarioList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre-";

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.TIPO_COMENTARIO_CONTROLLER_BASE_PATH)
        .queryParam("s", query).build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoComentario>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<TipoComentario>>() {
        });

    // then: Respuesta OK, TipoComentarios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoComentario> tipoComentarios = response.getBody();
    Assertions.assertThat(tipoComentarios.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      TipoComentario tipoComentario = tipoComentarios.get(i);
      Assertions.assertThat(tipoComentario.getId()).isEqualTo(8 - i);
      Assertions.assertThat(tipoComentario.getNombre()).isEqualTo("TipoComentario" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoComentarioSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "nombre-";
    // when: Filtra por nombre like e id equals
    String filter = "nombre~%00%";

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.TIPO_COMENTARIO_CONTROLLER_BASE_PATH)
        .queryParam("s", sort).queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoComentario>> response = restTemplate.exchange(uri, HttpMethod.GET,
        new HttpEntity<>(headers), new ParameterizedTypeReference<List<TipoComentario>>() {
        });

    // then: Respuesta OK, TipoComentarios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoComentario> tipoComentarios = response.getBody();
    Assertions.assertThat(tipoComentarios.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene nombre='TipoComentario001', 'TipoComentario002',
    // 'TipoComentario003'
    Assertions.assertThat(tipoComentarios.get(0).getNombre()).isEqualTo("TipoComentario" + String.format("%03d", 3));
    Assertions.assertThat(tipoComentarios.get(1).getNombre()).isEqualTo("TipoComentario" + String.format("%03d", 2));
    Assertions.assertThat(tipoComentarios.get(2).getNombre()).isEqualTo("TipoComentario" + String.format("%03d", 1));

  }

  /**
   * Función que devuelve un objeto TipoComentario
   * 
   * @param id     id del TipoComentario
   * @param nombre la descripción del TipoComentario
   * @return el objeto TipoComentario
   */

  public TipoComentario generarMockTipoComentario(Long id, String nombre) {

    TipoComentario tipoComentario = new TipoComentario();
    tipoComentario.setId(id);
    tipoComentario.setNombre(nombre);
    tipoComentario.setActivo(Boolean.TRUE);

    return tipoComentario;
  }

}