package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.BloqueFormulario;
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
 * Test de integracion de BloqueFormulario.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BloqueFormularioIT {

  @Autowired
  private TestRestTemplate restTemplate;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH = "/bloqueformularios";

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getBloqueFormulario_WithId_ReturnsBloqueFormulario() throws Exception {
    final ResponseEntity<BloqueFormulario> response = restTemplate
        .getForEntity(BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, BloqueFormulario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final BloqueFormulario bloqueFormulario = response.getBody();

    Assertions.assertThat(bloqueFormulario.getId()).isEqualTo(1L);
    Assertions.assertThat(bloqueFormulario.getNombre()).isEqualTo("BloqueFormulario1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addBloqueFormulario_ReturnsBloqueFormulario() throws Exception {

    BloqueFormulario nuevoBloqueFormulario = new BloqueFormulario();
    nuevoBloqueFormulario.setNombre("BloqueFormulario1");
    nuevoBloqueFormulario.setActivo(Boolean.TRUE);

    restTemplate.postForEntity(BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH, nuevoBloqueFormulario, BloqueFormulario.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeBloqueFormulario_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<BloqueFormulario> response = restTemplate.exchange(
        BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, null, BloqueFormulario.class,
        id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeBloqueFormulario_DoNotGetBloqueFormulario() throws Exception {
    restTemplate.delete(BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L);

    final ResponseEntity<BloqueFormulario> response = restTemplate
        .getForEntity(BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, BloqueFormulario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceBloqueFormulario_ReturnsBloqueFormulario() throws Exception {

    BloqueFormulario replaceBloqueFormulario = generarMockBloqueFormulario(1L, "BloqueFormulario1");

    final HttpEntity<BloqueFormulario> requestEntity = new HttpEntity<BloqueFormulario>(replaceBloqueFormulario,
        new HttpHeaders());

    final ResponseEntity<BloqueFormulario> response = restTemplate.exchange(

        BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, requestEntity,
        BloqueFormulario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final BloqueFormulario bloqueFormulario = response.getBody();

    Assertions.assertThat(bloqueFormulario.getId()).isNotNull();
    Assertions.assertThat(bloqueFormulario.getNombre()).isEqualTo(replaceBloqueFormulario.getNombre());
    Assertions.assertThat(bloqueFormulario.getActivo()).isEqualTo(replaceBloqueFormulario.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsBloqueFormularioSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<BloqueFormulario>> response = restTemplate.exchange(uri, HttpMethod.GET,
        new HttpEntity<>(headers), new ParameterizedTypeReference<List<BloqueFormulario>>() {
        });

    // then: Respuesta OK, BloqueFormularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<BloqueFormulario> bloqueFormularios = response.getBody();
    Assertions.assertThat(bloqueFormularios.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de nombre='BloqueFormulario6' a 'BloqueFormulario8'
    Assertions.assertThat(bloqueFormularios.get(0).getNombre()).isEqualTo("BloqueFormulario6");
    Assertions.assertThat(bloqueFormularios.get(1).getNombre()).isEqualTo("BloqueFormulario7");
    Assertions.assertThat(bloqueFormularios.get(2).getNombre()).isEqualTo("BloqueFormulario8");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredBloqueFormularioList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 5L;
    String query = "nombre~BloqueFormulario%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<BloqueFormulario>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<BloqueFormulario>>() {
        });

    // then: Respuesta OK, BloqueFormularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<BloqueFormulario> bloqueFormularios = response.getBody();
    Assertions.assertThat(bloqueFormularios.size()).isEqualTo(1);
    Assertions.assertThat(bloqueFormularios.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(bloqueFormularios.get(0).getNombre()).startsWith("BloqueFormulario");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedBloqueFormularioList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre-";

    URI uri = UriComponentsBuilder.fromUriString(BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<BloqueFormulario>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<BloqueFormulario>>() {
        });

    // then: Respuesta OK, BloqueFormularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<BloqueFormulario> bloqueFormularios = response.getBody();
    Assertions.assertThat(bloqueFormularios.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      BloqueFormulario bloqueFormulario = bloqueFormularios.get(i);
      Assertions.assertThat(bloqueFormulario.getId()).isEqualTo(8 - i);
      Assertions.assertThat(bloqueFormulario.getNombre()).isEqualTo("BloqueFormulario" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsBloqueFormularioSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "nombre-";
    // when: Filtra por nombre like
    String filter = "nombre~%00%";

    URI uri = UriComponentsBuilder.fromUriString(BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<BloqueFormulario>> response = restTemplate.exchange(uri, HttpMethod.GET,
        new HttpEntity<>(headers), new ParameterizedTypeReference<List<BloqueFormulario>>() {
        });

    // then: Respuesta OK, BloqueFormularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<BloqueFormulario> bloqueFormularios = response.getBody();
    Assertions.assertThat(bloqueFormularios.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene nombre='BloqueFormulario001', 'BloqueFormulario002',
    // 'BloqueFormulario003'
    Assertions.assertThat(bloqueFormularios.get(0).getNombre())
        .isEqualTo("BloqueFormulario" + String.format("%03d", 3));
    Assertions.assertThat(bloqueFormularios.get(1).getNombre())
        .isEqualTo("BloqueFormulario" + String.format("%03d", 2));
    Assertions.assertThat(bloqueFormularios.get(2).getNombre())
        .isEqualTo("BloqueFormulario" + String.format("%03d", 1));

  }

  /**
   * Función que devuelve un objeto BloqueFormulario
   * 
   * @param id     id del BloqueFormulario
   * @param nombre el nombre de BloqueFormulario
   * @return el objeto BloqueFormulario
   */

  public BloqueFormulario generarMockBloqueFormulario(Long id, String nombre) {

    Formulario formulario = new Formulario();
    formulario.setId(1L);
    formulario.setNombre("Formulario1");
    formulario.setDescripcion("Descripcion formulario 1");
    formulario.setActivo(Boolean.TRUE);

    BloqueFormulario bloqueFormulario = new BloqueFormulario();
    bloqueFormulario.setId(id);
    bloqueFormulario.setFormulario(formulario);
    bloqueFormulario.setNombre(nombre);
    bloqueFormulario.setOrden(1);
    bloqueFormulario.setActivo(Boolean.TRUE);

    return bloqueFormulario;
  }

}