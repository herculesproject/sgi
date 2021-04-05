package org.crue.hercules.sgi.csp.controller;

import java.time.Instant;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaConceptoGastoCodigoEcNotFoundException;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.service.ConvocatoriaConceptoGastoCodigoEcService;
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
 * ConvocatoriaConceptoGastoCodigoEcControllerTest
 */
@WebMvcTest(ConvocatoriaConceptoGastoCodigoEcController.class)
public class ConvocatoriaConceptoGastoCodigoEcControllerTest extends BaseControllerTest {

  @MockBean
  private ConvocatoriaConceptoGastoCodigoEcService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/convocatoriaconceptogastocodigoecs";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C" })
  public void create_ReturnsConvocatoriaConceptoGastoCodigoEc() throws Exception {
    // given: new ConvocatoriaConceptoGastoCodigoEc
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = generarMockConvocatoriaConceptoGastoCodigoEc(
        null);

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaConceptoGastoCodigoEc>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ConvocatoriaConceptoGastoCodigoEc newConvocatoriaConceptoGastoCodigoEc = new ConvocatoriaConceptoGastoCodigoEc();
          BeanUtils.copyProperties(invocation.getArgument(0), newConvocatoriaConceptoGastoCodigoEc);
          newConvocatoriaConceptoGastoCodigoEc.setId(1L);
          return newConvocatoriaConceptoGastoCodigoEc;
        });

    // when: create ConvocatoriaConceptoGastoCodigoEc
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaConceptoGastoCodigoEc)))
        .andDo(MockMvcResultHandlers.print())
        // then: new ConvocatoriaConceptoGastoCodigoEc is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty()).andExpect(MockMvcResultMatchers
            .jsonPath("codigoEconomicoRef").value(convocatoriaConceptoGastoCodigoEc.getCodigoEconomicoRef()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a ConvocatoriaConceptoGastoCodigoEc with id filled
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = generarMockConvocatoriaConceptoGastoCodigoEc(
        1L);

    BDDMockito.given(service.create(ArgumentMatchers.<ConvocatoriaConceptoGastoCodigoEc>any()))
        .willThrow(new IllegalArgumentException());

    // when: create ConvocatoriaConceptoGastoCodigoEc
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(convocatoriaConceptoGastoCodigoEc)))
        .andDo(MockMvcResultHandlers.print())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void update_ReturnsConvocatoriaConceptoGastoCodigoEc() throws Exception {
    // given: Existing ConvocatoriaConceptoGastoCodigoEc to be updated
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEcExistente = generarMockConvocatoriaConceptoGastoCodigoEc(
        1L);
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = generarMockConvocatoriaConceptoGastoCodigoEc(
        1L);
    convocatoriaConceptoGastoCodigoEc.setCodigoEconomicoRef("cod-new");

    BDDMockito.given(service.update(ArgumentMatchers.<ConvocatoriaConceptoGastoCodigoEc>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update ConvocatoriaConceptoGastoCodigoEc
    mockMvc
        .perform(MockMvcRequestBuilders
            .put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, convocatoriaConceptoGastoCodigoEcExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaConceptoGastoCodigoEc)))
        .andDo(MockMvcResultHandlers.print())
        // then: ConvocatoriaConceptoGastoCodigoEc is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("codigoEconomicoRef")
            .value(convocatoriaConceptoGastoCodigoEc.getCodigoEconomicoRef()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = generarMockConvocatoriaConceptoGastoCodigoEc(
        1L);

    BDDMockito.willThrow(new ConvocatoriaConceptoGastoCodigoEcNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<ConvocatoriaConceptoGastoCodigoEc>any());

    // when: update ConvocatoriaConceptoGastoCodigoEc
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(convocatoriaConceptoGastoCodigoEc)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-V" })
  public void findById_WithExistingId_ReturnsConvocatoriaConceptoGastoCodigoEc() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockConvocatoriaConceptoGastoCodigoEc(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested ConvocatoriaConceptoGastoCodigoEc is resturned as JSON
        // object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ConvocatoriaConceptoGastoCodigoEcNotFoundException(1L);
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

    BDDMockito.willThrow(new ConvocatoriaConceptoGastoCodigoEcNotFoundException(id)).given(service)
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
   * Función que devuelve un objeto ConvocatoriaConceptoGastoCodigoEc
   * 
   * @param id id del ConvocatoriaConceptoGastoCodigoEc
   * @return el objeto ConvocatoriaConceptoGastoCodigoEc
   */
  private ConvocatoriaConceptoGastoCodigoEc generarMockConvocatoriaConceptoGastoCodigoEc(Long id) {
    return generarMockConvocatoriaConceptoGastoCodigoEc(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto ConvocatoriaConceptoGastoCodigoEc
   * 
   * @param id     id del ConvocatoriaConceptoGastoCodigoEc
   * @param nombre nombre del ConvocatoriaConceptoGastoCodigoEc
   * @return el objeto ConvocatoriaConceptoGastoCodigoEc
   */
  private ConvocatoriaConceptoGastoCodigoEc generarMockConvocatoriaConceptoGastoCodigoEc(Long id, String nombre) {
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = new ConvocatoriaConceptoGastoCodigoEc();
    convocatoriaConceptoGastoCodigoEc.setId(id);
    convocatoriaConceptoGastoCodigoEc.setConvocatoriaConceptoGastoId((id != null ? id : 1));
    convocatoriaConceptoGastoCodigoEc.setCodigoEconomicoRef("cod-" + (id != null ? id : 1));
    convocatoriaConceptoGastoCodigoEc.setFechaInicio(Instant.now());
    convocatoriaConceptoGastoCodigoEc.setFechaFin(Instant.now());

    return convocatoriaConceptoGastoCodigoEc;
  }

}
