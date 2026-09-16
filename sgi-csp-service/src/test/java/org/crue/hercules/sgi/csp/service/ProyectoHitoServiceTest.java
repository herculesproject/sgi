package org.crue.hercules.sgi.csp.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.ProyectoHitoAvisoInput;
import org.crue.hercules.sgi.csp.dto.ProyectoHitoInput;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.tp.SgiApiInstantTaskOutput;
import org.crue.hercules.sgi.csp.exceptions.SentAvisoNotUpdatableException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoHitoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.EstadoProyectoComentario;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.model.ProyectoHitoAviso;
import org.crue.hercules.sgi.csp.model.ProyectoHitoComentario;
import org.crue.hercules.sgi.csp.model.ProyectoObservaciones;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.model.TipoHitoDescripcion;
import org.crue.hercules.sgi.csp.model.TipoHitoNombre;
import org.crue.hercules.sgi.csp.repository.ModeloTipoHitoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoHitoAvisoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoHitoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.TipoHitoRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoHitoServiceImpl;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiTpService;
import org.crue.hercules.sgi.csp.util.ComGenericEmailTextHelper;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ProyectoHitoServiceTest
 */

class ProyectoHitoServiceTest extends BaseServiceTest {

  private static final Instant AVISO_FECHA_ENVIO = Instant.parse("2020-10-18T00:00:00Z");

  @Mock
  private ProyectoHitoRepository repository;
  @Mock
  private ProyectoRepository proyectoRepository;
  @Mock
  private ModeloTipoHitoRepository modeloTipoHitoRepository;
  @Mock
  private SgiApiComService emailService;
  @Mock
  private SgiApiTpService sgiApiTaskService;
  @Mock
  private ProyectoHitoAvisoRepository proyectoHitoAvisoRepository;
  @Mock
  private SgiApiSgpService personaService;
  @Mock
  private ProyectoEquipoRepository proyectoEquipoReposiotry;
  @Mock
  private TipoHitoRepository tipoHitoRepository;
  @Mock
  private ProyectoHelper proyectoHelper;

  private ProyectoHitoService service;

  @BeforeEach
  void setUp() {
    service = new ProyectoHitoServiceImpl(repository,
        proyectoRepository,
        modeloTipoHitoRepository,
        emailService,
        sgiApiTaskService,
        proyectoHitoAvisoRepository,
        personaService,
        proyectoEquipoReposiotry,
        tipoHitoRepository,
        proyectoHelper);
  }

