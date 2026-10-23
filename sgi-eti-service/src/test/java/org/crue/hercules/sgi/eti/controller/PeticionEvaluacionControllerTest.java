package org.crue.hercules.sgi.eti.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.converter.PeticionEvaluacionConverter;
import org.crue.hercules.sgi.eti.converter.PeticionEvaluacionUnidadVinculacionConverter;
import org.crue.hercules.sgi.eti.dto.EquipoTrabajoWithIsEliminable;
import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.dto.EstadoFinanciacionDto;
import org.crue.hercules.sgi.eti.dto.PeticionEvaluacionEstadoFinanciacionInput;
import org.crue.hercules.sgi.eti.dto.PeticionEvaluacionSolicitudCspInput;
import org.crue.hercules.sgi.eti.dto.PeticionEvaluacionWithIsEliminable;
import org.crue.hercules.sgi.eti.dto.TareaWithIsEliminable;
import org.crue.hercules.sgi.eti.exceptions.PeticionEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.EstadoFinanciacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionDisMetodologico;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionObjetivos;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionResumen;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionTitulo;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.model.TareaFormacion;
import org.crue.hercules.sgi.eti.model.TareaNombre;
import org.crue.hercules.sgi.eti.model.TareaOrganismo;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoTarea;
import org.crue.hercules.sgi.eti.service.EquipoTrabajoService;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.crue.hercules.sgi.eti.service.PeticionEvaluacionService;
import org.crue.hercules.sgi.eti.service.PeticionEvaluacionUnidadVinculacionService;
import org.crue.hercules.sgi.eti.service.TareaService;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.security.oauth2.server.resource.authentication.SgiJwtAuthenticationConverter;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * PeticionEvaluacionControllerTest
 */
@WebMvcTest(PeticionEvaluacionController.class)
class PeticionEvaluacionControllerTest extends BaseControllerTest {

  @MockBean
  private PeticionEvaluacionService peticionEvaluacionService;

  @MockBean
  private EquipoTrabajoService equipoTrabajoService;

  @MockBean
  private MemoriaService memoriaService;

  @MockBean
  private TareaService tareaService;

  @MockBean
  private PeticionEvaluacionUnidadVinculacionService peticionEvaluacionUnidadVinculacionService;

  @MockBean
  private PeticionEvaluacionUnidadVinculacionConverter peticionEvaluacionUnidadVinculacionConverter;

