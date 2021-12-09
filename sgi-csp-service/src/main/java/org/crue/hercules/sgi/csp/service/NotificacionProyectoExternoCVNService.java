package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de
 * {@link NotificacionProyectoExternoCVN}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class NotificacionProyectoExternoCVNService {

  public NotificacionProyectoExternoCVNService() {

  }
}
