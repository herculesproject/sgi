package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.exceptions.TipoEnlaceNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.repository.TipoEnlaceRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoEnlaceSpecifications;
import org.crue.hercules.sgi.csp.service.TipoEnlaceService;
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
 * Service Implementation para gestion {@link TipoEnlace}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoEnlaceServiceImpl implements TipoEnlaceService {

  private final TipoEnlaceRepository repository;

  public TipoEnlaceServiceImpl(TipoEnlaceRepository repository) {
    this.repository = repository;
  }

  /**
   * Guarda la entidad {@link TipoEnlace}.
   * 
   * @param tipoEnlace la entidad {@link TipoEnlace} a guardar.
   * @return TipoEnlace la entidad {@link TipoEnlace} persistida.
   */
  @Override
  @Transactional
  public TipoEnlace create(TipoEnlace tipoEnlace) {
    log.debug("create(TipoEnlace tipoEnlace) - start");

    Assert.isNull(tipoEnlace.getId(), "Id tiene que ser null para crear TipoEnlace");
    Assert.isTrue(!(repository.findByNombre(tipoEnlace.getNombre()).isPresent()),
        "Ya existe TipoEnlace con el nombre " + tipoEnlace.getNombre());

    tipoEnlace.setActivo(Boolean.TRUE);
    TipoEnlace returnValue = repository.save(tipoEnlace);

    log.debug("create(TipoEnlace tipoEnlace) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link TipoEnlace}.
   * 
   * @param tipoEnlace tipoEnlaceActualizar {@link TipoEnlace} con los datos
   *                   actualizados.
   * @return {@link TipoEnlace} actualizado.
   */
  @Override
  @Transactional
  public TipoEnlace update(TipoEnlace tipoEnlace) {
    log.debug("update(TipoEnlace tipoEnlace) - start");

    Assert.notNull(tipoEnlace.getId(), "Id no puede ser null para actualizar TipoEnlace");
    repository.findByNombre(tipoEnlace.getNombre()).ifPresent((tipoEnlaceExistente) -> {
      Assert.isTrue(tipoEnlace.getId() == tipoEnlaceExistente.getId(),
          "Ya existe un TipoEnlace con el nombre " + tipoEnlaceExistente.getNombre());
    });

    return repository.findById(tipoEnlace.getId()).map((data) -> {
      data.setNombre(tipoEnlace.getNombre());
      data.setDescripcion(tipoEnlace.getDescripcion());
      data.setActivo(tipoEnlace.getActivo());

      TipoEnlace returnValue = repository.save(data);
      log.debug("update(TipoEnlace tipoEnlace) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoEnlaceNotFoundException(tipoEnlace.getId()));
  }

  /**
   * Desactiva el {@link TipoEnlace}.
   *
   * @param id Id del {@link TipoEnlace}.
   * @return la entidad {@link TipoEnlace} persistida.
   */
  @Override
  @Transactional
  public TipoEnlace disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "TipoEnlace id no puede ser null para desactivar un TipoEnlace");

    return repository.findById(id).map(tipoEnlace -> {
      tipoEnlace.setActivo(false);

      TipoEnlace returnValue = repository.save(tipoEnlace);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoEnlaceNotFoundException(id));
  }

  /**
   * Obtiene todas las entidades {@link TipoEnlace} activas paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link TipoEnlace} paginadas y filtradas.
   */
  @Override
  public Page<TipoEnlace> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - start");
    Specification<TipoEnlace> specByQuery = new QuerySpecification<TipoEnlace>(query);
    Specification<TipoEnlace> specActivos = TipoEnlaceSpecifications.activos();

    Specification<TipoEnlace> specs = Specification.where(specActivos).and(specByQuery);

    Page<TipoEnlace> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link TipoEnlace} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link TipoEnlace} paginadas y filtradas.
   */
  @Override
  public Page<TipoEnlace> findAllTodos(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - start");
    Specification<TipoEnlace> spec = new QuerySpecification<TipoEnlace>(query);
    Page<TipoEnlace> returnValue = repository.findAll(spec, paging);
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link TipoEnlace} por id.
   * 
   * @param id Identificador de la entidad {@link TipoEnlace}.
   * @return TipoEnlace la entidad {@link TipoEnlace}.
   */
  @Override
  public TipoEnlace findById(Long id) {
    log.debug("findById(Long id) - start");
    final TipoEnlace returnValue = repository.findById(id).orElseThrow(() -> new TipoEnlaceNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

}
