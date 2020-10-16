package org.crue.hercules.sgi.eti.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.config.SecurityConfig;
import org.crue.hercules.sgi.eti.exceptions.ComiteFormularioNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.ComiteNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.service.ComiteFormularioService;
import org.crue.hercules.sgi.eti.service.ComiteService;
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
 * ComiteControllerTest
 */
@WebMvcTest(ComiteController.class)
// Since WebMvcTest is only sliced controller layer for the testing, it would
// not take the security configurations.
@Import(SecurityConfig.class)
public class ComiteControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private ComiteService comiteService;

  @MockBean
  private ComiteFormularioService comiteFormularioService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String COMITE_CONTROLLER_BASE_PATH = "/comites";

  /* Retorna una lista Comite y comprueba los datos */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V", "ETI-CNV-V" })
  public void getComite_ReturnsComiteList() throws Exception {

    List<Comite> comiteLista = new ArrayList<>();

    comiteLista.add(new Comite(1L, "Comite1", Boolean.TRUE));
    comiteLista.add(new Comite(2L, "Comite2", Boolean.TRUE));

    BDDMockito
        .given(comiteService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(comiteLista));

    mockMvc
        .perform(MockMvcRequestBuilders.get(COMITE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))

        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].comite").value("Comite1"))

        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].comite").value("Comite2"));
  }

  /* Retorna una lista vacía */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V", "ETI-CNV-V" })
  public void getComite_ReturnsEmptyList() throws Exception {

    List<Comite> comiteResponseList = new ArrayList<Comite>();

    BDDMockito
        .given(comiteService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(comiteResponseList));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(COMITE_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /* Retorna un Comite por id y comprueba los datos */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMITE-VER" })
  public void getComite_WithId_ReturnsComite() throws Exception {

    BDDMockito.given(comiteService.findById(ArgumentMatchers.anyLong()))
        .willReturn((new Comite(1L, "Comite1", Boolean.TRUE)));

    mockMvc
        .perform(MockMvcRequestBuilders.get(COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("comite").value("Comite1"));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMITE-VER" })
  public void getComite_NotFound_Returns404() throws Exception {
    BDDMockito.given(comiteService.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new ComiteNotFoundException(invocation.getArgument(0));
    });
    mockMvc
        .perform(MockMvcRequestBuilders.get(COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /* Crear Comite */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMITE-EDITAR" })
  public void addComite_ReturnsComite() throws Exception {

    // given: Un Comite nuevo
    String nuevoComiteJson = "{\"comite\": \"Comite1\"}";

    Comite comite = new Comite();
    comite.setId(1L);
    comite.setComite("Comite1");

    BDDMockito.given(comiteService.create(ArgumentMatchers.<Comite>any())).willReturn(comite);

    // when: Creamos un Comite

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(COMITE_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON).content(nuevoComiteJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Crea el nuevo Comite y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("comite").value("Comite1"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMITE-EDITAR" })
  public void addComite_Error_Returns400() throws Exception {
    // given: Un Comite nuevo que produce un error al crearse
    String nuevoComiteJson = "{\"id\": 1, \"comite\": \"Comite1\"}";

    BDDMockito.given(comiteService.create(ArgumentMatchers.<Comite>any())).willThrow(new IllegalArgumentException());

    // when: Creamos un Comite
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(COMITE_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON).content(nuevoComiteJson))
        .andDo(MockMvcResultHandlers.print())

        .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  /* Modificar un Comite */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMITE-EDITAR" })
  public void replaceComite_ReturnsComite() throws Exception {
    // given: Un Comite a modificar
    String replaceComiteJson = "{\"comite\": \"Comite1\"}";

    Comite comite = new Comite();
    comite.setId(1L);
    comite.setComite("Replace Comite1");

    BDDMockito.given(comiteService.update(ArgumentMatchers.<Comite>any())).willReturn(comite);

    mockMvc
        .perform(MockMvcRequestBuilders.put(COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceComiteJson))
        .andDo(MockMvcResultHandlers.print())
        // then: Modifica el Comite y lo devuelve
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("comite").value("Replace Comite1"));

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMITE-EDITAR" })
  public void replaceComite_NotFound() throws Exception {
    // given: Un Comite a modificar
    String replaceComiteJson = "{\"id\": \"1\",\"comite\": \"Comite1\"}";

    Comite comite = new Comite();
    comite.setId(1L);
    comite.setComite("Replace Comite1");

    BDDMockito.given(comiteService.update(ArgumentMatchers.<Comite>any())).will((InvocationOnMock invocation) -> {
      throw new ComiteNotFoundException(((Comite) invocation.getArgument(0)).getId());
    });
    mockMvc
        .perform(MockMvcRequestBuilders.put(COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(replaceComiteJson))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  /* Eliminar un Comite */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-COMITE-EDITAR" })
  public void deleteComite() throws Exception {

    BDDMockito.given(comiteService.findById(ArgumentMatchers.anyLong()))
        .willReturn(new Comite(1L, "Comite1", Boolean.TRUE));

    mockMvc
        .perform(MockMvcRequestBuilders.delete(COMITE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk());
  }

  /* Retorna lista paginada Comite */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V", "ETI-CNV-V" })
  public void findAll_WithPaging_ReturnsComiteSubList() throws Exception {

    // given: Cien Comite
    List<Comite> comiteList = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      comiteList.add(new Comite(Long.valueOf(i), "Comite" + String.format("%03d", i), Boolean.TRUE));
    }

    BDDMockito
        .given(comiteService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Comite>>() {
          @Override
          public Page<Comite> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Comite> content = comiteList.subList(fromIndex, toIndex);
            Page<Comite> page = new PageImpl<>(content, pageable, comiteList.size());
            return page;
          }
        });

    // when: Obtiene page=3 con pagesize=10
    MvcResult requestResult = mockMvc
        .perform(
            MockMvcRequestBuilders.get(COMITE_CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("X-Page", "3").header("X-Page-Size", "10").accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: El Comite retorn la información de la página correcta en el
        // header
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "100"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(10))).andReturn();

    // Usa TypeReference para informar Jackson sobre el tipo genérico de la lista
    List<Comite> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<Comite>>() {
        });

    // Contiene Comite='Comite031' to 'Comite040'
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Comite comite = actual.get(i);
      Assertions.assertThat(comite.getComite()).isEqualTo("Comite" + String.format("%03d", j));
    }
  }

  /* Retorna lista filtrada Comite */
  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V", "ETI-CNV-V" })
  public void findAll_WithSearchQuery_ReturnsFilteredComiteList() throws Exception {

    // given: Dos Comite y una query

    List<Comite> comiteList = new ArrayList<>();
    comiteList.add(new Comite(1L, "Comite1", Boolean.TRUE));
    comiteList.add(new Comite(2L, "Comite2", Boolean.TRUE));

    String query = "comite~Comite%";

    BDDMockito
        .given(comiteService.findAll(ArgumentMatchers.<List<QueryCriteria>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Comite>>() {
          @Override
          public Page<Comite> answer(InvocationOnMock invocation) throws Throwable {
            List<QueryCriteria> queryCriterias = invocation.<List<QueryCriteria>>getArgument(0);

            List<Comite> content = new ArrayList<>();
            for (Comite comite : comiteList) {
              boolean add = true;
              for (QueryCriteria queryCriteria : queryCriterias) {
                Field field = ReflectionUtils.findField(Comite.class, queryCriteria.getKey());
                field.setAccessible(true);
                String fieldValue = ReflectionUtils.getField(field, comite).toString();
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
                content.add(comite);
              }
            }
            Page<Comite> page = new PageImpl<>(content);
            return page;
          }
        });

    // when: Encuenta la búsqueda de la query
    mockMvc
        .perform(MockMvcRequestBuilders.get(COMITE_CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).param("q", query).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Obtiene la página
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-ER-INV" })
  public void findComiteFormulario() throws Exception {
    // given: Existe la memoria pero no tiene documentacion
    Long id = 3L;
    final String url = new StringBuilder(COMITE_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/comite-formulario").toString();

    BDDMockito.given(comiteFormularioService.findComiteFormularioTipoM(ArgumentMatchers.anyLong()))
        .willReturn(new Formulario(1L, "M10", "Formulario M10", Boolean.TRUE));

    // when: Se buscan todos los datos
    mockMvc.perform(MockMvcRequestBuilders.get(url, id).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("M10"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-ER-INV" })
  public void findComiteFormulario_Returns404() throws Exception {
    BDDMockito.given(comiteFormularioService.findComiteFormularioTipoM(ArgumentMatchers.anyLong()))
        .will((InvocationOnMock invocation) -> {
          throw new ComiteFormularioNotFoundException(invocation.getArgument(0));
        });

    final String url = new StringBuilder(COMITE_CONTROLLER_BASE_PATH).append(PATH_PARAMETER_ID)
        .append("/comite-formulario").toString();

    mockMvc.perform(MockMvcRequestBuilders.get(url, 1L).with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

}