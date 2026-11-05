package org.crue.hercules.sgi.csp.service;

import java.util.List;
import java.util.Objects;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudHito;
import org.crue.hercules.sgi.csp.model.SolicitudHitoAviso;
import org.crue.hercules.sgi.csp.repository.SolicitudHitoRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudHitoSpecifications;
import org.crue.hercules.sgi.csp.util.AvisoTaskHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para la gestión conjunta de los avisos ({@link SolicitudHitoAviso})
 * de una {@link Solicitud}.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SolicitudAvisoService {

  private final SolicitudHitoRepository solicitudHitoRepository;
  private final AvisoTaskHelper avisoTaskHelper;

  /**
   * Activa las tareas programadas de los avisos pendientes de envío de los
   * {@link SolicitudHito} de la {@link Solicitud}.
   *
   * @param solicitudId identificador de la {@link Solicitud}
   */
  public void enablePendingAvisos(Long solicitudId) {
    log.debug("enablePendingAvisos - solicitudId: {}", solicitudId);
    avisoTaskHelper.enablePendingTasks(findTareaProgramadaRefs(solicitudId));
  }

  /**
   * Desactiva las tareas programadas de los avisos pendientes de envío de los
   * {@link SolicitudHito} de la {@link Solicitud}, de forma que no se envían
   * mientras sigan desactivadas.
   *
   * @param solicitudId identificador de la {@link Solicitud}
   */
  public void disablePendingAvisos(Long solicitudId) {
    log.debug("disablePendingAvisos - solicitudId: {}", solicitudId);
    avisoTaskHelper.disablePendingTasks(findTareaProgramadaRefs(solicitudId));
  }

  /**
   * Obtiene las referencias de las tareas programadas de los avisos de los
   * {@link SolicitudHito} de la {@link Solicitud}.
   *
   * @param solicitudId identificador de la {@link Solicitud}
   * @return referencias de las tareas programadas
   */
  private List<String> findTareaProgramadaRefs(Long solicitudId) {
    return solicitudHitoRepository
        .findAll(SolicitudHitoSpecifications.bySolicitudId(solicitudId)).stream()
        .map(SolicitudHito::getSolicitudHitoAviso)
        .filter(Objects::nonNull)
        .map(SolicitudHitoAviso::getTareaProgramadaRef)
        .toList();
  }
}
