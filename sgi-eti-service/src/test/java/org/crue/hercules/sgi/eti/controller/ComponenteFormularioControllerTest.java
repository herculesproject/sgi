package org.crue.hercules.sgi.eti.controller;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ComponenteFormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.ComponenteFormulario;
import org.crue.hercules.sgi.eti.service.ComponenteFormularioService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.framework.security.web.SgiAuthenticationEntryPoint;
import org.crue.hercules.sgi.framework.security.web.access.SgiAccessDeniedHandler;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ReflectionUtils;

/**
 * ComponenteFormularioControllerTest
 */
@WebMvcTest(ComponenteFormularioController.class)
public class ComponenteFormularioControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private ComponenteFormularioService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH = "/componenteformularios";

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
  @WithMockUser(username = "user", authorities = { "ETI-COMPONENTEFORMULARIO-EDITAR" })
  public void create_ReturnsComponenteFormulario() throws Exception {

    // given: Nueva entidad sin Id
    final String url = new StringBuilder(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH).toString();

    ComponenteFormulario response = getMockData(1L);
    String nuevoComponenteFormularioJson = mapper.writeValueAsString(response);

    BDDMockito.given(service.create(ArgumentMatchers.<ComponenteFormulario>any())).willReturn(response);

    // when: Se crea la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoComponenteFormularioJson))
        .andDo(MockMvcResultHandlers.print())
        // then: La entidad se crea correctamente
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(response.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("esquema").value(response.getEsquema()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMPONENTEFORMULARIO-EDITAR" })
  public void create_WithId_Returns400() throws Exception {

    // given: Nueva entidad con Id
    final String url = new StringBuilder(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH).toString();
    String nuevoComponenteFormularioJson = mapper.writeValueAsString(getMockData(1L));

    BDDMockito.given(service.create(ArgumentMatchers.<ComponenteFormulario>any()))
        .willThrow(new IllegalArgumentException());

    // when: Se crea la entidad
    // then: Se produce error porque ya tiene Id
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoComponenteFormularioJson))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMPONENTEFORMULARIO-EDITAR" })
  public void update_WithExistingId_ReturnsComponenteFormulario() throws Exception {

    // given: Entidad existente que se va a actualizar
    ComponenteFormulario response = getMockData(1L);
    final String url = new StringBuilder(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();
    String replaceComponenteFormularioJson = mapper.writeValueAsString(response);

    BDDMockito.given(service.update(ArgumentMatchers.<ComponenteFormulario>any())).willReturn(response);

    // when: Se actualiza la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.put(url, response.getId()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceComponenteFormularioJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Los datos se actualizan correctamente
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(response.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("esquema").value(response.getEsquema()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMPONENTEFORMULARIO-EDITAR" })
  public void update_WithNoExistingId_Returns404() throws Exception {

    // given: Entidad a actualizar que no existe
    ComponenteFormulario response = getMockData(1L);
    final String url = new StringBuilder(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();
    String replaceComponenteFormularioJson = mapper.writeValueAsString(response);

    BDDMockito.given(service.update(ArgumentMatchers.<ComponenteFormulario>any()))
        .will((InvocationOnMock invocation) -> {
          throw new ComponenteFormularioNotFoundException(((ComponenteFormulario) invocation.getArgument(0)).getId());
        });

    // when: Se actualiza la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.put(url, response.getId()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceComponenteFormularioJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Se produce error porque no encuentra la entidad a actualizar
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMPONENTEFORMULARIO-EDITAR" })
  public void delete_WithExistingId_Return204() throws Exception {

    // given: Entidad existente
    ComponenteFormulario response = getMockData(1L);
    final String url = new StringBuilder(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willReturn(response);

    // when: Se elimina la entidad
    mockMvc.perform(MockMvcRequestBuilders.delete(url, response.getId()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: La entidad se elimina correctamente
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMPONENTEFORMULARIO-EDITAR" })
  public void delete_WithNoExistingId_Returns404() throws Exception {

    // given: Id de una entidad que no existe
    ComponenteFormulario ComponenteFormulario = getMockData(1L);
    final String url = new StringBuilder(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();

    BDDMockito.willThrow(new ComponenteFormularioNotFoundException(0L)).given(service)
        .delete(ArgumentMatchers.<Long>any());

    // when: Se elimina la entidad
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(url, ComponenteFormulario.getId()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Se produce error porque no encuentra la entidad a eliminar
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMPONENTEFORMULARIO-VER" })
  public void findById_WithExistingId_ReturnsComponenteFormulario() throws Exception {

    // given: Entidad con un determinado Id
    ComponenteFormulario response = getMockData(1L);
    final String url = new StringBuilder(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();

    BDDMockito.given(service.findById(response.getId())).willReturn(response);

    // when: Se busca la entidad por ese Id
    mockMvc.perform(MockMvcRequestBuilders.get(url, response.getId()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Se recupera la entidad con el Id
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(response.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("esquema").value(response.getEsquema()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMPONENTEFORMULARIO-VER" })
  public void findById_WithNoExistingId_Returns404() throws Exception {

    // given: No existe entidad con el id indicado
    ComponenteFormulario response = getMockData(1L);

    final String url = new StringBuilder(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ComponenteFormularioNotFoundException(invocation.getArgument(0));
    });

    // when: Se busca entidad con ese id
    mockMvc.perform(MockMvcRequestBuilders.get(url, response.getId()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Se produce error porque no encuentra la entidad con ese Id
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMPONENTEFORMULARIO-VER" })
  public void findAll_Unlimited_ReturnsFullComponenteFormularioList() throws Exception {

    // given: Datos existentes
    final String url = new StringBuilder(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH).toString();

    List<ComponenteFormulario> response = new LinkedList<ComponenteFormulario>();
    response.add(getMockData(1L));
    response.add(getMockData(2L));

    BDDMockito.given(service.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(response));

    // when: Se buscan todos los datos
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Se recuperan todos los datos
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2))).andReturn();

    Assertions.assertThat(mapper.readValue(result.getResponse().getContentAsString(Charset.forName("UTF-8")),
        new TypeReference<List<ComponenteFormulario>>() {
        })).isEqualTo(response);

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMPONENTEFORMULARIO-VER" })
  public void findAll_Unlimited_Returns204() throws Exception {

    // given: No hay datos
    final String url = new StringBuilder(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH).toString();

    BDDMockito.given(service.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));
    // when: Se buscan todos los datos
    mockMvc.perform(MockMvcRequestBuilders.get(url)).andDo(MockMvcResultHandlers.print())
        // then: Se recupera lista vacía);
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMPONENTEFORMULARIO-VER" })
  public void findAll_WithPaging_ReturnsComponenteFormularioSubList() throws Exception {

    // given: Datos existentes
    String url = new StringBuilder(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH).toString();

    List<ComponenteFormulario> response = new LinkedList<>();
    response.add(getMockData(1L));
    response.add(getMockData(2L));
    response.add(getMockData(3L));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<ComponenteFormulario> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(service.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(pageResponse);

    // when: Se buscan todos los datos paginados
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get(url).header("X-Page", pageable.getPageNumber())
            .header("X-Page-Size", pageable.getPageSize()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Se recuperan los datos correctamente según la paginación solicitada
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", String.valueOf(pageable.getPageNumber())))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", String.valueOf(pageable.getPageSize())))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", String.valueOf(response.size())))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1))).andReturn();

    Assertions.assertThat(mapper.readValue(result.getResponse().getContentAsString(Charset.forName("UTF-8")),
        new TypeReference<List<ComponenteFormulario>>() {
        })).isEqualTo(response.subList(2, 3));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMPONENTEFORMULARIO-VER" })
  public void findAll_WithPaging_Returns204() throws Exception {

    // given: Datos existentes
    String url = new StringBuilder(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH).toString();

    List<ComponenteFormulario> response = new LinkedList<ComponenteFormulario>();
    Pageable pageable = PageRequest.of(1, 2);
    Page<ComponenteFormulario> pageResponse = new PageImpl<>(response, pageable, response.size());

    BDDMockito.given(service.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(pageResponse);

    // when: Se buscan todos los datos paginados
    mockMvc
        .perform(MockMvcRequestBuilders.get(url).header("X-Page", pageable.getPageNumber())
            .header("X-Page-Size", pageable.getPageSize()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Se recupera lista de datos paginados vacía
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMPONENTEFORMULARIO-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredComponenteFormularioList() throws Exception {

    // given: Datos existentes
    List<ComponenteFormulario> response = new LinkedList<>();
    response.add(getMockData(1L));
    response.add(getMockData(2L));
    response.add(getMockData(3L));
    response.add(getMockData(4L));
    response.add(getMockData(5L));

    final String url = new StringBuilder(COMPONENTE_FORMULARIO_CONTROLLER_BASE_PATH).toString();

    // search
    String query = "esquema~EsquemaComponenteFormulario0%,id:3";

    BDDMockito.given(service.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ComponenteFormulario>>() {
          @Override
          public Page<ComponenteFormulario> answer(InvocationOnMock invocation) throws Throwable {
            List<QueryCriteria> queryCriterias = invocation.<List<QueryCriteria>>getArgument(0);

            List<ComponenteFormulario> content = new LinkedList<>();
            for (ComponenteFormulario item : response) {
              boolean add = true;
              for (QueryCriteria queryCriteria : queryCriterias) {
                Field field = ReflectionUtils.findField(ComponenteFormulario.class, queryCriteria.getKey());
                field.setAccessible(true);
                String fieldValue = ReflectionUtils.getField(field, item).toString();
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
                content.add(item);
              }
            }
            Page<ComponenteFormulario> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: Se buscan los datos con el filtro indicado
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get(url).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Se recuperan los datos filtrados
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1))).andReturn();

    Assertions.assertThat(mapper.readValue(result.getResponse().getContentAsString(Charset.forName("UTF-8")),
        new TypeReference<List<ComponenteFormulario>>() {
        })).isEqualTo(response.subList(2, 3));
  }

  /**
   * Genera un objeto {@link ComponenteFormulario}
   * 
   * @param id
   * @return ComponenteFormulario
   */
  private ComponenteFormulario getMockData(Long id) {

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final ComponenteFormulario data = new ComponenteFormulario();
    data.setId(id);
    data.setEsquema("EsquemaComponenteFormulario" + txt);

    return data;
  }
}
