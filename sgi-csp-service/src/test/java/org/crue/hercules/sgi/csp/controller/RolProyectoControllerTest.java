package org.crue.hercules.sgi.csp.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.RolProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.service.RolProyectoService;
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
 * RolProyectoControllerTest
 */
@WebMvcTest(RolProyectoController.class)
public class RolProyectoControllerTest extends BaseControllerTest {

  @MockBean
  private RolProyectoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String PATH_PARAMETER_TODOS = "/todos";
  private static final String CONTROLLER_BASE_PATH = "/rolproyectos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RPRO-C" })
  public void create_ReturnsRolProyecto() throws Exception {
    // given: new RolProyecto
    RolProyecto rolProyecto = generarMockRolProyecto(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<RolProyecto>any())).willAnswer(new Answer<RolProyecto>() {
      @Override
      public RolProyecto answer(InvocationOnMock invocation) throws Throwable {
        RolProyecto givenData = invocation.getArgument(0, RolProyecto.class);
        RolProyecto newData = new RolProyecto();
        BeanUtils.copyProperties(givenData, newData);
        newData.setId(rolProyecto.getId());
        return newData;
      }
    });

    // when: create RolProyecto
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(rolProyecto)))
        .andDo(MockMvcResultHandlers.print())
        // then: new RolProyecto is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("abreviatura").value(rolProyecto.getAbreviatura()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(rolProyecto.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(rolProyecto.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("rolPrincipal").value(rolProyecto.getRolPrincipal()))
        .andExpect(MockMvcResultMatchers.jsonPath("responsableEconomico").value(rolProyecto.getResponsableEconomico()))
        .andExpect(MockMvcResultMatchers.jsonPath("equipo").value(rolProyecto.getEquipo().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("colectivoRef").value(rolProyecto.getColectivoRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(Boolean.TRUE));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RPRO-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a RolProyecto with id filled
    RolProyecto newRolProyecto = generarMockRolProyecto(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<RolProyecto>any())).willThrow(new IllegalArgumentException());

    // when: create RolProyecto
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(newRolProyecto)))
        .andDo(MockMvcResultHandlers.print())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RPRO-E" })
  public void update_WithExistingId_ReturnsRolProyecto() throws Exception {
    // given: existing RolProyecto
    RolProyecto rolProyectoExistente = generarMockRolProyecto(1L);
    RolProyecto rolProyecto = generarMockRolProyecto(1L);
    rolProyecto.setDescripcion("descripcion-modificada");

    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any())).willReturn(rolProyectoExistente);
    BDDMockito.given(service.update(ArgumentMatchers.<RolProyecto>any())).willAnswer(new Answer<RolProyecto>() {
      @Override
      public RolProyecto answer(InvocationOnMock invocation) throws Throwable {
        RolProyecto givenData = invocation.getArgument(0, RolProyecto.class);
        return givenData;
      }
    });

    // when: update RolProyecto
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, rolProyectoExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(rolProyecto)))
        .andDo(MockMvcResultHandlers.print())
        // then: RolProyecto is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(rolProyectoExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("abreviatura").value(rolProyectoExistente.getAbreviatura()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(rolProyectoExistente.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(rolProyecto.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("rolPrincipal").value(rolProyecto.getRolPrincipal()))
        .andExpect(MockMvcResultMatchers.jsonPath("responsableEconomico").value(rolProyecto.getResponsableEconomico()))
        .andExpect(MockMvcResultMatchers.jsonPath("equipo").value(rolProyecto.getEquipo().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("colectivoRef").value(rolProyecto.getColectivoRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(rolProyectoExistente.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RPRO-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: a RolProyecto with non existing id
    RolProyecto rolProyecto = generarMockRolProyecto(1L);

    BDDMockito.willThrow(new RolProyectoNotFoundException(rolProyecto.getId())).given(service)
        .findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<RolProyecto>any()))
        .willThrow(new RolProyectoNotFoundException(rolProyecto.getId()));

    // when: update RolProyecto
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, rolProyecto.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(rolProyecto)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RPRO-X" })
  public void enable_WithExistingId_Return204() throws Exception {
    // given: existing id
    RolProyecto rolProyecto = generarMockRolProyecto(1L, Boolean.FALSE);

    BDDMockito.given(service.enable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      RolProyecto rolProyectoDisabled = new RolProyecto();
      BeanUtils.copyProperties(rolProyecto, rolProyectoDisabled);
      rolProyectoDisabled.setActivo(Boolean.TRUE);
      return rolProyectoDisabled;
    });

    // when: enable by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, rolProyecto.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: rolProyecto is enabled
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(rolProyecto.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(Boolean.TRUE));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RPRO-X" })
  public void enable_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new RolProyectoNotFoundException(id)).given(service).enable(ArgumentMatchers.<Long>any());

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
  @WithMockUser(username = "user", authorities = { "CSP-RPRO-B" })
  public void disable_WithExistingId_Return204() throws Exception {
    // given: existing id
    RolProyecto rolProyecto = generarMockRolProyecto(1L);

    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      RolProyecto rolProyectoDisabled = new RolProyecto();
      BeanUtils.copyProperties(rolProyecto, rolProyectoDisabled);
      rolProyectoDisabled.setActivo(Boolean.FALSE);
      return rolProyectoDisabled;
    });

    // when: desactivar by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, rolProyecto.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: rolProyecto is disabled
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(rolProyecto.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(Boolean.FALSE));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RPRO-B" })
  public void disable_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new RolProyectoNotFoundException(id)).given(service).disable(ArgumentMatchers.<Long>any());

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
  @WithMockUser(username = "user", authorities = { "CSP-RPRO-V" })
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
  @WithMockUser(username = "user", authorities = { "CSP-RPRO-V" })
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
  @WithMockUser(username = "user", authorities = { "CSP-RPRO-V" })
  public void findById_WithExistingId_ReturnsRolProyecto() throws Exception {
    // given: existing id
    RolProyecto rolProyectoExistente = generarMockRolProyecto(1L);
    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any())).willReturn(rolProyectoExistente);

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested RolProyecto is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RPRO-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new RolProyectoNotFoundException(1L);
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
  @WithMockUser(username = "user", authorities = { "CSP-RPRO-V" })
  public void findAll_WithPaging_ReturnsRolProyectoSubList() throws Exception {
    // given: One hundred RolProyecto
    List<RolProyecto> rolProyectos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {

      if (i % 2 == 0) {
        rolProyectos.add(generarMockRolProyecto(Long.valueOf(i)));
      }
    }

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RolProyecto>>() {
          @Override
          public Page<RolProyecto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<RolProyecto> content = rolProyectos.subList(fromIndex, toIndex);
            Page<RolProyecto> page = new PageImpl<>(content, pageable, rolProyectos.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked RolProyecto are returned with the right page information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "50"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<RolProyecto> actual = mapper.readValue(requestResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
        new TypeReference<List<RolProyecto>>() {
        });

    // containing Codigo='codigo-62' to 'codigo-80'
    for (int i = 0, j = 62; i < 10; i++, j += 2) {
      RolProyecto item = actual.get(i);
      Assertions.assertThat(item.getAbreviatura()).isEqualTo(String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo(Boolean.TRUE);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RPRO-V" })
  public void findAll_EmptyList_Returns204() throws Exception {
    // given: no data RolProyecto
    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RolProyecto>>() {
          @Override
          public Page<RolProyecto> answer(InvocationOnMock invocation) throws Throwable {
            Page<RolProyecto> page = new PageImpl<>(Collections.emptyList());
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
  public void findAllTodos_WithPaging_ReturnsRolProyectoSubList() throws Exception {
    // given: One hundred RolProyecto
    List<RolProyecto> rolProyectos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      rolProyectos.add(generarMockRolProyecto(Long.valueOf(i), (i % 2 == 0) ? Boolean.TRUE : Boolean.FALSE));
    }

    BDDMockito.given(service.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RolProyecto>>() {
          @Override
          public Page<RolProyecto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<RolProyecto> content = rolProyectos.subList(fromIndex, toIndex);
            Page<RolProyecto> page = new PageImpl<>(content, pageable, rolProyectos.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_TODOS)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked RolProyecto are returned with the right page information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<RolProyecto> actual = mapper.readValue(requestResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
        new TypeReference<List<RolProyecto>>() {
        });

    // containing Codigo='codigo-31' to 'codigo-40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      RolProyecto item = actual.get(i);
      Assertions.assertThat(item.getAbreviatura()).isEqualTo(String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo((j % 2 == 0 ? Boolean.TRUE : Boolean.FALSE));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-RPRO-V" })
  public void findAllTodos_EmptyList_Returns204() throws Exception {
    // given: no data RolProyecto
    BDDMockito.given(service.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RolProyecto>>() {
          @Override
          public Page<RolProyecto> answer(InvocationOnMock invocation) throws Throwable {
            Page<RolProyecto> page = new PageImpl<>(Collections.emptyList());
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
   * Función que genera RolProyecto
   * 
   * @param rolProyectoId
   * @return el rolProyecto
   */
  private RolProyecto generarMockRolProyecto(Long rolProyectoId) {

    String suffix = String.format("%03d", rolProyectoId);

    RolProyecto rolProyecto = RolProyecto.builder()//
        .id(rolProyectoId)//
        .abreviatura(suffix)//
        .nombre("nombre-" + suffix)//
        .descripcion("descripcion-" + suffix)//
        .rolPrincipal(Boolean.FALSE)//
        .responsableEconomico(Boolean.FALSE)//
        .equipo(RolProyecto.Equipo.INVESTIGACION)//
        .colectivoRef("PDI")//
        .activo(Boolean.TRUE)//
        .build();

    return rolProyecto;
  }

  /**
   * Función que genera RolProyecto con el estado indicado
   * 
   * @param rolProyectoId
   * @param activo
   * @return el rolProyecto
   */
  private RolProyecto generarMockRolProyecto(Long rolProyectoId, Boolean activo) {

    RolProyecto rolProyecto = generarMockRolProyecto(rolProyectoId);
    rolProyecto.setActivo(activo);

    return rolProyecto;
  }

}