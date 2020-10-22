package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.SecurityConfig;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVNEnum;
import org.crue.hercules.sgi.csp.enums.TipoDestinatarioEnum;
import org.crue.hercules.sgi.csp.enums.TipoEstadoConvocatoriaEnum;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.AreaTematicaArbol;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.service.ConvocatoriaAreaTematicaService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEnlaceService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadConvocanteService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadFinanciadoraService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadGestoraService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
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
  private ConvocatoriaService service;
  @MockBean
  private ConvocatoriaEntidadFinanciadoraService convocatoriaEntidadFinanciadoraService;

  @MockBean
  private ConvocatoriaEntidadGestoraService convocatoriaEntidadGestoraService;

  @MockBean
  private ConvocatoriaAreaTematicaService convocatoriaAreaTematicaService;
  @MockBean
  private ConvocatoriaEnlaceService convocatoriaEnlaceService;
  @MockBean
  private ConvocatoriaEntidadConvocanteService convocatoriaEntidadConvocanteService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_REGISTRAR = "/registrar";
  private static final String PATH_PARAMETER_TODOS = "/todos";
  private static final String CONTROLLER_BASE_PATH = "/convocatorias";
  private static final String PATH_AREA_TEMATICA = "/convocatoriaareatematicas";
  private static final String PATH_ENTIDAD_ENLACES = "/convocatoriaenlaces";
  private static final String PATH_ENTIDAD_CONVOCANTE = "/convocatoriaentidadconvocantes";
  private static final String PATH_ENTIDAD_FINANCIADORA = "/convocatoriaentidadfinanciadoras";
  private static final String PATH_ENTIDAD_GESTORA = "/convocatoriaentidadgestoras";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CONV-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a Convocatoria with id filled
    Convocatoria newConvocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);

    BDDMockito.given(service.create(ArgumentMatchers.<Convocatoria>any())).willThrow(new IllegalArgumentException());

    // when: create Convocatoria
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(newConvocatoria)))
        .andDo(MockMvcResultHandlers.print())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CONV-E" })
  public void update_WithExistingId_ReturnsConvocatoria() throws Exception {
    // given: existing Convocatoria
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setTitulo("titulo-modificado");
    convocatoria.setObservaciones("observaciones-modificadas");

    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any())).willReturn(convocatoriaExistente);
    BDDMockito.given(service.update(ArgumentMatchers.<Convocatoria>any())).willAnswer(new Answer<Convocatoria>() {
      @Override
      public Convocatoria answer(InvocationOnMock invocation) throws Throwable {
        Convocatoria givenData = invocation.getArgument(0, Convocatoria.class);
        return givenData;
      }
    });

    // when: update Convocatoria
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoriaExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoria)))
        .andDo(MockMvcResultHandlers.print())
        // then: Convocatoria is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(convocatoriaExistente.getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("unidadGestionRef").value(convocatoriaExistente.getUnidadGestionRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("modeloEjecucion.id")
            .value(convocatoriaExistente.getModeloEjecucion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("codigo").value(convocatoriaExistente.getCodigo()))
        .andExpect(MockMvcResultMatchers.jsonPath("anio").value(convocatoriaExistente.getAnio()))
        .andExpect(MockMvcResultMatchers.jsonPath("titulo").value(convocatoria.getTitulo()))
        .andExpect(MockMvcResultMatchers.jsonPath("objeto").value(convocatoriaExistente.getObjeto()))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value(convocatoria.getObservaciones()))
        .andExpect(MockMvcResultMatchers.jsonPath("finalidad.id").value(convocatoriaExistente.getFinalidad().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("regimenConcurrencia.id")
            .value(convocatoriaExistente.getRegimenConcurrencia().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("destinatarios").value(TipoDestinatarioEnum.INDIVIDUAL.getValue()))
        .andExpect(MockMvcResultMatchers.jsonPath("colaborativos").value(convocatoriaExistente.getColaborativos()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("estadoActual").value(TipoEstadoConvocatoriaEnum.REGISTRADA.getValue()))
        .andExpect(MockMvcResultMatchers.jsonPath("duracion").value(convocatoriaExistente.getDuracion()))
        .andExpect(MockMvcResultMatchers.jsonPath("ambitoGeografico.id")
            .value(convocatoriaExistente.getAmbitoGeografico().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("clasificacionCVN").value(ClasificacionCVNEnum.AYUDAS.getValue()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(convocatoriaExistente.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CONV-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: a Convocatoria with non existing id
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);

    BDDMockito.willThrow(new ConvocatoriaNotFoundException(convocatoria.getId())).given(service)
        .findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<Convocatoria>any()))
        .willThrow(new ConvocatoriaNotFoundException(convocatoria.getId()));

    // when: update Convocatoria
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoria.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoria)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CONV-E" })
  public void registrar_WithEstadoBorradorAnddExistingId_ReturnsConvocatoria() throws Exception {
    // given: existing Convocatoria with estado Borrador
    Convocatoria convocatoriaBorradorExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoriaBorradorExistente.setEstadoActual(TipoEstadoConvocatoriaEnum.BORRADOR);
    Convocatoria convocatoriaRegistrada = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);

    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any())).willReturn(convocatoriaBorradorExistente);
    BDDMockito.given(service.registrar(ArgumentMatchers.<Long>any())).willReturn(convocatoriaRegistrada);

    // when: registrar Convocatoria
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REGISTRAR,
                convocatoriaBorradorExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaRegistrada)))
        .andDo(MockMvcResultHandlers.print())
        // then: Convocatoria estado is Registrada
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(convocatoriaBorradorExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("unidadGestionRef")
            .value(convocatoriaBorradorExistente.getUnidadGestionRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("modeloEjecucion.id")
            .value(convocatoriaBorradorExistente.getModeloEjecucion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("codigo").value(convocatoriaBorradorExistente.getCodigo()))
        .andExpect(MockMvcResultMatchers.jsonPath("anio").value(convocatoriaBorradorExistente.getAnio()))
        .andExpect(MockMvcResultMatchers.jsonPath("titulo").value(convocatoriaBorradorExistente.getTitulo()))
        .andExpect(MockMvcResultMatchers.jsonPath("objeto").value(convocatoriaBorradorExistente.getObjeto()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("observaciones").value(convocatoriaBorradorExistente.getObservaciones()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("finalidad.id").value(convocatoriaBorradorExistente.getFinalidad().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("regimenConcurrencia.id")
            .value(convocatoriaBorradorExistente.getRegimenConcurrencia().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("destinatarios").value(TipoDestinatarioEnum.INDIVIDUAL.getValue()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("colaborativos").value(convocatoriaBorradorExistente.getColaborativos()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("estadoActual").value(TipoEstadoConvocatoriaEnum.REGISTRADA.getValue()))
        .andExpect(MockMvcResultMatchers.jsonPath("duracion").value(convocatoriaBorradorExistente.getDuracion()))
        .andExpect(MockMvcResultMatchers.jsonPath("ambitoGeografico.id")
            .value(convocatoriaBorradorExistente.getAmbitoGeografico().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("clasificacionCVN").value(ClasificacionCVNEnum.AYUDAS.getValue()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(convocatoriaBorradorExistente.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CONV-E" })
  public void registrar_WithEstadoBorradorAndNoExistingId_Returns404() throws Exception {
    // given: a Convocatoria with non existing id
    Convocatoria convocatoriaBorradorExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoriaBorradorExistente.setEstadoActual(TipoEstadoConvocatoriaEnum.BORRADOR);

    BDDMockito.willThrow(new ConvocatoriaNotFoundException(convocatoriaBorradorExistente.getId())).given(service)
        .findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.registrar(ArgumentMatchers.anyLong()))
        .willThrow(new ConvocatoriaNotFoundException(convocatoriaBorradorExistente.getId()));

    // when: update Convocatoria
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REGISTRAR,
                convocatoriaBorradorExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaBorradorExistente)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CONV-B" })
  public void delete_WithExistingId_Return204() throws Exception {
    // given: existing id
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);

    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willReturn(convocatoria);

    // when: delete by id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoria.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CONV-B" })
  public void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ConvocatoriaNotFoundException(id)).given(service).findById(ArgumentMatchers.<Long>any());
    BDDMockito.willThrow(new ConvocatoriaNotFoundException(id)).given(service).disable(ArgumentMatchers.<Long>any());

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
  @WithMockUser(username = "user", authorities = { "CSP-CONV-V" })
  public void findById_WithExistingId_ReturnsConvocatoria() throws Exception {
    // given: existing id
    Convocatoria convocatoriaExistente = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any())).willReturn(convocatoriaExistente);

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested Convocatoria is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CONV-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ConvocatoriaNotFoundException(1L);
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
  @WithMockUser(username = "user", authorities = { "CSP-CONV-V" })
  public void findAll_WithPaging_ReturnsConvocatoriaSubList() throws Exception {
    // given: One hundred Convocatoria
    List<Convocatoria> convocatorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {

      if (i % 2 == 0) {
        convocatorias.add(generarMockConvocatoria(Long.valueOf(i), 1L, 1L, 1L, 1L, 1L, Boolean.TRUE));
      }
    }

    BDDMockito.given(service.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Convocatoria>>() {
          @Override
          public Page<Convocatoria> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Convocatoria> content = convocatorias.subList(fromIndex, toIndex);
            Page<Convocatoria> page = new PageImpl<>(content, pageable, convocatorias.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked Convocatoria are returned with the right page information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "50"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Convocatoria> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Convocatoria>>() {
        });

    // containing Codigo='codigo-62' to 'codigo-80'
    for (int i = 0, j = 62; i < 10; i++, j += 2) {
      Convocatoria item = actual.get(i);
      Assertions.assertThat(item.getCodigo()).isEqualTo("codigo-" + String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo(Boolean.TRUE);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "SYSADMIN" })
  public void findAllTodos_WithPaging_ReturnsConvocatoriaSubList() throws Exception {
    // given: One hundred Convocatoria
    List<Convocatoria> convocatorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      convocatorias.add(
          generarMockConvocatoria(Long.valueOf(i), 1L, 1L, 1L, 1L, 1L, (i % 2 == 0) ? Boolean.TRUE : Boolean.FALSE));
    }

    BDDMockito
        .given(service.findAllTodos(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Convocatoria>>() {
          @Override
          public Page<Convocatoria> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Convocatoria> content = convocatorias.subList(fromIndex, toIndex);
            Page<Convocatoria> page = new PageImpl<>(content, pageable, convocatorias.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_TODOS)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked Convocatoria are returned with the right page information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Convocatoria> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Convocatoria>>() {
        });

    // containing Codigo='codigo-31' to 'codigo-40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Convocatoria item = actual.get(i);
      Assertions.assertThat(item.getCodigo()).isEqualTo("codigo-" + String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo((j % 2 == 0 ? Boolean.TRUE : Boolean.FALSE));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CONV-V" })
  public void findAll_EmptyList_Returns204() throws Exception {
    // given: no data Convocatoria
    BDDMockito.given(service.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Convocatoria>>() {
          @Override
          public Page<Convocatoria> answer(InvocationOnMock invocation) throws Throwable {
            Page<Convocatoria> page = new PageImpl<>(Collections.emptyList());
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
   * 
   * CONVOCATORIA AREA TEMATICA
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-V" })
  public void findAllConvocatoriaAreaTematica_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConvocatoriaAreaTematica para la Convocatoria
    Long convocatoriaId = 1L;

    List<ConvocatoriaAreaTematica> convocatoriasAreasTematicas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasAreasTematicas.add(generarConvocatoriaAreaTematica(i, convocatoriaId, i));
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
            toIndex = toIndex > convocatoriasAreasTematicas.size() ? convocatoriasAreasTematicas.size() : toIndex;
            List<ConvocatoriaAreaTematica> content = convocatoriasAreasTematicas.subList(fromIndex, toIndex);
            Page<ConvocatoriaAreaTematica> page = new PageImpl<>(content, pageable, convocatoriasAreasTematicas.size());
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
    List<ConvocatoriaAreaTematica> convocatoriasAreasTematicas = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaAreaTematicaService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaAreaTematica>>() {
          @Override
          public Page<ConvocatoriaAreaTematica> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(2, Pageable.class);
            Page<ConvocatoriaAreaTematica> page = new PageImpl<>(convocatoriasAreasTematicas, pageable, 0);
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
   * 
   * CONVOCATORIA ENTIDAD FINANCIADORA
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CENTGES-V" })
  public void findAllConvocatoriaEntidadFinanciadora_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConvocatoriaEntidadFinanciadora para la Convocatoria
    Long convocatoriaId = 1L;

    List<ConvocatoriaEntidadFinanciadora> convocatoriaEntidadFinanciadoras = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriaEntidadFinanciadoras.add(generarMockConvocatoriaEntidadFinanciadora(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaEntidadFinanciadoraService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > convocatoriaEntidadFinanciadoras.size() ? convocatoriaEntidadFinanciadoras.size()
              : toIndex;
          List<ConvocatoriaEntidadFinanciadora> content = convocatoriaEntidadFinanciadoras.subList(fromIndex, toIndex);
          Page<ConvocatoriaEntidadFinanciadora> pageResponse = new PageImpl<>(content, pageable,
              convocatoriaEntidadFinanciadoras.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_FINANCIADORA, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve la pagina 3 con los ConvocatoriaEntidadFinanciadora del 31 al
        // 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ConvocatoriaEntidadFinanciadora> convocatoriaEntidadFinanciadorasResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<ConvocatoriaEntidadFinanciadora>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = convocatoriaEntidadFinanciadorasResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(convocatoriaEntidadFinanciadora.getEntidadRef()).isEqualTo("entidad-" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CENTGES-V" })
  public void findAllConvocatoriaEntidadFinanciadora_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ConvocatoriaEntidadFinanciadora para la
    // Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaEntidadFinanciadora> convocatoriaEntidadFinanciadoras = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaEntidadFinanciadoraService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          Page<ConvocatoriaEntidadFinanciadora> pageResponse = new PageImpl<>(convocatoriaEntidadFinanciadoras,
              pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_FINANCIADORA, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * CONVOCATORIA ENTIDAD ENLACE
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
   * CONVOCATORIA ENTIDAD CONVOCANTE
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CENTGES-V" })
  public void findAllConvocatoriaEntidadConvocantes_ReturnsPage() throws Exception {
    // given: Una lista con 37 ConvocatoriaEntidadConvocante para la Convocatoria
    Long convocatoriaId = 1L;

    List<ConvocatoriaEntidadConvocante> convocatoriasEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasEntidadesConvocantes.add(generarMockConvocatoriaEntidadConvocante(i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaEntidadConvocanteService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > convocatoriasEntidadesConvocantes.size() ? convocatoriasEntidadesConvocantes.size()
              : toIndex;
          List<ConvocatoriaEntidadConvocante> content = convocatoriasEntidadesConvocantes.subList(fromIndex, toIndex);
          Page<ConvocatoriaEntidadConvocante> pageResponse = new PageImpl<>(content, pageable,
              convocatoriasEntidadesConvocantes.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_CONVOCANTE, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve la pagina 3 con los ConvocatoriaEntidadConvocante del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<ConvocatoriaEntidadConvocante> convocatoriaEntidadConvocantesResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<ConvocatoriaEntidadConvocante>>() {
        });

    for (int i = 31; i <= 37; i++) {
      ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = convocatoriaEntidadConvocantesResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(convocatoriaEntidadConvocante.getEntidadRef()).isEqualTo("entidad-" + i);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CENTGES-V" })
  public void findAllConvocatoriaEntidadConvocantes_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de ConvocatoriaEntidadConvocante para la Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaEntidadConvocante> convocatoriasEntidadesConvocantes = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(convocatoriaEntidadConvocanteService.findAllByConvocatoria(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          Page<ConvocatoriaEntidadConvocante> pageResponse = new PageImpl<>(convocatoriasEntidadesConvocantes, pageable,
              0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ENTIDAD_CONVOCANTE, convocatoriaId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CONV-C" })
  public void create_ReturnsConvocatoria() throws Exception {
    // given: new Convocatoria
    Convocatoria newConvocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);

    BDDMockito.given(service.create(ArgumentMatchers.<Convocatoria>any())).willAnswer(new Answer<Convocatoria>() {
      @Override
      public Convocatoria answer(InvocationOnMock invocation) throws Throwable {
        Convocatoria givenData = invocation.getArgument(0, Convocatoria.class);
        Convocatoria newData = new Convocatoria();
        BeanUtils.copyProperties(givenData, newData);
        newData.setId(newConvocatoria.getId());
        return newData;
      }
    });

    // when: create Convocatoria
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(newConvocatoria)))
        .andDo(MockMvcResultHandlers.print())
        // then: new Convocatoria is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("unidadGestionRef").value(newConvocatoria.getUnidadGestionRef()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("modeloEjecucion.id").value(newConvocatoria.getModeloEjecucion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("codigo").value(newConvocatoria.getCodigo()))
        .andExpect(MockMvcResultMatchers.jsonPath("anio").value(newConvocatoria.getAnio()))
        .andExpect(MockMvcResultMatchers.jsonPath("titulo").value(newConvocatoria.getTitulo()))
        .andExpect(MockMvcResultMatchers.jsonPath("objeto").value(newConvocatoria.getObjeto()))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value(newConvocatoria.getObservaciones()))
        .andExpect(MockMvcResultMatchers.jsonPath("finalidad.id").value(newConvocatoria.getFinalidad().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("regimenConcurrencia.id")
            .value(newConvocatoria.getRegimenConcurrencia().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("destinatarios").value(TipoDestinatarioEnum.INDIVIDUAL.getValue()))
        .andExpect(MockMvcResultMatchers.jsonPath("colaborativos").value(newConvocatoria.getColaborativos()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("estadoActual").value(TipoEstadoConvocatoriaEnum.REGISTRADA.getValue()))
        .andExpect(MockMvcResultMatchers.jsonPath("duracion").value(newConvocatoria.getDuracion()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("ambitoGeografico.id").value(newConvocatoria.getAmbitoGeografico().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("clasificacionCVN").value(ClasificacionCVNEnum.AYUDAS.getValue()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(newConvocatoria.getActivo()));
  }

  /**
   * 
   * MOCKS
   * 
   */

  /**
   * Función que genera Convocatoria
   * 
   * @param convocatoriaId
   * @param unidadGestionId
   * @param modeloEjecucionId
   * @param modeloTipoFinalidadId
   * @param tipoRegimenConcurrenciaId
   * @param tipoAmbitoGeogragicoId
   * @param activo
   * @return la convocatoria
   */
  private Convocatoria generarMockConvocatoria(Long convocatoriaId, Long unidadGestionId, Long modeloEjecucionId,
      Long modeloTipoFinalidadId, Long tipoRegimenConcurrenciaId, Long tipoAmbitoGeogragicoId, Boolean activo) {

    ModeloEjecucion modeloEjecucion = (modeloEjecucionId == null) ? null
        : ModeloEjecucion.builder()//
            .id(modeloEjecucionId)//
            .nombre("nombreModeloEjecucion-" + String.format("%03d", modeloEjecucionId))//
            .activo(Boolean.TRUE)//
            .build();

    TipoFinalidad tipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : TipoFinalidad.builder()//
            .id(modeloTipoFinalidadId)//
            .nombre("nombreTipoFinalidad-" + String.format("%03d", modeloTipoFinalidadId))//
            .activo(Boolean.TRUE)//
            .build();

    ModeloTipoFinalidad modeloTipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : ModeloTipoFinalidad.builder()//
            .id(modeloTipoFinalidadId)//
            .modeloEjecucion(modeloEjecucion)//
            .tipoFinalidad(tipoFinalidad)//
            .activo(Boolean.TRUE)//
            .build();

    TipoRegimenConcurrencia tipoRegimenConcurrencia = (tipoRegimenConcurrenciaId == null) ? null
        : TipoRegimenConcurrencia.builder()//
            .id(tipoRegimenConcurrenciaId)//
            .nombre("nombreTipoRegimenConcurrencia-" + String.format("%03d", tipoRegimenConcurrenciaId))//
            .activo(Boolean.TRUE)//
            .build();

    TipoAmbitoGeografico tipoAmbitoGeografico = (tipoAmbitoGeogragicoId == null) ? null
        : TipoAmbitoGeografico.builder()//
            .id(tipoAmbitoGeogragicoId)//
            .nombre("nombreTipoAmbitoGeografico-" + String.format("%03d", tipoAmbitoGeogragicoId))//
            .activo(Boolean.TRUE)//
            .build();

    Convocatoria convocatoria = Convocatoria.builder()//
        .id(convocatoriaId)//
        .unidadGestionRef((unidadGestionId == null) ? null : "unidad-" + String.format("%03d", unidadGestionId))//
        .modeloEjecucion(modeloEjecucion)//
        .codigo("codigo-" + String.format("%03d", convocatoriaId))//
        .anio(2020)//
        .titulo("titulo-" + String.format("%03d", convocatoriaId))//
        .objeto("objeto-" + String.format("%03d", convocatoriaId))//
        .observaciones("observaciones-" + String.format("%03d", convocatoriaId))//
        .finalidad((modeloTipoFinalidad == null) ? null : modeloTipoFinalidad.getTipoFinalidad())//
        .regimenConcurrencia(tipoRegimenConcurrencia)//
        .destinatarios(TipoDestinatarioEnum.INDIVIDUAL)//
        .colaborativos(Boolean.TRUE)//
        .estadoActual(TipoEstadoConvocatoriaEnum.REGISTRADA)//
        .duracion(12)//
        .ambitoGeografico(tipoAmbitoGeografico)//
        .clasificacionCVN(ClasificacionCVNEnum.AYUDAS)//
        .activo(activo)//
        .build();

    return convocatoria;
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

  /**
   * Función que devuelve un objeto ConvocatoriaEntidadConvocante
   * 
   * @param id id del ConvocatoriaEntidadConvocante
   * @return el objeto ConvocatoriaEntidadConvocante
   */
  private ConvocatoriaEntidadConvocante generarMockConvocatoriaEntidadConvocante(Long id) {
    Convocatoria convocatoria = new Convocatoria();
    convocatoria.setId(id);

    Programa programa = new Programa();
    programa.setId(id);

    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = new ConvocatoriaEntidadConvocante();
    convocatoriaEntidadConvocante.setId(id);
    convocatoriaEntidadConvocante.setConvocatoria(convocatoria);
    convocatoriaEntidadConvocante.setEntidadRef("entidad-" + (id == null ? 0 : id));
    convocatoriaEntidadConvocante.setPrograma(programa);

    return convocatoriaEntidadConvocante;
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
   * Función que devuelve un objeto ConvocatoriaEntidadFinanciadora
   * 
   * @param id id del ConvocatoriaEntidadFinanciadora
   * @return el objeto ConvocatoriaEntidadFinanciadora
   */
  private ConvocatoriaEntidadFinanciadora generarMockConvocatoriaEntidadFinanciadora(Long id) {
    Convocatoria convocatoria = new Convocatoria();
    convocatoria.setId(id == null ? 1 : id);

    FuenteFinanciacion fuenteFinanciacion = new FuenteFinanciacion();
    fuenteFinanciacion.setId(id == null ? 1 : id);
    fuenteFinanciacion.setActivo(true);

    TipoFinanciacion tipoFinanciacion = new TipoFinanciacion();
    tipoFinanciacion.setId(id == null ? 1 : id);
    tipoFinanciacion.setActivo(true);

    Programa programa = new Programa();
    programa.setId(id);

    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = new ConvocatoriaEntidadFinanciadora();
    convocatoriaEntidadFinanciadora.setId(id);
    convocatoriaEntidadFinanciadora.setConvocatoria(convocatoria);
    convocatoriaEntidadFinanciadora.setEntidadRef("entidad-" + (id == null ? 0 : id));
    convocatoriaEntidadFinanciadora.setFuenteFinanciacion(fuenteFinanciacion);
    convocatoriaEntidadFinanciadora.setTipoFinanciacion(tipoFinanciacion);
    convocatoriaEntidadFinanciadora.setPorcentajeFinanciacion(50);

    return convocatoriaEntidadFinanciadora;
  }

}