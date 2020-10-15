package org.crue.hercules.sgi.csp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.csp.config.SecurityConfig;
import org.crue.hercules.sgi.csp.exceptions.AreaTematicaArbolNotFoundException;
import org.crue.hercules.sgi.csp.model.AreaTematicaArbol;
import org.crue.hercules.sgi.csp.model.ListadoAreaTematica;
import org.crue.hercules.sgi.csp.service.AreaTematicaArbolService;
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
 * AreaTematicaArbolControllerTest
 */
@WebMvcTest(AreaTematicaArbolController.class)
@Import(SecurityConfig.class)
public class AreaTematicaArbolControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private AreaTematicaArbolService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/areatematicaarboles";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C" })
  public void create_ReturnsModeloAreaTematicaArbol() throws Exception {
    // given: new AreaTematicaArbol
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<AreaTematicaArbol>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          AreaTematicaArbol newAreaTematicaArbol = new AreaTematicaArbol();
          BeanUtils.copyProperties(invocation.getArgument(0), newAreaTematicaArbol);
          newAreaTematicaArbol.setId(1L);
          return newAreaTematicaArbol;
        });

    // when: create AreaTematicaArbol
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(areaTematicaArbol)))
        .andDo(MockMvcResultHandlers.print())
        // then: new AreaTematicaArbol is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(areaTematicaArbol.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("abreviatura").value(areaTematicaArbol.getAbreviatura()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(areaTematicaArbol.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a AreaTematicaArbol with id filled
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<AreaTematicaArbol>any()))
        .willThrow(new IllegalArgumentException());

    // when: create AreaTematicaArbol
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(areaTematicaArbol)))
        .andDo(MockMvcResultHandlers.print())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void update_ReturnsAreaTematicaArbol() throws Exception {
    // given: Existing AreaTematicaArbol to be updated
    AreaTematicaArbol areaTematicaArbolExistente = generarMockAreaTematicaArbol(1L);
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(1L);
    areaTematicaArbol.setNombre("nuevo-nombre");

    BDDMockito.given(service.findById(ArgumentMatchers.<Long>any())).willReturn(areaTematicaArbolExistente);
    BDDMockito.given(service.update(ArgumentMatchers.<AreaTematicaArbol>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update AreaTematicaArbol
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, areaTematicaArbolExistente.getId())
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(areaTematicaArbol)))
        .andDo(MockMvcResultHandlers.print())
        // then: AreaTematicaArbol is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(areaTematicaArbolExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(areaTematicaArbol.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("abreviatura").value(areaTematicaArbol.getAbreviatura()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(areaTematicaArbolExistente.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(1L);

    BDDMockito.willThrow(new AreaTematicaArbolNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<AreaTematicaArbol>any());

    // when: update AreaTematicaArbol
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(areaTematicaArbol)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-B" })
  public void delete_WithExistingId_Return204() throws Exception {
    // given: existing id
    AreaTematicaArbol areaTematicaArbol = generarMockAreaTematicaArbol(1L);
    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      AreaTematicaArbol areaTematicaArbolDisabled = new AreaTematicaArbol();
      BeanUtils.copyProperties(areaTematicaArbol, areaTematicaArbolDisabled);
      areaTematicaArbolDisabled.setActivo(false);
      return areaTematicaArbolDisabled;
    });

    // when: delete by id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, areaTematicaArbol.getId())
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

    BDDMockito.willThrow(new AreaTematicaArbolNotFoundException(id)).given(service)
        .disable(ArgumentMatchers.<Long>any());

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
  public void findById_WithExistingId_ReturnsAreaTematicaArbol() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockAreaTematicaArbol(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested AreaTematicaArbol is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("nombre-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("abreviatura").value("A-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new AreaTematicaArbolNotFoundException(1L);
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
   * Función que devuelve un objeto ListadoAreaTematica
   * 
   * @param id id del ListadoAreaTematica
   * @return el objeto ListadoAreaTematica
   */
  private ListadoAreaTematica generarMockListadoAreaTematica(Long id) {
    return generarMockListadoAreaTematica(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto ListadoAreaTematica
   * 
   * @param id     id del ListadoAreaTematica
   * @param nombre nombre del ListadoAreaTematica
   * @return el objeto ListadoAreaTematica
   */
  private ListadoAreaTematica generarMockListadoAreaTematica(Long id, String nombre) {
    ListadoAreaTematica listadoAreaTematica = new ListadoAreaTematica();
    listadoAreaTematica.setId(id);
    listadoAreaTematica.setNombre(nombre);
    listadoAreaTematica.setDescripcion("descripcion-" + id);
    listadoAreaTematica.setActivo(true);

    return listadoAreaTematica;
  }

  /**
   * Función que devuelve un objeto AreaTematicaArbol
   * 
   * @param id id del AreaTematicaArbol
   * @return el objeto AreaTematicaArbol
   */
  private AreaTematicaArbol generarMockAreaTematicaArbol(Long id) {
    return generarMockAreaTematicaArbol(id, "nombre-" + id, id, null);
  }

  /**
   * Función que devuelve un objeto AreaTematicaArbol
   * 
   * @param id                       id del AreaTematicaArbol
   * @param nombre                   nombre del AreaTematicaArbol
   * @param idListadoAreaTematica    id del ListadoAreaTematica
   * @param idAreaTematicaArbolPadre id del AreaTematicaArbol padre
   * @return el objeto AreaTematicaArbol
   */
  private AreaTematicaArbol generarMockAreaTematicaArbol(Long id, String nombre, Long idListadoAreaTematica,
      Long idAreaTematicaArbolPadre) {
    AreaTematicaArbol areaTematicaArbol = new AreaTematicaArbol();
    areaTematicaArbol.setId(id);
    areaTematicaArbol.setNombre(nombre);
    areaTematicaArbol.setAbreviatura("A-" + (id == null ? 0 : id));
    areaTematicaArbol.setListadoAreaTematica(generarMockListadoAreaTematica(idListadoAreaTematica));
    if (idAreaTematicaArbolPadre != null) {
      areaTematicaArbol.setPadre(generarMockAreaTematicaArbol(idAreaTematicaArbolPadre));
    }
    areaTematicaArbol.setActivo(true);

    return areaTematicaArbol;
  }

}
