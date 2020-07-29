package org.crue.hercules.sgi.eti.controller;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.config.SecurityConfig;
import org.crue.hercules.sgi.eti.exceptions.EvaluadorNotFoundException;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.service.EvaluadorService;
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
 * EvaluadorControllerTest
 */
@WebMvcTest(EvaluadorController.class)
// Since WebMvcTest is only sliced controller layer for the testing, it would
// not take the security configurations.
@Import(SecurityConfig.class)
public class EvaluadorControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private EvaluadorService evaluadorService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String EVALUADOR_CONTROLLER_BASE_PATH = "/evaluadores";

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVALUADOR-VER" })
  public void getEvaluador_WithId_ReturnsEvaluador() throws Exception {
    BDDMockito.given(evaluadorService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockEvaluador(1L, "Evaluador1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("resumen").value("Evaluador1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVALUADOR-VER" })
  public void getEvaluador_NotFound_Returns404() throws Exception {
    BDDMockito.given(evaluadorService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new EvaluadorNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVALUADOR-EDITAR" })
  public void newEvaluador_ReturnsEvaluador() throws Exception {
    // given: Un evaluador nuevo
    Evaluador evaluador = generarMockEvaluador(1L, "Evaluador1");
    evaluador.setId(null);
    String nuevoEvaluadorJson = mapper.writeValueAsString(evaluador);
    evaluador.setId(1L);

    BDDMockito.given(evaluadorService.create(ArgumentMatchers.<Evaluador>any())).willReturn(evaluador);

    // when: Creamos un evaluador
    mockMvc
        .perform(MockMvcRequestBuilders.post(EVALUADOR_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoEvaluadorJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Crea el nuevo evaluador y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("resumen").value("Evaluador1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVALUADOR-EDITAR" })
  public void newEvaluador_Error_Returns400() throws Exception {
    // given: Un evaluador nuevo que produce un error al crearse
    String nuevoEvaluadorJson = "{\"resumen\": \"Evaluador1\", \"activo\": \"true\"}";

    BDDMockito.given(evaluadorService.create(ArgumentMatchers.<Evaluador>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un evaluador
    mockMvc
        .perform(MockMvcRequestBuilders.post(EVALUADOR_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoEvaluadorJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVALUADOR-EDITAR" })
  public void replaceEvaluador_ReturnsEvaluador() throws Exception {
    // given: Un evaluador a modificar
    Evaluador evaluador = generarMockEvaluador(1L, "Replace Evaluador1");
    String replaceEvaluadorJson = mapper.writeValueAsString(evaluador);

    BDDMockito.given(evaluadorService.update(ArgumentMatchers.<Evaluador>any())).willReturn(evaluador);

    mockMvc
        .perform(MockMvcRequestBuilders.put(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceEvaluadorJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Modifica el evaluador y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("resumen").value("Replace Evaluador1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVALUADOR-EDITAR" })
  public void replaceEvaluador_NotFound() throws Exception {
    // given: Un evaluador a modificar
    String replaceEvaluadorJson = mapper.writeValueAsString(generarMockEvaluador(1L, "Evaluador1"));

    BDDMockito.given(evaluadorService.update(ArgumentMatchers.<Evaluador>any())).will((InvocationOnMock invocation) -> {
      throw new EvaluadorNotFoundException(((Evaluador) invocation.getArgument(0)).getId());
    });
    mockMvc
        .perform(MockMvcRequestBuilders.put(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceEvaluadorJson))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVALUADOR-EDITAR" })
  public void removeEvaluador_ReturnsOk() throws Exception {
    BDDMockito.given(evaluadorService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockEvaluador(1L, "Evaluador1"));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(EVALUADOR_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVALUADOR-VER" })
  public void findAll_Unlimited_ReturnsFullEvaluadorList() throws Exception {
    // given: One hundred Evaluador
    List<Evaluador> evaluadores = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluadores.add(generarMockEvaluador(Long.valueOf(i), "Evaluador" + String.format("%03d", i)));
    }

    BDDMockito
        .given(evaluadorService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(evaluadores));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUADOR_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred Evaluador
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVALUADOR-VER" })
  public void findAll_WithPaging_ReturnsEvaluadorSubList() throws Exception {
    // given: One hundred Evaluador
    List<Evaluador> evaluadores = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluadores.add(generarMockEvaluador(Long.valueOf(i), "Evaluador" + String.format("%03d", i)));
    }

    BDDMockito
        .given(evaluadorService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Evaluador>>() {
          @Override
          public Page<Evaluador> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Evaluador> content = evaluadores.subList(fromIndex, toIndex);
            Page<Evaluador> page = new PageImpl<>(content, pageable, evaluadores.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(EVALUADOR_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked Evaluadors are returned with the right page
        // information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<Evaluador> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Evaluador>>() {
        });

    // containing resumen='Evaluador031' to 'Evaluador040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Evaluador evaluador = actual.get(i);
      Assertions.assertThat(evaluador.getResumen()).isEqualTo("Evaluador" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-EVALUADOR-VER" })
  public void findAll_WithSearchQuery_ReturnsFilteredEvaluadorList() throws Exception {
    // given: One hundred Evaluador and a search query
    List<Evaluador> evaluadores = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluadores.add(generarMockEvaluador(Long.valueOf(i), "Evaluador" + String.format("%03d", i)));
    }
    String query = "resumen~Evaluador%,id:5";

    BDDMockito
        .given(evaluadorService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Evaluador>>() {
          @Override
          public Page<Evaluador> answer(InvocationOnMock invocation) throws Throwable {
            List<QueryCriteria> queryCriterias = invocation.<List<QueryCriteria>>getArgument(0);

            List<Evaluador> content = new ArrayList<>();
            for (Evaluador evaluador : evaluadores) {
              boolean add = true;
              for (QueryCriteria queryCriteria : queryCriterias) {
                Field field = ReflectionUtils.findField(Evaluador.class, queryCriteria.getKey());
                field.setAccessible(true);
                String fieldValue = ReflectionUtils.getField(field, evaluador).toString();
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
                content.add(evaluador);
              }
            }
            Page<Evaluador> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUADOR_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred Evaluador
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto Evaluador
   * 
   * @param id      id del Evaluador
   * @param resumen el resumen de Evaluador
   * @return el objeto Evaluador
   */

  public Evaluador generarMockEvaluador(Long id, String resumen) {
    CargoComite cargoComite = new CargoComite();
    cargoComite.setId(1L);
    cargoComite.setNombre("CargoComite1");
    cargoComite.setActivo(Boolean.TRUE);

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    Evaluador evaluador = new Evaluador();
    evaluador.setId(id);
    evaluador.setCargoComite(cargoComite);
    evaluador.setComite(comite);
    evaluador.setFechaAlta(LocalDate.now());
    evaluador.setFechaBaja(LocalDate.now());
    evaluador.setResumen(resumen);
    evaluador.setUsuarioRef("user-00" + id);
    evaluador.setActivo(Boolean.TRUE);

    return evaluador;
  }

}
