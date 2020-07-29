package org.crue.hercules.sgi.eti.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.config.SecurityConfig;
import org.crue.hercules.sgi.eti.exceptions.ConfiguracionNotFoundException;
import org.crue.hercules.sgi.eti.model.Configuracion;
import org.crue.hercules.sgi.eti.service.ConfiguracionService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ReflectionUtils;

/**
 * ConfiguracionControllerTest
 */
@WebMvcTest(ConfiguracionController.class)
// Since WebMvcTest is only sliced controller layer for the testing, it would
// not take the security configurations.
@Import(SecurityConfig.class)
public class ConfiguracionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private ConfiguracionService configuracionService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONFIGURACION_CONTROLLER_BASE_PATH = "/configuraciones";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CONFIGURACION-VER" })
  public void getConfiguracion_WithId_ReturnsConfiguracion() throws Exception {
    BDDMockito.given(configuracionService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockConfiguracion(1L, "Configuracion1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(CONFIGURACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("clave").value("Configuracion1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CONFIGURACION-VER" })
  public void getConfiguracion_NotFound_Returns404() throws Exception {
    BDDMockito.given(configuracionService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ConfiguracionNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONFIGURACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CONFIGURACION-EDITAR" })
  public void newConfiguracion_ReturnsConfiguracion() throws Exception {
    // given: Una configuracion nueva
    String nuevoConfiguracionJson = "{ \"clave\": \"Configuracion1\", \"valor\": \"Valor conf1\"}";

    Configuracion configuracion = generarMockConfiguracion(1L, "Configuracion1");

    BDDMockito.given(configuracionService.create(ArgumentMatchers.<Configuracion>any())).willReturn(configuracion);

    // when: Creamos una configuracion
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONFIGURACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoConfiguracionJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Crea la nueva configuracion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("clave").value("Configuracion1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CONFIGURACION-EDITAR" })
  public void newConfiguracion_Error_Returns400() throws Exception {
    // given: Una configuracion nueva que produce un error al crearse
    String nuevoConfiguracionJson = "{ \"clave\": \"Configuracion1\", \"valor\": \"Valor conf1\"}";

    BDDMockito.given(configuracionService.create(ArgumentMatchers.<Configuracion>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un configuracion
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONFIGURACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoConfiguracionJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CONFIGURACION-EDITAR" })
  public void replaceConfiguracion_ReturnsConfiguracion() throws Exception {
    // given: Una configuracion a modificar
    String replaceConfiguracionJson = "{\"id\": 1,  \"clave\": \"Configuracion1\", \"valor\": \"Valor conf1\"}";

    Configuracion configuracion = generarMockConfiguracion(1L, "Configuracion Replace");

    BDDMockito.given(configuracionService.update(ArgumentMatchers.<Configuracion>any())).willReturn(configuracion);

    mockMvc
        .perform(MockMvcRequestBuilders.put(CONFIGURACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceConfiguracionJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Modifica la configuracion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("clave").value("Configuracion Replace"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CONFIGURACION-EDITAR" })
  public void replaceConfiguracion_NotFound() throws Exception {
    // given: Una configuracion a modificar
    String replaceConfiguracionJson = "{\"id\": 1, \"clave\": \"Configuracion1\", \"valor\": \"Valor conf1\"}";
    BDDMockito.given(configuracionService.update(ArgumentMatchers.<Configuracion>any()))
        .will((InvocationOnMock invocation) -> {
          throw new ConfiguracionNotFoundException(((Configuracion) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONFIGURACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceConfiguracionJson))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CONFIGURACION-EDITAR" })
  public void removeConfiguracion_ReturnsOk() throws Exception {
    BDDMockito.given(configuracionService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockConfiguracion(1L, null));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONFIGURACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CONFIGURACION-VER" })
  public void findAll_Unlimited_ReturnsFullConfiguracionList() throws Exception {
    // given: One hundred Configuracion
    List<Configuracion> configuraciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      configuraciones.add(generarMockConfiguracion(Long.valueOf(i), "Configuracion" + String.format("%03d", i)));
    }

    BDDMockito
        .given(
            configuracionService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(configuraciones));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONFIGURACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred Configuracion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CONFIGURACION-VER" })
  public void findAll_WithPaging_ReturnsConfiguracionSubList() throws Exception {
    // given: One hundred Configuracion
    List<Configuracion> configuraciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      configuraciones.add(generarMockConfiguracion(Long.valueOf(i), "Configuracion" + String.format("%03d", i)));
    }

    BDDMockito
        .given(
            configuracionService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Configuracion>>() {
          @Override
          public Page<Configuracion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Configuracion> content = configuraciones.subList(fromIndex, toIndex);
            Page<Configuracion> page = new PageImpl<>(content, pageable, configuraciones.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONFIGURACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked Configuraciones are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Configuracion> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Configuracion>>() {
        });

    // containing clave='Configuracion031' to 'Configuracion040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Configuracion configuracion = actual.get(i);
      Assertions.assertThat(configuracion.getClave()).isEqualTo("Configuracion" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-CONFIGURACION-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredConfiguracionList() throws Exception {
    // given: One hundred Configuracion and a search query
    List<Configuracion> configuraciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      configuraciones.add(generarMockConfiguracion(Long.valueOf(i), "Configuracion" + String.format("%03d", i)));
    }
    String query = "clave~Configuracion%,id:5";

    BDDMockito
        .given(
            configuracionService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Configuracion>>() {
          @Override
          public Page<Configuracion> answer(InvocationOnMock invocation) throws Throwable {
            List<QueryCriteria> queryCriterias = invocation.<List<QueryCriteria>>getArgument(0);

            List<Configuracion> content = new ArrayList<>();
            for (Configuracion configuracion : configuraciones) {
              boolean add = true;
              for (QueryCriteria queryCriteria : queryCriterias) {
                Field field = ReflectionUtils.findField(Configuracion.class, queryCriteria.getKey());
                field.setAccessible(true);
                String fieldValue = ReflectionUtils.getField(field, configuracion).toString();
                switch (queryCriteria.getOperation()) {
                  case EQUALS:
                    if (!fieldValue.equals(queryCriteria.getValue())) {
                      add = false;
                    }
                    break;
                  case GREATER:
                    if (!(fieldValue.compareTo(queryCriteria.getValue().toString()) > 0)) {
                      add = false;
                    }
                    break;
                  case GREATER_OR_EQUAL:
                    if (!(fieldValue.compareTo(queryCriteria.getValue().toString()) >= 0)) {
                      add = false;
                    }
                    break;
                  case LIKE:
                    if (!fieldValue.matches((queryCriteria.getValue().toString().replaceAll("%", ".*")))) {
                      add = false;
                    }
                    break;
                  case LOWER:
                    if (!(fieldValue.compareTo(queryCriteria.getValue().toString()) < 0)) {
                      add = false;
                    }
                    break;
                  case LOWER_OR_EQUAL:
                    if (!(fieldValue.compareTo(queryCriteria.getValue().toString()) <= 0)) {
                      add = false;
                    }
                    break;
                  case NOT_EQUALS:
                    if (fieldValue.equals(queryCriteria.getValue())) {
                      add = false;
                    }
                    break;
                  case NOT_LIKE:
                    if (fieldValue.matches((queryCriteria.getValue().toString().replaceAll("%", ".*")))) {
                      add = false;
                    }
                    break;
                  default:
                    break;
                }
              }
              if (add) {
                content.add(configuracion);
              }
            }
            Page<Configuracion> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONFIGURACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred Configuracion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto Configuracion
   * 
   * @param id    id del Configuracion
   * @param clave la clave de la Configuracion
   * @return el objeto Configuracion
   */

  public Configuracion generarMockConfiguracion(Long id, String clave) {

    Configuracion configuracion = new Configuracion();
    configuracion.setId(id);
    configuracion.setClave(clave);
    configuracion.setDescripcion("Descripcion" + id);
    configuracion.setValor("Valor" + id);

    return configuracion;
  }

}
