package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.SecurityConfig;
import org.crue.hercules.sgi.csp.exceptions.ListadoAreaTematicaNotFoundException;
import org.crue.hercules.sgi.csp.model.AreaTematicaArbol;
import org.crue.hercules.sgi.csp.model.ListadoAreaTematica;
import org.crue.hercules.sgi.csp.service.AreaTematicaArbolService;
import org.crue.hercules.sgi.csp.service.ListadoAreaTematicaService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ListadoAreaTematicaControllerTest
 */

@WebMvcTest(ListadoAreaTematicaController.class)
@Import(SecurityConfig.class)
public class ListadoAreaTematicaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private ListadoAreaTematicaService service;

  @MockBean
  private AreaTematicaArbolService areaTematicaArbolService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/listadoareatematicas";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-LATEM-C" })
  public void create_ReturnsListadoAreaTematica() throws Exception {
    // given: new ListadoAreaTematica
    ListadoAreaTematica data = generarMockListadoAreaTematica(null, Boolean.TRUE);

    BDDMockito.given(service.create(ArgumentMatchers.<ListadoAreaTematica>any()))
        .willAnswer(new Answer<ListadoAreaTematica>() {
          @Override
          public ListadoAreaTematica answer(InvocationOnMock invocation) throws Throwable {
            ListadoAreaTematica givenData = invocation.getArgument(0, ListadoAreaTematica.class);
            ListadoAreaTematica newData = new ListadoAreaTematica();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create ListadoAreaTematica
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(data)))
        .andDo(MockMvcResultHandlers.print())
        // then: new ListadoAreaTematica is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(data.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(data.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(data.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-LATEM-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ListadoAreaTematica with id filled
    ListadoAreaTematica data = generarMockListadoAreaTematica(1L, Boolean.TRUE);

    BDDMockito.given(service.create(ArgumentMatchers.<ListadoAreaTematica>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ListadoAreaTematica
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(data)))
        .andDo(MockMvcResultHandlers.print())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-LATEM-E" })
  public void update_WithExistingId_ReturnsListadoAreaTematica() throws Exception {
    // given: existing ListadoAreaTematica
    ListadoAreaTematica data = generarMockListadoAreaTematica(1L, Boolean.TRUE);

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<ListadoAreaTematica>() {
      @Override
      public ListadoAreaTematica answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarMockListadoAreaTematica(id, Boolean.FALSE);
      }
    });
    BDDMockito.given(service.update(ArgumentMatchers.<ListadoAreaTematica>any()))
        .willAnswer(new Answer<ListadoAreaTematica>() {
          @Override
          public ListadoAreaTematica answer(InvocationOnMock invocation) throws Throwable {
            ListadoAreaTematica givenData = invocation.getArgument(0, ListadoAreaTematica.class);
            return givenData;
          }
        });

    // when: update ListadoAreaTematica
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, data.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(data)))
        .andDo(MockMvcResultHandlers.print())
        // then: ListadoAreaTematica is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(data.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(data.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(data.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(data.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-LATEM-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: a ListadoAreaTematica with non existing id
    ListadoAreaTematica data = generarMockListadoAreaTematica(1L, Boolean.TRUE);

    BDDMockito.willThrow(new ListadoAreaTematicaNotFoundException(data.getId())).given(service)
        .findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<ListadoAreaTematica>any()))
        .willThrow(new ListadoAreaTematicaNotFoundException(data.getId()));

    // when: update ListadoAreaTematica
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, data.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(data)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-LATEM-B" })
  public void delete_WithExistingId_Return204() throws Exception {
    // given: existing id
    ListadoAreaTematica data = generarMockListadoAreaTematica(1L, Boolean.TRUE);
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
  @WithMockUser(username = "user", authorities = { "CSP-LATEM-B" })
  public void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ListadoAreaTematicaNotFoundException(id)).given(service)
        .findById(ArgumentMatchers.<Long>any());
    BDDMockito.willThrow(new ListadoAreaTematicaNotFoundException(id)).given(service)
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
  @WithMockUser(username = "user", authorities = { "CSP-LATEM-V" })
  public void findById_WithExistingId_ReturnsListadoAreaTematica() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<ListadoAreaTematica>() {
      @Override
      public ListadoAreaTematica answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarMockListadoAreaTematica(id, Boolean.TRUE);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ListadoAreaTematica is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-LATEM-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ListadoAreaTematicaNotFoundException(1L);
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
  @WithMockUser(username = "user", authorities = { "CSP-LATEM-V" })
  public void findAll_WithPaging_ReturnsListadoAreaTematicaSubList() throws Exception {
    // given: One hundred ListadoAreaTematica
    List<ListadoAreaTematica> data = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      data.add(generarMockListadoAreaTematica(Long.valueOf(i), Boolean.TRUE));
    }

    BDDMockito.given(service.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ListadoAreaTematica>>() {
          @Override
          public Page<ListadoAreaTematica> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<ListadoAreaTematica> content = data.subList(fromIndex, toIndex);
            Page<ListadoAreaTematica> page = new PageImpl<>(content, pageable, data.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked ListadoAreaTematica are returned with the right page
        // information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<ListadoAreaTematica> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ListadoAreaTematica>>() {
        });

    // containing Nombre='Nombre-31' to 'Nombre-40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      ListadoAreaTematica item = actual.get(i);
      Assertions.assertThat(item.getNombre()).isEqualTo("nombre-" + j);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-LATEM-V" })
  public void findAll_EmptyList_Returns204() throws Exception {
    // given: no data ListadoAreaTematica
    BDDMockito.given(service.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ListadoAreaTematica>>() {
          @Override
          public Page<ListadoAreaTematica> answer(InvocationOnMock invocation) throws Throwable {
            Page<ListadoAreaTematica> page = new PageImpl<>(Collections.emptyList());
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
  @WithMockUser(username = "user", authorities = { "CSP-TDOC-V" })
  public void findAllAreaTematicaArboles_ReturnsPage() throws Exception {
    // given: Una lista con 37 AreaTematicaArbol para el ListadoAreaTematica
    Long idListadoAreaTematica = 1L;

    List<AreaTematicaArbol> areaTematicaArboles = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      areaTematicaArboles.add(generarMockAreaTematicaArbol(i, "Programa" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(areaTematicaArbolService.findAllByListadoAreaTematica(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > areaTematicaArboles.size() ? areaTematicaArboles.size() : toIndex;
          List<AreaTematicaArbol> content = areaTematicaArboles.subList(fromIndex, toIndex);
          Page<AreaTematicaArbol> pageResponse = new PageImpl<>(content, pageable, areaTematicaArboles.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/areatematicaarboles", idListadoAreaTematica)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve la pagina 3 con los Programa del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<AreaTematicaArbol> areaTematicaArbolesResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<AreaTematicaArbol>>() {
        });

    for (int i = 31; i <= 37; i++) {
      AreaTematicaArbol areaTematicaArbol = areaTematicaArbolesResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(areaTematicaArbol.getNombre()).isEqualTo("Programa" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TDOC-V" })
  public void findAllAreaTematicaArboles_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de AreaTematicaArbol para el ListadoAreaTematica
    Long idPlan = 1L;

    List<AreaTematicaArbol> areaTematicaArboles = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(areaTematicaArbolService.findAllByListadoAreaTematica(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          Page<AreaTematicaArbol> pageResponse = new PageImpl<>(areaTematicaArboles, pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/areatematicaarboles", idPlan)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());

  }

  /**
   * Función que devuelve un objeto ListadoAreaTematica
   * 
   * @param id
   * @param activo
   * @return ListadoAreaTematica
   */
  private ListadoAreaTematica generarMockListadoAreaTematica(Long id, Boolean activo) {
    return ListadoAreaTematica.builder().id(id).nombre("nombre-" + id).descripcion("descripcion-" + id).activo(activo)
        .build();
  }

  /**
   * Función que devuelve un objeto AreaTematicaArbol
   * 
   * @param id     id del AreaTematicaArbol
   * @param nombre nombre del AreaTematicaArbol
   * @return el objeto AreaTematicaArbol
   */
  private AreaTematicaArbol generarMockAreaTematicaArbol(Long id, String nombre) {
    AreaTematicaArbol areaTematicaArbol = new AreaTematicaArbol();
    areaTematicaArbol.setId(id);
    areaTematicaArbol.setNombre(nombre);
    areaTematicaArbol.setAbreviatura("A-" + id);
    areaTematicaArbol.setActivo(true);

    return areaTematicaArbol;
  }

}
