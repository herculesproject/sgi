package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.ProyectoProyectoSgeNotFoundException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.repository.ProyectoProyectoSgeRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoProyectoSgeSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoProyectoSgeService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link ProyectoProyectoSge}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoProyectoSgeServiceImpl implements ProyectoProyectoSgeService {

  private final ProyectoProyectoSgeRepository repository;

  public ProyectoProyectoSgeServiceImpl(ProyectoProyectoSgeRepository proyectoProrrogaRepository) {
    this.repository = proyectoProrrogaRepository;
  }

  /**
   * Guarda la entidad {@link ProyectoProyectoSge}.
   * 
   * @param proyectoProyectoSge la entidad {@link ProyectoProyectoSge} a guardar.
   * @return la entidad {@link ProyectoProyectoSge} persistida.
   */
  @Override
  @Transactional
  public ProyectoProyectoSge create(ProyectoProyectoSge proyectoProyectoSge) {
    log.debug("create(ProyectoProyectoSge proyectoProyectoSge) - start");
    Assert.isNull(proyectoProyectoSge.getId(),
        "ProyectoProyectoSge id tiene que ser null para crear un nuevo ProyectoProyectoSge");
    Assert.notNull(proyectoProyectoSge.getProyectoId(), "Id Proyecto no puede ser null para crear ProyectoProyectoSge");
    Assert.notNull(proyectoProyectoSge.getProyectoSgeRef(),
        "Ref ProyectoSge no puede ser null para crear ProyectoProyectoSge");
    ProyectoProyectoSge returnValue = repository.save(proyectoProyectoSge);
    log.debug("create(ProyectoProyectoSge proyectoProyectoSge) - end");
    return returnValue;
  }

  /**
   * Elimina el {@link ProyectoProyectoSge}.
   *
   * @param id Id del {@link ProyectoProyectoSge}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "ProyectoProyectoSge id no puede ser null para desactivar un ProyectoProyectoSge");

    if (!repository.existsById(id)) {
      throw new ProyectoProyectoSgeNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Comprueba la existencia del {@link ProyectoProyectoSge} por id.
   *
   * @param id el id de la entidad {@link ProyectoProyectoSge}.
   * @return true si existe y false en caso contrario.
   */
  @Override
  public boolean existsById(final Long id) {
    log.debug("existsById(final Long id)  - start", id);
    final boolean existe = repository.existsById(id);
    log.debug("existsById(final Long id)  - end", id);
    return existe;
  }

  /**
   * Obtiene {@link ProyectoProyectoSge} por su id.
   *
   * @param id el id de la entidad {@link ProyectoProyectoSge}.
   * @return la entidad {@link ProyectoProyectoSge}.
   */
  @Override
  public ProyectoProyectoSge findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ProyectoProyectoSge returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoProyectoSgeNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;

  }

  /**
   * Obtiene los {@link ProyectoProyectoSge} para un {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoProyectoSge} del
   *         {@link Proyecto} paginadas.
   */
  @Override
  public Page<ProyectoProyectoSge> findAllByProyecto(Long proyectoId, String query, Pageable pageable) {
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - start");
    Specification<ProyectoProyectoSge> specs = ProyectoProyectoSgeSpecifications.byProyectoId(proyectoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoProyectoSge> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;
  }

}
