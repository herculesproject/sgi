package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Tarea;
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
 * Test de integracion de Tarea.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TareaIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getTarea_WithId_ReturnsTarea() throws Exception {
    final ResponseEntity<Tarea> response = restTemplate
        .getForEntity(ConstantesEti.TAREA_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, Tarea.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Tarea tarea = response.getBody();

    Assertions.assertThat(tarea.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(tarea.getEquipoTrabajo()).as("equipoTrabajo").isNotNull();
    Assertions.assertThat(tarea.getEquipoTrabajo().getId()).as("equipoTrabajo.id").isEqualTo(100L);
    Assertions.assertThat(tarea.getMemoria()).as("memoria").isNotNull();
    Assertions.assertThat(tarea.getMemoria().getId()).as("memoria.id").isEqualTo(200L);
    Assertions.assertThat(tarea.getTarea()).as("tarea").isEqualTo("Tarea1");
    Assertions.assertThat(tarea.getFormacion()).as("formacion").isEqualTo("Formacion1");
    Assertions.assertThat(tarea.getFormacionEspecifica()).as("formacionEspecifica").isNotNull();
    Assertions.assertThat(tarea.getFormacionEspecifica().getId()).as("formacionEspecifica.id").isEqualTo(300L);
    Assertions.assertThat(tarea.getOrganismo()).as("organismo").isEqualTo("Organismo1");
    Assertions.assertThat(tarea.getAnio()).as("anio").isEqualTo(2020);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addTarea_ReturnsTarea() throws Exception {

    Tarea nuevaTarea = generarMockTarea(null, "Tarea");

    final ResponseEntity<Tarea> response = restTemplate.postForEntity(ConstantesEti.TAREA_CONTROLLER_BASE_PATH,
        nuevaTarea, Tarea.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    final Tarea tarea = response.getBody();

    Assertions.assertThat(tarea.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(tarea.getEquipoTrabajo()).as("equipoTrabajo").isNotNull();
    Assertions.assertThat(tarea.getEquipoTrabajo().getId()).as("equipoTrabajo.id").isEqualTo(100L);
    Assertions.assertThat(tarea.getMemoria()).as("memoria").isNotNull();
    Assertions.assertThat(tarea.getMemoria().getId()).as("memoria.id").isEqualTo(200L);
    Assertions.assertThat(tarea.getTarea()).as("tarea").isEqualTo("Tarea");
    Assertions.assertThat(tarea.getFormacion()).as("formacion").isEqualTo("Formacion");
    Assertions.assertThat(tarea.getFormacionEspecifica()).as("formacionEspecifica").isNotNull();
    Assertions.assertThat(tarea.getFormacionEspecifica().getId()).as("formacionEspecifica.id").isEqualTo(300L);
    Assertions.assertThat(tarea.getOrganismo()).as("organismo").isEqualTo("Organismo");
    Assertions.assertThat(tarea.getAnio()).as("anio").isEqualTo(2020);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTarea_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<Tarea> response = restTemplate.exchange(
        ConstantesEti.TAREA_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, HttpMethod.DELETE, null,
        Tarea.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTarea_DoNotGetTarea() throws Exception {
    restTemplate.delete(ConstantesEti.TAREA_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, 1L);

    final ResponseEntity<Tarea> response = restTemplate
        .getForEntity(ConstantesEti.TAREA_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, Tarea.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceTarea_ReturnsTarea() throws Exception {

    Tarea replaceTarea = generarMockTarea(1L, "Tarea1");

    final HttpEntity<Tarea> requestEntity = new HttpEntity<Tarea>(replaceTarea, new HttpHeaders());

    final ResponseEntity<Tarea> response = restTemplate.exchange(
        ConstantesEti.TAREA_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, HttpMethod.PUT, requestEntity,
        Tarea.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Tarea tarea = response.getBody();

    Assertions.assertThat(tarea.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(tarea.getEquipoTrabajo()).as("equipoTrabajo").isNotNull();
    Assertions.assertThat(tarea.getEquipoTrabajo().getId()).as("equipoTrabajo.id").isEqualTo(100L);
    Assertions.assertThat(tarea.getMemoria()).as("memoria").isNotNull();
    Assertions.assertThat(tarea.getMemoria().getId()).as("memoria.id").isEqualTo(200L);
    Assertions.assertThat(tarea.getTarea()).as("tarea").isEqualTo("Tarea1");
    Assertions.assertThat(tarea.getFormacion()).as("formacion").isEqualTo("Formacion1");
    Assertions.assertThat(tarea.getFormacionEspecifica()).as("formacionEspecifica").isNotNull();
    Assertions.assertThat(tarea.getFormacionEspecifica().getId()).as("formacionEspecifica.id").isEqualTo(300L);
    Assertions.assertThat(tarea.getOrganismo()).as("organismo").isEqualTo("Organismo1");
    Assertions.assertThat(tarea.getAnio()).as("anio").isEqualTo(2020);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsTareaSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.TAREA_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<Tarea>> response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers),
        new ParameterizedTypeReference<List<Tarea>>() {
        });

    // then: Respuesta OK, tareas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Tarea> tareas = response.getBody();
    Assertions.assertThat(tareas.size()).as("size").isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).as("x-page").isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).as("x-page-size").isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).as("x-total-count").isEqualTo("8");

    // Contiene de tarea='Tarea6' a 'Tarea8'
    Assertions.assertThat(tareas.get(0).getTarea()).as("0.tarea").isEqualTo("Tarea6");
    Assertions.assertThat(tareas.get(1).getTarea()).as("1.tarea").isEqualTo("Tarea7");
    Assertions.assertThat(tareas.get(2).getTarea()).as("2.tarea").isEqualTo("Tarea8");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTareaList() throws Exception {
    // when: Búsqueda por tarea like e id equals
    Long id = 5L;
    String query = "tarea~Tarea%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.TAREA_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Tarea>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<Tarea>>() {
        });

    // then: Respuesta OK, Tareas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Tarea> tareas = response.getBody();
    Assertions.assertThat(tareas.size()).as("size").isEqualTo(1);
    Assertions.assertThat(tareas.get(0).getId()).as("id").isEqualTo(id);
    Assertions.assertThat(tareas.get(0).getTarea()).as("tarea").startsWith("Tarea5");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTareaList() throws Exception {
    // when: Ordenación por tarea desc
    String sort = "tarea-";

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.TAREA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Tarea>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<Tarea>>() {
        });

    // then: Respuesta OK, Tareas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Tarea> tareas = response.getBody();
    Assertions.assertThat(tareas.size()).as("size").isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      Tarea tarea = tareas.get(i);
      Assertions.assertThat(tarea.getId()).as((8 - i) + ".id").isEqualTo(8 - i);
      Assertions.assertThat(tarea.getTarea()).as((8 - i) + ".tarea").isEqualTo("Tarea" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTareaSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=3
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por tarea desc
    String sort = "tarea-";
    // when: Filtra por tarea like
    String filter = "tarea~%00%";

    URI uri = UriComponentsBuilder.fromUriString(ConstantesEti.TAREA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Tarea>> response = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers),
        new ParameterizedTypeReference<List<Tarea>>() {
        });

    // then: Respuesta OK, Tareas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Tarea> tareas = response.getBody();
    Assertions.assertThat(tareas.size()).as("size").isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("x-page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("x-page-size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("x-total-count").isEqualTo("3");

    // Contiene tarea='Tarea003', 'Tarea002', 'Tarea001'
    Assertions.assertThat(tareas.get(0).getTarea()).as("0.tarea").isEqualTo("Tarea" + String.format("%03d", 3));
    Assertions.assertThat(tareas.get(1).getTarea()).as("1.tarea").isEqualTo("Tarea" + String.format("%03d", 2));
    Assertions.assertThat(tareas.get(2).getTarea()).as("2.tarea").isEqualTo("Tarea" + String.format("%03d", 1));
  }

  /**
   * Función que devuelve un objeto Tarea
   * 
   * @param id          id de la tarea
   * @param descripcion descripcion de la tarea
   * @return el objeto Tarea
   */
  public Tarea generarMockTarea(Long id, String descripcion) {
    EquipoTrabajo equipoTrabajo = new EquipoTrabajo();
    equipoTrabajo.setId(100L);

    Memoria memoria = new Memoria();
    memoria.setId(200L);

    FormacionEspecifica formacionEspecifica = new FormacionEspecifica();
    formacionEspecifica.setId(300L);

    Tarea tarea = new Tarea();
    tarea.setId(id);
    tarea.setEquipoTrabajo(equipoTrabajo);
    tarea.setMemoria(memoria);
    tarea.setTarea(descripcion);
    tarea.setFormacion("Formacion" + (id != null ? id : ""));
    tarea.setFormacionEspecifica(formacionEspecifica);
    tarea.setOrganismo("Organismo" + (id != null ? id : ""));
    tarea.setAnio(2020);

    return tarea;
  }

}