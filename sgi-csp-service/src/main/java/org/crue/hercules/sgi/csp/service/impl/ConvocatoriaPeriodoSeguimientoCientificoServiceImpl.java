package org.crue.hercules.sgi.csp.service.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaPeriodoSeguimientoCientificoNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoSeguimientoCientificoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaPeriodoSeguimientoCientificoSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPeriodoSeguimientoCientificoService;
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
 * Service Implementation para gestion
 * {@link ConvocatoriaPeriodoSeguimientoCientifico}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaPeriodoSeguimientoCientificoServiceImpl
    implements ConvocatoriaPeriodoSeguimientoCientificoService {

  private final ConvocatoriaPeriodoSeguimientoCientificoRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;

  public ConvocatoriaPeriodoSeguimientoCientificoServiceImpl(
      ConvocatoriaPeriodoSeguimientoCientificoRepository repository, ConvocatoriaRepository convocatoriaRepository) {
    this.repository = repository;
    this.convocatoriaRepository = convocatoriaRepository;
  }

  /**
   * Guarda la entidad {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   * 
   * @param convocatoriaPeriodoSeguimientoCientifico la entidad
   *                                                 {@link ConvocatoriaPeriodoSeguimientoCientifico}
   *                                                 a guardar.
   * @return ConvocatoriaPeriodoSeguimientoCientifico la entidad
   *         {@link ConvocatoriaPeriodoSeguimientoCientifico} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaPeriodoSeguimientoCientifico create(
      ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico) {
    log.debug("create(ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico) - start");

    Assert.isNull(convocatoriaPeriodoSeguimientoCientifico.getId(),
        "Id tiene que ser null para crear ConvocatoriaPeriodoSeguimientoCientifico");

    // Lista de periodos validados
    List<ConvocatoriaPeriodoSeguimientoCientifico> validListConvocatoriaPeriodoSeguimientoCientifico = validarDatosConvocatoriaPeriodoSeguimientoCientifico(
        convocatoriaPeriodoSeguimientoCientifico, null);

    repository.saveAll(validListConvocatoriaPeriodoSeguimientoCientifico);

    log.debug("create(ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico) - end");
    return convocatoriaPeriodoSeguimientoCientifico;
  }

  /**
   * Actualiza los datos del {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   * 
   * @param convocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientificoActualizar
   *                                                 {@link ConvocatoriaPeriodoSeguimientoCientifico}
   *                                                 con los datos actualizados.
   * @return {@link ConvocatoriaPeriodoSeguimientoCientifico} actualizado.
   */
  @Override
  @Transactional
  public ConvocatoriaPeriodoSeguimientoCientifico update(
      ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico) {
    log.debug("update(ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico) - start");

    Assert.notNull(convocatoriaPeriodoSeguimientoCientifico.getId(),
        "Id no puede ser null para actualizar ConvocatoriaPeriodoSeguimientoCientifico");

    return repository.findById(convocatoriaPeriodoSeguimientoCientifico.getId()).map((data) -> {

      List<ConvocatoriaPeriodoSeguimientoCientifico> validListConvocatoriaPeriodoSeguimientoCientifico = validarDatosConvocatoriaPeriodoSeguimientoCientifico(
          convocatoriaPeriodoSeguimientoCientifico, data);

      repository.saveAll(validListConvocatoriaPeriodoSeguimientoCientifico);

      log.debug("update(ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico) - end");
      return data;
    }).orElseThrow(() -> new ConvocatoriaPeriodoSeguimientoCientificoNotFoundException(
        convocatoriaPeriodoSeguimientoCientifico.getId()));
  }

  /**
   * Elimina la {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   *
   * @param id Id del {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        "ConvocatoriaPeriodoSeguimientoCientifico id no puede ser null para eliminar un ConvocatoriaPeriodoSeguimientoCientifico");

    final ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaPeriodoSeguimientoCientificoNotFoundException(id));

    repository.deleteById(id);

    // recalcular numPeriodo
    List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientifico = repository
        .findAllByConvocatoriaIdOrderByMesInicial(convocatoriaPeriodoSeguimientoCientifico.getConvocatoria().getId());

    repository.saveAll(recalcularSecuenciaNumPeriodo(listaConvocatoriaPeriodoSeguimientoCientifico));

    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene una entidad {@link ConvocatoriaPeriodoSeguimientoCientifico} por id.
   * 
   * @param id Identificador de la entidad
   *           {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   * @return ConvocatoriaPeriodoSeguimientoCientifico la entidad
   *         {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   */
  @Override
  public ConvocatoriaPeriodoSeguimientoCientifico findById(Long id) {
    log.debug("findById(Long id) - start");
    final ConvocatoriaPeriodoSeguimientoCientifico returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaPeriodoSeguimientoCientificoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ConvocatoriaPeriodoSeguimientoCientifico} para una
   * {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades
   *         {@link ConvocatoriaPeriodoSeguimientoCientifico} de la
   *         {@link Convocatoria} paginadas.
   */
  public Page<ConvocatoriaPeriodoSeguimientoCientifico> findAllByConvocatoria(Long convocatoriaId,
      List<QueryCriteria> query, Pageable pageable) {
    log.debug("findAllByConvocatoria(Long convocatoriaId, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<ConvocatoriaPeriodoSeguimientoCientifico> specByQuery = new QuerySpecification<ConvocatoriaPeriodoSeguimientoCientifico>(
        query);
    Specification<ConvocatoriaPeriodoSeguimientoCientifico> specByConvocatoria = ConvocatoriaPeriodoSeguimientoCientificoSpecifications
        .byConvocatoriaId(convocatoriaId);

    Specification<ConvocatoriaPeriodoSeguimientoCientifico> specs = Specification.where(specByConvocatoria)
        .and(specByQuery);

    Page<ConvocatoriaPeriodoSeguimientoCientifico> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoria(Long convocatoriaId, List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Comprueba y valida y reordena los periodos retornando una lista con los datos
   * {@link ConvocatoriaPeriodoSeguimientoCientifico} que deben ser guardados.
   * 
   * @param datosConvocatoriaPeriodoSeguimientoCientifico
   * @return lista de {@link ConvocatoriaPeriodoSeguimientoCientifico} con los
   *         datos validados y listos para ser guardados.
   */
  private List<ConvocatoriaPeriodoSeguimientoCientifico> validarDatosConvocatoriaPeriodoSeguimientoCientifico(
      ConvocatoriaPeriodoSeguimientoCientifico datosConvocatoriaPeriodoSeguimientoCientifico,
      ConvocatoriaPeriodoSeguimientoCientifico datosOriginales) {
    log.debug(
        "List<ConvocatoriaPeriodoSeguimientoCientifico> validarDatosConvocatoriaPeriodoSeguimientoCientifico( ConvocatoriaPeriodoSeguimientoCientifico datosConvocatoriaPeriodoSeguimientoCientifico, ConvocatoriaPeriodoSeguimientoCientifico datosOriginales) - start");

    // Convocatoria
    Assert.notNull(datosConvocatoriaPeriodoSeguimientoCientifico.getConvocatoria(),
        "Convocatoria no puede ser null en ConvocatoriaPeriodoSeguimientoCientifico");

    // Mes Inicial < Mes Final
    Assert
        .isTrue(
            (datosConvocatoriaPeriodoSeguimientoCientifico
                .getMesInicial() < datosConvocatoriaPeriodoSeguimientoCientifico.getMesFinal()),
            "El mes inicial debe ser anterior al mes final");

    // Fecha Inicio < Fecha Fin
    if (datosConvocatoriaPeriodoSeguimientoCientifico.getFechaInicio() != null
        && datosConvocatoriaPeriodoSeguimientoCientifico.getFechaFin() != null) {
      Assert.isTrue(
          datosConvocatoriaPeriodoSeguimientoCientifico.getFechaInicio()
              .isBefore(datosConvocatoriaPeriodoSeguimientoCientifico.getFechaFin()),
          "La fecha de inicio debe ser anterior a la fecha de fin");
    }

    // Convocatoria
    datosConvocatoriaPeriodoSeguimientoCientifico.setConvocatoria(
        convocatoriaRepository.findById(datosConvocatoriaPeriodoSeguimientoCientifico.getConvocatoria().getId())
            .orElseThrow(() -> new ConvocatoriaNotFoundException(
                datosConvocatoriaPeriodoSeguimientoCientifico.getConvocatoria().getId())));

    // duración en convocatoria < max mesFin de la convocatoria
    if (datosConvocatoriaPeriodoSeguimientoCientifico.getConvocatoria().getDuracion() != null) {
      Assert.isTrue(
          (datosConvocatoriaPeriodoSeguimientoCientifico.getConvocatoria()
              .getDuracion() > datosConvocatoriaPeriodoSeguimientoCientifico.getMesFinal()),
          "La duración en meses de la convocatoria es inferior al periodo");
    }

    // lista periodos de la convocatoria
    List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientifico = repository
        .findAllByConvocatoriaIdOrderByMesInicial(
            datosConvocatoriaPeriodoSeguimientoCientifico.getConvocatoria().getId());

    // se elimina de la lista el que se está editando
    listaConvocatoriaPeriodoSeguimientoCientifico
        .removeIf(item -> item.getId().equals(datosConvocatoriaPeriodoSeguimientoCientifico.getId()));

    Assert.isTrue(!listaConvocatoriaPeriodoSeguimientoCientifico.stream().anyMatch(t -> {
      Boolean result = (datosConvocatoriaPeriodoSeguimientoCientifico.getMesInicial() <= t.getMesFinal())
          && (t.getMesInicial() <= datosConvocatoriaPeriodoSeguimientoCientifico.getMesFinal());
      return result;
    }), "El periodo se solapa con otro existente");

    // Si no hay cambio de MesInicial no es necesario recálculo
    if (datosOriginales != null
        && datosOriginales.getMesInicial() == datosConvocatoriaPeriodoSeguimientoCientifico.getMesInicial()) {
      log.debug(
          "List<ConvocatoriaPeriodoSeguimientoCientifico> validarDatosConvocatoriaPeriodoSeguimientoCientifico(ConvocatoriaPeriodoSeguimientoCientifico datosConvocatoriaPeriodoSeguimientoCientifico, ConvocatoriaPeriodoSeguimientoCientifico datosOriginales) - end");
      return Arrays.asList(datosConvocatoriaPeriodoSeguimientoCientifico);
    }
    // Se añade el periodo que se está añadiendo o editando a la lista a actualizar
    listaConvocatoriaPeriodoSeguimientoCientifico.add(datosConvocatoriaPeriodoSeguimientoCientifico);

    List<ConvocatoriaPeriodoSeguimientoCientifico> returnValue = recalcularSecuenciaNumPeriodo(
        listaConvocatoriaPeriodoSeguimientoCientifico);

    log.debug(
        "List<ConvocatoriaPeriodoSeguimientoCientifico> validarDatosConvocatoriaPeriodoSeguimientoCientifico(ConvocatoriaPeriodoSeguimientoCientifico datosConvocatoriaPeriodoSeguimientoCientifico, ConvocatoriaPeriodoSeguimientoCientifico datosOriginales) - end");

    return returnValue;
  }

  /**
   * Reasigna la secuencia con el número del periodo según el orden del mes
   * inicial a todos los periodos de seguimiento de la convocatoria
   * 
   * @param listaConvocatoriaPeriodoSeguimientoCientifico
   * @return lista con los númmeros de periodo actualizados
   */
  private List<ConvocatoriaPeriodoSeguimientoCientifico> recalcularSecuenciaNumPeriodo(
      List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientifico) {
    log.debug(
        "List<ConvocatoriaPeriodoSeguimientoCientifico> recalcularSecuenciaNumPeriodo(List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientifico - start");

    listaConvocatoriaPeriodoSeguimientoCientifico
        .sort(Comparator.comparing(ConvocatoriaPeriodoSeguimientoCientifico::getMesInicial));

    AtomicInteger numPeriodo = new AtomicInteger(0);
    listaConvocatoriaPeriodoSeguimientoCientifico.stream().map(ConvocatoriaPeriodoSeguimientoCientifico -> {
      ConvocatoriaPeriodoSeguimientoCientifico.setNumPeriodo(numPeriodo.incrementAndGet());
      return ConvocatoriaPeriodoSeguimientoCientifico;
    }).collect(Collectors.toList());

    log.debug(
        "List<ConvocatoriaPeriodoSeguimientoCientifico> recalcularSecuenciaNumPeriodo(List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientifico - end");

    return listaConvocatoriaPeriodoSeguimientoCientifico;
  }
}
