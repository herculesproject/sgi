package org.crue.hercules.sgi.tp.controller;

import java.time.Instant;
import java.util.Arrays;

import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.crue.hercules.sgi.tp.converter.SgiApiTaskConverter;
import org.crue.hercules.sgi.tp.model.BeanMethodInstantTask;
import org.crue.hercules.sgi.tp.service.BeanMethodTaskService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * SgiApiTaskControllerTest
 */
@WebMvcTest(SgiApiTaskController.class)
class SgiApiTaskControllerTest extends BaseControllerTest {

  @MockBean
  private BeanMethodTaskService service;

  private static final String CONTROLLER_BASE_PATH = SgiApiTaskController.MAPPING;
  private static final String PATH_ENABLE = SgiApiTaskController.PATH_ENABLE;
  private static final String PATH_DISABLE = SgiApiTaskController.PATH_DISABLE;

  @Test
  @WithMockUser(username = "user")
  void enable_WithExistingId_Returns200() throws Exception {
    // given: una tarea programada desactivada
    Long id = 1L;
    BDDMockito.given(service.enable(ArgumentMatchers.anyLong()))
        .willReturn(generarMockBeanMethodInstantTask(id, Boolean.FALSE));

    // when: se activa la tarea
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_ENABLE, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: se devuelve la tarea activada
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
        .andExpect(MockMvcResultMatchers.jsonPath("disabled").value(Boolean.FALSE));
  }

  @Test
  @WithMockUser(username = "user")
  void enable_WithNoExistingId_Returns404() throws Exception {
    // given: una tarea programada que no existe
    Long id = 1L;
    BDDMockito.given(service.enable(ArgumentMatchers.anyLong())).willThrow(new NotFoundException());

    // when: se activa la tarea
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_ENABLE, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user")
  void disable_WithExistingId_Returns200() throws Exception {
    // given: una tarea programada activa
    Long id = 1L;
    BDDMockito.given(service.disable(ArgumentMatchers.anyLong()))
        .willReturn(generarMockBeanMethodInstantTask(id, Boolean.TRUE));

    // when: se desactiva la tarea
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_DISABLE, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: se devuelve la tarea desactivada
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
        .andExpect(MockMvcResultMatchers.jsonPath("disabled").value(Boolean.TRUE));
  }

  @Test
  @WithMockUser(username = "user")
  void disable_WithNoExistingId_Returns404() throws Exception {
    // given: una tarea programada que no existe
    Long id = 1L;
    BDDMockito.given(service.disable(ArgumentMatchers.anyLong())).willThrow(new NotFoundException());

    // when: se desactiva la tarea
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_DISABLE, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  private BeanMethodInstantTask generarMockBeanMethodInstantTask(Long id, Boolean disabled) {
    return BeanMethodInstantTask.builder()
        .id(id)
        .bean(SgiApiTaskConverter.BEAN_NAME_SGI_API_CALLER_TASK)
        .method("call")
        .params(Arrays.asList("COM", "/emails/1/send", "GET"))
        .description("Task for send email 1")
        .instant(Instant.parse("2099-01-01T00:00:00Z"))
        .disabled(disabled)
        .build();
  }
}
