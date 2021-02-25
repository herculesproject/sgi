package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.TipoRegimenConcurrenciaNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.repository.TipoRegimenConcurrenciaRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoRegimenConcurrenciaSpecifications;
import org.crue.hercules.sgi.csp.service.TipoRegimenConcurrenciaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link TipoRegimenConcurrencia}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoRegimenConcurrenciaServiceImpl implements TipoRegimenConcurrenciaService {

  private final TipoRegimenConcurrenciaRepository repository;

  public TipoRegimenConcurrenciaServiceImpl(TipoRegimenConcurrenciaRepository repository) {
    this.repository = repository;
  }

  /**
   * Guarda la entidad {@link TipoRegimenConcurrencia}.
   * 
   * @param tipoRegimenConcurrencia la entidad {@link TipoRegimenConcurrencia} a
   *                                guardar.
   * @return TipoRegimenConcurrencia la entidad {@link TipoRegimenConcurrencia}
   *         persistida.
   */
  @Override
  @Transactional
  public TipoRegimenConcurrencia create(TipoRegimenConcurrencia tipoRegimenConcurrencia) {
    log.debug("create(TipoRegimenConcurrencia tipoRegimenConcurrencia) - start");

    Assert.isNull(tipoRegimenConcurrencia.getId(), "Id tiene que ser null para crear TipoRegimenConcurrencia");
    Assert.isTrue(!(repository.findByNombre(tipoRegimenConcurrencia.getNombre()).isPresent()),
        "Ya existe TipoRegimenConcurrencia con el nombre " + tipoRegimenConcurrencia.getNombre());

    tipoRegimenConcurrencia.setActivo(Boolean.TRUE);
    TipoRegimenConcurrencia returnValue = repository.save(tipoRegimenConcurrencia);

    log.debug("create(TipoRegimenConcurrencia tipoRegimenConcurrencia) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link TipoRegimenConcurrencia}.
   * 
   * @param tipoRegimenConcurrencia tipoRegimenConcurrenciaActualizar
   *                                {@link TipoRegimenConcurrencia} con los datos
   *                                actualizados.
   * @return {@link TipoRegimenConcurrencia} actualizado.
   */
  @Override
  @Transactional
  public TipoRegimenConcurrencia update(TipoRegimenConcurrencia tipoRegimenConcurrencia) {
    log.debug("update(TipoRegimenConcurrencia tipoRegimenConcurrencia) - start");

    Assert.notNull(tipoRegimenConcurrencia.getId(), "Id no puede ser null para actualizar TipoRegimenConcurrencia");
    repository.findByNombre(tipoRegimenConcurrencia.getNombre()).ifPresent((tipoRegimenConcurrenciaExistente) -> {
      Assert.isTrue(tipoRegimenConcurrencia.getId() == tipoRegimenConcurrenciaExistente.getId(),
          "Ya existe un TipoRegimenConcurrencia con el nombre " + tipoRegimenConcurrenciaExistente.getNombre());
    });

    return repository.findById(tipoRegimenConcurrencia.getId()).map((data) -> {
      data.setNombre(tipoRegimenConcurrencia.getNombre());
      data.setActivo(tipoRegimenConcurrencia.getActivo());

      TipoRegimenConcurrencia returnValue = repository.save(data);
      log.debug("update(TipoRegimenConcurrencia tipoRegimenConcurrencia) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoRegimenConcurrenciaNotFoundException(tipoRegimenConcurrencia.getId()));
  }

  /**
   * Desactiva el {@link TipoRegimenConcurrencia}.
   *
   * @param id Id del {@link TipoRegimenConcurrencia}.
   * @return la entidad {@link TipoRegimenConcurrencia} persistida.
   */
  @Override
  @Transactional
  public TipoRegimenConcurrencia disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "TipoRegimenConcurrencia id no puede ser null para desactivar un TipoRegimenConcurrencia");

    return repository.findById(id).map(tipoRegimenConcurrencia -> {
      tipoRegimenConcurrencia.setActivo(false);

      TipoRegimenConcurrencia returnValue = repository.save(tipoRegimenConcurrencia);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoRegimenConcurrenciaNotFoundException(id));
  }

  /**
   * Obtiene todas las entidades {@link TipoRegimenConcurrencia} activos paginadas
   * y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link TipoRegimenConcurrencia} paginadas y
   *         filtradas.
   */
  @Override
  public Page<TipoRegimenConcurrencia> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Specification<TipoRegimenConcurrencia> specs = TipoRegimenConcurrenciaSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoRegimenConcurrencia> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link TipoRegimenConcurrencia} paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link TipoRegimenConcurrencia} paginadas y
   *         filtradas.
   */
  @Override
  public Page<TipoRegimenConcurrencia> findAllTodos(String query, Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Specification<TipoRegimenConcurrencia> specs = SgiRSQLJPASupport.toSpecification(query);
    Page<TipoRegimenConcurrencia> returnValue = repository.findAll(specs, paging);
    log.debug("findAllTodos(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link TipoRegimenConcurrencia} por id.
   * 
   * @param id Identificador de la entidad {@link TipoRegimenConcurrencia}.
   * @return TipoRegimenConcurrencia la entidad {@link TipoRegimenConcurrencia}.
   */
  @Override
  public TipoRegimenConcurrencia findById(Long id) {
    log.debug("findById(Long id) - start");
    final TipoRegimenConcurrencia returnValue = repository.findById(id)
        .orElseThrow(() -> new TipoRegimenConcurrenciaNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

}
