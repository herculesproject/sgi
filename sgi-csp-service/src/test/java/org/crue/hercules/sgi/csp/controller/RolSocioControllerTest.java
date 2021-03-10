package org.crue.hercules.sgi.csp.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.RolSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.service.RolSocioService;
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
 * RolSocioControllerTest
 */
@WebMvcTest(RolSocioController.class)
public class RolSocioControllerTest extends BaseControllerTest {

  @MockBean
  private RolSocioService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String PATH_PARAMETER_TODOS = "/todos";
  private static final String CONTROLLER_BASE_PATH = "/rolsocios";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RSOC-C" })
  public void create_ReturnsRolSocio() throws Exception {
    // given: new RolSocio
    RolSocio rolSocio = generarMockRolSocio(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<RolSocio>any())).willAnswer(new Answer<RolSocio>() {
      @Override
      public RolSocio answer(InvocationOnMock invocation) throws Throwable {
        RolSocio givenData = invocation.getArgument(0, RolSocio.class);
        RolSocio newData = new RolSocio();
        BeanUtils.copyProperties(givenData, newData);
        newData.setId(rolSocio.getId());
        return newData;
      }
    });

    // when: create RolSocio
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(rolSocio)))
        .andDo(MockMvcResultHandlers.print())
        // then: new RolSocio is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("abreviatura").value(rolSocio.getAbreviatura()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(rolSocio.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(rolSocio.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("coordinador").value(rolSocio.getCoordinador()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(Boolean.TRUE));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RSOC-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a RolSocio with id filled
    RolSocio newRolSocio = generarMockRolSocio(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<RolSocio>any())).willThrow(new IllegalArgumentException());

    // when: create RolSocio
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(newRolSocio)))
        .andDo(MockMvcResultHandlers.print())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RSOC-E" })
  public void update_WithExistingId_ReturnsRolSocio() throws Exception {
    // given: existing RolSocio
    RolSocio rolSocioExistente = generarMockRolSocio(1L);
    RolSocio rolSocio = generarMockRolSocio(1L);
    rolSocio.setDescripcion("descripcion-modificada");

    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any())).willReturn(rolSocioExistente);
    BDDMockito.given(service.update(ArgumentMatchers.<RolSocio>any())).willAnswer(new Answer<RolSocio>() {
      @Override
      public RolSocio answer(InvocationOnMock invocation) throws Throwable {
        RolSocio givenData = invocation.getArgument(0, RolSocio.class);
        return givenData;
      }
    });

    // when: update RolSocio
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, rolSocioExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(rolSocio)))
        .andDo(MockMvcResultHandlers.print())
        // then: RolSocio is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(rolSocioExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("abreviatura").value(rolSocioExistente.getAbreviatura()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(rolSocioExistente.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(rolSocio.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("coordinador").value(rolSocioExistente.getCoordinador()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(rolSocioExistente.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RSOC-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: a RolSocio with non existing id
    RolSocio rolSocio = generarMockRolSocio(1L);

    BDDMockito.willThrow(new RolSocioNotFoundException(rolSocio.getId())).given(service)
        .findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<RolSocio>any()))
        .willThrow(new RolSocioNotFoundException(rolSocio.getId()));

    // when: update RolSocio
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, rolSocio.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(rolSocio)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RSOC-X" })
  public void enable_WithExistingId_Return204() throws Exception {
    // given: existing id
    RolSocio rolSocio = generarMockRolSocio(1L, Boolean.FALSE);

    BDDMockito.given(service.enable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      RolSocio rolSocioDisabled = new RolSocio();
      BeanUtils.copyProperties(rolSocio, rolSocioDisabled);
      rolSocioDisabled.setActivo(Boolean.TRUE);
      return rolSocioDisabled;
    });

    // when: enable by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, rolSocio.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: rolSocio is enabled
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(rolSocio.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(Boolean.TRUE));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RSOC-X" })
  public void enable_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new RolSocioNotFoundException(id)).given(service).enable(ArgumentMatchers.<Long>any());

    // when: enable by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RSOC-B" })
  public void disable_WithExistingId_Return204() throws Exception {
    // given: existing id
    RolSocio rolSocio = generarMockRolSocio(1L);

    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      RolSocio rolSocioDisabled = new RolSocio();
      BeanUtils.copyProperties(rolSocio, rolSocioDisabled);
      rolSocioDisabled.setActivo(Boolean.FALSE);
      return rolSocioDisabled;
    });

    // when: desactivar by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, rolSocio.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: rolSocio is disabled
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(rolSocio.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(Boolean.FALSE));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RSOC-B" })
  public void disable_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new RolSocioNotFoundException(id)).given(service).disable(ArgumentMatchers.<Long>any());

    // when: disable by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RSOC-V" })
  public void existsById_WithExistingId_Returns200() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: exists by id
    mockMvc
        .perform(MockMvcRequestBuilders.head(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print())
        // then: 200 OK
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RSOC-V" })
  public void existsById_WithNoExistingId_Returns204() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: exists by id
    mockMvc
        .perform(MockMvcRequestBuilders.head(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print())
        // then: 204 No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RSOC-V" })
  public void findById_WithExistingId_ReturnsRolSocio() throws Exception {
    // given: existing id
    RolSocio rolSocioExistente = generarMockRolSocio(1L);
    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any())).willReturn(rolSocioExistente);

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested RolSocio is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RSOC-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new RolSocioNotFoundException(1L);
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
  @WithMockUser(username = "user", authorities = { "CSP-RSOC-V" })
  public void findAll_WithPaging_ReturnsRolSocioSubList() throws Exception {
    // given: One hundred RolSocio
    List<RolSocio> rolSocios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {

      if (i % 2 == 0) {
        rolSocios.add(generarMockRolSocio(Long.valueOf(i)));
      }
    }

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RolSocio>>() {
          @Override
          public Page<RolSocio> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<RolSocio> content = rolSocios.subList(fromIndex, toIndex);
            Page<RolSocio> page = new PageImpl<>(content, pageable, rolSocios.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked RolSocio are returned with the right page information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "50"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<RolSocio> actual = mapper.readValue(requestResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
        new TypeReference<List<RolSocio>>() {
        });

    // containing Codigo='codigo-62' to 'codigo-80'
    for (int i = 0, j = 62; i < 10; i++, j += 2) {
      RolSocio item = actual.get(i);
      Assertions.assertThat(item.getAbreviatura()).isEqualTo(String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo(Boolean.TRUE);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RSOC-V" })
  public void findAll_EmptyList_Returns204() throws Exception {
    // given: no data RolSocio
    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RolSocio>>() {
          @Override
          public Page<RolSocio> answer(InvocationOnMock invocation) throws Throwable {
            Page<RolSocio> page = new PageImpl<>(Collections.emptyList());
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
  @WithMockUser(username = "user", authorities = { "SYSADMIN" })
  public void findAllTodos_WithPaging_ReturnsRolSocioSubList() throws Exception {
    // given: One hundred RolSocio
    List<RolSocio> rolSocios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      rolSocios.add(generarMockRolSocio(Long.valueOf(i), (i % 2 == 0) ? Boolean.TRUE : Boolean.FALSE));
    }

    BDDMockito.given(service.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RolSocio>>() {
          @Override
          public Page<RolSocio> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<RolSocio> content = rolSocios.subList(fromIndex, toIndex);
            Page<RolSocio> page = new PageImpl<>(content, pageable, rolSocios.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_TODOS)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked RolSocio are returned with the right page information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<RolSocio> actual = mapper.readValue(requestResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
        new TypeReference<List<RolSocio>>() {
        });

    // containing Codigo='codigo-31' to 'codigo-40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      RolSocio item = actual.get(i);
      Assertions.assertThat(item.getAbreviatura()).isEqualTo(String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo((j % 2 == 0 ? Boolean.TRUE : Boolean.FALSE));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RSOC-V" })
  public void findAllTodos_EmptyList_Returns204() throws Exception {
    // given: no data RolSocio
    BDDMockito.given(service.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RolSocio>>() {
          @Override
          public Page<RolSocio> answer(InvocationOnMock invocation) throws Throwable {
            Page<RolSocio> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_TODOS)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que genera RolSocio
   * 
   * @param rolSocioId
   * @return el rolSocio
   */
  private RolSocio generarMockRolSocio(Long rolSocioId) {

    String suffix = String.format("%03d", rolSocioId);

    // @formatter:off
    RolSocio rolSocio = RolSocio.builder()
        .id(rolSocioId)
        .abreviatura(suffix)
        .nombre("nombre-" + suffix)
        .descripcion("descripcion-" + suffix)
        .coordinador(Boolean.FALSE)
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on

    return rolSocio;
  }

  /**
   * Función que genera RolSocio con el estado indicado
   * 
   * @param rolSocioId
   * @param activo
   * @return el rolSocio
   */
  private RolSocio generarMockRolSocio(Long rolSocioId, Boolean activo) {

    RolSocio rolSocio = generarMockRolSocio(rolSocioId);
    rolSocio.setActivo(activo);

    return rolSocio;
  }

}