package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.GastoRequerimientoJustificacion;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.crue.hercules.sgi.csp.repository.GastoRequerimientoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.GastoRequerimientoJustificacionSpecifications;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion
 * {@link GastoRequerimientoJustificacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GastoRequerimientoJustificacionService {

  private final GastoRequerimientoJustificacionRepository repository;

  /**
   * Obtener todas las entidades {@link GastoRequerimientoJustificacion}
   * pertenecientes al {@link RequerimientoJustificacion} paginadas y/o filtradas.
   *
   * @param requerimientoJustificacionId el identificador de un
   *                                     {@link RequerimientoJustificacion}
   * @param pageable                     la información de la paginación.
   * @param query                        la información del filtro.
   * @return la lista de entidades {@link GastoRequerimientoJustificacion}
   *         paginadas y/o filtradas.
   */
  public Page<GastoRequerimientoJustificacion> findAllByRequerimientoJustificacionId(
      Long requerimientoJustificacionId, String query, Pageable pageable) {
    log.debug(
        "findAllByRequerimientoJustificacionId(Long requerimientoJustificacionId, String query, Pageable pageable) - start");
    Specification<GastoRequerimientoJustificacion> specs = GastoRequerimientoJustificacionSpecifications
        .byRequerimientoJustificacionId(requerimientoJustificacionId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<GastoRequerimientoJustificacion> returnValue = repository.findAll(specs, pageable);
    log.debug(
        "findAllByRequerimientoJustificacionId(Long requerimientoJustificacionId, String query, Pageable pageable) - end");
    return returnValue;
  }
}
