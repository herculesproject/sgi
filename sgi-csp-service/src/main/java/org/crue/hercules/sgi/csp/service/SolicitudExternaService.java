package org.crue.hercules.sgi.csp.service;

import java.util.Optional;
import java.util.UUID;

import org.crue.hercules.sgi.csp.exceptions.SolicitudExternaIncorrectAccessException;
import org.crue.hercules.sgi.csp.model.SolicitanteExterno;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudExterna;
import org.crue.hercules.sgi.csp.repository.SolicitudExternaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para la gestión de {@link SolicitudExterna}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class SolicitudExternaService {

  private final SolicitudExternaRepository repository;

  /**
   * Obtiene el uuid de la {@link SolicitudExterna}.
   * 
   * @param codigoRegistroInterno codigo de la {@link Solicitud}.
   * @param numeroDocumento       numero documento del {@link SolicitanteExterno}.
   * @return el uuid.
   */
  public UUID findIdByCodigoRegistroInternoAndNumeroDocumento(String codigoRegistroInterno, String numeroDocumento) {
    log.debug(
        "findIdByCodigoRegistroInternoAndNumeroDocumento(String codigoRegistroInterno, String numeroDocumento) - start");

    final UUID returnValue = repository.findPublicId(codigoRegistroInterno,
        numeroDocumento)
        .orElseThrow(() -> new SolicitudExternaIncorrectAccessException());

    log.debug(
        "findIdByCodigoRegistroInternoAndNumeroDocumento(String codigoRegistroInterno, String numeroDocumento) - end");
    return returnValue;
  }

  /**
   * Obtiene el id de la {@link Solicitud}.
   *
   * @param uuid el id de la {@link SolicitudExterna}.
   * @return el {@link SolicitanteExterno} para una {@link Solicitud}.
   */
  public Long findSolicitudId(String uuid) {
    log.debug("findSolicitudId(String uuid) - start");
    Optional<Long> returnValue = repository.findSolicitudId(UUID.fromString(uuid));
    log.debug("findSolicitudId(String uuid) - end");
    return (returnValue.isPresent()) ? returnValue.get() : null;
  }

}
