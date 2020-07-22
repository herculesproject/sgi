package org.crue.hercules.sgi.eti.controller;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.EvaluadorEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.EvaluadorEvaluacion;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.service.EvaluadorEvaluacionService;
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

/**
 * EvaluadorEvaluacionControllerTest
 */
@WebMvcTest(EvaluadorEvaluacionController.class)
public class EvaluadorEvaluacionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private EvaluadorEvaluacionService evaluadorEvaluacionService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH = "/evaluadorevaluaciones";

  @Test
  public void getEvaluadorEvaluacion_WithId_ReturnsEvaluadorEvaluacion() throws Exception {
    BDDMockito.given(evaluadorEvaluacionService.findById(ArgumentMatchers.anyLong()))
        .willReturn((generarMockEvaluadorEvaluacion(1L)));

    mockMvc.perform(MockMvcRequestBuilders.get(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("evaluador.resumen").value("Evaluador"));
    ;
  }

  @Test
  public void getEvaluadorEvaluacion_NotFound_Returns404() throws Exception {
    BDDMockito.given(evaluadorEvaluacionService.findById(ArgumentMatchers.anyLong()))
        .will((InvocationOnMock invocation) -> {
          throw new EvaluadorEvaluacionNotFoundException(invocation.getArgument(0));
        });
    mockMvc.perform(MockMvcRequestBuilders.get(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void newEvaluadorEvaluacion_ReturnsEvaluadorEvaluacion() throws Exception {
    // given: Un EvaluadorEvaluacion nuevo
    String nuevoEvaluadorEvaluacionJson = "{\"evaluador\": {\"resumen\": \"Evaluador\", \"activo\": \"true\"}, \"evaluacion\": {\"id\": 1, \"memoria\":{\"id\": 1, \"numReferencia\": \"numRef-5598\", \"peticionEvaluacion\": {\"id\": 1, \"titulo\": \"PeticionEvaluacion1\"},"
        + " \"comite\": {\"comite\": \"Comite1\"},\"titulo\": \"Memoria1\", \"numReferencia\": \"userRef-55\", \"fechaEstado\": \"2020-06-09\","
        + "\"tipoMemoria\": {\"id\": 1, \"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}, \"requiereRetrospectiva\": \"false\",\"version\": \"1\"}, \"dictamen\": {\"id\": 1, \"nombre\": \"Dictamen1\", \"activo\": \"true\"}, \"esRevMinima\": \"true\", \"activo\": \"true\"}}";

    EvaluadorEvaluacion evaluadorEvaluacion = generarMockEvaluadorEvaluacion(1L);

    BDDMockito.given(evaluadorEvaluacionService.create(ArgumentMatchers.<EvaluadorEvaluacion>any()))
        .willReturn(evaluadorEvaluacion);

    // when: Creamos un EvaluadorEvaluacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON).content(nuevoEvaluadorEvaluacionJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Crea el nuevo EvaluadorEvaluacion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("evaluador.resumen").value("Evaluador"))
        .andExpect(MockMvcResultMatchers.jsonPath("evaluacion.memoria.titulo").value("Memoria1"))
        .andExpect(MockMvcResultMatchers.jsonPath("evaluacion.dictamen.nombre").value("Dictamen1"));

  }

  @Test
  public void newEvaluadorEvaluacion_Error_Returns400() throws Exception {
    // given: Un EvaluadorEvaluacion nuevo que produce un error al crearse
    String nuevoEvaluadorEvaluacionJson = "{\"evaluador\": {\"resumen\": \"Evaluador\", \"activo\": \"true\"}, \"evaluacion\": {\"id\": 1}}";

    BDDMockito.given(evaluadorEvaluacionService.create(ArgumentMatchers.<EvaluadorEvaluacion>any()))
        .willThrow(new IllegalArgumentException());

    // when: Creamos un EvaluadorEvaluacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON).content(nuevoEvaluadorEvaluacionJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Devueve un error 400
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  public void replaceEvaluadorEvaluacion_ReturnsEvaluadorEvaluacion() throws Exception {
    // given: Un EvaluadorEvaluacion a modificar
    String replaceEvaluadorEvaluacionJson = "{\"id\": 1, \"evaluador\": {\"resumen\": \"Evaluador\", \"activo\": \"true\"}, \"evaluacion\": {\"id\": 1, \"memoria\":{\"id\": 1, \"numReferencia\": \"numRef-5598\", \"peticionEvaluacion\": {\"id\": 1, \"titulo\": \"PeticionEvaluacion1\"},"
        + " \"comite\": {\"comite\": \"Comite1\"},\"titulo\": \"Memoria1 replace\", \"numReferencia\": \"userRef-55\", \"fechaEstado\": \"2020-06-09\","
        + "\"tipoMemoria\": {\"id\": 1, \"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}, \"requiereRetrospectiva\": \"false\",\"version\": \"1\"}, \"dictamen\": {\"id\": 1, \"nombre\": \"Dictamen1\", \"activo\": \"true\"}, \"esRevMinima\": \"true\", \"activo\": \"true\"}}";

    EvaluadorEvaluacion evaluadorEvaluacion = generarMockEvaluadorEvaluacion(1L);
    evaluadorEvaluacion.getEvaluador().setResumen("Evaluador actualizado");
    evaluadorEvaluacion.getEvaluacion().getMemoria().setTitulo("Memoria actualizada");
    evaluadorEvaluacion.getEvaluacion().getDictamen().setNombre("Dictamen actualizado");

    BDDMockito.given(evaluadorEvaluacionService.update(ArgumentMatchers.<EvaluadorEvaluacion>any()))
        .willReturn(evaluadorEvaluacion);

    mockMvc
        .perform(MockMvcRequestBuilders.put(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON).content(replaceEvaluadorEvaluacionJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Modifica el EvaluadorEvaluacion y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("evaluador.resumen").value("Evaluador actualizado"))
        .andExpect(MockMvcResultMatchers.jsonPath("evaluacion.memoria.titulo").value("Memoria actualizada"))
        .andExpect(MockMvcResultMatchers.jsonPath("evaluacion.dictamen.nombre").value("Dictamen actualizado"));
  }

  @Test
  public void replaceEvaluadorEvaluacion_NotFound() throws Exception {
    // given: Un EvaluadorEvaluacion a modificar
    String replaceEvaluadorEvaluacionJson = "{\"id\": 1, \"evaluador\": {\"resumen\": \"Evaluador\", \"activo\": \"true\"}, \"evaluacion\": {\"id\": 1, \"memoria\":{\"id\": 1, \"numReferencia\": \"numRef-5598\", \"peticionEvaluacion\": {\"id\": 1, \"titulo\": \"PeticionEvaluacion1\"},"
        + " \"comite\": {\"comite\": \"Comite1\"},\"titulo\": \"Memoria1 replace\", \"numReferencia\": \"userRef-55\", \"fechaEstado\": \"2020-06-09\","
        + "\"tipoMemoria\": {\"id\": 1, \"nombre\": \"TipoMemoria1\", \"activo\": \"true\"}, \"requiereRetrospectiva\": \"false\",\"version\": \"1\"}, \"dictamen\": {\"id\": 1, \"nombre\": \"Dictamen1\", \"activo\": \"true\"}, \"esRevMinima\": \"true\", \"activo\": \"true\"}}";

    BDDMockito.given(evaluadorEvaluacionService.update(ArgumentMatchers.<EvaluadorEvaluacion>any()))
        .will((InvocationOnMock invocation) -> {
          throw new EvaluadorEvaluacionNotFoundException(((EvaluadorEvaluacion) invocation.getArgument(0)).getId());
        });
    mockMvc
        .perform(MockMvcRequestBuilders.put(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON).content(replaceEvaluadorEvaluacionJson))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  @Test
  public void removeEvaluadorEvaluacion_ReturnsOk() throws Exception {
    BDDMockito.given(evaluadorEvaluacionService.findById(ArgumentMatchers.anyLong()))
        .willReturn(generarMockEvaluadorEvaluacion(1L));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void findAll_Unlimited_ReturnsFullEvaluadorEvaluacionList() throws Exception {
    // given: One hundred EvaluadorEvaluacion
    List<EvaluadorEvaluacion> evaluadorEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluadorEvaluaciones.add(generarMockEvaluadorEvaluacion(Long.valueOf(i)));
    }

    BDDMockito.given(evaluadorEvaluacionService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(evaluadorEvaluaciones));

    // when: find unlimited
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred EvaluadorEvaluacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(100)));
  }

  @Test
  public void findAll_WithPaging_ReturnsEvaluadorEvaluacionSubList() throws Exception {
    // given: One hundred EvaluadorEvaluacion
    List<EvaluadorEvaluacion> evaluadorEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluadorEvaluaciones.add(generarMockEvaluadorEvaluacion(Long.valueOf(i)));
    }

    BDDMockito.given(evaluadorEvaluacionService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<EvaluadorEvaluacion>>() {
          @Override
          public Page<EvaluadorEvaluacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<EvaluadorEvaluacion> content = evaluadorEvaluaciones.subList(fromIndex, toIndex);
            Page<EvaluadorEvaluacion> page = new PageImpl<>(content, pageable, evaluadorEvaluaciones.size());
            return page;
          }
        });

    // when: get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH).header("X-Page", "3")
            .header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: the asked EvaluadorEvaluacions are returned with the right page
        // information
        // in headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // this uses a TypeReference to inform Jackson about the Lists's generic type
    List<EvaluadorEvaluacion> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<EvaluadorEvaluacion>>() {
        });

    // containing id='31' to '40'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      EvaluadorEvaluacion EvaluadorEvaluacion = actual.get(i);
      Assertions.assertThat(EvaluadorEvaluacion.getId()).isEqualTo(j);
    }
  }

  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredEvaluadorEvaluacionList() throws Exception {
    // given: One hundred EvaluadorEvaluacion and a search query
    List<EvaluadorEvaluacion> evaluadorEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluadorEvaluaciones.add(generarMockEvaluadorEvaluacion(Long.valueOf(i)));
    }
    String query = "id:5";

    BDDMockito.given(evaluadorEvaluacionService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<EvaluadorEvaluacion>>() {
          @Override
          public Page<EvaluadorEvaluacion> answer(InvocationOnMock invocation) throws Throwable {
            List<QueryCriteria> queryCriterias = invocation.<List<QueryCriteria>>getArgument(0);

            List<EvaluadorEvaluacion> content = new ArrayList<>();
            for (EvaluadorEvaluacion evaluadorEvaluacion : evaluadorEvaluaciones) {
              boolean add = true;
              for (QueryCriteria queryCriteria : queryCriterias) {
                Field field = ReflectionUtils.findField(EvaluadorEvaluacion.class, queryCriteria.getKey());
                field.setAccessible(true);
                String fieldValue = ReflectionUtils.getField(field, evaluadorEvaluacion).toString();
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
                content.add(evaluadorEvaluacion);
              }
            }
            Page<EvaluadorEvaluacion> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: find with search query
    mockMvc
        .perform(MockMvcRequestBuilders.get(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH).param("q", query)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Get a page one hundred EvaluadorEvaluacion
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  /**
   * Función que devuelve un objeto EvaluadorEvaluacion
   * 
   * @param id id del EvaluadorEvaluacion
   * @return el objeto EvaluadorEvaluacion
   */

  public EvaluadorEvaluacion generarMockEvaluadorEvaluacion(Long id) {
    CargoComite cargoComite = new CargoComite();
    cargoComite.setId(1L);
    cargoComite.setNombre("CargoComite1");
    cargoComite.setActivo(Boolean.TRUE);

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    Evaluador evaluador = new Evaluador();
    evaluador.setId(1L);
    evaluador.setCargoComite(cargoComite);
    evaluador.setComite(comite);
    evaluador.setFechaAlta(LocalDate.now());
    evaluador.setFechaBaja(LocalDate.now());
    evaluador.setResumen("Evaluador");
    evaluador.setUsuarioRef("user-001");
    evaluador.setActivo(Boolean.TRUE);

    Dictamen dictamen = new Dictamen();
    dictamen.setId(id);
    dictamen.setNombre("Dictamen" + id);
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

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(1L);
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    Memoria memoria = new Memoria(id, "numRef-001", peticionEvaluacion, comite, "Memoria" + id, "user-00" + id,
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
    convocatoriaReunion.setCodigo("CR-" + id);
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

    EvaluadorEvaluacion evaluadorEvaluacion = new EvaluadorEvaluacion(id, evaluador, evaluacion);

    return evaluadorEvaluacion;
  }

}
