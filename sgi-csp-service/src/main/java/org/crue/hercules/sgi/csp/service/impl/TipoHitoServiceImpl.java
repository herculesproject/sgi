package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.exceptions.TipoHitoNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.repository.TipoHitoRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoHitoSpecifications;
import org.crue.hercules.sgi.csp.service.TipoHitoService;
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
public class TipoHitoServiceImpl implements TipoHitoService {

  private final TipoHitoRepository tipoHitoRepository;

  public TipoHitoServiceImpl(TipoHitoRepository tipoHitoRepository) {
    this.tipoHitoRepository = tipoHitoRepository;
  }

  /**
   * Guardar {@link TipoHito}.
   *
   * @param tipoHito la entidad {@link TipoHito} a guardar.
   * @return la entidad {@link TipoHito} persistida.
   */
  @Override
  @Transactional
  public TipoHito create(TipoHito tipoHito) {
    log.debug("create(TipoHito tipoHito) - start");

    Assert.isNull(tipoHito.getId(), "tipoHito id tiene que ser null para crear un nuevo tipoHito");
    Assert.isTrue(!(tipoHitoRepository.findByNombre(tipoHito.getNombre()).isPresent()),
        "Ya existe TipoHito con el nombre " + tipoHito.getNombre());

    tipoHito.setActivo(Boolean.TRUE);
    TipoHito returnValue = tipoHitoRepository.save(tipoHito);

    log.debug("create(TipoHito tipoHito) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link TipoHito}.
   *
   * @param tipoHitoActualizar la entidad {@link TipoHito} a actualizar.
   * @return la entidad {@link TipoHito} persistida.
   */
  @Override
  @Transactional
  public TipoHito update(TipoHito tipoHitoActualizar) {
    log.debug("update(TipoHito tipoHitoActualizar) - start");

    Assert.notNull(tipoHitoActualizar.getId(), "TipoHito id no puede ser null para actualizar");
    tipoHitoRepository.findByNombre(tipoHitoActualizar.getNombre()).ifPresent((tipoHitoExistente) -> {
      Assert.isTrue(tipoHitoActualizar.getId() == tipoHitoExistente.getId(),
          "Ya existe un TipoHito con el nombre " + tipoHitoExistente.getNombre());
    });

    return tipoHitoRepository.findById(tipoHitoActualizar.getId()).map(tipoHito -> {
      tipoHito.setNombre(tipoHitoActualizar.getNombre());
      tipoHito.setDescripcion(tipoHitoActualizar.getDescripcion());
      tipoHito.setActivo(tipoHitoActualizar.getActivo());
      TipoHito returnValue = tipoHitoRepository.save(tipoHito);
      log.debug("update(TipoHito tipoHitoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoHitoNotFoundException(tipoHitoActualizar.getId()));

  }

  /**
   * Obtener todas las entidades {@link TipoHito} activas paginadas
   *
   * @param pageable la información de la paginación.
   * @param query    información del filtro.
   * @return la lista de entidades {@link TipoHito} paginadas
   */
  @Override
  public Page<TipoHito> findAll(List<QueryCriteria> query, Pageable pageable) {
    log.debug("findAll(List<QueryCriteria> query, Pageable pageable) - start");
    Specification<TipoHito> specByQuery = new QuerySpecification<TipoHito>(query);
    Specification<TipoHito> specActivos = TipoHitoSpecifications.activos();

    Specification<TipoHito> specs = Specification.where(specActivos).and(specByQuery);

    Page<TipoHito> returnValue = tipoHitoRepository.findAll(specs, pageable);

    log.debug("findAll(List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link TipoHito} paginadas
   *
   * @param pageable la información de la paginación.
   * @param query    información del filtro.
   * @return la lista de entidades {@link TipoHito} paginadas
   */
  @Override
  public Page<TipoHito> findAllTodos(List<QueryCriteria> query, Pageable pageable) {
    log.debug("findAll(List<QueryCriteria> query, Pageable pageable) - start");
    Specification<TipoHito> spec = new QuerySpecification<TipoHito>(query);

    Page<TipoHito> returnValue = tipoHitoRepository.findAll(spec, pageable);

    log.debug("findAll(List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link TipoHito} por id.
   *
   * @param id el id de la entidad {@link TipoHito}.
   * @return la entidad {@link TipoHito}.
   */
  @Override
  public TipoHito findById(Long id) throws TipoHitoNotFoundException {
    log.debug("findById(Long id) id:{}- start", id);
    TipoHito tipoHito = tipoHitoRepository.findById(id).orElseThrow(() -> new TipoHitoNotFoundException(id));
    log.debug("findById(Long id) id:{}- end", id);
    return tipoHito;
  }

  /**
   * Elimina el {@link TipoHito} por id.
   *
   * @param id el id de la entidad {@link TipoHito}.
   */
  @Override
  @Transactional
  public TipoHito disable(Long id) throws TipoHitoNotFoundException {
    log.debug("disable(Long id) start");
    Assert.notNull(id, "El id no puede ser nulo");

    return tipoHitoRepository.findById(id).map(tipoHito -> {
      tipoHito.setActivo(false);

      TipoHito returnValue = tipoHitoRepository.save(tipoHito);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoHitoNotFoundException(id));

  }

}