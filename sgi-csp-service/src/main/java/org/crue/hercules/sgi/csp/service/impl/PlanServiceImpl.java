package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.exceptions.PlanNotFoundException;
import org.crue.hercules.sgi.csp.model.Plan;
import org.crue.hercules.sgi.csp.repository.PlanRepository;
import org.crue.hercules.sgi.csp.service.PlanService;
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
 * Service Implementation para la gestión de {@link Plan}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class PlanServiceImpl implements PlanService {

  private final PlanRepository repository;

  public PlanServiceImpl(PlanRepository planRepository) {
    this.repository = planRepository;
  }

  /**
   * Guardar un nuevo {@link Plan}.
   *
   * @param plan la entidad {@link Plan} a guardar.
   * @return la entidad {@link Plan} persistida.
   */
  @Override
  @Transactional
  public Plan create(Plan plan) {
    log.debug("create(Plan plan) - start");

    Assert.isNull(plan.getId(), "Plan id tiene que ser null para crear un nuevo Plan");
    Assert.isTrue(!(repository.findByNombre(plan.getNombre()).isPresent()),
        "Ya existe un Plan con el nombre " + plan.getNombre());

    plan.setActivo(true);
    Plan returnValue = repository.save(plan);

    log.debug("create(Plan plan) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link Plan}.
   *
   * @param planActualizar la entidad {@link Plan} a actualizar.
   * @return la entidad {@link Plan} persistida.
   */
  @Override
  @Transactional
  public Plan update(Plan planActualizar) {
    log.debug("update(Plan planActualizar) - start");

    Assert.notNull(planActualizar.getId(), "Plan id no puede ser null para actualizar un Plan");
    repository.findByNombre(planActualizar.getNombre()).ifPresent((tipoDocumentoExistente) -> {
      Assert.isTrue(planActualizar.getId() == tipoDocumentoExistente.getId(),
          "Ya existe un Plan con el nombre " + tipoDocumentoExistente.getNombre());
    });

    return repository.findById(planActualizar.getId()).map(plan -> {
      plan.setNombre(planActualizar.getNombre());
      plan.setDescripcion(planActualizar.getDescripcion());
      plan.setActivo(planActualizar.getActivo());

      Plan returnValue = repository.save(plan);
      log.debug("update(Plan planActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new PlanNotFoundException(planActualizar.getId()));
  }

  /**
   * Desactiva el {@link Plan}.
   *
   * @param id Id del {@link Plan}.
   * @return la entidad {@link Plan} persistida.
   */
  @Override
  @Transactional
  public Plan disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "Plan id no puede ser null para desactivar un Plan");

    return repository.findById(id).map(plan -> {
      plan.setActivo(false);

      Plan returnValue = repository.save(plan);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new PlanNotFoundException(id));
  }

  /**
   * Obtener todas las entidades {@link Plan} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Plan} paginadas y/o filtradas.
   */
  @Override
  public Page<Plan> findAll(List<QueryCriteria> query, Pageable pageable) {
    log.debug("findAll(List<QueryCriteria> query, Pageable pageable) - start");
    Specification<Plan> specByQuery = new QuerySpecification<Plan>(query);

    Specification<Plan> specs = Specification.where(specByQuery);

    Page<Plan> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link Plan} por su id.
   *
   * @param id el id de la entidad {@link Plan}.
   * @return la entidad {@link Plan}.
   */
  @Override
  public Plan findById(Long id) {
    log.debug("findById(Long id)  - start");
    final Plan returnValue = repository.findById(id).orElseThrow(() -> new PlanNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

}
