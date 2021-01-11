package org.crue.hercules.sgi.csp.controller;

import java.math.BigDecimal;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioService;
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
 * SolicitudProyectoSocioControllerTest
 */
@WebMvcTest(SolicitudProyectoSocioController.class)
public class SolicitudProyectoSocioControllerTest extends BaseControllerTest {

  @MockBean
  private SolicitudProyectoSocioService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectosocio";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-C" })
  public void create_ReturnsSolicitudProyectoSocio() throws Exception {
    // given: new SolicitudProyectoSocio
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(null, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudProyectoSocio>any()))
        .willAnswer(new Answer<SolicitudProyectoSocio>() {
          @Override
          public SolicitudProyectoSocio answer(InvocationOnMock invocation) throws Throwable {
            SolicitudProyectoSocio givenData = invocation.getArgument(0, SolicitudProyectoSocio.class);
            SolicitudProyectoSocio newData = new SolicitudProyectoSocio();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create SolicitudProyectoSocio
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudProyectoSocio)))
        .andDo(MockMvcResultHandlers.print())
        // then: new SolicitudProyectoSocio is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("rolSocio.id").value(solicitudProyectoSocio.getRolSocio().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudProyectoDatos.id")
            .value(solicitudProyectoSocio.getSolicitudProyectoDatos().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("mesInicio").value(solicitudProyectoSocio.getMesInicio()))
        .andExpect(MockMvcResultMatchers.jsonPath("mesFin").value(solicitudProyectoSocio.getMesFin()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("numInvestigadores").value(solicitudProyectoSocio.getNumInvestigadores()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("importeSolicitado").value(solicitudProyectoSocio.getImporteSolicitado()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a SolicitudProyectoSocio with id filled
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudProyectoSocio>any()))
        .willThrow(new IllegalArgumentException());

    // when: create SolicitudProyectoSocio
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudProyectoSocio)))
        .andDo(MockMvcResultHandlers.print())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-E" })
  public void update_WithExistingId_ReturnsSolicitudProyectoSocio() throws Exception {
    // given: existing SolicitudProyectoSocio
    SolicitudProyectoSocio updatedSolicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);
    updatedSolicitudProyectoSocio.setMesFin(12);

    BDDMockito.given(service.update(ArgumentMatchers.<SolicitudProyectoSocio>any()))
        .willAnswer(new Answer<SolicitudProyectoSocio>() {
          @Override
          public SolicitudProyectoSocio answer(InvocationOnMock invocation) throws Throwable {
            SolicitudProyectoSocio givenData = invocation.getArgument(0, SolicitudProyectoSocio.class);
            return givenData;
          }
        });

    // when: update SolicitudProyectoSocio
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedSolicitudProyectoSocio.getId())
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedSolicitudProyectoSocio)))
        .andDo(MockMvcResultHandlers.print())
        // then: SolicitudProyectoSocio is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(
            MockMvcResultMatchers.jsonPath("rolSocio.id").value(updatedSolicitudProyectoSocio.getRolSocio().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudProyectoDatos.id")
            .value(updatedSolicitudProyectoSocio.getSolicitudProyectoDatos().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("mesInicio").value(updatedSolicitudProyectoSocio.getMesInicio()))
        .andExpect(MockMvcResultMatchers.jsonPath("mesFin").value(updatedSolicitudProyectoSocio.getMesFin()))
        .andExpect(MockMvcResultMatchers.jsonPath("numInvestigadores")
            .value(updatedSolicitudProyectoSocio.getNumInvestigadores()))
        .andExpect(MockMvcResultMatchers.jsonPath("importeSolicitado")
            .value(updatedSolicitudProyectoSocio.getImporteSolicitado()));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: a SolicitudProyectoSocio with non existing id
    SolicitudProyectoSocio updatedSolicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);

    BDDMockito.willThrow(new SolicitudProyectoSocioNotFoundException(updatedSolicitudProyectoSocio.getId()))
        .given(service).findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<SolicitudProyectoSocio>any()))
        .willThrow(new SolicitudProyectoSocioNotFoundException(updatedSolicitudProyectoSocio.getId()));

    // when: update SolicitudProyectoSocio
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedSolicitudProyectoSocio.getId())
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedSolicitudProyectoSocio)))
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

    BDDMockito.willThrow(new SolicitudProyectoSocioNotFoundException(id)).given(service)
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
  public void findById_WithExistingId_ReturnsSolicitudProyectoSocio() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<SolicitudProyectoSocio>() {
      @Override
      public SolicitudProyectoSocio answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarSolicitudProyectoSocio(id, 1L, 1L);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested SolicitudProyectoSocio is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new SolicitudProyectoSocioNotFoundException(id);
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
   * Función que devuelve un objeto SolicitudProyectoSocio
   * 
   * @param solicitudProyectoSocioId
   * @param solicitudProyectoDatosId
   * @return el objeto SolicitudProyectoSocio
   */
  private SolicitudProyectoSocio generarSolicitudProyectoSocio(Long solicitudProyectoSocioId,
      Long solicitudProyectoDatosId, Long rolSocioId) {

    SolicitudProyectoSocio solicitudProyectoSocio = SolicitudProyectoSocio.builder().id(solicitudProyectoSocioId)
        .solicitudProyectoDatos(SolicitudProyectoDatos.builder().id(solicitudProyectoDatosId).build())
        .rolSocio(RolSocio.builder().id(rolSocioId).build()).mesInicio(1).mesFin(3).numInvestigadores(2)
        .importeSolicitado(new BigDecimal("335")).empresaRef("002").build();

    return solicitudProyectoSocio;
  }
}
