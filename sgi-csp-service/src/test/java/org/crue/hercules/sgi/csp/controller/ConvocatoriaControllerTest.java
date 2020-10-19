package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.SecurityConfig;
import org.crue.hercules.sgi.csp.model.AreaTematicaArbol;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.service.ConvocatoriaAreaTematicaService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEnlaceService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadGestoraService;
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
 * ConvocatoriaControllerTest
 */
@WebMvcTest(ConvocatoriaController.class)
@Import(SecurityConfig.class)
public class ConvocatoriaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private ConvocatoriaEntidadGestoraService convocatoriaEntidadGestoraService;
  @MockBean
  private ConvocatoriaAreaTematicaService convocatoriaAreaTematicaService;
  @MockBean
  private ConvocatoriaEnlaceService convocatoriaEnlaceService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_ENTIDAD_GESTORA = "/convocatoriaentidadgestoras";
  private static final String PATH_AREA_TEMATICA = "/convocatoriaareatematicas";
  private static final String CONTROLLER_BASE_PATH = "/convocatorias";
  private static final String PATH_ENTIDAD_ENLACES = "/convocatoriaenlaces";

  /**
   * 
   * CONVOCATORIA ENLACE
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CENL-V" })
  public void findAllConvocatoriaEnlace_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConvocatoriaEnlace para la Convocatoria
    Long convocatoriaId = 1L;

    List<ConvocatoriaEnlace> convocatoriaEnlaces = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriaEnlaces.add(generarMockConvocatoriaEnlace(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaEnlaceService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaEnlace>>() {
          @Override
          public Page<ConvocatoriaEnlace> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > convocatoriaEnlaces.size() ? convocatoriaEnlaces.size() : toIndex;
            List<ConvocatoriaEnlace> content = convocatoriaEnlaces.subList(fromIndex, toIndex);
            Page<ConvocatoriaEnlace> page = new PageImpl<>(content, pageable, convocatoriaEnlaces.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_ENLACES, convocatoriaId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page)
                .header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve la pagina 3 con los ConvocatoriaEnlace del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ConvocatoriaEnlace> convocatoriaEnlaceResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<ConvocatoriaEnlace>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConvocatoriaEnlace convocatoriaEnlace = convocatoriaEnlaceResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(convocatoriaEnlace.getDescripcion()).isEqualTo("descripcion-" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CENL-V" })
  public void findAllConvocatoriaEnlace_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ConvocatoriaEnlace para la Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaEnlace> convocatoriaEnlaces = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaEnlaceService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaEnlace>>() {
          @Override
          public Page<ConvocatoriaEnlace> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ConvocatoriaEnlace> page = new PageImpl<>(convocatoriaEnlaces, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_ENLACES, convocatoriaId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page)
                .header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * CONVOCATORIA ENTIDAD GESTORA
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CENTGES-V" })
  public void findAllConvocatoriaEntidadGestora_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConvocatoriaEntidadGestora para la Convocatoria
    Long convocatoriaId = 1L;

    List<ConvocatoriaEntidadGestora> convocatoriasEntidadesGestoras = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasEntidadesGestoras.add(generarConvocatoriaEntidadGestora(i, convocatoriaId, "entidad-" + i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaEntidadGestoraService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaEntidadGestora>>() {
          @Override
          public Page<ConvocatoriaEntidadGestora> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > convocatoriasEntidadesGestoras.size() ? convocatoriasEntidadesGestoras.size() : toIndex;
            List<ConvocatoriaEntidadGestora> content = convocatoriasEntidadesGestoras.subList(fromIndex, toIndex);
            Page<ConvocatoriaEntidadGestora> page = new PageImpl<>(content, pageable,
                convocatoriasEntidadesGestoras.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_GESTORA, convocatoriaId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page)
                .header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve la pagina 3 con los ConvocatoriaEntidadGestora del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ConvocatoriaEntidadGestora> convocatoriaEntidadGestoraResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<ConvocatoriaEntidadGestora>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConvocatoriaEntidadGestora convocatoriaEntidadGestora = convocatoriaEntidadGestoraResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(convocatoriaEntidadGestora.getEntidadRef()).isEqualTo("entidad-" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CENTGES-V" })
  public void findAllConvocatoriaEntidadGestora_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ConvocatoriaEntidadGestora para la Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaEntidadGestora> convocatoriasEntidadesGestoras = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaEntidadGestoraService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaEntidadGestora>>() {
          @Override
          public Page<ConvocatoriaEntidadGestora> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ConvocatoriaEntidadGestora> page = new PageImpl<>(convocatoriasEntidadesGestoras, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_GESTORA, convocatoriaId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page)
                .header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto ConvocatoriaEnlace
   * 
   * @param id     id del ConvocatoriaEnlace
   * @param nombre nombre del ConvocatoriaEnlace
   * @return el objeto ConvocatoriaEnlace
   */
  private ConvocatoriaEnlace generarMockConvocatoriaEnlace(Long id) {

    ConvocatoriaEnlace convocatoriaEnlace = new ConvocatoriaEnlace();
    convocatoriaEnlace.setId(id);
    convocatoriaEnlace
        .setConvocatoria(Convocatoria.builder().id(id).activo(Boolean.TRUE).codigo("codigo" + id).build());
    convocatoriaEnlace.setDescripcion("descripcion-" + id);
    convocatoriaEnlace.setUrl("www.url" + id + ".es");
    convocatoriaEnlace.setTipoEnlace(TipoEnlace.builder().id(id).nombre("tipoEnlace" + id)
        .descripcion("descripcionEnlace" + id).activo(Boolean.TRUE).build());

    return convocatoriaEnlace;
  }

  /**
   * Función que devuelve un objeto ConvocatoriaEntidadGestora
   * 
   * @param convocatoriaEntidadGestoraId
   * @param convocatoriaId
   * @param entidadRef
   * @return el objeto ConvocatoriaEntidadGestora
   */
  private ConvocatoriaEntidadGestora generarConvocatoriaEntidadGestora(Long convocatoriaEntidadGestoraId,
      Long convocatoriaId, String entidadRef) {

    return ConvocatoriaEntidadGestora.builder().id(convocatoriaEntidadGestoraId)
        .convocatoria(Convocatoria.builder().id(convocatoriaId).build()).entidadRef(entidadRef).build();

  }

  /**
   * 
   * CONVOCATORIA AREA TEMATICA
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-V" })
  public void findAllConvocatoriaAreaTematica_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConvocatoriaAreaTematica para la Convocatoria
    Long convocatoriaId = 1L;

    List<ConvocatoriaAreaTematica> convocatoriasEntidadesGestoras = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasEntidadesGestoras.add(generarConvocatoriaAreaTematica(i, convocatoriaId, i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaAreaTematicaService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaAreaTematica>>() {
          @Override
          public Page<ConvocatoriaAreaTematica> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > convocatoriasEntidadesGestoras.size() ? convocatoriasEntidadesGestoras.size() : toIndex;
            List<ConvocatoriaAreaTematica> content = convocatoriasEntidadesGestoras.subList(fromIndex, toIndex);
            Page<ConvocatoriaAreaTematica> page = new PageImpl<>(content, pageable,
                convocatoriasEntidadesGestoras.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_AREA_TEMATICA, convocatoriaId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page)
                .header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve la pagina 3 con los ConvocatoriaAreaTematica del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ConvocatoriaAreaTematica> convocatoriaAreaTematicaResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<ConvocatoriaAreaTematica>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConvocatoriaAreaTematica convocatoriaAreaTematica = convocatoriaAreaTematicaResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(convocatoriaAreaTematica.getObservaciones()).isEqualTo("observaciones-" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-V" })
  public void findAllConvocatoriaAreaTematica_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ConvocatoriaAreaTematica para la Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaAreaTematica> convocatoriasEntidadesGestoras = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaAreaTematicaService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaAreaTematica>>() {
          @Override
          public Page<ConvocatoriaAreaTematica> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ConvocatoriaAreaTematica> page = new PageImpl<>(convocatoriasEntidadesGestoras, pageable, 0);
            return page;
          }
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_AREA_TEMATICA, convocatoriaId)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page)
                .header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto ConvocatoriaAreaTematica
   * 
   * @param convocatoriaAreaTematicaId
   * @param convocatoriaId
   * @param areaTematicaId
   * @return el objeto ConvocatoriaAreaTematica
   */
  private ConvocatoriaAreaTematica generarConvocatoriaAreaTematica(Long convocatoriaAreaTematicaId, Long convocatoriaId,
      Long areaTematicaId) {

    return ConvocatoriaAreaTematica.builder().id(convocatoriaAreaTematicaId)
        .convocatoria(Convocatoria.builder().id(convocatoriaId).build())
        .areaTematicaArbol(AreaTematicaArbol.builder().id(areaTematicaId).build())
        .observaciones("observaciones-" + convocatoriaAreaTematicaId).build();
  }

}