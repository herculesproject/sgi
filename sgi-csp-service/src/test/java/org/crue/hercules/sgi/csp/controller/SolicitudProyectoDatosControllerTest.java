package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoDatosNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoDatosService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEntidadFinanciadoraAjenaService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPresupuestoService;
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
 * SolicitudProyectoDatosControllerTest
 */
@WebMvcTest(SolicitudProyectoDatosController.class)
public class SolicitudProyectoDatosControllerTest extends BaseControllerTest {

  @MockBean
  private SolicitudProyectoDatosService service;
  @MockBean
  private SolicitudProyectoPresupuestoService solicitudProyectoPresupuestoService;
  @MockBean
  private SolicitudProyectoSocioService solicitudProyectoSocioService;
  @MockBean
  private SolicitudProyectoEntidadFinanciadoraAjenaService solicitudProyectoEntidadFinanciadoraAjenaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectodatos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-C" })
  public void create_ReturnsSolicitudProyectoDatos() throws Exception {
    // given: new SolicitudProyectoDatos
    SolicitudProyectoDatos solicitudProyectoDatos = generarSolicitudProyectoDatos(null, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudProyectoDatos>any()))
        .willAnswer(new Answer<SolicitudProyectoDatos>() {
          @Override
          public SolicitudProyectoDatos answer(InvocationOnMock invocation) throws Throwable {
            SolicitudProyectoDatos givenData = invocation.getArgument(0, SolicitudProyectoDatos.class);
            SolicitudProyectoDatos newData = new SolicitudProyectoDatos();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create SolicitudProyectoDatos
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudProyectoDatos)))
        .andDo(MockMvcResultHandlers.print())
        // then: new SolicitudProyectoDatos is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("solicitud.id").value(solicitudProyectoDatos.getSolicitud().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("titulo").value(solicitudProyectoDatos.getTitulo()))
        .andExpect(MockMvcResultMatchers.jsonPath("colaborativo").value(solicitudProyectoDatos.getColaborativo()))
        .andExpect(MockMvcResultMatchers.jsonPath("presupuestoPorEntidades")
            .value(solicitudProyectoDatos.getPresupuestoPorEntidades()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a SolicitudProyectoDatos with id filled
    SolicitudProyectoDatos solicitudProyectoDatos = generarSolicitudProyectoDatos(1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudProyectoDatos>any()))
        .willThrow(new IllegalArgumentException());

    // when: create SolicitudProyectoDatos
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudProyectoDatos)))
        .andDo(MockMvcResultHandlers.print())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-E" })
  public void update_WithExistingId_ReturnsSolicitudProyectoDatos() throws Exception {
    // given: existing SolicitudProyectoDatos
    SolicitudProyectoDatos updatedSolicitudProyectoDatos = generarSolicitudProyectoDatos(1L, 1L);
    updatedSolicitudProyectoDatos.setTitulo("titulo-modificado");

    BDDMockito.given(service.update(ArgumentMatchers.<SolicitudProyectoDatos>any()))
        .willAnswer(new Answer<SolicitudProyectoDatos>() {
          @Override
          public SolicitudProyectoDatos answer(InvocationOnMock invocation) throws Throwable {
            SolicitudProyectoDatos givenData = invocation.getArgument(0, SolicitudProyectoDatos.class);
            return givenData;
          }
        });

    // when: update SolicitudProyectoDatos
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedSolicitudProyectoDatos.getId())
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedSolicitudProyectoDatos)))
        .andDo(MockMvcResultHandlers.print())
        // then: SolicitudProyectoDatos is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(
            MockMvcResultMatchers.jsonPath("solicitud.id").value(updatedSolicitudProyectoDatos.getSolicitud().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("titulo").value(updatedSolicitudProyectoDatos.getTitulo()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("colaborativo").value(updatedSolicitudProyectoDatos.getColaborativo()))
        .andExpect(MockMvcResultMatchers.jsonPath("presupuestoPorEntidades")
            .value(updatedSolicitudProyectoDatos.getPresupuestoPorEntidades()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: a SolicitudProyectoDatos with non existing id
    SolicitudProyectoDatos updatedSolicitudProyectoDatos = generarSolicitudProyectoDatos(1L, 1L);

    BDDMockito.willThrow(new SolicitudProyectoDatosNotFoundException(updatedSolicitudProyectoDatos.getId()))
        .given(service).findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<SolicitudProyectoDatos>any()))
        .willThrow(new SolicitudProyectoDatosNotFoundException(updatedSolicitudProyectoDatos.getId()));

    // when: update SolicitudProyectoDatos
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedSolicitudProyectoDatos.getId())
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedSolicitudProyectoDatos)))
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

    BDDMockito.willThrow(new SolicitudProyectoDatosNotFoundException(id)).given(service)
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
  public void findById_WithExistingId_ReturnsSolicitudProyectoDatos() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<SolicitudProyectoDatos>() {
      @Override
      public SolicitudProyectoDatos answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarSolicitudProyectoDatos(id, 1L);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested SolicitudProyectoDatos is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new SolicitudProyectoDatosNotFoundException(id);
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
   * Función que devuelve un objeto SolicitudProyectoDatos
   * 
   * @param solicitudProyectoDatosId
   * @param solicitudId
   * @return el objeto SolicitudProyectoDatos
   */
  private SolicitudProyectoDatos generarSolicitudProyectoDatos(Long solicitudProyectoDatosId, Long solicitudId) {

    SolicitudProyectoDatos solicitudProyectoDatos = SolicitudProyectoDatos.builder().id(solicitudProyectoDatosId)
        .solicitud(Solicitud.builder().id(solicitudId).build()).titulo("titulo-" + solicitudProyectoDatosId)
        .acronimo("acronimo-" + solicitudProyectoDatosId).colaborativo(Boolean.TRUE)
        .presupuestoPorEntidades(Boolean.TRUE).build();

    solicitudProyectoDatos.getSolicitud().setEstado(new EstadoSolicitud());
    solicitudProyectoDatos.getSolicitud().getEstado().setEstado(EstadoSolicitud.Estado.BORRADOR);
    return solicitudProyectoDatos;
  }
}
