package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.TipoAmbitoGeograficoNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.repository.TipoAmbitoGeograficoRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoAmbitoGeograficoSpecifications;
import org.crue.hercules.sgi.csp.service.TipoAmbitoGeograficoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link TipoAmbitoGeografico}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoAmbitoGeograficoServiceImpl implements TipoAmbitoGeograficoService {

  private final TipoAmbitoGeograficoRepository repository;

  public TipoAmbitoGeograficoServiceImpl(TipoAmbitoGeograficoRepository tipoAmbitoGeograficoRepository) {
    this.repository = tipoAmbitoGeograficoRepository;
  }

  /**
   * Guardar un nuevo {@link TipoAmbitoGeografico}.
   *
   * @param tipoAmbitoGeografico la entidad {@link TipoAmbitoGeografico} a
   *                             guardar.
   * @return la entidad {@link TipoAmbitoGeografico} persistida.
   */
  @Override
  @Transactional
  public TipoAmbitoGeografico create(TipoAmbitoGeografico tipoAmbitoGeografico) {
    log.debug("create(TipoAmbitoGeografico tipoAmbitoGeografico) - start");

    Assert.isNull(tipoAmbitoGeografico.getId(),
        "TipoAmbitoGeografico id tiene que ser null para crear un nuevo TipoAmbitoGeografico");
    Assert.isTrue(!(repository.findByNombre(tipoAmbitoGeografico.getNombre()).isPresent()),
        "Ya existe un TipoAmbitoGeografico con el nombre " + tipoAmbitoGeografico.getNombre());

    tipoAmbitoGeografico.setActivo(true);
    TipoAmbitoGeografico returnValue = repository.save(tipoAmbitoGeografico);

    log.debug("create(TipoAmbitoGeografico tipoAmbitoGeografico) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link TipoAmbitoGeografico}.
   *
   * @param tipoAmbitoGeograficoActualizar la entidad {@link TipoAmbitoGeografico}
   *                                       a actualizar.
   * @return la entidad {@link TipoAmbitoGeografico} persistida.
   */
  @Override
  @Transactional
  public TipoAmbitoGeografico update(TipoAmbitoGeografico tipoAmbitoGeograficoActualizar) {
    log.debug("update(TipoAmbitoGeografico tipoAmbitoGeograficoActualizar) - start");

    Assert.notNull(tipoAmbitoGeograficoActualizar.getId(),
        "TipoAmbitoGeografico id no puede ser null para actualizar un TipoAmbitoGeografico");
    repository.findByNombre(tipoAmbitoGeograficoActualizar.getNombre()).ifPresent((tipoDocumentoExistente) -> {
      Assert.isTrue(tipoAmbitoGeograficoActualizar.getId() == tipoDocumentoExistente.getId(),
          "Ya existe un TipoAmbitoGeografico con el nombre " + tipoDocumentoExistente.getNombre());
    });

    return repository.findById(tipoAmbitoGeograficoActualizar.getId()).map(tipoAmbitoGeografico -> {
      tipoAmbitoGeografico.setNombre(tipoAmbitoGeograficoActualizar.getNombre());
      tipoAmbitoGeografico.setActivo(tipoAmbitoGeograficoActualizar.getActivo());

      TipoAmbitoGeografico returnValue = repository.save(tipoAmbitoGeografico);
      log.debug("update(TipoAmbitoGeografico tipoAmbitoGeograficoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoAmbitoGeograficoNotFoundException(tipoAmbitoGeograficoActualizar.getId()));
  }

  /**
   * Desactiva el {@link TipoAmbitoGeografico}.
   *
   * @param id Id del {@link TipoAmbitoGeografico}.
   * @return la entidad {@link TipoAmbitoGeografico} persistida.
   */
  @Override
  @Transactional
  public TipoAmbitoGeografico disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "TipoAmbitoGeografico id no puede ser null para desactivar un TipoAmbitoGeografico");

    return repository.findById(id).map(tipoAmbitoGeografico -> {
      tipoAmbitoGeografico.setActivo(false);

      TipoAmbitoGeografico returnValue = repository.save(tipoAmbitoGeografico);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoAmbitoGeograficoNotFoundException(id));
  }

  /**
   * Obtener todas las entidades {@link TipoAmbitoGeografico} activos paginadas
   * y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoAmbitoGeografico} paginadas y/o
   *         filtradas.
   */
  @Override
  public Page<TipoAmbitoGeografico> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<TipoAmbitoGeografico> specs = TipoAmbitoGeograficoSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoAmbitoGeografico> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link TipoAmbitoGeografico} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoAmbitoGeografico} paginadas y/o
   *         filtradas.
   */
  @Override
  public Page<TipoAmbitoGeografico> findAllTodos(String query, Pageable pageable) {
    log.debug("findAllTodos(String query, Pageable pageable) - start");
    Specification<TipoAmbitoGeografico> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<TipoAmbitoGeografico> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllTodos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link TipoAmbitoGeografico} por su id.
   *
   * @param id el id de la entidad {@link TipoAmbitoGeografico}.
   * @return la entidad {@link TipoAmbitoGeografico}.
   */
  @Override
  public TipoAmbitoGeografico findById(Long id) {
    log.debug("findById(Long id)  - start");
    final TipoAmbitoGeografico returnValue = repository.findById(id)
        .orElseThrow(() -> new TipoAmbitoGeograficoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

}
