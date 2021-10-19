package org.crue.hercules.sgi.tp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.tp.model.BeanMethodInstantTask;
import org.crue.hercules.sgi.tp.model.BeanMethodTask;
import org.crue.hercules.sgi.tp.repository.BeanMethodTaskRepository;
import org.crue.hercules.sgi.tp.repository.specification.BeanMehtodTaskSpecifications;
import org.crue.hercules.sgi.tp.scheduling.BeanMethodTaskScheduler;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for {@link BeanMethodTask}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class BeanMethodTaskService {

  public static final String TASK_CACHE_KEY = "task";
  public static final String TASKS_CACHE_KEY = "tasks";
  public static final String ENABLED_TASKS_CACHE_KEY = "enabled-tasks";
  public static final String ENABLED_FUTURE_TASKS_CACHE_KEY = "enabled-future-tasks";

  private final BeanMethodTaskRepository repository;
  private final BeanMethodTaskScheduler scheduler;

  public BeanMethodTaskService(BeanMethodTaskRepository repository, BeanMethodTaskScheduler scheduler) {
    this.repository = repository;
    this.scheduler = scheduler;
  }

  /**
   * Creates and schedules a new {@link BeanMethodTask}
   *
   * @param beanMethodTask the {@link BeanMethodTask} to create
   * @return the created {@link BeanMethodTask}
   */
  @Cacheable(value = TASK_CACHE_KEY, key = "#result.id")
  @Caching(evict = { @CacheEvict(value = TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = ENABLED_TASKS_CACHE_KEY, allEntries = true, condition = "#disabled==false"),
      @CacheEvict(value = ENABLED_FUTURE_TASKS_CACHE_KEY, allEntries = true, condition = "#disabled==false") })
  @Transactional
  public BeanMethodTask create(BeanMethodTask beanMethodTask) {
    log.debug("create(BeanMethodTask beanMethodTask) - start");
    Assert.isNull(beanMethodTask.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(BeanMethodTask.class)).build());

    BeanMethodTask returnValue = repository.save(beanMethodTask);
    scheduler.scheduleTask(returnValue);
    log.debug("create(BeanMethodTask beanMethodTask) - end");
    return returnValue;
  }

  /**
   * Updates and re-schedules a new {@link BeanMethodTask}
   *
   * @param beanMethodTask the {@link BeanMethodTask} to modify
   * @return the modified {@link BeanMethodTask}
   */
  @CachePut(value = TASK_CACHE_KEY, key = "#beanMethodTask.id")
  @Caching(evict = { @CacheEvict(value = TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = ENABLED_TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = ENABLED_FUTURE_TASKS_CACHE_KEY, allEntries = true) })
  @Transactional
  public BeanMethodTask update(BeanMethodTask beanMethodTask) {
    log.debug("update(BeanMethodTask beanMethodTask) - start");
    Assert.notNull(beanMethodTask.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(BeanMethodTask.class)).build());

    BeanMethodTask returnValue = repository.save(beanMethodTask);
    scheduler.scheduleTask(returnValue);
    log.debug("update(BeanMethodTask beanMethodTask) - end");
    return returnValue;
  }

  /**
   * Deletes and un-schedules a {@link BeanMethodTask}
   *
   * @param id the identifier of the {@link BeanMethodTask} to delete
   */
  @Caching(evict = { @CacheEvict(value = TASK_CACHE_KEY, key = "#id"),
      @CacheEvict(value = TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = ENABLED_TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = ENABLED_FUTURE_TASKS_CACHE_KEY, allEntries = true) })
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(BeanMethodTask.class)).build());
    repository.deleteById(id);
    scheduler.unScheduleTask(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Get a {@link BeanMethodTask}
   *
   * @param id the identifier of the {@link BeanMethodTask} to get
   * @return the {@link BeanMethodTask} with the provided id
   */
  @Cacheable(value = TASK_CACHE_KEY, key = "#id")
  public BeanMethodTask get(Long id) {
    log.debug("get(Long id) - start");
    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(BeanMethodTask.class)).build());
    repository.deleteById(id);
    BeanMethodTask returnValue = repository.findById(id)
        .orElseThrow(() -> new NotFoundException(ProblemMessage.builder().key(NotFoundException.class)
            .parameter("entity", ApplicationContextSupport.getMessage(BeanMethodTask.class)).parameter("id", id)
            .build()));
    log.debug("get(Long id) - end");
    return returnValue;
  }

  /**
   * Find {@link BeanMethodTask}
   *
   * @param pageable pagging info
   * @param query    RSQL expression with the restrictions to apply in the search
   * @return {@link BeanMethodTask} pagged and filtered
   */
  @Cacheable(TASKS_CACHE_KEY)
  public Page<BeanMethodTask> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<BeanMethodTask> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<BeanMethodTask> tasks = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return tasks;
  }

  /**
   * Find the enabled {@link BeanMethodTask}
   *
   * @param pageable pagging info
   * @param query    RSQL expression with the restrictions to apply in the search
   * @return {@link BeanMethodTask} pagged and filtered
   */
  @Cacheable(ENABLED_TASKS_CACHE_KEY)
  public Page<BeanMethodTask> findEnabled(String query, Pageable pageable) {
    log.debug("findEnabled(String query, Pageable pageable) - start");
    Specification<BeanMethodTask> enabled = BeanMehtodTaskSpecifications.enabled();
    Specification<BeanMethodTask> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<BeanMethodTask> tasks = repository.findAll(enabled.and(specs), pageable);
    log.debug("findEnabled(String query, Pageable pageable) - end");
    return tasks;
  }

  /**
   * Find the enabled {@link BeanMethodTask} including only the
   * {@link BeanMethodInstantTask} whose instant is after to current date-time.
   *
   * @param pageable pagging info
   * @param query    RSQL expression with the restrictions to apply in the search
   * @return {@link BeanMethodTask} pagged and filtered
   */
  @Cacheable(ENABLED_FUTURE_TASKS_CACHE_KEY)
  public Page<BeanMethodTask> findEnabledButOnlyFuture(String query, Pageable pageable) {
    log.debug("findEnabledButOnlyFuture(String query, Pageable pageable) - start");
    Specification<BeanMethodTask> future = BeanMehtodTaskSpecifications.includeBeanMethodInstantTaskIfFuture();
    Specification<BeanMethodTask> enabled = BeanMehtodTaskSpecifications.enabled();
    Specification<BeanMethodTask> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<BeanMethodTask> tasks = repository.findAll(enabled.and(future).and(specs), pageable);
    log.debug("findEnabledButOnlyFuture(String query, Pageable pageable) - end");
    return tasks;
  }

  /**
   * Disables all {@link BeanMethodInstantTask} whose instant is before to current
   * date-time.
   */
  @Caching(evict = { @CacheEvict(value = TASK_CACHE_KEY, allEntries = true),
      @CacheEvict(value = TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = ENABLED_TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = ENABLED_FUTURE_TASKS_CACHE_KEY, allEntries = true) })
  @Transactional
  public void disablePast() {
    log.debug("disablePast() - start");
    log.info("Disabling Past BeanMethodTasks...");
    List<Long> ids = new ArrayList<>();
    List<BeanMethodTask> pastInstantTasks = repository.findAll(BeanMehtodTaskSpecifications.pastInstantTasks());
    for (BeanMethodTask task : pastInstantTasks) {
      task.setDisabled(Boolean.TRUE);
      update(task);
      ids.add(task.getId());
    }
    log.info("Disabled {} BeanMethodTasks. Ids: {}", ids.size(), Arrays.toString(ids.toArray()));
    log.debug("disablePast() - end");
  }

}