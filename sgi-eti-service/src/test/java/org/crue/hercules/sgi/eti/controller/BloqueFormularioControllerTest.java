package org.crue.hercules.sgi.eti.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.BloqueFormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.BloqueFormulario;
import org.crue.hercules.sgi.eti.service.BloqueFormularioService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ReflectionUtils;

import org.crue.hercules.sgi.framework.security.web.SgiAuthenticationEntryPoint;
import org.crue.hercules.sgi.framework.security.web.access.SgiAccessDeniedHandler;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.context.ActiveProfiles;

/**
 * BloqueFormularioControllerTest
 */
@WebMvcTest(BloqueFormularioController.class)
@ActiveProfiles("SECURITY_MOCK")
public class BloqueFormularioControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private BloqueFormularioService bloqueFormularioService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH = "/bloqueformularios";

  @Profile("SECURITY_MOCK") // If we use the SECURITY_MOCK profile, we use this bean!
  @TestConfiguration // Unlike a nested @Configuration class, which would be used instead of your
                     // application’s primary configuration, a nested @TestConfiguration class is
                     // used in addition to your application’s primary configuration.
  static class TestSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.csrf().disable() //
          .authorizeRequests().antMatchers("/error").permitAll() //
          .antMatchers("/**").authenticated() //
          .anyRequest().denyAll() //
          .and() //
          .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
          .authenticationEntryPoint(authenticationEntryPoint) //
          .and() //
          .httpBasic();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(ObjectMapper mapper) {
      return new SgiAccessDeniedHandler(mapper);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper mapper) {
      return new SgiAuthenticationEntryPoint(mapper);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-BLOQUEFORMULARIO-VER" })
  public void getBloqueFormulario_WithId_ReturnsBloqueFormulario() throws Exception {
    BDDMockito.given(bloqueFormularioService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockBloqueFormulario(1L, "BloqueFormulario1")));

    mockMvc.perform(MockMvcRequestBuilders.get(BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("BloqueFormulario1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-BLOQUEFORMULARIO-VER" })
  public void getBloqueFormulario_NotFound_Returns404() throws Exception {
    BDDMockito.given(bloqueFormularioService.findById(ArgumentMatchers.anyLong()))
        .will((InvocationOnMock invocation) -> {
          throw new BloqueFormularioNotFoundException(invocation.getArgument(0));
        });
    mockMvc.perform(MockMvcRequestBuilders.get(BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-BLOQUEFORMULARIO-EDITAR" })
  public void newBloqueFormulario_ReturnsBloqueFormulario() throws Exception {
    // given: Un bloqueFormulario nuevo
    String nuevoBloqueFormularioJson = "{\"nombre\": \"BloqueFormulario1\", \"activo\": \"true\",\"orden\": 1}";

    BloqueFormulario bloqueFormulario = generarMockBloqueFormulario(1L, "BloqueFormulario1");

    BDDMockito.given(bloqueFormularioService.create(ArgumentMatchers.<BloqueFormulario>any()))
        .willReturn(bloqueFormulario);

    // when: Creamos un bloqueFormulario
    mockMvc
        .perform(MockMvcRequestBuilders.post(BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON).content(nuevoBloqueFormularioJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Crea el nuevo bloqueFormulario y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("BloqueFormulario1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-BLOQUEFORMULARIO-EDITAR" })
  public void newBloqueFormulario_Error_Returns400() throws Exception {
    // given: Un bloqueFormulario nuevo que produce un error al crearse
    String nuevoBloqueFormularioJson = "{\"nombre\": \"BloqueFormulario1\", \"activo\": \"true\"}";

    BDDMockito.given(bloqueFormularioService.create(ArgumentMatchers.<BloqueFormulario>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un bloqueFormulario
    mockMvc
        .perform(MockMvcRequestBuilders.post(BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON).content(nuevoBloqueFormularioJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-BLOQUEFORMULARIO-EDITAR" })
  public void replaceBloqueFormulario_ReturnsBloqueFormulario() throws Exception {
    // given: Un bloqueFormulario a modificar
    String replaceBloqueFormularioJson = "{\"id\": 1, \"nombre\": \"BloqueFormulario1\", \"orden\": 1, \"activo\": true}";

    BloqueFormulario bloqueFormulario = generarMockBloqueFormulario(1L, "Replace BloqueFormulario1");

    BDDMockito.given(bloqueFormularioService.update(ArgumentMatchers.<BloqueFormulario>any()))
        .willReturn(bloqueFormulario);

    mockMvc
        .perform(MockMvcRequestBuilders.put(BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON).content(replaceBloqueFormularioJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Modifica el bloqueFormulario y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("Replace BloqueFormulario1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-BLOQUEFORMULARIO-EDITAR" })
  public void replaceBloqueFormulario_NotFound() throws Exception {
    // given: Un bloqueFormulario a modificar
    String replaceBloqueFormularioJson = "{\"id\": 1, \"nombre\": \"BloqueFormulario1\", \"orden\": 1 ,\"activo\": \"true\"}";

    BDDMockito.given(bloqueFormularioService.update(ArgumentMatchers.<BloqueFormulario>any()))
        .will((InvocationOnMock invocation) -> {
          throw new BloqueFormularioNotFoundException(((BloqueFormulario) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON).content(replaceBloqueFormularioJson))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-BLOQUEFORMULARIO-EDITAR" })
  public void removeBloqueFormulario_ReturnsOk() throws Exception {
    BDDMockito.given(bloqueFormularioService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockBloqueFormulario(1L, "BloqueFormulario1"));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-BLOQUEFORMULARIO-VER" })
  public void findAll_Unlimited_ReturnsFullBloqueFormularioList() throws Exception {
    // given: One hundred BloqueFormulario
    List<BloqueFormulario> bloqueFormularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      bloqueFormularios
          .add(generarMockBloqueFormulario(Long.valueOf(i), "BloqueFormulario" + String.format("%03d", i)));
    }

    BDDMockito.given(
        bloqueFormularioService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(bloqueFormularios));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred BloqueFormulario
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-BLOQUEFORMULARIO-VER" })
  public void findAll_WithPaging_ReturnsBloqueFormularioSubList() throws Exception {
    // given: One hundred BloqueFormulario
    List<BloqueFormulario> bloqueFormularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      bloqueFormularios
          .add(generarMockBloqueFormulario(Long.valueOf(i), "BloqueFormulario" + String.format("%03d", i)));
    }

    BDDMockito.given(
        bloqueFormularioService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<BloqueFormulario>>() {
          @Override
          public Page<BloqueFormulario> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<BloqueFormulario> content = bloqueFormularios.subList(fromIndex, toIndex);
            Page<BloqueFormulario> page = new PageImpl<>(content, pageable, bloqueFormularios.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH).header("X-Page", "3")
            .header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked BloqueFormularios are returned with the right page
        // information in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<BloqueFormulario> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<BloqueFormulario>>() {
        });

    // containing nombre='BloqueFormulario031' to 'BloqueFormulario040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      BloqueFormulario bloqueFormulario = actual.get(i);
      Assertions.assertThat(bloqueFormulario.getNombre()).isEqualTo("BloqueFormulario" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-BLOQUEFORMULARIO-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredBloqueFormularioList() throws Exception {
    // given: One hundred BloqueFormulario and a search query
    List<BloqueFormulario> bloqueFormularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      bloqueFormularios
          .add(generarMockBloqueFormulario(Long.valueOf(i), "BloqueFormulario" + String.format("%03d", i)));
    }
    String query = "nombre~BloqueFormulario%,id:5";

    BDDMockito.given(
        bloqueFormularioService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<BloqueFormulario>>() {
          @Override
          public Page<BloqueFormulario> answer(InvocationOnMock invocation) throws Throwable {
            List<QueryCriteria> queryCriterias = invocation.<List<QueryCriteria>>getArgument(0);

            List<BloqueFormulario> content = new ArrayList<>();
            for (BloqueFormulario bloqueFormulario : bloqueFormularios) {
              boolean add = true;
              for (QueryCriteria queryCriteria : queryCriterias) {
                Field field = ReflectionUtils.findField(BloqueFormulario.class, queryCriteria.getKey());
                field.setAccessible(true);
                String fieldValue = ReflectionUtils.getField(field, bloqueFormulario).toString();
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
                content.add(bloqueFormulario);
              }
            }
            Page<BloqueFormulario> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(BLOQUE_FORMULARIO_CONTROLLER_BASE_PATH).param("q", query)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred BloqueFormulario
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto BloqueFormulario
   * 
   * @param id     id del BloqueFormulario
   * @param nombre el nombre de BloqueFormulario
   * @return el objeto BloqueFormulario
   */

  public BloqueFormulario generarMockBloqueFormulario(Long id, String nombre) {

    Formulario formulario = new Formulario();
    formulario.setId(1L);
    formulario.setNombre("Formulario1");
    formulario.setDescripcion("Descripcion formulario 1");
    formulario.setActivo(Boolean.TRUE);

    BloqueFormulario bloqueFormulario = new BloqueFormulario();
    bloqueFormulario.setId(id);
    bloqueFormulario.setFormulario(formulario);
    bloqueFormulario.setNombre(nombre);
    bloqueFormulario.setOrden(1);
    bloqueFormulario.setActivo(Boolean.TRUE);

    return bloqueFormulario;
  }

}
