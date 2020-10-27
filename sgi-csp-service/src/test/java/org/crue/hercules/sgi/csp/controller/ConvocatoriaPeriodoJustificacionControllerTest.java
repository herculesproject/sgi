package org.crue.hercules.sgi.csp.controller;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.csp.config.SecurityConfig;
import org.crue.hercules.sgi.csp.enums.TipoJustificacionEnum;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPeriodoJustificacionService;
import org.crue.hercules.sgi.csp.service.ProgramaService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ConvocatoriaPeriodoJustificacionControllerTest
 */
@WebMvcTest(ConvocatoriaPeriodoJustificacionController.class)
@Import(SecurityConfig.class)
public class ConvocatoriaPeriodoJustificacionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private ConvocatoriaPeriodoJustificacionService service;

  @MockBean
  private ProgramaService programaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaperiodojustificaciones";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C" })
  public void create_ReturnsModeloConvocatoriaPeriodoJustificacion() throws Exception {
    // given: new ConvocatoriaPeriodoJustificacion
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaPeriodoJustificacion>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ConvocatoriaPeriodoJustificacion newConvocatoriaPeriodoJustificacion = new ConvocatoriaPeriodoJustificacion();
          BeanUtils.copyProperties(invocation.getArgument(0), newConvocatoriaPeriodoJustificacion);
          newConvocatoriaPeriodoJustificacion.setId(1L);
          return newConvocatoriaPeriodoJustificacion;
        });

    // when: create ConvocatoriaPeriodoJustificacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaPeriodoJustificacion)))
        .andDo(MockMvcResultHandlers.print())
        // then: new ConvocatoriaPeriodoJustificacion is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoria.id")
            .value(convocatoriaPeriodoJustificacion.getConvocatoria().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("numPeriodo").value(convocatoriaPeriodoJustificacion.getNumPeriodo()))
        .andExpect(MockMvcResultMatchers.jsonPath("mesInicial").value(convocatoriaPeriodoJustificacion.getMesInicial()))
        .andExpect(MockMvcResultMatchers.jsonPath("mesFinal").value(convocatoriaPeriodoJustificacion.getMesFinal()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicioPresentacion").value("2020-10-10"))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFinPresentacion").value("2020-11-20"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("observaciones").value(convocatoriaPeriodoJustificacion.getObservaciones()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoJustificacion").value("periodica"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ConvocatoriaPeriodoJustificacion with id filled
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaPeriodoJustificacion>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ConvocatoriaPeriodoJustificacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaPeriodoJustificacion)))
        .andDo(MockMvcResultHandlers.print())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void update_ReturnsConvocatoriaPeriodoJustificacion() throws Exception {
    // given: Existing ConvocatoriaPeriodoJustificacion to be updated
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionExistente = generarMockConvocatoriaPeriodoJustificacion(
        1L);
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(1L);
    convocatoriaPeriodoJustificacion.setObservaciones("observaciones-nuevas");

    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any()))
        .willReturn(convocatoriaPeriodoJustificacionExistente);
    BDDMockito.given(service.update(ArgumentMatchers.<ConvocatoriaPeriodoJustificacion>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update ConvocatoriaPeriodoJustificacion
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoriaPeriodoJustificacionExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaPeriodoJustificacion)))
        .andDo(MockMvcResultHandlers.print())
        // then: ConvocatoriaPeriodoJustificacion is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(convocatoriaPeriodoJustificacionExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoria.id")
            .value(convocatoriaPeriodoJustificacionExistente.getConvocatoria().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("numPeriodo")
            .value(convocatoriaPeriodoJustificacionExistente.getNumPeriodo()))
        .andExpect(MockMvcResultMatchers.jsonPath("mesInicial")
            .value(convocatoriaPeriodoJustificacionExistente.getMesInicial()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("mesFinal").value(convocatoriaPeriodoJustificacionExistente.getMesFinal()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicioPresentacion").value("2020-10-10"))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFinPresentacion").value("2020-11-20"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("observaciones").value(convocatoriaPeriodoJustificacion.getObservaciones()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoJustificacion").value("periodica"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = generarMockConvocatoriaPeriodoJustificacion(1L);

    BDDMockito.willThrow(new ConvocatoriaPeriodoJustificacionNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<ConvocatoriaPeriodoJustificacion>any());

    // when: update ConvocatoriaPeriodoJustificacion
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaPeriodoJustificacion)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-B" })
  public void delete_WithExistingId_Return204() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.doNothing().when(service).delete(ArgumentMatchers.anyLong());

    // when: delete by id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());

    Mockito.verify(service, Mockito.times(1)).delete(ArgumentMatchers.anyLong());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-B" })
  public void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ConvocatoriaPeriodoJustificacionNotFoundException(id)).given(service)
        .delete(ArgumentMatchers.<Long>any());

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
  @WithMockUser(username = "user", authorities = { "CSP-ME-V" })
  public void findById_WithExistingId_ReturnsConvocatoriaPeriodoJustificacion() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockConvocatoriaPeriodoJustificacion(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ConvocatoriaPeriodoJustificacion is resturned as JSON
        // object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoria.id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("numPeriodo").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("mesInicial").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("mesFinal").value(2))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicioPresentacion").value("2020-10-10"))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFinPresentacion").value("2020-11-20"))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value("observaciones-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoJustificacion").value("periodica"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ConvocatoriaPeriodoJustificacionNotFoundException(1L);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * Función que devuelve un objeto ConvocatoriaPeriodoJustificacion
   * 
   * @param id id del ConvocatoriaPeriodoJustificacion
   * @return el objeto ConvocatoriaPeriodoJustificacion
   */
  private ConvocatoriaPeriodoJustificacion generarMockConvocatoriaPeriodoJustificacion(Long id) {
    Convocatoria convocatoria = new Convocatoria();
    convocatoria.setId(id == null ? 1 : id);

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion = new ConvocatoriaPeriodoJustificacion();
    convocatoriaPeriodoJustificacion.setId(id);
    convocatoriaPeriodoJustificacion.setConvocatoria(convocatoria);
    convocatoriaPeriodoJustificacion.setNumPeriodo(1);
    convocatoriaPeriodoJustificacion.setMesInicial(1);
    convocatoriaPeriodoJustificacion.setMesFinal(2);
    convocatoriaPeriodoJustificacion.setFechaInicioPresentacion(LocalDate.of(2020, 10, 10));
    convocatoriaPeriodoJustificacion.setFechaFinPresentacion(LocalDate.of(2020, 11, 20));
    convocatoriaPeriodoJustificacion.setObservaciones("observaciones-" + id);
    convocatoriaPeriodoJustificacion.setTipoJustificacion(TipoJustificacionEnum.PERIODICA);

    return convocatoriaPeriodoJustificacion;
  }

}
