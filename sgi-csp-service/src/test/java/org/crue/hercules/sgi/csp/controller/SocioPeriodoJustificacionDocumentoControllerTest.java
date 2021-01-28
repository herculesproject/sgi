package org.crue.hercules.sgi.csp.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.SocioPeriodoJustificacionDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SocioPeriodoJustificacionDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.service.SocioPeriodoJustificacionDocumentoService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * SocioPeriodoJustificacionDocumentoControllerTest
 */
@WebMvcTest(SocioPeriodoJustificacionDocumentoController.class)
public class SocioPeriodoJustificacionDocumentoControllerTest extends BaseControllerTest {

  @MockBean
  private SocioPeriodoJustificacionDocumentoService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/socioperiodojustificaciondocumentos";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-C" })
  public void update_ReturnsSocioPeriodoJustificacionDocumentoList() throws Exception {
    // given: una lista con uno de los SocioPeriodoJustificacionDocumento
    // actualizado,
    // otro nuevo y sin los otros 3 periodos existentes
    Long proyectoSocioPeriodoJustificacionId = 1L;
    SocioPeriodoJustificacionDocumento newSocioPeriodoJustificacionDocumento = generarMockSocioPeriodoJustificacionDocumento(
        null);
    SocioPeriodoJustificacionDocumento updatedSocioPeriodoJustificacionDocumento = generarMockSocioPeriodoJustificacionDocumento(
        4L);

    List<SocioPeriodoJustificacionDocumento> socioPeriodoJustificacionDocumento = Arrays
        .asList(updatedSocioPeriodoJustificacionDocumento, newSocioPeriodoJustificacionDocumento);

    BDDMockito
        .given(
            service.update(ArgumentMatchers.anyLong(), ArgumentMatchers.<SocioPeriodoJustificacionDocumento>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<SocioPeriodoJustificacionDocumento> periodoJustificaciones = invocation.getArgument(1);
          return periodoJustificaciones.stream().map(periodoJustificacion -> {
            if (periodoJustificacion.getId() == null) {
              periodoJustificacion.setId(5L);
            }
            periodoJustificacion.getProyectoSocioPeriodoJustificacion().setId(proyectoSocioPeriodoJustificacionId);
            return periodoJustificacion;
          }).collect(Collectors.toList());
        });

    // when: updateSocioPeriodoJustificacionDocumentoesProyectoSocio
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, proyectoSocioPeriodoJustificacionId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(socioPeriodoJustificacionDocumento)))
        .andDo(MockMvcResultHandlers.print())
        // then: Se crea el nuevo SocioPeriodoJustificacionDocumento, se actualiza el
        // existe y se eliminan el resto
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(socioPeriodoJustificacionDocumento.get(0).getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$[0].nombre").value(socioPeriodoJustificacionDocumento.get(0).getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].comentario")
            .value(socioPeriodoJustificacionDocumento.get(0).getComentario()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].documentoRef")
            .value(socioPeriodoJustificacionDocumento.get(0).getDocumentoRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].tipoDocumento.id")
            .value(socioPeriodoJustificacionDocumento.get(0).getTipoDocumento().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].visible")
            .value(socioPeriodoJustificacionDocumento.get(0).getVisible()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(5L))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$[1].nombre").value(socioPeriodoJustificacionDocumento.get(1).getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].comentario")
            .value(socioPeriodoJustificacionDocumento.get(1).getComentario()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].documentoRef")
            .value(socioPeriodoJustificacionDocumento.get(1).getDocumentoRef()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].tipoDocumento.id")
            .value(socioPeriodoJustificacionDocumento.get(1).getTipoDocumento().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].visible")
            .value(socioPeriodoJustificacionDocumento.get(1).getVisible()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-E" })
  public void updateSocioPeriodoJustificacionDocumentoesProyectoSocio_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    SocioPeriodoJustificacionDocumento socioPeriodoJustificacionDocumento = generarMockSocioPeriodoJustificacionDocumento(
        1L);

    BDDMockito.willThrow(new SocioPeriodoJustificacionDocumentoNotFoundException(id)).given(service)
        .update(ArgumentMatchers.anyLong(), ArgumentMatchers.<SocioPeriodoJustificacionDocumento>anyList());

    // when: updateSocioPeriodoJustificacionDocumentoesProyectoSocio
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(Arrays.asList(socioPeriodoJustificacionDocumento))))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-V" })
  public void findById_WithExistingId_ReturnsSocioPeriodoJustificacionDocumento() throws Exception {
    // given: existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      return generarMockSocioPeriodoJustificacionDocumento(invocation.getArgument(0));
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested SocioPeriodoJustificacionDocumento is resturned as JSON
        // object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoDocumento.id").value(1L))
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value("nombre-1"))
        .andExpect(MockMvcResultMatchers.jsonPath("comentario").value("comentario"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-ME-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new SocioPeriodoJustificacionDocumentoNotFoundException(1L);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * Función que devuelve un objeto SocioPeriodoJustificacionDocumento
   * 
   * @param id id del SocioPeriodoJustificacionDocumento
   * 
   * @return el objeto SocioPeriodoJustificacionDocumento
   */
  private SocioPeriodoJustificacionDocumento generarMockSocioPeriodoJustificacionDocumento(Long id) {
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = ProyectoSocioPeriodoJustificacion.builder()
        .id(1L).build();

    TipoDocumento tipoDocumento = TipoDocumento.builder().id(1L).nombre("tipo1").activo(Boolean.TRUE).build();
    SocioPeriodoJustificacionDocumento socioPeriodoJustificacionDocumento = SocioPeriodoJustificacionDocumento.builder()
        .id(id).nombre("nombre-" + id).comentario("comentario").documentoRef("001")
        .proyectoSocioPeriodoJustificacion(proyectoSocioPeriodoJustificacion).tipoDocumento(tipoDocumento)
        .visible(Boolean.TRUE).build();
    return socioPeriodoJustificacionDocumento;
  }

}
