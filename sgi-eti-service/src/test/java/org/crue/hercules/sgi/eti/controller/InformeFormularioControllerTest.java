package org.crue.hercules.sgi.eti.controller;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.InformeFormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.FormularioMemoria;
import org.crue.hercules.sgi.eti.model.InformeFormulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.service.InformeFormularioService;
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
 * InformeFormularioControllerTest
 */
@WebMvcTest(InformeFormularioController.class)
@ActiveProfiles("SECURITY_MOCK")
public class InformeFormularioControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private InformeFormularioService informeFormularioService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String INFORME_FORMULARIO_CONTROLLER_BASE_PATH = "/informeformularios";

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
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-VER" })
  public void getInformeFormulario_WithId_ReturnsInformeFormulario() throws Exception {
    BDDMockito.given(informeFormularioService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockInformeFormulario(1L, "Documento1")));

    mockMvc.perform(MockMvcRequestBuilders.get(INFORME_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("documentoRef").value("Documento1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-VER" })
  public void getInformeFormulario_NotFound_Returns404() throws Exception {
    BDDMockito.given(informeFormularioService.findById(ArgumentMatchers.anyLong()))
        .will((InvocationOnMock invocation) -> {
          throw new InformeFormularioNotFoundException(invocation.getArgument(0));
        });
    mockMvc.perform(MockMvcRequestBuilders.get(INFORME_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-EDITAR" })
  public void newInformeFormulario_ReturnsInformeFormulario() throws Exception {
    // given: Un informeFormulario nuevo
    String nuevoInformeFormularioJson = "{\"documentoRef\": \"Documento1\", \"version\": \"2\"}";

    InformeFormulario informeFormulario = generarMockInformeFormulario(1L, "Documento1");

    BDDMockito.given(informeFormularioService.create(ArgumentMatchers.<InformeFormulario>any()))
        .willReturn(informeFormulario);

    // when: Creamos un informeFormulario
    mockMvc
        .perform(MockMvcRequestBuilders.post(INFORME_FORMULARIO_CONTROLLER_BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON).content(nuevoInformeFormularioJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Crea el nuevo informeFormulario y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("documentoRef").value("Documento1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-EDITAR" })
  public void newInformeFormulario_Error_Returns400() throws Exception {
    // given: Un informeFormulario nuevo que produce un error al crearse
    String nuevoInformeFormularioJson = "{\"documentoRef\": \"Documento1\", \"version\": \"2\"}";

    BDDMockito.given(informeFormularioService.create(ArgumentMatchers.<InformeFormulario>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un informeFormulario
    mockMvc
        .perform(MockMvcRequestBuilders.post(INFORME_FORMULARIO_CONTROLLER_BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON).content(nuevoInformeFormularioJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-EDITAR" })
  public void replaceInformeFormulario_ReturnsInformeFormulario() throws Exception {
    // given: Un informeFormulario a modificar
    String replaceInformeFormularioJson = "{\"id\": 1, \"documentoRef\": \"Documento1\", \"version\": \"2\"}";

    InformeFormulario informeFormulario = generarMockInformeFormulario(1L, "Replace Documento1");

    BDDMockito.given(informeFormularioService.update(ArgumentMatchers.<InformeFormulario>any()))
        .willReturn(informeFormulario);

    mockMvc
        .perform(MockMvcRequestBuilders.put(INFORME_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON).content(replaceInformeFormularioJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Modifica el informeFormulario y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("documentoRef").value("Replace Documento1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-EDITAR" })
  public void replaceInformeFormulario_NotFound() throws Exception {
    // given: Un informeFormulario a modificar
    String replaceInformeFormularioJson = "{\"id\": 1, \"documentoRef\": \"Documento1\", \"version\": \"2\"}";

    BDDMockito.given(informeFormularioService.update(ArgumentMatchers.<InformeFormulario>any()))
        .will((InvocationOnMock invocation) -> {
          throw new InformeFormularioNotFoundException(((InformeFormulario) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(INFORME_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON).content(replaceInformeFormularioJson))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-EDITAR" })
  public void removeInformeFormulario_ReturnsOk() throws Exception {
    BDDMockito.given(informeFormularioService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockInformeFormulario(1L, "Documento1"));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(INFORME_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-VER" })
  public void findAll_Unlimited_ReturnsFullInformeFormularioList() throws Exception {
    // given: One hundred InformeFormulario
    List<InformeFormulario> informeFormularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      informeFormularios.add(generarMockInformeFormulario(Long.valueOf(i), "Documento" + String.format("%03d", i)));
    }

    BDDMockito.given(
        informeFormularioService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(informeFormularios));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(INFORME_FORMULARIO_CONTROLLER_BASE_PATH).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred InformeFormulario
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-VER" })
  public void findAll_WithPaging_ReturnsInformeFormularioSubList() throws Exception {
    // given: One hundred InformeFormulario
    List<InformeFormulario> informeFormularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      informeFormularios.add(generarMockInformeFormulario(Long.valueOf(i), "Documento" + String.format("%03d", i)));
    }

    BDDMockito.given(
        informeFormularioService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<InformeFormulario>>() {
          @Override
          public Page<InformeFormulario> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<InformeFormulario> content = informeFormularios.subList(fromIndex, toIndex);
            Page<InformeFormulario> page = new PageImpl<>(content, pageable, informeFormularios.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(INFORME_FORMULARIO_CONTROLLER_BASE_PATH).header("X-Page", "3")
            .header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked InformeFormularios are returned with the right page
        // information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<InformeFormulario> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<InformeFormulario>>() {
        });

    // containing documentoRef='Documento031' to 'Documento040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      InformeFormulario informeFormulario = actual.get(i);
      Assertions.assertThat(informeFormulario.getDocumentoRef()).isEqualTo("Documento" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-INFORMEFORMULARIO-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredInformeFormularioList() throws Exception {
    // given: One hundred InformeFormulario and a search query
    List<InformeFormulario> informeFormularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      informeFormularios.add(generarMockInformeFormulario(Long.valueOf(i), "Documento" + String.format("%03d", i)));
    }
    String query = "documentoRef~Documento%,id:5";

    BDDMockito.given(
        informeFormularioService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<InformeFormulario>>() {
          @Override
          public Page<InformeFormulario> answer(InvocationOnMock invocation) throws Throwable {
            List<QueryCriteria> queryCriterias = invocation.<List<QueryCriteria>>getArgument(0);

            List<InformeFormulario> content = new ArrayList<>();
            for (InformeFormulario informeFormulario : informeFormularios) {
              boolean add = true;
              for (QueryCriteria queryCriteria : queryCriterias) {
                Field field = ReflectionUtils.findField(InformeFormulario.class, queryCriteria.getKey());
                field.setAccessible(true);
                String fieldValue = ReflectionUtils.getField(field, informeFormulario).toString();
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
                content.add(informeFormulario);
              }
            }
            Page<InformeFormulario> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(INFORME_FORMULARIO_CONTROLLER_BASE_PATH).param("q", query)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred InformeFormulario
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto InformeFormulario
   * 
   * @param id           id del InformeFormulario
   * @param documentoRef la referencia del documento
   * @return el objeto InformeFormulario
   */

  public InformeFormulario generarMockInformeFormulario(Long id, String documentoRef) {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo1");
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico1");
    peticionEvaluacion.setExterno(Boolean.FALSE);
    peticionEvaluacion.setFechaFin(LocalDate.now());
    peticionEvaluacion.setFechaInicio(LocalDate.now());
    peticionEvaluacion.setFuenteFinanciacionRef("Referencia fuente financiacion");
    peticionEvaluacion.setObjetivos("Objetivos1");
    peticionEvaluacion.setOtroValorSocial("Otro valor social1");
    peticionEvaluacion.setResumen("Resumen");
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo("PeticionEvaluacion1");
    peticionEvaluacion.setUsuarioRef("user-001");
    peticionEvaluacion.setValorSocial(3);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(id);
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    Memoria memoria = new Memoria(1L, "numRef-001", peticionEvaluacion, comite, "Memoria1", "user-001", tipoMemoria,
        LocalDate.now(), Boolean.FALSE, LocalDate.now(), 3, new TipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE));
    Formulario formulario = new Formulario(1L, "FORM-1", "Formulario1", Boolean.TRUE);
    FormularioMemoria formularioMemoria = new FormularioMemoria(1L, memoria, formulario, Boolean.TRUE);

    InformeFormulario informeFormulario = new InformeFormulario();
    informeFormulario.setId(id);
    informeFormulario.setDocumentoRef(documentoRef);
    informeFormulario.setFormularioMemoria(formularioMemoria);
    informeFormulario.setVersion(3);

    return informeFormulario;
  }

}
