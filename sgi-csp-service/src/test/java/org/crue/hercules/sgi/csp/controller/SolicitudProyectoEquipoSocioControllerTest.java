package org.crue.hercules.sgi.csp.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoEquipoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEquipoSocioService;
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
 * SolicitudProyectoEquipoSocioControllerTest
 */
@WebMvcTest(SolicitudProyectoEquipoSocioController.class)
public class SolicitudProyectoEquipoSocioControllerTest extends BaseControllerTest {

  @MockBean
  private SolicitudProyectoEquipoSocioService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectoequiposocio";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-E" })
  public void update_WithExistingId_ReturnsSolicitudProyectoEquipoSocio() throws Exception {
    // given: una lista con uno de los ConvocatoriaPeriodoJustificacion actualizado,
    // otro nuevo y sin los otros 3 periodos existentes
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoEquipoSocio newuSolicitudProyectoEquipoSocio = generarSolicitudProyectoEquipoSocio(null, 1L);
    SolicitudProyectoEquipoSocio updatedSolicitudProyectoEquipoSocio = generarSolicitudProyectoEquipoSocio(2L, 1L);
    updatedSolicitudProyectoEquipoSocio.setMesFin(6);

    List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocioUpdated = Arrays
        .asList(updatedSolicitudProyectoEquipoSocio, newuSolicitudProyectoEquipoSocio);

    BDDMockito
        .given(service.update(ArgumentMatchers.anyLong(), ArgumentMatchers.<SolicitudProyectoEquipoSocio>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<SolicitudProyectoEquipoSocio> solitudProyectoEquipoSocios = invocation.getArgument(1);
          return solitudProyectoEquipoSocios.stream().map(solicitudProyectoEquipoSocio -> {
            if (solicitudProyectoEquipoSocio.getId() == null) {
              solicitudProyectoEquipoSocio.setId(5L);
            }
            solicitudProyectoEquipoSocio.getSolicitudProyectoSocio().setId(solicitudProyectoSocioId);
            return solicitudProyectoEquipoSocio;
          }).collect(Collectors.toList());
        });

    // when: update SolicitudProyectoEquipoSocio
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, solicitudProyectoSocioId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(solicitudProyectoEquipoSocioUpdated)))
        .andDo(MockMvcResultHandlers.print())
        // then: SolicitudProyectoEquipoSocio is updated
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].rolProyecto.id")
            .value(updatedSolicitudProyectoEquipoSocio.getRolProyecto().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].personaRef")
            .value(updatedSolicitudProyectoEquipoSocio.getPersonaRef()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$[0].mesInicio").value(updatedSolicitudProyectoEquipoSocio.getMesInicio()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$[0].mesFin").value(updatedSolicitudProyectoEquipoSocio.getMesFin()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    SolicitudProyectoEquipoSocio solicitudProyectoEquipoSocio = generarSolicitudProyectoEquipoSocio(1L, 1L);
    List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocioUpdated = Arrays
        .asList(solicitudProyectoEquipoSocio);

    BDDMockito.willThrow(new SolicitudProyectoEquipoSocioNotFoundException(id)).given(service)
        .update(ArgumentMatchers.anyLong(), ArgumentMatchers.<SolicitudProyectoEquipoSocio>anyList());

    // when: update SolicitudProyectoEquipoSocio
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(solicitudProyectoEquipoSocioUpdated)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-V" })
  public void findById_WithExistingId_ReturnsSolicitudProyectoEquipoSocio() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong()))
        .willAnswer(new Answer<SolicitudProyectoEquipoSocio>() {
          @Override
          public SolicitudProyectoEquipoSocio answer(InvocationOnMock invocation) throws Throwable {
            Long id = invocation.getArgument(0, Long.class);
            return generarSolicitudProyectoEquipoSocio(id, 1L);
          }
        });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested SolicitudProyectoEquipoSocio is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new SolicitudProyectoEquipoSocioNotFoundException(id);
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
   * Función que devuelve un objeto SolicitudProyectoEquipoSocio
   * 
   * @param solicitudProyectoEquipoSocioId
   * @param entidadesRelacionadasId
   * @return el objeto SolicitudProyectoEquipoSocio
   */
  private SolicitudProyectoEquipoSocio generarSolicitudProyectoEquipoSocio(Long solicitudProyectoEquipoSocioId,
      Long entidadesRelacionadasId) {

    SolicitudProyectoEquipoSocio solicitudProyectoEquipoSocio = SolicitudProyectoEquipoSocio.builder()
        .id(solicitudProyectoEquipoSocioId)
        .solicitudProyectoSocio(SolicitudProyectoSocio.builder().id(entidadesRelacionadasId).build())
        .rolProyecto(RolProyecto.builder().id(entidadesRelacionadasId).build())
        .personaRef("user-" + solicitudProyectoEquipoSocioId).mesInicio(1).mesFin(3).build();

    solicitudProyectoEquipoSocio.getSolicitudProyectoSocio().setSolicitudProyectoDatos(new SolicitudProyectoDatos());
    solicitudProyectoEquipoSocio.getSolicitudProyectoSocio().getSolicitudProyectoDatos().setSolicitud(new Solicitud());
    solicitudProyectoEquipoSocio.getSolicitudProyectoSocio().getSolicitudProyectoDatos().getSolicitud()
        .setEstado(new EstadoSolicitud());
    solicitudProyectoEquipoSocio.getSolicitudProyectoSocio().getSolicitudProyectoDatos().getSolicitud().getEstado()
        .setEstado(EstadoSolicitud.Estado.BORRADOR);

    return solicitudProyectoEquipoSocio;
  }

}
