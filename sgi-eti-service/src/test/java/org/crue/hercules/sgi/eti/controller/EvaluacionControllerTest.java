package org.crue.hercules.sgi.eti.controller;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.config.SecurityConfig;
import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
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
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
 * EvaluacionControllerTest
 */
@WebMvcTest(EvaluacionController.class)
// Since WebMvcTest is only sliced controller layer for the testing, it would
// not take the security configurations.
@Import(SecurityConfig.class)
public class EvaluacionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private EvaluacionService evaluacionService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_BY_CONVOCATORIA_REUNION = "/convocatoriareunion";
  private static final String EVALUACION_CONTROLLER_BASE_PATH = "/evaluaciones";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V" })
  public void getEvaluacion_WithId_ReturnsEvaluacion() throws Exception {
    BDDMockito.given(evaluacionService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockEvaluacion(1L, null)));

    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("dictamen.nombre").value("Dictamen1"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoEvaluacion.nombre").value("TipoEvaluacion1"))
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.titulo").value("Memoria1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V" })
  public void getEvaluacion_NotFound_Returns404() throws Exception {
    BDDMockito.given(evaluacionService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new EvaluacionNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-C" })
  public void newEvaluacion_ReturnsEvaluacion() throws Exception {
    // given: Una evaluacion nueva
    String nuevoEvaluacionJson = "{\"memoria\":{\"id\": 1, \"numReferencia\": \"numRef-5598\", \"peticionEvaluacion\": {\"id\": 1, \"titulo\": \"PeticionEvaluacion1\"},"
        + " \"comite\": {\"comite\": \"Comite1\"},\"titulo\": \"Memoria1 replace\", \"numReferencia\": \"userRef-55\", \"fechaEstado\": \"2020-06-09\","
        + "\"tipoMemoria\": {\"id\": 1, \"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}, \"requiereRetrospectiva\": \"false\",\"version\": \"1\"}, \"convocatoriaReunion\": {\"id\": 1},"
        + "\"dictamen\": {\"id\": 1, \"nombre\": \"Dictamen1\", \"activo\": \"true\"}, \"tipoEvaluacion\": {\"id\": 1, \"nombre\": \"TipoEvaluacion1\", \"activo\": \"true\"},\"esRevMinima\": \"true\", \"activo\": \"true\",\"version\": \"1\"}";
    Evaluacion evaluacion = generarMockEvaluacion(1L, null);

    BDDMockito.given(evaluacionService.create(ArgumentMatchers.<Evaluacion>any())).willReturn(evaluacion);

    // when: Creamos una evaluacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoEvaluacionJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Crea la nueva evaluacion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.titulo").value("Memoria1"))
        .andExpect(MockMvcResultMatchers.jsonPath("dictamen.nombre").value("Dictamen1"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoEvaluacion.nombre").value("TipoEvaluacion1"))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaReunion.id").value("1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-C" })
  public void newEvaluacion_Error_Returns400() throws Exception {
    // given: Una evaluacion nueva que produce un error al crearse
    String nuevoEvaluacionJson = "{\"memoria\":{\"id\": 1, \"numReferencia\": \"numRef-5598\", \"peticionEvaluacion\": {\"id\": 1, \"titulo\": \"PeticionEvaluacion1\"},"
        + " \"comite\": {\"comite\": \"Comite1\"},\"titulo\": \"Memoria1 replace\", \"numReferencia\": \"userRef-55\", \"fechaEstado\": \"2020-06-09\","
        + "\"tipoMemoria\": {\"id\": 1, \"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}, \"requiereRetrospectiva\": \"false\",\"version\": \"1\"}, \"dictamen\": {\"id\": 1, \"nombre\": \"Dictamen1\", \"activo\": \"true\"}, \"tipoEvaluacion\": {\"id\": 1, \"nombre\": \"TipoEvaluacion1\", \"activo\": \"true\"}, \"esRevMinima\": \"true\", \"activo\": \"true\"}";

    BDDMockito.given(evaluacionService.create(ArgumentMatchers.<Evaluacion>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un evaluacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoEvaluacionJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-E" })
  public void replaceEvaluacion_ReturnsEvaluacion() throws Exception {
    // given: Una evaluacion a modificar
    String replaceEvaluacionJson = "{\"id\": 1, \"memoria\":{\"id\": 1, \"numReferencia\": \"numRef-5598\", \"peticionEvaluacion\": {\"id\": 1, \"titulo\": \"PeticionEvaluacion1\"},"
        + " \"comite\": {\"comite\": \"Comite1\"},\"titulo\": \"Memoria1 replace\", \"numReferencia\": \"userRef-55\", \"fechaEstado\": \"2020-06-09\","
        + "\"tipoMemoria\": {\"id\": 1, \"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}, \"requiereRetrospectiva\": \"false\",\"version\": \"1\"}, \"convocatoriaReunion\": {\"id\": 1},"
        + "\"dictamen\": {\"id\": 1, \"nombre\": \"Dictamen1\", \"activo\": \"true\"}, \"tipoEvaluacion\": {\"id\": 1, \"nombre\": \"TipoEvaluacion1\", \"activo\": \"true\"}, \"esRevMinima\": \"true\", \"activo\": \"true\",\"version\": \"1\"}";

    Evaluacion evaluacion = generarMockEvaluacion(1L, " Replace");

    BDDMockito.given(evaluacionService.update(ArgumentMatchers.<Evaluacion>any())).willReturn(evaluacion);

    mockMvc
        .perform(MockMvcRequestBuilders.put(EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceEvaluacionJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Modifica la evaluacion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("memoria.titulo").value("Memoria Replace"))
        .andExpect(MockMvcResultMatchers.jsonPath("dictamen.nombre").value("Dictamen Replace"))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoEvaluacion.nombre").value("TipoEvaluacion1"))
        .andExpect(MockMvcResultMatchers.jsonPath("convocatoriaReunion.id").value("1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-E" })
  public void replaceEvaluacion_NotFound() throws Exception {
    // given: Una evaluacion a modificar
    String replaceEvaluacionJson = "{\"id\": 1, \"memoria\":{\"id\": 1, \"numReferencia\": \"numRef-5598\", \"peticionEvaluacion\": {\"id\": 1, \"titulo\": \"PeticionEvaluacion1\"},"
        + " \"comite\": {\"comite\": \"Comite1\"},\"titulo\": \"Memoria1 replace\", \"numReferencia\": \"userRef-55\", \"fechaEstado\": \"2020-06-09\","
        + "\"tipoMemoria\": {\"id\": 1, \"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}, \"requiereRetrospectiva\": \"false\",\"version\": \"1\"}, \"convocatoriaReunion\": {\"id\": 1},"
        + "\"dictamen\": {\"id\": 1, \"nombre\": \"Dictamen1\", \"activo\": \"true\"}, \"tipoEvaluacion\": {\"id\": 1, \"nombre\": \"TipoEvaluacion1\", \"activo\": \"true\"}, \"esRevMinima\": \"true\", \"activo\": \"true\",\"version\": \"1\"}";

    BDDMockito.given(evaluacionService.update(ArgumentMatchers.<Evaluacion>any()))
        .will((InvocationOnMock invocation) -> {
          throw new EvaluacionNotFoundException(((Evaluacion) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceEvaluacionJson))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-B" })
  public void removeEvaluacion_ReturnsOk() throws Exception {
    BDDMockito.given(evaluacionService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockEvaluacion(1L, null));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V" })
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
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred Evaluacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V" })
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
        .perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
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
      Assertions.assertThat(evaluacion.getConvocatoriaReunion().getId()).isEqualTo(1L);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V" })
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
        .perform(MockMvcRequestBuilders.get(EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred Evaluacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-ACT-E" })
  public void findAllActivasByConvocatoriaReunionId_Unlimited_ReturnsFullEvaluacionList() throws Exception {
    // given: Datos existentes con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    final String url = new StringBuilder(EVALUACION_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_BY_CONVOCATORIA_REUNION)//
        .append(PATH_PARAMETER_ID).toString();

    List<Evaluacion> response = new ArrayList<>();
    response.add(generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1)));
    response.add(generarMockEvaluacion(Long.valueOf(3), String.format("%03d", 3)));

    BDDMockito.given(evaluacionService.findAllActivasByConvocatoriaReunionId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(response));

    // when: Se buscan todos los datos
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get(url, convocatoriaReunionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Se recuperan todos los datos
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2))).andReturn();

    Assertions.assertThat(mapper.readValue(result.getResponse().getContentAsString(Charset.forName("UTF-8")),
        new TypeReference<List<Evaluacion>>() {
        })).isEqualTo(response);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVC-V", "ETI-ACT-C", "ETI-ACT-E" })
  public void findAllActivasByConvocatoriaReunionId_Unlimited_Returns204() throws Exception {

    // given: Sin datos con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    final String url = new StringBuilder(EVALUACION_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_BY_CONVOCATORIA_REUNION)//
        .append(PATH_PARAMETER_ID).toString();

    BDDMockito.given(evaluacionService.findAllActivasByConvocatoriaReunionId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Se buscan todos los datos
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(url, convocatoriaReunionId).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print())
        // then: Se recupera lista vacía);
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-ACT-E" })
  public void findAllActivasByConvocatoriaReunionId_WithPaging_ReturnsEvaluacionSubList() throws Exception {

    // given: Datos existentes con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    final String url = new StringBuilder(EVALUACION_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_BY_CONVOCATORIA_REUNION)//
        .append(PATH_PARAMETER_ID).toString();

    List<Evaluacion> response = new ArrayList<>();
    response.add(generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1)));
    response.add(generarMockEvaluacion(Long.valueOf(3), String.format("%03d", 3)));
    response.add(generarMockEvaluacion(Long.valueOf(5), String.format("%03d", 5)));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<Evaluacion> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(evaluacionService.findAllActivasByConvocatoriaReunionId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan todos los datos paginados
    MvcResult result = mockMvc
        .perform(MockMvcRequestBuilders.get(url, convocatoriaReunionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", pageable.getPageNumber())
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
        new TypeReference<List<Evaluacion>>() {
        })).isEqualTo(response.subList(2, 3));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-C", "ETI-ACT-E" })
  public void findAllActivasByConvocatoriaReunionId_WithPaging_Returns204() throws Exception {

    // given: Sin datos con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    final String url = new StringBuilder(EVALUACION_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_BY_CONVOCATORIA_REUNION)//
        .append(PATH_PARAMETER_ID).toString();

    List<Evaluacion> response = new ArrayList<Evaluacion>();
    Pageable pageable = PageRequest.of(1, 2);
    Page<Evaluacion> pageResponse = new PageImpl<>(response, pageable, response.size());

    BDDMockito.given(evaluacionService.findAllActivasByConvocatoriaReunionId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan todos los datos paginados
    mockMvc
        .perform(MockMvcRequestBuilders.get(url, convocatoriaReunionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", pageable.getPageNumber())
            .header("X-Page-Size", pageable.getPageSize()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Se recupera lista de datos paginados vacía
        .andExpect(MockMvcResultMatchers.status().isNoContent());
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
    peticionEvaluacion.setFuenteFinanciacion("Fuente financiación");
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

    Memoria memoria = new Memoria(1L, "numRef-001", peticionEvaluacion, comite, "Memoria" + sufijoStr, "user-00" + id,
        tipoMemoria, new TipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), LocalDate.now(), Boolean.FALSE,
        new Retrospectiva(id, new EstadoRetrospectiva(1L, "Pendiente", Boolean.TRUE), LocalDate.now()), 3,
        Boolean.TRUE);

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);

    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(1L);
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(LocalDateTime.now());
    convocatoriaReunion.setFechaLimite(LocalDate.now());
    convocatoriaReunion.setLugar("Lugar");
    convocatoriaReunion.setOrdenDia("Orden del día convocatoria reunión");
    convocatoriaReunion.setAnio(2020);
    convocatoriaReunion.setNumeroActa(100L);
    convocatoriaReunion.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    convocatoriaReunion.setHoraInicio(7);
    convocatoriaReunion.setMinutoInicio(30);
    convocatoriaReunion.setFechaEnvio(LocalDate.now());
    convocatoriaReunion.setActivo(Boolean.TRUE);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setNombre("TipoEvaluacion1");
    tipoEvaluacion.setActivo(Boolean.TRUE);

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    evaluacion.setDictamen(dictamen);
    evaluacion.setEsRevMinima(Boolean.TRUE);
    evaluacion.setFechaDictamen(LocalDate.now());
    evaluacion.setMemoria(memoria);
    evaluacion.setConvocatoriaReunion(convocatoriaReunion);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);
    evaluacion.setVersion(2);
    evaluacion.setActivo(Boolean.TRUE);

    return evaluacion;
  }

}
