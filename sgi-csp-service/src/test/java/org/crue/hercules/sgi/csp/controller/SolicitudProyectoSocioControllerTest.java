package org.crue.hercules.sgi.csp.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoPago;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEquipoSocioService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPeriodoJustificacionService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPeriodoPagoService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * SolicitudProyectoSocioControllerTest
 */
@WebMvcTest(SolicitudProyectoSocioController.class)
public class SolicitudProyectoSocioControllerTest extends BaseControllerTest {

  @MockBean
  private SolicitudProyectoSocioService service;

  @MockBean
  private SolicitudProyectoEquipoSocioService solicitudProyectoEquipoSocioService;

  @MockBean
  private SolicitudProyectoPeriodoPagoService solicitudProyectoPeriodoPagoService;

  @MockBean
  private SolicitudProyectoPeriodoJustificacionService solicitudProyectoPeriodoJustificacionService;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectosocio";

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-C" })
  public void create_ReturnsSolicitudProyectoSocio() throws Exception {
    // given: new SolicitudProyectoSocio
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(null, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudProyectoSocio>any()))
        .willAnswer(new Answer<SolicitudProyectoSocio>() {
          @Override
          public SolicitudProyectoSocio answer(InvocationOnMock invocation) throws Throwable {
            SolicitudProyectoSocio givenData = invocation.getArgument(0, SolicitudProyectoSocio.class);
            SolicitudProyectoSocio newData = new SolicitudProyectoSocio();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create SolicitudProyectoSocio
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudProyectoSocio)))
        .andDo(MockMvcResultHandlers.print())
        // then: new SolicitudProyectoSocio is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("rolSocio.id").value(solicitudProyectoSocio.getRolSocio().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudProyectoDatos.id")
            .value(solicitudProyectoSocio.getSolicitudProyectoDatos().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("mesInicio").value(solicitudProyectoSocio.getMesInicio()))
        .andExpect(MockMvcResultMatchers.jsonPath("mesFin").value(solicitudProyectoSocio.getMesFin()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("numInvestigadores").value(solicitudProyectoSocio.getNumInvestigadores()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("importeSolicitado").value(solicitudProyectoSocio.getImporteSolicitado()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-C" })
  public void create_WithId_Returns400() throws Exception {
    // given: a SolicitudProyectoSocio with id filled
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);

    BDDMockito.given(service.create(ArgumentMatchers.<SolicitudProyectoSocio>any()))
        .willThrow(new IllegalArgumentException());

    // when: create SolicitudProyectoSocio
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(solicitudProyectoSocio)))
        .andDo(MockMvcResultHandlers.print())
        // then: 400 error
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-E" })
  public void update_WithExistingId_ReturnsSolicitudProyectoSocio() throws Exception {
    // given: existing SolicitudProyectoSocio
    SolicitudProyectoSocio updatedSolicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);
    updatedSolicitudProyectoSocio.setMesFin(12);

    BDDMockito.given(service.update(ArgumentMatchers.<SolicitudProyectoSocio>any()))
        .willAnswer(new Answer<SolicitudProyectoSocio>() {
          @Override
          public SolicitudProyectoSocio answer(InvocationOnMock invocation) throws Throwable {
            SolicitudProyectoSocio givenData = invocation.getArgument(0, SolicitudProyectoSocio.class);
            return givenData;
          }
        });

    // when: update SolicitudProyectoSocio
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedSolicitudProyectoSocio.getId())
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedSolicitudProyectoSocio)))
        .andDo(MockMvcResultHandlers.print())
        // then: SolicitudProyectoSocio is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(
            MockMvcResultMatchers.jsonPath("rolSocio.id").value(updatedSolicitudProyectoSocio.getRolSocio().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("solicitudProyectoDatos.id")
            .value(updatedSolicitudProyectoSocio.getSolicitudProyectoDatos().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("mesInicio").value(updatedSolicitudProyectoSocio.getMesInicio()))
        .andExpect(MockMvcResultMatchers.jsonPath("mesFin").value(updatedSolicitudProyectoSocio.getMesFin()))
        .andExpect(MockMvcResultMatchers.jsonPath("numInvestigadores")
            .value(updatedSolicitudProyectoSocio.getNumInvestigadores()))
        .andExpect(MockMvcResultMatchers.jsonPath("importeSolicitado")
            .value(updatedSolicitudProyectoSocio.getImporteSolicitado()));
    ;
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: a SolicitudProyectoSocio with non existing id
    SolicitudProyectoSocio updatedSolicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);

    BDDMockito.willThrow(new SolicitudProyectoSocioNotFoundException(updatedSolicitudProyectoSocio.getId()))
        .given(service).findById(ArgumentMatchers.<Long>any());
    BDDMockito.given(service.update(ArgumentMatchers.<SolicitudProyectoSocio>any()))
        .willThrow(new SolicitudProyectoSocioNotFoundException(updatedSolicitudProyectoSocio.getId()));

    // when: update SolicitudProyectoSocio
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, updatedSolicitudProyectoSocio.getId())
                .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updatedSolicitudProyectoSocio)))
        .andDo(MockMvcResultHandlers.print())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-B" })
  public void delete_WithExistingId_Return204() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.doNothing().when(service).delete(ArgumentMatchers.anyLong());
    // when: delete by id
    mockMvc
        .perform(MockMvcRequestBuilders.delete(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-B" })
  public void delete_WithoutId_Return404() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.willThrow(new SolicitudProyectoSocioNotFoundException(id)).given(service)
        .delete(ArgumentMatchers.<Long>any());

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
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-V" })
  public void findById_WithExistingId_ReturnsSolicitudProyectoSocio() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer(new Answer<SolicitudProyectoSocio>() {
      @Override
      public SolicitudProyectoSocio answer(InvocationOnMock invocation) throws Throwable {
        Long id = invocation.getArgument(0, Long.class);
        return generarSolicitudProyectoSocio(id, 1L, 1L);
      }
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested SolicitudProyectoSocio is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CATEM-V" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      throw new SolicitudProyectoSocioNotFoundException(id);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());

  }

  /**
   * 
   * Solicitud proyecto periodo pago
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CENTGES-V" })
  public void findAllSolicitudProyectoPeriodoPago_ReturnsPage() throws Exception {
    // given: Una lista con 37 SolicitudProyectoPeriodoPago para la
    // SolicitudProyectoSocio
    Long solicitudId = 1L;

    List<SolicitudProyectoPeriodoPago> solicitudProyectoPeriodoPago = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudProyectoPeriodoPago.add(generarSolicitudProyectoPeriodoPago(i, i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(solicitudProyectoPeriodoPagoService.findAllBySolicitudProyectoSocio(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > solicitudProyectoPeriodoPago.size() ? solicitudProyectoPeriodoPago.size() : toIndex;
          List<SolicitudProyectoPeriodoPago> content = solicitudProyectoPeriodoPago.subList(fromIndex, toIndex);
          Page<SolicitudProyectoPeriodoPago> pageResponse = new PageImpl<>(content, pageable,
              solicitudProyectoPeriodoPago.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyectoperiodopago", solicitudId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve la pagina 3 con los SolicitudProyectoPeriodoPago del 31 al
        // 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<SolicitudProyectoPeriodoPago> solicitudProyectoSocioResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<SolicitudProyectoPeriodoPago>>() {
        });

    for (int i = 31; i <= 37; i++) {
      SolicitudProyectoPeriodoPago solicitudProyectoPeriodoPagoRecuperado = solicitudProyectoSocioResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(solicitudProyectoPeriodoPagoRecuperado.getImporte()).isEqualTo(new BigDecimal(i));
    }
  }

  /**
   * 
   * Solicitud proyecto equipo socio
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CENTGES-V" })
  public void findAllSolicitudProyectoEquipoSocio_ReturnsPage() throws Exception {
    // given: Una lista con 37 SolicitudProyectoEquipo para la Solicitud
    Long solicitudId = 1L;

    List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocio = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudProyectoEquipoSocio.add(generarSolicitudProyectoEquipoSocio(i, i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(solicitudProyectoEquipoSocioService.findAllBySolicitudProyectoSocio(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > solicitudProyectoEquipoSocio.size() ? solicitudProyectoEquipoSocio.size() : toIndex;
          List<SolicitudProyectoEquipoSocio> content = solicitudProyectoEquipoSocio.subList(fromIndex, toIndex);
          Page<SolicitudProyectoEquipoSocio> pageResponse = new PageImpl<>(content, pageable,
              solicitudProyectoEquipoSocio.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyectoequiposocio", solicitudId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve la pagina 3 con los SolicitudProyectoSocio del 31 al
        // 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocioResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<SolicitudProyectoEquipoSocio>>() {
        });

    for (int i = 31; i <= 37; i++) {
      SolicitudProyectoEquipoSocio solicitudProyectoEquipoSocioRecuperado = solicitudProyectoEquipoSocioResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(solicitudProyectoEquipoSocioRecuperado.getId()).isEqualTo(new Long(i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CENTGES-V" })
  public void findAllSolicitudProyectoEquipoSocio_Returns204() throws Exception {
    // given: Una lista vacia de SolicitudProyectoSocio para la
    // Solicitud
    Long solicitudId = 1L;
    List<SolicitudProyectoEquipoSocio> solicitudProyectoSocio = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito
        .given(solicitudProyectoEquipoSocioService.findAllBySolicitudProyectoSocio(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          Page<SolicitudProyectoEquipoSocio> pageResponse = new PageImpl<>(solicitudProyectoSocio, pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyectoequiposocio", solicitudId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  /**
   * 
   * Solicitud proyecto periodo justificación
   * 
   */

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CENTGES-V" })
  public void findAllSolicitudProyectoPeriodoJustificacion_ReturnsPage() throws Exception {
    // given: Una lista con 37 SolicitudProyectoPeriodoJustificacion para la
    // SolicitudProyectoSocio
    Long solicitudId = 1L;

    List<SolicitudProyectoPeriodoJustificacion> solicitudProyectoPeriodoJustificacion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      long mesInicio = i + 1;
      long mesFin = mesInicio + 1;
      solicitudProyectoPeriodoJustificacion
          .add(generarMockSolicitudProyectoPeriodoJustificacion(i, (int) mesInicio, (int) mesFin, i));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito
        .given(solicitudProyectoPeriodoJustificacionService.findAllBySolicitudProyectoSocio(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(2, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > solicitudProyectoPeriodoJustificacion.size()
              ? solicitudProyectoPeriodoJustificacion.size()
              : toIndex;
          List<SolicitudProyectoPeriodoJustificacion> content = solicitudProyectoPeriodoJustificacion.subList(fromIndex,
              toIndex);
          Page<SolicitudProyectoPeriodoJustificacion> pageResponse = new PageImpl<>(content, pageable,
              solicitudProyectoPeriodoJustificacion.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders
            .get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/solicitudproyectoperiodojustificaciones", solicitudId)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: Devuelve la pagina 3 con los SolicitudProyectoPeriodoJustificacion del
        // 31 al
        // 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<SolicitudProyectoPeriodoJustificacion> solicitudProyectoSocioResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(),
        new TypeReference<List<SolicitudProyectoPeriodoJustificacion>>() {
        });

    for (int i = 31; i <= 37; i++) {
      SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacionRecuperado = solicitudProyectoSocioResponse
          .get(i - (page * pageSize) - 1);
      Assertions.assertThat(solicitudProyectoPeriodoJustificacionRecuperado.getId()).isEqualTo(new Long(i));
    }
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoSocio
   * 
   * @param solicitudProyectoSocioId
   * @param solicitudProyectoDatosId
   * @return el objeto SolicitudProyectoSocio
   */
  private SolicitudProyectoSocio generarSolicitudProyectoSocio(Long solicitudProyectoSocioId,
      Long solicitudProyectoDatosId, Long rolSocioId) {

    SolicitudProyectoSocio solicitudProyectoSocio = SolicitudProyectoSocio.builder().id(solicitudProyectoSocioId)
        .solicitudProyectoDatos(SolicitudProyectoDatos.builder().id(solicitudProyectoDatosId).build())
        .rolSocio(RolSocio.builder().id(rolSocioId).build()).mesInicio(1).mesFin(3).numInvestigadores(2)
        .importeSolicitado(new BigDecimal("335")).empresaRef("002").build();

    return solicitudProyectoSocio;
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoPeriodoPago
   * 
   * @param solicitudProyectoPeriodoPagoId
   * @param solicitudProyectoSocioId
   * @return el objeto SolicitudProyectoPeriodoPago
   */
  private SolicitudProyectoPeriodoPago generarSolicitudProyectoPeriodoPago(Long solicitudProyectoPeriodoPagoId,
      Long solicitudProyectoSocioId) {

    SolicitudProyectoPeriodoPago solicitudProyectoPeriodoPago = SolicitudProyectoPeriodoPago.builder()
        .id(solicitudProyectoPeriodoPagoId)
        .solicitudProyectoSocio(SolicitudProyectoSocio.builder().id(solicitudProyectoSocioId).build()).numPeriodo(3)
        .importe(new BigDecimal(solicitudProyectoPeriodoPagoId)).mes(3).build();

    solicitudProyectoPeriodoPago.getSolicitudProyectoSocio().setSolicitudProyectoDatos(new SolicitudProyectoDatos());
    solicitudProyectoPeriodoPago.getSolicitudProyectoSocio().getSolicitudProyectoDatos().setSolicitud(new Solicitud());
    solicitudProyectoPeriodoPago.getSolicitudProyectoSocio().getSolicitudProyectoDatos().getSolicitud()
        .setEstado(new EstadoSolicitud());
    solicitudProyectoPeriodoPago.getSolicitudProyectoSocio().getSolicitudProyectoDatos().getSolicitud().getEstado()
        .setEstado(EstadoSolicitud.Estado.BORRADOR);
    return solicitudProyectoPeriodoPago;
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoEquipoSocio
   * 
   * @param solicitudProyectoEquipoSocioId
   * @param entidadesRelacionadasId
   * @return el objeto SolicitudProyectoEquipoSocio
   */
  private SolicitudProyectoEquipoSocio generarSolicitudProyectoEquipoSocio(Long solicitudProyectoEquipoSocioId,
      Long entidadesRelacionadasId) {

    SolicitudProyectoEquipoSocio solicitudProyectoEquipoSocio = SolicitudProyectoEquipoSocio.builder()
        .id(solicitudProyectoEquipoSocioId)
        .solicitudProyectoSocio(SolicitudProyectoSocio.builder().id(entidadesRelacionadasId).build())
        .rolProyecto(RolProyecto.builder().id(entidadesRelacionadasId).build())
        .personaRef("user-" + solicitudProyectoEquipoSocioId).mesInicio(1).mesFin(3).build();

    solicitudProyectoEquipoSocio.getSolicitudProyectoSocio().setSolicitudProyectoDatos(new SolicitudProyectoDatos());
    solicitudProyectoEquipoSocio.getSolicitudProyectoSocio().getSolicitudProyectoDatos().setSolicitud(new Solicitud());
    solicitudProyectoEquipoSocio.getSolicitudProyectoSocio().getSolicitudProyectoDatos().getSolicitud()
        .setEstado(new EstadoSolicitud());
    solicitudProyectoEquipoSocio.getSolicitudProyectoSocio().getSolicitudProyectoDatos().getSolicitud().getEstado()
        .setEstado(EstadoSolicitud.Estado.BORRADOR);

    return solicitudProyectoEquipoSocio;
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoPeriodoJustificacion
   * 
   * @param id                  id del SolicitudProyectoPeriodoJustificacion
   * @param mesInicial          Mes inicial
   * @param mesFinal            Mes final
   * @param tipo                Tipo justificacion
   * @param solicitudProyectoId Id SolicitudProyecto
   * @return el objeto SolicitudProyectoPeriodoJustificacion
   */
  private SolicitudProyectoPeriodoJustificacion generarMockSolicitudProyectoPeriodoJustificacion(Long id,
      Integer mesInicial, Integer mesFinal, Long solicitudProyectoId) {
    SolicitudProyectoSocio solicitudProyectoSocio = new SolicitudProyectoSocio();
    solicitudProyectoSocio.setId(solicitudProyectoId == null ? 1 : solicitudProyectoId);

    SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacion = new SolicitudProyectoPeriodoJustificacion();
    solicitudProyectoPeriodoJustificacion.setId(id);
    solicitudProyectoPeriodoJustificacion.setSolicitudProyectoSocio(solicitudProyectoSocio);
    solicitudProyectoPeriodoJustificacion.setNumPeriodo(1);
    solicitudProyectoPeriodoJustificacion.setMesInicial(mesInicial);
    solicitudProyectoPeriodoJustificacion.setMesFinal(mesFinal);
    solicitudProyectoPeriodoJustificacion.setFechaInicio(LocalDate.of(2020, 10, 10));
    solicitudProyectoPeriodoJustificacion.setFechaFin(LocalDate.of(2020, 11, 20));
    solicitudProyectoPeriodoJustificacion.setObservaciones("observaciones-" + id);

    return solicitudProyectoPeriodoJustificacion;
  }
}
