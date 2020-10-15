package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.SecurityConfig;
import org.crue.hercules.sgi.csp.exceptions.PlanNotFoundException;
import org.crue.hercules.sgi.csp.model.Plan;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.service.PlanService;
import org.crue.hercules.sgi.csp.service.ProgramaService;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.BeanUtils;
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

/**
 * PlanControllerTest
 */
@WebMvcTest(PlanController.class)
@Import(SecurityConfig.class)
public class PlanControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private PlanService service;

  @MockBean
  private ProgramaService programaService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/planes";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C" })
  public void create_ReturnsModeloPlan() throws Exception {
    // given: new Plan
    Plan plan = generarMockPlan(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<Plan>any())).willAnswer((InvocationOnMock invocation) -> {
      Plan newPlan = new Plan();
      BeanUtils.copyProperties(invocation.getArgument(0), newPlan);
      newPlan.setId(1L);
      return newPlan;
    });

    // when: create Plan
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(plan)))
        .andDo(MockMvcResultHandlers.print())
        // then: new Plan is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(plan.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(plan.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(plan.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a Plan with id filled
    Plan plan = generarMockPlan(1L);

    BDDMockito.given(service.create(ArgumentMatchers.<Plan>any())).willThrow(new IllegalArgumentException());

    // when: create Plan
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(plan)))
        .andDo(MockMvcResultHandlers.print())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void update_ReturnsPlan() throws Exception {
    // given: Existing Plan to be updated
    Plan planExistente = generarMockPlan(1L);
    Plan plan = generarMockPlan(1L);
    plan.setNombre("nuevo-nombre");

    BDDMockito.given(service.update(ArgumentMatchers.<Plan>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update Plan
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, planExistente.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(plan)))
        .andDo(MockMvcResultHandlers.print())
        // then: Plan is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(planExistente.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(plan.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(plan.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(planExistente.getActivo()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    Plan plan = generarMockPlan(1L);

    BDDMockito.willThrow(new PlanNotFoundException(id)).given(service).update(ArgumentMatchers.<Plan>any());

    // when: update Plan
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(plan)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-B" })
  public void delete_WithExistingId_Return204() throws Exception {
    // given: existing id
    Plan plan = generarMockPlan(1L);
    BDDMockito.given(service.disable(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      Plan planDisabled = new Plan();
      BeanUtils.copyProperties(plan, planDisabled);
      planDisabled.setActivo(false);
      return planDisabled;
    });

    // when: delete by id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, plan.getId())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-B" })
  public void delete_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new PlanNotFoundException(id)).given(service).disable(ArgumentMatchers.<Long>any());

    // when: delete by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TDOC-V" })
  public void findAll_ReturnsPage() throws Exception {
    // given: Una lista con 37 Plan
    List<Plan> planes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      planes.add(generarMockPlan(i, "Plan" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > planes.size() ? planes.size() : toIndex;
          List<Plan> content = planes.subList(fromIndex, toIndex);
          Page<Plan> pageResponse = new PageImpl<>(content, pageable, planes.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve la pagina 3 con los Plan del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<Plan> planesResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Plan>>() {
        });

    for (int i = 31; i <= 37; i++) {
      Plan plan = planesResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(plan.getNombre()).isEqualTo("Plan" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TDOC-V" })
  public void findAll_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de Plan
    List<Plan> planes = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(service.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<Plan> pageResponse = new PageImpl<>(planes, pageable, 0);
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
  @WithMockUser(username = "user", authorities = { "CSP-ME-V" })
  public void findById_WithExistingId_ReturnsPlan() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockPlan(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested Plan is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("nombre-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value("descripcion-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(true));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new PlanNotFoundException(1L);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TDOC-V" })
  public void findAllProgramas_ReturnsPage() throws Exception {
    // given: Una lista con 37 Programa para el Plan
    Long idPlan = 1L;

    List<Programa> programas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      programas.add(generarMockPrograma(i, "Programa" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(programaService.findAllByPlan(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > programas.size() ? programas.size() : toIndex;
          List<Programa> content = programas.subList(fromIndex, toIndex);
          Page<Programa> pageResponse = new PageImpl<>(content, pageable, programas.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/programas", idPlan)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve la pagina 3 con los Programa del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<Programa> programasResponse = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Programa>>() {
        });

    for (int i = 31; i <= 37; i++) {
      Programa programa = programasResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(programa.getNombre()).isEqualTo("Programa" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-TDOC-V" })
  public void findAllProgramas_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de Programa para el Plan
    Long idPlan = 1L;

    List<Programa> programas = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(programaService.findAllByPlan(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          Page<Programa> pageResponse = new PageImpl<>(programas, pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/programas", idPlan)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());

  }

  /**
   * Función que devuelve un objeto Plan
   * 
   * @param id id del Plan
   * @return el objeto Plan
   */
  private Plan generarMockPlan(Long id) {
    return generarMockPlan(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto Plan
   * 
   * @param id     id del Plan
   * @param nombre nombre del Plan
   * @return el objeto Plan
   */
  private Plan generarMockPlan(Long id, String nombre) {
    Plan plan = new Plan();
    plan.setId(id);
    plan.setNombre(nombre);
    plan.setDescripcion("descripcion-" + id);
    plan.setActivo(true);

    return plan;
  }

  /**
   * Función que devuelve un objeto Programa
   * 
   * @param id     id del Programa
   * @param nombre nombre del Programa
   * @return el objeto Programa
   */
  private Programa generarMockPrograma(Long id, String nombre) {
    Programa programa = new Programa();
    programa.setId(id);
    programa.setNombre(nombre);
    programa.setDescripcion("descripcion-" + id);
    programa.setActivo(true);

    return programa;
  }

}
