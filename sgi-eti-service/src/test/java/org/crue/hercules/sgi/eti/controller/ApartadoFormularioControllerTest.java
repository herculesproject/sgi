package org.crue.hercules.sgi.eti.controller;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ApartadoFormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.ApartadoFormulario;
import org.crue.hercules.sgi.eti.model.BloqueFormulario;
import org.crue.hercules.sgi.eti.model.ComponenteFormulario;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.service.ApartadoFormularioService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ReflectionUtils;

/**
 * ApartadoFormularioControllerTest
 */
@WebMvcTest(ApartadoFormularioController.class)
public class ApartadoFormularioControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private ApartadoFormularioService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String APARTADO_FORMULARIO_CONTROLLER_BASE_PATH = "/apartadoformularios";

  @Test
  public void create_ReturnsApartadoFormulario() throws Exception {

    // given: Nueva entidad sin Id
    final String url = new StringBuilder(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH).toString();

    ApartadoFormulario response = getMockData(1L, 1L, 1L, null);
    String nuevoApartadoFormularioJson = mapper.writeValueAsString(response);

    BDDMockito.given(service.create(ArgumentMatchers.<ApartadoFormulario>any())).willReturn(response);

    // when: Se crea la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoApartadoFormularioJson))
        .andDo(MockMvcResultHandlers.print())
        // then: La entidad se crea correctamente
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(response.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("bloqueFormulario").value(response.getBloqueFormulario()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(response.getNombre()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("apartadoFormularioPadre").value(response.getApartadoFormularioPadre()))
        .andExpect(MockMvcResultMatchers.jsonPath("orden").value(response.getOrden()))
        .andExpect(MockMvcResultMatchers.jsonPath("componenteFormulario").value(response.getComponenteFormulario()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(response.getActivo()));
  }

  @Test
  public void create_WithId_Returns400() throws Exception {

    // given: Nueva entidad con Id
    final String url = new StringBuilder(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH).toString();
    String nuevoApartadoFormularioJson = mapper.writeValueAsString(getMockData(1L, 1L, 1L, null));

    BDDMockito.given(service.create(ArgumentMatchers.<ApartadoFormulario>any()))
        .willThrow(new IllegalArgumentException());

    // when: Se crea la entidad
    // then: Se produce error porque ya tiene Id
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON)
            .content(nuevoApartadoFormularioJson))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void update_WithExistingId_ReturnsApartadoFormulario() throws Exception {

    // given: Entidad existente que se va a actualizar
    ApartadoFormulario response = getMockData(1L, 1L, 1L, null);
    final String url = new StringBuilder(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();
    String replaceApartadoFormularioJson = mapper.writeValueAsString(response);

    BDDMockito.given(service.update(ArgumentMatchers.<ApartadoFormulario>any())).willReturn(response);

    // when: Se actualiza la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.put(url, response.getId()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceApartadoFormularioJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Los datos se actualizan correctamente
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(response.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("bloqueFormulario").value(response.getBloqueFormulario()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(response.getNombre()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("apartadoFormularioPadre").value(response.getApartadoFormularioPadre()))
        .andExpect(MockMvcResultMatchers.jsonPath("orden").value(response.getOrden()))
        .andExpect(MockMvcResultMatchers.jsonPath("componenteFormulario").value(response.getComponenteFormulario()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(response.getActivo()));
  }

  @Test
  public void update_WithNoExistingId_Returns404() throws Exception {

    // given: Entidad a actualizar que no existe
    ApartadoFormulario response = getMockData(1L, 1L, 1L, null);
    final String url = new StringBuilder(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();
    String replaceApartadoFormularioJson = mapper.writeValueAsString(response);

    BDDMockito.given(service.update(ArgumentMatchers.<ApartadoFormulario>any())).will((InvocationOnMock invocation) -> {
      throw new ApartadoFormularioNotFoundException(((ApartadoFormulario) invocation.getArgument(0)).getId());
    });

    // when: Se actualiza la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.put(url, response.getId()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceApartadoFormularioJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Se produce error porque no encuentra la entidad a actualizar
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void delete_WithExistingId_Return204() throws Exception {

    // given: Entidad existente
    ApartadoFormulario response = getMockData(1L, 1L, 1L, null);
    final String url = new StringBuilder(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH)//
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
  public void delete_WithNoExistingId_Returns404() throws Exception {

    // given: Id de una entidad que no existe
    ApartadoFormulario apartadoFormulario = getMockData(1L, 1L, 1L, null);
    final String url = new StringBuilder(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ApartadoFormularioNotFoundException(invocation.getArgument(0));
    });

    // when: Se elimina la entidad
    mockMvc
        .perform(MockMvcRequestBuilders.delete(url, apartadoFormulario.getId()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Se produce error porque no encuentra la entidad a eliminar
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void findById_WithExistingId_ReturnsApartadoFormulario() throws Exception {

    // given: Entidad con un determinado Id
    ApartadoFormulario response = getMockData(1L, 1L, 1L, null);
    final String url = new StringBuilder(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();

    BDDMockito.given(service.findById(response.getId())).willReturn(response);

    // when: Se busca la entidad por ese Id
    mockMvc.perform(MockMvcRequestBuilders.get(url, response.getId()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Se recupera la entidad con el Id
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(response.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("bloqueFormulario").value(response.getBloqueFormulario()))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(response.getNombre()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("apartadoFormularioPadre").value(response.getApartadoFormularioPadre()))
        .andExpect(MockMvcResultMatchers.jsonPath("orden").value(response.getOrden()))
        .andExpect(MockMvcResultMatchers.jsonPath("componenteFormulario").value(response.getComponenteFormulario()))
        .andExpect(MockMvcResultMatchers.jsonPath("activo").value(response.getActivo()));
  }

  @Test
  public void findById_WithNoExistingId_Returns404() throws Exception {

    // given: No existe entidad con el id indicado
    ApartadoFormulario response = getMockData(1L, 1L, 1L, null);

    final String url = new StringBuilder(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();

    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ApartadoFormularioNotFoundException(invocation.getArgument(0));
    });

    // when: Se busca entidad con ese id
    mockMvc.perform(MockMvcRequestBuilders.get(url, response.getId()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Se produce error porque no encuentra la entidad con ese Id
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void findAll_Unlimited_ReturnsFullApartadoFormularioList() throws Exception {

    // given: Datos existentes
    final String url = new StringBuilder(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH).toString();

    List<ApartadoFormulario> response = new LinkedList<ApartadoFormulario>();
    response.add(getMockData(1L, 1L, 1L, null));
    response.add(getMockData(2L, 1L, 1L, 1L));

    BDDMockito.given(service.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(response));

    // when: Se buscan todos los datos
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Se recuperan todos los datos
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2))).andReturn();

    Assertions.assertThat(mapper.readValue(result.getResponse().getContentAsString(Charset.forName("UTF-8")),
        new TypeReference<List<ApartadoFormulario>>() {
        })).isEqualTo(response);

  }

  @Test
  public void findAll_Unlimited_Returns204() throws Exception {

    // given: No hay datos
    final String url = new StringBuilder(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH).toString();

    BDDMockito.given(service.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));
    // when: Se buscan todos los datos
    mockMvc.perform(MockMvcRequestBuilders.get(url)).andDo(MockMvcResultHandlers.print())
        // then: Se recupera lista vacía);
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  public void findAll_WithPaging_ReturnsDemoSubList() throws Exception {

    // given: Datos existentes
    String url = new StringBuilder(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH).toString();

    List<ApartadoFormulario> response = new LinkedList<>();
    response.add(getMockData(1L, 1L, 1L, null));
    response.add(getMockData(2L, 1L, 1L, 1L));
    response.add(getMockData(3L, 1L, 1L, 1L));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<ApartadoFormulario> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

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
        new TypeReference<List<ApartadoFormulario>>() {
        })).isEqualTo(response.subList(2, 3));
  }

  @Test
  public void findAll_WithPaging_Returns204() throws Exception {

    // given: Datos existentes
    String url = new StringBuilder(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH).toString();

    List<ApartadoFormulario> response = new LinkedList<ApartadoFormulario>();
    Pageable pageable = PageRequest.of(1, 2);
    Page<ApartadoFormulario> pageResponse = new PageImpl<>(response, pageable, response.size());

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
  public void findAll_WithSearchQuery_ReturnsFilteredApartadoFormularioList() throws Exception {

    // given: Datos existentes
    List<ApartadoFormulario> response = new LinkedList<>();
    response.add(getMockData(1L, 1L, 1L, null));
    response.add(getMockData(2L, 1L, 1L, 1L));
    response.add(getMockData(3L, 1L, 1L, 1L));
    response.add(getMockData(4L, 2L, 2L, null));
    response.add(getMockData(5L, 2L, 2L, 4L));

    final String url = new StringBuilder(APARTADO_FORMULARIO_CONTROLLER_BASE_PATH).toString();

    // search
    String query = "nombre~ApartadoFormulario0%,id:3";

    BDDMockito.given(service.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ApartadoFormulario>>() {
          @Override
          public Page<ApartadoFormulario> answer(InvocationOnMock invocation) throws Throwable {
            List<QueryCriteria> queryCriterias = invocation.<List<QueryCriteria>>getArgument(0);

            List<ApartadoFormulario> content = new LinkedList<>();
            for (ApartadoFormulario item : response) {
              boolean add = true;
              for (QueryCriteria queryCriteria : queryCriterias) {
                Field field = ReflectionUtils.findField(ApartadoFormulario.class, queryCriteria.getKey());
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
            Page<ApartadoFormulario> page = new PageImpl<>(content);
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
        new TypeReference<List<ApartadoFormulario>>() {
        })).isEqualTo(response.subList(2, 3));
  }

  /**
   * Genera un objeto {@link ApartadoFormulario}
   * 
   * @param id
   * @param bloqueFormularioId
   * @param componenteFormularioId
   * @param apartadoFormularioPadreId
   * @return ApartadoFormulario
   */
  private ApartadoFormulario getMockData(Long id, Long bloqueFormularioId, Long componenteFormularioId,
      Long apartadoFormularioPadreId) {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion1", Boolean.TRUE);
    BloqueFormulario bloqueFormulario = new BloqueFormulario(bloqueFormularioId, formulario,
        "Bloque Formulario " + bloqueFormularioId, bloqueFormularioId.intValue(), Boolean.TRUE);
    ComponenteFormulario componenteFormulario = new ComponenteFormulario(componenteFormularioId,
        "EsquemaComponenteFormulario" + componenteFormularioId);

    ApartadoFormulario apartadoFormularioPadre = (apartadoFormularioPadreId != null)
        ? getMockData(apartadoFormularioPadreId, bloqueFormularioId, componenteFormularioId, null)
        : null;

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final ApartadoFormulario data = new ApartadoFormulario();
    data.setId(id);
    data.setBloqueFormulario(bloqueFormulario);
    data.setNombre("ApartadoFormulario" + txt);
    data.setApartadoFormularioPadre(apartadoFormularioPadre);
    data.setOrden(id.intValue());
    data.setComponenteFormulario(componenteFormulario);
    data.setActivo(Boolean.TRUE);

    return data;
  }
}
