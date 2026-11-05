package org.crue.hercules.sgi.csp.util;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.tp.SgiApiInstantTaskOutput;
import org.crue.hercules.sgi.csp.service.BaseServiceTest;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiTpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;

/**
 * AvisoTaskHelperTest
 */
class AvisoTaskHelperTest extends BaseServiceTest {

  private static final Long PENDING_TASK_ID = 11L;
  private static final Long SENT_TASK_ID = 12L;
  private static final Long FAILING_TASK_ID = 21L;

  @Mock
  private SgiApiTpService sgiApiTaskService;

  private AvisoTaskHelper helper;

  @BeforeEach
  void setUp() {
    helper = new AvisoTaskHelper(sgiApiTaskService);
  }

  @Test
  void disablePendingTasks_DisablesOnlyPendingTasks() {
    // given: dos tareas pendientes y una ya enviada
    List<String> tareaProgramadaRefs = givenTasks();

    // when: se desactivan las tareas pendientes
    helper.disablePendingTasks(tareaProgramadaRefs);

    // then: solo se desactivan las tareas cuya fecha de envio no ha llegado
    verify(sgiApiTaskService).disableTask(PENDING_TASK_ID);
    verify(sgiApiTaskService).disableTask(FAILING_TASK_ID);
    verify(sgiApiTaskService, never()).disableTask(SENT_TASK_ID);
    verify(sgiApiTaskService, never()).enableTask(ArgumentMatchers.anyLong());
  }

  @Test
  void enablePendingTasks_EnablesOnlyPendingTasks() {
    // given: dos tareas pendientes y una ya enviada
    List<String> tareaProgramadaRefs = givenTasks();

    // when: se activan las tareas pendientes
    helper.enablePendingTasks(tareaProgramadaRefs);

    // then: solo se activan las tareas cuya fecha de envio no ha llegado
    verify(sgiApiTaskService).enableTask(PENDING_TASK_ID);
    verify(sgiApiTaskService).enableTask(FAILING_TASK_ID);
    verify(sgiApiTaskService, never()).enableTask(SENT_TASK_ID);
    verify(sgiApiTaskService, never()).disableTask(ArgumentMatchers.anyLong());
  }

  @Test
  void enablePendingTasks_WhenTpFails_CompensatesProcessedTasksAndRethrows() {
    // given: tareas pendientes y TP falla al activar la segunda
    List<String> tareaProgramadaRefs = givenTasks();
    RuntimeException tpError = new RuntimeException("TP no disponible");
    givenEnableTaskFailsFor(FAILING_TASK_ID, tpError);

    // when: se activan las tareas pendientes
    Assertions.assertThatThrownBy(() -> helper.enablePendingTasks(tareaProgramadaRefs))
        // then: se relanza el error de TP
        .isSameAs(tpError);

    // and: se vuelve a desactivar la tarea que ya se había activado
    verify(sgiApiTaskService).disableTask(PENDING_TASK_ID);
    verify(sgiApiTaskService, never()).disableTask(FAILING_TASK_ID);
  }

  @Test
  void disablePendingTasks_WhenTpFails_CompensatesProcessedTasksAndRethrows() {
    // given: tareas pendientes y TP falla al desactivar la segunda
    List<String> tareaProgramadaRefs = givenTasks();
    RuntimeException tpError = new RuntimeException("TP no disponible");
    BDDMockito.willAnswer(invocation -> {
      if (FAILING_TASK_ID.equals(invocation.getArgument(0))) {
        throw tpError;
      }
      return null;
    }).given(sgiApiTaskService).disableTask(ArgumentMatchers.anyLong());

    // when: se desactivan las tareas pendientes
    Assertions.assertThatThrownBy(() -> helper.disablePendingTasks(tareaProgramadaRefs))
        // then: se relanza el error de TP
        .isSameAs(tpError);

    // and: se vuelve a activar la tarea que ya se había desactivado
    verify(sgiApiTaskService).enableTask(PENDING_TASK_ID);
    verify(sgiApiTaskService, never()).enableTask(FAILING_TASK_ID);
  }

  @Test
  void enablePendingTasks_WhenTpFailsAndCompensationFails_RethrowsOriginalErrorWithoutChainedCompensation() {
    // given: tareas pendientes, TP falla al activar la segunda y también al
    // compensar la primera
    List<String> tareaProgramadaRefs = givenTasks();
    RuntimeException tpError = new RuntimeException("TP no disponible");
    givenEnableTaskFailsFor(FAILING_TASK_ID, tpError);
    BDDMockito.willThrow(new RuntimeException("TP no disponible al compensar")).given(sgiApiTaskService)
        .disableTask(ArgumentMatchers.anyLong());

    // when: se activan las tareas pendientes
    Assertions.assertThatThrownBy(() -> helper.enablePendingTasks(tareaProgramadaRefs))
        // then: se relanza el error original, no el de la compensación
        .isSameAs(tpError);

    // and: la compensación se intenta una sola vez y no se compensa a su vez
    verify(sgiApiTaskService).findInstantTaskById(PENDING_TASK_ID);
    verify(sgiApiTaskService).findInstantTaskById(SENT_TASK_ID);
    verify(sgiApiTaskService).findInstantTaskById(FAILING_TASK_ID);
    verify(sgiApiTaskService).enableTask(PENDING_TASK_ID);
    verify(sgiApiTaskService).enableTask(FAILING_TASK_ID);
    verify(sgiApiTaskService).disableTask(PENDING_TASK_ID);
    BDDMockito.verifyNoMoreInteractions(sgiApiTaskService);
  }

  @Test
  void disablePendingTasks_WithoutTasks_DoesNotCallTp() {
    // given: ninguna tarea programada

    // when: se desactivan las tareas pendientes
    helper.disablePendingTasks(Collections.emptyList());

    // then: no se llama a TP
    BDDMockito.verifyNoInteractions(sgiApiTaskService);
  }

  private List<String> givenTasks() {
    Instant future = Instant.now().plus(1, ChronoUnit.DAYS);
    Instant past = Instant.now().minus(1, ChronoUnit.DAYS);
    BDDMockito.given(sgiApiTaskService.findInstantTaskById(PENDING_TASK_ID))
        .willReturn(buildTask(PENDING_TASK_ID, future));
    BDDMockito.given(sgiApiTaskService.findInstantTaskById(SENT_TASK_ID))
        .willReturn(buildTask(SENT_TASK_ID, past));
    BDDMockito.given(sgiApiTaskService.findInstantTaskById(FAILING_TASK_ID))
        .willReturn(buildTask(FAILING_TASK_ID, future));
    return Arrays.asList(PENDING_TASK_ID.toString(), SENT_TASK_ID.toString(), FAILING_TASK_ID.toString());
  }

  private void givenEnableTaskFailsFor(Long taskId, RuntimeException error) {
    BDDMockito.willAnswer(invocation -> {
      if (taskId.equals(invocation.getArgument(0))) {
        throw error;
      }
      return null;
    }).given(sgiApiTaskService).enableTask(ArgumentMatchers.anyLong());
  }

  private SgiApiInstantTaskOutput buildTask(Long id, Instant instant) {
    SgiApiInstantTaskOutput task = new SgiApiInstantTaskOutput();
    task.setId(id);
    task.setInstant(instant);
    return task;
  }

}
