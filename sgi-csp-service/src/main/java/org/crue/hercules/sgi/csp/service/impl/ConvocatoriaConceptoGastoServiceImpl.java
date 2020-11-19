package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.mapper.ConvocatoriaConceptoGastoMapper;

import java.util.List;

import org.crue.hercules.sgi.csp.dto.ConvocatoriaConceptoGastoWithEnableAccion;
import org.crue.hercules.sgi.csp.exceptions.ConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.repository.ConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoCodigoEcRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.service.ConvocatoriaConceptoGastoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para {@link ConvocatoriaConceptoGasto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaConceptoGastoServiceImpl implements ConvocatoriaConceptoGastoService {

  private final ConvocatoriaConceptoGastoRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ConceptoGastoRepository conceptoGastoRepository;
  private final ConvocatoriaConceptoGastoMapper convocatoriaConceptoGastoMapper;
  private final ConvocatoriaConceptoGastoCodigoEcRepository convocatoriaConceptoGastoCodigoEcRepository;

  public ConvocatoriaConceptoGastoServiceImpl(ConvocatoriaConceptoGastoRepository repository,
      ConvocatoriaRepository convocatoriaRepository, ConceptoGastoRepository conceptoGastoRepository,
      ConvocatoriaConceptoGastoMapper convocatoriaConceptoGastoMapper,
      ConvocatoriaConceptoGastoCodigoEcRepository convocatoriaConceptoGastoCodigoEcRepository) {
    this.repository = repository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.conceptoGastoRepository = conceptoGastoRepository;
    this.convocatoriaConceptoGastoMapper = convocatoriaConceptoGastoMapper;
    this.convocatoriaConceptoGastoCodigoEcRepository = convocatoriaConceptoGastoCodigoEcRepository;
  }

  /**
   * Guarda la entidad {@link ConvocatoriaConceptoGasto}.
   * 
   * @param convocatoriaConceptoGasto la entidad {@link ConvocatoriaConceptoGasto}
   *                                  a guardar.
   * @return ConvocatoriaConceptoGasto la entidad
   *         {@link ConvocatoriaConceptoGasto} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaConceptoGasto create(ConvocatoriaConceptoGasto convocatoriaConceptoGasto) {
    log.debug("create(ConvocatoriaConceptoGasto convocatoriaConceptoGasto) - start");

    Assert.isNull(convocatoriaConceptoGasto.getId(), "Id tiene que ser null para crear ConvocatoriaConceptoGasto");

    Assert.notNull(convocatoriaConceptoGasto.getConvocatoria().getId(),
        "Id Convocatoria no puede ser null para crear ConvocatoriaConceptoGasto");

    if (convocatoriaConceptoGasto.getConceptoGasto() != null) {
      if (convocatoriaConceptoGasto.getConceptoGasto().getId() != null) {
        convocatoriaConceptoGasto.setConceptoGasto(
            conceptoGastoRepository.findById(convocatoriaConceptoGasto.getConceptoGasto().getId()).orElseThrow(
                () -> new ConceptoGastoNotFoundException(convocatoriaConceptoGasto.getConceptoGasto().getId())));
        Assert.isTrue(convocatoriaConceptoGasto.getConceptoGasto().getActivo(), "El ConceptoGasto debe estar activo");
      } else {
        convocatoriaConceptoGasto.setConceptoGasto(null);
      }
    }

    Assert
        .isTrue(!repository
            .findByConvocatoriaIdAndConceptoGastoActivoTrueAndConceptoGastoIdAndPermitidoIs(
                convocatoriaConceptoGasto.getConvocatoria().getId(),
                convocatoriaConceptoGasto.getConceptoGasto().getId(), convocatoriaConceptoGasto.getPermitido())
            .isPresent(), "Ya existe una asociación activa para esa Convocatoria y ConceptoGasto");

    convocatoriaConceptoGasto
        .setConvocatoria(convocatoriaRepository.findById(convocatoriaConceptoGasto.getConvocatoria().getId())
            .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaConceptoGasto.getConvocatoria().getId())));

    ConvocatoriaConceptoGasto returnValue = repository.save(convocatoriaConceptoGasto);

    log.debug("create(ConvocatoriaConceptoGasto convocatoriaConceptoGasto) - end");
    return returnValue;
  }

  /**
   * Actualiza la entidad {@link ConvocatoriaConceptoGasto}.
   * 
   * @param convocatoriaConceptoGastoActualizar la entidad
   *                                            {@link ConvocatoriaConceptoGasto}
   *                                            a guardar.
   * @return ConvocatoriaConceptoGasto la entidad
   *         {@link ConvocatoriaConceptoGasto} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaConceptoGasto update(ConvocatoriaConceptoGasto convocatoriaConceptoGastoActualizar) {
    log.debug("update(ConvocatoriaConceptoGasto convocatoriaConceptoGastoActualizar) - start");

    Assert.notNull(convocatoriaConceptoGastoActualizar.getId(),
        "ConvocatoriaConceptoGasto id no puede ser null para actualizar un ConvocatoriaConceptoGasto");

    Assert.notNull(convocatoriaConceptoGastoActualizar.getConvocatoria().getId(),
        "Id Convocatoria no puede ser null para actualizar ConvocatoriaConceptoGasto");

    if (convocatoriaConceptoGastoActualizar.getConceptoGasto() != null) {
      if (convocatoriaConceptoGastoActualizar.getConceptoGasto().getId() != null) {
        convocatoriaConceptoGastoActualizar.setConceptoGasto(
            conceptoGastoRepository.findById(convocatoriaConceptoGastoActualizar.getConceptoGasto().getId())
                .orElseThrow(() -> new ConceptoGastoNotFoundException(
                    convocatoriaConceptoGastoActualizar.getConceptoGasto().getId())));
      } else {
        convocatoriaConceptoGastoActualizar.setConceptoGasto(null);
      }
    }

    return repository.findById(convocatoriaConceptoGastoActualizar.getId()).map(convocatoriaConceptoGasto -> {
      convocatoriaConceptoGasto.setConceptoGasto(convocatoriaConceptoGastoActualizar.getConceptoGasto());
      convocatoriaConceptoGasto.setConvocatoria(convocatoriaConceptoGastoActualizar.getConvocatoria());
      convocatoriaConceptoGasto.setImporteMaximo(convocatoriaConceptoGastoActualizar.getImporteMaximo());
      convocatoriaConceptoGasto.setNumMeses(convocatoriaConceptoGastoActualizar.getNumMeses());
      convocatoriaConceptoGasto.setObservaciones(convocatoriaConceptoGastoActualizar.getObservaciones());
      convocatoriaConceptoGasto.setPermitido(convocatoriaConceptoGastoActualizar.getPermitido());
      convocatoriaConceptoGasto
          .setPorcentajeCosteIndirecto(convocatoriaConceptoGastoActualizar.getPorcentajeCosteIndirecto());

      ConvocatoriaConceptoGasto returnValue = repository.save(convocatoriaConceptoGasto);
      log.debug("update(ConvocatoriaConceptoGasto convocatoriaConceptoGastoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaConceptoGastoNotFoundException(convocatoriaConceptoGastoActualizar.getId()));

  }

  /**
   * Elimina la {@link ConvocatoriaConceptoGasto}.
   *
   * @param id Id del {@link ConvocatoriaConceptoGasto}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "ConvocatoriaConceptoGasto id no puede ser null para eliminar un ConvocatoriaConceptoGasto");
    if (!repository.existsById(id)) {
      throw new ConvocatoriaConceptoGastoNotFoundException(id);
    }

    List<ConvocatoriaConceptoGastoCodigoEc> codigosEconomicos = convocatoriaConceptoGastoCodigoEcRepository
        .findByConvocatoriaConceptoGastoId(id);

    if (codigosEconomicos != null) {
      codigosEconomicos.stream().forEach(codigoEconomico -> {
        convocatoriaConceptoGastoCodigoEcRepository.deleteById(codigoEconomico.getId());
      });
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ConvocatoriaConceptoGasto} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaConceptoGasto}.
   * @return la entidad {@link ConvocatoriaConceptoGasto}.
   */
  @Override
  public ConvocatoriaConceptoGasto findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaConceptoGasto returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaConceptoGastoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ConvocatoriaConceptoGasto} permitidos para una
   * {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaConceptoGasto} de la
   *         {@link Convocatoria} paginadas.
   */
  @Override
  public Page<ConvocatoriaConceptoGastoWithEnableAccion> findAllByConvocatoriaAndPermitidoTrue(Long convocatoriaId,
      Pageable pageable) {
    log.debug(
        "findAllByConvocatoriaAndPermitidoTrue(Long convocatoriaId, Boolean permitido, List<QueryCriteria> query, Pageable pageable)) - start");
    Page<ConvocatoriaConceptoGasto> returnValue = repository
        .findAllByConvocatoriaIdAndConceptoGastoActivoTrueAndPermitidoTrue(convocatoriaId, pageable);
    log.debug(
        "findAllByConvocatoriaAndPermitidoTrue(Long convocatoriaId, Boolean permitido, List<QueryCriteria> query, Pageable pageable) - end");
    return new PageImpl<ConvocatoriaConceptoGastoWithEnableAccion>(convocatoriaConceptoGastoMapper
        .convocatoriaConceptoGastosToConvocatoriaConceptoGastoesWithEnableAccion(returnValue.getContent()), pageable,
        returnValue.getTotalElements());
  }

  /**
   * Obtiene las {@link ConvocatoriaConceptoGasto} NO permitidos para una
   * {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaConceptoGasto} de la
   *         {@link Convocatoria} paginadas.
   */
  @Override
  public Page<ConvocatoriaConceptoGastoWithEnableAccion> findAllByConvocatoriaAndPermitidoFalse(Long convocatoriaId,
      Pageable pageable) {
    log.debug(
        "findAllByConvocatoriaAndPermitidoTrue(Long convocatoriaId, Boolean permitido, List<QueryCriteria> query, Pageable pageable)) - start");
    Page<ConvocatoriaConceptoGasto> returnValue = repository
        .findAllByConvocatoriaIdAndConceptoGastoActivoTrueAndPermitidoFalse(convocatoriaId, pageable);
    log.debug(
        "findAllByConvocatoriaAndPermitidoTrue(Long convocatoriaId, Boolean permitido, List<QueryCriteria> query, Pageable pageable) - end");
    return new PageImpl<ConvocatoriaConceptoGastoWithEnableAccion>(convocatoriaConceptoGastoMapper
        .convocatoriaConceptoGastosToConvocatoriaConceptoGastoesWithEnableAccion(returnValue.getContent()), pageable,
        returnValue.getTotalElements());
  }
}