  @Test
  void create_ReturnsProyectoHito() {
    // given: Un nuevo ProyectoHito
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    ProyectoHito created = this.generarMockProyectoHito(1L);
    TipoHito tipoHito = this.generarMockTipoHito(1L, Boolean.TRUE);
    ModeloTipoHito modeloTipoHito = this.generarMockModeloTipoHito(1L, created, Boolean.TRUE);
    created.setTipoHito(tipoHito);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito.given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(
        ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoHito));
    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(tipoHito));

    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoHito>any())).willReturn(created);

    // when: Creamos el ProyectoHito
    ProyectoHito proyectoHitoCreado = service.create(proyectoHito);

    // then: El ProyectoHito se crea correctamente
    Assertions.assertThat(proyectoHitoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoHitoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoHitoCreado.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoHito.getProyectoId());
    Assertions.assertThat(proyectoHitoCreado.getFecha()).as("getFecha()")
        .isEqualTo(proyectoHito.getFecha());
    Assertions.assertThat(I18nHelper.getValueForLanguage(proyectoHitoCreado.getComentario(), Language.ES))
        .as("getComentario()")
        .isEqualTo(I18nHelper.getValueForLanguage(proyectoHito.getComentario(), Language.ES));
    Assertions.assertThat(proyectoHitoCreado.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(proyectoHito.getTipoHitoId());
  }

  @Test
  void create_WithFechaAnterior_SaveGeneraAvisoFalse() {
    // given: Un nuevo ProyectoHito con fecha pasada
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    ProyectoHito created = this.generarMockProyectoHito(1L);
    TipoHito tipoHito = this.generarMockTipoHito(1L, Boolean.TRUE);
    ModeloTipoHito modeloTipoHito = this.generarMockModeloTipoHito(1L, created, Boolean.TRUE);
    created.setTipoHito(tipoHito);
    proyectoHito.setFecha(Instant.now().minus(Period.ofDays(2)));

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito.given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(
        ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoHito));
    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(tipoHito));

    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoHito>any())).willReturn(created);

    // when: Creamos el ProyectoHito
    ProyectoHito proyectoHitoCreado = service.create(proyectoHito);

    // then: El ProyectoHito se crea correctamente con GenerarAviso como FALSE
    Assertions.assertThat(proyectoHitoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoHitoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoHitoCreado.getProyectoId()).as("getProyectoId()")
        .isEqualTo(created.getProyectoId());
    Assertions.assertThat(proyectoHitoCreado.getFecha()).as("getFecha()")
        .isEqualTo(created.getFecha());
    Assertions.assertThat(proyectoHitoCreado.getComentario()).as("getComentario()")
        .isEqualTo(created.getComentario());
    Assertions.assertThat(proyectoHitoCreado.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(created.getTipoHito().getId());
    Assertions.assertThat(proyectoHitoCreado.getProyectoHitoAviso()).as("getProyectoHitoAviso()")
        .isEqualTo(created.getProyectoHitoAviso());
    Assertions.assertThat(proyectoHitoCreado.getProyectoHitoAviso().getId())
        .as("getProyectoHitoAviso().getId()").isNull();
  }

  @Test
  void create_WithoutProyectoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoHito without ProyectoId
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyectoHito.setProyectoId(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoHito
        () -> service.create(proyectoHito))
        // then: throw exception as ProyectoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Proyecto no puede ser nulo");
  }

  @Test
  void create_WithoutTipoHitoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoHito without TipoHitoId
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    Proyecto proyecto = generarMockProyecto(1L);
    proyectoHito.setProyectoId(proyecto.getId());
    proyectoHito.setTipoHitoId(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoHito
        () -> service.create(proyectoHito))
        // then: throw exception as TipoHitoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Tipo Hito no puede ser nulo");
  }

  @Test
  void create_WithoutFecha_ThrowsIllegalArgumentException() {
    // given: a ProyectoHito without Fecha
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyectoHito.setFecha(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoHito
        () -> service.create(proyectoHito))
        // then: throw exception as Fecha is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Fecha de Proyecto Hito no puede ser nulo");
  }

  @Test
  void create_WithNoExistingProyecto_ThrowsProyectoNotFoundException() {
    // given: a ProyectoHito with non existing Proyecto
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: create ProyectoHito
        () -> service.create(proyectoHito))
        // then: throw exception as Proyecto is not found
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  void create_WithoutModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: ProyectoHito con Proyecto sin Modelo de Ejecucion
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyecto.setModeloEjecucion(null);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: create ProyectoHito
        () -> service.create(proyectoHito))
        // then: throw exception as ModeloEjecucion not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tipo Hito no disponible para el Modelo Ejecución Proyecto sin modelo asignado");
  }

  @Test
  void create_WithoutModeloTipoHito_ThrowsIllegalArgumentException() {
    // given: ProyectoHito con TipoHito no asignado al Modelo de Ejecucion de la
    // proyecto
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ProyectoHito
        () -> service.create(proyectoHito))
        // then: throw exception as ModeloTipoHito not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tipo Hito no disponible para el Modelo Ejecución Proyecto sin modelo asignado");
  }

  @Test
  void create_WithDisabledModeloTipoHito_ThrowsIllegalArgumentException() {
    // given: ProyectoHito con la asignación de TipoHito al Modelo de Ejecucion
    // de la proyecto inactiva
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHitoInput input = generarMockProyectoHito();
    ModeloTipoHito modeloTipoHito = generarMockModeloTipoHito(1L, generarMockProyectoHito(1L), Boolean.FALSE);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoHito));

    Assertions.assertThatThrownBy(
        // when: create ProyectoHito
        () -> service.create(input))
        // then: throw exception as ModeloTipoHito is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("%s de Modelo Tipo Hito no está activo para el modelo ejecución %s",
            modeloTipoHito.getTipoHito().getNombre(), proyecto.getModeloEjecucion().getNombre());
  }

  @Test
  void create_WithDisabledTipoHito_ThrowsIllegalArgumentException() {
    // given: ProyectoHito TipoHito disabled
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHito proyectoHito = generarMockProyectoHito(1L);
    proyectoHito.setId(null);
    proyectoHito.getTipoHito().setActivo(Boolean.FALSE);
    ProyectoHitoInput input = generarMockProyectoHito();

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoHito(1L, proyectoHito, Boolean.TRUE)));

    Assertions.assertThatThrownBy(
        // when: create ProyectoHito
        () -> service.create(input))
        // then: throw exception as TipoHito is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("%s de Tipo Hito no está activo", proyectoHito.getTipoHito().getNombre());
  }

  @Test
  void create_WithFechaYTipoHitoDuplicado_ThrowsIllegalArgumentException() {
    // given: a ProyectoHito fecha duplicada
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHito proyectoHitoExistente = generarMockProyectoHito(2L);
    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    ModeloTipoHito modeloTipoHito = generarMockModeloTipoHito(1L, proyectoHito, Boolean.TRUE);
    modeloTipoHito.getTipoHito().setActivo(Boolean.TRUE);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(
            ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoHito));
    BDDMockito
        .given(repository.findByProyectoIdAndFechaAndTipoHitoId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Instant>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoHitoExistente));

    Assertions.assertThatThrownBy(
        // when: create ProyectoHito
        () -> service.create(proyectoHito))
        // then: throw exception as fecha is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Proyecto Hito de Proyecto ya existe");
  }

  @Test
  void update_ReturnsProyectoHito() {
    // given: Un nuevo ProyectoHito con el tipoHito actualizado
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHito proyectoHito = generarMockProyectoHito(1L);
    proyectoHito.setProyectoHitoAviso(ProyectoHitoAviso.builder()
        .comunicadoRef("1")
        .tareaProgramadaRef("1").build());
    ProyectoHitoInput proyectoHitoActualizado = generarMockProyectoHito();
    proyectoHitoActualizado.setTipoHitoId(2L);
    ModeloTipoHito modeloTipoHito = generarMockModeloTipoHito(1L, proyectoHito, Boolean.TRUE);
    modeloTipoHito.getTipoHito().setActivo(Boolean.TRUE);
    SgiApiInstantTaskOutput task = SgiApiInstantTaskOutput.builder()
        .instant(Instant.now().plus(Period.ofDays(3)))
        .id(1L)
        .build();

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoHito));

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito.given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(
        ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(
            modeloTipoHito));
    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(
        modeloTipoHito.getTipoHito()));

    BDDMockito.given(sgiApiTaskService
        .findInstantTaskById(anyLong())).willReturn(task);
    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoHito>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ProyectoHito
    ProyectoHito updated = service.update(1L, proyectoHitoActualizado);

    // then: El ProyectoHito se actualiza correctamente.
    Assertions.assertThat(updated).as("isNotNull()").isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(proyectoHito.getId());
    Assertions.assertThat(updated.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoHito.getProyectoId());
    Assertions.assertThat(I18nHelper.getValueForLanguage(updated.getComentario(), Language.ES)).as("getComentario()")
        .isEqualTo(I18nHelper.getValueForLanguage(proyectoHitoActualizado.getComentario(), Language.ES));
    Assertions.assertThat(updated.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(proyectoHitoActualizado.getTipoHitoId());
    Assertions.assertThat(updated.getFecha()).as("getFecha()")
        .isEqualTo(proyectoHitoActualizado.getFecha());
  }

  @Test
  void update_WithFechaPasadaYAvisoEnviadoSinCambios_ConservaElAviso() {
    // given: un hito con fecha pasada cuyo aviso ya se envió, y una modificación
    // que no toca los datos del aviso
    ProyectoHitoInput proyectoHitoActualizado = mockUpdateProyectoHitoConAviso(AVISO_FECHA_ENVIO);
    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoHito>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));
    BDDMockito.given(emailService.findGenericEmailTextById(anyLong())).willReturn(buildMockEmailOutputAviso());

    // when: se actualiza el hito
    ProyectoHito updated = service.update(1L, proyectoHitoActualizado);

    // then: la edición se permite, el aviso se conserva y no se toca el comunicado
    Assertions.assertThat(updated.getProyectoHitoAviso()).isNotNull();
    verify(emailService, never()).updateProyectoHitoEmail(anyLong(), anyLong(),
        ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.<List<Recipient>>any());
    verify(emailService, never()).deleteEmail(anyLong());
  }

  @Test
  void update_WithFechaPasadaYAvisoEnviadoConCambios_ThrowsSentAvisoNotUpdatableException() {
    // given: un hito con fecha pasada cuyo aviso ya se envió y cuyo asunto se
    // modifica
    ProyectoHitoInput proyectoHitoActualizado = mockUpdateProyectoHitoConAviso(AVISO_FECHA_ENVIO);
    proyectoHitoActualizado.getAviso().setAsunto("Asunto modificado");
    BDDMockito.given(emailService.findGenericEmailTextById(anyLong())).willReturn(buildMockEmailOutputAviso());

    // when: se actualiza el hito
    // then: se informa de que el aviso ya ha sido enviado
    Assertions.assertThatThrownBy(() -> service.update(1L, proyectoHitoActualizado))
        .isInstanceOf(SentAvisoNotUpdatableException.class);
  }

  @Test
  void update_WithAvisoPendienteDeEnvio_UpdatesProyectoHitoEmail() {
    // given: un hito de proyecto con un aviso cuya tarea aun no se ha enviado
    Proyecto proyecto = generarMockProyecto(1L);
    ProyectoHito proyectoHito = generarMockProyectoHito(1L);
    proyectoHito.setProyectoHitoAviso(ProyectoHitoAviso.builder()
        .comunicadoRef("10")
        .tareaProgramadaRef("20").build());
    ProyectoHitoInput proyectoHitoActualizado = generarMockProyectoHito();
    proyectoHitoActualizado.setFecha(Instant.now().plus(Period.ofDays(3)));
    proyectoHitoActualizado.getAviso().setAsunto("Asunto del aviso");
    proyectoHitoActualizado.getAviso().setContenido("Contenido del aviso");
    proyectoHitoActualizado.getAviso().setDestinatarios(Arrays.asList(
        ProyectoHitoAvisoInput.Destinatario.builder().nombre("test").email("test@test.com").build()));
    ModeloTipoHito modeloTipoHito = generarMockModeloTipoHito(1L, proyectoHito, Boolean.TRUE);
    modeloTipoHito.getTipoHito().setActivo(Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoHito));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito.given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.of(modeloTipoHito));
    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(modeloTipoHito.getTipoHito()));
    BDDMockito.given(sgiApiTaskService.findInstantTaskById(anyLong())).willReturn(SgiApiInstantTaskOutput.builder()
        .instant(Instant.now().plus(Period.ofDays(3))).id(1L).build());
    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoHito>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: se actualiza el hito de proyecto
    service.update(1L, proyectoHitoActualizado);

    // then: el email se actualiza usando el endpoint de hitos de proyecto
    verify(emailService).updateProyectoHitoEmail(ArgumentMatchers.eq(10L), ArgumentMatchers.eq(1L),
        ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.<List<Recipient>>any());
  }

  @Test
  void update_WithFechaAnterior_SaveGeneraAvisoFalse() {
    // given: Un nuevo ProyectoHito con el la fecha anterior
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHito proyectoHito = generarMockProyectoHito(1L);
    proyectoHito.setProyectoHitoAviso(ProyectoHitoAviso.builder()
        .comunicadoRef("1")
        .tareaProgramadaRef("1").build());
    ProyectoHitoInput proyectoHitoActualizado = generarMockProyectoHito();
    proyectoHitoActualizado.setTipoHitoId(2L);
    proyectoHitoActualizado.setFecha(Instant.now().minus(Period.ofDays(2)));
    ModeloTipoHito modeloTipoHito = generarMockModeloTipoHito(1L, proyectoHito, Boolean.TRUE);
    modeloTipoHito.getTipoHito().setActivo(Boolean.TRUE);
    SgiApiInstantTaskOutput task = SgiApiInstantTaskOutput.builder()
        .instant(Instant.now().plus(Period.ofDays(3)))
        .id(1L)
        .build();

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoHito));

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito.given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(
        ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(
            modeloTipoHito));
    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(
        modeloTipoHito.getTipoHito()));

    BDDMockito.given(sgiApiTaskService
        .findInstantTaskById(anyLong())).willReturn(task);
    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoHito>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ProyectoHito
    ProyectoHito updated = service.update(1L, proyectoHitoActualizado);

    // then: El ProyectoHito se actualiza correctamente.
    Assertions.assertThat(updated).as("isNotNull()").isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(proyectoHito.getId());
    Assertions.assertThat(updated.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoHito.getProyectoId());
    Assertions.assertThat(updated.getComentario()).as("getComentario()")
        .isEqualTo(proyectoHito.getComentario());
    Assertions.assertThat(updated.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(proyectoHito.getTipoHito().getId());
    Assertions.assertThat(updated.getFecha()).as("getFecha()")
        .isEqualTo(proyectoHitoActualizado.getFecha());
    // el aviso ya existente se conserva: la regla de la fecha pasada impide crear
    // uno
    // nuevo, no destruir el que el hito ya tuviera
    Assertions.assertThat(updated.getProyectoHitoAviso()).as("getProyectoHitoAviso()").isNotNull();
  }

  @Test
  void update_WithoutProyectoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoHito without ProyectoId

    List<I18nFieldValueDto> proyectoHitoComentario = new ArrayList<I18nFieldValueDto>();
    proyectoHitoComentario
        .add(new I18nFieldValueDto(Language.ES, "comentario modificado"));

    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyectoHito.setComentario(proyectoHitoComentario);
    proyectoHito.setProyectoId(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoHito
        () -> service.update(1L, proyectoHito))
        // then: throw exception as ProyectoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Proyecto no puede ser nulo");
  }

  @Test
  void update_WithoutTipoHitoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoHito without TipoHitoId

    List<I18nFieldValueDto> proyectoHitoComentario = new ArrayList<I18nFieldValueDto>();
    proyectoHitoComentario
        .add(new I18nFieldValueDto(Language.ES, "comentario modificado"));

    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyectoHito.setComentario(proyectoHitoComentario);
    proyectoHito.setTipoHitoId(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoHito
        () -> service.update(1L, proyectoHito))
        // then: throw exception as TipoHitoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Tipo Hito no puede ser nulo");
  }

  @Test
  void update_WithoutFecha_ThrowsIllegalArgumentException() {
    // given: a ProyectoHito without Fecha

    List<I18nFieldValueDto> proyectoHitoComentario = new ArrayList<I18nFieldValueDto>();
    proyectoHitoComentario
        .add(new I18nFieldValueDto(Language.ES, "comentario modificado"));

    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyectoHito.setComentario(proyectoHitoComentario);
    proyectoHito.setFecha(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoHito
        () -> service.update(1L, proyectoHito))
        // then: throw exception as Fecha is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Fecha de Proyecto Hito no puede ser nulo");
  }

  @Test
  void update_WithNoExistingProyecto_ThrowsProyectoNotFoundException() {
    // given: a ProyectoHito with non existing Proyecto
    ProyectoHito proyectoHitoOriginal = generarMockProyectoHito(1L);

    List<I18nFieldValueDto> proyectoHitoComentario = new ArrayList<I18nFieldValueDto>();
    proyectoHitoComentario
        .add(new I18nFieldValueDto(Language.ES, "comentario modificado"));

    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyectoHito.setComentario(proyectoHitoComentario);
    TipoHito tipoHito = generarMockTipoHito(1L, Boolean.TRUE);

    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(tipoHito));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoHitoOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: update ProyectoHito
        () -> service.update(1L, proyectoHito))
        // then: throw exception as Proyecto is not found
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  void update_WithoutModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: ProyectoHito con Proyecto sin Modelo de Ejecucion
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHito proyectoHitoOriginal = generarMockProyectoHito(1L);

    List<I18nFieldValueDto> proyectoHitoComentario = new ArrayList<I18nFieldValueDto>();
    proyectoHitoComentario
        .add(new I18nFieldValueDto(Language.ES, "comentario modificado"));

    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyectoHito.setComentario(proyectoHitoComentario);
    proyecto.setModeloEjecucion(null);
    TipoHito tipoHito = generarMockTipoHito(1L, Boolean.TRUE);

    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(tipoHito));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoHitoOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: update ProyectoHito
        () -> service.update(1L, proyectoHito))
        // then: throw exception as ModeloEjecucion not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tipo Hito no disponible para el Modelo Ejecución Proyecto sin modelo asignado");
  }

  @Test
  void update_WithoutModeloTipoHito_ThrowsIllegalArgumentException() {
    // given: ProyectoHito con TipoHito no asignado al Modelo de Ejecucion de la
    // proyecto
    ProyectoHito proyectoHitoOriginal = generarMockProyectoHito(1L);

    List<I18nFieldValueDto> proyectoHitoComentario = new ArrayList<I18nFieldValueDto>();
    proyectoHitoComentario
        .add(new I18nFieldValueDto(Language.ES, "comentario modificado"));

    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyectoHito.setComentario(proyectoHitoComentario);
    TipoHito tipoHito = generarMockTipoHito(1L, Boolean.TRUE);

    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(tipoHito));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoHitoOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ProyectoHito
        () -> service.update(1L, proyectoHito))
        // then: throw exception as ModeloTipoHito not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tipo Hito no disponible para el Modelo Ejecución Proyecto sin modelo asignado");
  }

  @Test
  void update_WithDisabledModeloTipoHito_ThrowsIllegalArgumentException() {
    // given: ProyectoHito con la asignación de TipoHito al Modelo de Ejecucion
    // de la proyecto inactiva
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHito proyectoHito = generarMockProyectoHito(1L);
    ProyectoHitoInput input = generarMockProyectoHito();
    ModeloTipoHito modeloTipoHito = generarMockModeloTipoHito(1L, generarMockProyectoHito(1L), Boolean.FALSE);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoHito));
    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(proyectoHito.getTipoHito()));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoHito));

    Assertions.assertThatThrownBy(
        // when: update ProyectoHito
        () -> service.update(proyectoId, input))
        // then: throw exception as ModeloTipoHito is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("%s de Modelo Tipo Hito no está activo para el modelo ejecución %s",
            modeloTipoHito.getTipoHito().getNombre(), proyecto.getModeloEjecucion().getNombre());
  }

  @Test
  void update_WithDisabledTipoHito_ThrowsIllegalArgumentException() {
    // given: ProyectoHito TipoHito disabled
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHito proyectoHito = generarMockProyectoHito(1L);
    proyectoHito.setId(null);
    proyectoHito.getTipoHito().setActivo(Boolean.FALSE);
    ProyectoHitoInput input = generarMockProyectoHito();

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoHito));
    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(proyectoHito.getTipoHito()));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoHito(1L, proyectoHito, Boolean.TRUE)));

    Assertions.assertThatThrownBy(
        // when: update ProyectoHito
        () -> service.update(proyectoId, input))
        // then: throw exception as TipoHito is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("%s de Tipo Hito no está activo", proyectoHito.getTipoHito().getNombre());
  }

  @Test
  void update_WithFechaYTipoHitoDuplicado_ThrowsIllegalArgumentException() {
    // given: Un ProyectoHito a actualizar con fecha duplicada
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoHito proyectoHitoExistente = generarMockProyectoHito(2L);
    ProyectoHito proyectoHitoOriginal = generarMockProyectoHito(1L);

    List<I18nFieldValueDto> proyectoHitoComentario = new ArrayList<I18nFieldValueDto>();
    proyectoHitoComentario
        .add(new I18nFieldValueDto(Language.ES, "comentario modificado"));

    ProyectoHitoInput proyectoHito = generarMockProyectoHito();
    proyectoHito.setComentario(proyectoHitoComentario);
    TipoHito tipoHito = generarMockTipoHito(1L, Boolean.TRUE);
    ModeloTipoHito modeloTipoHito = generarMockModeloTipoHito(1L, proyectoHito, Boolean.TRUE);
    modeloTipoHito.setTipoHito(tipoHito);

    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(tipoHito));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoHitoOriginal));

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(
            ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoHito));
    BDDMockito.given(repository.findByProyectoIdAndFechaAndTipoHitoId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoHitoExistente));

    // when: Actualizamos el ProyectoHito
    // then: Lanza una excepcion porque la fecha ya existe para ese tipo
    Assertions.assertThatThrownBy(() -> service.update(1L, proyectoHito))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Proyecto Hito de Proyecto ya existe");
  }

  @Test
  void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing ProyectoHito
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.<Long>any());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  void delete_WithNoExistingId_ThrowsNotFoundException() {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ProyectoHitoNotFoundException.class);
  }

  @Test
  void findById_ReturnsProyectoHito() {
    // given: Un ProyectoHito con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockProyectoHito(idBuscado)));

    // when: Buscamos el ProyectoHito por su id
    ProyectoHito proyectoHito = service.findById(idBuscado);

    // then: el ProyectoHito
    Assertions.assertThat(proyectoHito).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoHito.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  void findById_WithIdNotExist_ThrowsProyectoHitoNotFoundException() {
    // given: Ningun ProyectoHito con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ProyectoHito por su id
    // then: lanza un ProyectoHitoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ProyectoHitoNotFoundException.class);
  }

  @Test
  void findAllByProyecto_ReturnsPage() {
    // given: Una lista con 37 ProyectoHito para la Proyecto
    Long proyectoId = 1L;
    List<ProyectoHito> proyectosEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectosEntidadesConvocantes.add(generarMockProyectoHito(Long.valueOf(i)));
    }

    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<ProyectoHito>>any(),
                ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectosEntidadesConvocantes.size()
              ? proyectosEntidadesConvocantes.size()
              : toIndex;
          List<ProyectoHito> content = proyectosEntidadesConvocantes.subList(fromIndex,
              toIndex);
          return new PageImpl<>(content, pageable, proyectosEntidadesConvocantes.size());
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoHito> page = service.findAllByProyecto(proyectoId, null, paging);

    // then: Devuelve la pagina 3 con los ProyectoHito del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ProyectoHito proyectoHito = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyectoHito.getId()).isEqualTo(Long.valueOf(i));
    }
  }

  /**
   * Función que devuelve un objeto Proyecto
   * 
   * @param id id del Proyecto
   * @return el objeto Proyecto
   */
  private Proyecto generarMockProyecto(Long id) {
    EstadoProyecto estadoProyecto = generarMockEstadoProyecto(1L);

    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombre-modelo-ejecucion"));

    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);
    modeloEjecucion.setNombre(nombreModeloEjecucion);

    TipoFinalidad tipoFinalidad = new TipoFinalidad();
    tipoFinalidad.setId(1L);

    TipoAmbitoGeografico tipoAmbitoGeografico = new TipoAmbitoGeografico();
    tipoAmbitoGeografico.setId(1L);

    Set<ProyectoTitulo> tituloProyecto = new HashSet<>();
    tituloProyecto.add(new ProyectoTitulo(Language.ES, "PRO" + (id != null ? id : 1)));

    Set<ProyectoObservaciones> observacionesProyecto = new HashSet<>();
    observacionesProyecto
        .add(new ProyectoObservaciones(Language.ES, "observaciones-proyecto-" + String.format("%03d", id)));

    Proyecto proyecto = new Proyecto();
    proyecto.setId(id);
    proyecto.setTitulo(tituloProyecto);
    proyecto.setCodigoExterno("cod-externo-" + (id != null ? String.format("%03d", id) : "001"));
    proyecto.setObservaciones(observacionesProyecto);
    proyecto.setUnidadGestionRef("2");
    proyecto.setFechaInicio(Instant.now());
    proyecto.setFechaFin(Instant.now());
    proyecto.setModeloEjecucion(modeloEjecucion);
    proyecto.setFinalidad(tipoFinalidad);
    proyecto.setAmbitoGeografico(tipoAmbitoGeografico);
    proyecto.setActivo(true);

    if (id != null) {
      proyecto.setEstado(estadoProyecto);
    }

    return proyecto;
  }

  /**
   * Función que devuelve un objeto EstadoProyecto
   * 
   * @param id id del EstadoProyecto
   * @return el objeto EstadoProyecto
   */
  private EstadoProyecto generarMockEstadoProyecto(Long id) {
    Set<EstadoProyectoComentario> estadoProyectoComentario = new HashSet<>();
    estadoProyectoComentario
        .add(new EstadoProyectoComentario(Language.ES, "estado-proyecto-" + String.format("%03d", id)));

    EstadoProyecto estadoProyecto = new EstadoProyecto();
    estadoProyecto.setId(id);
    estadoProyecto.setComentario(estadoProyectoComentario);
    estadoProyecto.setEstado(EstadoProyecto.Estado.BORRADOR);
    estadoProyecto.setFechaEstado(Instant.now());
    estadoProyecto.setProyectoId(1L);

    return estadoProyecto;
  }

  /**
   * Función que devuelve un objeto TipoHito
   * 
   * @param id     id del TipoHito
   * @param activo
   * @return el objeto TipoHito
   */
  private TipoHito generarMockTipoHito(Long id, Boolean activo) {
    Set<TipoHitoNombre> nombreTipoHito = new HashSet<>();
    nombreTipoHito.add(new TipoHitoNombre(Language.ES, "nombre-hito-" + String.format("%03d", id)));

    Set<TipoHitoDescripcion> descripcionTipoHito = new HashSet<>();
    descripcionTipoHito.add(new TipoHitoDescripcion(Language.ES, "descripcion-hito-" + String.format("%03d", id)));

    TipoHito tipoHito = new TipoHito();
    tipoHito.setId(id);
    tipoHito.setNombre(nombreTipoHito);
    tipoHito.setDescripcion(descripcionTipoHito);
    tipoHito.setActivo(activo);

    return tipoHito;
  }

  /**
   * Función que genera ModeloTipoHito a partir de un objeto ProyectoHito
   * 
   * @param id
   * @param proyectoHito
   * @param activo
   * @return
   */
  private ModeloTipoHito generarMockModeloTipoHito(Long id, ProyectoHito proyectoHito, Boolean activo) {
    return ModeloTipoHito.builder()
        .id(id)
        .modeloEjecucion(generarMockProyecto(proyectoHito.getProyectoId()).getModeloEjecucion())
        .tipoHito(proyectoHito.getTipoHito())
        .activo(activo)
        .build();
  }

  private ModeloTipoHito generarMockModeloTipoHito(Long id, ProyectoHitoInput proyectoHito, Boolean activo) {
    Set<TipoHitoNombre> nombreTipoHito = new HashSet<>();
    nombreTipoHito.add(new TipoHitoNombre(Language.ES, "nombreTipoHito"));

    return ModeloTipoHito.builder()
        .id(id)
        .modeloEjecucion(generarMockProyecto(proyectoHito.getProyectoId()).getModeloEjecucion())
        .tipoHito(TipoHito.builder()
            .nombre(nombreTipoHito)
            .id(proyectoHito.getTipoHitoId())
            .build())
        .activo(activo)
        .build();
  }

  /**
   * Función que devuelve un objeto ProyectoHito
   * 
   * @param id id del ProyectoHito
   * @return el objeto ProyectoHito
   */
  private ProyectoHito generarMockProyectoHito(Long id) {

    Set<ProyectoHitoComentario> proyectoHitoComentario = new HashSet<>();
    proyectoHitoComentario
        .add(new ProyectoHitoComentario(Language.ES, "comentario-proyecto-hito" + String.format("%03d", id)));

    return ProyectoHito.builder()
        .id(id)
        .proyectoId(1L)
        .fecha(Instant.parse("2020-10-19T00:00:00Z"))
        .comentario(proyectoHitoComentario)
        .proyectoHitoAviso(ProyectoHitoAviso.builder().build())
        .tipoHito(generarMockTipoHito(1L, Boolean.TRUE))
        .build();
  }

  private ProyectoHitoInput generarMockProyectoHito() {

    List<I18nFieldValueDto> proyectoHitoComentario = new ArrayList<I18nFieldValueDto>();
    proyectoHitoComentario
        .add(new I18nFieldValueDto(Language.ES, "comentario-proyecto-hito" + String.format("%03d", 1)));

    return ProyectoHitoInput.builder()
        .proyectoId(1L)
        .fecha(Instant.parse("2020-10-19T00:00:00Z"))
        .comentario(proyectoHitoComentario)
        .aviso(ProyectoHitoAvisoInput.builder()
            .fechaEnvio(Instant.parse("2020-10-18T00:00:00Z"))
            .asunto("Asunto del aviso")
            .contenido("Contenido del aviso")
            .destinatarios(Arrays.asList(
                ProyectoHitoAvisoInput.Destinatario.builder().nombre("test").email("test@test.com").build()))
            .incluirIpsProyecto(Boolean.FALSE)
            .build())
        .tipoHitoId(1L)
        .build();
  }

  private ProyectoHitoInput mockUpdateProyectoHitoConAviso(Instant fechaEnvioTarea) {
    Proyecto proyecto = generarMockProyecto(1L);
    ProyectoHito proyectoHito = generarMockProyectoHito(1L);
    proyectoHito.setProyectoHitoAviso(ProyectoHitoAviso.builder()
        .comunicadoRef("10").tareaProgramadaRef("20").incluirIpsProyecto(Boolean.FALSE).build());
    ModeloTipoHito modeloTipoHito = generarMockModeloTipoHito(1L, proyectoHito, Boolean.TRUE);
    modeloTipoHito.getTipoHito().setActivo(Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoHito));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito.given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.of(modeloTipoHito));
    BDDMockito.given(tipoHitoRepository.findById(anyLong())).willReturn(Optional.of(modeloTipoHito.getTipoHito()));
    BDDMockito.given(sgiApiTaskService.findInstantTaskById(anyLong()))
        .willReturn(SgiApiInstantTaskOutput.builder().instant(fechaEnvioTarea).id(1L).build());

    return generarMockProyectoHito();
  }

  private EmailOutput buildMockEmailOutputAviso() {
    EmailOutput email = EmailOutput.builder().id(10L).build();
    email.setRecipients(Arrays.asList(new Recipient("test", "test@test.com")));
    email.setParams(ComGenericEmailTextHelper.buildParams("Asunto del aviso", "Contenido del aviso"));
    return email;
  }

}
