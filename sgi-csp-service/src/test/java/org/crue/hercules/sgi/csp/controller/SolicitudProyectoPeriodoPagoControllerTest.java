package org.crue.hercules.sgi.csp.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoPeriodoPagoNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoPago;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPeriodoPagoService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * SolicitudProyectoPeriodoPagoControllerTest
 */
@WebMvcTest(SolicitudProyectoPeriodoPagoController.class)
public class SolicitudProyectoPeriodoPagoControllerTest extends BaseControllerTest {

  @MockBean
  private SolicitudProyectoPeriodoPagoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectoperiodopago";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-E" })
  public void update_WithExistingId_ReturnsSolicitudProyectoPeriodoPago() throws Exception {
    // given: una lista con uno de los SolicitudProyectoPeriodoPago actualizado,
    // otro nuevo y sin los otros 3 periodos existentes
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoPeriodoPago newSolicitudProyectoPeriodoPago = generarSolicitudProyectoPeriodoPago(null, 1L);
    SolicitudProyectoPeriodoPago updatedSolicitudProyectoPeriodoPago = generarSolicitudProyectoPeriodoPago(4L, 1L);

    List<SolicitudProyectoPeriodoPago> solicitudProyectoPeriodoPagos = Arrays
        .asList(updatedSolicitudProyectoPeriodoPago, newSolicitudProyectoPeriodoPago);

    BDDMockito
        .given(service.update(ArgumentMatchers.anyLong(), ArgumentMatchers.<SolicitudProyectoPeriodoPago>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<SolicitudProyectoPeriodoPago> solicitudProyectoPeriodoPagoList = invocation.getArgument(1);
          return solicitudProyectoPeriodoPagoList.stream().map(solicitudProyectoPeriodoPago -> {
            if (solicitudProyectoPeriodoPago.getId() == null) {
              solicitudProyectoPeriodoPago.setId(5L);
            }
            solicitudProyectoPeriodoPago.getSolicitudProyectoSocio().setId(solicitudProyectoSocioId);
            return solicitudProyectoPeriodoPago;
          }).collect(Collectors.toList());
        });

    // when: update SolicitudProyectoPeriodoPago
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, solicitudProyectoSocioId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(solicitudProyectoPeriodoPagos)))
        .andDo(MockMvcResultHandlers.print())
        // then: SolicitudProyectoPeriodoPago is updated
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].solicitudProyectoSocio.id")
            .value(solicitudProyectoPeriodoPagos.get(0).getSolicitudProyectoSocio().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].numPeriodo")
            .value(solicitudProyectoPeriodoPagos.get(0).getNumPeriodo()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$[0].importe").value(solicitudProyectoPeriodoPagos.get(0).getImporte()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].mes").value(solicitudProyectoPeriodoPagos.get(0).getMes()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].solicitudProyectoSocio.id")
            .value(solicitudProyectoPeriodoPagos.get(1).getSolicitudProyectoSocio().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].numPeriodo")
            .value(solicitudProyectoPeriodoPagos.get(1).getNumPeriodo()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$[1].importe").value(solicitudProyectoPeriodoPagos.get(1).getImporte()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].mes").value(solicitudProyectoPeriodoPagos.get(1).getMes()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    SolicitudProyectoPeriodoPago solicitudProyectoPeriodoPago = generarSolicitudProyectoPeriodoPago(1L, 1L);
    List<SolicitudProyectoPeriodoPago> solicitudProyectoPeriodoPagoUpdate = Arrays.asList(solicitudProyectoPeriodoPago);

    BDDMockito.willThrow(new SolicitudProyectoPeriodoPagoNotFoundException(id)).given(service)
        .update(ArgumentMatchers.anyLong(), ArgumentMatchers.<SolicitudProyectoPeriodoPago>anyList());

    // when: update SolicitudProyectoPeriodoPago
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, solicitudProyectoPeriodoPago.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(solicitudProyectoPeriodoPagoUpdate)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-V" })
  public void findById_WithExistingId_ReturnsSolicitudProyectoPeriodoPago() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong()))
        .willAnswer(new Answer<SolicitudProyectoPeriodoPago>() {
          @Override
          public SolicitudProyectoPeriodoPago answer(InvocationOnMock invocation) throws Throwable {
            Long id = invocation.getArgument(0, Long.class);
            return generarSolicitudProyectoPeriodoPago(id, 1L);
          }
        });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested SolicitudProyectoPeriodoPago is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new SolicitudProyectoPeriodoPagoNotFoundException(id);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  /**
   * Función que devuelve un objeto SolicitudProyectoPeriodoPago
   * 
   * @param solicitudProyectoPeriodoPagoId
   * @param solicitudProyectoSocioId
   * @return el objeto SolicitudProyectoPeriodoPago
   */
  private SolicitudProyectoPeriodoPago generarSolicitudProyectoPeriodoPago(Long solicitudProyectoPeriodoPagoId,
      Long solicitudProyectoSocioId) {

    SolicitudProyectoPeriodoPago solicitudProyectoPeriodoPago = SolicitudProyectoPeriodoPago.builder()
        .id(solicitudProyectoPeriodoPagoId)
        .solicitudProyectoSocio(SolicitudProyectoSocio.builder().id(solicitudProyectoSocioId).build()).numPeriodo(3)
        .importe(new BigDecimal(358)).mes(3).build();

    solicitudProyectoPeriodoPago.getSolicitudProyectoSocio().setSolicitudProyectoDatos(new SolicitudProyectoDatos());
    solicitudProyectoPeriodoPago.getSolicitudProyectoSocio().getSolicitudProyectoDatos().setSolicitud(new Solicitud());
    solicitudProyectoPeriodoPago.getSolicitudProyectoSocio().getSolicitudProyectoDatos().getSolicitud()
        .setEstado(new EstadoSolicitud());
    solicitudProyectoPeriodoPago.getSolicitudProyectoSocio().getSolicitudProyectoDatos().getSolicitud().getEstado()
        .setEstado(EstadoSolicitud.Estado.BORRADOR);
    return solicitudProyectoPeriodoPago;
  }
}
