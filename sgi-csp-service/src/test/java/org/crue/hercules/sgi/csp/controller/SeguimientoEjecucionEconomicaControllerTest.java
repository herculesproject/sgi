package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoEjecucionEconomica;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoJustificacionService;
import org.crue.hercules.sgi.csp.service.ProyectoService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * SeguimientoEjecucionEconomicaControllerTest
 */
@WebMvcTest(SeguimientoEjecucionEconomicaController.class)
public class SeguimientoEjecucionEconomicaControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoService proyectoService;
  @MockBean
  private ProyectoPeriodoJustificacionService proyectoPeriodoJustificacionService;

  private static final String CONTROLLER_BASE_PATH = SeguimientoEjecucionEconomicaController.REQUEST_MAPPING;
  private static final String PATH_PROYECTOS = SeguimientoEjecucionEconomicaController.PATH_PROYECTOS;
  private static final String PATH_PERIODO_JUSTIFICACION = SeguimientoEjecucionEconomicaController.PATH_PERIODO_JUSTIFICACION;

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-V" })
  public void findProyectosSeguimientoEjecucionEconomica_ReturnsPage() throws Exception {
    // given: Una lista con 37 ProyectoSeguimientoEjecucionEconomica
    String proyectoSgeRef = "1";
    List<ProyectoSeguimientoEjecucionEconomica> proyectos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectos.add(generarMockProyectoSeguimientoEjecucionEconomica(i, "Proyecto-" + String.format("%03d", i)));
    }
    Integer page = 3;
    Integer pageSize = 10;
    BDDMockito
        .given(proyectoService.findProyectosSeguimientoEjecucionEconomica(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectos.size() ? proyectos.size() : toIndex;
          List<ProyectoSeguimientoEjecucionEconomica> content = proyectos.subList(fromIndex, toIndex);
          Page<ProyectoSeguimientoEjecucionEconomica> pageResponse = new PageImpl<>(content, pageable,
              proyectos.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PROYECTOS, proyectoSgeRef)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ProyectoSeguimientoEjecucionEconomica del
        // 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();
    List<ProyectoSeguimientoEjecucionEconomica> proyectosResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ProyectoSeguimientoEjecucionEconomica>>() {
        });
    for (int i = 31; i <= 37; i++) {
      ProyectoSeguimientoEjecucionEconomica proyecto = proyectosResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(proyecto.getNombre()).isEqualTo("Proyecto-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-V" })
  public void findProyectosSeguimientoEjecucionEconomica_EmptyList_Returns204() throws Exception {
    // given: no data ProyectoSeguimientoEjecucionEconomica
    String proyectoSgeRef = "1";
    BDDMockito
        .given(proyectoService.findProyectosSeguimientoEjecucionEconomica(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoSeguimientoEjecucionEconomica>>() {
          @Override
          public Page<ProyectoSeguimientoEjecucionEconomica> answer(InvocationOnMock invocation) throws Throwable {
            Page<ProyectoSeguimientoEjecucionEconomica> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PROYECTOS, proyectoSgeRef)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-V" })
  public void findProyectoPeriodosJustificacion_ReturnsPage() throws Exception {
    // given: Una lista con 37 ProyectoPeriodoJustificacion
    String proyectoSgeRef = "1";
    List<ProyectoPeriodoJustificacion> periodosJustificacion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      periodosJustificacion.add(generarMockProyectoPeriodoJustificacion(i));
    }
    Integer page = 3;
    Integer pageSize = 10;
    BDDMockito
        .given(proyectoPeriodoJustificacionService.findAllByProyectoSgeRef(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > periodosJustificacion.size() ? periodosJustificacion.size() : toIndex;
          List<ProyectoPeriodoJustificacion> content = periodosJustificacion.subList(fromIndex, toIndex);
          Page<ProyectoPeriodoJustificacion> pageResponse = new PageImpl<>(content, pageable,
              periodosJustificacion.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PERIODO_JUSTIFICACION, proyectoSgeRef)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ProyectoPeriodoJustificacion del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();
    List<ProyectoPeriodoJustificacion> periodosJustificacionResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(),
        new TypeReference<List<ProyectoPeriodoJustificacion>>() {
        });
    for (int i = 31; i <= 37; i++) {
      ProyectoPeriodoJustificacion periodoJustificacion = periodosJustificacionResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(periodoJustificacion.getObservaciones())
          .isEqualTo("observaciones-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SJUS-V" })
  public void findProyectoPeriodosJustificacion_EmptyList_Returns204() throws Exception {
    // given: no data ProyectoSeguimientoEjecucionEconomica
    String proyectoSgeRef = "1";
    BDDMockito
        .given(proyectoPeriodoJustificacionService.findAllByProyectoSgeRef(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoSeguimientoEjecucionEconomica>>() {
          @Override
          public Page<ProyectoSeguimientoEjecucionEconomica> answer(InvocationOnMock invocation) throws Throwable {
            Page<ProyectoSeguimientoEjecucionEconomica> page = new PageImpl<>(Collections.emptyList());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PERIODO_JUSTIFICACION, proyectoSgeRef)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  private ProyectoSeguimientoEjecucionEconomica generarMockProyectoSeguimientoEjecucionEconomica(Long id,
      String nombre) {
    return ProyectoSeguimientoEjecucionEconomica.builder()
        .id(id)
        .nombre(nombre)
        .build();
  }

  private ProyectoPeriodoJustificacion generarMockProyectoPeriodoJustificacion(Long id) {
    final String observacionesSuffix = id != null ? String.format("%03d", id) : "001";
    return ProyectoPeriodoJustificacion.builder()
        .id(id)
        .observaciones("observaciones-" + observacionesSuffix)
        .build();
  }
}
