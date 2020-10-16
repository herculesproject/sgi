package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.exceptions.TipoFaseNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.repository.TipoFaseRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoFaseSpecifications;
import org.crue.hercules.sgi.csp.service.TipoFaseService;
import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoFaseServiceImpl implements TipoFaseService {

  private final TipoFaseRepository tipoFaseRepository;

  public TipoFaseServiceImpl(TipoFaseRepository tipoFaseRepository) {
    this.tipoFaseRepository = tipoFaseRepository;
  }

  /**
   * Guardar {@link TipoFase}.
   *
   * @param tipoFase la entidad {@link TipoFase} a guardar.
   * @return la entidad {@link TipoFase} persistida.
   */
  @Override
  @Transactional
  public TipoFase create(TipoFase tipoFase) {
    log.debug("create (TipoFase tipoFase) - start");

    Assert.isNull(tipoFase.getId(), "tipoFase id no puede ser null para crear un nuevo tipoFase");
    Assert.isTrue(!(tipoFaseRepository.findByNombre(tipoFase.getNombre()).isPresent()),
        "Ya existe TipoFase con el nombre " + tipoFase.getNombre());

    tipoFase.setActivo(Boolean.TRUE);
    TipoFase returnValue = tipoFaseRepository.save(tipoFase);

    log.debug("create (TipoFase tipoFase) - start");
    return returnValue;
  }

  /**
   * Actualizar {@link TipoFase}.
   *
   * @param tipoFaseActualizar la entidad {@link TipoFase} a actualizar.
   * @return la entidad {@link TipoFase} persistida.
   */
  @Override
  @Transactional
  public TipoFase update(TipoFase tipoFaseActualizar) {
    log.debug("update(TipoFase tipoFaseActualizar) - start");

    Assert.notNull(tipoFaseActualizar.getId(), "TipoFase id no puede ser null para actualizar");
    tipoFaseRepository.findByNombre(tipoFaseActualizar.getNombre()).ifPresent((tipoFaseExistente) -> {
      Assert.isTrue(tipoFaseActualizar.getId() == tipoFaseExistente.getId(),
          "Ya existe un TipoFase con el nombre " + tipoFaseExistente.getNombre());
    });

    return tipoFaseRepository.findById(tipoFaseActualizar.getId()).map(tipoFase -> {
      tipoFase.setNombre(tipoFaseActualizar.getNombre());
      tipoFase.setDescripcion(tipoFaseActualizar.getDescripcion());
      tipoFase.setActivo(tipoFaseActualizar.getActivo());
      TipoFase returnValue = tipoFaseRepository.save(tipoFase);
      log.debug("update(TipoFase tipoFaseActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoFaseNotFoundException(tipoFaseActualizar.getId()));

  }

  /**
   * Obtener todas las entidades {@link TipoFase} activas paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoFase} paginadas y/o filtradas.
   */
  @Override
  public Page<TipoFase> findAll(List<QueryCriteria> query, Pageable pageable) {
    log.debug("findAll(List<QueryCriteria> query, Pageable pageable) - start");

    Specification<TipoFase> specByQuery = new QuerySpecification<TipoFase>(query);
    Specification<TipoFase> specActivos = TipoFaseSpecifications.activos();

    Specification<TipoFase> specs = Specification.where(specActivos).and(specByQuery);

    Page<TipoFase> returnValue = tipoFaseRepository.findAll(specs, pageable);

    log.debug("findAll(List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link TipoFase} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoFase} paginadas y/o filtradas.
   */
  @Override
  public Page<TipoFase> findAllTodos(List<QueryCriteria> query, Pageable pageable) {
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable pageable) - start");

    Specification<TipoFase> spec = new QuerySpecification<TipoFase>(query);

    Page<TipoFase> returnValue = tipoFaseRepository.findAll(spec, pageable);

    log.debug("findAllTodos(List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;

  }

  /**
   * Obtiene {@link TipoFase} por id.
   *
   * @param id el id de la entidad {@link TipoFase}.
   * @return la entidad {@link TipoFase}.
   */
  @Override
  public TipoFase findById(Long id) throws TipoFaseNotFoundException {
    log.debug("findById(Long id) - start");
    TipoFase tipoFase = tipoFaseRepository.findById(id).orElseThrow(() -> new TipoFaseNotFoundException(id));
    log.debug("findById(Long id) - end");
    return tipoFase;
  }

  /**
   * Desactiva el {@link TipoFase} por id.
   *
   * @param id el id de la entidad {@link TipoFase}.
   */
  @Override
  @Transactional
  public TipoFase disable(Long id) throws TipoFaseNotFoundException {
    log.debug("disable(Long id) - start");
    Assert.notNull(id, "El id no puede ser nulo");
    return tipoFaseRepository.findById(id).map(tipoFase -> {
      tipoFase.setActivo(false);

      TipoFase returnValue = tipoFaseRepository.save(tipoFase);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoFaseNotFoundException(id));

  }

}
