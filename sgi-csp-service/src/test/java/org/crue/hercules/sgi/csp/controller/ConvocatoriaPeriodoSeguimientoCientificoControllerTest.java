package org.crue.hercules.sgi.csp.controller;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.csp.config.SecurityConfig;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaPeriodoSeguimientoCientificoNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPeriodoSeguimientoCientificoService;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ConvocatoriaPeriodoSeguimientoCientificoControllerTest
 */
@WebMvcTest(ConvocatoriaPeriodoSeguimientoCientificoController.class)
@Import(SecurityConfig.class)
public class ConvocatoriaPeriodoSeguimientoCientificoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private ConvocatoriaPeriodoSeguimientoCientificoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaperiodoseguimientocientificos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CPSCI-C" })
  public void create_ReturnsConvocatoriaPeriodoSeguimientoCientifico() throws Exception {
    // given: new ConvocatoriaPeriodoSeguimientoCientifico
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico//
        .builder()//
        .convocatoria(convocatoria)//
        .mesInicial(1)//
        .mesFinal(2)//
        .fechaInicio(LocalDate.of(2020, 1, 1))//
        .fechaFin(LocalDate.of(2020, 2, 1))//
        .observaciones("observaciones")//
        .build();

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaPeriodoSeguimientoCientifico>any()))
        .willAnswer(new Answer<ConvocatoriaPeriodoSeguimientoCientifico>() {
          @Override
          public ConvocatoriaPeriodoSeguimientoCientifico answer(InvocationOnMock invocation) throws Throwable {
            ConvocatoriaPeriodoSeguimientoCientifico givenData = invocation.getArgument(0,
                ConvocatoriaPeriodoSeguimientoCientifico.class);
            ConvocatoriaPeriodoSeguimientoCientifico newData = new ConvocatoriaPeriodoSeguimientoCientifico();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            newData.setNumPeriodo(1);
            return newData;
          }
        });

    // when: create ConvocatoriaPeriodoSeguimientoCientifico
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaPeriodoSeguimientoCientifico)))
        .andDo(MockMvcResultHandlers.print())
        // then: new ConvocatoriaPeriodoSeguimientoCientifico is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoria.id")
            .value(convocatoriaPeriodoSeguimientoCientifico.getConvocatoria().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("numPeriodo").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("mesInicial")
            .value(convocatoriaPeriodoSeguimientoCientifico.getMesInicial()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("mesFinal").value(convocatoriaPeriodoSeguimientoCientifico.getMesFinal()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicio").value("2020-01-01"))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFin").value("2020-02-01")).andExpect(MockMvcResultMatchers
            .jsonPath("observaciones").value(convocatoriaPeriodoSeguimientoCientifico.getObservaciones()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CPSCI-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with id filled
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico//
        .builder()//
        .id(1L)//
        .convocatoria(convocatoria)//
        .mesInicial(1)//
        .mesFinal(2)//
        .fechaInicio(LocalDate.of(2020, 1, 1))//
        .fechaFin(LocalDate.of(2020, 2, 1))//
        .observaciones("observaciones")//
        .build();

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaPeriodoSeguimientoCientifico>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ConvocatoriaPeriodoSeguimientoCientifico
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaPeriodoSeguimientoCientifico)))
        .andDo(MockMvcResultHandlers.print())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CPSCI-E" })
  public void update_WithExistingId_ReturnsConvocatoriaPeriodoSeguimientoCientifico() throws Exception {
    // given: existing ConvocatoriaPeriodoSeguimientoCientifico
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientificoExistente = ConvocatoriaPeriodoSeguimientoCientifico//
        .builder()//
        .id(1L)//
        .convocatoria(convocatoria)//
        .numPeriodo(1)//
        .mesInicial(1)//
        .mesFinal(2)//
        .fechaInicio(LocalDate.of(2020, 1, 1))//
        .fechaFin(LocalDate.of(2020, 2, 1))//
        .observaciones("observaciones")//
        .build();
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico//
        .builder()//
        .id(convocatoriaPeriodoSeguimientoCientificoExistente.getId())//
        .convocatoria(convocatoriaPeriodoSeguimientoCientificoExistente.getConvocatoria())//
        .numPeriodo(2)//
        .mesInicial(3)//
        .mesFinal(4)//
        .fechaInicio(convocatoriaPeriodoSeguimientoCientificoExistente.getFechaInicio())//
        .fechaFin(convocatoriaPeriodoSeguimientoCientificoExistente.getFechaFin())//
        .observaciones("observaciones-modificadas")//
        .build();

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong()))
        .willReturn(convocatoriaPeriodoSeguimientoCientificoExistente);
    BDDMockito.given(service.update(ArgumentMatchers.<ConvocatoriaPeriodoSeguimientoCientifico>any()))
        .willReturn(convocatoriaPeriodoSeguimientoCientifico);

    // when: update ConvocatoriaPeriodoSeguimientoCientifico
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoriaPeriodoSeguimientoCientificoExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaPeriodoSeguimientoCientificoExistente)))
        .andDo(MockMvcResultHandlers.print())
        // then: ConvocatoriaPeriodoSeguimientoCientifico is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("id").value(convocatoriaPeriodoSeguimientoCientificoExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoria.id")
            .value(convocatoriaPeriodoSeguimientoCientificoExistente.getConvocatoria().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("numPeriodo").value(2))
        .andExpect(MockMvcResultMatchers.jsonPath("mesInicial").value(3))
        .andExpect(MockMvcResultMatchers.jsonPath("mesFinal").value(4))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicio").value("2020-01-01"))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFin").value("2020-02-01"))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value("observaciones-modificadas"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CPSCI-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with non existing id
    Convocatoria convocatoria = Convocatoria.builder().id(1L).duracion(24).build();
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientificoExistente = ConvocatoriaPeriodoSeguimientoCientifico//
        .builder()//
        .id(1L)//
        .convocatoria(convocatoria)//
        .numPeriodo(1)//
        .mesInicial(1)//
        .mesFinal(2)//
        .fechaInicio(LocalDate.of(2020, 1, 1))//
        .fechaFin(LocalDate.of(2020, 2, 1))//
        .observaciones("observaciones")//
        .build();

    BDDMockito
        .willThrow(new ConvocatoriaPeriodoSeguimientoCientificoNotFoundException(
            convocatoriaPeriodoSeguimientoCientificoExistente.getId()))
        .given(service).findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<ConvocatoriaPeriodoSeguimientoCientifico>any()))
        .willThrow(new ConvocatoriaPeriodoSeguimientoCientificoNotFoundException(
            convocatoriaPeriodoSeguimientoCientificoExistente.getId()));

    // when: update ConvocatoriaPeriodoSeguimientoCientifico
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoriaPeriodoSeguimientoCientificoExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaPeriodoSeguimientoCientificoExistente)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CPSCI-B" })
  public void delete_WithExistingId_Return204() throws Exception {
    // given: existing id
    Long convocatoriaPeriodoSeguimientoCientificoId = 1L;

    BDDMockito.doNothing().when(service).delete(ArgumentMatchers.anyLong());

    // when: delete by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoriaPeriodoSeguimientoCientificoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CPSCI-B" })
  public void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long convocatoriaPeriodoSeguimientoCientificoId = 1L;

    BDDMockito
        .willThrow(
            new ConvocatoriaPeriodoSeguimientoCientificoNotFoundException(convocatoriaPeriodoSeguimientoCientificoId))
        .given(service).findById(ArgumentMatchers.<Long>any());
    BDDMockito
        .willThrow(
            new ConvocatoriaPeriodoSeguimientoCientificoNotFoundException(convocatoriaPeriodoSeguimientoCientificoId))
        .given(service).delete(ArgumentMatchers.<Long>any());

    // when: delete by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders
            .delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoriaPeriodoSeguimientoCientificoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CPSCI-V" })
  public void findById_WithExistingId_ReturnsConvocatoriaPeriodoSeguimientoCientifico() throws Exception {
    // given: existing id
    Long convocatoriaPeriodoSeguimientoCientificoId = 1L;

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong()))
        .willAnswer(new Answer<ConvocatoriaPeriodoSeguimientoCientifico>() {
          @Override
          public ConvocatoriaPeriodoSeguimientoCientifico answer(InvocationOnMock invocation) throws Throwable {
            Long id = invocation.getArgument(0, Long.class);
            return ConvocatoriaPeriodoSeguimientoCientifico.builder().id(id).build();
          }
        });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoriaPeriodoSeguimientoCientificoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ConvocatoriaPeriodoSeguimientoCientifico is resturned as
        // JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(convocatoriaPeriodoSeguimientoCientificoId));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CPSCI-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long convocatoriaPeriodoSeguimientoCientificoId = 1L;

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ConvocatoriaPeriodoSeguimientoCientificoNotFoundException(convocatoriaPeriodoSeguimientoCientificoId);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoriaPeriodoSeguimientoCientificoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }
}
