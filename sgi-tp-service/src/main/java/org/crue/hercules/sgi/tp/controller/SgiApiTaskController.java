package org.crue.hercules.sgi.tp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.tp.dto.SgiApiCronTaskInput;
import org.crue.hercules.sgi.tp.dto.SgiApiCronTaskOutput;
import org.crue.hercules.sgi.tp.dto.SgiApiInstantTaskInput;
import org.crue.hercules.sgi.tp.dto.SgiApiInstantTaskOutput;
import org.crue.hercules.sgi.tp.enums.ServiceType;
import org.crue.hercules.sgi.tp.model.BeanMethodCronTask;
import org.crue.hercules.sgi.tp.model.BeanMethodInstantTask;
import org.crue.hercules.sgi.tp.model.BeanMethodTask;
import org.crue.hercules.sgi.tp.service.BeanMethodTaskService;
import org.crue.hercules.sgi.tp.tasks.SgiApiCallerTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ConfigController
 */
@RestController
@RequestMapping(SgiApiTaskController.MAPPING)
@Slf4j
public class SgiApiTaskController {
  public static final String MAPPING = "/sgiapitasks";

  private BeanMethodTaskService beanMethodTaskService;

  public SgiApiTaskController(BeanMethodTaskService beanMethodTaskService) {
    this.beanMethodTaskService = beanMethodTaskService;
  }

