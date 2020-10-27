package org.crue.hercules.sgi.csp.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.enums.TipoJustificacionEnum;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaPeriodoJustificacionSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPeriodoJustificacionService;
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
 * Service Implementation para la gestión de
 * {@link ConvocatoriaPeriodoJustificacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaPeriodoJustificacionServiceImpl implements ConvocatoriaPeriodoJustificacionService {

  private final ConvocatoriaPeriodoJustificacionRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;

  public ConvocatoriaPeriodoJustificacionServiceImpl(
      ConvocatoriaPeriodoJustificacionRepository convocatoriaPeriodoJustificacionRepository,
      ConvocatoriaRepository convocatoriaRepository) {
    this.repository = convocatoriaPeriodoJustificacionRepository;
    this.convocatoriaRepository = convocatoriaRepository;
  }

  /**
   * Guardar un nuevo {@link ConvocatoriaPeriodoJustificacion}.
   *
   * @param convocatoriaPeriodoJustificacion la entidad
   *                                         {@link ConvocatoriaPeriodoJustificacion}
   *                                         a guardar.
   * @return la entidad {@link ConvocatoriaPeriodoJustificacion} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaPeriodoJustificacion create(ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion) {
    log.debug("create(ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion) - start");

    Assert.isNull(convocatoriaPeriodoJustificacion.getId(),
        "ConvocatoriaPeriodoJustificacion id tiene que ser null para crear un nuevo ConvocatoriaPeriodoJustificacion");

    Assert.notNull(convocatoriaPeriodoJustificacion.getConvocatoria().getId(),
        "Id Convocatoria no puede ser null para crear ConvocatoriaPeriodoJustificacion");

    Assert.isTrue(convocatoriaPeriodoJustificacion.getMesInicial() < convocatoriaPeriodoJustificacion.getMesFinal(),
        "El mes final tiene que ser posterior al mes inicial");

    if (convocatoriaPeriodoJustificacion.getFechaInicioPresentacion() != null
        && convocatoriaPeriodoJustificacion.getFechaFinPresentacion() != null) {
      Assert.isTrue(
          convocatoriaPeriodoJustificacion.getFechaInicioPresentacion()
              .isBefore(convocatoriaPeriodoJustificacion.getFechaFinPresentacion()),
          "La fecha de fin tiene que ser posterior a la fecha de inicio");
    }

    convocatoriaPeriodoJustificacion.setConvocatoria(
        convocatoriaRepository.findById(convocatoriaPeriodoJustificacion.getConvocatoria().getId()).orElseThrow(
            () -> new ConvocatoriaNotFoundException(convocatoriaPeriodoJustificacion.getConvocatoria().getId())));

    Assert.isTrue(
        convocatoriaPeriodoJustificacion.getConvocatoria().getDuracion() == null || convocatoriaPeriodoJustificacion
            .getMesFinal() <= convocatoriaPeriodoJustificacion.getConvocatoria().getDuracion(),
        "El mes final no puede ser superior a la duración en meses indicada en la Convocatoria");

    repository.findAllByConvocatoriaIdAndTipoJustificacion(convocatoriaPeriodoJustificacion.getConvocatoria().getId(),
        TipoJustificacionEnum.FINAL).stream().findFirst().ifPresent(convocatoriaPeriodoJustificacionFinal -> {
          Assert.isTrue(convocatoriaPeriodoJustificacion.getTipoJustificacion().equals(TipoJustificacionEnum.PERIODICA),
              "La convocatoria ya tiene un ConvocatoriaPeriodoJustificacion de tipo final");

          Assert.isTrue(
              convocatoriaPeriodoJustificacionFinal.getMesFinal() > convocatoriaPeriodoJustificacion.getMesInicial(),
              "El ConvocatoriaPeriodoJustificacion de tipo final tiene que ser el último");
        });

    Assert.isTrue(
        hasConvocatoriaPeriodoJustificacionMesesSolapados(null,
            convocatoriaPeriodoJustificacion.getConvocatoria().getId(),
            convocatoriaPeriodoJustificacion.getMesInicial(), convocatoriaPeriodoJustificacion.getMesFinal()),
        "El periodo se solapa con otro existente");

    // Recupera la lista ConvocatoriaPeriodoJustificacion de la convocatoria y le
    // añade el nuevo para reordenarlos
    List<ConvocatoriaPeriodoJustificacion> listaConvocatoriaPeriodoJustificacionActualizar = repository
        .findAllByConvocatoriaId(convocatoriaPeriodoJustificacion.getConvocatoria().getId());
    listaConvocatoriaPeriodoJustificacionActualizar.add(convocatoriaPeriodoJustificacion);

    List<ConvocatoriaPeriodoJustificacion> listaConvocatoriaPeriodoJustificacionActualizados = repository
        .saveAll(recalcularSecuenciaNumPeriodo(listaConvocatoriaPeriodoJustificacionActualizar));

    ConvocatoriaPeriodoJustificacion returnValue = listaConvocatoriaPeriodoJustificacionActualizados.stream()
        .max(Comparator.comparing(ConvocatoriaPeriodoJustificacion::getId)).get();

    log.debug("create(ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ConvocatoriaPeriodoJustificacion}.
   *
   * @param convocatoriaPeriodoJustificacionActualizar la entidad
   *                                                   {@link ConvocatoriaPeriodoJustificacion}
   *                                                   a actualizar.
   * @return la entidad {@link ConvocatoriaPeriodoJustificacion} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaPeriodoJustificacion update(
      ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionActualizar) {
    log.debug("update(ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionActualizar) - start");

    Assert.notNull(convocatoriaPeriodoJustificacionActualizar.getId(),
        "ConvocatoriaPeriodoJustificacion id no puede ser null para actualizar un ConvocatoriaPeriodoJustificacion");

    Assert.notNull(convocatoriaPeriodoJustificacionActualizar.getConvocatoria().getId(),
        "Id Convocatoria no puede ser null para crear ConvocatoriaPeriodoJustificacion");

    Assert.isTrue(convocatoriaPeriodoJustificacionActualizar
        .getMesInicial() < convocatoriaPeriodoJustificacionActualizar.getMesFinal(),
        "El mes final tiene que ser posterior al mes inicial");

    if (convocatoriaPeriodoJustificacionActualizar.getFechaInicioPresentacion() != null
        && convocatoriaPeriodoJustificacionActualizar.getFechaFinPresentacion() != null) {
      Assert.isTrue(
          convocatoriaPeriodoJustificacionActualizar.getFechaInicioPresentacion()
              .isBefore(convocatoriaPeriodoJustificacionActualizar.getFechaFinPresentacion()),
          "La fecha de fin tiene que ser posterior a la fecha de inicio");
    }

    convocatoriaPeriodoJustificacionActualizar.setConvocatoria(
        convocatoriaRepository.findById(convocatoriaPeriodoJustificacionActualizar.getConvocatoria().getId())
            .orElseThrow(() -> new ConvocatoriaNotFoundException(
                convocatoriaPeriodoJustificacionActualizar.getConvocatoria().getId())));

    Assert.isTrue(
        convocatoriaPeriodoJustificacionActualizar.getConvocatoria().getDuracion() == null
            || convocatoriaPeriodoJustificacionActualizar.getMesFinal() <= convocatoriaPeriodoJustificacionActualizar
                .getConvocatoria().getDuracion(),
        "El mes final no puede ser superior a la duración en meses indicada en la Convocatoria");

    return repository.findById(convocatoriaPeriodoJustificacionActualizar.getId())
        .map(convocatoriaPeriodoJustificacion -> {

          boolean recalcularNumPeriodos = false;

          if (convocatoriaPeriodoJustificacionActualizar.getMesInicial() != convocatoriaPeriodoJustificacion
              .getMesInicial()
              || convocatoriaPeriodoJustificacionActualizar.getMesFinal() != convocatoriaPeriodoJustificacion
                  .getMesFinal()) {
            Assert.isTrue(
                hasConvocatoriaPeriodoJustificacionMesesSolapados(convocatoriaPeriodoJustificacion.getId(),
                    convocatoriaPeriodoJustificacion.getConvocatoria().getId(),
                    convocatoriaPeriodoJustificacion.getMesInicial(), convocatoriaPeriodoJustificacion.getMesFinal()),
                "El periodo se solapa con otro existente");

            if (convocatoriaPeriodoJustificacionActualizar.getMesInicial() != convocatoriaPeriodoJustificacion
                .getMesInicial()) {
              recalcularNumPeriodos = true;
            }
          }

          convocatoriaPeriodoJustificacion.setMesInicial(convocatoriaPeriodoJustificacionActualizar.getMesInicial());
          convocatoriaPeriodoJustificacion.setMesFinal(convocatoriaPeriodoJustificacionActualizar.getMesFinal());
          convocatoriaPeriodoJustificacion
              .setFechaInicioPresentacion(convocatoriaPeriodoJustificacionActualizar.getFechaInicioPresentacion());
          convocatoriaPeriodoJustificacion
              .setFechaFinPresentacion(convocatoriaPeriodoJustificacionActualizar.getFechaFinPresentacion());
          convocatoriaPeriodoJustificacion
              .setObservaciones(convocatoriaPeriodoJustificacionActualizar.getObservaciones());

          ConvocatoriaPeriodoJustificacion returnValue = repository.save(convocatoriaPeriodoJustificacion);

          if (recalcularNumPeriodos) {
            List<ConvocatoriaPeriodoJustificacion> listaConvocatoriaPeriodoJustificacionActualizar = recalcularSecuenciaNumPeriodo(
                repository.findAllByConvocatoriaId(convocatoriaPeriodoJustificacion.getConvocatoria().getId()));

            listaConvocatoriaPeriodoJustificacionActualizar.stream()
                .filter(
                    periodoActualizado -> periodoActualizado.getTipoJustificacion().equals(TipoJustificacionEnum.FINAL))
                .findFirst().ifPresent(convocatoriaPeriodoJustificacionFinal -> {
                  Assert.isTrue(
                      listaConvocatoriaPeriodoJustificacionActualizar.indexOf(
                          convocatoriaPeriodoJustificacionFinal) == (listaConvocatoriaPeriodoJustificacionActualizar
                              .size() - 1),
                      "El ConvocatoriaPeriodoJustificacion de tipo final tiene que ser el último");
                });

            repository.saveAll(listaConvocatoriaPeriodoJustificacionActualizar);

            returnValue = listaConvocatoriaPeriodoJustificacionActualizar.stream()
                .filter(periodoActualizado -> periodoActualizado.getId() == convocatoriaPeriodoJustificacion.getId())
                .findFirst().get();
          }

          log.debug("update(ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionActualizar) - end");
          return returnValue;
        }).orElseThrow(() -> new ConvocatoriaPeriodoJustificacionNotFoundException(
            convocatoriaPeriodoJustificacionActualizar.getId()));
  }

  /**
   * Elimina el {@link ConvocatoriaPeriodoJustificacion}.
   *
   * @param id Id del {@link ConvocatoriaPeriodoJustificacion}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        "ConvocatoriaPeriodoJustificacion id no puede ser null para desactivar un ConvocatoriaPeriodoJustificacion");

    repository.findById(id).map(periodoEliminar -> {

      repository.deleteById(id);

      List<ConvocatoriaPeriodoJustificacion> periodosActualizados = repository
          .findAllByConvocatoriaIdAndNumPeriodoGreaterThan(id, periodoEliminar.getNumPeriodo()).stream()
          .map(convocatoriaPeriodoJustificacion -> {
            convocatoriaPeriodoJustificacion.setNumPeriodo(convocatoriaPeriodoJustificacion.getNumPeriodo() - 1);
            return convocatoriaPeriodoJustificacion;
          }).collect(Collectors.toList());

      repository.saveAll(periodosActualizados);

      log.debug("delete(Long id) - end");
      return periodoEliminar;
    }).orElseThrow(() -> new ConvocatoriaPeriodoJustificacionNotFoundException(id));

    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ConvocatoriaPeriodoJustificacion} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaPeriodoJustificacion}.
   * @return la entidad {@link ConvocatoriaPeriodoJustificacion}.
   */
  @Override
  public ConvocatoriaPeriodoJustificacion findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaPeriodoJustificacion returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaPeriodoJustificacionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ConvocatoriaPeriodoJustificacion} para una
   * {@link Convocatoria}.
   *
   * @param idConvocatoria el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaPeriodoJustificacion} de la
   *         {@link Convocatoria} paginadas.
   */
  public Page<ConvocatoriaPeriodoJustificacion> findAllByConvocatoria(Long idConvocatoria, List<QueryCriteria> query,
      Pageable pageable) {
    log.debug("findAllByConvocatoria(Long idConvocatoria, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<ConvocatoriaPeriodoJustificacion> specByQuery = new QuerySpecification<ConvocatoriaPeriodoJustificacion>(
        query);
    Specification<ConvocatoriaPeriodoJustificacion> specByConvocatoria = ConvocatoriaPeriodoJustificacionSpecifications
        .byConvocatoriaId(idConvocatoria);

    Specification<ConvocatoriaPeriodoJustificacion> specs = Specification.where(specByConvocatoria).and(specByQuery);

    Page<ConvocatoriaPeriodoJustificacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoria(Long idConvocatoria, List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Comprueba si existe algun {@link ConvocatoriaPeriodoJustificacion} con meses
   * solapados a los meses del {@link ConvocatoriaPeriodoJustificacion} indicado
   *
   * @param id             identificador de la
   *                       {@link ConvocatoriaPeriodoJustificacion}.
   * @param idConvocatoria identificador de la {@link Convocatoria}.
   * @param mesInicial     mes inicial
   * @param mesFinal       mes final
   * @return true si existe algun {@link ConvocatoriaPeriodoJustificacion} con
   *         meses solapados a los meses del
   *         {@link ConvocatoriaPeriodoJustificacion} indicado
   */

  private boolean hasConvocatoriaPeriodoJustificacionMesesSolapados(Long id, Long idConvocatoria, Integer mesInicial,
      Integer mesFinal) {
    log.debug(
        "hasConvocatoriaPeriodoJustificacionMesesSolapados(Long id, Long idConvocatoria, Integer mesInicial, Integer mesFinal) - start");
    Specification<ConvocatoriaPeriodoJustificacion> spec = ConvocatoriaPeriodoJustificacionSpecifications
        .byConvocatoriaAndRangoMesesSolapados(id, idConvocatoria, mesInicial, mesFinal);

    boolean returnValue = repository.findAll(spec, Pageable.unpaged()).isEmpty();

    log.debug(
        "hasConvocatoriaPeriodoJustificacionMesesSolapados(Long id, Long idConvocatoria, Integer mesInicial, Integer mesFinal) - end");
    return returnValue;

  }

  /**
   * Reasigna la secuencia con el número del periodo según el orden del mes
   * inicial a los {@link ConvocatoriaPeriodoJustificacion} de la lista
   * 
   * @param listaConvocatoriaPeriodoJustificacion lista de
   *                                              {@link ConvocatoriaPeriodoJustificacion}
   * @return lista de {@link ConvocatoriaPeriodoJustificacion} con los números de
   *         periodo actualizados
   */
  private List<ConvocatoriaPeriodoJustificacion> recalcularSecuenciaNumPeriodo(
      List<ConvocatoriaPeriodoJustificacion> listaConvocatoriaPeriodoJustificacion) {
    log.debug(
        "List<ConvocatoriaPeriodoJustificacion> recalcularSecuenciaNumPeriodo(List<ConvocatoriaPeriodoJustificacion> listaConvocatoriaPeriodoJustificacion - start");
    listaConvocatoriaPeriodoJustificacion.sort(Comparator.comparing(ConvocatoriaPeriodoJustificacion::getMesInicial));
    AtomicInteger numPeriodo = new AtomicInteger(0);
    listaConvocatoriaPeriodoJustificacion.stream().map(ConvocatoriaPeriodoSeguimientoCientifico -> {
      ConvocatoriaPeriodoSeguimientoCientifico.setNumPeriodo(numPeriodo.incrementAndGet());
      return ConvocatoriaPeriodoSeguimientoCientifico;
    }).collect(Collectors.toList());
    log.debug(
        "List<ConvocatoriaPeriodoJustificacion> recalcularSecuenciaNumPeriodo(List<ConvocatoriaPeriodoJustificacion> listaConvocatoriaPeriodoJustificacion - end");
    return listaConvocatoriaPeriodoJustificacion;
  }

}