  @MockBean
  private PeticionEvaluacionConverter peticionEvaluacionConverter;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PETICION_EVALUACION_CONTROLLER_BASE_PATH = "/peticionevaluaciones";
  private static final String MEMORIAS_CONTROLLER_BASE_PATH = "/memorias";
  private static final String PERSONA_CONTROLLER_BASE_PATH = "/persona";
  private static final String PATH_SOLICITUD_CSP = PeticionEvaluacionController.PATH_SOLICITUD_CSP;
  private static final String PATH_ESTADO_FINANCIACION = PeticionEvaluacionController.PATH_ESTADO_FINANCIACION;

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-ER", "ETI-PEV-V" })
  void getPeticionEvaluacion_WithId_ReturnsPeticionEvaluagetPeticionEvaluacion_NotFound_Returns404cion()
      throws Exception {
    BDDMockito.given(peticionEvaluacionService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1")));

    mockMvc
        .perform(MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("titulo[0].value").value("PeticionEvaluacion1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-V", "ETI-PEV-INV-VR", "ETI-PEV-INV-C", "ETI-PEV-INV-ER" })
  void getPeticionEvaluacion_NotFound_Returns404() throws Exception {
    BDDMockito.given(peticionEvaluacionService.findById(ArgumentMatchers.anyLong()))
        .will((InvocationOnMock invocation) -> {
          throw new PeticionEvaluacionNotFoundException(invocation.getArgument(0));
        });
    mockMvc
        .perform(MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-C", "ETI-PEV-MOD-C" })
  void newPeticionEvaluacion_ReturnsPeticionEvaluacion() throws Exception {
    // given: Un peticionEvaluacion nuevo
    String nuevoPeticionEvaluacionJson = "{\"titulo\": [{\"lang\": \"es\", \"value\": \"PeticionEvaluacion1\"}], \"activo\": \"true\"}";

    PeticionEvaluacion peticionEvaluacion = generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1");

    BDDMockito.given(peticionEvaluacionService.create(ArgumentMatchers.<PeticionEvaluacion>any()))
        .willReturn(peticionEvaluacion);

    // when: Creamos un peticionEvaluacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(PETICION_EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoPeticionEvaluacionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo peticionEvaluacion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(
            MockMvcResultMatchers.jsonPath("titulo[0].value").value("PeticionEvaluacion1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-C", "ETI-PEV-MOD-C" })
  void newPeticionEvaluacion_Error_Returns400() throws Exception {
    // given: Un peticionEvaluacion nuevo que produce un error al crearse
    String nuevoPeticionEvaluacionJson = "{\"titulo\": [{\"lang\": \"es\", \"value\": \"PeticionEvaluacion1\"}], \"activo\": \"true\"}";

    BDDMockito.given(peticionEvaluacionService.create(ArgumentMatchers.<PeticionEvaluacion>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un peticionEvaluacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(PETICION_EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoPeticionEvaluacionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-ER", "ETI-PEV-MOD-C" })
  void replacePeticionEvaluacion_ReturnsPeticionEvaluacion() throws Exception {
    // given: Un peticionEvaluacion a modificar
    String replacePeticionEvaluacionJson = "{\"id\": 1, \"titulo\": [{\"lang\": \"es\", \"value\": \"PeticionEvaluacion1\"}]}";

    PeticionEvaluacion peticionEvaluacion = generarMockPeticionEvaluacion(1L, "Replace PeticionEvaluacion1");

    BDDMockito.given(peticionEvaluacionService.update(ArgumentMatchers.<PeticionEvaluacion>any()))
        .willReturn(peticionEvaluacion);

    mockMvc
        .perform(MockMvcRequestBuilders.put(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replacePeticionEvaluacionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Modifica el peticionEvaluacion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("titulo[0].value").value("Replace PeticionEvaluacion1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-ER", "ETI-PEV-MOD-C" })
  void replacePeticionEvaluacion_NotFound() throws Exception {
    // given: Un peticionEvaluacion a modificar
    String replacePeticionEvaluacionJson = "{\"id\": 1, \"titulo\": [{\"lang\": \"es\", \"value\": \"PeticionEvaluacion1\"}], \"activo\": \"true\"}";

    BDDMockito.given(peticionEvaluacionService.update(ArgumentMatchers.<PeticionEvaluacion>any()))
        .will((InvocationOnMock invocation) -> {
          throw new PeticionEvaluacionNotFoundException(((PeticionEvaluacion) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replacePeticionEvaluacionJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-BR" })
  void removePeticionEvaluacion_ReturnsOk() throws Exception {
    BDDMockito.given(peticionEvaluacionService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1"));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-VR", "ETI-PEV-V" })
  void findAll_Unlimited_ReturnsFullPeticionEvaluacionList() throws Exception {
    // given: One hundred PeticionEvaluacion
    List<PeticionEvaluacionWithIsEliminable> peticionEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      peticionEvaluaciones.add(generarMockPeticionEvaluacionWithIsEliminable(Long.valueOf(i),
          "PeticionEvaluacion" + String.format("%03d", i)));
    }

    BDDMockito
        .given(peticionEvaluacionService.findAllPeticionesWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any(), ArgumentMatchers.isNull()))
        .willReturn(new PageImpl<>(peticionEvaluaciones));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred PeticionEvaluacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-VR", "ETI-PEV-V" })
  void findAll_ReturnsNoContent() throws Exception {
    // given: PeticionEvaluacion empty
    List<PeticionEvaluacionWithIsEliminable> peticionEvaluaciones = new ArrayList<>();

    BDDMockito
        .given(peticionEvaluacionService.findAllPeticionesWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any(), ArgumentMatchers.isNull()))
        .willReturn(new PageImpl<>(peticionEvaluaciones));

    mockMvc
        .perform(MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-VR", "ETI-PEV-V" })
  void findAll_WithPaging_ReturnsPeticionEvaluacionSubList() throws Exception {
    // given: One hundred PeticionEvaluacion
    List<PeticionEvaluacion> peticionEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      peticionEvaluaciones
          .add(generarMockPeticionEvaluacion(Long.valueOf(i), "PeticionEvaluacion" + String.format("%03d", i)));
    }

    BDDMockito
        .given(peticionEvaluacionService.findAllPeticionesWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any(), ArgumentMatchers.isNull()))
        .willAnswer(new Answer<Page<PeticionEvaluacion>>() {
          @Override
          public Page<PeticionEvaluacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<PeticionEvaluacion> content = peticionEvaluaciones.subList(fromIndex, toIndex);
            return new PageImpl<>(content, pageable, peticionEvaluaciones.size());
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked PeticionEvaluacions are returned with the right page
        // information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<PeticionEvaluacion> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<PeticionEvaluacion>>() {
        });

    // containing titulo='PeticionEvaluacion031' to 'PeticionEvaluacion040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      PeticionEvaluacion peticionEvaluacion = actual.get(i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(peticionEvaluacion.getTitulo(), Language.ES))
          .isEqualTo("PeticionEvaluacion" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-VR", "ETI-PEV-V" })
  void findAll_WithSearchQuery_ReturnsFilteredPeticionEvaluacionList() throws Exception {
    // given: One hundred PeticionEvaluacion and a search query
    List<PeticionEvaluacion> peticionEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      peticionEvaluaciones
          .add(generarMockPeticionEvaluacion(Long.valueOf(i), "PeticionEvaluacion" + String.format("%03d", i)));
    }
    String query = "titulo~PeticionEvaluacion%,id:5";

    BDDMockito
        .given(peticionEvaluacionService.findAllPeticionesWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any(), ArgumentMatchers.isNull()))
        .willAnswer(new Answer<Page<PeticionEvaluacion>>() {
          @Override
          public Page<PeticionEvaluacion> answer(InvocationOnMock invocation) throws Throwable {
            List<PeticionEvaluacion> content = new ArrayList<>();
            for (PeticionEvaluacion peticionEvaluacion : peticionEvaluaciones) {
              if (I18nHelper.getValueForLanguage(peticionEvaluacion.getTitulo(), Language.ES)
                  .startsWith("PeticionEvaluacion")
                  && peticionEvaluacion.getId().equals(5L)) {
                content.add(peticionEvaluacion);
              }
            }
            return new PageImpl<>(content);
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred PeticionEvaluacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-CR", "ETI-PEV-INV-ER", "ETI-PEV-INV-VR", "ETI-PEV-INV-C",
      "ETI-PEV-INV-ER" })
  void findEquipoInvestigador_ReturnsEquipoTrabajoSubList() throws Exception {
    // given: 10 EquipoTrabajos por PeticionEvaluacion
    List<EquipoTrabajoWithIsEliminable> equipoTrabajos = new ArrayList<>();
    for (int i = 1, j = 1; i <= 10; i++, j++) {
      equipoTrabajos.add(generarMockEquipoTrabajoWithIsEliminable(Long.valueOf(i * 10 + j - 10),
          generarMockPeticionEvaluacion(Long.valueOf(i), "PeticionEvaluacion" + String.format("%03d", i))));
    }

    BDDMockito.given(equipoTrabajoService.findAllByPeticionEvaluacionId(ArgumentMatchers.<Long>any()))
        .willReturn(equipoTrabajos);

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/equipo-investigador", 1L)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked EquipoTrabajos are returned with the right page information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<EquipoTrabajo> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<EquipoTrabajo>>() {
        });

    // containing peticionEvaluacion.titulo='PeticionEvaluacion006' to
    // 'PeticionEvaluacion010'
    for (int i = 0, j = 1; i < 10 && j <= 10; i++, j++) {
      EquipoTrabajo equipoTrabajo = actual.get(i);
      Assertions.assertThat(equipoTrabajo.getPeticionEvaluacionId())
          .isEqualTo(j);
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-CR", "ETI-PEV-INV-ER", "ETI-PEV-INV-VR", "ETI-PEV-INV-C",
      "ETI-PEV-INV-ER" })
  void findEquipoInvestigador_ReturnsNoContent() throws Exception {
    // given: EquipoTrabajos empty
    List<EquipoTrabajoWithIsEliminable> equipoTrabajos = new ArrayList<>();

    BDDMockito.given(equipoTrabajoService.findAllByPeticionEvaluacionId(ArgumentMatchers.<Long>any()))
        .willReturn(equipoTrabajos);

    mockMvc
        .perform(MockMvcRequestBuilders
            .get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/equipo-investigador", 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-C", "ETI-PEV-INV-ER" })
  void findTareas_ReturnsTareaSubList() throws Exception {
    // given: One hundred tareas
    List<TareaWithIsEliminable> tareas = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      tareas.add(generarMockTareaWithIsEliminable(Long.valueOf(i), "Tarea" + String.format("%03d", i)));
    }

    BDDMockito.given(tareaService.findAllByPeticionEvaluacionId(ArgumentMatchers.<Long>any())).willReturn(tareas);

    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/tareas", 1L)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked tareas are returned with the right page information in
        // headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<TareaWithIsEliminable> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<TareaWithIsEliminable>>() {
        });

    for (int i = 0, j = 1; i < 10; i++, j++) {
      TareaWithIsEliminable tarea = actual.get(i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(tarea.getNombre(), Language.ES))
          .isEqualTo("Tarea" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-C", "ETI-PEV-INV-ER" })
  void findTareas_ReturnsNoContent() throws Exception {
    // given: Tareas empty
    List<TareaWithIsEliminable> tareas = new ArrayList<>();

    BDDMockito.given(tareaService.findAllByPeticionEvaluacionId(ArgumentMatchers.<Long>any())).willReturn(tareas);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/tareas", 1L)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-C", "ETI-PEV-INV-ER" })
  void findMemorias_NotFound_Returns404() throws Exception {

    BDDMockito.given(memoriaService.findMemoriaByPeticionEvaluacionMaxVersion(ArgumentMatchers.anyLong()))
        .will((InvocationOnMock invocation) -> {
          throw new PeticionEvaluacionNotFoundException(invocation.getArgument(0));
        });
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/memorias", 1L)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-C", "ETI-PEV-INV-ER" })
  void findMemorias_listMemoriaPeticionEvaluacion_ReturnsOk() throws Exception {

    List<MemoriaPeticionEvaluacion> listMemoriaPeticionEvaluacion = new ArrayList<MemoriaPeticionEvaluacion>();

    for (int i = 1, j = 1; i <= 10; i++, j++) {
      listMemoriaPeticionEvaluacion.add(generarMockMemoriaPeticionEvaluacion(Long.valueOf(i * 10 + j - 10)));
    }

    BDDMockito.given(memoriaService.findMemoriaByPeticionEvaluacionMaxVersion(ArgumentMatchers.anyLong()))
        .willReturn(listMemoriaPeticionEvaluacion);
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/memorias", 1L)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-C", "ETI-PEV-INV-ER" })
  void findMemorias_listMemoriaPeticionEvaluacion_ReturnsNoContent() throws Exception {

    List<MemoriaPeticionEvaluacion> listMemoriaPeticionEvaluacion = new ArrayList<MemoriaPeticionEvaluacion>();

    BDDMockito.given(memoriaService.findMemoriaByPeticionEvaluacionMaxVersion(ArgumentMatchers.anyLong()))
        .willReturn(listMemoriaPeticionEvaluacion);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/memorias", 1L)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-C", "ETI-PEV-INV-ER" })
  void newTarea_ReturnsTarea() throws Exception {
    // given: Una tarea nueva
    String nuevaTareaJson = "{\"nombre\": [{\"lang\": \"es\", \"value\": \"Tarea1\"}], \"equipoTrabajo\": {\"id\": 100}, \"memoria\": {\"id\": 200}, \"formacion\": [{\"lang\": \"es\", \"value\": \"Formacion1\"}], \"formacionEspecifica\": {\"id\": 300}, \"organismo\": [{\"lang\": \"es\", \"value\": \"Organismo1\"}], \"anio\": 2020}";

    Tarea tarea = generarMockTarea(1L, "Tarea1");
    EquipoTrabajo equipoTrabajo = generarMockEquipoTrabajo(1L,
        generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1"));

    BDDMockito.given(equipoTrabajoService.findById(1L)).willReturn(equipoTrabajo);
    BDDMockito.given(tareaService.create(ArgumentMatchers.<Tarea>any())).willReturn(tarea);

    // when: Creamos una tarea
    mockMvc
        .perform(MockMvcRequestBuilders
            .post(PETICION_EVALUACION_CONTROLLER_BASE_PATH
                + "/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}/tareas", 1L, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevaTareaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea la nueva tarea y la devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre[0].value").value("Tarea1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-C", "ETI-PEV-INV-ER" })
  void newTarea_EquipoTrabajoIsNull_ReturnsNotFound() throws Exception {
    // given: Una tarea con equipoTrabajo null
    String nuevaTareaJson = "{\"nombre\": [{\"lang\": \"es\", \"value\": \"Tarea1\"}], \"equipoTrabajo\": {\"id\": 100}, \"memoria\": {\"id\": 200}, \"formacion\": [{\"lang\": \"es\", \"value\": \"Formacion1\"}], \"formacionEspecifica\": {\"id\": 300}, \"organismo\": [{\"lang\": \"es\", \"value\": \"Organismo1\"}], \"anio\": 2020}";

    Tarea tarea = generarMockTarea(1L, "Tarea1");

    BDDMockito.given(equipoTrabajoService.findById(1L)).willReturn(null);
    BDDMockito.given(tareaService.create(ArgumentMatchers.<Tarea>any())).willReturn(tarea);

    // when: Creamos una tarea
    mockMvc
        .perform(MockMvcRequestBuilders
            .post(PETICION_EVALUACION_CONTROLLER_BASE_PATH
                + "/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}/tareas", 1L, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevaTareaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 404
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-C", "ETI-PEV-INV-ER" })
  void newTarea_Error_Returns400() throws Exception {
    // given: Una tarea nueva que produce un error al crearse
    String nuevaTareaJson = "{\"id\": 1, \"nombre\": [{\"lang\": \"es\", \"value\": \"Tarea1\"}], \"equipoTrabajo\": {\"id\": 100}, \"memoria\": {\"id\": 200}, \"formacion\": [{\"lang\": \"es\", \"value\": \"Formacion1\"}], \"formacionEspecifica\": {\"id\": 300}, \"organismo\": [{\"lang\": \"es\", \"value\": \"Organismo1\"}], \"anio\": 2020}";

    EquipoTrabajo equipoTrabajo = generarMockEquipoTrabajo(1L,
        generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1"));
    BDDMockito.given(equipoTrabajoService.findById(1L)).willReturn(equipoTrabajo);
    BDDMockito.given(tareaService.create(ArgumentMatchers.<Tarea>any())).willThrow(new IllegalArgumentException());

    // when: Creamos una tarea
    mockMvc
        .perform(MockMvcRequestBuilders
            .post(PETICION_EVALUACION_CONTROLLER_BASE_PATH
                + "/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}/tareas", 1L, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevaTareaJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-V", "ETI-PEV-INV-VR", "ETI-PEV-INV-C", "ETI-PEV-INV-ER",
      "ETI-PEV-MOD-C" })
  void newEquipoTrabajo_ReturnsEquipoTrabajo() throws Exception {
    // given: Un equipo de trabajo nuevo
    String nuevoEquipoTrabajoJson = "{\"personaRef\": \"user-001\", \"peticionEvaluacionId\": 1}";

    PeticionEvaluacion peticionEvaluacion = generarMockPeticionEvaluacion(1L, "peticionEvaluacion1");

    EquipoTrabajo equipoTrabajo = generarMockEquipoTrabajo(1L,
        generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1"));

    BDDMockito.given(peticionEvaluacionService.findById(1L)).willReturn(peticionEvaluacion);
    BDDMockito.given(equipoTrabajoService.create(ArgumentMatchers.<EquipoTrabajo>any())).willReturn(equipoTrabajo);

    // when: Creamos un EquipoTrabajo
    mockMvc
        .perform(MockMvcRequestBuilders
            .post(PETICION_EVALUACION_CONTROLLER_BASE_PATH + "/{idPeticionEvaluacion}/equipos-trabajo", 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoEquipoTrabajoJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Crea el nuevo EquipoTrabajo y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("personaRef").value("user-001"))
        .andExpect(MockMvcResultMatchers.jsonPath("peticionEvaluacionId").value(1));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-V", "ETI-PEV-INV-VR", "ETI-PEV-INV-C", "ETI-PEV-INV-ER",
      "ETI-PEV-MOD-C" })
  void newEquipoTrabajo_PeticionEvaluacionIsNull_ReturnsNotFound() throws Exception {
    // given: Un equipo de trabajo nuevo con peticionEvaluacion null
    String nuevoEquipoTrabajoJson = "{\"personaRef\": \"user-001\", \"peticionEvaluacionId\": 1}";

    EquipoTrabajo equipoTrabajo = generarMockEquipoTrabajo(1L,
        generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1"));

    BDDMockito.doThrow(new PeticionEvaluacionNotFoundException(1L)).when(peticionEvaluacionService).existsById(1L);
    BDDMockito.given(equipoTrabajoService.create(ArgumentMatchers.<EquipoTrabajo>any())).willReturn(equipoTrabajo);
    // when: Creamos un EquipoTrabajo
    mockMvc
        .perform(MockMvcRequestBuilders
            .post(PETICION_EVALUACION_CONTROLLER_BASE_PATH + "/{idPeticionEvaluacion}/equipos-trabajo", 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoEquipoTrabajoJson))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-V", "ETI-PEV-INV-VR", "ETI-PEV-INV-C", "ETI-PEV-INV-ER",
      "ETI-PEV-MOD-C" })
  void newEquipoTrabajo_Error_Returns400() throws Exception {
    // given: Un equipo de trabajo nuevo que produce un error al crearse
    String nuevoEquipoTrabajoJson = "{\"personaRef\": \"user-001\", \"peticionEvaluacion\": {\"titulo\": [{\"lang\": \"es\", \"value\": \"PeticionEvaluacion1\"}]}, \"activo\": \"true\"}}";

    PeticionEvaluacion peticionEvaluacion = generarMockPeticionEvaluacion(1L, "peticionEvaluacion1");
    BDDMockito.given(peticionEvaluacionService.findById(1L)).willReturn(peticionEvaluacion);
    BDDMockito.given(equipoTrabajoService.create(ArgumentMatchers.<EquipoTrabajo>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un equipo de trabajo
    mockMvc
        .perform(MockMvcRequestBuilders
            .post(PETICION_EVALUACION_CONTROLLER_BASE_PATH + "/{idPeticionEvaluacion}/equipos-trabajo", 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoEquipoTrabajoJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-ER" })
  void removeTarea_ReturnsOk() throws Exception {

    long idPeticionEvaluacion = 1L;
    long idEquipoTrabajo = 1L;
    long idTarea = 1L;

    BDDMockito.given(tareaService.findById(idTarea)).willReturn(generarMockTarea(1L, "Tarea1"));
    BDDMockito.doNothing().when(tareaService).delete(idTarea);

    mockMvc
        .perform(MockMvcRequestBuilders
            .delete(
                PETICION_EVALUACION_CONTROLLER_BASE_PATH
                    + "/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}/tareas/{idTarea}",
                idPeticionEvaluacion, idEquipoTrabajo, idTarea)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-ER" })
  void removeTarea_TareaIsNull_ReturnsNotFound() throws Exception {
    // given: Una tarea que es null
    long idPeticionEvaluacion = 1L;
    long idEquipoTrabajo = 1L;
    long idTarea = 1L;

    BDDMockito.given(tareaService.findById(idTarea)).willReturn(null);
    BDDMockito.doNothing().when(tareaService).delete(idTarea);
    // when: Tratamos de borrar la tarea
    mockMvc
        .perform(MockMvcRequestBuilders
            .delete(
                PETICION_EVALUACION_CONTROLLER_BASE_PATH
                    + "/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}/tareas/{idTarea}",
                idPeticionEvaluacion, idEquipoTrabajo, idTarea)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        // then: Devuelve un error 404
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-ER" })
  void removeEquipoTrabajo_ReturnsOk() throws Exception {
    // given: Un equipo de trabajo de la petición de evaluación
    long idPeticionEvaluacion = 1L;
    long idEquipoTrabajo = 111L;

    BDDMockito.given(equipoTrabajoService.findById(idEquipoTrabajo))
        .willReturn((generarMockEquipoTrabajo(1L, generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1"))));

    BDDMockito.doNothing().when(tareaService).deleteByEquipoTrabajo(idEquipoTrabajo);
    BDDMockito.doNothing().when(equipoTrabajoService).delete(idEquipoTrabajo);

    // when: Borramos el equipo de trabajo
    mockMvc.perform(MockMvcRequestBuilders
        .delete(PETICION_EVALUACION_CONTROLLER_BASE_PATH + "/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}",
            idPeticionEvaluacion, idEquipoTrabajo)
        .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        // then: Devuelve 200
        .andDo(SgiMockMvcResultHandlers.printOnError())
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-ER" })
  void removeEquipoTrabajo_EquipoTrabajoIsNull_ReturnsNotFound() throws Exception {
    // given: Un equipo de trabajo que es null
    long idPeticionEvaluacion = 1L;
    long idEquipoTrabajo = 111L;

    BDDMockito.given(equipoTrabajoService.findById(idEquipoTrabajo)).willReturn(null);
    BDDMockito.doNothing().when(tareaService).deleteByEquipoTrabajo(idEquipoTrabajo);
    BDDMockito.doNothing().when(equipoTrabajoService).delete(idEquipoTrabajo);
    // when: Tratamos de borrar el equipo de trabajo
    mockMvc
        .perform(
            MockMvcRequestBuilders
                .delete(
                    PETICION_EVALUACION_CONTROLLER_BASE_PATH
                        + "/{idPeticionEvaluacion}/equipos-trabajo/{idEquipoTrabajo}",
                    idPeticionEvaluacion, idEquipoTrabajo)
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        // then: Devuelve un error 404
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-VR", "ETI-PEV-V" })
  void findAllPeticionEvaluacionMemoria_Unlimited_ReturnsFullPeticionEvaluacionList() throws Exception {
    // given: One hundred PeticionEvaluacion
    List<PeticionEvaluacionWithIsEliminable> peticionEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      peticionEvaluaciones.add(generarMockPeticionEvaluacionWithIsEliminable(Long.valueOf(i),
          "PeticionEvaluacion" + String.format("%03d", i)));
    }

    BDDMockito
        .given(peticionEvaluacionService.findAllPeticionesWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any(), ArgumentMatchers.anyString()))
        .willReturn(new PageImpl<>(peticionEvaluaciones));

    // when: find unlimited
    mockMvc
        .perform(MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + MEMORIAS_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred PeticionEvaluacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-VR", "ETI-PEV-V" })
  void findAllPeticionEvaluacionMemoria_ReturnsNoContent() throws Exception {
    List<PeticionEvaluacionWithIsEliminable> peticionEvaluaciones = new ArrayList<>();

    BDDMockito
        .given(peticionEvaluacionService.findAllPeticionesWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any(), ArgumentMatchers.anyString()))
        .willReturn(new PageImpl<>(peticionEvaluaciones));

    mockMvc
        .perform(MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + MEMORIAS_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-VR", "ETI-PEV-V" })
  void findAllByPersonaRef_ReturnOk() throws Exception {
    // given: One hundred PeticionEvaluacion
    List<PeticionEvaluacion> peticionEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      peticionEvaluaciones
          .add(generarMockPeticionEvaluacion(Long.valueOf(i), "PeticionEvaluacion" + String.format("%03d", i)));
    }
    BDDMockito
        .given(peticionEvaluacionService.findAllByPersonaRef(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<Pageable>any(), ArgumentMatchers.anyString()))
        .willAnswer(new Answer<Page<PeticionEvaluacion>>() {
          @Override
          public Page<PeticionEvaluacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<PeticionEvaluacion> content = peticionEvaluaciones.subList(fromIndex, toIndex);
            return new PageImpl<>(content, pageable, peticionEvaluaciones.size());
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PERSONA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked PeticionEvaluacions are returned with the right page
        // information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<PeticionEvaluacion> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<PeticionEvaluacion>>() {
        });

    // containing titulo='PeticionEvaluacion031' to 'PeticionEvaluacion040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      PeticionEvaluacion peticionEvaluacion = actual.get(i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(peticionEvaluacion.getTitulo(), Language.ES))
          .isEqualTo("PeticionEvaluacion" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-VR", "ETI-PEV-V" })
  void findAllByPersonaRef_ReturnsNoContent() throws Exception {
    // given: Peticion de evaluacion empty
    List<PeticionEvaluacion> peticionEvaluaciones = new ArrayList<>();

    BDDMockito
        .given(peticionEvaluacionService.findAllByPersonaRef(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<Pageable>any(), ArgumentMatchers.anyString()))
        .willReturn(new PageImpl<>(peticionEvaluaciones));

    mockMvc
        .perform(MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PERSONA_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-VR", "ETI-PEV-V" })
  void findAllPeticionEvaluacionMemoria_WithPaging_ReturnsPeticionEvaluacionSubList() throws Exception {
    // given: One hundred PeticionEvaluacion
    List<PeticionEvaluacion> peticionEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      peticionEvaluaciones
          .add(generarMockPeticionEvaluacion(Long.valueOf(i), "PeticionEvaluacion" + String.format("%03d", i)));
    }

    BDDMockito
        .given(peticionEvaluacionService.findAllPeticionesWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any(), ArgumentMatchers.anyString()))
        .willAnswer(new Answer<Page<PeticionEvaluacion>>() {
          @Override
          public Page<PeticionEvaluacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<PeticionEvaluacion> content = peticionEvaluaciones.subList(fromIndex, toIndex);
            return new PageImpl<>(content, pageable, peticionEvaluaciones.size());
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + MEMORIAS_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", "3").header("X-Page-Size", "10")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: the asked PeticionEvaluacions are returned with the right page
        // information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<PeticionEvaluacion> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<PeticionEvaluacion>>() {
        });

    // containing titulo='PeticionEvaluacion031' to 'PeticionEvaluacion040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      PeticionEvaluacion peticionEvaluacion = actual.get(i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(peticionEvaluacion.getTitulo(), Language.ES))
          .isEqualTo("PeticionEvaluacion" + String.format("%03d", j));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-VR", "ETI-PEV-V" })
  void findAllPeticionEvaluacionMemoria_WithSearchQuery_ReturnsFilteredPeticionEvaluacionList()
      throws Exception {
    // given: One hundred PeticionEvaluacion and a search query
    List<PeticionEvaluacion> peticionEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      peticionEvaluaciones
          .add(generarMockPeticionEvaluacion(Long.valueOf(i), "PeticionEvaluacion" + String.format("%03d", i)));
    }
    String query = "titulo~PeticionEvaluacion%,id:5";

    BDDMockito
        .given(peticionEvaluacionService.findAllPeticionesWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any(), ArgumentMatchers.anyString()))
        .willAnswer(new Answer<Page<PeticionEvaluacion>>() {
          @Override
          public Page<PeticionEvaluacion> answer(InvocationOnMock invocation) throws Throwable {
            List<PeticionEvaluacion> content = new ArrayList<>();
            for (PeticionEvaluacion peticionEvaluacion : peticionEvaluaciones) {
              if (I18nHelper.getValueForLanguage(peticionEvaluacion.getTitulo(), Language.ES)
                  .startsWith("PeticionEvaluacion")
                  && peticionEvaluacion.getId().equals(5L)) {
                content.add(peticionEvaluacion);
              }
            }
            return new PageImpl<>(content);
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(PETICION_EVALUACION_CONTROLLER_BASE_PATH + MEMORIAS_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Get a page one hundred PeticionEvaluacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  @Test
  void newPeticionEvaluacionFromSolicitudCsp_WithClient_Returns201() throws Exception {
    // given: un input válido con las propiedades adicionales del DTO de CSP y un
    // token de cliente de servicio
    String inputJson = mapper.writeValueAsString(generarMockSolicitudCspInput()).replaceFirst("\\{",
        "{\"id\": null, \"codigo\": null, \"activo\": null, \"externo\": null, \"tipoActividad\": null, ");
    PeticionEvaluacion peticionEvaluacion = generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1");
    BDDMockito.given(peticionEvaluacionConverter.convert(ArgumentMatchers.<PeticionEvaluacionSolicitudCspInput>any()))
        .willReturn(peticionEvaluacion);
    BDDMockito.given(peticionEvaluacionService.createFromSolicitudCsp(ArgumentMatchers.<PeticionEvaluacion>any()))
        .willReturn(peticionEvaluacion);

    // when: se crea la petición desde CSP
    mockMvc
        .perform(MockMvcRequestBuilders.post(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_SOLICITUD_CSP)
            .with(clientJwt()).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(inputJson))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: devuelve 201 con la petición creada
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("titulo[0].value").value("PeticionEvaluacion1"));
  }

  @ParameterizedTest
  @NullAndEmptySource
  void newPeticionEvaluacionFromSolicitudCsp_WithoutTitulo_Returns400(List<I18nFieldValueDto> titulo)
      throws Exception {
    // given: un input sin título
    PeticionEvaluacionSolicitudCspInput input = generarMockSolicitudCspInput();
    input.setTitulo(titulo);

    // when: se crea la petición desde CSP
    mockMvc
        .perform(MockMvcRequestBuilders.post(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_SOLICITUD_CSP)
            .with(clientJwt()).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(input)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: devuelve 400 con el error en el campo titulo y no crea nada
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("errors[0].field").value("titulo"));
    BDDMockito.verify(peticionEvaluacionService, Mockito.never())
        .createFromSolicitudCsp(ArgumentMatchers.<PeticionEvaluacion>any());
  }

  @Test
  void newPeticionEvaluacionFromSolicitudCsp_WithoutPersonaRef_Returns400() throws Exception {
    // given: un input sin personaRef
    PeticionEvaluacionSolicitudCspInput input = generarMockSolicitudCspInput();
    input.setPersonaRef(null);

    // when: se crea la petición desde CSP
    mockMvc
        .perform(MockMvcRequestBuilders.post(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_SOLICITUD_CSP)
            .with(clientJwt()).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(input)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: devuelve 400 con el error en el campo personaRef y no crea nada
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("errors[0].field").value("personaRef"));
    BDDMockito.verify(peticionEvaluacionService, Mockito.never())
        .createFromSolicitudCsp(ArgumentMatchers.<PeticionEvaluacion>any());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "SCOPE_sgi-eti", "ETI-PEV-INV-C", "ETI-PEV-MOD-C" })
  void newPeticionEvaluacionFromSolicitudCsp_WithUser_Returns403() throws Exception {
    // given: un input válido y un usuario (no cliente de servicio)
    PeticionEvaluacionSolicitudCspInput input = generarMockSolicitudCspInput();

    // when: se crea la petición desde CSP
    mockMvc
        .perform(MockMvcRequestBuilders.post(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_SOLICITUD_CSP)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(input)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: devuelve 403 y no crea nada
        .andExpect(MockMvcResultMatchers.status().isForbidden());
    BDDMockito.verify(peticionEvaluacionService, Mockito.never())
        .createFromSolicitudCsp(ArgumentMatchers.<PeticionEvaluacion>any());
  }

  @Test
  void updateEstadoFinanciacion_WithClient_Returns200() throws Exception {
    // given: un nuevo estado de financiación y un token de cliente de servicio
    PeticionEvaluacionEstadoFinanciacionInput input = new PeticionEvaluacionEstadoFinanciacionInput(
        EstadoFinanciacionDto.CONCEDIDO);
    BDDMockito.given(peticionEvaluacionConverter.convert(EstadoFinanciacionDto.CONCEDIDO))
        .willReturn(EstadoFinanciacion.CONCEDIDO);

    // when: se actualiza el estado de financiación
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_ESTADO_FINANCIACION, 1L)
            .with(clientJwt()).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(input)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: devuelve 200 sin cuerpo y delega en el servicio con el estado
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(""));
    BDDMockito.verify(peticionEvaluacionService).updateEstadoFinanciacion(1L, EstadoFinanciacion.CONCEDIDO);
  }

  @Test
  void updateEstadoFinanciacion_WithoutEstado_Returns400() throws Exception {
    // given: un input sin estado de financiación
    PeticionEvaluacionEstadoFinanciacionInput input = new PeticionEvaluacionEstadoFinanciacionInput();

    // when: se actualiza el estado de financiación
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_ESTADO_FINANCIACION, 1L)
            .with(clientJwt()).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(input)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: devuelve 400 con el error en el campo estadoFinanciacion
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("errors[0].field").value("estadoFinanciacion"));
    BDDMockito.verify(peticionEvaluacionService, Mockito.never())
        .updateEstadoFinanciacion(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
  }

  @Test
  void updateEstadoFinanciacion_NotFound_Returns404() throws Exception {
    // given: una petición de evaluación que no existe
    PeticionEvaluacionEstadoFinanciacionInput input = new PeticionEvaluacionEstadoFinanciacionInput(
        EstadoFinanciacionDto.CONCEDIDO);
    BDDMockito.given(peticionEvaluacionConverter.convert(EstadoFinanciacionDto.CONCEDIDO))
        .willReturn(EstadoFinanciacion.CONCEDIDO);
    BDDMockito.willThrow(new PeticionEvaluacionNotFoundException(1L)).given(peticionEvaluacionService)
        .updateEstadoFinanciacion(1L, EstadoFinanciacion.CONCEDIDO);

    // when: se actualiza el estado de financiación
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_ESTADO_FINANCIACION, 1L)
            .with(clientJwt()).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(input)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: devuelve 404
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "SCOPE_sgi-eti", "ETI-PEV-E" })
  void updateEstadoFinanciacion_WithUser_Returns403() throws Exception {
    // given: un nuevo estado de financiación y un usuario (no cliente de servicio)
    PeticionEvaluacionEstadoFinanciacionInput input = new PeticionEvaluacionEstadoFinanciacionInput(
        EstadoFinanciacionDto.CONCEDIDO);

    // when: se actualiza el estado de financiación
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(PETICION_EVALUACION_CONTROLLER_BASE_PATH + PATH_ESTADO_FINANCIACION, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(input)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: devuelve 403 y no actualiza nada
        .andExpect(MockMvcResultMatchers.status().isForbidden());
    BDDMockito.verify(peticionEvaluacionService, Mockito.never())
        .updateEstadoFinanciacion(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
  }

  /**
   * Autenticación de un cliente de servicio con el scope de ETI.
   *
   * @return el post-procesador de la petición con el token del cliente.
   */
  private static JwtRequestPostProcessor clientJwt() {
    return SecurityMockMvcRequestPostProcessors.jwt()
        .jwt(jwt -> jwt.claim(SgiJwtAuthenticationConverter.CLIENT_ID, "csp-service"))
        .authorities(new SimpleGrantedAuthority("SCOPE_sgi-eti"));
  }

  /**
   * Crea un {@link PeticionEvaluacionSolicitudCspInput} válido.
   *
   * @return el input.
   */
  private PeticionEvaluacionSolicitudCspInput generarMockSolicitudCspInput() {
    return PeticionEvaluacionSolicitudCspInput.builder()
        .titulo(List.of(new I18nFieldValueDto(Language.ES, "PeticionEvaluacion1")))
        .personaRef("user-001")
        .existeFinanciacion(Boolean.TRUE)
        .estadoFinanciacion(EstadoFinanciacionDto.SOLICITADO)
        .solicitudConvocatoriaRef("1")
        .checklistId(1L)
        .build();
  }

  /**
   * Función que devuelve un objeto PeticionEvaluacion
   * 
   * @param id     id del PeticionEvaluacion
   * @param titulo el título de PeticionEvaluacion
   * @return el objeto PeticionEvaluacion
   */
  private PeticionEvaluacion generarMockPeticionEvaluacion(Long id, String titulo) {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    Set<PeticionEvaluacionTitulo> tit = new HashSet<>();
    tit.add(new PeticionEvaluacionTitulo(Language.ES, titulo));
    Set<PeticionEvaluacionResumen> resumen = new HashSet<>();
    resumen.add(new PeticionEvaluacionResumen(Language.ES, "Resumen" + id));
    Set<PeticionEvaluacionObjetivos> objetivos = new HashSet<>();
    objetivos.add(new PeticionEvaluacionObjetivos(Language.ES, "Objetivos" + id));
    Set<PeticionEvaluacionDisMetodologico> disMetodologico = new HashSet<>();
    disMetodologico.add(new PeticionEvaluacionDisMetodologico(Language.ES, "DiseñoMetodologico" + id));
    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo" + id);
    peticionEvaluacion.setDisMetodologico(disMetodologico);
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos(objetivos);
    peticionEvaluacion.setResumen(resumen);
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria" + id);
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo(tit);
    peticionEvaluacion.setPersonaRef("user-00" + id);
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    return peticionEvaluacion;
  }

  /**
   * Crea una memoria de petición evaluación.
   * 
   * @param id identificador.
   */
  private MemoriaPeticionEvaluacion generarMockMemoriaPeticionEvaluacion(Long id) {

    MemoriaPeticionEvaluacion memoria = new MemoriaPeticionEvaluacion();
    memoria.setId(id);

    Comite comite = new Comite();
    comite.setId(id);
    memoria.setComite(comite);

    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(id);
    memoria.setEstadoActual(tipoEstadoMemoria);

    memoria.setFechaEvaluacion(Instant.parse("2020-05-15T00:00:00Z"));
    memoria.setFechaLimite(Instant.parse("2020-08-18T23:59:59Z"));
    return memoria;
  }

  /**
   * Función que devuelve un objeto EquipoTrabajo
   * 
   * @param id                 id del EquipoTrabajo
   * @param peticionEvaluacion la PeticionEvaluacion del EquipoTrabajo
   * @return el objeto EquipoTrabajo
   */
  private EquipoTrabajo generarMockEquipoTrabajo(Long id, PeticionEvaluacion peticionEvaluacion) {

    EquipoTrabajo equipoTrabajo = new EquipoTrabajo();
    equipoTrabajo.setId(id);
    equipoTrabajo.setPeticionEvaluacionId(peticionEvaluacion.getId());
    equipoTrabajo.setPersonaRef("user-00" + id);

    return equipoTrabajo;
  }

  /**
   * Función que devuelve un objeto EquipoTrabajoWithIsEliminable
   * 
   * @param id                 id del EquipoTrabajo
   * @param peticionEvaluacion la PeticionEvaluacion del EquipoTrabajo
   * @return el objeto EquipoTrabajo
   */
  private EquipoTrabajoWithIsEliminable generarMockEquipoTrabajoWithIsEliminable(Long id,
      PeticionEvaluacion peticionEvaluacion) {

    EquipoTrabajoWithIsEliminable equipoTrabajo = new EquipoTrabajoWithIsEliminable();
    equipoTrabajo.setId(id);
    equipoTrabajo.setPeticionEvaluacionId(peticionEvaluacion.getId());
    equipoTrabajo.setPersonaRef("user-00" + id);
    equipoTrabajo.setEliminable(true);

    return equipoTrabajo;
  }

  /**
   * Función que devuelve un objeto Tarea
   * 
   * @param id          id de la tarea
   * @param descripcion descripcion de la tarea
   * @return el objeto Tarea
   */
  private Tarea generarMockTarea(Long id, String descripcion) {
    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);

    EquipoTrabajo equipoTrabajo = new EquipoTrabajo();
    equipoTrabajo.setId(id);
    equipoTrabajo.setPeticionEvaluacionId(peticionEvaluacion.getId());

    Memoria memoria = new Memoria();
    memoria.setId(200L);

    FormacionEspecifica formacionEspecifica = new FormacionEspecifica();
    formacionEspecifica.setId(300L);

    TipoTarea tipoTarea = new TipoTarea();
    tipoTarea.setId(1L);

    Set<TareaNombre> nombre = new HashSet<>();
    nombre.add(new TareaNombre(Language.ES, descripcion));
    Set<TareaFormacion> formacion = new HashSet<>();
    formacion.add(new TareaFormacion(Language.ES, "Formacion" + id));
    Set<TareaOrganismo> organismo = new HashSet<>();
    organismo.add(new TareaOrganismo(Language.ES, "Organismo" + id));

    Tarea tarea = new Tarea();
    tarea.setId(id);
    tarea.setEquipoTrabajo(equipoTrabajo);
    tarea.setMemoria(memoria);
    tarea.setNombre(nombre);
    tarea.setFormacion(formacion);
    tarea.setFormacionEspecifica(formacionEspecifica);
    tarea.setOrganismo(organismo);
    tarea.setAnio(2020);
    tarea.setTipoTarea(tipoTarea);

    return tarea;
  }

  /**
   * Función que devuelve un objeto TareaWithIsEliminable
   * 
   * @param id          id de la tarea
   * @param descripcion descripcion de la tarea
   * @return el objeto TareaWithIsEliminable
   */
  private TareaWithIsEliminable generarMockTareaWithIsEliminable(Long id, String descripcion) {
    EquipoTrabajo equipoTrabajo = new EquipoTrabajo();
    equipoTrabajo.setId(id);
    equipoTrabajo.setPeticionEvaluacionId(id);

    Memoria memoria = new Memoria();
    memoria.setId(200L);

    FormacionEspecifica formacionEspecifica = new FormacionEspecifica();
    formacionEspecifica.setId(300L);

    TipoTarea tipoTarea = new TipoTarea();
    tipoTarea.setId(1L);

    Set<TareaNombre> nombre = new HashSet<>();
    nombre.add(new TareaNombre(Language.ES, descripcion));
    Set<TareaFormacion> formacion = new HashSet<>();
    formacion.add(new TareaFormacion(Language.ES, "Formacion" + id));
    Set<TareaOrganismo> organismo = new HashSet<>();
    organismo.add(new TareaOrganismo(Language.ES, "Organismo" + id));

    TareaWithIsEliminable tarea = new TareaWithIsEliminable();
    tarea.setId(id);
    tarea.setEquipoTrabajo(equipoTrabajo);
    tarea.setMemoria(memoria);
    tarea.setNombre(nombre);
    tarea.setFormacion(formacion);
    tarea.setFormacionEspecifica(formacionEspecifica);
    tarea.setOrganismo(organismo);
    tarea.setEliminable(true);

    return tarea;
  }

  private PeticionEvaluacionWithIsEliminable generarMockPeticionEvaluacionWithIsEliminable(Long id, String titulo) {
    return new PeticionEvaluacionWithIsEliminable(generarMockPeticionEvaluacion(id, titulo), true);
  }

}
