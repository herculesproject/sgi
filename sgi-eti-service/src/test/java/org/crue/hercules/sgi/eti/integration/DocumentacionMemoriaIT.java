package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
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

import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer;
import org.crue.hercules.sgi.framework.test.security.Oauth2WireMockInitializer.TokenBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

/**
 * Test de integracion de DocumentacionMemoria.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = { Oauth2WireMockInitializer.class })
public class DocumentacionMemoriaIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenBuilder tokenBuilder;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH = "/documentacionmemorias";

  private HttpEntity<DocumentacionMemoria> buildRequest(HttpHeaders headers, DocumentacionMemoria entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", "ETI-DOCUMENTACIONMEMORIA-EDITAR", "ETI-DOCUMENTACIONMEMORIA-VER")));

    HttpEntity<DocumentacionMemoria> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getDocumentacionMemoria_WithId_ReturnsDocumentacionMemoria() throws Exception {
    final ResponseEntity<DocumentacionMemoria> response = restTemplate.exchange(
        DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        DocumentacionMemoria.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final DocumentacionMemoria documentacionMemoria = response.getBody();

    Assertions.assertThat(documentacionMemoria.getId()).isEqualTo(1L);
    Assertions.assertThat(documentacionMemoria.getMemoria().getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(documentacionMemoria.getTipoDocumento().getNombre()).isEqualTo("TipoDocumento1");
    Assertions.assertThat(documentacionMemoria.getDocumentoRef()).isEqualTo("doc-001");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeDocumentacionMemoria_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<DocumentacionMemoria> response = restTemplate.exchange(
        DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        DocumentacionMemoria.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeDocumentacionMemoria_DoNotGetDocumentacionMemoria() throws Exception {

    final ResponseEntity<DocumentacionMemoria> response = restTemplate.exchange(
        DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, buildRequest(null, null),
        DocumentacionMemoria.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceDocumentacionMemoria_ReturnsDocumentacionMemoria() throws Exception {

    Memoria memoria = generarMockMemoria(1L, "Memoria1");
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);

    DocumentacionMemoria replaceDocumentacionMemoria = generarMockDocumentacionMemoria(1L, memoria, tipoDocumento);

    final ResponseEntity<DocumentacionMemoria> response = restTemplate.exchange(
        DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, replaceDocumentacionMemoria), DocumentacionMemoria.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final DocumentacionMemoria documentacionMemoria = response.getBody();

    Assertions.assertThat(documentacionMemoria.getId()).isNotNull();
    Assertions.assertThat(documentacionMemoria.getMemoria().getTitulo())
        .isEqualTo(replaceDocumentacionMemoria.getMemoria().getTitulo());
    Assertions.assertThat(documentacionMemoria.getTipoDocumento().getNombre())
        .isEqualTo(replaceDocumentacionMemoria.getTipoDocumento().getNombre());
    Assertions.assertThat(documentacionMemoria.getDocumentoRef())
        .isEqualTo(replaceDocumentacionMemoria.getDocumentoRef());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsDocumentacionMemoriaSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    final ResponseEntity<List<DocumentacionMemoria>> response = restTemplate.exchange(
        DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH, HttpMethod.GET, buildRequest(headers, null),
        new ParameterizedTypeReference<List<DocumentacionMemoria>>() {
        });

    // then: Respuesta OK, DocumentacionMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<DocumentacionMemoria> DocumentacionMemorias = response.getBody();
    Assertions.assertThat(DocumentacionMemorias.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de memoria.tiutulo='Memoria6' a
    // 'Memoria8'
    Assertions.assertThat(DocumentacionMemorias.get(0).getMemoria().getTitulo()).isEqualTo("Memoria6");
    Assertions.assertThat(DocumentacionMemorias.get(1).getMemoria().getTitulo()).isEqualTo("Memoria7");
    Assertions.assertThat(DocumentacionMemorias.get(2).getMemoria().getTitulo()).isEqualTo("Memoria8");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredDocumentacionMemoriaList() throws Exception {
    // when: Búsqueda por id equals
    String query = "id:5";

    URI uri = UriComponentsBuilder.fromUriString(DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<DocumentacionMemoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<DocumentacionMemoria>>() {
        });

    // then: Respuesta OK, DocumentacionMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<DocumentacionMemoria> documentacionMemorias = response.getBody();
    Assertions.assertThat(documentacionMemorias.size()).isEqualTo(1);
    Assertions.assertThat(documentacionMemorias.get(0).getId()).isEqualTo(5L);
    Assertions.assertThat(documentacionMemorias.get(0).getMemoria().getTitulo()).startsWith("Memoria");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedDocumentacionMemoriaList() throws Exception {
    // when: Ordenación por documentoRef desc
    String query = "documentoRef-";

    URI uri = UriComponentsBuilder.fromUriString(DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<DocumentacionMemoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null), new ParameterizedTypeReference<List<DocumentacionMemoria>>() {
        });

    // then: Respuesta OK, DocumentacionMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<DocumentacionMemoria> documentacionMemorias = response.getBody();
    Assertions.assertThat(documentacionMemorias.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      DocumentacionMemoria documentacionMemoria = documentacionMemorias.get(i);
      Assertions.assertThat(documentacionMemoria.getId()).isEqualTo(8 - i);
      Assertions.assertThat(documentacionMemoria.getMemoria().getTitulo())
          .isEqualTo("Memoria" + String.format("%03d", 8 - i));
      Assertions.assertThat(documentacionMemoria.getDocumentoRef()).isEqualTo("doc-" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsDocumentacionMemoriaSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por documentoRef desc
    String sort = "documentoRef-";
    // when: Filtra por documentoRef like
    String filter = "documentoRef~%00%";

    URI uri = UriComponentsBuilder.fromUriString(DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<DocumentacionMemoria>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<DocumentacionMemoria>>() {
        });

    // then: Respuesta OK, DocumentacionMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<DocumentacionMemoria> documentacionMemorias = response.getBody();
    Assertions.assertThat(documentacionMemorias.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene documentoRef='doc-001', 'doc-002', 'doc-003'
    Assertions.assertThat(documentacionMemorias.get(0).getDocumentoRef()).isEqualTo("doc-" + String.format("%03d", 3));
    Assertions.assertThat(documentacionMemorias.get(1).getDocumentoRef()).isEqualTo("doc-" + String.format("%03d", 2));
    Assertions.assertThat(documentacionMemorias.get(2).getDocumentoRef()).isEqualTo("doc-" + String.format("%03d", 1));

  }

  /**
   * Función que devuelve un objeto DocumentacionMemoria
   * 
   * @param id            id de DocumentacionMemoria
   * @param memoria       la Memoria de DocumentacionMemoria
   * @param tipoDocumento el TipoDocumento de DocumentacionMemoria
   * @return el objeto DocumentacionMemoria
   */

  public DocumentacionMemoria generarMockDocumentacionMemoria(Long id, Memoria memoria, TipoDocumento tipoDocumento) {

    DocumentacionMemoria documentacionMemoria = new DocumentacionMemoria();
    documentacionMemoria.setId(id);
    documentacionMemoria.setMemoria(memoria);
    documentacionMemoria.setTipoDocumento(tipoDocumento);
    documentacionMemoria.setDocumentoRef("doc-00" + (id == null ? 1 : id));
    documentacionMemoria.setAportado(Boolean.TRUE);

    return documentacionMemoria;
  }

  /**
   * Función que devuelve un objeto Memoria
   * 
   * @param id     id del Memoria
   * @param titulo título de la memoria
   * @return el objeto Memoria
   */

  public Memoria generarMockMemoria(Long id, String titulo) {

    Memoria memoria = new Memoria();
    memoria.setId(id);
    memoria.setTitulo(titulo);

    return memoria;
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */

  public TipoDocumento generarMockTipoDocumento(Long id) {

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre("TipoDocumento" + id);

    return tipoDocumento;
  }

}