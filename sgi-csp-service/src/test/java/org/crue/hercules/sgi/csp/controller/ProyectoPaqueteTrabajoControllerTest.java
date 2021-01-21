package org.crue.hercules.sgi.csp.controller;

import java.time.LocalDate;

import org.crue.hercules.sgi.csp.exceptions.ProyectoPaqueteTrabajoNotFoundException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.crue.hercules.sgi.csp.service.ProyectoPaqueteTrabajoService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
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
 * ProyectoPaqueteTrabajoControllerTest
 */
@WebMvcTest(ProyectoPaqueteTrabajoController.class)
public class ProyectoPaqueteTrabajoControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoPaqueteTrabajoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectopaquetetrabajos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-C" })
  public void create_ReturnsProyectoPaqueteTrabajo() throws Exception {
    // given: new ProyectoPaqueteTrabajo
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setId(null);

    BDDMockito.given(service.create(ArgumentMatchers.<ProyectoPaqueteTrabajo>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ProyectoPaqueteTrabajo newProyectoPaqueteTrabajo = new ProyectoPaqueteTrabajo();
          BeanUtils.copyProperties(invocation.getArgument(0), newProyectoPaqueteTrabajo);
          newProyectoPaqueteTrabajo.setId(1L);
          return newProyectoPaqueteTrabajo;
        });

    // when: create ProyectoPaqueteTrabajo
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoPaqueteTrabajo)))
        .andDo(MockMvcResultHandlers.print())
        // then: new ProyectoPaqueteTrabajo is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("proyecto.id").value(proyectoPaqueteTrabajo.getProyecto().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(proyectoPaqueteTrabajo.getNombre()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("fechaInicio").value(proyectoPaqueteTrabajo.getFechaInicio().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFin").value(proyectoPaqueteTrabajo.getFechaFin().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("personaMes").value(proyectoPaqueteTrabajo.getPersonaMes()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(proyectoPaqueteTrabajo.getDescripcion()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ProyectoPaqueteTrabajo with id filled
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ProyectoPaqueteTrabajo>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ProyectoPaqueteTrabajo
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(proyectoPaqueteTrabajo)))
        .andDo(MockMvcResultHandlers.print())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_ReturnsProyectoPaqueteTrabajo() throws Exception {
    // given: Existing ProyectoPaqueteTrabajo to be updated
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajoExistente = generarMockProyectoPaqueteTrabajo(1L, 1L);
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setDescripcion("descripcion-modificada");

    BDDMockito.given(service.update(ArgumentMatchers.<ProyectoPaqueteTrabajo>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update ProyectoPaqueteTrabajo
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, proyectoPaqueteTrabajoExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyectoPaqueteTrabajo)))
        .andDo(MockMvcResultHandlers.print())
        // then: ProyectoPaqueteTrabajo is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(proyectoPaqueteTrabajoExistente.getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("proyecto.id").value(proyectoPaqueteTrabajoExistente.getProyecto().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(proyectoPaqueteTrabajoExistente.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicio")
            .value(proyectoPaqueteTrabajoExistente.getFechaInicio().toString()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("fechaFin").value(proyectoPaqueteTrabajoExistente.getFechaFin().toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("personaMes").value(proyectoPaqueteTrabajoExistente.getPersonaMes()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(proyectoPaqueteTrabajo.getDescripcion()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);

    BDDMockito.willThrow(new ProyectoPaqueteTrabajoNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<ProyectoPaqueteTrabajo>any());

    // when: update ProyectoPaqueteTrabajo
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(proyectoPaqueteTrabajo)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-B" })
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
  @WithMockUser(username = "user", authorities = { "CSP-PRO-B" })
  public void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ProyectoPaqueteTrabajoNotFoundException(id)).given(service)
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
  @WithMockUser(username = "user", authorities = { "CSP-PRO-V" })
  public void existsById_WithExistingId_Returns200() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: exists by id
    mockMvc
        .perform(MockMvcRequestBuilders.head(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print())
        // then: 200 OK
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-V" })
  public void existsById_WithNoExistingId_Returns204() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: exists by id
    mockMvc
        .perform(MockMvcRequestBuilders.head(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print())
        // then: 204 No Content
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-V" })
  public void findById_WithExistingId_ReturnsProyectoPaqueteTrabajo() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockProyectoPaqueteTrabajo(invocation.getArgument(0), 1L);
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ProyectoPaqueteTrabajo is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
        .andExpect(MockMvcResultMatchers.jsonPath("proyecto.id").value(1L))
        .andExpect(
            MockMvcResultMatchers.jsonPath("nombre").value("proyecto-paquete-trabajo-" + String.format("%03d", id)))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaInicio").value("2020-01-01"))
        .andExpect(MockMvcResultMatchers.jsonPath("fechaFin").value("2020-01-15"))
        .andExpect(MockMvcResultMatchers.jsonPath("personaMes").value(1D)).andExpect(MockMvcResultMatchers
            .jsonPath("descripcion").value("descripcion-proyecto-paquete-trabajo-" + String.format("%03d", id)));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ProyectoPaqueteTrabajoNotFoundException(1L);
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
   * Función que devuelve un objeto ProyectoPaqueteTrabajo
   * 
   * @param id         id del ProyectoPaqueteTrabajo
   * @param proyectoId id del Proyecto
   * @return el objeto ProyectoPaqueteTrabajo
   */
  private ProyectoPaqueteTrabajo generarMockProyectoPaqueteTrabajo(Long id, Long proyectoId) {

    return ProyectoPaqueteTrabajo.builder()//
        .id(id)//
        .proyecto(Proyecto.builder().id(proyectoId).build())//
        .nombre("proyecto-paquete-trabajo-" + (id == null ? "" : String.format("%03d", id)))//
        .fechaInicio(LocalDate.of(2020, 01, 01))//
        .fechaFin(LocalDate.of(2020, 01, 15))//
        .personaMes(1D)//
        .descripcion("descripcion-proyecto-paquete-trabajo-" + (id == null ? "" : String.format("%03d", id)))//
        .build();
  }

}
