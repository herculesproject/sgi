package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoEquipoNotFoundException;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEquipoService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * SolicitudProyectoEquipoTest
 */
@WebMvcTest(SolicitudProyectoEquipoController.class)
public class SolicitudProyectoEquipoControllerTest extends BaseControllerTest {

  @MockBean
  private SolicitudProyectoEquipoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectoequipo";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-C" })
  public void create_ReturnsSolicitudProyectoEquipo() throws Exception {
    // given: new SolicitudProyectoEquipo
    SolicitudProyectoEquipo solicitudProyectoEquipo = generarSolicitudProyectoEquipo(null, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudProyectoEquipo>any()))
        .willAnswer(new Answer<SolicitudProyectoEquipo>() {
          @Override
          public SolicitudProyectoEquipo answer(InvocationOnMock invocation) throws Throwable {
            SolicitudProyectoEquipo givenData = invocation.getArgument(0, SolicitudProyectoEquipo.class);
            SolicitudProyectoEquipo newData = new SolicitudProyectoEquipo();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create SolicitudProyectoEquipo
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudProyectoEquipo)))
        .andDo(MockMvcResultHandlers.print())
        // then: new SolicitudProyectoEquipo is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudProyectoId")
            .value(solicitudProyectoEquipo.getSolicitudProyectoId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("rolProyecto.id").value(solicitudProyectoEquipo.getRolProyecto().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("personaRef").value(solicitudProyectoEquipo.getPersonaRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("mesInicio").value(solicitudProyectoEquipo.getMesInicio()))
        .andExpect(MockMvcResultMatchers.jsonPath("mesFin").value(solicitudProyectoEquipo.getMesFin()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a SolicitudProyectoEquipo with id filled
    SolicitudProyectoEquipo solicitudProyectoEquipo = generarSolicitudProyectoEquipo(1L, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudProyectoEquipo>any()))
        .willThrow(new IllegalArgumentException());

    // when: create SolicitudProyectoEquipo
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudProyectoEquipo)))
        .andDo(MockMvcResultHandlers.print())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-E" })
  public void update_WithExistingId_ReturnsSolicitudProyectoEquipo() throws Exception {
    // given: existing SolicitudProyectoEquipo
    SolicitudProyectoEquipo updatedSolicitudProyectoEquipo = generarSolicitudProyectoEquipo(1L, 1L, 1L);
    updatedSolicitudProyectoEquipo.setMesFin(3);

    BDDMockito.given(service.update(ArgumentMatchers.<SolicitudProyectoEquipo>any()))
        .willAnswer(new Answer<SolicitudProyectoEquipo>() {
          @Override
          public SolicitudProyectoEquipo answer(InvocationOnMock invocation) throws Throwable {
            SolicitudProyectoEquipo givenData = invocation.getArgument(0, SolicitudProyectoEquipo.class);
            return givenData;
          }
        });

    // when: update SolicitudProyectoEquipo
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedSolicitudProyectoEquipo.getId())
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedSolicitudProyectoEquipo)))
        .andDo(MockMvcResultHandlers.print())
        // then: SolicitudProyectoEquipo is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudProyectoId")
            .value(updatedSolicitudProyectoEquipo.getSolicitudProyectoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("rolProyecto.id")
            .value(updatedSolicitudProyectoEquipo.getRolProyecto().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("personaRef").value(updatedSolicitudProyectoEquipo.getPersonaRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("mesInicio").value(updatedSolicitudProyectoEquipo.getMesInicio()))
        .andExpect(MockMvcResultMatchers.jsonPath("mesFin").value(updatedSolicitudProyectoEquipo.getMesFin()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: a SolicitudProyectoEquipo with non existing id
    SolicitudProyectoEquipo updatedSolicitudProyectoEquipo = generarSolicitudProyectoEquipo(1L, 1L, 1L);

    BDDMockito.willThrow(new SolicitudProyectoEquipoNotFoundException(updatedSolicitudProyectoEquipo.getId()))
        .given(service).findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<SolicitudProyectoEquipo>any()))
        .willThrow(new SolicitudProyectoEquipoNotFoundException(updatedSolicitudProyectoEquipo.getId()));

    // when: update SolicitudProyectoEquipo
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedSolicitudProyectoEquipo.getId())
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedSolicitudProyectoEquipo)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-B" })
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
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-B" })
  public void delete_WithoutId_Return404() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.willThrow(new SolicitudProyectoEquipoNotFoundException(id)).given(service)
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
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-V" })
  public void findById_WithExistingId_ReturnsSolicitudProyectoEquipo() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<SolicitudProyectoEquipo>() {
      @Override
      public SolicitudProyectoEquipo answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarSolicitudProyectoEquipo(id, 1L, 1L);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested SolicitudProyectoEquipo is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new SolicitudProyectoEquipoNotFoundException(id);
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
   * Función que devuelve un objeto SolicitudProyectoEquipo
   * 
   * @param solicitudProyectoEquipoId
   * @param solicitudProyectoId
   * @param tipoDocumentoId
   * @return el objeto SolicitudProyectoEquipo
   */
  private SolicitudProyectoEquipo generarSolicitudProyectoEquipo(Long solicitudProyectoEquipoId,
      Long solicitudProyectoId, Long rolProyectoId) {

    SolicitudProyectoEquipo solicitudProyectoEquipo = SolicitudProyectoEquipo.builder().id(solicitudProyectoEquipoId)
        .solicitudProyectoId(solicitudProyectoId).personaRef("personaRef-" + solicitudProyectoEquipoId)
        .rolProyecto(RolProyecto.builder().id(rolProyectoId).build()).mesInicio(1).mesFin(5).build();

    return solicitudProyectoEquipo;
  }

}
