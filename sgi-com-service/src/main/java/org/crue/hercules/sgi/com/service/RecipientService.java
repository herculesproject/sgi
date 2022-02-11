package org.crue.hercules.sgi.com.service;

import java.util.List;

import org.crue.hercules.sgi.com.model.Recipient;
import org.crue.hercules.sgi.com.repository.RecipientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@Validated
@Slf4j
public class RecipientService {
  private final RecipientRepository repository;

  public RecipientService(RecipientRepository repository) {
    log.debug(
        "RecipientService(RecipientRepository repository) - start");
    this.repository = repository;
    log.debug(
        "RecipientService(RecipientRepository repository) - end");
  }

  public List<Recipient> findByEmailId(Long id) {
    return repository.findByEmailId(id);
  }
}
