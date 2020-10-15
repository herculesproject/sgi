package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.exceptions.AreaTematicaArbolNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaAreaTematicaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.repository.AreaTematicaArbolRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaAreaTematicaRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaAreaTematicaSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaAreaTematicaService;
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
 * Service Implementation para gestion {@link ConvocatoriaAreaTematica}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaAreaTematicaServiceImpl implements ConvocatoriaAreaTematicaService {

  private final ConvocatoriaAreaTematicaRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final AreaTematicaArbolRepository areaTematicaArbolRepository;

  public ConvocatoriaAreaTematicaServiceImpl(ConvocatoriaAreaTematicaRepository repository,
      ConvocatoriaRepository convocatoriaRepository, AreaTematicaArbolRepository areaTematicaArbolRepository) {
    this.repository = repository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.areaTematicaArbolRepository = areaTematicaArbolRepository;
  }

  /**
   * Guarda la entidad {@link ConvocatoriaAreaTematica}.
   * 
   * @param convocatoriaAreaTematica la entidad {@link ConvocatoriaAreaTematica} a
   *                                 guardar.
   * @return ConvocatoriaAreaTematica la entidad {@link ConvocatoriaAreaTematica}
   *         persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaAreaTematica create(ConvocatoriaAreaTematica convocatoriaAreaTematica) {
    log.debug("create(ConvocatoriaAreaTematica convocatoriaAreaTematica) - start");

    Assert.isNull(convocatoriaAreaTematica.getId(), "Id tiene que ser null para crear ConvocatoriaAreaTematica");

    Assert.notNull(convocatoriaAreaTematica.getConvocatoria().getId(),
        "Id Convocatoria no puede ser null para crear ConvocatoriaAreaTematica");

    Assert.notNull(convocatoriaAreaTematica.getAreaTematicaArbol().getId(),
        "Id AreaTematicaArbol no puede ser null para crear ConvocatoriaAreaTematica");

    convocatoriaAreaTematica
        .setConvocatoria(convocatoriaRepository.findById(convocatoriaAreaTematica.getConvocatoria().getId())
            .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaAreaTematica.getConvocatoria().getId())));

    convocatoriaAreaTematica.setAreaTematicaArbol(
        areaTematicaArbolRepository.findById(convocatoriaAreaTematica.getAreaTematicaArbol().getId()).orElseThrow(
            () -> new AreaTematicaArbolNotFoundException(convocatoriaAreaTematica.getAreaTematicaArbol().getId())));

    Assert.isTrue(
        !repository.findByConvocatoriaIdAndAreaTematicaArbolId(convocatoriaAreaTematica.getConvocatoria().getId(),
            convocatoriaAreaTematica.getAreaTematicaArbol().getId()).isPresent(),
        "Ya existe una asociación activa para esa Convocatoria y AreaTematicaArbol");

    ConvocatoriaAreaTematica returnValue = repository.save(convocatoriaAreaTematica);

    log.debug("create(ConvocatoriaAreaTematica convocatoriaAreaTematica) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ConvocatoriaAreaTematica}.
   *
   * @param convocatoriaAreaTematicaActualizar la entidad
   *                                           {@link ConvocatoriaAreaTematica} a
   *                                           actualizar.
   * @return la entidad {@link ConvocatoriaAreaTematica} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaAreaTematica update(ConvocatoriaAreaTematica convocatoriaAreaTematicaActualizar) {
    log.debug("update(ConvocatoriaAreaTematica convocatoriaAreaTematicaActualizar) - start");

    Assert.notNull(convocatoriaAreaTematicaActualizar.getId(),
        "ConvocatoriaAreaTematica id no puede ser null para actualizar un ConvocatoriaAreaTematica");

    return repository.findById(convocatoriaAreaTematicaActualizar.getId()).map(convocatoriaAreaTematica -> {
      convocatoriaAreaTematica.setObservaciones(convocatoriaAreaTematicaActualizar.getObservaciones());
      ConvocatoriaAreaTematica returnValue = repository.save(convocatoriaAreaTematicaActualizar);
      log.debug("update(ConvocatoriaAreaTematica convocatoriaAreaTematicaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaAreaTematicaNotFoundException(convocatoriaAreaTematicaActualizar.getId()));
  }

  /**
   * Elimina la {@link ConvocatoriaAreaTematica}.
   *
   * @param id Id del {@link ConvocatoriaAreaTematica}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "ConvocatoriaAreaTematica id no puede ser null para eliminar un ConvocatoriaAreaTematica");
    if (!repository.existsById(id)) {
      throw new ConvocatoriaAreaTematicaNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ConvocatoriaAreaTematica} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaAreaTematica}.
   * @return la entidad {@link ConvocatoriaAreaTematica}.
   */
  @Override
  public ConvocatoriaAreaTematica findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaAreaTematica returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaAreaTematicaNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;

  }

  /**
   * Obtiene las {@link ConvocatoriaAreaTematica} para una {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaAreaTematica} de la
   *         {@link Convocatoria} paginadas.
   */
  public Page<ConvocatoriaAreaTematica> findAllByConvocatoria(Long convocatoriaId, List<QueryCriteria> query,
      Pageable pageable) {
    log.debug("findAllByConvocatoria(Long convocatoriaId, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<ConvocatoriaAreaTematica> specByQuery = new QuerySpecification<ConvocatoriaAreaTematica>(query);
    Specification<ConvocatoriaAreaTematica> specByConvocatoria = ConvocatoriaAreaTematicaSpecifications
        .byConvocatoriaId(convocatoriaId);

    Specification<ConvocatoriaAreaTematica> specs = Specification.where(specByConvocatoria).and(specByQuery);

    Page<ConvocatoriaAreaTematica> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoria(Long convocatoriaId, List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

}
