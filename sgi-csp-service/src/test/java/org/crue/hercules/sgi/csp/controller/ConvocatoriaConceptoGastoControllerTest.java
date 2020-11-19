package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.service.ConvocatoriaConceptoGastoService;
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
 * ConvocatoriaConceptoGastoControllerTest
 */
@WebMvcTest(ConvocatoriaConceptoGastoController.class)
public class ConvocatoriaConceptoGastoControllerTest extends BaseControllerTest {

  @MockBean
  private ConvocatoriaConceptoGastoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaconceptogastos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C" })
  public void create_ReturnsConvocatoriaConceptoGasto() throws Exception {
    // given: new ConvocatoriaConceptoGasto
    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = generarMockConvocatoriaConceptoGasto(null);

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaConceptoGasto>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ConvocatoriaConceptoGasto newConvocatoriaConceptoGasto = new ConvocatoriaConceptoGasto();
          BeanUtils.copyProperties(invocation.getArgument(0), newConvocatoriaConceptoGasto);
          newConvocatoriaConceptoGasto.setId(1L);
          return newConvocatoriaConceptoGasto;
        });

    // when: create ConvocatoriaConceptoGasto
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaConceptoGasto)))
        .andDo(MockMvcResultHandlers.print())
        // then: new ConvocatoriaConceptoGasto is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("conceptoGasto.nombre")
            .value(convocatoriaConceptoGasto.getConceptoGasto().getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("conceptoGasto.descripcion")
            .value(convocatoriaConceptoGasto.getConceptoGasto().getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("conceptoGasto.activo")
            .value(convocatoriaConceptoGasto.getConceptoGasto().getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ConvocatoriaConceptoGasto with id filled
    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = generarMockConvocatoriaConceptoGasto(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaConceptoGasto>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ConvocatoriaConceptoGasto
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaConceptoGasto)))
        .andDo(MockMvcResultHandlers.print())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void update_ReturnsConvocatoriaConceptoGasto() throws Exception {
    // given: Existing ConvocatoriaConceptoGasto to be updated
    ConvocatoriaConceptoGasto convocatoriaConceptoGastoExistente = generarMockConvocatoriaConceptoGasto(1L);
    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = generarMockConvocatoriaConceptoGasto(1L);
    convocatoriaConceptoGasto.setObservaciones("Observaciones nuevas");

    BDDMockito.given(service.update(ArgumentMatchers.<ConvocatoriaConceptoGasto>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update ConvocatoriaConceptoGasto
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoriaConceptoGastoExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaConceptoGasto)))
        .andDo(MockMvcResultHandlers.print())
        // then: ConvocatoriaConceptoGasto is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("observaciones").value(convocatoriaConceptoGasto.getObservaciones()))
        .andExpect(MockMvcResultMatchers.jsonPath("conceptoGasto.nombre")
            .value(convocatoriaConceptoGastoExistente.getConceptoGasto().getNombre()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = generarMockConvocatoriaConceptoGasto(1L);

    BDDMockito.willThrow(new ConvocatoriaConceptoGastoNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<ConvocatoriaConceptoGasto>any());

    // when: update ConvocatoriaConceptoGasto
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaConceptoGasto)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-V" })
  public void findById_WithExistingId_ReturnsConvocatoriaConceptoGasto() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockConvocatoriaConceptoGasto(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ConvocatoriaConceptoGasto is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ConvocatoriaConceptoGastoNotFoundException(1L);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-B" })
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
  @WithMockUser(username = "user", authorities = { "CSP-ME-B" })
  public void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new ConvocatoriaConceptoGastoNotFoundException(id)).given(service)
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

  /**
   * Función que devuelve un objeto ConvocatoriaConceptoGasto
   * 
   * @param id id del ConvocatoriaConceptoGasto
   * @return el objeto ConvocatoriaConceptoGasto
   */
  private ConvocatoriaConceptoGasto generarMockConvocatoriaConceptoGasto(Long id) {
    return generarMockConvocatoriaConceptoGasto(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto ConvocatoriaConceptoGasto
   * 
   * @param id     id del ConvocatoriaConceptoGasto
   * @param nombre nombre del ConvocatoriaConceptoGasto
   * @return el objeto ConvocatoriaConceptoGasto
   */
  private ConvocatoriaConceptoGasto generarMockConvocatoriaConceptoGasto(Long id, String nombre) {
    ConceptoGasto conceptoGasto = new ConceptoGasto();
    conceptoGasto.setDescripcion("Descripcion");
    conceptoGasto.setNombre("nombre-" + (id != null ? id : 1));
    conceptoGasto.setActivo(true);

    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = new ConvocatoriaConceptoGasto();
    convocatoriaConceptoGasto.setId(id);
    convocatoriaConceptoGasto.setConceptoGasto(conceptoGasto);
    convocatoriaConceptoGasto
        .setConvocatoria(Convocatoria.builder().id(id).activo(Boolean.TRUE).codigo("codigo" + id).build());
    convocatoriaConceptoGasto.setObservaciones("Obs-" + (id != null ? id : 1));

    return convocatoriaConceptoGasto;
  }

}
