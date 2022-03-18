package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.converter.SolicitudGrupoConverter;
import org.crue.hercules.sgi.csp.dto.SolicitudGrupoInput;
import org.crue.hercules.sgi.csp.model.SolicitudGrupo;
import org.crue.hercules.sgi.csp.repository.SolicitudGrupoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link SolicitudGrupo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class SolicitudGrupoService {

  /** SolcitudGrupo repository */
  private final SolicitudGrupoRepository repository;

  /** Solicitud Grupo converter */
  private final SolicitudGrupoConverter converter;

  public SolicitudGrupoService(
      SolicitudGrupoRepository repository,
      SolicitudGrupoConverter converter) {
    this.repository = repository;
    this.converter = converter;
  }

  /**
   * Guarda la entidad {@link SolicitudGrupo}.
   * 
   * @param solicitudGrupo la entidad {@link SolicitudGrupoInput} a guardar.
   * @return la entidad {@link SolicitudGrupo} persistida.
   */
  @Transactional
  public SolicitudGrupo create(SolicitudGrupoInput solicitudGrupo) {
    log.debug("create(SolicitudGrupo solicitudGrupo) - start");

    SolicitudGrupo returnValue = repository.save(converter.convert(solicitudGrupo));

    log.debug("create(SolicitudGrupo solicitudGrupo) - end");
    return returnValue;
  }

  public SolicitudGrupo findBySolicitudId(Long solicitudId) {
    log.debug("findBySolicitudId(Long solicitudId) - start");
    SolicitudGrupo returnValue = repository.findBySolicitudId(solicitudId);
    log.debug("findBySolicitudId(Long solicitudId) - end");
    return returnValue;
  }
}
