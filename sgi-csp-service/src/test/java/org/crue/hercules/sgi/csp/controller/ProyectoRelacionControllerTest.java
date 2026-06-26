package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.crue.hercules.sgi.csp.dto.rel.ProyectoRelacionOutput;
import org.crue.hercules.sgi.csp.dto.rel.RelacionEntidadOutput;
import org.crue.hercules.sgi.csp.dto.rel.RelacionOutput.TipoEntidad;
import org.crue.hercules.sgi.csp.service.ProyectoRelacionService;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
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

@WebMvcTest(ProyectoRelacionController.class)
class ProyectoRelacionControllerTest extends BaseControllerTest {

  @MockBean
  private ProyectoRelacionService proyectoRelacionService;

  private static final String CONTROLLER_BASE_PATH = ProyectoRelacionController.REQUEST_MAPPING;
  private static final String PATH_RELACIONES = "/{id}/relaciones";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-INV-VR" })
  void findRelaciones_ReturnsList() throws Exception {
    // given: una lista de relaciones enriquecidas del proyecto
    Long proyectoId = 1L;
    List<ProyectoRelacionOutput> relaciones = new ArrayList<>();
    for (long i = 1; i <= 3; i++) {
      relaciones.add(generarMockProyectoRelacionOutput(i));
    }
    BDDMockito.given(proyectoRelacionService.findRelacionesProyecto(ArgumentMatchers.<Long>any()))
        .willReturn(relaciones);

    // when: se piden las relaciones del proyecto
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_RELACIONES, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: devuelve la lista de relaciones enriquecidas
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].tipoEntidadRelacionada").value(TipoEntidad.INVENCION.name()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].entidadRelacionada.id").value(1));

    BDDMockito.verify(proyectoRelacionService).findRelacionesProyecto(proyectoId);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-INV-VR" })
  void findRelaciones_EmptyList_ReturnsEmptyList() throws Exception {
    // given: el proyecto no tiene relaciones
    Long proyectoId = 1L;
    BDDMockito.given(proyectoRelacionService.findRelacionesProyecto(ArgumentMatchers.<Long>any()))
        .willReturn(new ArrayList<>());

    // when: se piden las relaciones del proyecto
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_RELACIONES, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: devuelve 200 con una lista vacia
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(0)));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "OTRA-AUTORIDAD" })
  void findRelaciones_WithoutAuthority_Returns403() throws Exception {
    // given: un usuario sin autoridad para acceder a proyectos
    Long proyectoId = 1L;

    // when: se piden las relaciones del proyecto
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_RELACIONES, proyectoId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: devuelve un 403
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  private ProyectoRelacionOutput generarMockProyectoRelacionOutput(Long id) {
    return ProyectoRelacionOutput.builder()
        .id(id)
        .tipoEntidadRelacionada(TipoEntidad.INVENCION)
        .entidadRelacionada(RelacionEntidadOutput.builder()
            .id(id)
            .titulo(Collections.singletonList(new I18nFieldValueDto(Language.ES, "Invencion " + id)))
            .build())
        .observaciones(Collections.singletonList(new I18nFieldValueDto(Language.ES, "Observaciones " + id)))
        .build();
  }

}
