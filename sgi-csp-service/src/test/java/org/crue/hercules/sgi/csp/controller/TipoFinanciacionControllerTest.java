package org.crue.hercules.sgi.csp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.csp.config.SecurityConfig;
import org.crue.hercules.sgi.csp.exceptions.TipoFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.csp.service.TipoFinanciacionService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
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
 * TipoFinanciacionControllerTest
 */
@WebMvcTest(TipoFinanciacionController.class)
@Import(SecurityConfig.class)
public class TipoFinanciacionControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper mapper;
  @MockBean
  private TipoFinanciacionService tipoFinanciacionService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_FINANCIACION_CONTROLLER_BASE_PATH = "/tipofinanciaciones";

  @Test
  @WithMockUser(username = "user")
  public void create_ReturnsTipoFinanciacion() throws Exception {
    // given: Un TipoFinanciacion nuevo
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(null);

    String tipoFinanciacionJson = mapper.writeValueAsString(tipoFinanciacion);

    BDDMockito.given(tipoFinanciacionService.create(ArgumentMatchers.<TipoFinanciacion>any()))
        .will((InvocationOnMock invocation) -> {
          TipoFinanciacion tipoFinanciacionCreado = invocation.getArgument(0);
          tipoFinanciacionCreado.setId(1L);
          return tipoFinanciacionCreado;
        });
    // when: Creamos un TipoFinanciacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_FINANCIACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(tipoFinanciacionJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Crea el nuevo ModeleoTipoFinanciacion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty());
  }

  @Test
  @WithMockUser(username = "user")
  public void create_WithId_Returns400() throws Exception {

    // given: Un TipoFinanciacion que produce un error al crearse porque ya tiene id
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L);

    String tipoFinanciacionJson = mapper.writeValueAsString(tipoFinanciacion);
    BDDMockito.given(tipoFinanciacionService.create(ArgumentMatchers.<TipoFinanciacion>any()))
        .willThrow(new IllegalArgumentException());
    // when: Creamos un TipoFinanciacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(TIPO_FINANCIACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(tipoFinanciacionJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user")
  public void update_ReturnsTipoFinanciacion() throws Exception {
    // given: Un TipoFinanciacion a modificar
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L);
    String tipoFinanciacionJson = mapper.writeValueAsString(tipoFinanciacion);
    TipoFinanciacion tipoFinanciacionSinModificar = generarMockTipoFinanciacion(1L);
    BDDMockito.given(tipoFinanciacionService.findById(ArgumentMatchers.<Long>any()))
        .willReturn(tipoFinanciacionSinModificar);
    BDDMockito.given(tipoFinanciacionService.update(ArgumentMatchers.<TipoFinanciacion>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));
    // when: Actualizamos el TipoFinanciacion
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_FINANCIACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(tipoFinanciacionJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Modifica el TipoFinanciacion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("nombre-1"));
  }

  @Test
  @WithMockUser(username = "user")
  public void update_WithIdNotExist_ReturnsNotFound() throws Exception {
    // given: Un TipoFinanciacion a modificar
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L);
    String replaceTipoFinanciacionJson = mapper.writeValueAsString(tipoFinanciacion);
    BDDMockito.given(tipoFinanciacionService.update(ArgumentMatchers.<TipoFinanciacion>any()))
        .will((InvocationOnMock invocation) -> {
          throw new TipoFinanciacionNotFoundException(((TipoFinanciacion) invocation.getArgument(0)).getId());
        });

    // when: Actualizamos el TipoFinanciacion
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_FINANCIACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoFinanciacionJson))
        // then: No encuentra el TipoFinanciacion y devuelve un 404
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user")
  public void update_WithIdActivoFalse_ReturnsIllegalArgumentException() throws Exception {
    // given: Un TipoFinanciacion a modificar
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L);
    tipoFinanciacion.setActivo(false);
    String replaceTipoFinanciacionJson = mapper.writeValueAsString(tipoFinanciacion);
    BDDMockito.given(tipoFinanciacionService.update(tipoFinanciacion)).willThrow(new IllegalArgumentException());
    // when: Actualizamos el TipoFinanciacion
    mockMvc
        .perform(MockMvcRequestBuilders.put(TIPO_FINANCIACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceTipoFinanciacionJson))
        // then: No encuentra el TipoFinanciacion y devuelve un 404
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user")
  public void findById_WithExistingId_ReturnsTipoFinanciacion() throws Exception {

    // given: Entidad con un determinado Id
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L);

    BDDMockito.given(tipoFinanciacionService.findById(tipoFinanciacion.getId())).willReturn(tipoFinanciacion);

    // when: Se busca la entidad por ese Id
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_FINANCIACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Se recupera la entidad con el Id
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(tipoFinanciacion.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(tipoFinanciacion.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(tipoFinanciacion.getDescripcion()));
  }

  @Test
  @WithMockUser(username = "user")
  public void findById_WithNoExistingId_Returns404() throws Exception {

    BDDMockito.given(tipoFinanciacionService.findById(ArgumentMatchers.anyLong()))
        .will((InvocationOnMock invocation) -> {
          throw new TipoFinanciacionNotFoundException(invocation.getArgument(0));
        });

    // when: Se busca entidad con ese id
    mockMvc
        .perform(MockMvcRequestBuilders.get(TIPO_FINANCIACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Se produce error porque no encuentra la entidad con ese Id
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user")
  public void disableById_Returns204() throws Exception {
    // given: TipoFinanciacion con el id buscado
    Long idBuscado = 1L;
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L);

    BDDMockito.given(tipoFinanciacionService.disable(ArgumentMatchers.<Long>any())).willReturn(tipoFinanciacion);

    // when: Eliminamos el TipoFinanciacion por su id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(TIPO_FINANCIACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, idBuscado)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * Función que devuelve un objeto TipoFinanciacion
   * 
   * @param id id del TipoFinanciacion
   * @return el objeto TipoFinanciacion
   */
  public TipoFinanciacion generarMockTipoFinanciacion(Long id) {
    return generarMockTipoFinanciacion(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoFinanciacion
   * 
   * @param id     id del TipoFinanciacion
   * @param nombre nombre del TipoFinanciacion
   * @return el objeto TipoFinanciacion
   */
  public TipoFinanciacion generarMockTipoFinanciacion(Long id, String nombre) {

    TipoFinanciacion tipoFinanciacion = new TipoFinanciacion();
    tipoFinanciacion.setId(id);
    tipoFinanciacion.setActivo(true);
    tipoFinanciacion.setNombre(nombre);
    tipoFinanciacion.setDescripcion("descripcion-" + 1);

    return tipoFinanciacion;
  }
}
