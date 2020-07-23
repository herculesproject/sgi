package org.crue.hercules.sgi.eti.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ActaNotFoundException;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
import org.crue.hercules.sgi.eti.service.ActaService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ReflectionUtils;

/**
 * ActaControllerTest
 */
@WebMvcTest(ActaController.class)
@ActiveProfiles("SECURITY_MOCK")
public class ActaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private ActaService actaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String ACTA_CONTROLLER_BASE_PATH = "/actas";

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
  @WithMockUser(username = "user", authorities = { "ETI-ACTA-VER" })
  public void getActa_WithId_ReturnsActa() throws Exception {
    BDDMockito.given(actaService.findById(ArgumentMatchers.anyLong())).willReturn((generarMockActa(1L, 123)));

    mockMvc.perform(MockMvcRequestBuilders.get(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaReunion.id").value(100))
        .andExpect(MockMvcResultMatchers.jsonPath("horaInicio").value(10))
        .andExpect(MockMvcResultMatchers.jsonPath("minutoInicio").value(15))
        .andExpect(MockMvcResultMatchers.jsonPath("horaFin").value(12))
        .andExpect(MockMvcResultMatchers.jsonPath("minutoFin").value(0))
        .andExpect(MockMvcResultMatchers.jsonPath("resumen").value("Resumen123"))
        .andExpect(MockMvcResultMatchers.jsonPath("numero").value(123))
        .andExpect(MockMvcResultMatchers.jsonPath("estadoActual.id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("inactiva").value(true))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACTA-VER" })
  public void getActa_NotFound_Returns404() throws Exception {
    BDDMockito.given(actaService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ActaNotFoundException(invocation.getArgument(0));
    });
    mockMvc.perform(MockMvcRequestBuilders.get(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACTA-EDITAR" })
  public void newActa_ReturnsActa() throws Exception {
    // given: Un acta nuevo
    String nuevoActaJson = "{\"convocatoriaReunion\": {\"id\": 100}, \"horaInicio\": 10, \"minutoInicio\": 15, \"horaFin\": 12, \"minutoFin\": 0, \"resumen\": \"Resumen123\", \"numero\": 123, \"estadoActual\": {\"id\": 1}, \"inactiva\": true, \"activo\": true}";

    Acta acta = generarMockActa(1L, 123);

    BDDMockito.given(actaService.create(ArgumentMatchers.<Acta>any())).willReturn(acta);

    // when: Creamos un acta
    mockMvc
        .perform(MockMvcRequestBuilders.post(ACTA_CONTROLLER_BASE_PATH).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoActaJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Crea el nuevo acta y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaReunion.id").value(100))
        .andExpect(MockMvcResultMatchers.jsonPath("horaInicio").value(10))
        .andExpect(MockMvcResultMatchers.jsonPath("minutoInicio").value(15))
        .andExpect(MockMvcResultMatchers.jsonPath("horaFin").value(12))
        .andExpect(MockMvcResultMatchers.jsonPath("minutoFin").value(0))
        .andExpect(MockMvcResultMatchers.jsonPath("resumen").value("Resumen123"))
        .andExpect(MockMvcResultMatchers.jsonPath("numero").value(123))
        .andExpect(MockMvcResultMatchers.jsonPath("estadoActual.id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("inactiva").value(true))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACTA-EDITAR" })
  public void newActa_Error_Returns400() throws Exception {
    // given: Un acta nuevo que produce un error al crearse
    String nuevoActaJson = "{\"id\": 1, \"convocatoriaReunion\": {\"id\": 100}, \"horaInicio\": 10, \"minutoInicio\": 15, \"horaFin\": 12, \"minutoFin\": 0, \"resumen\": \"Resumen123\", \"numero\": 123, \"estadoActual\": {\"id\": 1}, \"inactiva\": true, \"activo\": true}";

    BDDMockito.given(actaService.create(ArgumentMatchers.<Acta>any())).willThrow(new IllegalArgumentException());

    // when: Creamos un acta
    mockMvc
        .perform(MockMvcRequestBuilders.post(ACTA_CONTROLLER_BASE_PATH).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoActaJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACTA-EDITAR" })
  public void replaceActa_ReturnsActa() throws Exception {
    // given: Un acta a modificar
    String replaceActaJson = "{\"id\": 1, \"convocatoriaReunion\": {\"id\": 100}, \"horaInicio\": 10, \"minutoInicio\": 15, \"horaFin\": 12, \"minutoFin\": 0, \"resumen\": \"Resumen123\", \"numero\": 123, \"estadoActual\": {\"id\": 1}, \"inactiva\": true, \"activo\": true}";

    Acta actaActualizado = generarMockActa(1L, 456);

    BDDMockito.given(actaService.update(ArgumentMatchers.<Acta>any())).willReturn(actaActualizado);

    mockMvc
        .perform(MockMvcRequestBuilders.put(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON).content(replaceActaJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Modifica el acta y lo devuelve
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaReunion.id").value(100))
        .andExpect(MockMvcResultMatchers.jsonPath("horaInicio").value(10))
        .andExpect(MockMvcResultMatchers.jsonPath("minutoInicio").value(15))
        .andExpect(MockMvcResultMatchers.jsonPath("horaFin").value(12))
        .andExpect(MockMvcResultMatchers.jsonPath("minutoFin").value(0))
        .andExpect(MockMvcResultMatchers.jsonPath("resumen").value("Resumen456"))
        .andExpect(MockMvcResultMatchers.jsonPath("numero").value(456))
        .andExpect(MockMvcResultMatchers.jsonPath("estadoActual.id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("inactiva").value(true))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACTA-EDITAR" })
  public void replaceActa_NotFound() throws Exception {
    // given: Un acta a modificar
    String replaceActaJson = "{\"id\": 1, \"convocatoriaReunion\": {\"id\": 100}, \"horaInicio\": 10, \"minutoInicio\": 15, \"horaFin\": 12, \"minutoFin\": 0, \"resumen\": \"Resumen123\", \"numero\": 123, \"estadoActual\": {\"id\": 1}, \"inactiva\": true, \"activo\": true}";

    BDDMockito.given(actaService.update(ArgumentMatchers.<Acta>any())).will((InvocationOnMock invocation) -> {
      throw new ActaNotFoundException(((Acta) invocation.getArgument(0)).getId());
    });
    mockMvc
        .perform(MockMvcRequestBuilders.put(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON).content(replaceActaJson))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACTA-EDITAR" })
  public void removeActa_ReturnsOk() throws Exception {
    BDDMockito.given(actaService.findById(ArgumentMatchers.anyLong())).willReturn(generarMockActa(1L, 123));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACTA-VER" })
  public void findAll_Unlimited_ReturnsFullActaList() throws Exception {
    // given: One hundred actas
    List<Acta> actas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      actas.add(generarMockActa(Long.valueOf(i), i));
    }

    BDDMockito.given(actaService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(actas));

    // when: find unlimited
    mockMvc.perform(MockMvcRequestBuilders.get(ACTA_CONTROLLER_BASE_PATH).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred actas
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACTA-VER" })
  public void findAll_WithPaging_ReturnsActaSubList() throws Exception {
    // given: One hundred actas
    List<Acta> actas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      actas.add(generarMockActa(Long.valueOf(i), i));
    }

    BDDMockito.given(actaService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Acta>>() {
          @Override
          public Page<Acta> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Acta> content = actas.subList(fromIndex, toIndex);
            Page<Acta> page = new PageImpl<>(content, pageable, actas.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(ACTA_CONTROLLER_BASE_PATH).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked actas are returned with the right page information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Acta> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Acta>>() {
        });

    // containing id='31' to '40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Acta acta = actual.get(i);
      Assertions.assertThat(acta.getId()).isEqualTo(j);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACTA-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredActaList() throws Exception {
    // given: One hundred actas and a search query
    List<Acta> actas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      actas.add(generarMockActa(Long.valueOf(i), 123));
    }
    String query = "id:5";

    BDDMockito.given(actaService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Acta>>() {
          @Override
          public Page<Acta> answer(InvocationOnMock invocation) throws Throwable {
            List<QueryCriteria> queryCriterias = invocation.<List<QueryCriteria>>getArgument(0);

            List<Acta> content = new ArrayList<>();
            for (Acta acta : actas) {
              boolean add = true;
              for (QueryCriteria queryCriteria : queryCriterias) {
                Field field = ReflectionUtils.findField(Acta.class, queryCriteria.getKey());
                field.setAccessible(true);
                String fieldValue = ReflectionUtils.getField(field, acta).toString();
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
                content.add(acta);
              }
            }
            Page<Acta> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(ACTA_CONTROLLER_BASE_PATH).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one acta
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto Acta
   * 
   * @param id     id del acta
   * @param numero numero del acta
   * @return el objeto Acta
   */
  public Acta generarMockActa(Long id, Integer numero) {
    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(100L);

    TipoEstadoActa tipoEstadoActa = new TipoEstadoActa();
    tipoEstadoActa.setId(id);
    tipoEstadoActa.setNombre("En elaboración");
    tipoEstadoActa.setActivo(Boolean.TRUE);

    Acta acta = new Acta();
    acta.setId(id);
    acta.setConvocatoriaReunion(convocatoriaReunion);
    acta.setHoraInicio(10);
    acta.setMinutoInicio(15);
    acta.setHoraFin(12);
    acta.setMinutoFin(0);
    acta.setResumen("Resumen" + numero);
    acta.setNumero(numero);
    acta.setEstadoActual(tipoEstadoActa);
    acta.setInactiva(true);
    acta.setActivo(true);

    return acta;
  }

}
