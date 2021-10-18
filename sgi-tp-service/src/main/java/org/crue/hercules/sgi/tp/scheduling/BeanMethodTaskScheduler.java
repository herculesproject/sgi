package org.crue.hercules.sgi.tp.scheduling;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;

import org.crue.hercules.sgi.tp.model.BeanMethodCronTask;
import org.crue.hercules.sgi.tp.model.BeanMethodInstantTask;
import org.crue.hercules.sgi.tp.model.BeanMethodTask;
import org.crue.hercules.sgi.tp.run.RunnableBeanMethod;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BeanMethodTaskScheduler {
  private boolean enabled = false;
  public static final long GRACE_TIME = 5;
  private static final String TASK_PREFIX = "BeanMethodTask";
  private final TaskRegistrar cronTaskRegistrar;

  public BeanMethodTaskScheduler(TaskRegistrar cronTaskRegistrar) {
    this.cronTaskRegistrar = cronTaskRegistrar;
  }

  /**
   * Shedules the provided BeanMethodTask
   * 
   * @param task the BeanMethodTask to be scheduled
   * @return boolean true if the BeanMethodTask has been scheduled or false in
   *         other case
   */
  public boolean scheduleTask(BeanMethodTask task) {
    boolean scheduled = false;
    if (enabled) {
      if (task.getDisabled()) {
        unScheduleTask(task);
      } else {
        RunnableBeanMethod runnableBeanMethod;
        try {
          runnableBeanMethod = new RunnableBeanMethod(task.getBean(), task.getMethod(), task.getParams().toArray());

          if (task instanceof BeanMethodCronTask) {
            cronTaskRegistrar.addTask(getBeanMethodTaskId(task.getId()), runnableBeanMethod,
                ((BeanMethodCronTask) task).getCronExpression());
            scheduled = true;
          } else if (task instanceof BeanMethodInstantTask) {
            BeanMethodInstantTask beanMethodInstantTask = (BeanMethodInstantTask) task;
            Instant now = Instant.now();
            long timeElapsed = Duration.between(beanMethodInstantTask.getInstant(), now).toMinutes();
            // Allow scheduling recently dued active tasks
            if (beanMethodInstantTask.getInstant().compareTo(now) > 0 || timeElapsed < GRACE_TIME) {
              cronTaskRegistrar.addTask(getBeanMethodTaskId(task.getId()), runnableBeanMethod,
                  ((BeanMethodInstantTask) task).getInstant());
              scheduled = true;
            } else {
              log.info(String.format(
                  "BeanMethodTask not scheduled as it's timmer is over - bean: %s， Method: %s， Parameters: %s, Instant: %s",
                  beanMethodInstantTask.getBean(), beanMethodInstantTask.getMethod(),
                  Arrays.toString(beanMethodInstantTask.getParams().toArray()),
                  DateTimeFormatter.ISO_INSTANT.format(beanMethodInstantTask.getInstant())));
            }
          }
        } catch (RuntimeException ex) {
          log.error(String.format("RunnableBeanMethod creation exception - bean: %s， Method: %s， Parameters: %s ",
              task.getBean(), task.getMethod(), task.getParams().toArray()), ex);
        }
      }
    } else {
      log.warn("Scheduler disabled.  Task {} not scheduled.", task.getId());
    }
    return scheduled;
  }

  /**
   * Un-Shedules the provided BeanMethodTask
   * 
   * @param task the BeanMethodTask to be un-scheduled
   * @return boolean true if the BeanMethodTask has been un-scheduled or false in
   *         other case
   */
  public boolean unScheduleTask(BeanMethodTask task) {
    return unScheduleTask(task.getId());
  }

  /**
   * Un-Shedules the BeanMethodTask with the provided Id.
   * 
   * @param id the identifier of the BeanMethodTask to be un-scheduled
   * @return boolean true if the BeanMethodTask has been un-scheduled or false in
   *         other case
   */
  public boolean unScheduleTask(Long id) {
    return cronTaskRegistrar.removeTask(getBeanMethodTaskId(id));
  }

  /**
   * Un-Shedules all currently scheduled tasks.
   */
  public void unScheduleAll() {
    Set<String> ids = cronTaskRegistrar.getScheduledTaskIds();
    for (String id : ids) {
      // Remove only own taks
      if (id.startsWith(TASK_PREFIX)) {
        cronTaskRegistrar.removeTask(id);
      }
    }
  }

  private String getBeanMethodTaskId(Long id) {
    return String.format("%s-%d", TASK_PREFIX, id);
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

}
