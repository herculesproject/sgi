package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.EquipoTrabajoWithIsEliminable;
import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.dto.EstadoFinanciacionDto;
import org.crue.hercules.sgi.eti.dto.PeticionEvaluacionEstadoFinanciacionInput;
import org.crue.hercules.sgi.eti.dto.PeticionEvaluacionSolicitudCspInput;
import org.crue.hercules.sgi.eti.dto.PeticionEvaluacionUnidadVinculacionInput;
import org.crue.hercules.sgi.eti.dto.PeticionEvaluacionUnidadVinculacionOutput;
import org.crue.hercules.sgi.eti.dto.TareaWithIsEliminable;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionDisMetodologico;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionObjetivos;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionResumen;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionTitulo;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionUnidadVinculacion;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.model.TareaFormacion;
import org.crue.hercules.sgi.eti.model.TareaNombre;
import org.crue.hercules.sgi.eti.model.TareaOrganismo;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoTarea;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.jdbc.SqlMergeMode.MergeMode;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de PeticionEvaluacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
// @formatter:off
  "classpath:scripts/tipo_actividad.sql", 
  "classpath:scripts/formulario.sql",
  "classpath:scripts/comite.sql",
  "classpath:scripts/tipo_estado_memoria.sql",
  "classpath:scripts/estado_retrospectiva.sql", 
  "classpath:scripts/formacion_especifica.sql",
  "classpath:scripts/tipo_tarea.sql",
  "classpath:scripts/peticion_evaluacion.sql",
  "classpath:scripts/equipo_trabajo.sql",
  "classpath:scripts/retrospectiva.sql",
  "classpath:scripts/memoria.sql", 
  "classpath:scripts/tarea.sql"
