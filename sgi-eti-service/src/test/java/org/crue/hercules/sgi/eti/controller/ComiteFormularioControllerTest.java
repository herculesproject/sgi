package org.crue.hercules.sgi.eti.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ComiteFormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ComiteFormulario;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.service.ComiteFormularioService;
import org.crue.hercules.sgi.eti.util.ConstantesEti;
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
 * ComiteFormularioControllerTest
 */
@WebMvcTest(ComiteFormularioController.class)
public class ComiteFormularioControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private ComiteFormularioService comiteFormularioService;

  @Test
  public void getComiteFormulario_WithId_ReturnsComiteFormulario() throws Exception {

    Comite comite = new Comite(1L, "Comite", Boolean.TRUE);
    Formulario formulario = new Formulario(1L, "M10", "Descripcion", Boolean.TRUE);

    BDDMockito.given(comiteFormularioService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockComiteFormulario(1L, comite, formulario)));

    mockMvc
        .perform(MockMvcRequestBuilders
            .get(ConstantesEti.COMITE_FORMULARIO_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, 1L))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("formulario").value(formulario))
        .andExpect(MockMvcResultMatchers.jsonPath("comite").value(comite));
    ;
  }

  @Test
  public void getComiteFormulario_NotFound_Returns404() throws Exception {
    BDDMockito.given(comiteFormularioService.findById(ArgumentMatchers.anyLong()))
        .will((InvocationOnMock invocation) -> {
          throw new ComiteFormularioNotFoundException(invocation.getArgument(0));
        });
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(ConstantesEti.COMITE_FORMULARIO_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, 1L))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void newComiteFormulario_ReturnsComiteFormulario() throws Exception {

    Comite comite = new Comite(1L, "Comite", Boolean.TRUE);
    Formulario formulario = new Formulario(1L, "M10", "Descripcion", Boolean.TRUE);

    // given: Un ComiteFormulario nuevo
    String nuevoComiteFormularioJson = "{\"comite\": {\"comite\": \"Comite1\", \"activo\": \"true\"}, \"formulario\": {\"nombre\": \"M10\", \"descripcion\": \"Descripcion\", \"activo\": \"true\"}}";

    ComiteFormulario comiteFormulario = generarMockComiteFormulario(1L, comite, formulario);

    BDDMockito.given(comiteFormularioService.create(ArgumentMatchers.<ComiteFormulario>any()))
        .willReturn(comiteFormulario);

    // when: Creamos un ComiteFormulario
    mockMvc
        .perform(MockMvcRequestBuilders.post(ConstantesEti.COMITE_FORMULARIO_CONTROLLER_BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON).content(nuevoComiteFormularioJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Crea el nuevo ComiteFormulario y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("comite").value(comite))
        .andExpect(MockMvcResultMatchers.jsonPath("formulario").value(formulario));
  }

  @Test
  public void newComiteFormulario_Error_Returns400() throws Exception {
    // given: Un ComiteFormulario nuevo que produce un error al crearse
    String nuevoComiteFormularioJson = "{\"comite\": {\"comite\": \"Comite1\", \"activo\": \"true\"}, \"formulario\": {\"nombre\": \"M10\", \"descripcion\": \"Descripcion\", \"activo\": \"true\"}}";

    BDDMockito.given(comiteFormularioService.create(ArgumentMatchers.<ComiteFormulario>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un ComiteFormulario
    mockMvc
        .perform(MockMvcRequestBuilders.post(ConstantesEti.COMITE_FORMULARIO_CONTROLLER_BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON).content(nuevoComiteFormularioJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  public void replaceComiteFormulario_ReturnsComiteFormulario() throws Exception {

    Comite comite = new Comite(1L, "Comite", Boolean.TRUE);
    Formulario formulario = new Formulario(1L, "M10", "Descripcion", Boolean.TRUE);

    // given: Un ComiteFormulario a modificar
    String replaceComiteFormularioJson = "{\"comite\": {\"comite\": \"Comite1\", \"activo\": \"true\"}, \"formulario\": {\"nombre\": \"M10\", \"descripcion\": \"Descripcion\", \"activo\": \"true\"}}";

    ComiteFormulario comiteFormulario = generarMockComiteFormulario(1L, comite, formulario);

    BDDMockito.given(comiteFormularioService.update(ArgumentMatchers.<ComiteFormulario>any()))
        .willReturn(comiteFormulario);

    mockMvc
        .perform(MockMvcRequestBuilders
            .put(ConstantesEti.COMITE_FORMULARIO_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON).content(replaceComiteFormularioJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Modifica el ComiteFormulario y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("comite").value(comite))
        .andExpect(MockMvcResultMatchers.jsonPath("formulario").value(formulario));

  }

  @Test
  public void replaceComiteFormulario_NotFound() throws Exception {
    // given: Un ComiteFormulario a modificar
    String replaceComiteFormularioJson = "{\"comite\": {\"comite\": \"Comite1\", \"activo\": \"true\"}, \"formulario\": {\"nombre\": \"M10\", \"descripcion\": \"Descripcion\", \"activo\": \"true\"}}";

    BDDMockito.given(comiteFormularioService.update(ArgumentMatchers.<ComiteFormulario>any()))
        .will((InvocationOnMock invocation) -> {
          throw new ComiteFormularioNotFoundException(((ComiteFormulario) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(ConstantesEti.COMITE_FORMULARIO_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON).content(replaceComiteFormularioJson))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  public void removeComiteFormulario_ReturnsOk() throws Exception {

    Comite comite = new Comite(1L, "Comite", Boolean.TRUE);
    Formulario formulario = new Formulario(1L, "M10", "Descripcion", Boolean.TRUE);

    BDDMockito.given(comiteFormularioService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockComiteFormulario(1L, comite, formulario));

    mockMvc
        .perform(MockMvcRequestBuilders
            .delete(ConstantesEti.COMITE_FORMULARIO_CONTROLLER_BASE_PATH + ConstantesEti.PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void findAll_Unlimited_ReturnsFullComiteFormularioList() throws Exception {

    Comite comite = new Comite(1L, "Comite", Boolean.TRUE);
    Formulario formulario = new Formulario(1L, "M10", "Descripcion", Boolean.TRUE);

    // given: One hundred ComiteFormulario
    List<ComiteFormulario> comiteFormularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      comiteFormularios.add(generarMockComiteFormulario(Long.valueOf(i), comite, formulario));
    }

    BDDMockito.given(
        comiteFormularioService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(comiteFormularios));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(ConstantesEti.COMITE_FORMULARIO_CONTROLLER_BASE_PATH)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred ComiteFormulario
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  public void findAll_WithPaging_ReturnsComiteFormularioSubList() throws Exception {

    Comite comite = new Comite(1L, "Comite", Boolean.TRUE);
    Formulario formulario = new Formulario(1L, "M10", "Descripcion", Boolean.TRUE);

    // given: One hundred ComiteFormulario
    List<ComiteFormulario> comiteFormularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      comite.setComite("Comite" + String.format("%03d", i));
      comiteFormularios.add(generarMockComiteFormulario(Long.valueOf(i), comite, formulario));
    }

    BDDMockito.given(
        comiteFormularioService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ComiteFormulario>>() {
          @Override
          public Page<ComiteFormulario> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<ComiteFormulario> content = comiteFormularios.subList(fromIndex, toIndex);
            Page<ComiteFormulario> page = new PageImpl<>(content, pageable, comiteFormularios.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(ConstantesEti.COMITE_FORMULARIO_CONTROLLER_BASE_PATH).header("X-Page", "3")
            .header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked ComiteFormularios are returned with the right page
        // information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<ComiteFormulario> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ComiteFormulario>>() {
        });

    // containing id='31' to '40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      ComiteFormulario comiteFormulario = actual.get(i);
      Assertions.assertThat(comiteFormulario.getId()).isEqualTo(j);
    }
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredComiteFormularioList() throws Exception {

    Comite comite = new Comite(1L, "Comite", Boolean.TRUE);
    Formulario formulario = new Formulario(1L, "M10", "Descripcion", Boolean.TRUE);

    // given: One hundred ComiteFormulario and a search query
    List<ComiteFormulario> comiteFormularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      comite.setComite(comite.getComite() + String.format("%03d", i));
      comiteFormularios.add(generarMockComiteFormulario(Long.valueOf(i), comite, formulario));
    }
    String query = "id:5";

    BDDMockito.given(
        comiteFormularioService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ComiteFormulario>>() {
          @Override
          public Page<ComiteFormulario> answer(InvocationOnMock invocation) throws Throwable {
            List<QueryCriteria> queryCriterias = invocation.<List<QueryCriteria>>getArgument(0);

            List<ComiteFormulario> content = new ArrayList<>();
            for (ComiteFormulario comiteFormulario : comiteFormularios) {
              boolean add = true;
              for (QueryCriteria queryCriteria : queryCriterias) {
                Field field = ReflectionUtils.findField(ComiteFormulario.class, queryCriteria.getKey());
                field.setAccessible(true);
                String fieldValue = ReflectionUtils.getField(field, comiteFormulario).toString();
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
                content.add(comiteFormulario);
              }
            }
            Page<ComiteFormulario> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(ConstantesEti.COMITE_FORMULARIO_CONTROLLER_BASE_PATH).param("q", query)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred ComiteFormulario
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto ComiteFormulario
   * 
   * @param id         id del ComiteFormulario
   * @param comite     el Comite de ComiteFormulario
   * @param formulario el Formulario de ComiteFormulario
   * @return el objeto ComiteFormulario
   */

  public ComiteFormulario generarMockComiteFormulario(Long id, Comite comite, Formulario formulario) {

    ComiteFormulario comiteFormulario = new ComiteFormulario();
    comiteFormulario.setId(id);
    comiteFormulario.setComite(comite);
    comiteFormulario.setFormulario(formulario);

    return comiteFormulario;
  }

}