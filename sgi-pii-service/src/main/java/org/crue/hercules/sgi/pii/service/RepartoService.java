package org.crue.hercules.sgi.pii.service;

import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.pii.exceptions.RepartoNotFoundException;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionGasto;
import org.crue.hercules.sgi.pii.model.Reparto;
import org.crue.hercules.sgi.pii.repository.RepartoRepository;
import org.crue.hercules.sgi.pii.repository.specification.RepartoSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Negocio de la entidad Reparto.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RepartoService {

  private final RepartoRepository repository;

  public RepartoService(RepartoRepository repartoRepository) {
    this.repository = repartoRepository;
  }

  /**
   * Obtiene los {@link Reparto} para una {@link Invencion}paginadas y/o
   * filtradas.
   * 
   * @param invencionId el id de la {@link Invencion}.
   * @param query       la información del filtro.
   * @param pageable    la información de la paginación.
   * @return la lista de {@link Reparto} de la {@link Invencion}paginadas y/o
   *         filtradas.
   */
  public Page<Reparto> findByInvencionId(Long invencionId, String query, Pageable pageable) {
    log.debug("findByInvencionId(Long invencionId, String query, Pageable pageable) - start");

    Specification<Reparto> specs = RepartoSpecifications.byInvencionId(invencionId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<Reparto> returnValue = repository.findAll(specs, pageable);
    log.debug("findByInvencionId(Long invencionId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link Reparto} por su id.
   *
   * @param id el id de la entidad {@link Reparto}.
   * @return la entidad {@link Reparto}.
   */
  public Reparto findById(Long id) {
    log.debug("findById(Long id)  - start");
    final Reparto returnValue = repository.findById(id).orElseThrow(() -> new RepartoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }
}