// @formatter:on
})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
@SqlMergeMode(MergeMode.MERGE)
class PeticionEvaluacionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PETICION_EVALUACION_CONTROLLER_BASE_PATH = "/peticionevaluaciones";
  private static final String PATH_UNIDADES_VINCULACION = "/{id}/unidades-vinculacion";
  private static final String PATH_SOLICITUD_CSP = "/solicitud-csp";
  private static final String PATH_ESTADO_FINANCIACION = "/{id}/estado-financiacion";

  private HttpEntity<PeticionEvaluacion> buildRequest(HttpHeaders headers, PeticionEvaluacion entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", "bearer " + tokenBuilder.buildToken("user", "ETI-PEV-INV-ER",
        "ETI-PEV-V", "ETI-PEV-CR", "ETI-MEM-CR", "ETI-PEV-INV-BR", "ETI-PEV-INV-C", "ETI-PEV-V"));

    return new HttpEntity<>(entity, headers);
  }

  private HttpEntity<EquipoTrabajo> buildRequestEquipoTrabajo(HttpHeaders headers, EquipoTrabajo entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        "bearer " + tokenBuilder.buildToken("user", "ETI-PEV-INV-C", "ETI-PEV-INV-ER", "ETI-PEV-INV-VR"));

    return new HttpEntity<>(entity, headers);
  }

  private HttpEntity<Tarea> buildRequestTarea(HttpHeaders headers, Tarea entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        "bearer " + tokenBuilder.buildToken("user", "ETI-PEV-INV-C", "ETI-PEV-INV-ER"));

    return new HttpEntity<>(entity, headers);
  }

  private HttpEntity<EquipoTrabajo> buildRequestMemoriaPeticionEvaluacion(HttpHeaders headers, EquipoTrabajo entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        "bearer " + tokenBuilder.buildToken("user", "ETI-PEV-CR", "ETI-PEV-INV-ER", "ETI-EVR-V", "ETI-PEV-INV-C"));

    return new HttpEntity<>(entity, headers);
  }

  @Test
  void getPeticionEvaluacion_WithId_ReturnsPeticionEvaluacion() throws Exception {
    final ResponseEntity<PeticionEvaluacion> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        PeticionEvaluacion.class, 2L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final PeticionEvaluacion tipoActividad = response.getBody();

    Assertions.assertThat(tipoActividad.getId()).isEqualTo(2L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoActividad.getTitulo(), Language.ES))
        .isEqualTo("PeticionEvaluacion2");
  }

  @Test
  void addPeticionEvaluacion_ReturnsPeticionEvaluacion() throws Exception {

    PeticionEvaluacion nuevoPeticionEvaluacion = generarMockPeticionEvaluacion(null, "titulo");

    final ResponseEntity<PeticionEvaluacion> response = restTemplate.exchange(PETICION_EVALUACION_CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, nuevoPeticionEvaluacion), PeticionEvaluacion.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody().getId()).isNotNull();
  }

  @Test
  void removePeticionEvaluacion_Success() throws Exception {

    // when: Delete con id existente
    long id = 2L;
    final ResponseEntity<PeticionEvaluacion> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        PeticionEvaluacion.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Test
  void removePeticionEvaluacion_DoNotGetPeticionEvaluacion() throws Exception {
    restTemplate.exchange(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE,
        buildRequest(null, null), PeticionEvaluacion.class, 1L);

    final ResponseEntity<PeticionEvaluacion> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        PeticionEvaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Test
  void replacePeticionEvaluacion_ReturnsPeticionEvaluacion() throws Exception {

    PeticionEvaluacion replacePeticionEvaluacion = generarMockPeticionEvaluacion(2L, "PeticionEvaluacion2");

    final ResponseEntity<PeticionEvaluacion> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, replacePeticionEvaluacion), PeticionEvaluacion.class, 2L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final PeticionEvaluacion tipoActividad = response.getBody();

    Assertions.assertThat(tipoActividad.getId()).isNotNull();
    Assertions.assertThat(tipoActividad.getTitulo()).isEqualTo(replacePeticionEvaluacion.getTitulo());
    Assertions.assertThat(tipoActividad.getActivo()).isEqualTo(replacePeticionEvaluacion.getActivo());
  }

  @Test
  void findAll_WithPaging_ReturnsPeticionEvaluacionSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    String query = "id,asc";

    URI uri = UriComponentsBuilder.fromUriString(PETICION_EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    final ResponseEntity<List<PeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<PeticionEvaluacion>>() {
        });

    // then: Respuesta OK, PeticionEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PeticionEvaluacion> peticionEvaluaciones = response.getBody();
    Assertions.assertThat(peticionEvaluaciones).hasSize(2);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("7");

    // Contiene de titulo='PeticionEvaluacion6' a 'PeticionEvaluacion8'
    List<String> titulos = new ArrayList<>();
    for (int i = 0; i < 2; i++) {
      titulos.add(I18nHelper.getValueForLanguage(peticionEvaluaciones.get(i).getTitulo(), Language.ES));
    }
    Assertions.assertThat(titulos).contains("PeticionEvaluacion7", "PeticionEvaluacion8");
  }

  @Test
  void findAll_WithSearchQuery_ReturnsFilteredPeticionEvaluacionList() throws Exception {
    // when: Búsqueda por titulo like e id equals
    Long id = 2L;
    String query = "peticionEvaluacion.titulo.value=ke=PeticionEvaluacion2";

    URI uri = UriComponentsBuilder.fromUriString(PETICION_EVALUACION_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<PeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<PeticionEvaluacion>>() {
        });

    // then: Respuesta OK, PeticionEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PeticionEvaluacion> peticionEvaluaciones = response.getBody();
    Assertions.assertThat(peticionEvaluaciones).hasSize(1);
    Assertions.assertThat(peticionEvaluaciones.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(I18nHelper.getValueForLanguage(peticionEvaluaciones.get(0).getTitulo(), Language.ES))
        .startsWith(
            "PeticionEvaluacion");
  }

  @Test
  void findAll_WithSortQuery_ReturnsOrderedPeticionEvaluacionList() throws Exception {
    // when: Ordenación por titulo desc
    String query = "titulo.value,desc";

    URI uri = UriComponentsBuilder.fromUriString(PETICION_EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<PeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<PeticionEvaluacion>>() {
        });

    // then: Respuesta OK, PeticionEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PeticionEvaluacion> peticionEvaluaciones = response.getBody();
    Assertions.assertThat(peticionEvaluaciones).hasSize(7);
    for (int i = 0; i < 7; i++) {
      PeticionEvaluacion peticionEvaluacion = peticionEvaluaciones.get(i);
      Assertions.assertThat(peticionEvaluacion.getId()).isEqualTo(8 - i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(peticionEvaluacion.getTitulo(), Language.ES))
          .isEqualTo("PeticionEvaluacion" + String.format("%d", 8 - i));
    }
  }

  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsPeticionEvaluacionSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por titulo desc
    String sort = "titulo.value,desc";
    // when: Filtra por titulo like e id equals
    String filter = "peticionEvaluacion.titulo.value=ke=PeticionEvaluacion2";

    URI uri = UriComponentsBuilder.fromUriString(PETICION_EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<PeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<PeticionEvaluacion>>() {
        });

    // then: Respuesta OK, PeticionEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PeticionEvaluacion> peticionEvaluaciones = response.getBody();
    Assertions.assertThat(peticionEvaluaciones).hasSize(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("1");

    // Contiene titulo='PeticionEvaluacion1', 'PeticionEvaluacion2',
    // 'PeticionEvaluacion3'
    Assertions.assertThat(I18nHelper.getValueForLanguage(peticionEvaluaciones.get(0).getTitulo(), Language.ES))
        .isEqualTo("PeticionEvaluacion" + String.format("%d", 2));
  }

  @Test
  void findEquipoInvestigador_WithPaging_ReturnsEquipoInvestigadorSubList() throws Exception {

    final ResponseEntity<List<EquipoTrabajoWithIsEliminable>> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/equipo-investigador", HttpMethod.GET,
        buildRequestEquipoTrabajo(null, null), new ParameterizedTypeReference<List<EquipoTrabajoWithIsEliminable>>() {
        }, 2L);

    // then: Respuesta OK, PeticionEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EquipoTrabajoWithIsEliminable> equiposTrabajo = response.getBody();
    Assertions.assertThat(equiposTrabajo).hasSize(1);
  }

  @Test
  void findTareas_WithPaging_ReturnsTareasSubList() throws Exception {

    final ResponseEntity<List<TareaWithIsEliminable>> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/tareas", HttpMethod.GET,
        buildRequestEquipoTrabajo(null, null), new ParameterizedTypeReference<List<TareaWithIsEliminable>>() {
        }, 2L);

    // then: Respuesta OK, PeticionEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TareaWithIsEliminable> tareas = response.getBody();
    Assertions.assertThat(tareas).hasSize(7);
  }

  @Test
  void findMemorias_WithPaging_ReturnsMemoriaPeticionEvaluacionSubList() throws Exception {

    final ResponseEntity<List<MemoriaPeticionEvaluacion>> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/memorias", HttpMethod.GET,
        buildRequestMemoriaPeticionEvaluacion(null, null),
        new ParameterizedTypeReference<List<MemoriaPeticionEvaluacion>>() {
        }, 2L);

    // then: Respuesta OK, PeticionEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<MemoriaPeticionEvaluacion> memoriasPeticionEvaluacion = response.getBody();
    Assertions.assertThat(memoriasPeticionEvaluacion).hasSize(16);

    // Contiene de id='7' a '8'
    List<Long> ids = new ArrayList<>();
    for (MemoriaPeticionEvaluacion memoria : memoriasPeticionEvaluacion) {
      ids.add(memoria.getId());
    }
    Assertions.assertThat(ids).contains(7L, 8L);
  }

  @Test
  void addTarea_ReturnsTarea() throws Exception {

    Tarea nuevaTarea = generarMockTarea(null, "Tarea");

    final ResponseEntity<Tarea> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + "/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}/tareas",
        HttpMethod.POST, buildRequestTarea(null, nuevaTarea), Tarea.class, 2L, 2L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    final Tarea tarea = response.getBody();

    Assertions.assertThat(tarea.getId()).as("id").isEqualTo(9L);
    Assertions.assertThat(tarea.getEquipoTrabajo()).as("equipoTrabajo").isNotNull();
    Assertions.assertThat(tarea.getEquipoTrabajo().getId()).as("equipoTrabajo.id").isEqualTo(2L);
    Assertions.assertThat(tarea.getMemoria()).as("memoria").isNotNull();
    Assertions.assertThat(tarea.getMemoria().getId()).as("memoria.id").isEqualTo(2L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(tarea.getNombre(), Language.ES)).as("nombre")
        .isEqualTo("Tarea");
    Assertions.assertThat(I18nHelper.getValueForLanguage(tarea.getFormacion(), Language.ES)).as("formacion")
        .isEqualTo("Formacion");
    Assertions.assertThat(tarea.getFormacionEspecifica()).as("formacionEspecifica").isNotNull();
    Assertions.assertThat(tarea.getFormacionEspecifica().getId()).as("formacionEspecifica.id").isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(tarea.getOrganismo(), Language.ES)).as("organismo")
        .isEqualTo("Organismo");
    Assertions.assertThat(tarea.getAnio()).as("anio").isEqualTo(2020);
  }

  @Test
  void addEquipoTrabajo_ReturnsEquipoTrabajo() throws Exception {

    EquipoTrabajo nuevoEquipoTrabajo = generarMockEquipoTrabajo(null,
        generarMockPeticionEvaluacion(2L, "PeticionEvaluacion2"));

    final ResponseEntity<EquipoTrabajo> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + "/{idPeticionEvaluacion}/equipos-trabajo", HttpMethod.POST,
        buildRequestEquipoTrabajo(null, nuevoEquipoTrabajo), EquipoTrabajo.class, 2L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    final EquipoTrabajo equipoTrabajo = response.getBody();

    Assertions.assertThat(equipoTrabajo.getId()).isNotNull();
    Assertions.assertThat(equipoTrabajo.getPeticionEvaluacionId()).isEqualTo(2);
    Assertions.assertThat(equipoTrabajo.getPersonaRef()).isEqualTo("user-001");
  }

  @Test
  void removeEquipoTrabajo_Success() throws Exception {

    // when: Delete con id existente
    long idPeticionEvaluacion = 2L;
    long idEquipoTrabajo = 2L;
    final ResponseEntity<EquipoTrabajo> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + "/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}",
        HttpMethod.DELETE, buildRequest(null, null), EquipoTrabajo.class, idPeticionEvaluacion, idEquipoTrabajo);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Test
  void removeEquipoTrabajo_DoNotGetEquipoTrabajo() throws Exception {

    // when: Delete con id no existente
    long idPeticionEvaluacion = 1L;
    long idEquipoTrabajo = 111L;

    final ResponseEntity<EquipoTrabajo> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + "/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}",
        HttpMethod.DELETE, buildRequest(null, null), EquipoTrabajo.class, idPeticionEvaluacion, idEquipoTrabajo);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void removeTarea_Success() throws Exception {

    // when: Delete con id existente
    long idPeticionEvaluacion = 2L;
    long idEquipoTrabajo = 2L;
    long idTarea = 2L;
    final ResponseEntity<Tarea> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH
            + "/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}/tareas/{idTarea}",
        HttpMethod.DELETE, buildRequest(null, null), Tarea.class, idPeticionEvaluacion, idEquipoTrabajo, idTarea);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Test
  void removeTarea_DoNotGetTarea() throws Exception {

    // when: Delete con id no existente
    long idPeticionEvaluacion = 1L;
    long idTarea = 111L;
    final ResponseEntity<Tarea> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + "/{idPeticionEvaluacion}/tareas/{idTarea}", HttpMethod.DELETE,
        buildRequest(null, null), Tarea.class, idPeticionEvaluacion, idTarea);

    // then: 404
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void findAllPeticionEvaluacionMemoria_WithPagingSortingAndFiltering_ReturnsPeticionEvaluacionSubList()
      throws Exception {
    // when: Obtiene page=1 con pagesize=3
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        "bearer " + tokenBuilder.buildToken("user", "ETI-PEV-INV-VR", "ETI-PEV-V", "ETI-MEM-INV-CR"));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por id desc
    String sort = "id,desc";
    // when: Filtra por numReferencia like
    String filter = "peticionEvaluacion.titulo.value=ke=PeticionEvaluacion2";

    URI uri = UriComponentsBuilder.fromUriString(PETICION_EVALUACION_CONTROLLER_BASE_PATH + "/memorias")
        .queryParam("s", sort).queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<PeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<PeticionEvaluacion>>() {
        });

    // then: Respuesta OK, peticiones de evaluación retorna la información de la
    // página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PeticionEvaluacion> peticionesEvaluacion = response.getBody();
    Assertions.assertThat(peticionesEvaluacion).hasSize(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("1");

    // Contiene titulo='PeticionEvaluacion1'
    Assertions.assertThat(I18nHelper.getValueForLanguage(peticionesEvaluacion.get(0).getTitulo(), Language.ES))
        .isEqualTo("PeticionEvaluacion2");
  }

  @Test
  void findAllPeticionEvaluacionMemoria_WithPagingSortingAndWithoutFiltering_ReturnsPeticionEvaluacionSubList()
      throws Exception {
    // given: 7 peticiones de evaluación activas creadas por el usuario
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization",
        "bearer " + tokenBuilder.buildToken("user", "ETI-PEV-INV-VR", "ETI-PEV-V", "ETI-MEM-INV-CR"));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";

    // when: se obtiene la primera página sin query de filtrado
    URI uri = UriComponentsBuilder.fromUriString(PETICION_EVALUACION_CONTROLLER_BASE_PATH + "/memorias")
        .queryParam("s", sort).build(false).toUri();

    final ResponseEntity<List<PeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<PeticionEvaluacion>>() {
        });

    // then: se devuelven las 3 peticiones de evaluación de mayor id de un total
    // de 7
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<PeticionEvaluacion> peticionesEvaluacion = response.getBody();
    Assertions.assertThat(peticionesEvaluacion).extracting(PeticionEvaluacion::getId).containsExactly(8L, 7L, 6L);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("7");
  }

  /**
   * Función que devuelve un objeto PeticionEvaluacion
   * 
   * @param id     id del PeticionEvaluacion
   * @param titulo el título de PeticionEvaluacion
   * @return el objeto PeticionEvaluacion
   */

  private PeticionEvaluacion generarMockPeticionEvaluacion(Long id, String titulo) {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    Set<PeticionEvaluacionTitulo> tit = new HashSet<>();
    tit.add(new PeticionEvaluacionTitulo(Language.ES, titulo));
    Set<PeticionEvaluacionResumen> resumen = new HashSet<>();
    resumen.add(new PeticionEvaluacionResumen(Language.ES, "Resumen" + id));
    Set<PeticionEvaluacionObjetivos> objetivos = new HashSet<>();
    objetivos.add(new PeticionEvaluacionObjetivos(Language.ES, "Objetivos" + id));
    Set<PeticionEvaluacionDisMetodologico> disMetodologico = new HashSet<>();
    disMetodologico.add(new PeticionEvaluacionDisMetodologico(Language.ES, "DiseñoMetodologico" + id));
    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo" + id);
    peticionEvaluacion.setDisMetodologico(disMetodologico);
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos(objetivos);
    peticionEvaluacion.setResumen(resumen);
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria" + id);
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo(tit);
    peticionEvaluacion.setPersonaRef("user-00" + id);
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    return peticionEvaluacion;
  }

  /**
   * Función que devuelve un objeto EquipoTrabajo
   * 
   * @param id                 id del EquipoTrabajo
   * @param peticionEvaluacion la PeticionEvaluacion del EquipoTrabajo
   * @return el objeto EquipoTrabajo
   */
  private EquipoTrabajo generarMockEquipoTrabajo(Long id, PeticionEvaluacion peticionEvaluacion) {

    EquipoTrabajo equipoTrabajo = new EquipoTrabajo();
    equipoTrabajo.setId(id);
    equipoTrabajo.setPeticionEvaluacionId(peticionEvaluacion.getId());
    equipoTrabajo.setPersonaRef("user-00" + (id == null ? 1 : id));

    return equipoTrabajo;
  }

  /**
   * Función que devuelve un objeto Tarea
   * 
   * @param id          id de la tarea
   * @param descripcion descripcion de la tarea
   * @return el objeto Tarea
   */
  private Tarea generarMockTarea(Long id, String descripcion) {
    EquipoTrabajo equipoTrabajo = new EquipoTrabajo();
    equipoTrabajo.setId(2L);

    Memoria memoria = new Memoria();
    memoria.setId(2L);

    FormacionEspecifica formacionEspecifica = new FormacionEspecifica();
    formacionEspecifica.setId(1L);

    TipoTarea tipoTarea = new TipoTarea();
    tipoTarea.setId(1L);

    Set<TareaNombre> nombre = new HashSet<>();
    nombre.add(new TareaNombre(Language.ES, descripcion));
    Set<TareaFormacion> formacion = new HashSet<>();
    formacion.add(new TareaFormacion(Language.ES, "Formacion" + (id != null ? id : "")));
    Set<TareaOrganismo> organismo = new HashSet<>();
    organismo.add(new TareaOrganismo(Language.ES, "Organismo" + (id != null ? id : "")));

    Tarea tarea = new Tarea();
    tarea.setId(id);
    tarea.setEquipoTrabajo(equipoTrabajo);
    tarea.setMemoria(memoria);
    tarea.setNombre(nombre);
    tarea.setFormacion(formacion);
    tarea.setFormacionEspecifica(formacionEspecifica);
    tarea.setOrganismo(organismo);
    tarea.setAnio(2020);
    tarea.setTipoTarea(tipoTarea);

    return tarea;
  }

  private <T> HttpEntity<T> buildRequestWithRoles(T entity, String... roles) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", "bearer " + tokenBuilder.buildToken("user", roles));
    return new HttpEntity<>(entity, headers);
  }

  @Test
  @Sql("classpath:scripts/peticion_evaluacion_unidad_vinculacion.sql")
  void findUnidadesVinculacion_ReturnsPeticionEvaluacionUnidadVinculacionOutputList() throws Exception {
    // given: una petición con dos unidades de vinculación
    Long peticionEvaluacionId = 2L;
    URI uri = UriComponentsBuilder.fromUriString(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_UNIDADES_VINCULACION)
        .queryParam("s", "id,desc").buildAndExpand(peticionEvaluacionId).toUri();

    // when: se obtienen sus unidades con autoridad de visualización
    final ResponseEntity<List<PeticionEvaluacionUnidadVinculacionOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET, buildRequestWithRoles(null, "ETI-PEV-V"),
        new ParameterizedTypeReference<List<PeticionEvaluacionUnidadVinculacionOutput>>() {
        });

    // then: se devuelven las dos unidades ordenadas por id descendente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).extracting(PeticionEvaluacionUnidadVinculacionOutput::getId)
        .containsExactly(2L, 1L);
    Assertions.assertThat(response.getBody())
        .extracting(PeticionEvaluacionUnidadVinculacionOutput::getUnidadVinculacionRef)
        .containsExactly("UV-2", "UV-1");
    Assertions.assertThat(response.getBody())
        .extracting(PeticionEvaluacionUnidadVinculacionOutput::getPeticionEvaluacionId)
        .containsOnly(peticionEvaluacionId);
  }

  @Test
  @Sql("classpath:scripts/peticion_evaluacion_unidad_vinculacion.sql")
  void findUnidadesVinculacion_WithMemoriaAuthority_ReturnsFilteredList() throws Exception {
    // given: una petición con dos unidades y un filtro por referencia
    Long peticionEvaluacionId = 3L;
    URI uri = UriComponentsBuilder.fromUriString(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_UNIDADES_VINCULACION)
        .queryParam("s", "id,desc").queryParam("q", "unidadVinculacionRef==UV-3").buildAndExpand(peticionEvaluacionId)
        .toUri();

    // when: se obtienen sus unidades con autoridad de memorias
    final ResponseEntity<List<PeticionEvaluacionUnidadVinculacionOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET, buildRequestWithRoles(null, "ETI-MEM-V"),
        new ParameterizedTypeReference<List<PeticionEvaluacionUnidadVinculacionOutput>>() {
        });

    // then: se devuelve solo la unidad filtrada
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).extracting(PeticionEvaluacionUnidadVinculacionOutput::getId)
        .containsExactly(4L);
  }

  @Test
  void findUnidadesVinculacion_WithoutUnidades_ReturnsOkEmptyList() throws Exception {
    // given: una petición sin unidades de vinculación
    URI uri = UriComponentsBuilder.fromUriString(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_UNIDADES_VINCULACION)
        .queryParam("s", "id,desc").buildAndExpand(4L).toUri();

    // when: se obtienen sus unidades
    final ResponseEntity<List<PeticionEvaluacionUnidadVinculacionOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET, buildRequestWithRoles(null, "ETI-PEV-E"),
        new ParameterizedTypeReference<List<PeticionEvaluacionUnidadVinculacionOutput>>() {
        });

    // then: se devuelve 200 con la lista vacía
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEmpty();
  }

  @Test
  void findUnidadesVinculacion_WithNoExistingPeticion_Returns404() throws Exception {
    // given: una petición que no existe
    URI uri = UriComponentsBuilder.fromUriString(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_UNIDADES_VINCULACION)
        .queryParam("s", "id,desc").buildAndExpand(999L).toUri();

    // when: se obtienen sus unidades
    final ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequestWithRoles(null, "ETI-PEV-V"), String.class);

    // then: se devuelve 404
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @Sql("classpath:scripts/peticion_evaluacion_unidad_vinculacion.sql")
  void updateUnidadesVinculacion_WithNewRefs_ReplacesAll() throws Exception {
    // given: una petición con UV-1 y UV-2 y una lista nueva con UV-4 repetida y
    // UV-5
    Long peticionEvaluacionId = 2L;
    List<PeticionEvaluacionUnidadVinculacionInput> input = Arrays.asList(
        PeticionEvaluacionUnidadVinculacionInput.builder().unidadVinculacionRef("UV-4").build(),
        PeticionEvaluacionUnidadVinculacionInput.builder().unidadVinculacionRef("UV-4").build(),
        PeticionEvaluacionUnidadVinculacionInput.builder().unidadVinculacionRef("UV-5").build());

    // when: se actualizan las unidades
    final ResponseEntity<List<PeticionEvaluacionUnidadVinculacionOutput>> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_UNIDADES_VINCULACION, HttpMethod.PATCH,
        buildRequestWithRoles(input, "ETI-PEV-E"),
        new ParameterizedTypeReference<List<PeticionEvaluacionUnidadVinculacionOutput>>() {
        }, peticionEvaluacionId);

    // then: se sustituyen por las nuevas sin duplicados
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody())
        .extracting(PeticionEvaluacionUnidadVinculacionOutput::getUnidadVinculacionRef)
        .containsExactly("UV-4", "UV-5");
    Assertions.assertThat(response.getBody()).extracting(PeticionEvaluacionUnidadVinculacionOutput::getId)
        .doesNotContain(1L, 2L);
  }

  @Test
  @Sql("classpath:scripts/peticion_evaluacion_unidad_vinculacion.sql")
  void updateUnidadesVinculacion_WithSameRefs_PreservesIds() throws Exception {
    // given: una petición con UV-1 y UV-2 y una lista con las mismas referencias
    Long peticionEvaluacionId = 2L;
    List<PeticionEvaluacionUnidadVinculacionInput> input = Arrays.asList(
        PeticionEvaluacionUnidadVinculacionInput.builder().unidadVinculacionRef("UV-1").build(),
        PeticionEvaluacionUnidadVinculacionInput.builder().unidadVinculacionRef("UV-2").build());

    // when: se actualizan las unidades
    final ResponseEntity<List<PeticionEvaluacionUnidadVinculacionOutput>> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_UNIDADES_VINCULACION, HttpMethod.PATCH,
        buildRequestWithRoles(input, "ETI-PEV-E"),
        new ParameterizedTypeReference<List<PeticionEvaluacionUnidadVinculacionOutput>>() {
        }, peticionEvaluacionId);

    // then: se conservan los ids existentes
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).extracting(PeticionEvaluacionUnidadVinculacionOutput::getId)
        .containsExactlyInAnyOrder(1L, 2L);
  }

  @Test
  void updateUnidadesVinculacion_WithoutEditAuthority_Returns403() throws Exception {
    // given: un usuario solo con autoridad de visualización
    List<PeticionEvaluacionUnidadVinculacionInput> input = Arrays.asList(
        PeticionEvaluacionUnidadVinculacionInput.builder().unidadVinculacionRef("UV-1").build());

    // when: se intentan actualizar las unidades
    final ResponseEntity<String> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_UNIDADES_VINCULACION, HttpMethod.PATCH,
        buildRequestWithRoles(input, "ETI-PEV-V"), String.class, 2L);

    // then: se deniega el acceso
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void updateUnidadesVinculacion_WithRefTooLong_Returns400() throws Exception {
    // given: una referencia que supera la longitud máxima
    List<PeticionEvaluacionUnidadVinculacionInput> input = Arrays.asList(
        PeticionEvaluacionUnidadVinculacionInput.builder()
            .unidadVinculacionRef(
                StringUtils.repeat("X", PeticionEvaluacionUnidadVinculacion.UNIDAD_VINCULACION_REF_LENGTH + 1))
            .build());

    // when: se intentan actualizar las unidades
    final ResponseEntity<String> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_UNIDADES_VINCULACION, HttpMethod.PATCH,
        buildRequestWithRoles(input, "ETI-PEV-E"), String.class, 2L);

    // then: se devuelve error de validación
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void newPeticionEvaluacionFromSolicitudCsp_WithUserToken_Returns403() throws Exception {
    // given: un input válido y un token de usuario (no de cliente de servicio)
    PeticionEvaluacionSolicitudCspInput input = PeticionEvaluacionSolicitudCspInput.builder()
        .titulo(Arrays.asList(new I18nFieldValueDto(Language.ES, "PeticionEvaluacionCsp")))
        .personaRef("user-001")
        .existeFinanciacion(Boolean.FALSE)
        .solicitudConvocatoriaRef("1")
        .checklistId(1L)
        .build();

    // when: se intenta crear la petición desde CSP
    final ResponseEntity<String> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_SOLICITUD_CSP, HttpMethod.POST,
        buildRequestWithRoles(input, "ETI-PEV-INV-C", "ETI-PEV-MOD-C"), String.class);

    // then: se deniega el acceso
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void updateEstadoFinanciacion_WithUserToken_Returns403() throws Exception {
    // given: un nuevo estado de financiación y un token de usuario (no de cliente
    // de servicio)
    PeticionEvaluacionEstadoFinanciacionInput input = new PeticionEvaluacionEstadoFinanciacionInput(
        EstadoFinanciacionDto.CONCEDIDO);

    // when: se intenta actualizar el estado de financiación
    final ResponseEntity<String> response = restTemplate.exchange(
        PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_ESTADO_FINANCIACION, HttpMethod.PATCH,
        buildRequestWithRoles(input, "ETI-PEV-E", "ETI-PEV-INV-ER", "ETI-PEV-MOD-C"), String.class, 2L);

    // then: se deniega el acceso
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  @Sql("classpath:scripts/peticion_evaluacion_unidad_vinculacion.sql")
  void findAll_WithUnidadVinculacionRef_ReturnsFilteredPeticionEvaluacionList() throws Exception {
    // given: dos peticiones vinculadas a UV-1
    URI uri = UriComponentsBuilder.fromUriString(PETICION_EVALUACION_CONTROLLER_BASE_PATH)
        .queryParam("s", "id,desc").queryParam("q", "unidadesVinculacion.unidadVinculacionRef==UV-1")
        .build(false).toUri();

    // when: se buscan las peticiones por la unidad
    final ResponseEntity<List<PeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<PeticionEvaluacion>>() {
        });

    // then: se devuelven las dos peticiones
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).extracting(PeticionEvaluacion::getId).containsExactly(3L, 2L);
  }

  @Test
  @Sql("classpath:scripts/peticion_evaluacion_unidad_vinculacion.sql")
  void findAll_WithComiteAndUnidadVinculacionRef_ReturnsPeticionesFromMemorias() throws Exception {
    // given: el filtro por comité solo se resuelve sobre las memorias; solo la
    // petición 2 tiene memorias y está vinculada a UV-1
    URI uri = UriComponentsBuilder.fromUriString(PETICION_EVALUACION_CONTROLLER_BASE_PATH)
        .queryParam("s", "id,desc").queryParam("q", "comite.id==1;unidadesVinculacion.unidadVinculacionRef==UV-1")
        .build(false).toUri();

    // when: se buscan las peticiones por comité y unidad
    final ResponseEntity<List<PeticionEvaluacion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<PeticionEvaluacion>>() {
        });

    // then: se devuelve solo la petición con memorias
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).extracting(PeticionEvaluacion::getId).containsExactly(2L);
  }

}