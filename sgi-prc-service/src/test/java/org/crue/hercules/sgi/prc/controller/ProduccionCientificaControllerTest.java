package org.crue.hercules.sgi.prc.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.crue.hercules.sgi.prc.dto.PublicacionResumen;
import org.crue.hercules.sgi.prc.exceptions.ProduccionCientificaNotFoundException;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica.EpigrafeCVN;
import org.crue.hercules.sgi.prc.service.ProduccionCientificaService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ProduccionCientificaControllerTest
 */
@WebMvcTest(ProduccionCientificaController.class)
public class ProduccionCientificaControllerTest extends BaseControllerTest {

  @MockBean
  private ProduccionCientificaService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = ProduccionCientificaController.MAPPING;

  public void findAll_ReturnsPage() throws Exception {
    // given: Una lista con 37 ProduccionCientifica
    List<ProduccionCientifica> produccionCientificas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      produccionCientificas.add(generarMockProduccionCientifica(i,
          "ProduccionCientifica" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > produccionCientificas.size() ? produccionCientificas.size() : toIndex;
          List<ProduccionCientifica> content = produccionCientificas.subList(fromIndex, toIndex);
          Page<ProduccionCientifica> pageResponse = new PageImpl<>(content, pageable,
              produccionCientificas.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page",
                page)
            .header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los ProduccionCientifica del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$",
            Matchers.hasSize(7)))
        .andReturn();

    List<PublicacionResumen> produccionCientificasResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<PublicacionResumen>>() {
        });

    for (int i = 31; i <= 37; i++) {
      PublicacionResumen produccionCientifica = produccionCientificasResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(produccionCientifica.getProduccionCientificaRef()).isEqualTo("ProduccionCientifica"
          + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = {})
  public void findById_WithExistingId_ReturnsProduccionCientifica() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      Long paramId = invocation.getArgument(0);
      ProduccionCientifica produccionCientifica = ProduccionCientifica.builder().id(paramId).build();
      return produccionCientifica;
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        // and the requested ProduccionCientifica is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = {})
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      Long paramId = invocation.getArgument(0);
      throw new ProduccionCientificaNotFoundException(paramId.toString());
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  private ProduccionCientifica generarMockProduccionCientifica(Long id, String idRef) {
    ProduccionCientifica produccionCientifica = new ProduccionCientifica();
    produccionCientifica.setId(id);
    produccionCientifica.setProduccionCientificaRef(idRef);
    produccionCientifica
        .setEpigrafeCVN(EpigrafeCVN.E060_010_010_000);

    return produccionCientifica;
  }
}
