package org.crue.hercules.sgi.com.service;

import java.util.List;

import org.crue.hercules.sgi.com.model.Param;
import org.crue.hercules.sgi.com.repository.ParamRepository;
import org.crue.hercules.sgi.com.repository.specification.ParamSpecifications;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@Validated
@Slf4j
public class ParamService {
  private final ParamRepository repository;

  public ParamService(ParamRepository repository) {
    log.debug(
        "EmailService(ParamRepository repository) - start");
    this.repository = repository;
    log.debug(
        "EmailService(ParamRepository repository) - end");
  }

  public List<Param> findSubjectTplParamByEmailTplName(String name) {
    return repository.findAll(ParamSpecifications.subjectTplParamByEmailTplName(name));
  }

  public List<Param> findSubjectTplParamByEmailTplName(String name, String query) {
    Specification<Param> specs = ParamSpecifications.subjectTplParamByEmailTplName(name).and(SgiRSQLJPASupport.toSpecification(query));
    return repository.findAll(specs);
  }

  public Page<Param> findSubjectTplParamByEmailTplName(String name, String query, Pageable pageable) {
    Specification<Param> specs = ParamSpecifications.subjectTplParamByEmailTplName(name).and(SgiRSQLJPASupport.toSpecification(query));
    return repository.findAll(specs, pageable);
  }

  public List<Param> findContentTplParamByEmailTplName(String name) {
    return repository.findAll(ParamSpecifications.contentTplParamByEmailTplName(name));
  }

  public List<Param> findContentTplParamByEmailTplName(String name, String query) {
    Specification<Param> specs = ParamSpecifications.contentTplParamByEmailTplName(name).and(SgiRSQLJPASupport.toSpecification(query));
    return repository.findAll(specs);
  }

  public Page<Param> findContentTplParamByEmailTplName(String name, String query, Pageable pageable) {
    Specification<Param> specs = ParamSpecifications.contentTplParamByEmailTplName(name).and(SgiRSQLJPASupport.toSpecification(query));
    return repository.findAll(specs, pageable);
  }

  public List<Param> findByEmailTplName(String name) {
    return repository.findAll(ParamSpecifications.byEmailTplName(name));
  }

  public List<Param> findByEmailTplName(String name, String query) {
    Specification<Param> specs = ParamSpecifications.byEmailTplName(name).and(SgiRSQLJPASupport.toSpecification(query));
    return repository.findAll(specs);
  }

  public Page<Param> findByEmailTplName(String name, String query, Pageable pageable) {
    Specification<Param> specs = ParamSpecifications.byEmailTplName(name).and(SgiRSQLJPASupport.toSpecification(query));
    return repository.findAll(specs, pageable);
  }
}
