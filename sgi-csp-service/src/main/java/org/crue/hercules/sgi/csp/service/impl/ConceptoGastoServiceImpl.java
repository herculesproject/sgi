package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.exceptions.ConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.repository.ConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConceptoGastoSpecifications;
import org.crue.hercules.sgi.csp.service.ConceptoGastoService;
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
 * Service Implementation para la gestión de {@link ConceptoGasto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConceptoGastoServiceImpl implements ConceptoGastoService {

  private final ConceptoGastoRepository repository;

  public ConceptoGastoServiceImpl(ConceptoGastoRepository conceptoGastoRepository) {
    this.repository = conceptoGastoRepository;
  }

  /**
   * Guardar un nuevo {@link ConceptoGasto}.
   *
   * @param conceptoGasto la entidad {@link ConceptoGasto} a guardar.
   * @return la entidad {@link ConceptoGasto} persistida.
   */
  @Override
  @Transactional
  public ConceptoGasto create(ConceptoGasto conceptoGasto) {
    log.debug("create(ConceptoGasto conceptoGasto) - start");

    Assert.isNull(conceptoGasto.getId(), "ConceptoGasto id tiene que ser null para crear un nuevo ConceptoGasto");

    Assert.isTrue(!(repository.findByNombreAndActivoIsTrue(conceptoGasto.getNombre()).isPresent()),
        "Ya existe un ConceptoGasto con el nombre " + conceptoGasto.getNombre());

    conceptoGasto.setActivo(true);
    ConceptoGasto returnValue = repository.save(conceptoGasto);

    log.debug("create(ConceptoGasto conceptoGasto) - end");
    return returnValue;

  }

  /**
   * Actualizar {@link ConceptoGasto}.
   *
   * @param conceptoGastoActualizar la entidad {@link ConceptoGasto} a actualizar.
   * @return la entidad {@link ConceptoGasto} persistida.
   */
  @Override
  @Transactional
  public ConceptoGasto update(ConceptoGasto conceptoGastoActualizar) {
    log.debug("update(ConceptoGasto conceptoGastoActualizar) - start");

    Assert.notNull(conceptoGastoActualizar.getId(),
        "ConceptoGasto id no puede ser null para actualizar un ConceptoGasto");

    repository.findByNombreAndActivoIsTrue(conceptoGastoActualizar.getNombre()).ifPresent((conceptoGastoExistente) -> {
      Assert.isTrue(conceptoGastoActualizar.getId() == conceptoGastoExistente.getId(),
          "Ya existe un ConceptoGasto con el nombre " + conceptoGastoExistente.getNombre());
    });

    return repository.findById(conceptoGastoActualizar.getId()).map(conceptoGasto -> {
      conceptoGasto.setNombre(conceptoGastoActualizar.getNombre());
      conceptoGasto.setDescripcion(conceptoGastoActualizar.getDescripcion());

      ConceptoGasto returnValue = repository.save(conceptoGasto);
      log.debug("update(ConceptoGasto conceptoGastoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ConceptoGastoNotFoundException(conceptoGastoActualizar.getId()));
  }

  /**
   * Reactiva el {@link ConceptoGasto}.
   *
   * @param id Id del {@link ConceptoGasto}.
   * @return la entidad {@link ConceptoGasto} persistida.
   */
  @Override
  @Transactional
  public ConceptoGasto enable(Long id) {
    log.debug("enable(Long id) - start");

    Assert.notNull(id, "ConceptoGasto id no puede ser null para reactivar un ConceptoGasto");

    return repository.findById(id).map(conceptoGasto -> {
      if (conceptoGasto.getActivo()) {
        // Si esta activo no se hace nada
        return conceptoGasto;
      }

      repository.findByNombreAndActivoIsTrue(conceptoGasto.getNombre()).ifPresent((conceptoGastoExistente) -> {
        Assert.isTrue(conceptoGasto.getId() == conceptoGastoExistente.getId(),
            "Ya existe un ConceptoGasto con el nombre " + conceptoGastoExistente.getNombre());
      });

      conceptoGasto.setActivo(true);

      ConceptoGasto returnValue = repository.save(conceptoGasto);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ConceptoGastoNotFoundException(id));
  }

  /**
   * Desactiva el {@link ConceptoGasto}.
   *
   * @param id Id del {@link ConceptoGasto}.
   * @return la entidad {@link ConceptoGasto} persistida.
   */
  @Override
  @Transactional
  public ConceptoGasto disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "ConceptoGasto id no puede ser null para desactivar un ConceptoGasto");

    return repository.findById(id).map(conceptoGasto -> {
      if (!conceptoGasto.getActivo()) {
        // Si no esta activo no se hace nada
        return conceptoGasto;
      }

      conceptoGasto.setActivo(false);

      ConceptoGasto returnValue = repository.save(conceptoGasto);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ConceptoGastoNotFoundException(id));
  }

  /**
   * Obtener todas las entidades {@link ConceptoGasto} activos paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ConceptoGasto} paginadas y/o filtradas.
   */
  @Override
  public Page<ConceptoGasto> findAll(List<QueryCriteria> query, Pageable pageable) {
    log.debug("findAll(List<QueryCriteria> query, Pageable pageable) - start");
    Specification<ConceptoGasto> specByQuery = new QuerySpecification<ConceptoGasto>(query);
    Specification<ConceptoGasto> specActivos = ConceptoGastoSpecifications.activos();

    Specification<ConceptoGasto> specs = Specification.where(specActivos).and(specByQuery);

    Page<ConceptoGasto> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link ConceptoGasto} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ConceptoGasto} paginadas y/o filtradas.
   */
  @Override
  public Page<ConceptoGasto> findAllTodos(List<QueryCriteria> query, Pageable pageable) {
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable pageable) - start");
    Specification<ConceptoGasto> specByQuery = new QuerySpecification<ConceptoGasto>(query);
    Page<ConceptoGasto> returnValue = repository.findAll(specByQuery, pageable);
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link ConceptoGasto} por su id.
   *
   * @param id el id de la entidad {@link ConceptoGasto}.
   * @return la entidad {@link ConceptoGasto}.
   */
  @Override
  public ConceptoGasto findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConceptoGasto returnValue = repository.findById(id).orElseThrow(() -> new ConceptoGastoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;

  }

}
