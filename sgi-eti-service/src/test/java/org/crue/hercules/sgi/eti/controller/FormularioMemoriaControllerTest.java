package org.crue.hercules.sgi.eti.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.FormularioMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.FormularioMemoria;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.service.FormularioMemoriaService;
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
 * FormularioMemoriaControllerTest
 */
@WebMvcTest(FormularioMemoriaController.class)
public class FormularioMemoriaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private FormularioMemoriaService formularioMemoriaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH = "/formulariomemorias";

  @Test
  public void getFormularioMemoria_WithId_ReturnsFormularioMemoria() throws Exception {
    BDDMockito.given(formularioMemoriaService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockFormularioMemoria(1L)));

    mockMvc.perform(MockMvcRequestBuilders.get(FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("memoria").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.id").value(100))
        .andExpect(MockMvcResultMatchers.jsonPath("formulario").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("formulario.id").value(200))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  public void getFormularioMemoria_NotFound_Returns404() throws Exception {
    BDDMockito.given(formularioMemoriaService.findById(ArgumentMatchers.anyLong()))
        .will((InvocationOnMock invocation) -> {
          throw new FormularioMemoriaNotFoundException(invocation.getArgument(0));
        });
    mockMvc.perform(MockMvcRequestBuilders.get(FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void newFormularioMemoria_ReturnsFormularioMemoria() throws Exception {
    // given: Un formulario memoria nuevo
    String nuevoFormularioMemoriaJson = "{\"memoria\": {\"id\": 100}, \"formulario\": {\"id\": 200}, \"activo\": true}";

    FormularioMemoria formularioMemoria = generarMockFormularioMemoria(1L);

    BDDMockito.given(formularioMemoriaService.create(ArgumentMatchers.<FormularioMemoria>any()))
        .willReturn(formularioMemoria);

    // when: Creamos un formulario memoria
    mockMvc
        .perform(MockMvcRequestBuilders.post(FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON).content(nuevoFormularioMemoriaJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Crea el nuevo formulario memoria y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("memoria").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.id").value(100))
        .andExpect(MockMvcResultMatchers.jsonPath("formulario").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("formulario.id").value(200))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  public void newFormularioMemoria_Error_Returns400() throws Exception {
    // given: Un formulario memoria nuevo que produce un error al crearse
    String nuevoFormularioMemoriaJson = "{\"id\": 1, \"memoria\": {\"id\": 100}, \"formulario\": {\"id\": 200}, \"activo\": true}";

    BDDMockito.given(formularioMemoriaService.create(ArgumentMatchers.<FormularioMemoria>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un formularioMemoria
    mockMvc
        .perform(MockMvcRequestBuilders.post(FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON).content(nuevoFormularioMemoriaJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void replaceFormularioMemoria_ReturnsFormularioMemoria() throws Exception {
    // given: Un formulario memoria a modificar
    String replaceFormularioMemoriaJson = "{\"id\": 1, \"memoria\": {\"id\": 100}, \"formulario\": {\"id\": 200}, \"activo\": true}";

    FormularioMemoria formularioMemoriaActualizado = generarMockFormularioMemoria(1L);

    BDDMockito.given(formularioMemoriaService.update(ArgumentMatchers.<FormularioMemoria>any()))
        .willReturn(formularioMemoriaActualizado);

    mockMvc
        .perform(MockMvcRequestBuilders.put(FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON).content(replaceFormularioMemoriaJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Modifica el formulario memoria y lo devuelve
        .andExpect(MockMvcResultMatchers.jsonPath("memoria").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.id").value(100))
        .andExpect(MockMvcResultMatchers.jsonPath("formulario").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("formulario.id").value(200))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  public void replaceFormularioMemoria_NotFound() throws Exception {
    // given: Un formulario memoria a modificar
    String replaceFormularioMemoriaJson = "{\"id\": 1, \"memoria\": {\"id\": 100}, \"formulario\": {\"id\": 200}, \"activo\": true}";

    BDDMockito.given(formularioMemoriaService.update(ArgumentMatchers.<FormularioMemoria>any()))
        .will((InvocationOnMock invocation) -> {
          throw new FormularioMemoriaNotFoundException(((FormularioMemoria) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON).content(replaceFormularioMemoriaJson))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void removeFormularioMemoria_ReturnsOk() throws Exception {
    BDDMockito.given(formularioMemoriaService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockFormularioMemoria(1L));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void findAll_Unlimited_ReturnsFullFormularioMemoriaList() throws Exception {
    // given: One hundred formularios memorias
    List<FormularioMemoria> formulariosMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      formulariosMemorias.add(generarMockFormularioMemoria(Long.valueOf(i)));
    }

    BDDMockito.given(
        formularioMemoriaService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(formulariosMemorias));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred formularios memorias
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  public void findAll_WithPaging_ReturnsFormularioMemoriaSubList() throws Exception {
    // given: One hundred formularios memorias
    List<FormularioMemoria> formularioMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      formularioMemorias.add(generarMockFormularioMemoria(Long.valueOf(i)));
    }

    BDDMockito.given(
        formularioMemoriaService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<FormularioMemoria>>() {
          @Override
          public Page<FormularioMemoria> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<FormularioMemoria> content = formularioMemorias.subList(fromIndex, toIndex);
            Page<FormularioMemoria> page = new PageImpl<>(content, pageable, formularioMemorias.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH).header("X-Page", "3")
            .header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked formularios memorias are returned with the right page
        // information in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<FormularioMemoria> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<FormularioMemoria>>() {
        });

    // containing id='31' to '40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      FormularioMemoria formularioMemoria = actual.get(i);
      Assertions.assertThat(formularioMemoria.getId()).isEqualTo(j);
    }
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredFormularioMemoriaList() throws Exception {
    // given: One hundred formularios memorias and a search query
    List<FormularioMemoria> formulariosMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      formulariosMemorias.add(generarMockFormularioMemoria(Long.valueOf(i)));
    }
    String query = "id:5";

    BDDMockito.given(
        formularioMemoriaService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<FormularioMemoria>>() {
          @Override
          public Page<FormularioMemoria> answer(InvocationOnMock invocation) throws Throwable {
            List<QueryCriteria> queryCriterias = invocation.<List<QueryCriteria>>getArgument(0);

            List<FormularioMemoria> content = new ArrayList<>();
            for (FormularioMemoria formularioMemoria : formulariosMemorias) {
              boolean add = true;
              for (QueryCriteria queryCriteria : queryCriterias) {
                Field field = ReflectionUtils.findField(FormularioMemoria.class, queryCriteria.getKey());
                field.setAccessible(true);
                String fieldValue = ReflectionUtils.getField(field, formularioMemoria).toString();
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
                content.add(formularioMemoria);
              }
            }
            Page<FormularioMemoria> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(FORMULARIO_MEMORIA_CONTROLLER_BASE_PATH).param("q", query)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one formularios memorias
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto FormularioMemoria
   * 
   * @param id     id del formulario memoria
   * @param numero numero del formulario memoria
   * @return el objeto FormularioMemoria
   */
  public FormularioMemoria generarMockFormularioMemoria(Long id) {
    Memoria memoria = new Memoria();
    memoria.setId(100L);

    Formulario formulario = new Formulario();
    formulario.setId(200L);

    FormularioMemoria formularioMemoria = new FormularioMemoria();
    formularioMemoria.setId(id);
    formularioMemoria.setMemoria(memoria);
    formularioMemoria.setFormulario(formulario);
    formularioMemoria.setActivo(true);

    return formularioMemoria;
  }

}
