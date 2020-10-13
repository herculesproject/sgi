package org.crue.hercules.sgi.csp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.csp.config.SecurityConfig;
import org.crue.hercules.sgi.csp.exceptions.ProgramaNotFoundException;
import org.crue.hercules.sgi.csp.model.Plan;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.service.ProgramaService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
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
 * ProgramaControllerTest
 */
@WebMvcTest(ProgramaController.class)
@Import(SecurityConfig.class)
public class ProgramaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private ProgramaService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/programas";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C" })
  public void create_ReturnsModeloPrograma() throws Exception {
    // given: new Programa
    Programa programa = generarMockPrograma(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<Programa>any())).willAnswer((InvocationOnMock invocation) -> {
      Programa newPrograma = new Programa();
      BeanUtils.copyProperties(invocation.getArgument(0), newPrograma);
      newPrograma.setId(1L);
      return newPrograma;
    });

    // when: create Programa
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(programa)))
        .andDo(MockMvcResultHandlers.print())
        // then: new Programa is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(programa.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(programa.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(programa.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a Programa with id filled
    Programa programa = generarMockPrograma(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<Programa>any())).willThrow(new IllegalArgumentException());

    // when: create Programa
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(programa)))
        .andDo(MockMvcResultHandlers.print())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void update_ReturnsPrograma() throws Exception {
    // given: Existing Programa to be updated
    Programa programaExistente = generarMockPrograma(1L);
    Programa programa = generarMockPrograma(1L);
    programa.setNombre("nuevo-nombre");

    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any())).willReturn(programaExistente);
    BDDMockito.given(service.update(ArgumentMatchers.<Programa>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update Programa
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, programaExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(programa)))
        .andDo(MockMvcResultHandlers.print())
        // then: Programa is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(programaExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(programa.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(programa.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(programaExistente.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    Programa programa = generarMockPrograma(1L);

    BDDMockito.willThrow(new ProgramaNotFoundException(id)).given(service).update(ArgumentMatchers.<Programa>any());

    // when: update Programa
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(programa)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-B" })
  public void delete_WithExistingId_Return204() throws Exception {
    // given: existing id
    Programa programa = generarMockPrograma(1L);
    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      Programa programaDisabled = new Programa();
      BeanUtils.copyProperties(programa, programaDisabled);
      programaDisabled.setActivo(false);
      return programaDisabled;
    });

    // when: delete by id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, programa.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-B" })
  public void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ProgramaNotFoundException(id)).given(service).disable(ArgumentMatchers.<Long>any());

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
  public void findById_WithExistingId_ReturnsPrograma() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockPrograma(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested Programa is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("nombre-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value("descripcion-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ProgramaNotFoundException(1L);
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
   * Función que devuelve un objeto Plan
   * 
   * @param id id del Plan
   * @return el objeto Plan
   */
  private Plan generarMockPlan(Long id) {
    return generarMockPlan(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto Plan
   * 
   * @param id     id del Plan
   * @param nombre nombre del Plan
   * @return el objeto Plan
   */
  private Plan generarMockPlan(Long id, String nombre) {
    Plan plan = new Plan();
    plan.setId(id);
    plan.setNombre(nombre);
    plan.setDescripcion("descripcion-" + id);
    plan.setActivo(true);

    return plan;
  }

  /**
   * Función que devuelve un objeto Programa
   * 
   * @param id id del Programa
   * @return el objeto Programa
   */
  private Programa generarMockPrograma(Long id) {
    return generarMockPrograma(id, "nombre-" + id, id, null);
  }

  /**
   * Función que devuelve un objeto Programa
   * 
   * @param id              id del Programa
   * @param nombre          nombre del Programa
   * @param idPlan          id del plan
   * @param idProgramaPadre id del Programa padre
   * @return el objeto Programa
   */
  private Programa generarMockPrograma(Long id, String nombre, Long idPlan, Long idProgramaPadre) {
    Programa programa = new Programa();
    programa.setId(id);
    programa.setNombre(nombre);
    programa.setDescripcion("descripcion-" + id);
    programa.setPlan(generarMockPlan(idPlan));
    if (idProgramaPadre != null) {
      programa.setPadre(generarMockPrograma(idProgramaPadre));
    }
    programa.setActivo(true);

    return programa;
  }

}
