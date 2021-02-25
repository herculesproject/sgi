package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.FuenteFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoAmbitoGeograficoNotFoundException;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacion;
import org.crue.hercules.sgi.csp.service.FuenteFinanciacionService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * FuenteFinanciacionControllerTest
 */
@WebMvcTest(FuenteFinanciacionController.class)
public class FuenteFinanciacionControllerTest extends BaseControllerTest {

  @MockBean
  private FuenteFinanciacionService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/fuentefinanciaciones";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C" })
  public void create_ReturnsFuenteFinanciacion() throws Exception {
    // given: new FuenteFinanciacion
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<FuenteFinanciacion>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          FuenteFinanciacion newFuenteFinanciacion = new FuenteFinanciacion();
          BeanUtils.copyProperties(invocation.getArgument(0), newFuenteFinanciacion);
          newFuenteFinanciacion.setId(1L);
          return newFuenteFinanciacion;
        });

    // when: create FuenteFinanciacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(fuenteFinanciacion)))
        .andDo(MockMvcResultHandlers.print())
        // then: new FuenteFinanciacion is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(fuenteFinanciacion.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(fuenteFinanciacion.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("fondoEstructural").value(fuenteFinanciacion.getFondoEstructural()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoAmbitoGeografico.id")
            .value(fuenteFinanciacion.getTipoAmbitoGeografico().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoOrigenFuenteFinanciacion.id")
            .value(fuenteFinanciacion.getTipoOrigenFuenteFinanciacion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(fuenteFinanciacion.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a FuenteFinanciacion with id filled
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<FuenteFinanciacion>any()))
        .willThrow(new IllegalArgumentException());

    // when: create FuenteFinanciacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(fuenteFinanciacion)))
        .andDo(MockMvcResultHandlers.print())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void update_ReturnsFuenteFinanciacion() throws Exception {
    // given: Existing TipoAmbitoGeografico to be updated
    FuenteFinanciacion fuenteFinanciacionExistente = generarMockFuenteFinanciacion(1L);
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(1L);
    fuenteFinanciacion.setNombre("nuevo-nombre");

    BDDMockito.given(service.update(ArgumentMatchers.<FuenteFinanciacion>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update FuenteFinanciacion
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, fuenteFinanciacionExistente.getId())
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(fuenteFinanciacion)))
        .andDo(MockMvcResultHandlers.print())
        // then: FuenteFinanciacion is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(fuenteFinanciacion.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(fuenteFinanciacionExistente.getDescripcion()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("fondoEstructural").value(fuenteFinanciacionExistente.getFondoEstructural()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoAmbitoGeografico.id")
            .value(fuenteFinanciacionExistente.getTipoAmbitoGeografico().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoOrigenFuenteFinanciacion.id")
            .value(fuenteFinanciacionExistente.getTipoOrigenFuenteFinanciacion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(fuenteFinanciacionExistente.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(1L);

    BDDMockito.willThrow(new TipoAmbitoGeograficoNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<FuenteFinanciacion>any());

    // when: update FuenteFinanciacion
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(fuenteFinanciacion)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-B" })
  public void reactivar_WithExistingId_ReturnFuenteFinanciacion() throws Exception {
    // given: existing id
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(1L);
    fuenteFinanciacion.setActivo(false);

    BDDMockito.given(service.enable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      FuenteFinanciacion fuenteFinanciacionDisabled = new FuenteFinanciacion();
      BeanUtils.copyProperties(fuenteFinanciacion, fuenteFinanciacionDisabled);
      fuenteFinanciacionDisabled.setActivo(true);
      return fuenteFinanciacionDisabled;
    });

    // when: reactivar by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, fuenteFinanciacion.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: FuenteFinanciacion is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(fuenteFinanciacion.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(fuenteFinanciacion.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("fondoEstructural").value(fuenteFinanciacion.getFondoEstructural()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoAmbitoGeografico.id")
            .value(fuenteFinanciacion.getTipoAmbitoGeografico().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoOrigenFuenteFinanciacion.id")
            .value(fuenteFinanciacion.getTipoOrigenFuenteFinanciacion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-B" })
  public void reactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new FuenteFinanciacionNotFoundException(id)).given(service)
        .enable(ArgumentMatchers.<Long>any());

    // when: reactivar by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-B" })
  public void desactivar_WithExistingId_ReturnFuenteFinanciacion() throws Exception {
    // given: existing id
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(1L);
    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      FuenteFinanciacion fuenteFinanciacionDisabled = new FuenteFinanciacion();
      BeanUtils.copyProperties(fuenteFinanciacion, fuenteFinanciacionDisabled);
      fuenteFinanciacionDisabled.setActivo(false);
      return fuenteFinanciacionDisabled;
    });

    // when: desactivar by id
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, fuenteFinanciacion.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: FuenteFinanciacion is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(fuenteFinanciacion.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(fuenteFinanciacion.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("fondoEstructural").value(fuenteFinanciacion.getFondoEstructural()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoAmbitoGeografico.id")
            .value(fuenteFinanciacion.getTipoAmbitoGeografico().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoOrigenFuenteFinanciacion.id")
            .value(fuenteFinanciacion.getTipoOrigenFuenteFinanciacion().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(false));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-B" })
  public void desactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new FuenteFinanciacionNotFoundException(id)).given(service)
        .disable(ArgumentMatchers.<Long>any());

    // when: desactivar by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TDOC-V" })
  public void findAll_ReturnsPage() throws Exception {
    // given: Una lista con 37 FuenteFinanciacion
    List<FuenteFinanciacion> fuenteFinanciaciones = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      fuenteFinanciaciones.add(generarMockFuenteFinanciacion(i, "FuenteFinanciacion" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > fuenteFinanciaciones.size() ? fuenteFinanciaciones.size() : toIndex;
          List<FuenteFinanciacion> content = fuenteFinanciaciones.subList(fromIndex, toIndex);
          Page<FuenteFinanciacion> pageResponse = new PageImpl<>(content, pageable, fuenteFinanciaciones.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve la pagina 3 con los FuenteFinanciacion del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<FuenteFinanciacion> fuenteFinanciacionesResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<FuenteFinanciacion>>() {
        });

    for (int i = 31; i <= 37; i++) {
      FuenteFinanciacion fuenteFinanciacion = fuenteFinanciacionesResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(fuenteFinanciacion.getNombre()).isEqualTo("FuenteFinanciacion" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TDOC-V" })
  public void findAll_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de FuenteFinanciacion
    List<FuenteFinanciacion> fuenteFinanciaciones = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<FuenteFinanciacion> pageResponse = new PageImpl<>(fuenteFinanciaciones, pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TDOC-V" })
  public void findAllTodos_ReturnsPage() throws Exception {
    // given: Una lista con 37 FuenteFinanciacion
    List<FuenteFinanciacion> fuenteFinanciaciones = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      fuenteFinanciaciones.add(generarMockFuenteFinanciacion(i, "FuenteFinanciacion" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > fuenteFinanciaciones.size() ? fuenteFinanciaciones.size() : toIndex;
          List<FuenteFinanciacion> content = fuenteFinanciaciones.subList(fromIndex, toIndex);
          Page<FuenteFinanciacion> pageResponse = new PageImpl<>(content, pageable, fuenteFinanciaciones.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve la pagina 3 con los FuenteFinanciacion del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<FuenteFinanciacion> fuenteFinanciacionesResponse = mapper
        .readValue(requestResult.getResponse().getContentAsString(), new TypeReference<List<FuenteFinanciacion>>() {
        });

    for (int i = 31; i <= 37; i++) {
      FuenteFinanciacion fuenteFinanciacion = fuenteFinanciacionesResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(fuenteFinanciacion.getNombre()).isEqualTo("FuenteFinanciacion" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TDOC-V" })
  public void findAllTodos_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de FuenteFinanciacion
    List<FuenteFinanciacion> fuenteFinanciaciones = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(service.findAllTodos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<FuenteFinanciacion> pageResponse = new PageImpl<>(fuenteFinanciaciones, pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-V" })
  public void findById_WithExistingId_ReturnsTipoAmbitoGeografico() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockFuenteFinanciacion(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested FuenteFinanciacion is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new FuenteFinanciacionNotFoundException(1L);
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
   * Función que devuelve un objeto FuenteFinanciacion
   * 
   * @param id id del FuenteFinanciacion
   * @return el objeto FuenteFinanciacion
   */
  private FuenteFinanciacion generarMockFuenteFinanciacion(Long id) {
    return generarMockFuenteFinanciacion(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto FuenteFinanciacion
   * 
   * @param id     id del FuenteFinanciacion
   * @param nombre nombre del FuenteFinanciacion
   * @return el objeto FuenteFinanciacion
   */
  private FuenteFinanciacion generarMockFuenteFinanciacion(Long id, String nombre) {
    TipoAmbitoGeografico tipoAmbitoGeografico = new TipoAmbitoGeografico();
    tipoAmbitoGeografico.setId(1L);

    TipoOrigenFuenteFinanciacion tipoOrigenFuenteFinanciacion = new TipoOrigenFuenteFinanciacion();
    tipoOrigenFuenteFinanciacion.setId(1L);

    FuenteFinanciacion fuenteFinanciacion = new FuenteFinanciacion();
    fuenteFinanciacion.setId(id);
    fuenteFinanciacion.setNombre(nombre);
    fuenteFinanciacion.setDescripcion("descripcion-" + id);
    fuenteFinanciacion.setFondoEstructural(true);
    fuenteFinanciacion.setTipoAmbitoGeografico(tipoAmbitoGeografico);
    fuenteFinanciacion.setTipoOrigenFuenteFinanciacion(tipoOrigenFuenteFinanciacion);
    fuenteFinanciacion.setActivo(true);

    return fuenteFinanciacion;
  }

}
