package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.exceptions.EstadoAutorizacionNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion;
import org.crue.hercules.sgi.csp.repository.EstadoAutorizacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link EstadoAutorizacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class EstadoAutorizacionService {
  private final EstadoAutorizacionRepository repository;

  public EstadoAutorizacionService(EstadoAutorizacionRepository repository) {
    this.repository = repository;
  }

  public EstadoAutorizacion findById(Long id) {
    log.debug("findById(Long id)  - start");
    final EstadoAutorizacion returnValue = repository.findById(id)
        .orElseThrow(() -> new EstadoAutorizacionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

}
