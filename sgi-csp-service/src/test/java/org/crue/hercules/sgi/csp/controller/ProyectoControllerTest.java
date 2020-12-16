package org.crue.hercules.sgi.csp.controller;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.TipoEstadoProyectoEnum;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.service.ProyectoService;
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
 * ProyectoControllerTest
 */
@WebMvcTest(ProyectoController.class)
public class ProyectoControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/proyectos";
  private static final String PATH_TODOS = "/todos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-C" })
  public void create_ReturnsProyecto() throws Exception {
    // given: new Proyecto
    Proyecto proyecto = generarMockProyecto(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<Proyecto>any(), ArgumentMatchers.<String>anyList()))
        .willAnswer((InvocationOnMock invocation) -> {
          Proyecto newProyecto = new Proyecto();
          BeanUtils.copyProperties(invocation.getArgument(0), newProyecto);
          newProyecto.setId(1L);
          return newProyecto;
        });

    // when: create Proyecto
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyecto)))
        .andDo(MockMvcResultHandlers.print())
        // then: new Proyecto is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("estado.id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value(proyecto.getObservaciones()))
        .andExpect(MockMvcResultMatchers.jsonPath("unidadGestionRef").value(proyecto.getUnidadGestionRef()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a Proyecto with id filled
    Proyecto proyecto = generarMockProyecto(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<Proyecto>any(), ArgumentMatchers.<String>anyList()))
        .willThrow(new IllegalArgumentException());

    // when: create Proyecto
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyecto)))
        .andDo(MockMvcResultHandlers.print())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_ReturnsProyecto() throws Exception {
    // given: Existing Proyecto to be updated
    Proyecto proyectoExistente = generarMockProyecto(1L);
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.setObservaciones("observaciones actualizadas");

    BDDMockito.given(service.update(ArgumentMatchers.<Proyecto>any(), ArgumentMatchers.<String>anyList(),
        ArgumentMatchers.anyBoolean())).willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update Proyecto
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, proyectoExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyecto)))
        .andDo(MockMvcResultHandlers.print())
        // then: Proyecto is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(proyectoExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("estado.id").value(proyectoExistente.getEstado().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value(proyecto.getObservaciones()))
        .andExpect(MockMvcResultMatchers.jsonPath("unidadGestionRef").value(proyectoExistente.getUnidadGestionRef()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    Proyecto proyecto = generarMockProyecto(1L);

    BDDMockito.willThrow(new ProyectoNotFoundException(id)).given(service).update(ArgumentMatchers.<Proyecto>any(),
        ArgumentMatchers.<String>anyList(), ArgumentMatchers.anyBoolean());

    // when: update Proyecto
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyecto)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void reactivar_WithExistingId_ReturnProyecto() throws Exception {
    // given: existing id
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.setActivo(false);

    BDDMockito.given(service.enable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>anyList()))
        .willAnswer((InvocationOnMock invocation) -> {
          Proyecto proyectoDisabled = new Proyecto();
          BeanUtils.copyProperties(proyecto, proyectoDisabled);
          proyectoDisabled.setActivo(true);
          return proyectoDisabled;
        });

    // when: reactivar by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, proyecto.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Proyecto is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void reactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ProyectoNotFoundException(id)).given(service).enable(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>anyList());

    // when: reactivar by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-B" })
  public void desactivar_WithExistingId_ReturnProyecto() throws Exception {
    // given: existing id
    Proyecto proyecto = generarMockProyecto(1L);
    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>anyList()))
        .willAnswer((InvocationOnMock invocation) -> {
          Proyecto proyectoDisabled = new Proyecto();
          BeanUtils.copyProperties(proyecto, proyectoDisabled);
          proyectoDisabled.setActivo(false);
          return proyectoDisabled;
        });

    // when: desactivar by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, proyecto.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Programa is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(false));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-B" })
  public void desactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ProyectoNotFoundException(id)).given(service).disable(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>anyList());

    // when: desactivar by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-V" })
  public void findById_WithExistingId_ReturnsProyecto() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong(), ArgumentMatchers.<String>anyList()))
        .willAnswer((InvocationOnMock invocation) -> {
          return generarMockProyecto(invocation.getArgument(0));
        });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested Proyecto is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("estado.id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value("observaciones-001"))
        .andExpect(MockMvcResultMatchers.jsonPath("unidadGestionRef").value("OPE"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong(), ArgumentMatchers.<String>anyList()))
        .will((InvocationOnMock invocation) -> {
          throw new ProyectoNotFoundException(1L);
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
  @WithMockUser(username = "user", authorities = { "CSP-PRO-V" })
  public void findAll_ReturnsPage() throws Exception {
    // given: Una lista con 37 Proyecto
    List<Proyecto> proyectos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectos.add(generarMockProyecto(i));
    }
    Integer page = 3;
    Integer pageSize = 10;
    BDDMockito
        .given(service.findAllRestringidos(ArgumentMatchers.<List<QueryCriteria>>any(),
            ArgumentMatchers.<Pageable>any(), ArgumentMatchers.<List<String>>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectos.size() ? proyectos.size() : toIndex;
          List<Proyecto> content = proyectos.subList(fromIndex, toIndex);
          Page<Proyecto> pageResponse = new PageImpl<>(content, pageable, proyectos.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve la pagina 3 del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();
    List<Proyecto> proyectosResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Proyecto>>() {
        });
    for (int i = 31; i <= 37; i++) {
      Proyecto proyecto = proyectosResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(proyecto.getObservaciones()).isEqualTo("observaciones-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CONV-V" })
  public void findAll_EmptyList_Returns204() throws Exception {
    // given: no data Proyecto
    BDDMockito
        .given(service.findAllRestringidos(ArgumentMatchers.<List<QueryCriteria>>any(),
            ArgumentMatchers.<Pageable>any(), ArgumentMatchers.<List<String>>any()))
        .willAnswer(new Answer<Page<Proyecto>>() {
          @Override
          public Page<Proyecto> answer(InvocationOnMock invocation) throws Throwable {
            Page<Proyecto> page = new PageImpl<>(Collections.emptyList());
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
  @WithMockUser(username = "user", authorities = { "CSP-TFAS-V" })
  public void findAllTodos_ReturnsPage() throws Exception {
    // given: Una lista con 37 Proyecto
    List<Proyecto> proyectos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectos.add(generarMockProyecto(i));
    }
    Integer page = 3;
    Integer pageSize = 10;
    BDDMockito
        .given(service.findAllTodosRestringidos(ArgumentMatchers.<List<QueryCriteria>>any(),
            ArgumentMatchers.<Pageable>any(), ArgumentMatchers.<List<String>>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectos.size() ? proyectos.size() : toIndex;
          List<Proyecto> content = proyectos.subList(fromIndex, toIndex);
          Page<Proyecto> pageResponse = new PageImpl<>(content, pageable, proyectos.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_TODOS)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve la pagina 3 con los TipoFase del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();
    List<Proyecto> proyectosResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Proyecto>>() {
        });
    for (int i = 31; i <= 37; i++) {
      Proyecto proyecto = proyectosResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(proyecto.getObservaciones()).isEqualTo("observaciones-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CONV-V" })
  public void findAllTodos_EmptyList_Returns204() throws Exception {
    // given: no data Proyecto
    BDDMockito
        .given(service.findAllTodosRestringidos(ArgumentMatchers.<List<QueryCriteria>>any(),
            ArgumentMatchers.<Pageable>any(), ArgumentMatchers.<List<String>>any()))
        .willAnswer(new Answer<Page<Proyecto>>() {
          @Override
          public Page<Proyecto> answer(InvocationOnMock invocation) throws Throwable {
            Page<Proyecto> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_TODOS)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto Proyecto
   * 
   * @param id id del Proyecto
   * @return el objeto Proyecto
   */
  private Proyecto generarMockProyecto(Long id) {
    EstadoProyecto estadoProyecto = generarMockEstadoProyecto(1L);

    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    TipoFinalidad tipoFinalidad = new TipoFinalidad();
    tipoFinalidad.setId(1L);

    TipoAmbitoGeografico tipoAmbitoGeografico = new TipoAmbitoGeografico();
    tipoAmbitoGeografico.setId(1L);

    Proyecto proyecto = new Proyecto();
    proyecto.setId(id);
    proyecto.setTitulo("PRO" + (id != null ? id : 1));
    proyecto.setCodigoExterno("cod-externo-" + (id != null ? String.format("%03d", id) : "001"));
    proyecto.setObservaciones("observaciones-" + String.format("%03d", id));
    proyecto.setUnidadGestionRef("OPE");
    proyecto.setFechaInicio(LocalDate.now());
    proyecto.setFechaFin(LocalDate.now());
    proyecto.setModeloEjecucion(modeloEjecucion);
    proyecto.setFinalidad(tipoFinalidad);
    proyecto.setAmbitoGeografico(tipoAmbitoGeografico);
    proyecto.setConfidencial(Boolean.FALSE);
    proyecto.setActivo(true);

    if (id != null) {
      proyecto.setEstado(estadoProyecto);
    }

    return proyecto;
  }

  /**
   * Función que devuelve un objeto EstadoProyecto
   * 
   * @param id id del EstadoProyecto
   * @return el objeto EstadoProyecto
   */
  private EstadoProyecto generarMockEstadoProyecto(Long id) {
    EstadoProyecto estadoProyecto = new EstadoProyecto();
    estadoProyecto.setId(id);
    estadoProyecto.setComentario("Estado-" + id);
    estadoProyecto.setEstado(TipoEstadoProyectoEnum.BORRADOR);
    estadoProyecto.setFechaEstado(LocalDateTime.now());
    estadoProyecto.setIdProyecto(1L);

    return estadoProyecto;
  }

}
