package org.crue.hercules.sgi.usr.service.impl;

import java.util.List;

import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.usr.exceptions.UnidadNotFoundException;
import org.crue.hercules.sgi.usr.model.Unidad;
import org.crue.hercules.sgi.usr.repository.UnidadRepository;
import org.crue.hercules.sgi.usr.repository.specification.UnidadSpecifications;
import org.crue.hercules.sgi.usr.service.UnidadService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Unidad}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class UnidadServiceImpl implements UnidadService {

  private final UnidadRepository repository;

  public UnidadServiceImpl(UnidadRepository unidadRepository) {
    this.repository = unidadRepository;
  }

  /**
   * Guardar un nuevo {@link Unidad}.
   *
   * @param unidad la entidad {@link Unidad} a guardar.
   * @return la entidad {@link Unidad} persistida.
   */
  @Override
  @Transactional
  public Unidad create(Unidad unidad) {
    log.debug("create(Unidad unidad) - start");

    Assert.isNull(unidad.getId(), "Unidad id tiene que ser null para crear un nuevo Unidad");

    Assert.isTrue(!(repository.findByNombre(unidad.getNombre()).isPresent()),
        "Ya existe una Unidad con el nombre " + unidad.getNombre());

    Assert.isTrue(!(repository.findByAcronimo(unidad.getAcronimo()).isPresent()),
        "Ya existe una Unidad con el acrónimo " + unidad.getAcronimo());

    unidad.setActivo(true);
    Unidad returnValue = repository.save(unidad);

    log.debug("create(Unidad unidad) - end");
    return returnValue;

  }

  /**
   * Actualizar {@link Unidad}.
   *
   * @param unidadActualizar la entidad {@link Unidad} a actualizar.
   * @return la entidad {@link Unidad} persistida.
   */
  @Override
  @Transactional
  public Unidad update(Unidad unidadActualizar) {
    log.debug("update(Unidad unidadActualizar) - start");

    Assert.notNull(unidadActualizar.getId(), "Unidad id no puede ser null para actualizar un Unidad");

    repository.findByNombre(unidadActualizar.getNombre()).ifPresent((unidadExistente) -> {
      Assert.isTrue(unidadActualizar.getId() == unidadExistente.getId(),
          "Ya existe una Unidad con el nombre " + unidadExistente.getNombre());
    });

    repository.findByAcronimo(unidadActualizar.getAcronimo()).ifPresent((unidadExistente) -> {
      Assert.isTrue(unidadActualizar.getId() == unidadExistente.getId(),
          "Ya existe una Unidad con el acrónimo " + unidadExistente.getAcronimo());
    });

    return repository.findById(unidadActualizar.getId()).map(unidad -> {
      unidad.setNombre(unidadActualizar.getNombre());
      unidad.setAcronimo(unidadActualizar.getAcronimo());
      unidad.setDescripcion(unidadActualizar.getDescripcion());
      unidad.setActivo(unidadActualizar.getActivo());

      Unidad returnValue = repository.save(unidad);
      log.debug("update(Unidad unidadActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new UnidadNotFoundException(unidadActualizar.getId()));
  }

  /**
   * Desactiva el {@link Unidad}.
   *
   * @param id Id del {@link Unidad}.
   * @return la entidad {@link Unidad} persistida.
   */
  @Override
  public Unidad disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "Unidad id no puede ser null para desactivar un Unidad");

    return repository.findById(id).map(unidad -> {
      unidad.setActivo(false);

      Unidad returnValue = repository.save(unidad);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new UnidadNotFoundException(id));
  }

  /**
   * Obtener todas las entidades {@link Unidad} activas paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Unidad} paginadas y/o filtradas.
   */
  @Override
  public Page<Unidad> findAll(List<QueryCriteria> query, Pageable pageable) {
    log.debug("findAll(List<QueryCriteria> query, Pageable pageable) - start");
    Specification<Unidad> specByQuery = new QuerySpecification<Unidad>(query);
    Specification<Unidad> specActivos = UnidadSpecifications.activos();

    Specification<Unidad> specs = Specification.where(specActivos).and(specByQuery);

    Page<Unidad> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link Unidad} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Unidad} paginadas y/o filtradas.
   */
  @Override
  public Page<Unidad> findAllTodos(List<QueryCriteria> query, Pageable pageable) {
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable pageable) - start");
    Specification<Unidad> specByQuery = new QuerySpecification<Unidad>(query);
    Page<Unidad> returnValue = repository.findAll(specByQuery, pageable);
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link Unidad} por su id.
   *
   * @param id el id de la entidad {@link Unidad}.
   * @return la entidad {@link Unidad}.
   */
  @Override
  public Unidad findById(Long id) {
    log.debug("findById(Long id)  - start");
    final Unidad returnValue = repository.findById(id).orElseThrow(() -> new UnidadNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;

  }

}