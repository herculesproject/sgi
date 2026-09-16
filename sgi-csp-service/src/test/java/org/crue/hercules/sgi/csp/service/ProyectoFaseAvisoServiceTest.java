package org.crue.hercules.sgi.csp.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.ProyectoFaseAvisoInput;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.EmailParam;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.tp.SgiApiInstantTaskOutput;
import org.crue.hercules.sgi.csp.exceptions.SentAvisoNotDeletableException;
import org.crue.hercules.sgi.csp.exceptions.SentAvisoNotUpdatableException;
import org.crue.hercules.sgi.csp.model.ProyectoFaseAviso;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoFaseAvisoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoFaseRepository;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiTpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;

/**
 * ProyectoFaseAvisoServiceTest
 */
class ProyectoFaseAvisoServiceTest extends BaseServiceTest {

  private static final String AVISO_ASUNTO = "Asunto del aviso";
  private static final String AVISO_CONTENIDO = "Contenido del aviso";
  private static final String PARAM_SUBJECT = "GENERIC_SUBJECT";
  private static final String PARAM_CONTENT = "GENERIC_CONTENT_TEXT";
  private static final Long COMUNICADO_REF = 10L;
  private static final Long TAREA_PROGRAMADA_REF = 20L;
  private static final Long PROYECTO_FASE_ID = 5L;

  @Mock
  private SgiApiComService emailService;
  @Mock
  private SgiApiTpService sgiApiTaskService;
  @Mock
  private SgiApiSgpService personaService;
  @Mock
  private ProyectoEquipoRepository proyectoEquipoRepository;
  @Mock
  private ProyectoFaseAvisoRepository proyectoFaseAvisoRepository;
  @Mock
  private ProyectoFaseRepository proyectoFaseRepository;

  private ProyectoFaseAvisoService service;

  @BeforeEach
  void setUp() {
    service = new ProyectoFaseAvisoService(emailService, sgiApiTaskService, personaService, proyectoEquipoRepository,
        proyectoFaseAvisoRepository, proyectoFaseRepository);
  }

