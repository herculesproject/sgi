package org.crue.hercules.sgi.csp.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.service.ProgramaService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPeriodoJustificacionService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * SolicitudProyectoPeriodoJustificacionControllerTest
 */
@WebMvcTest(SolicitudProyectoPeriodoJustificacionController.class)
public class SolicitudProyectoPeriodoJustificacionControllerTest extends BaseControllerTest {

  @MockBean
  private SolicitudProyectoPeriodoJustificacionService service;

  @MockBean
  private ProgramaService programaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectoperiodojustificaciones";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C" })
  public void update_ReturnsSolicitudProyectoPeriodoJustificacionList() throws Exception {
    // given: una lista con uno de los SolicitudProyectoPeriodoJustificacion
    // actualizado,
    // otro nuevo y sin los otros 3 periodos existentes
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoPeriodoJustificacion newSolicitudProyectoPeriodoJustificacion = generarMockSolicitudProyectoPeriodoJustificacion(
        null, 27, 30, 1L);
    SolicitudProyectoPeriodoJustificacion updatedSolicitudProyectoPeriodoJustificacion = generarMockSolicitudProyectoPeriodoJustificacion(
        4L, 24, 26, 1L);

    List<SolicitudProyectoPeriodoJustificacion> solicitudProyectoPeriodoJustificaciones = Arrays
        .asList(updatedSolicitudProyectoPeriodoJustificacion, newSolicitudProyectoPeriodoJustificacion);

    BDDMockito.given(
        service.update(ArgumentMatchers.anyLong(), ArgumentMatchers.<SolicitudProyectoPeriodoJustificacion>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<SolicitudProyectoPeriodoJustificacion> periodoJustificaciones = invocation.getArgument(1);
          return periodoJustificaciones.stream().map(periodoJustificacion -> {
            if (periodoJustificacion.getId() == null) {
              periodoJustificacion.setId(5L);
            }
            periodoJustificacion.getSolicitudProyectoSocio().setId(solicitudProyectoSocioId);
            return periodoJustificacion;
          }).collect(Collectors.toList());
        });

    // when: update
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, solicitudProyectoSocioId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudProyectoPeriodoJustificaciones)))
        .andDo(MockMvcResultHandlers.print())
        // then: Se crea el nuevo SolicitudProyectoPeriodoJustificacion, se actualiza el
        // existe y se eliminan el resto
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$[0].id").value(solicitudProyectoPeriodoJustificaciones.get(0).getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].solicitudProyectoSocio.id").value(solicitudProyectoSocioId))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].numPeriodo")
            .value(solicitudProyectoPeriodoJustificaciones.get(0).getNumPeriodo()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].mesInicial")
            .value(solicitudProyectoPeriodoJustificaciones.get(0).getMesInicial()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].mesFinal")
            .value(solicitudProyectoPeriodoJustificaciones.get(0).getMesFinal()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].fechaInicio").value("2020-10-10"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].fechaFin").value("2020-11-20"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].observaciones")
            .value(solicitudProyectoPeriodoJustificaciones.get(0).getObservaciones()))

        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(5))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].solicitudProyectoSocio.id").value(solicitudProyectoSocioId))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].numPeriodo")
            .value(solicitudProyectoPeriodoJustificaciones.get(1).getNumPeriodo()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].mesInicial")
            .value(solicitudProyectoPeriodoJustificaciones.get(1).getMesInicial()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].mesFinal")
            .value(solicitudProyectoPeriodoJustificaciones.get(1).getMesFinal()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].fechaInicio").value("2020-10-10"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].fechaFin").value("2020-11-20")).andExpect(MockMvcResultMatchers
            .jsonPath("$[1].observaciones").value(solicitudProyectoPeriodoJustificaciones.get(1).getObservaciones()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacion = generarMockSolicitudProyectoPeriodoJustificacion(
        1L);

    BDDMockito.willThrow(new SolicitudProyectoPeriodoJustificacionNotFoundException(id)).given(service)
        .update(ArgumentMatchers.anyLong(), ArgumentMatchers.<SolicitudProyectoPeriodoJustificacion>anyList());

    // when: update
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(Arrays.asList(solicitudProyectoPeriodoJustificacion))))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-V" })
  public void findById_WithExistingId_ReturnsSolicitudProyectoPeriodoJustificacion() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockSolicitudProyectoPeriodoJustificacion(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested SolicitudProyectoPeriodoJustificacion is resturned as JSON
        // object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudProyectoSocio.id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("numPeriodo").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("mesInicial").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("mesFinal").value(2))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicio").value("2020-10-10"))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFin").value("2020-11-20"))
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value("observaciones-1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new SolicitudProyectoPeriodoJustificacionNotFoundException(1L);
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
   * Función que devuelve un objeto SolicitudProyectoPeriodoJustificacion
   * 
   * @param id id del SolicitudProyectoPeriodoJustificacion
   * @return el objeto SolicitudProyectoPeriodoJustificacion
   */
  private SolicitudProyectoPeriodoJustificacion generarMockSolicitudProyectoPeriodoJustificacion(Long id) {
    return generarMockSolicitudProyectoPeriodoJustificacion(id, 1, 2, id);
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoPeriodoJustificacion
   * 
   * @param id                  id del SolicitudProyectoPeriodoJustificacion
   * @param mesInicial          Mes inicial
   * @param mesFinal            Mes final
   * @param solicitudProyectoId Id SolicitudProyecto
   * @return el objeto SolicitudProyectoPeriodoJustificacion
   */
  private SolicitudProyectoPeriodoJustificacion generarMockSolicitudProyectoPeriodoJustificacion(Long id,
      Integer mesInicial, Integer mesFinal, Long solicitudProyectoId) {
    SolicitudProyectoSocio solicitudProyectoSocio = new SolicitudProyectoSocio();
    solicitudProyectoSocio.setId(solicitudProyectoId == null ? 1 : solicitudProyectoId);

    SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacion = new SolicitudProyectoPeriodoJustificacion();
    solicitudProyectoPeriodoJustificacion.setId(id);
    solicitudProyectoPeriodoJustificacion.setSolicitudProyectoSocio(solicitudProyectoSocio);
    solicitudProyectoPeriodoJustificacion.setNumPeriodo(1);
    solicitudProyectoPeriodoJustificacion.setMesInicial(mesInicial);
    solicitudProyectoPeriodoJustificacion.setMesFinal(mesFinal);
    solicitudProyectoPeriodoJustificacion.setFechaInicio(LocalDate.of(2020, 10, 10));
    solicitudProyectoPeriodoJustificacion.setFechaFin(LocalDate.of(2020, 11, 20));
    solicitudProyectoPeriodoJustificacion.setObservaciones("observaciones-" + id);

    return solicitudProyectoPeriodoJustificacion;
  }

}
