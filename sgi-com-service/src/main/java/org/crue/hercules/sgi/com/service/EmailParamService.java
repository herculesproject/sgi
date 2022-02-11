package org.crue.hercules.sgi.com.service;

import java.util.List;

import org.crue.hercules.sgi.com.model.EmailParam;
import org.crue.hercules.sgi.com.repository.EmailParamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@Validated
@Slf4j
public class EmailParamService {
  private final EmailParamRepository repository;

  public EmailParamService(EmailParamRepository repository) {
    log.debug(
        "EmailParamService(EmailParamRepository repository) - start");
    this.repository = repository;
    log.debug(
        "EmailParamService(EmailParamRepository repository) - end");
  }

  public List<EmailParam> findByEmailId(Long id) {
    return repository.findByPkEmailId(id);
  }
}
