package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.dto.ConvocatoriaConceptoGastoCodigoEcWithEnableAccion;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaConceptoGastoCodigoEcNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.mapper.ConvocatoriaConceptoGastoCodigoEcMapper;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoCodigoEcRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaConceptoGastoCodigoEcSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaConceptoGastoCodigoEcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para {@link ConvocatoriaConceptoGastoCodigoEc}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaConceptoGastoCodigoEcServiceImpl implements ConvocatoriaConceptoGastoCodigoEcService {

  private final ConvocatoriaConceptoGastoCodigoEcRepository repository;
  private final ConvocatoriaConceptoGastoRepository convocatoriaConceptoGastoRepository;
  private final ConvocatoriaConceptoGastoCodigoEcMapper convocatoriaConceptoGastoCodigoEcMapper;

  public ConvocatoriaConceptoGastoCodigoEcServiceImpl(ConvocatoriaConceptoGastoCodigoEcRepository repository,
      ConvocatoriaConceptoGastoRepository convocatoriaConceptoGastoRepository,
      ConvocatoriaConceptoGastoCodigoEcMapper convocatoriaConceptoGastoCodigoEcMapper) {
    this.repository = repository;
    this.convocatoriaConceptoGastoRepository = convocatoriaConceptoGastoRepository;
    this.convocatoriaConceptoGastoCodigoEcMapper = convocatoriaConceptoGastoCodigoEcMapper;
  }

  /**
   * Guarda la entidad {@link ConvocatoriaConceptoGastoCodigoEc}.
   * 
   * @param convocatoriaConceptoGastoCodigoEc la entidad
   *                                          {@link ConvocatoriaConceptoGastoCodigoEc}
   *                                          a guardar.
   * @return ConvocatoriaConceptoGastoCodigoEc la entidad
   *         {@link ConvocatoriaConceptoGastoCodigoEc} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaConceptoGastoCodigoEc create(ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc) {
    log.debug("create(ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc) - start");

    Assert.isNull(convocatoriaConceptoGastoCodigoEc.getId(),
        "Id tiene que ser null para crear ConvocatoriaConceptoGastoCodigoEc");

    Assert.notNull(convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto().getId(),
        "Id ConvocatoriaConceptoGasto no puede ser null para crear ConvocatoriaConceptoGastoCodigoEc");

    convocatoriaConceptoGastoCodigoEc.setConvocatoriaConceptoGasto(convocatoriaConceptoGastoRepository
        .findById(convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto().getId())
        .orElseThrow(() -> new ConvocatoriaConceptoGastoNotFoundException(
            convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto().getId())));

    ConvocatoriaConceptoGastoCodigoEc returnValue = repository.save(convocatoriaConceptoGastoCodigoEc);

    log.debug("create(ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc) - end");
    return returnValue;
  }

  /**
   * Actualiza la entidad {@link ConvocatoriaConceptoGastoCodigoEc}.
   * 
   * @param convocatoriaConceptoGastoCodigoEcActualizar la entidad
   *                                                    {@link ConvocatoriaConceptoGastoCodigoEc}
   *                                                    a guardar.
   * @return ConvocatoriaConceptoGastoCodigoEc la entidad
   *         {@link ConvocatoriaConceptoGastoCodigoEc} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaConceptoGastoCodigoEc update(
      ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEcActualizar) {
    log.debug("update(ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEcActualizar) - start");

    Assert.notNull(convocatoriaConceptoGastoCodigoEcActualizar.getId(),
        "ConvocatoriaConceptoGastoCodigoEc id no puede ser null para actualizar un ConvocatoriaConceptoGastoCodigoEc");

    Assert.notNull(convocatoriaConceptoGastoCodigoEcActualizar.getConvocatoriaConceptoGasto().getId(),
        "Id ConvocatoriaConceptoGasto no puede ser null para actualizar ConvocatoriaConceptoGastoCodigoEc");

    convocatoriaConceptoGastoCodigoEcActualizar.setConvocatoriaConceptoGasto(convocatoriaConceptoGastoRepository
        .findById(convocatoriaConceptoGastoCodigoEcActualizar.getConvocatoriaConceptoGasto().getId())
        .orElseThrow(() -> new ConvocatoriaConceptoGastoNotFoundException(
            convocatoriaConceptoGastoCodigoEcActualizar.getConvocatoriaConceptoGasto().getId())));

    return repository.findById(convocatoriaConceptoGastoCodigoEcActualizar.getId())
        .map(convocatoriaConceptoGastoCodigoEc -> {
          convocatoriaConceptoGastoCodigoEc
              .setConvocatoriaConceptoGasto(convocatoriaConceptoGastoCodigoEcActualizar.getConvocatoriaConceptoGasto());
          convocatoriaConceptoGastoCodigoEc
              .setCodigoEconomicoRef(convocatoriaConceptoGastoCodigoEcActualizar.getCodigoEconomicoRef());
          convocatoriaConceptoGastoCodigoEc
              .setFechaInicio(convocatoriaConceptoGastoCodigoEcActualizar.getFechaInicio());
          convocatoriaConceptoGastoCodigoEc.setFechaFin(convocatoriaConceptoGastoCodigoEcActualizar.getFechaFin());
          convocatoriaConceptoGastoCodigoEc
              .setObservaciones(convocatoriaConceptoGastoCodigoEcActualizar.getObservaciones());

          ConvocatoriaConceptoGastoCodigoEc returnValue = repository.save(convocatoriaConceptoGastoCodigoEc);
          log.debug("update(ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEcActualizar) - end");
          return returnValue;
        }).orElseThrow(() -> new ConvocatoriaConceptoGastoCodigoEcNotFoundException(
            convocatoriaConceptoGastoCodigoEcActualizar.getId()));

  }

  /**
   * Elimina la {@link ConvocatoriaConceptoGastoCodigoEc}.
   *
   * @param id Id del {@link ConvocatoriaConceptoGastoCodigoEc}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        "ConvocatoriaConceptoGastoCodigoEc id no puede ser null para eliminar un ConvocatoriaConceptoGastoCodigoEc");
    if (!repository.existsById(id)) {
      throw new ConvocatoriaConceptoGastoCodigoEcNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ConvocatoriaConceptoGastoCodigoEc} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaConceptoGastoCodigoEc}.
   * @return la entidad {@link ConvocatoriaConceptoGastoCodigoEc}.
   */
  @Override
  public ConvocatoriaConceptoGastoCodigoEc findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaConceptoGastoCodigoEc returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaConceptoGastoCodigoEcNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ConvocatoriaConceptoGastoCodigoEc} permitidos para una
   * {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaConceptoGastoCodigoEc} de la
   *         {@link Convocatoria} paginadas.
   */
  @Override
  public Page<ConvocatoriaConceptoGastoCodigoEcWithEnableAccion> findAllByConvocatoriaAndPermitidoTrue(
      Long convocatoriaId, Pageable pageable) {
    log.debug(
        "findAllByConvocatoriaAndPermitidoTrue(Long convocatoriaId, Boolean permitido, List<QueryCriteria> query, Pageable pageable)) - start");
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConvocatoria = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConvocatoria(convocatoriaId);
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConceptoGastoActivo = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConceptoGastoActivo();
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConvocatoriaConceptoGastoPermitido = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConvocatoriaConceptoGastoPermitido(true);

    Specification<ConvocatoriaConceptoGastoCodigoEc> specs = Specification.where(specByConvocatoria)
        .and(specByConceptoGastoActivo).and(specByConvocatoriaConceptoGastoPermitido);
    Page<ConvocatoriaConceptoGastoCodigoEc> returnValue = repository.findAll(specs, pageable);
    log.debug(
        "findAllByConvocatoriaAndPermitidoTrue(Long convocatoriaId, Boolean permitido, List<QueryCriteria> query, Pageable pageable) - end");
    return new PageImpl<ConvocatoriaConceptoGastoCodigoEcWithEnableAccion>(convocatoriaConceptoGastoCodigoEcMapper
        .convocatoriaConceptoGastoCodigoEcsToConvocatoriaConceptoGastoCodigoEcsWithEnableAccion(
            returnValue.getContent()),
        pageable, returnValue.getTotalElements());
  }

  /**
   * Obtiene las {@link ConvocatoriaConceptoGastoCodigoEc} NO permitidos para una
   * {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaConceptoGastoCodigoEc} de la
   *         {@link Convocatoria} paginadas.
   */
  @Override
  public Page<ConvocatoriaConceptoGastoCodigoEcWithEnableAccion> findAllByConvocatoriaAndPermitidoFalse(
      Long convocatoriaId, Pageable pageable) {
    log.debug(
        "findAllByConvocatoriaAndPermitidoFalse(Long convocatoriaId, Boolean permitido, List<QueryCriteria> query, Pageable pageable)) - start");
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConvocatoria = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConvocatoria(convocatoriaId);
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConceptoGastoActivo = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConceptoGastoActivo();
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConvocatoriaConceptoGastoPermitido = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConvocatoriaConceptoGastoPermitido(false);

    Specification<ConvocatoriaConceptoGastoCodigoEc> specs = Specification.where(specByConvocatoria)
        .and(specByConceptoGastoActivo).and(specByConvocatoriaConceptoGastoPermitido);
    Page<ConvocatoriaConceptoGastoCodigoEc> returnValue = repository.findAll(specs, pageable);
    log.debug(
        "findAllByConvocatoriaAndPermitidoFalse(Long convocatoriaId, Boolean permitido, List<QueryCriteria> query, Pageable pageable) - end");
    return new PageImpl<ConvocatoriaConceptoGastoCodigoEcWithEnableAccion>(convocatoriaConceptoGastoCodigoEcMapper
        .convocatoriaConceptoGastoCodigoEcsToConvocatoriaConceptoGastoCodigoEcsWithEnableAccion(
            returnValue.getContent()),
        pageable, returnValue.getTotalElements());
  }

}