  /**
   * Creates a new SGI API cron task.
   * 
   * @param cronTask the {@link SgiApiCronTaskInput} to be created
   * @return the newly created {@link SgiApiCronTaskOutput}
   */
  @PostMapping("/cron")
  ResponseEntity<SgiApiCronTaskOutput> createSgiApiCronTask(@Valid @RequestBody SgiApiCronTaskInput cronTask) {
    log.debug("createSgiApiCronTask(SgiApiCronTaskInput cronTask) - start");
    BeanMethodCronTask returnValue = beanMethodTaskService.create(convert(cronTask));
    log.debug("createSgiApiCronTask(SgiApiCronTaskInput cronTask) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Creates a new SGI API instant task.
   * 
   * @param cronTask the {@link SgiApiInstanTaskInput} to be created
   * @return the newly created {@link SgiApiInstantTaskOutput}
   */
  @PostMapping("/instant")
  ResponseEntity<SgiApiInstantTaskOutput> createSgiApiInstantTask(
      @Valid @RequestBody SgiApiInstantTaskInput instantTask) {
    log.debug("createSgiApiInstantTask(SgiApiInstantTaskInput instantTask) - start");
    BeanMethodInstantTask returnValue = beanMethodTaskService.create(convert(instantTask));
    log.debug("createSgiApiInstantTask(SgiApiInstantTaskInput instantTask) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Updates an existing SGI API cron task.
   * 
   * @param cronTask the new SGI API cron task data
   * @param id       identifier of the SGI API cron task to be updated
   * @return the {@link SgiApiCronTaskOutput} with the updated data
   */
  @PutMapping("/cron/{id}")
  SgiApiCronTaskOutput updateSgiApiCronTask(@PathVariable Long id, @Valid @RequestBody SgiApiCronTaskInput cronTask) {
    log.debug("updateSgiApiCronTask(Long id, SgiApiCronTaskInput cronTask) - start");
    BeanMethodCronTask returnValue = beanMethodTaskService.update(convert(id, cronTask));
    log.debug("updateSgiApiCronTask(Long id, SgiApiCronTaskInput cronTask) - end");
    return convert(returnValue);
  }

  /**
   * Updates an existing SGI API instant task.
   * 
   * @param instantTask the new SGI API instant task data
   * @param id          identifier of the SGI API instant task to be updated
   * @return the {@link SgiApiInstantTaskOutput} with the updated data
   */
  @PutMapping("/instant/{id}")
  SgiApiInstantTaskOutput updateSgiApiInstantTask(@PathVariable Long id,
      @Valid @RequestBody SgiApiInstantTaskInput instantTask) {
    log.debug("updateSgiApiInstantTask(Long id, SgiApiInstantTaskInput instantTask) - start");
    BeanMethodInstantTask returnValue = beanMethodTaskService.update(convert(id, instantTask));
    log.debug("updateSgiApiInstantTask(Long id, SgiApiInstantTaskInput instantTask) - end");
    return convert(returnValue);
  }

  @GetMapping("/{id}")
  Object get(@PathVariable Long id) {
    log.debug("get(Long id) - start");
    BeanMethodTask value = beanMethodTaskService.get(id);
    if ("sgiApiCallerTask".equals(value.getBean())) {
      Object returnValue = convert(value);
      log.debug("get(Long id) - end");
      return returnValue;
    } else {
      throw new NotFoundException(ProblemMessage.builder().key(NotFoundException.class)
          .parameter("entity", ApplicationContextSupport.getMessage(SgiApiCallerTask.class)).parameter("id", id)
          .build());
    }
  }

  /**
   * Returns a paged and filtered list of enabled {@link SgiApiCronTaskOutput} and
   * {@link SgiApiInstantTaskOutput}.
   * 
   * @param query  search filter
   * @param paging page information
   */
  @GetMapping
  ResponseEntity<Page<Object>> findEnabledSgiApiTasks(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findEnabledSgiApiCrontTasks(String query, Pageable paging) - start");
    Page<BeanMethodTask> page = beanMethodTaskService.findEnabled(completeQuery(query), paging);

    if (page.isEmpty()) {
      log.debug("findEnabledSgiApiCrontTasks(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findEnabledSgiApiCrontTasks(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Returns a paged and filtered list of enabled {@link SgiApiCronTaskOutput}.
   * 
   * @param query  search filter
   * @param paging page information
   */
  @GetMapping("/cron")
  ResponseEntity<Page<SgiApiCronTaskOutput>> findEnabledSgiApiCronTasks(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findEnabledSgiApiCrontTasks(String query, Pageable paging) - start");
    Page<BeanMethodCronTask> page = beanMethodTaskService.findEnabledCronTasks(completeQuery(query), paging);

    if (page.isEmpty()) {
      log.debug("findEnabledSgiApiCrontTasks(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findEnabledSgiApiCrontTasks(String query, Pageable paging) - end");
    return new ResponseEntity<>(convertCron(page), HttpStatus.OK);
  }

  /**
   * Returns a paged and filtered list of enabled {@link SgiApiInstantTaskOutput}.
   * 
   * @param query  search filter
   * @param paging page information
   */
  @GetMapping("/instant")
  ResponseEntity<Page<SgiApiInstantTaskOutput>> findEnabledSgiApiInstantTasks(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findEnabledSgiApiInstantTasks(String query, Pageable paging) - start");
    Page<BeanMethodInstantTask> page = beanMethodTaskService.findEnabledInstantTasks(completeQuery(query), paging);

    if (page.isEmpty()) {
      log.debug("findEnabledSgiApiInstantTasks(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findEnabledSgiApiInstantTasks(String query, Pageable paging) - end");
    return new ResponseEntity<>(convertInstant(page), HttpStatus.OK);
  }

  private BeanMethodCronTask convert(Long id, SgiApiCronTaskInput task) {
    BeanMethodCronTask returnValue = convert(task);
    returnValue.setId(id);
    return returnValue;
  }

  private BeanMethodInstantTask convert(Long id, SgiApiInstantTaskInput task) {
    BeanMethodInstantTask returnValue = convert(task);
    returnValue.setId(id);
    return returnValue;
  }

  private BeanMethodCronTask convert(SgiApiCronTaskInput cronTask) {
    List<String> params = new ArrayList<>();
    params.add(cronTask.getServiceType().name());
    params.add(cronTask.getRelativeUrl());
    params.add(cronTask.getHttpMethod().name());
    BeanMethodCronTask task = BeanMethodCronTask.builder().bean("sgiApiCallerTask").method("call").params(params)
        .description(cronTask.getDescription()).cronExpression(cronTask.getCronExpression()).disabled(Boolean.FALSE)
        .build();
    return task;
  }

  private BeanMethodInstantTask convert(SgiApiInstantTaskInput instantTask) {
    List<String> params = new ArrayList<>();
    params.add(instantTask.getServiceType().name());
    params.add(instantTask.getRelativeUrl());
    params.add(instantTask.getHttpMethod().name());
    BeanMethodInstantTask task = BeanMethodInstantTask.builder().bean("sgiApiCallerTask").method("call").params(params)
        .description(instantTask.getDescription()).instant(instantTask.getInstant()).disabled(Boolean.FALSE).build();
    return task;
  }

  private Object convert(BeanMethodTask task) {
    if (task instanceof BeanMethodCronTask) {
      return convert((BeanMethodCronTask) task);
    }
    return convert((BeanMethodInstantTask) task);
  }

  private SgiApiCronTaskOutput convert(BeanMethodCronTask cronTask) {
    List<String> params = cronTask.getParams();
    // There are two parameters (1-task type, 2-relative path)
    String serviceType = params.size() > 0 ? params.get(0) : "";
    String relativeUrl = params.size() > 1 ? params.get(1) : "/";
    String httpMehtod = params.size() > 2 ? params.get(2) : "GET";

    return SgiApiCronTaskOutput.builder().id(cronTask.getId()).disabled(cronTask.getDisabled())
        .description(cronTask.getDescription()).serviceType(ServiceType.valueOf(serviceType)).relativeUrl(relativeUrl)
        .httpMethod(HttpMethod.valueOf(httpMehtod)).cronExpression(cronTask.getCronExpression()).build();
  }

  private SgiApiInstantTaskOutput convert(BeanMethodInstantTask instantTask) {
    List<String> params = instantTask.getParams();
    // There are two parameters (1-task type, 2-relative path)
    String serviceType = params.size() > 0 ? params.get(0) : "";
    String relativeUrl = params.size() > 1 ? params.get(1) : "/";
    String httpMehtod = params.size() > 2 ? params.get(2) : "GET";

    return SgiApiInstantTaskOutput.builder().id(instantTask.getId()).disabled(instantTask.getDisabled())
        .description(instantTask.getDescription()).serviceType(ServiceType.valueOf(serviceType))
        .relativeUrl(relativeUrl).httpMethod(HttpMethod.valueOf(httpMehtod)).instant(instantTask.getInstant()).build();
  }

  private Page<SgiApiCronTaskOutput> convertCron(Page<BeanMethodCronTask> page) {
    List<SgiApiCronTaskOutput> content = page.getContent().stream().map((cronTask) -> convert(cronTask))
        .collect(Collectors.toList());
    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private Page<SgiApiInstantTaskOutput> convertInstant(Page<BeanMethodInstantTask> page) {
    List<SgiApiInstantTaskOutput> content = page.getContent().stream().map((instantTask) -> convert(instantTask))
        .collect(Collectors.toList());
    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private Page<Object> convert(Page<BeanMethodTask> page) {
    List<Object> content = page.getContent().stream().map((task) -> convert(task)).collect(Collectors.toList());
    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private String completeQuery(String query) {
    if (query == null) {
      return "bean==sgiApiCallerTask";
    } else {
      return String.format("bean==sgiApiCallerTask;(%s)", query);
    }
  }
}