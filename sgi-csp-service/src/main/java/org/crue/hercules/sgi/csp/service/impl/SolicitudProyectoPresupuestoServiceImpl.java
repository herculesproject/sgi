package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoPresupuestoNotFoundException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoPresupuestoRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoPresupuestoSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPresupuestoService;
import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link SolicitudProyectoPresupuesto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudProyectoPresupuestoServiceImpl implements SolicitudProyectoPresupuestoService {

  private final SolicitudProyectoPresupuestoRepository repository;

  public SolicitudProyectoPresupuestoServiceImpl(SolicitudProyectoPresupuestoRepository repository) {
    this.repository = repository;
  }

  /**
   * Guarda la entidad {@link SolicitudProyectoPresupuesto}.
   * 
   * @param solicitudProyectoPresupuesto la entidad
   *                                     {@link SolicitudProyectoPresupuesto} a
   *                                     guardar.
   * @return SolicitudProyectoPresupuesto la entidad
   *         {@link SolicitudProyectoPresupuesto} persistida.
   */
  @Override
  @Transactional
  public SolicitudProyectoPresupuesto create(SolicitudProyectoPresupuesto solicitudProyectoPresupuesto) {
    log.debug("create(SolicitudProyectoPresupuesto solicitudProyectoPresupuesto) - start");

    Assert.isNull(solicitudProyectoPresupuesto.getId(),
        "Id tiene que ser null para crear la SolicitudProyectoPresupuesto");

    SolicitudProyectoPresupuesto returnValue = repository.save(solicitudProyectoPresupuesto);

    log.debug("create(SolicitudProyectoPresupuesto solicitudProyectoPresupuesto) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link SolicitudProyectoPresupuesto}.
   * 
   * @param solicitudProyectoPresupuesto solicitudProyectoPresupuesto
   *                                     {@link SolicitudProyectoPresupuesto} con
   *                                     los datos actualizados.
   * @return {@link SolicitudProyectoPresupuesto} actualizado.
   */
  @Override
  @Transactional
  public SolicitudProyectoPresupuesto update(SolicitudProyectoPresupuesto solicitudProyectoPresupuesto) {
    log.debug("update(SolicitudProyectoPresupuesto solicitudProyectoPresupuesto) - start");

    Assert.notNull(solicitudProyectoPresupuesto.getId(),
        "Id no puede ser null para actualizar SolicitudProyectoPresupuesto");

    return repository.findById(solicitudProyectoPresupuesto.getId()).map((solicitudProyectoPresupuestoExistente) -> {

      solicitudProyectoPresupuestoExistente.setAnualidad(solicitudProyectoPresupuesto.getAnualidad());
      solicitudProyectoPresupuestoExistente.setImporteSolicitado(solicitudProyectoPresupuesto.getImporteSolicitado());
      solicitudProyectoPresupuestoExistente.setObservaciones(solicitudProyectoPresupuesto.getObservaciones());

      SolicitudProyectoPresupuesto returnValue = repository.save(solicitudProyectoPresupuestoExistente);

      log.debug("update(SolicitudProyectoPresupuesto solicitudProyectoPresupuesto) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudProyectoPresupuestoNotFoundException(solicitudProyectoPresupuesto.getId()));
  }

  /**
   * Comprueba la existencia del {@link SolicitudProyectoPresupuesto} por id.
   *
   * @param id el id de la entidad {@link SolicitudProyectoPresupuesto}.
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
   * Obtiene una entidad {@link SolicitudProyectoPresupuesto} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyectoPresupuesto}.
   * @return SolicitudProyectoPresupuesto la entidad
   *         {@link SolicitudProyectoPresupuesto}.
   */
  @Override
  public SolicitudProyectoPresupuesto findById(Long id) {
    log.debug("findById(Long id) - start");
    final SolicitudProyectoPresupuesto returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoPresupuestoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina la {@link SolicitudProyectoPresupuesto}.
   *
   * @param id Id del {@link SolicitudProyectoPresupuesto}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        "SolicitudProyectoPresupuesto id no puede ser null para eliminar un SolicitudProyectoPresupuesto");
    if (!repository.existsById(id)) {
      throw new SolicitudProyectoPresupuestoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene las {@link SolicitudProyectoPresupuesto} para un {@link Solicitud}.
   *
   * @param solicitudId el id del {@link Solicitud}.
   * @param query       la información del filtro.
   * @param paging      la información de la paginación.
   * @return la lista de entidades {@link SolicitudProyectoPresupuesto} del
   *         {@link Solicitud} paginadas.
   */
  @Override
  public Page<SolicitudProyectoPresupuesto> findAllBySolicitud(Long solicitudId, List<QueryCriteria> query,
      Pageable paging) {
    log.debug("findAllBySolicitudProyectoDatos(Long solicitudId, List<QueryCriteria> query, Pageable paging) - start");

    Specification<SolicitudProyectoPresupuesto> specByQuery = new QuerySpecification<SolicitudProyectoPresupuesto>(
        query);
    Specification<SolicitudProyectoPresupuesto> specBySolicitud = SolicitudProyectoPresupuestoSpecifications
        .bySolicitudId(solicitudId);
    Specification<SolicitudProyectoPresupuesto> specs = Specification.where(specByQuery).and(specBySolicitud);

    Page<SolicitudProyectoPresupuesto> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitudProyectoDatos(Long solicitudId, List<QueryCriteria> query, Pageable paging) - end");
    return returnValue;
  }

}