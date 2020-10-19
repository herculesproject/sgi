package org.crue.hercules.sgi.csp.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.PlanNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProgramaNotFoundException;
import org.crue.hercules.sgi.csp.model.Plan;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.repository.PlanRepository;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProgramaSpecifications;
import org.crue.hercules.sgi.csp.service.ProgramaService;
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
 * Service Implementation para la gestión de {@link Programa}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProgramaServiceImpl implements ProgramaService {

  private final ProgramaRepository repository;
  private final PlanRepository planRepository;

  public ProgramaServiceImpl(ProgramaRepository programaRepository, PlanRepository planRepository) {
    this.repository = programaRepository;
    this.planRepository = planRepository;
  }

  /**
   * Guardar un nuevo {@link Programa}.
   *
   * @param programa la entidad {@link Programa} a guardar.
   * @return la entidad {@link Programa} persistida.
   */
  @Override
  @Transactional
  public Programa create(Programa programa) {
    log.debug("create(Programa programa) - start");

    Assert.isNull(programa.getId(), "Programa id tiene que ser null para crear un nuevo Programa");
    Assert.notNull(programa.getPlan().getId(), "Id Plan no puede ser null para crear un Programa");

    Assert.isTrue(!(repository.findByNombreAndPlanId(programa.getNombre(), programa.getPlan().getId()).isPresent()),
        "Ya existe un Programa con el nombre " + programa.getNombre() + " en el Plan");

    programa.setPlan(planRepository.findById(programa.getPlan().getId())
        .orElseThrow(() -> new PlanNotFoundException(programa.getPlan().getId())));

    if (programa.getPadre() != null) {
      if (programa.getPadre().getId() == null) {
        programa.setPadre(null);
      } else {
        programa.setPadre(repository.findById(programa.getPadre().getId())
            .orElseThrow(() -> new ProgramaNotFoundException(programa.getPadre().getId())));
      }
    }

    programa.setActivo(true);
    Programa returnValue = repository.save(programa);

    log.debug("create(Programa programa) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link Programa} y si se pone activo a false hace lo mismo con
   * todos sus hijos en cascada.
   *
   * @param programaActualizar la entidad {@link Programa} a actualizar.
   * @return la entidad {@link Programa} persistida.
   */
  @Override
  @Transactional
  public Programa update(Programa programaActualizar) {
    log.debug("update(Programa programaActualizar) - start");

    Assert.notNull(programaActualizar.getId(), "Programa id no puede ser null para actualizar un Programa");

    if (programaActualizar.getPadre() != null) {
      if (programaActualizar.getPadre().getId() == null) {
        programaActualizar.setPadre(null);
      } else {
        programaActualizar.setPadre(repository.findById(programaActualizar.getPadre().getId())
            .orElseThrow(() -> new ProgramaNotFoundException(programaActualizar.getPadre().getId())));
      }
    }

    return repository.findById(programaActualizar.getId()).map(programa -> {
      repository.findByNombreAndPlanId(programaActualizar.getNombre(), programa.getPlan().getId())
          .ifPresent((tipoDocumentoExistente) -> {
            Assert.isTrue(programaActualizar.getId() == tipoDocumentoExistente.getId(),
                "Ya existe un Programa con el nombre " + tipoDocumentoExistente.getNombre() + " en el Plan");
          });

      programa.setNombre(programaActualizar.getNombre());
      programa.setDescripcion(programaActualizar.getDescripcion());
      programa.setPadre(programaActualizar.getPadre());
      programa.setActivo(programaActualizar.getActivo());

      // Si el programa se pone activo=false se hace lo mismo con sus hijos
      if (!programaActualizar.getActivo()) {
        List<Programa> programasHijos = repository.findByPadreIdIn(Arrays.asList(programa.getId()));

        while (!programasHijos.isEmpty()) {
          programasHijos.stream().map(programaHijo -> {
            programaHijo.setActivo(false);
            return programaHijo;
          }).collect(Collectors.toList());

          repository.saveAll(programasHijos);

          programasHijos = repository
              .findByPadreIdIn(programasHijos.stream().map(Programa::getId).collect(Collectors.toList()));
        }
      }

      Programa returnValue = repository.save(programa);
      log.debug("update(Programa programaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ProgramaNotFoundException(programaActualizar.getId()));
  }

  /**
   * Desactiva el {@link Programa} y todos sus hijos en cascada.
   *
   * @param id Id del {@link Programa}.
   * @return la entidad {@link Programa} persistida.
   */
  @Override
  @Transactional
  public Programa disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "Programa id no puede ser null para desactivar un Programa");

    return repository.findById(id).map(programa -> {
      programa.setActivo(false);

      List<Programa> programasHijos = repository.findByPadreIdIn(Arrays.asList(programa.getId()));

      while (!programasHijos.isEmpty()) {
        programasHijos.stream().map(programaHijo -> {
          programaHijo.setActivo(false);
          return programaHijo;
        }).collect(Collectors.toList());

        repository.saveAll(programasHijos);

        programasHijos = repository
            .findByPadreIdIn(programasHijos.stream().map(Programa::getId).collect(Collectors.toList()));
      }

      Programa returnValue = repository.save(programa);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ProgramaNotFoundException(id));
  }

  /**
   * Obtiene {@link Programa} por su id.
   *
   * @param id el id de la entidad {@link Programa}.
   * @return la entidad {@link Programa}.
   */
  @Override
  public Programa findById(Long id) {
    log.debug("findById(Long id)  - start");
    final Programa returnValue = repository.findById(id).orElseThrow(() -> new ProgramaNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link Programa} activos para un {@link Plan}.
   *
   * @param idPlan   el id de la entidad {@link Plan}.
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Programa} del {@link Plan} paginadas.
   */
  @Override
  public Page<Programa> findAllByPlan(Long idPlan, List<QueryCriteria> query, Pageable pageable) {
    log.debug("findAllByPlan(Long idPlan, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<Programa> specByQuery = new QuerySpecification<Programa>(query);
    Specification<Programa> specByPlanId = ProgramaSpecifications.byPlanId(idPlan);
    Specification<Programa> specActivos = ProgramaSpecifications.activos();

    Specification<Programa> specs = Specification.where(specByPlanId).and(specActivos).and(specByQuery);

    Page<Programa> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByPlan(Long idPlan, List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link Programa} para un {@link Plan}.
   *
   * @param idPlan   el id de la entidad {@link Plan}.
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Programa} del {@link Plan} paginadas.
   */
  @Override
  public Page<Programa> findAllTodosByPlan(Long idPlan, List<QueryCriteria> query, Pageable pageable) {
    log.debug("findAllTodosByPlan(Long idPlan, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<Programa> specByQuery = new QuerySpecification<Programa>(query);
    Specification<Programa> specByPlanId = ProgramaSpecifications.byPlanId(idPlan);

    Specification<Programa> specs = Specification.where(specByPlanId).and(specByQuery);

    Page<Programa> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllTodosByPlan(Long idPlan, List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

}