  @Test
  void create_CreatesProyectoFaseEmail() {
    // given: un aviso con fecha de envio futura
    ProyectoFaseAvisoInput avisoInput = buildAvisoInput(Instant.now().plus(1, ChronoUnit.DAYS));
    BDDMockito.given(emailService.createProyectoFaseEmail(anyLong(), ArgumentMatchers.anyString(),
        ArgumentMatchers.anyString(), ArgumentMatchers.<List<Recipient>>any())).willReturn(COMUNICADO_REF);
    BDDMockito.given(sgiApiTaskService.createSendEmailTask(anyLong(), ArgumentMatchers.<Instant>any()))
        .willReturn(TAREA_PROGRAMADA_REF);
    BDDMockito.given(proyectoFaseAvisoRepository.save(ArgumentMatchers.<ProyectoFaseAviso>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: se crea el aviso de la fase
    ProyectoFaseAviso aviso = service.create(PROYECTO_FASE_ID, avisoInput);

    // then: el email se crea contra la fase de proyecto
    verify(emailService).createProyectoFaseEmail(ArgumentMatchers.eq(PROYECTO_FASE_ID),
        ArgumentMatchers.eq(AVISO_ASUNTO), ArgumentMatchers.eq(AVISO_CONTENIDO),
        ArgumentMatchers.<List<Recipient>>any());
    Assertions.assertThat(aviso.getComunicadoRef()).isEqualTo(COMUNICADO_REF.toString());
    Assertions.assertThat(aviso.getTareaProgramadaRef()).isEqualTo(TAREA_PROGRAMADA_REF.toString());
  }

  @Test
  void create_WithFechaEnvioPasada_ThrowsIllegalArgumentException() {
    // given: un aviso con fecha de envio pasada
    ProyectoFaseAvisoInput avisoInput = buildAvisoInput(Instant.now().minus(1, ChronoUnit.DAYS));

    // when: se crea el aviso de la fase
    // then: se rechaza por ser una fecha anterior a la actual
    Assertions.assertThatThrownBy(() -> service.create(PROYECTO_FASE_ID, avisoInput))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Fecha de envio debe ser anterior a");
  }

  @Test
  void updateAvisoIfNeeded_WithAvisoPendienteDeEnvio_UpdatesProyectoFaseEmail() {
    // given: un aviso cuya tarea programada aun no se ha enviado
    Instant fechaEnvio = Instant.now().plus(1, ChronoUnit.DAYS);
    ProyectoFaseAvisoInput avisoInput = buildAvisoInput(fechaEnvio);
    mockTareaProgramada(fechaEnvio);

    // when: se actualiza el aviso
    service.updateAvisoIfNeeded(avisoInput, buildAviso(), PROYECTO_FASE_ID);

    // then: el email se actualiza usando el endpoint de fases de proyecto
    verify(emailService).updateProyectoFaseEmail(ArgumentMatchers.eq(COMUNICADO_REF),
        ArgumentMatchers.eq(PROYECTO_FASE_ID), ArgumentMatchers.eq(AVISO_ASUNTO),
        ArgumentMatchers.eq(AVISO_CONTENIDO), ArgumentMatchers.<List<Recipient>>any());
    verify(emailService, never()).updateConvocatoriaHitoEmail(anyLong(), anyLong(),
        ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.<List<Recipient>>any());
  }

  @Test
  void updateAvisoIfNeeded_WithAvisoOrInputNulo_DoesNothing() {
    // given: una fase sin aviso entrante, y otra sin aviso persistido
    ProyectoFaseAvisoInput avisoInput = buildAvisoInput(Instant.now().plus(1, ChronoUnit.DAYS));

    // when: se actualiza el aviso en ambos casos
    service.updateAvisoIfNeeded(null, buildAviso(), PROYECTO_FASE_ID);
    service.updateAvisoIfNeeded(avisoInput, null, PROYECTO_FASE_ID);

    // then: no se consulta la tarea programada ni se toca el email
    verifyNoInteractions(sgiApiTaskService, emailService, proyectoFaseAvisoRepository);
  }

  @Test
  void updateAvisoIfNeeded_WithAvisoEnviadoYAsuntoModificado_ThrowsSentAvisoNotUpdatableException() {
    // given: un aviso ya enviado cuyo asunto se ha modificado
    Instant fechaEnvio = Instant.now().minus(1, ChronoUnit.DAYS);
    ProyectoFaseAvisoInput avisoInput = buildAvisoInput(fechaEnvio);
    avisoInput.setAsunto("Asunto modificado");
    ProyectoFaseAviso aviso = buildAviso();
    mockTareaProgramada(fechaEnvio);
    BDDMockito.given(emailService.findGenericEmailTextById(anyLong())).willReturn(buildEmailOutput());

    // when: se actualiza el aviso
    // then: se informa de que el aviso ya ha sido enviado
    Assertions.assertThatThrownBy(() -> service.updateAvisoIfNeeded(avisoInput, aviso, PROYECTO_FASE_ID))
        .isInstanceOf(SentAvisoNotUpdatableException.class);
  }

  @Test
  void updateAvisoIfNeeded_WithAvisoEnviadoYDestinatariosModificados_ThrowsSentAvisoNotUpdatableException() {
    // given: un aviso ya enviado al que se le cambian los destinatarios
    Instant fechaEnvio = Instant.now().minus(1, ChronoUnit.DAYS);
    ProyectoFaseAvisoInput avisoInput = buildAvisoInput(fechaEnvio);
    avisoInput.setDestinatarios(Arrays.asList(
        ProyectoFaseAvisoInput.Destinatario.builder().nombre("otro").email("otro@test.com").build()));
    ProyectoFaseAviso aviso = buildAviso();
    mockTareaProgramada(fechaEnvio);
    BDDMockito.given(emailService.findGenericEmailTextById(anyLong())).willReturn(buildEmailOutput());

    // when: se actualiza el aviso
    // then: se informa de que el aviso ya ha sido enviado
    Assertions.assertThatThrownBy(() -> service.updateAvisoIfNeeded(avisoInput, aviso, PROYECTO_FASE_ID))
        .isInstanceOf(SentAvisoNotUpdatableException.class);
  }

  @Test
  void updateAvisoIfNeeded_WithAvisoEnviadoSinCambios_DoesNotUpdateEmail() {
    // given: un aviso ya enviado y sin cambios en sus datos
    Instant fechaEnvio = Instant.now().minus(1, ChronoUnit.DAYS);
    ProyectoFaseAvisoInput avisoInput = buildAvisoInput(fechaEnvio);
    mockTareaProgramada(fechaEnvio);
    BDDMockito.given(emailService.findGenericEmailTextById(anyLong())).willReturn(buildEmailOutput());

    // when: se actualiza el aviso
    service.updateAvisoIfNeeded(avisoInput, buildAviso(), PROYECTO_FASE_ID);

    // then: no se modifica ni el email ni la tarea programada
    verify(emailService, never()).updateProyectoFaseEmail(anyLong(), anyLong(),
        ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.<List<Recipient>>any());
    verify(sgiApiTaskService, never()).updateSendEmailTask(anyLong(), anyLong(),
        ArgumentMatchers.<Instant>any());
  }

  @Test
  void updateAvisoIfNeeded_WithAvisoEnviadoConCambios_ThrowsSentAvisoNotUpdatableException() {
    // given: un aviso ya enviado cuyo contenido se ha modificado
    Instant fechaEnvio = Instant.now().minus(1, ChronoUnit.DAYS);
    ProyectoFaseAvisoInput avisoInput = buildAvisoInput(fechaEnvio);
    avisoInput.setContenido("Contenido modificado");
    ProyectoFaseAviso aviso = buildAviso();
    mockTareaProgramada(fechaEnvio);
    BDDMockito.given(emailService.findGenericEmailTextById(anyLong())).willReturn(buildEmailOutput());

    // when: se actualiza el aviso
    // then: se informa de que el aviso ya ha sido enviado
    Assertions.assertThatThrownBy(() -> service.updateAvisoIfNeeded(avisoInput, aviso, PROYECTO_FASE_ID))
        .isInstanceOf(SentAvisoNotUpdatableException.class);
  }

  @Test
  void updateAvisoIfNeeded_WithAvisoEnviadoYFechaEnvioModificada_ThrowsSentAvisoNotUpdatableException() {
    // given: un aviso ya enviado que se intenta reprogramar a otra fecha
    Instant fechaEnvio = Instant.now().minus(1, ChronoUnit.DAYS);
    ProyectoFaseAvisoInput avisoInput = buildAvisoInput(Instant.now().plus(1, ChronoUnit.DAYS));
    ProyectoFaseAviso aviso = buildAviso();
    mockTareaProgramada(fechaEnvio);

    // when: se actualiza el aviso
    // then: se informa de que el aviso ya ha sido enviado, sin llegar a consultar
    // el comunicado en el modulo COM
    Assertions.assertThatThrownBy(() -> service.updateAvisoIfNeeded(avisoInput, aviso, PROYECTO_FASE_ID))
        .isInstanceOf(SentAvisoNotUpdatableException.class);
    verify(emailService, never()).findGenericEmailTextById(anyLong());
  }

  @Test
  void updateAvisoIfNeeded_WithAvisoEnviadoYIncluirIpsProyectoModificado_ThrowsSentAvisoNotUpdatableException() {
    // given: un aviso ya enviado en el que se marca incluir a los IP del proyecto
    Instant fechaEnvio = Instant.now().minus(1, ChronoUnit.DAYS);
    ProyectoFaseAvisoInput avisoInput = buildAvisoInput(fechaEnvio);
    avisoInput.setIncluirIpsProyecto(Boolean.TRUE);
    ProyectoFaseAviso aviso = buildAviso();
    mockTareaProgramada(fechaEnvio);

    // when: se actualiza el aviso
    // then: se informa de que el aviso ya ha sido enviado
    Assertions.assertThatThrownBy(() -> service.updateAvisoIfNeeded(avisoInput, aviso, PROYECTO_FASE_ID))
        .isInstanceOf(SentAvisoNotUpdatableException.class);
  }

  @Test
  void updateAvisoIfNeeded_WithAvisoEnviadoYDestinatariosEnDistintoOrden_DoesNotUpdateEmail() {
    // given: un aviso ya enviado cuyos destinatarios llegan en distinto orden
    Instant fechaEnvio = Instant.now().minus(1, ChronoUnit.DAYS);
    ProyectoFaseAvisoInput avisoInput = buildAvisoInput(fechaEnvio);
    avisoInput.setDestinatarios(Arrays.asList(
        ProyectoFaseAvisoInput.Destinatario.builder().nombre("dos").email("dos@test.com").build(),
        ProyectoFaseAvisoInput.Destinatario.builder().nombre("uno").email("uno@test.com").build()));
    mockTareaProgramada(fechaEnvio);
    BDDMockito.given(emailService.findGenericEmailTextById(anyLong())).willReturn(buildEmailOutput());

    // when: se actualiza el aviso
    service.updateAvisoIfNeeded(avisoInput, buildAviso(), PROYECTO_FASE_ID);

    // then: el orden no se considera un cambio y no se modifica el email
    verify(emailService, never()).updateProyectoFaseEmail(anyLong(), anyLong(),
        ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.<List<Recipient>>any());
  }

  @Test
  void deleteAvisoIfPossible_WithAvisoPendienteDeEnvio_DeletesAviso() {
    // given: un aviso cuya tarea programada aun no se ha enviado
    ProyectoFaseAviso aviso = buildAviso();
    mockTareaProgramada(Instant.now().plus(1, ChronoUnit.DAYS));

    // when: se elimina el aviso
    boolean deleted = service.deleteAvisoIfPossible(null, aviso, SentAvisoNotDeletableException::new);

    // then: se borran la tarea programada y el email
    Assertions.assertThat(deleted).isTrue();
    verify(sgiApiTaskService).deleteTask(TAREA_PROGRAMADA_REF);
    verify(emailService).deleteEmail(COMUNICADO_REF);
    verify(proyectoFaseAvisoRepository).delete(aviso);
  }

  @Test
  void deleteAvisoIfPossible_WithAvisoEnviado_ThrowsSentAvisoNotDeletableException() {
    // given: un aviso ya enviado
    ProyectoFaseAviso aviso = buildAviso();
    mockTareaProgramada(Instant.now().minus(1, ChronoUnit.DAYS));

    // when: se desmarca el aviso de la fase
    // then: se informa de que el aviso ya ha sido enviado
    Assertions.assertThatThrownBy(() -> service.deleteAvisoIfPossible(null, aviso, SentAvisoNotDeletableException::new))
        .isInstanceOf(SentAvisoNotDeletableException.class);
  }

  private void mockTareaProgramada(Instant instant) {
    BDDMockito.given(sgiApiTaskService.findInstantTaskById(anyLong()))
        .willReturn(SgiApiInstantTaskOutput.builder().instant(instant).build());
  }

  private ProyectoFaseAviso buildAviso() {
    ProyectoFaseAviso aviso = new ProyectoFaseAviso();
    aviso.setId(1L);
    aviso.setComunicadoRef(COMUNICADO_REF.toString());
    aviso.setTareaProgramadaRef(TAREA_PROGRAMADA_REF.toString());
    aviso.setIncluirIpsProyecto(Boolean.FALSE);
    return aviso;
  }

  private ProyectoFaseAvisoInput buildAvisoInput(Instant fechaEnvio) {
    return ProyectoFaseAvisoInput.builder()
        .fechaEnvio(fechaEnvio)
        .asunto(AVISO_ASUNTO)
        .contenido(AVISO_CONTENIDO)
        .destinatarios(Arrays.asList(
            ProyectoFaseAvisoInput.Destinatario.builder().nombre("uno").email("uno@test.com").build(),
            ProyectoFaseAvisoInput.Destinatario.builder().nombre("dos").email("dos@test.com").build()))
        .incluirIpsProyecto(Boolean.FALSE)
        .build();
  }

  private EmailOutput buildEmailOutput() {
    EmailOutput email = EmailOutput.builder().id(COMUNICADO_REF).build();
    email.setRecipients(Arrays.asList(
        new Recipient("uno", "uno@test.com"),
        new Recipient("dos", "dos@test.com")));
    email.setParams(Arrays.asList(
        new EmailParam(PARAM_SUBJECT, AVISO_ASUNTO),
        new EmailParam(PARAM_CONTENT, AVISO_CONTENIDO)));
    return email;
  }

}
