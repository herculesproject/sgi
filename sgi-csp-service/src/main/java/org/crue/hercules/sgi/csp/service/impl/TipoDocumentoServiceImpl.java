package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.exceptions.TipoDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.repository.TipoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoDocumentoSpecifications;
import org.crue.hercules.sgi.csp.service.TipoDocumentoService;
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
 * Service Implementation para la gestión de {@link TipoDocumento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoDocumentoServiceImpl implements TipoDocumentoService {

  private final TipoDocumentoRepository tipoDocumentoRepository;

  public TipoDocumentoServiceImpl(TipoDocumentoRepository tipoDocumentoRepository) {
    this.tipoDocumentoRepository = tipoDocumentoRepository;
  }

  /**
   * Guardar un nuevo {@link TipoDocumento}.
   *
   * @param tipoDocumento la entidad {@link TipoDocumento} a guardar.
   * @return la entidad {@link TipoDocumento} persistida.
   */
  @Override
  @Transactional
  public TipoDocumento create(TipoDocumento tipoDocumento) {
    log.debug("create(TipoDocumento tipoDocumento) - start");

    Assert.isNull(tipoDocumento.getId(), "TipoDocumento id tiene que ser null para crear un nuevo TipoDocumento");
    Assert.isTrue(!(tipoDocumentoRepository.findByNombre(tipoDocumento.getNombre()).isPresent()),
        "Ya existe TipoDocumento con el nombre " + tipoDocumento.getNombre());

    tipoDocumento.setActivo(Boolean.TRUE);
    TipoDocumento returnValue = tipoDocumentoRepository.save(tipoDocumento);

    log.debug("create(TipoDocumento tipoDocumento) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link TipoDocumento}.
   *
   * @param tipoDocumentoActualizar la entidad {@link TipoDocumento} a actualizar.
   * @return la entidad {@link TipoDocumento} persistida.
   */
  @Override
  @Transactional
  public TipoDocumento update(TipoDocumento tipoDocumentoActualizar) {
    log.debug("update(TipoDocumento tipoDocumento) - start");

    Assert.notNull(tipoDocumentoActualizar.getId(),
        "TipoDocumento id no puede ser null para actualizar un TipoDocumento");
    tipoDocumentoRepository.findByNombre(tipoDocumentoActualizar.getNombre()).ifPresent((tipoDocumentoExistente) -> {
      Assert.isTrue(tipoDocumentoActualizar.getId() == tipoDocumentoExistente.getId(),
          "Ya existe un TipoDocumento con el nombre " + tipoDocumentoExistente.getNombre());
    });

    return tipoDocumentoRepository.findById(tipoDocumentoActualizar.getId()).map(tipoDocumento -> {
      tipoDocumento.setNombre(tipoDocumentoActualizar.getNombre());
      tipoDocumento.setDescripcion(tipoDocumentoActualizar.getDescripcion());
      tipoDocumento.setActivo(tipoDocumentoActualizar.getActivo());

      TipoDocumento returnValue = tipoDocumentoRepository.save(tipoDocumento);
      log.debug("update(TipoDocumento tipoDocumento) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoDocumentoNotFoundException(tipoDocumentoActualizar.getId()));
  }

  /**
   * Desactiva el {@link TipoDocumento}.
   *
   * @param id Id del {@link TipoDocumento}.
   * @return la entidad {@link TipoDocumento} persistida.
   */
  @Override
  @Transactional
  public TipoDocumento disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "TipoDocumento id no puede ser null para desactivar un TipoDocumento");

    return tipoDocumentoRepository.findById(id).map(tipoDocumento -> {
      tipoDocumento.setActivo(false);

      TipoDocumento returnValue = tipoDocumentoRepository.save(tipoDocumento);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoDocumentoNotFoundException(id));
  }

  /**
   * Obtener todas las entidades {@link TipoDocumento} activas paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoDocumento} paginadas y/o filtradas.
   */
  @Override
  public Page<TipoDocumento> findAll(List<QueryCriteria> query, Pageable pageable) {
    log.debug("findAll(List<QueryCriteria> query, Pageable pageable) - start");
    Specification<TipoDocumento> specByQuery = new QuerySpecification<TipoDocumento>(query);
    Specification<TipoDocumento> specActivos = TipoDocumentoSpecifications.activos();

    Specification<TipoDocumento> specs = Specification.where(specActivos).and(specByQuery);

    Page<TipoDocumento> returnValue = tipoDocumentoRepository.findAll(specs, pageable);
    log.debug("findAll(List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link TipoDocumento} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoDocumento} paginadas y/o filtradas.
   */
  @Override
  public Page<TipoDocumento> findAllTodos(List<QueryCriteria> query, Pageable pageable) {
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable pageable) - start");
    Specification<TipoDocumento> spec = new QuerySpecification<TipoDocumento>(query);

    Page<TipoDocumento> returnValue = tipoDocumentoRepository.findAll(spec, pageable);
    log.debug("findAllTodos(List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link TipoDocumento} por su id.
   *
   * @param id el id de la entidad {@link TipoDocumento}.
   * @return la entidad {@link TipoDocumento}.
   */
  @Override
  public TipoDocumento findById(Long id) {
    log.debug("findById(Long id)  - start");
    final TipoDocumento returnValue = tipoDocumentoRepository.findById(id)
        .orElseThrow(() -> new TipoDocumentoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

}
