package org.crue.hercules.sgi.csp.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFaseAviso;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHitoAviso;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaFaseRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaHitoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaFaseSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaHitoSpecifications;
import org.crue.hercules.sgi.csp.util.AvisoTaskHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para la gestión conjunta de los avisos ({@link ConvocatoriaFaseAviso}
 * y {@link ConvocatoriaHitoAviso}) de una {@link Convocatoria}.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConvocatoriaAvisoService {

  private final ConvocatoriaFaseRepository convocatoriaFaseRepository;
  private final ConvocatoriaHitoRepository convocatoriaHitoRepository;
  private final AvisoTaskHelper avisoTaskHelper;

  /**
   * Activa las tareas programadas de los avisos pendientes de envío de las
   * {@link ConvocatoriaFase} y los {@link ConvocatoriaHito} de la
   * {@link Convocatoria}.
   *
   * @param convocatoriaId identificador de la {@link Convocatoria}
   */
  public void enablePendingAvisos(Long convocatoriaId) {
    log.debug("enablePendingAvisos - convocatoriaId: {}", convocatoriaId);
    avisoTaskHelper.enablePendingTasks(findTareaProgramadaRefs(convocatoriaId));
  }

  /**
   * Desactiva las tareas programadas de los avisos pendientes de envío de las
   * {@link ConvocatoriaFase} y los {@link ConvocatoriaHito} de la
   * {@link Convocatoria}, de forma que no se envían mientras sigan desactivadas.
   *
   * @param convocatoriaId identificador de la {@link Convocatoria}
   */
  public void disablePendingAvisos(Long convocatoriaId) {
    log.debug("disablePendingAvisos - convocatoriaId: {}", convocatoriaId);
    avisoTaskHelper.disablePendingTasks(findTareaProgramadaRefs(convocatoriaId));
  }

  /**
   * Obtiene las referencias de las tareas programadas de los avisos de las
   * {@link ConvocatoriaFase} y los {@link ConvocatoriaHito} de la
   * {@link Convocatoria}.
   *
   * @param convocatoriaId identificador de la {@link Convocatoria}
   * @return referencias de las tareas programadas
   */
  private List<String> findTareaProgramadaRefs(Long convocatoriaId) {
    Stream<String> faseRefs = convocatoriaFaseRepository
        .findAll(ConvocatoriaFaseSpecifications.byConvocatoriaId(convocatoriaId)).stream()
        .flatMap(fase -> Stream.of(fase.getConvocatoriaFaseAviso1(), fase.getConvocatoriaFaseAviso2()))
        .filter(Objects::nonNull)
        .map(ConvocatoriaFaseAviso::getTareaProgramadaRef);

    Stream<String> hitoRefs = convocatoriaHitoRepository
        .findAll(ConvocatoriaHitoSpecifications.byConvocatoriaId(convocatoriaId)).stream()
        .map(ConvocatoriaHito::getConvocatoriaHitoAviso)
        .filter(Objects::nonNull)
        .map(ConvocatoriaHitoAviso::getTareaProgramadaRef);

    return Stream.concat(faseRefs, hitoRefs).toList();
  }

}
