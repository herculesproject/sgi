package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.ApartadoFormulario;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.Evaluacion;
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
 * Test de integracion de Comentario.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ComentarioIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getComentario_WithId_ReturnsComentario() throws Exception {
    final ResponseEntity<Comentario> response = restTemplate.getForEntity(
        ConstantesEti.COMENTARIO_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, Comentario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Comentario comentario = response.getBody();

    Assertions.assertThat(comentario.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(comentario.getApartadoFormulario()).as("apartadoFormulario").isNotNull();
    Assertions.assertThat(comentario.getApartadoFormulario().getId()).as("apartadoFormulario.id").isEqualTo(100L);
    Assertions.assertThat(comentario.getEvaluacion()).as("evaluacion").isNotNull();
    Assertions.assertThat(comentario.getEvaluacion().getId()).as("evaluacion.id").isEqualTo(200L);
    Assertions.assertThat(comentario.getTipoComentario()).as("tipoComentario").isNotNull();
    Assertions.assertThat(comentario.getTipoComentario().getId()).as("tipoComentario.id").isEqualTo(300L);
    Assertions.assertThat(comentario.getTexto()).as("texto").isEqualTo("Comentario1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addComentario_ReturnsComentario() throws Exception {

    Comentario nuevoComentario = generarMockComentario(null, "Comentario");

    final ResponseEntity<Comentario> response = restTemplate
        .postForEntity(ConstantesEti.COMENTARIO_CONTROLLER_BASE_PATH, nuevoComentario, Comentario.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    final Comentario comentario = response.getBody();

    Assertions.assertThat(comentario.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(comentario.getApartadoFormulario()).as("apartadoFormulario").isNotNull();
    Assertions.assertThat(comentario.getApartadoFormulario().getId()).as("apartadoFormulario.id").isEqualTo(100L);
    Assertions.assertThat(comentario.getEvaluacion()).as("evaluacion").isNotNull();
    Assertions.assertThat(comentario.getEvaluacion().getId()).as("evaluacion.id").isEqualTo(200L);
    Assertions.assertThat(comentario.getTipoComentario()).as("tipoComentario").isNotNull();
    Assertions.assertThat(comentario.getTipoComentario().getId()).as("tipoComentario.id").isEqualTo(300L);
    Assertions.assertThat(comentario.getTexto()).as("texto").isEqualTo("Comentario");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeComentario_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<Comentario> response = restTemplate.exchange(
        ConstantesEti.COMENTARIO_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, HttpMethod.DELETE, null,
        Comentario.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeComentario_DoNotGetComentario() throws Exception {
    restTemplate.delete(ConstantesEti.COMENTARIO_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, 1L);

    final ResponseEntity<Comentario> response = restTemplate.getForEntity(
        ConstantesEti.COMENTARIO_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, Comentario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceComentario_ReturnsComentario() throws Exception {

    Comentario replaceComentario = generarMockComentario(1L, "Comentario1 actualizado");

    final HttpEntity<Comentario> requestEntity = new HttpEntity<Comentario>(replaceComentario, new HttpHeaders());

    final ResponseEntity<Comentario> response = restTemplate.exchange(
        ConstantesEti.COMENTARIO_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, HttpMethod.PUT, requestEntity,
        Comentario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Comentario comentario = response.getBody();

    Assertions.assertThat(comentario.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(comentario.getApartadoFormulario()).as("apartadoFormulario").isNotNull();
    Assertions.assertThat(comentario.getApartadoFormulario().getId()).as("apartadoFormulario.id").isEqualTo(100L);
    Assertions.assertThat(comentario.getEvaluacion()).as("evaluacion").isNotNull();
    Assertions.assertThat(comentario.getEvaluacion().getId()).as("evaluacion.id").isEqualTo(200L);
    Assertions.assertThat(comentario.getTipoComentario()).as("tipoComentario").isNotNull();
    Assertions.assertThat(comentario.getTipoComentario().getId()).as("tipoComentario.id").isEqualTo(300L);
    Assertions.assertThat(comentario.getTexto()).as("texto").isEqualTo("Comentario1 actualizado");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsComentarioSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.COMENTARIO_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<Comentario>> response = restTemplate.exchange(uri, HttpMethod.GET,
        new HttpEntity<>(headers), new ParameterizedTypeReference<List<Comentario>>() {
        });

    // then: Respuesta OK, comentarios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Comentario> comentarios = response.getBody();
    Assertions.assertThat(comentarios.size()).as("size").isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).as("x-page").isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).as("x-page-size").isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).as("x-total-count").isEqualTo("8");

    // Contiene de texto='Comentario6' a 'Comentario8'
    Assertions.assertThat(comentarios.get(0).getTexto()).as("0.texto").isEqualTo("Comentario6");
    Assertions.assertThat(comentarios.get(1).getTexto()).as("1.texto").isEqualTo("Comentario7");
    Assertions.assertThat(comentarios.get(2).getTexto()).as("2.texto").isEqualTo("Comentario8");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredComentarioList() throws Exception {
    // when: Búsqueda por comentario like e id equals
    Long id = 5L;
    String query = "texto~Comentario%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.COMENTARIO_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Comentario>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<Comentario>>() {
        });

    // then: Respuesta OK, Comentarios retorna la información de la página correcta
    // en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Comentario> comentarios = response.getBody();
    Assertions.assertThat(comentarios.size()).as("size").isEqualTo(1);
    Assertions.assertThat(comentarios.get(0).getId()).as("id").isEqualTo(id);
    Assertions.assertThat(comentarios.get(0).getTexto()).as("comentario").startsWith("Comentario5");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedComentarioList() throws Exception {
    // when: Ordenación por texto desc
    String sort = "texto-";

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.COMENTARIO_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Comentario>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<Comentario>>() {
        });

    // then: Respuesta OK, Comentarios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Comentario> comentarios = response.getBody();
    Assertions.assertThat(comentarios.size()).as("size").isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      Comentario comentario = comentarios.get(i);
      Assertions.assertThat(comentario.getId()).as((8 - i) + ".id").isEqualTo(8 - i);
      Assertions.assertThat(comentario.getTexto()).as((8 - i) + ".texto")
          .isEqualTo("Comentario" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsComentarioSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=3
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por texto desc
    String sort = "texto-";
    // when: Filtra por texto like
    String filter = "texto~%00%";

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.COMENTARIO_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Comentario>> response = restTemplate.exchange(uri, HttpMethod.GET,
        new HttpEntity<>(headers), new ParameterizedTypeReference<List<Comentario>>() {
        });

    // then: Respuesta OK, Comentarios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Comentario> comentarios = response.getBody();
    Assertions.assertThat(comentarios.size()).as("size").isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("x-page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("x-page-size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("x-total-count").isEqualTo("3");

    // Contiene texto='Comentario003', 'Comentario002', 'Comentario001'
    Assertions.assertThat(comentarios.get(0).getTexto()).as("0.texto")
        .isEqualTo("Comentario" + String.format("%03d", 3));
    Assertions.assertThat(comentarios.get(1).getTexto()).as("1.texto")
        .isEqualTo("Comentario" + String.format("%03d", 2));
    Assertions.assertThat(comentarios.get(2).getTexto()).as("2.texto")
        .isEqualTo("Comentario" + String.format("%03d", 1));
  }

  /**
   * Función que devuelve un objeto Comentario
   * 
   * @param id    id de la comentario
   * @param texto texto del comentario
   * @return el objeto Comentario
   */
  public Comentario generarMockComentario(Long id, String texto) {
    ApartadoFormulario apartadoFormulario = new ApartadoFormulario();
    apartadoFormulario.setId(100L);

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(200L);

    TipoComentario tipoComentario = new TipoComentario();
    tipoComentario.setId(300L);

    Comentario comentario = new Comentario();
    comentario.setId(id);
    comentario.setApartadoFormulario(apartadoFormulario);
    comentario.setEvaluacion(evaluacion);
    comentario.setTipoComentario(tipoComentario);
    comentario.setTexto(texto);

    return comentario;
  }

}