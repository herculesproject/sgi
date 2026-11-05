package org.crue.hercules.sgi.csp.util;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.crue.hercules.sgi.csp.dto.tp.SgiApiInstantTaskOutput;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiTpService;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Activa y desactiva en TP las tareas programadas de envío de los avisos.
 *
 * Solo actúa sobre las tareas pendientes (cuya fecha de envío todavía no ha
 * llegado), de forma que nunca se reprograma un aviso ya enviado.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AvisoTaskHelper {

  private final SgiApiTpService sgiApiTaskService;

  /**
   * Activa las tareas programadas pendientes de envío.
   *
   * @param tareaProgramadaRefs referencias de las tareas programadas de los
   *                            avisos
   */
  public void enablePendingTasks(List<String> tareaProgramadaRefs) {
    log.debug("enablePendingTasks - tareaProgramadaRefs: {}", tareaProgramadaRefs);
    updateTasks(findPendingTaskIds(tareaProgramadaRefs), TaskAction.ENABLE);
  }

  /**
   * Desactiva las tareas programadas pendientes de envío, de forma que no se
   * envían mientras sigan desactivadas.
   *
   * @param tareaProgramadaRefs referencias de las tareas programadas de los
   *                            avisos
   */
  public void disablePendingTasks(List<String> tareaProgramadaRefs) {
    log.debug("disablePendingTasks - tareaProgramadaRefs: {}", tareaProgramadaRefs);
    updateTasks(findPendingTaskIds(tareaProgramadaRefs), TaskAction.DISABLE);
  }

  /**
   * Aplica la acción a las tareas programadas indicadas. Si falla alguna,
   * compensa las ya procesadas aplicando la acción contraria y relanza la
   * excepción original, de forma que TP queda coherente con la entidad cuyo
   * cambio se revierte con la transacción.
   *
   * @param taskIds identificadores de las tareas programadas
   * @param action  acción a aplicar sobre cada tarea
   */
  private void updateTasks(List<Long> taskIds, TaskAction action) {
    List<Long> processed = new ArrayList<>();
    for (Long taskId : taskIds) {
      try {
        updateTask(taskId, action);
      } catch (RuntimeException ex) {
        compensate(processed, action);
        throw ex;
      }
      processed.add(taskId);
    }
  }

  /**
   * Aplica la acción contraria a las tareas programadas ya procesadas. Los
   * errores solo se registran: no se compensa la compensación, para no
   * encadenar compensaciones.
   *
   * @param processed identificadores de las tareas programadas ya procesadas
   * @param action    acción que se había aplicado sobre ellas
   */
  private void compensate(List<Long> processed, TaskAction action) {
    log.warn("compensate - action: {}, taskIds: {}", action, processed);
    for (Long taskId : processed) {
      try {
        updateTask(taskId, action.compensation());
      } catch (RuntimeException ex) {
        log.error("compensate - no se ha podido compensar, taskId: {}", taskId, ex);
      }
    }
  }

  private void updateTask(Long taskId, TaskAction action) {
    if (action == TaskAction.ENABLE) {
      sgiApiTaskService.enableTask(taskId);
    } else {
      sgiApiTaskService.disableTask(taskId);
    }
  }

  /**
   * Obtiene los identificadores de las tareas programadas cuya fecha de envío
   * todavía no ha llegado.
   *
   * @param tareaProgramadaRefs referencias de las tareas programadas
   * @return identificadores de las tareas programadas pendientes
   */
  private List<Long> findPendingTaskIds(List<String> tareaProgramadaRefs) {
    Instant now = Instant.now();
    return tareaProgramadaRefs.stream()
        .map(Long::parseLong)
        .filter(taskId -> isPending(sgiApiTaskService.findInstantTaskById(taskId), now))
        .toList();
  }

  private boolean isPending(SgiApiInstantTaskOutput task, Instant now) {
    return task.getInstant().isAfter(now);
  }

  /** Acción a aplicar sobre una tarea programada de TP. */
  private enum TaskAction {
    ENABLE,
    DISABLE;

    TaskAction compensation() {
      return this == ENABLE ? DISABLE : ENABLE;
    }
  }

}
