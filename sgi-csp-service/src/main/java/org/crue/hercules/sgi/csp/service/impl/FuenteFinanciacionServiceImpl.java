package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.FuenteFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoAmbitoGeograficoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoOrigenFuenteFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.repository.FuenteFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.TipoAmbitoGeograficoRepository;
import org.crue.hercules.sgi.csp.repository.TipoOrigenFuenteFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.FuenteFinanciacionSpecifications;
import org.crue.hercules.sgi.csp.service.FuenteFinanciacionService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link FuenteFinanciacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class FuenteFinanciacionServiceImpl implements FuenteFinanciacionService {

  private final FuenteFinanciacionRepository repository;
  private final TipoAmbitoGeograficoRepository tipoAmbitoGeograficoRepository;
  private final TipoOrigenFuenteFinanciacionRepository tipoOrigenFuenteFinanciacionRepository;

  public FuenteFinanciacionServiceImpl(FuenteFinanciacionRepository fuenteFinanciacionRepository,
      TipoAmbitoGeograficoRepository tipoAmbitoGeograficoRepository,
      TipoOrigenFuenteFinanciacionRepository tipoOrigenFuenteFinanciacionRepository) {
    this.repository = fuenteFinanciacionRepository;
    this.tipoAmbitoGeograficoRepository = tipoAmbitoGeograficoRepository;
    this.tipoOrigenFuenteFinanciacionRepository = tipoOrigenFuenteFinanciacionRepository;
  }

  /**
   * Guardar un nuevo {@link FuenteFinanciacion}.
   *
   * @param fuenteFinanciacion la entidad {@link FuenteFinanciacion} a guardar.
   * @return la entidad {@link FuenteFinanciacion} persistida.
   */
  @Override
  @Transactional
  public FuenteFinanciacion create(FuenteFinanciacion fuenteFinanciacion) {
    log.debug("create(FuenteFinanciacion fuenteFinanciacion) - start");

    Assert.isNull(fuenteFinanciacion.getId(),
        "FuenteFinanciacion id tiene que ser null para crear un nuevo FuenteFinanciacion");

    Assert.notNull(fuenteFinanciacion.getTipoAmbitoGeografico().getId(),
        "Id TipoAmbitoGeografico no puede ser null para crear un FuenteFinanciacion");
    Assert.notNull(fuenteFinanciacion.getTipoOrigenFuenteFinanciacion().getId(),
        "Id TipoOrigenFuenteFinanciacion no puede ser null para crear un FuenteFinanciacion");

    Assert.isTrue(!(repository.findByNombreAndActivoIsTrue(fuenteFinanciacion.getNombre()).isPresent()),
        "Ya existe un FuenteFinanciacion con el nombre " + fuenteFinanciacion.getNombre());

    fuenteFinanciacion.setTipoAmbitoGeografico(
        tipoAmbitoGeograficoRepository.findById(fuenteFinanciacion.getTipoAmbitoGeografico().getId()).orElseThrow(
            () -> new TipoAmbitoGeograficoNotFoundException(fuenteFinanciacion.getTipoAmbitoGeografico().getId())));
    Assert.isTrue(fuenteFinanciacion.getTipoAmbitoGeografico().getActivo(),
        "El TipoAmbitoGeografico debe estar Activo");

    fuenteFinanciacion.setTipoOrigenFuenteFinanciacion(
        tipoOrigenFuenteFinanciacionRepository.findById(fuenteFinanciacion.getTipoOrigenFuenteFinanciacion().getId())
            .orElseThrow(() -> new TipoOrigenFuenteFinanciacionNotFoundException(
                fuenteFinanciacion.getTipoOrigenFuenteFinanciacion().getId())));
    Assert.isTrue(fuenteFinanciacion.getTipoOrigenFuenteFinanciacion().getActivo(),
        "El TipoOrigenFuenteFinanciacion debe estar Activo");

    fuenteFinanciacion.setActivo(true);
    FuenteFinanciacion returnValue = repository.save(fuenteFinanciacion);

    log.debug("create(FuenteFinanciacion fuenteFinanciacion) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link FuenteFinanciacion}.
   *
   * @param fuenteFinanciacionActualizar la entidad {@link FuenteFinanciacion} a
   *                                     actualizar.
   * @return la entidad {@link FuenteFinanciacion} persistida.
   */
  @Override
  @Transactional
  public FuenteFinanciacion update(FuenteFinanciacion fuenteFinanciacionActualizar) {
    log.debug("update(FuenteFinanciacion fuenteFinanciacionActualizar) - start");

    Assert.notNull(fuenteFinanciacionActualizar.getId(),
        "FuenteFinanciacion id no puede ser null para actualizar un FuenteFinanciacion");

    Assert.notNull(fuenteFinanciacionActualizar.getTipoAmbitoGeografico().getId(),
        "Id TipoAmbitoGeografico no puede ser null para crear un FuenteFinanciacion");
    Assert.notNull(fuenteFinanciacionActualizar.getTipoOrigenFuenteFinanciacion().getId(),
        "Id TipoOrigenFuenteFinanciacion no puede ser null para crear un FuenteFinanciacion");

    repository.findByNombreAndActivoIsTrue(fuenteFinanciacionActualizar.getNombre())
        .ifPresent((fuenteFinanciacionExistente) -> {
          Assert.isTrue(fuenteFinanciacionActualizar.getId() == fuenteFinanciacionExistente.getId(),
              "Ya existe un FuenteFinanciacion con el nombre " + fuenteFinanciacionExistente.getNombre());
        });

    fuenteFinanciacionActualizar.setTipoAmbitoGeografico(
        tipoAmbitoGeograficoRepository.findById(fuenteFinanciacionActualizar.getTipoAmbitoGeografico().getId())
            .orElseThrow(() -> new TipoAmbitoGeograficoNotFoundException(
                fuenteFinanciacionActualizar.getTipoAmbitoGeografico().getId())));

    fuenteFinanciacionActualizar.setTipoOrigenFuenteFinanciacion(tipoOrigenFuenteFinanciacionRepository
        .findById(fuenteFinanciacionActualizar.getTipoOrigenFuenteFinanciacion().getId())
        .orElseThrow(() -> new TipoOrigenFuenteFinanciacionNotFoundException(
            fuenteFinanciacionActualizar.getTipoOrigenFuenteFinanciacion().getId())));

    return repository.findById(fuenteFinanciacionActualizar.getId()).map(fuenteFinanciacion -> {

      Assert.isTrue(
          fuenteFinanciacion.getTipoAmbitoGeografico().getId() == fuenteFinanciacionActualizar.getTipoAmbitoGeografico()
              .getId() || fuenteFinanciacionActualizar.getTipoAmbitoGeografico().getActivo(),
          "El TipoAmbitoGeografico debe estar Activo");

      Assert.isTrue(
          fuenteFinanciacion.getTipoOrigenFuenteFinanciacion().getId() == fuenteFinanciacionActualizar
              .getTipoOrigenFuenteFinanciacion().getId()
              || fuenteFinanciacionActualizar.getTipoOrigenFuenteFinanciacion().getActivo(),
          "El TipoOrigenFuenteFinanciacion debe estar Activo");

      fuenteFinanciacion.setNombre(fuenteFinanciacionActualizar.getNombre());
      fuenteFinanciacion.setDescripcion(fuenteFinanciacionActualizar.getDescripcion());
      fuenteFinanciacion.setFondoEstructural(fuenteFinanciacionActualizar.getFondoEstructural());
      fuenteFinanciacion.setTipoAmbitoGeografico(fuenteFinanciacionActualizar.getTipoAmbitoGeografico());
      fuenteFinanciacion
          .setTipoOrigenFuenteFinanciacion(fuenteFinanciacionActualizar.getTipoOrigenFuenteFinanciacion());

      FuenteFinanciacion returnValue = repository.save(fuenteFinanciacion);
      log.debug("update(FuenteFinanciacion fuenteFinanciacionActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new FuenteFinanciacionNotFoundException(fuenteFinanciacionActualizar.getId()));
  }

  /**
   * Reactiva el {@link FuenteFinanciacion}.
   *
   * @param id Id del {@link FuenteFinanciacion}.
   * @return la entidad {@link FuenteFinanciacion} persistida.
   */
  @Override
  @Transactional
  public FuenteFinanciacion enable(Long id) {
    log.debug("enable(Long id) - start");

    Assert.notNull(id, "FuenteFinanciacion id no puede ser null para reactivar un FuenteFinanciacion");

    return repository.findById(id).map(fuenteFinanciacion -> {
      if (fuenteFinanciacion.getActivo()) {
        // Si esta activo no se hace nada
        return fuenteFinanciacion;
      }

      repository.findByNombreAndActivoIsTrue(fuenteFinanciacion.getNombre())
          .ifPresent((fuenteFinanciacionExistente) -> {
            Assert.isTrue(fuenteFinanciacion.getId() == fuenteFinanciacionExistente.getId(),
                "Ya existe un FuenteFinanciacion con el nombre " + fuenteFinanciacion.getNombre());
          });

      fuenteFinanciacion.setActivo(true);

      FuenteFinanciacion returnValue = repository.save(fuenteFinanciacion);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new FuenteFinanciacionNotFoundException(id));
  }

  /**
   * Desactiva el {@link FuenteFinanciacion}.
   *
   * @param id Id del {@link FuenteFinanciacion}.
   * @return la entidad {@link FuenteFinanciacion} persistida.
   */
  @Override
  @Transactional
  public FuenteFinanciacion disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "FuenteFinanciacion id no puede ser null para desactivar un FuenteFinanciacion");

    return repository.findById(id).map(fuenteFinanciacion -> {
      if (!fuenteFinanciacion.getActivo()) {
        // Si no esta activo no se hace nada
        return fuenteFinanciacion;
      }

      fuenteFinanciacion.setActivo(false);

      FuenteFinanciacion returnValue = repository.save(fuenteFinanciacion);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new FuenteFinanciacionNotFoundException(id));
  }

  /**
   * Obtener todas las entidades {@link FuenteFinanciacion} activos paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link FuenteFinanciacion} paginadas y/o
   *         filtradas.
   */
  @Override
  public Page<FuenteFinanciacion> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<FuenteFinanciacion> specs = FuenteFinanciacionSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<FuenteFinanciacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link FuenteFinanciacion} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link FuenteFinanciacion} paginadas y/o
   *         filtradas.
   */
  @Override
  public Page<FuenteFinanciacion> findAllTodos(String query, Pageable pageable) {
    log.debug("findAllTodos(String query, Pageable pageable) - start");
    Specification<FuenteFinanciacion> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<FuenteFinanciacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllTodos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link FuenteFinanciacion} por su id.
   *
   * @param id el id de la entidad {@link FuenteFinanciacion}.
   * @return la entidad {@link FuenteFinanciacion}.
   */
  @Override
  public FuenteFinanciacion findById(Long id) {
    log.debug("findById(Long id)  - start");
    final FuenteFinanciacion returnValue = repository.findById(id)
        .orElseThrow(() -> new FuenteFinanciacionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

}
