package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.TipoRegimenConcurrenciaNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.service.TipoRegimenConcurrenciaService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * TipoRegimenConcurrenciaControllerTest
 */
@WebMvcTest(TipoRegimenConcurrenciaController.class)
public class TipoRegimenConcurrenciaControllerTest extends BaseControllerTest {

  @MockBean
  private TipoRegimenConcurrenciaService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/tiporegimenconcurrencias";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ADMIN" })
  public void create_ReturnsTipoRegimenConcurrencia() throws Exception {
    // given: new TipoRegimenConcurrencia
    TipoRegimenConcurrencia data = generarMockTipoRegimenConcurrencia(null, Boolean.TRUE);

    BDDMockito.given(service.create(ArgumentMatchers.<TipoRegimenConcurrencia>any()))
        .willAnswer(new Answer<TipoRegimenConcurrencia>() {
          @Override
          public TipoRegimenConcurrencia answer(InvocationOnMock invocation) throws Throwable {
            TipoRegimenConcurrencia givenData = invocation.getArgument(0, TipoRegimenConcurrencia.class);
            TipoRegimenConcurrencia newData = new TipoRegimenConcurrencia();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create TipoRegimenConcurrencia
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(data)))
        .andDo(MockMvcResultHandlers.print())
        // then: new TipoRegimenConcurrencia is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(data.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(data.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ADMIN" })
  public void create_WithId_Returns400() throws Exception {
    // given: a TipoRegimenConcurrencia with id filled
    TipoRegimenConcurrencia data = generarMockTipoRegimenConcurrencia(1L, Boolean.TRUE);

    BDDMockito.given(service.create(ArgumentMatchers.<TipoRegimenConcurrencia>any()))
        .willThrow(new IllegalArgumentException());

    // when: create TipoRegimenConcurrencia
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(data)))
        .andDo(MockMvcResultHandlers.print())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ADMIN" })
  public void update_WithExistingId_ReturnsTipoRegimenConcurrencia() throws Exception {
    // given: existing TipoRegimenConcurrencia
    TipoRegimenConcurrencia data = generarMockTipoRegimenConcurrencia(1L, Boolean.TRUE);

    BDDMockito.given(service.update(ArgumentMatchers.<TipoRegimenConcurrencia>any()))
        .willAnswer(new Answer<TipoRegimenConcurrencia>() {
          @Override
          public TipoRegimenConcurrencia answer(InvocationOnMock invocation) throws Throwable {
            TipoRegimenConcurrencia givenData = invocation.getArgument(0, TipoRegimenConcurrencia.class);
            return givenData;
          }
        });

    // when: update TipoRegimenConcurrencia
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, data.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(data)))
        .andDo(MockMvcResultHandlers.print())
        // then: TipoRegimenConcurrencia is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(data.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(data.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(data.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ADMIN" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: a TipoRegimenConcurrencia with non existing id
    TipoRegimenConcurrencia data = generarMockTipoRegimenConcurrencia(1L, Boolean.TRUE);

    BDDMockito.willThrow(new TipoRegimenConcurrenciaNotFoundException(data.getId())).given(service)
        .findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<TipoRegimenConcurrencia>any()))
        .willThrow(new TipoRegimenConcurrenciaNotFoundException(data.getId()));

    // when: update TipoRegimenConcurrencia
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, data.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(data)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ADMIN" })
  public void delete_WithExistingId_Return204() throws Exception {
    // given: existing id
    TipoRegimenConcurrencia data = generarMockTipoRegimenConcurrencia(1L, Boolean.TRUE);
    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willReturn(data);

    // when: delete by id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, data.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ADMIN" })
  public void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new TipoRegimenConcurrenciaNotFoundException(id)).given(service)
        .findById(ArgumentMatchers.<Long>any());
    BDDMockito.willThrow(new TipoRegimenConcurrenciaNotFoundException(id)).given(service)
        .disable(ArgumentMatchers.<Long>any());

    // when: delete by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ADMIN" })
  public void findById_WithExistingId_ReturnsTipoRegimenConcurrencia() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<TipoRegimenConcurrencia>() {
      @Override
      public TipoRegimenConcurrencia answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarMockTipoRegimenConcurrencia(id, Boolean.TRUE);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested TipoRegimenConcurrencia is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ADMIN" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new TipoRegimenConcurrenciaNotFoundException(1L);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ADMIN" })
  public void findAll_WithPaging_ReturnsTipoRegimenConcurrenciaSubList() throws Exception {
    // given: One hundred TipoRegimenConcurrencia
    List<TipoRegimenConcurrencia> data = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      data.add(generarMockTipoRegimenConcurrencia(Long.valueOf(i), Boolean.TRUE));
    }

    BDDMockito.given(service.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoRegimenConcurrencia>>() {
          @Override
          public Page<TipoRegimenConcurrencia> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoRegimenConcurrencia> content = data.subList(fromIndex, toIndex);
            Page<TipoRegimenConcurrencia> page = new PageImpl<>(content, pageable, data.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked TipoRegimenConcurrencia are returned with the right page
        // information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoRegimenConcurrencia> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoRegimenConcurrencia>>() {
        });

    // containing Nombre='Nombre-31' to 'Nombre-40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoRegimenConcurrencia item = actual.get(i);
      Assertions.assertThat(item.getNombre()).isEqualTo("nombre-" + j);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ADMIN" })
  public void findAll_EmptyList_Returns204() throws Exception {
    // given: no data TipoRegimenConcurrencia
    BDDMockito.given(service.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoRegimenConcurrencia>>() {
          @Override
          public Page<TipoRegimenConcurrencia> answer(InvocationOnMock invocation) throws Throwable {
            Page<TipoRegimenConcurrencia> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ADMIN" })
  public void findAllTodos_WithPaging_ReturnsTipoRegimenConcurrenciaSubList() throws Exception {
    // given: One hundred TipoRegimenConcurrencia
    List<TipoRegimenConcurrencia> data = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      data.add(generarMockTipoRegimenConcurrencia(Long.valueOf(i), Boolean.TRUE));
    }

    BDDMockito
        .given(service.findAllTodos(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoRegimenConcurrencia>>() {
          @Override
          public Page<TipoRegimenConcurrencia> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoRegimenConcurrencia> content = data.subList(fromIndex, toIndex);
            Page<TipoRegimenConcurrencia> page = new PageImpl<>(content, pageable, data.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked TipoRegimenConcurrencia are returned with the right page
        // information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TipoRegimenConcurrencia> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TipoRegimenConcurrencia>>() {
        });

    // containing Nombre='Nombre-31' to 'Nombre-40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoRegimenConcurrencia item = actual.get(i);
      Assertions.assertThat(item.getNombre()).isEqualTo("nombre-" + j);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ADMIN" })
  public void findAllTodos_EmptyList_Returns204() throws Exception {
    // given: no data TipoRegimenConcurrencia
    BDDMockito
        .given(service.findAllTodos(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoRegimenConcurrencia>>() {
          @Override
          public Page<TipoRegimenConcurrencia> answer(InvocationOnMock invocation) throws Throwable {
            Page<TipoRegimenConcurrencia> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto TipoRegimenConcurrencia
   * 
   * @param id
   * @param activo
   * @return TipoRegimenConcurrencia
   */
  private TipoRegimenConcurrencia generarMockTipoRegimenConcurrencia(Long id, Boolean activo) {
    return TipoRegimenConcurrencia.builder().id(id).nombre("nombre-" + id).activo(activo).build();
  }

}
