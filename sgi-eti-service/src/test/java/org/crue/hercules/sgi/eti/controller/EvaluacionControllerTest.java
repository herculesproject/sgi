package org.crue.hercules.sgi.eti.controller;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.service.EvaluacionService;
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
 * EvaluacionControllerTest
 */
@WebMvcTest(EvaluacionController.class)
@ActiveProfiles("SECURITY_MOCK")
public class EvaluacionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private EvaluacionService evaluacionService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String EVALUACION_CONTROLLER_BASE_PATH = "/evaluaciones";

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
  @WithMockUser(username = "user", authorities = { "ETI-EVALUACION-VER" })
  public void getEvaluacion_WithId_ReturnsEvaluacion() throws Exception {
    BDDMockito.given(evaluacionService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockEvaluacion(1L, null)));

    mockMvc.perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("dictamen.nombre").value("Dictamen1"))
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.titulo").value("Memoria1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVALUACION-VER" })
  public void getEvaluacion_NotFound_Returns404() throws Exception {
    BDDMockito.given(evaluacionService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new EvaluacionNotFoundException(invocation.getArgument(0));
    });
    mockMvc.perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVALUACION-EDITAR" })
  public void newEvaluacion_ReturnsEvaluacion() throws Exception {
    // given: Una evaluacion nueva
    String nuevoEvaluacionJson = "{\"memoria\":{\"id\": 1, \"numReferencia\": \"numRef-5598\", \"peticionEvaluacion\": {\"id\": 1, \"titulo\": \"PeticionEvaluacion1\"},"
        + " \"comite\": {\"comite\": \"Comite1\"},\"titulo\": \"Memoria1 replace\", \"numReferencia\": \"userRef-55\", \"fechaEstado\": \"2020-06-09\","
        + "\"tipoMemoria\": {\"id\": 1, \"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}, \"requiereRetrospectiva\": \"false\",\"version\": \"1\"}, \"convocatoriaReunion\": {\"id\": 1},"
        + "\"dictamen\": {\"id\": 1, \"nombre\": \"Dictamen1\", \"activo\": \"true\"}, \"esRevMinima\": \"true\", \"activo\": \"true\",\"version\": \"1\"}";
    Evaluacion evaluacion = generarMockEvaluacion(1L, null);

    BDDMockito.given(evaluacionService.create(ArgumentMatchers.<Evaluacion>any())).willReturn(evaluacion);

    // when: Creamos una evaluacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(EVALUACION_CONTROLLER_BASE_PATH).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoEvaluacionJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Crea la nueva evaluacion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.titulo").value("Memoria1"))
        .andExpect(MockMvcResultMatchers.jsonPath("dictamen.nombre").value("Dictamen1"))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaReunion.codigo").value("CR-1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVALUACION-EDITAR" })
  public void newEvaluacion_Error_Returns400() throws Exception {
    // given: Una evaluacion nueva que produce un error al crearse
    String nuevoEvaluacionJson = "{\"memoria\":{\"id\": 1, \"numReferencia\": \"numRef-5598\", \"peticionEvaluacion\": {\"id\": 1, \"titulo\": \"PeticionEvaluacion1\"},"
        + " \"comite\": {\"comite\": \"Comite1\"},\"titulo\": \"Memoria1 replace\", \"numReferencia\": \"userRef-55\", \"fechaEstado\": \"2020-06-09\","
        + "\"tipoMemoria\": {\"id\": 1, \"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}, \"requiereRetrospectiva\": \"false\",\"version\": \"1\"}, \"dictamen\": {\"id\": 1, \"nombre\": \"Dictamen1\", \"activo\": \"true\"}, \"esRevMinima\": \"true\", \"activo\": \"true\"}";

    BDDMockito.given(evaluacionService.create(ArgumentMatchers.<Evaluacion>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un evaluacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(EVALUACION_CONTROLLER_BASE_PATH).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoEvaluacionJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVALUACION-EDITAR" })
  public void replaceEvaluacion_ReturnsEvaluacion() throws Exception {
    // given: Una evaluacion a modificar
    String replaceEvaluacionJson = "{\"id\": 1, \"memoria\":{\"id\": 1, \"numReferencia\": \"numRef-5598\", \"peticionEvaluacion\": {\"id\": 1, \"titulo\": \"PeticionEvaluacion1\"},"
        + " \"comite\": {\"comite\": \"Comite1\"},\"titulo\": \"Memoria1 replace\", \"numReferencia\": \"userRef-55\", \"fechaEstado\": \"2020-06-09\","
        + "\"tipoMemoria\": {\"id\": 1, \"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}, \"requiereRetrospectiva\": \"false\",\"version\": \"1\"}, \"convocatoriaReunion\": {\"id\": 1},"
        + "\"dictamen\": {\"id\": 1, \"nombre\": \"Dictamen1\", \"activo\": \"true\"}, \"esRevMinima\": \"true\", \"activo\": \"true\",\"version\": \"1\"}";

    Evaluacion evaluacion = generarMockEvaluacion(1L, " Replace");

    BDDMockito.given(evaluacionService.update(ArgumentMatchers.<Evaluacion>any())).willReturn(evaluacion);

    mockMvc
        .perform(MockMvcRequestBuilders.put(EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON).content(replaceEvaluacionJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Modifica la evaluacion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.titulo").value("Memoria Replace"))
        .andExpect(MockMvcResultMatchers.jsonPath("dictamen.nombre").value("Dictamen Replace"))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaReunion.codigo").value("CR- Replace"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVALUACION-EDITAR" })
  public void replaceEvaluacion_NotFound() throws Exception {
    // given: Una evaluacion a modificar
    String replaceEvaluacionJson = "{\"id\": 1, \"memoria\":{\"id\": 1, \"numReferencia\": \"numRef-5598\", \"peticionEvaluacion\": {\"id\": 1, \"titulo\": \"PeticionEvaluacion1\"},"
        + " \"comite\": {\"comite\": \"Comite1\"},\"titulo\": \"Memoria1 replace\", \"numReferencia\": \"userRef-55\", \"fechaEstado\": \"2020-06-09\","
        + "\"tipoMemoria\": {\"id\": 1, \"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}, \"requiereRetrospectiva\": \"false\",\"version\": \"1\"}, \"convocatoriaReunion\": {\"id\": 1},"
        + "\"dictamen\": {\"id\": 1, \"nombre\": \"Dictamen1\", \"activo\": \"true\"}, \"esRevMinima\": \"true\", \"activo\": \"true\",\"version\": \"1\"}";

    BDDMockito.given(evaluacionService.update(ArgumentMatchers.<Evaluacion>any()))
        .will((InvocationOnMock invocation) -> {
          throw new EvaluacionNotFoundException(((Evaluacion) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON).content(replaceEvaluacionJson))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVALUACION-EDITAR" })
  public void removeEvaluacion_ReturnsOk() throws Exception {
    BDDMockito.given(evaluacionService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockEvaluacion(1L, null));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVALUACION-VER" })
  public void findAll_Unlimited_ReturnsFullEvaluacionList() throws Exception {
    // given: One hundred Evaluacion
    List<Evaluacion> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i)));
    }

    BDDMockito
        .given(evaluacionService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(evaluaciones));

    // when: find unlimited
    mockMvc.perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred Evaluacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVALUACION-VER" })
  public void findAll_WithPaging_ReturnsEvaluacionSubList() throws Exception {
    // given: One hundred Evaluacion
    List<Evaluacion> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i)));
    }

    BDDMockito
        .given(evaluacionService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Evaluacion>>() {
          @Override
          public Page<Evaluacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Evaluacion> content = evaluaciones.subList(fromIndex, toIndex);
            Page<Evaluacion> page = new PageImpl<>(content, pageable, evaluaciones.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH).header("X-Page", "3")
            .header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked Evaluaciones are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Evaluacion> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Evaluacion>>() {
        });

    // containing memoria.titulo='Memoria031' to 'Memoria040'
    // containing dictamen.nombre='Dictamen031' to 'Dictamen040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Evaluacion evaluacion = actual.get(i);
      Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", j));
      Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen" + String.format("%03d", j));
      Assertions.assertThat(evaluacion.getConvocatoriaReunion().getCodigo())
          .isEqualTo("CR-" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVALUACION-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredEvaluacionList() throws Exception {
    // given: One hundred Evaluacion and a search query
    List<Evaluacion> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i)));
    }
    String query = "esRevMinima:true,id:5";

    BDDMockito
        .given(evaluacionService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Evaluacion>>() {
          @Override
          public Page<Evaluacion> answer(InvocationOnMock invocation) throws Throwable {
            List<QueryCriteria> queryCriterias = invocation.<List<QueryCriteria>>getArgument(0);

            List<Evaluacion> content = new ArrayList<>();
            for (Evaluacion evaluacion : evaluaciones) {
              boolean add = true;
              for (QueryCriteria queryCriteria : queryCriterias) {
                Field field = ReflectionUtils.findField(Evaluacion.class, queryCriteria.getKey());
                field.setAccessible(true);
                String fieldValue = ReflectionUtils.getField(field, evaluacion).toString();
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
                content.add(evaluacion);
              }
            }
            Page<Evaluacion> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH).param("q", query)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred Evaluacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto Evaluacion
   * 
   * @param id     id del Evaluacion
   * @param sufijo el sufijo para título y nombre
   * @return el objeto Evaluacion
   */

  public Evaluacion generarMockEvaluacion(Long id, String sufijo) {

    String sufijoStr = (sufijo == null ? id.toString() : sufijo);

    Dictamen dictamen = new Dictamen();
    dictamen.setId(id);
    dictamen.setNombre("Dictamen" + sufijoStr);
    dictamen.setActivo(Boolean.TRUE);

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
    tipoMemoria.setId(1L);
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    Memoria memoria = new Memoria(id, "numRef-001", peticionEvaluacion, comite, "Memoria" + sufijoStr, "user-00" + id,
        tipoMemoria, LocalDate.now(), Boolean.FALSE, LocalDate.now(), 3,
        new TipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE));

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);

    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(1L);
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(LocalDateTime.now());
    convocatoriaReunion.setFechaLimite(LocalDate.now());
    convocatoriaReunion.setLugar("Lugar");
    convocatoriaReunion.setOrdenDia("Orden del día convocatoria reunión");
    convocatoriaReunion.setCodigo("CR-" + sufijoStr);
    convocatoriaReunion.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    convocatoriaReunion.setHoraInicio(7);
    convocatoriaReunion.setMinutoInicio(30);
    convocatoriaReunion.setFechaEnvio(LocalDate.now());
    convocatoriaReunion.setActivo(Boolean.TRUE);

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    evaluacion.setDictamen(dictamen);
    evaluacion.setEsRevMinima(Boolean.TRUE);
    evaluacion.setFechaDictamen(LocalDate.now());
    evaluacion.setMemoria(memoria);
    evaluacion.setConvocatoriaReunion(convocatoriaReunion);
    evaluacion.setVersion(2);
    evaluacion.setActivo(Boolean.TRUE);

    return evaluacion;
  }

}
