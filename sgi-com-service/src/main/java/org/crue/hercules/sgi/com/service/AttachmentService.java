package org.crue.hercules.sgi.com.service;

import java.util.List;

import org.crue.hercules.sgi.com.model.Attachment;
import org.crue.hercules.sgi.com.repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@Validated
@Slf4j
public class AttachmentService {
  private final AttachmentRepository repository;

  public AttachmentService(AttachmentRepository repository) {
    log.debug(
        "AttachmentService(AttachmentRepository repository) - start");
    this.repository = repository;
    log.debug(
        "AttachmentService(AttachmentRepository repository) - end");
  }

  public List<Attachment> findByEmailId(Long id) {
    return repository.findByEmailId(id);
  }
}
