package org.crue.hercules.sgi.csp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.csp.config.SecurityConfig;
import org.crue.hercules.sgi.csp.exceptions.ModeloEjecucionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ModeloUnidadNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.service.ModeloUnidadService;
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
 * ModeloUnidadControllerTest
 */
@WebMvcTest(ModeloUnidadController.class)
@Import(SecurityConfig.class)
public class ModeloUnidadControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private ModeloUnidadService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/modelounidades";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TENL-C" })
  public void create_ReturnsModeloUnidad() throws Exception {
    // given: new ModeloUnidad
    ModeloUnidad modeloUnidad = generarMockModeloUnidad(null);

    BDDMockito.given(service.create(ArgumentMatchers.<ModeloUnidad>any())).willAnswer(new Answer<ModeloUnidad>() {
      @Override
      public ModeloUnidad answer(InvocationOnMock invocation) throws Throwable {
        ModeloUnidad givenData = invocation.getArgument(0, ModeloUnidad.class);
        ModeloUnidad newData = new ModeloUnidad();
        BeanUtils.copyProperties(givenData, newData);
        newData.setId(1L);
        return newData;
      }
    });

    // when: create ModeloUnidad
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(modeloUnidad)))
        .andDo(MockMvcResultHandlers.print())
        // then: new ModeloUnidad is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(
            MockMvcResultMatchers.jsonPath("modeloEjecucion.id").value(modeloUnidad.getModeloEjecucion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("unidadGestionRef").value(modeloUnidad.getUnidadGestionRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TENL-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ModeloUnidad with id filled
    ModeloUnidad modeloUnidad = generarMockModeloUnidad(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ModeloUnidad>any())).willThrow(new IllegalArgumentException());

    // when: create ModeloUnidad
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(modeloUnidad)))
        .andDo(MockMvcResultHandlers.print())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TENL-C" })
  public void create_WithNonExistingModeloEjecucionId_Returns404() throws Exception {
    // given: a ModeloUnidad with non existing ModeloEjecucionId
    ModeloUnidad modeloUnidad = generarMockModeloUnidad(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ModeloUnidad>any()))
        .willThrow(new ModeloEjecucionNotFoundException(modeloUnidad.getModeloEjecucion().getId()));

    // when: create ModeloUnidad
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(modeloUnidad)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TENL-B" })
  public void delete_WithExistingId_Return204() throws Exception {
    // given: existing id
    ModeloUnidad modeloUnidad = generarMockModeloUnidad(1L);
    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willReturn(modeloUnidad);

    // when: delete by id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, modeloUnidad.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TENL-B" })
  public void delete_NonExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ModeloUnidadNotFoundException(id)).given(service).disable(ArgumentMatchers.<Long>any());

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
  @WithMockUser(username = "user", authorities = { "CSP-TENL-V" })
  public void findById_WithExistingId_ReturnsModeloUnidad() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<ModeloUnidad>() {
      @Override
      public ModeloUnidad answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarMockModeloUnidad(id);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ModeloUnidad is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TENL-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ModeloUnidadNotFoundException(1L);
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
   * Función que devuelve un objeto ModeloUnidad
   * 
   * @param id               id del ModeloUnidad
   * @param unidadGestionRef unidadGestionRef
   * @return el objeto ModeloUnidad
   */
  private ModeloUnidad generarMockModeloUnidad(Long id, String unidadGestionRef) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(id);
    modeloUnidad.setModeloEjecucion(modeloEjecucion);
    modeloUnidad.setUnidadGestionRef(unidadGestionRef);
    modeloUnidad.setActivo(true);

    return modeloUnidad;
  }

  /**
   * Función que devuelve un objeto ModeloUnidad
   * 
   * @param id id del ModeloUnidad
   * @return el objeto ModeloUnidad
   */
  private ModeloUnidad generarMockModeloUnidad(Long id) {
    return generarMockModeloUnidad(id, "ModeloUnidad" + String.format("%03d", id));
  }

}
