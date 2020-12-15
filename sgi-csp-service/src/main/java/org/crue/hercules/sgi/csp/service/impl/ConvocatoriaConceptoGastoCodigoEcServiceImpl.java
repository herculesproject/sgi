package org.crue.hercules.sgi.csp.service.impl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaConceptoGastoCodigoEcWithEnableAccion;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaConceptoGastoCodigoEcNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.mapper.ConvocatoriaConceptoGastoCodigoEcMapper;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoCodigoEcRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaConceptoGastoCodigoEcSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaConceptoGastoCodigoEcService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
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
  private final ConvocatoriaService convocatoriaService;

  public ConvocatoriaConceptoGastoCodigoEcServiceImpl(ConvocatoriaConceptoGastoCodigoEcRepository repository,
      ConvocatoriaConceptoGastoRepository convocatoriaConceptoGastoRepository,
      ConvocatoriaConceptoGastoCodigoEcMapper convocatoriaConceptoGastoCodigoEcMapper,
      ConvocatoriaService convocatoriaService) {
    this.repository = repository;
    this.convocatoriaConceptoGastoRepository = convocatoriaConceptoGastoRepository;
    this.convocatoriaConceptoGastoCodigoEcMapper = convocatoriaConceptoGastoCodigoEcMapper;
    this.convocatoriaService = convocatoriaService;
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

    // Si no es modificable no se podrán solapar las fechas
    Assert.isTrue(esModificable(convocatoriaConceptoGastoCodigoEc),
        "No se puede crear ConvocatoriaConceptoGastoCodigoEc. Las fechas se solapan con otras existentes y no tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");

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

          // Si no es modificable solo se permitirá cambiar las fechas
          boolean esConvocatoriaModificable = convocatoriaService.modificable(
              convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto().getConvocatoria().getId(),
              convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto().getConvocatoria().getUnidadGestionRef());

          if (!esConvocatoriaModificable) {
            Assert.isTrue(
                convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto().getId()
                    .equals(convocatoriaConceptoGastoCodigoEcActualizar.getConvocatoriaConceptoGasto().getId())
                    && StringUtils.equals(convocatoriaConceptoGastoCodigoEc.getCodigoEconomicoRef(),
                        convocatoriaConceptoGastoCodigoEcActualizar.getCodigoEconomicoRef())
                    && StringUtils.equals(convocatoriaConceptoGastoCodigoEc.getObservaciones(),
                        convocatoriaConceptoGastoCodigoEcActualizar.getObservaciones()),
                "No se puede modificar ConvocatoriaConceptoGastoCodigoEc. Solo está permitido modificar las fechas de inicio y fin. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");

            // Si no es modificable no se podrán solapar las fechas
            if (convocatoriaConceptoGastoCodigoEcActualizar.getFechaInicio() != null
                && convocatoriaConceptoGastoCodigoEcActualizar.getFechaFin() != null) {
              Assert.isTrue(!this.existenFechasSolapadas(
                  convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto().getConvocatoria().getId(),
                  convocatoriaConceptoGastoCodigoEc.getFechaInicio(), convocatoriaConceptoGastoCodigoEc.getFechaFin(),
                  Arrays.asList(convocatoriaConceptoGastoCodigoEc.getId())),
                  "No se puede modificar ConvocatoriaConceptoGastoCodigoEc. Las fechas se solapan con otras existentes y no tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");
            }
          }

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

    repository.findById(id).map(convocatoriaConceptoGastoCodigoEc -> {

      // comprobar si convocatoria es modificable
      Assert
          .isTrue(
              convocatoriaService.modificable(
                  convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto().getConvocatoria().getId(),
                  convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto().getConvocatoria()
                      .getUnidadGestionRef()),
              "No se puede eliminar ConvocatoriaConceptoGastoCodigoEc. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");

      return convocatoriaConceptoGastoCodigoEc;
    }).orElseThrow(() -> new ConvocatoriaConceptoGastoCodigoEcNotFoundException(id));

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

  /**
   * Comprueba que existen {@link ConvocatoriaConceptoGastoCodigoEc} activos para
   * una {@link Convocatoria} con las fechas solapadas. Solo se comprueban
   * aquellos que tienen valores en las fechas de inicio y de fin
   * 
   * @param convocatoriaId id {@link Convocatoria}
   * @param fechaInicio    fecha inicial
   * @param fechaFin       fecha final
   * @param excluirId      identificadores a excluir de la busqueda
   * @return
   */
  private boolean existenFechasSolapadas(Long convocatoriaId, LocalDate fechaInicio, LocalDate fechaFin,
      List<Long> excluidos) {
    log.debug("existenFechasSolapadas(Long convocatoriaId, LocalDate fechaInicio, LocalDate fechaFin) - start");

    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConvocatoria = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConvocatoria(convocatoriaId);
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByConceptoGastoActivo = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byConceptoGastoActivo();
    Specification<ConvocatoriaConceptoGastoCodigoEc> specByRangoFechaSolapados = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .byRangoFechaSolapados(fechaInicio, fechaFin);
    Specification<ConvocatoriaConceptoGastoCodigoEc> specWithFechas = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .withFechas();
    Specification<ConvocatoriaConceptoGastoCodigoEc> specExcluidos = ConvocatoriaConceptoGastoCodigoEcSpecifications
        .notIn(excluidos);

    Specification<ConvocatoriaConceptoGastoCodigoEc> specs = Specification.where(specByConvocatoria).and(specExcluidos)
        .and(specByConceptoGastoActivo).and(specWithFechas).and(specByRangoFechaSolapados);

    Page<ConvocatoriaConceptoGastoCodigoEc> listaSolapados = repository.findAll(specs, Pageable.unpaged());
    Boolean returnValue = !listaSolapados.isEmpty();

    log.debug("existenFechasSolapadas(Long convocatoriaId, LocalDate fechaInicio, LocalDate fechaFin) - end");
    return returnValue;
  }

  /**
   * Comprueba si es posible modificar las fechas de la
   * {@link ConvocatoriaConceptoGastoCodigoEc}. Cuando la {@link Convocatoria} no
   * es modificable solamente se podrán modificar cuando no se solapen con otras
   * existentes. Para hacer esa comprobación tanto la fecha de inicio como la
   * fecha de fin tienen que tener valor.
   * 
   * @param convocatoriaConceptoGastoCodigoEc
   * @return
   */
  private boolean esModificable(ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc) {
    Boolean returnValue = Boolean.TRUE;

    if (convocatoriaConceptoGastoCodigoEc.getFechaInicio() != null
        && convocatoriaConceptoGastoCodigoEc.getFechaFin() != null) {

      boolean esConvocatoriaModificable = convocatoriaService.modificable(
          convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto().getConvocatoria().getId(),
          convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto().getConvocatoria().getUnidadGestionRef());

      if (!esConvocatoriaModificable) {
        returnValue = !this.existenFechasSolapadas(
            convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto().getConvocatoria().getId(),
            convocatoriaConceptoGastoCodigoEc.getFechaInicio(), convocatoriaConceptoGastoCodigoEc.getFechaFin(),
            Arrays.asList(convocatoriaConceptoGastoCodigoEc.getId()));
      }
    }
    return returnValue;
  }

}
