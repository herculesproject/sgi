package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.exceptions.AutorizacionNotFoundException;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.repository.AutorizacionRepository;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Autorizacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class AutorizacionService {
  private final AutorizacionRepository repository;

  public AutorizacionService(AutorizacionRepository repository) {
    this.repository = repository;

  }

  public Page<Autorizacion> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Specification<Autorizacion> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<Autorizacion> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Elimina la {@link Autorizacion}.
   *
   * @param id Id del {@link Autorizacion}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Autorizacion.class)).build());

    if (!repository.existsById(id)) {
      throw new AutorizacionNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }
}
