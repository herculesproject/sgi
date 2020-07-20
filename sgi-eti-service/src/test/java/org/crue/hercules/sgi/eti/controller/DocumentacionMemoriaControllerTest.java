package org.crue.hercules.sgi.eti.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.DocumentacionMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.service.DocumentacionMemoriaService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ReflectionUtils;

/**
 * DocumentacionMemoriaControllerTest
 */
@WebMvcTest(DocumentacionMemoriaController.class)
public class DocumentacionMemoriaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private DocumentacionMemoriaService documentacionMemoriaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH = "/documentacionmemorias";

  @Test
  public void getDocumentacionMemoria_WithId_ReturnsDocumentacionMemoria() throws Exception {

    Memoria memoria = generarMockMemoria(1L, "Memoria1");
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);

    BDDMockito.given(documentacionMemoriaService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockDocumentacionMemoria(1L, memoria, tipoDocumento)));

    mockMvc.perform(MockMvcRequestBuilders.get(DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.titulo").value("Memoria1"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoDocumento.nombre").value("TipoDocumento1"))
        .andExpect(MockMvcResultMatchers.jsonPath("documentoRef").value("doc-001"));

    ;
  }

  @Test
  public void getDocumentacionMemoria_NotFound_Returns404() throws Exception {
    BDDMockito.given(documentacionMemoriaService.findById(ArgumentMatchers.anyLong()))
        .will((InvocationOnMock invocation) -> {
          throw new DocumentacionMemoriaNotFoundException(invocation.getArgument(0));
        });
    mockMvc.perform(MockMvcRequestBuilders.get(DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void newDocumentacionMemoria_ReturnsDocumentacionMemoria() throws Exception {

    Memoria memoria = generarMockMemoria(1L, "Memoria1");
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);

    // given: Una DocumentacionMemoria nueva
    String nuevoDocumentacionMemoriaJson = "{\"documentoRef\": \"doc-001\", \"memoria\": {\"titulo\": \"Memoria1\"}, \"tipoDocumento\": {\"nombre\": \"TipoDocumento1\"}}";

    DocumentacionMemoria documentacionMemoria = generarMockDocumentacionMemoria(1L, memoria, tipoDocumento);

    BDDMockito.given(documentacionMemoriaService.create(ArgumentMatchers.<DocumentacionMemoria>any()))
        .willReturn(documentacionMemoria);

    // when: Creamos un DocumentacionMemoria
    mockMvc
        .perform(MockMvcRequestBuilders.post(DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON).content(nuevoDocumentacionMemoriaJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Crea el nuevo DocumentacionMemoria y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.titulo").value("Memoria1"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoDocumento.nombre").value("TipoDocumento1"))
        .andExpect(MockMvcResultMatchers.jsonPath("documentoRef").value("doc-001"));

  }

  @Test
  public void newDocumentacionMemoria_Error_Returns400() throws Exception {

    // given: Una DocumentacionMemoria nueva que produce un error al crearse
    String nuevoDocumentacionMemoriaJson = "{\"documentoRef\": \"doc-001\", \"memoria\": {\"titulo\": \"Memoria1\"}, \"tipoDocumento\": {\"nombre\": \"TipoDocumento1\"}}";

    BDDMockito.given(documentacionMemoriaService.create(ArgumentMatchers.<DocumentacionMemoria>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos una DocumentacionMemoria
    mockMvc
        .perform(MockMvcRequestBuilders.post(DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON).content(nuevoDocumentacionMemoriaJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  public void replaceDocumentacionMemoria_ReturnsDocumentacionMemoria() throws Exception {

    Memoria memoria = generarMockMemoria(1L, "Memoria1");
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);

    // given: Un DocumentacionMemoria a modificar
    String replaceDocumentacionMemoriaJson = "{\"documentoRef\": \"doc-001\", \"memoria\": {\"titulo\": \"Memoria1\"}, \"tipoDocumento\": {\"nombre\": \"TipoDocumento1\"}}";

    DocumentacionMemoria documentacionMemoria = generarMockDocumentacionMemoria(1L, memoria, tipoDocumento);

    BDDMockito.given(documentacionMemoriaService.update(ArgumentMatchers.<DocumentacionMemoria>any()))
        .willReturn(documentacionMemoria);

    mockMvc
        .perform(MockMvcRequestBuilders.put(DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON).content(replaceDocumentacionMemoriaJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Modifica el DocumentacionMemoria y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.titulo").value("Memoria1"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoDocumento.nombre").value("TipoDocumento1"))
        .andExpect(MockMvcResultMatchers.jsonPath("documentoRef").value("doc-001"));

  }

  @Test
  public void replaceDocumentacionMemoria_NotFound() throws Exception {

    // given: Una DocumentacionMemoria a modificar
    String replaceDocumentacionMemoriaJson = "{\"documentoRef\": \"doc-001\", \"memoria\": {\"titulo\": \"Memoria1\"}, \"tipoDocumento\": {\"nombre\": \"TipoDocumento1\"}}";

    BDDMockito.given(documentacionMemoriaService.update(ArgumentMatchers.<DocumentacionMemoria>any()))
        .will((InvocationOnMock invocation) -> {
          throw new DocumentacionMemoriaNotFoundException(((DocumentacionMemoria) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON).content(replaceDocumentacionMemoriaJson))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  public void removeDocumentacionMemoria_ReturnsOk() throws Exception {

    Memoria memoria = generarMockMemoria(1L, "Memoria1");
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);

    BDDMockito.given(documentacionMemoriaService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockDocumentacionMemoria(1L, memoria, tipoDocumento));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void findAll_Unlimited_ReturnsFullDocumentacionMemoriaList() throws Exception {

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);

    // given: One hundred DocumentacionMemoria
    List<DocumentacionMemoria> documentacionMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      documentacionMemorias.add(generarMockDocumentacionMemoria(Long.valueOf(i),
          generarMockMemoria(Long.valueOf(i), "Memoria" + String.format("%03d", i)), tipoDocumento));
    }

    BDDMockito.given(documentacionMemoriaService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(documentacionMemorias));

    // when: find unlimited
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred DocumentacionMemoria
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  public void findAll_WithPaging_ReturnsDocumentacionMemoriaSubList() throws Exception {

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);

    // given: One hundred DocumentacionMemoria
    List<DocumentacionMemoria> documentacionMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      documentacionMemorias.add(generarMockDocumentacionMemoria(Long.valueOf(i),
          generarMockMemoria(Long.valueOf(i), "Memoria" + String.format("%03d", i)), tipoDocumento));
    }

    BDDMockito.given(documentacionMemoriaService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<DocumentacionMemoria>>() {
          @Override
          public Page<DocumentacionMemoria> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<DocumentacionMemoria> content = documentacionMemorias.subList(fromIndex, toIndex);
            Page<DocumentacionMemoria> page = new PageImpl<>(content, pageable, documentacionMemorias.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH).header("X-Page", "3")
            .header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked DocumentacionMemorias are returned with the right page
        // information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<DocumentacionMemoria> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<DocumentacionMemoria>>() {
        });

    // containing memoria.titulo='Memoria031' to
    // 'Memoria040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      DocumentacionMemoria documentacionMemoria = actual.get(i);
      Assertions.assertThat(documentacionMemoria.getMemoria().getTitulo())
          .isEqualTo("Memoria" + String.format("%03d", j));
    }
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredDocumentacionMemoriaList() throws Exception {

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);

    // given: One hundred DocumentacionMemoria and a search query
    List<DocumentacionMemoria> DocumentacionMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      DocumentacionMemorias.add(generarMockDocumentacionMemoria(Long.valueOf(i),
          generarMockMemoria(Long.valueOf(i), "Memoria" + String.format("%03d", i)), tipoDocumento));
    }
    String query = "id:5";

    BDDMockito.given(documentacionMemoriaService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<DocumentacionMemoria>>() {
          @Override
          public Page<DocumentacionMemoria> answer(InvocationOnMock invocation) throws Throwable {
            List<QueryCriteria> queryCriterias = invocation.<List<QueryCriteria>>getArgument(0);

            List<DocumentacionMemoria> content = new ArrayList<>();
            for (DocumentacionMemoria documentacionMemoria : DocumentacionMemorias) {
              boolean add = true;
              for (QueryCriteria queryCriteria : queryCriterias) {
                Field field = ReflectionUtils.findField(DocumentacionMemoria.class, queryCriteria.getKey());
                field.setAccessible(true);
                String fieldValue = ReflectionUtils.getField(field, documentacionMemoria).toString();
                switch (queryCriteria.getOperation()) {
                  case EQUALS:
                    if (!fieldValue.equals(queryCriteria.getValue())) {
                      add = false;
                    }
                    break;
                  case GREATER:
                    if (!(fieldValue.compareTo(queryCriteria.getValue().toString()) > 0)) {
                      add = false;
                    }
                    break;
                  case GREATER_OR_EQUAL:
                    if (!(fieldValue.compareTo(queryCriteria.getValue().toString()) >= 0)) {
                      add = false;
                    }
                    break;
                  case LIKE:
                    if (!fieldValue.matches((queryCriteria.getValue().toString().replaceAll("%", ".*")))) {
                      add = false;
                    }
                    break;
                  case LOWER:
                    if (!(fieldValue.compareTo(queryCriteria.getValue().toString()) < 0)) {
                      add = false;
                    }
                    break;
                  case LOWER_OR_EQUAL:
                    if (!(fieldValue.compareTo(queryCriteria.getValue().toString()) <= 0)) {
                      add = false;
                    }
                    break;
                  case NOT_EQUALS:
                    if (fieldValue.equals(queryCriteria.getValue())) {
                      add = false;
                    }
                    break;
                  case NOT_LIKE:
                    if (fieldValue.matches((queryCriteria.getValue().toString().replaceAll("%", ".*")))) {
                      add = false;
                    }
                    break;
                  default:
                    break;
                }
              }
              if (add) {
                content.add(documentacionMemoria);
              }
            }
            Page<DocumentacionMemoria> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(DOCUMENTACION_MEMORIA_CONTROLLER_BASE_PATH).param("q", query)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred DocumentacionMemoria
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
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
    documentacionMemoria.setDocumentoRef("doc-00" + id);

    return documentacionMemoria;
  }

  /**
   * Función que devuelve un objeto Memoria
   * 
   * @param id id del Memoria
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
