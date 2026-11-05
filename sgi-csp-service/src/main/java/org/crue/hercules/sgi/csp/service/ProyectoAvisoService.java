package org.crue.hercules.sgi.csp.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.crue.hercules.sgi.csp.model.ProyectoFaseAviso;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.model.ProyectoHitoAviso;
import org.crue.hercules.sgi.csp.repository.ProyectoFaseRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoHitoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoFaseSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoHitoSpecifications;
import org.crue.hercules.sgi.csp.util.AvisoTaskHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para la gestión conjunta de los avisos ({@link ProyectoFaseAviso} y
 * {@link ProyectoHitoAviso}) de un {@link Proyecto}.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProyectoAvisoService {

  private final ProyectoFaseRepository proyectoFaseRepository;
  private final ProyectoHitoRepository proyectoHitoRepository;
  private final AvisoTaskHelper avisoTaskHelper;

  /**
   * Activa las tareas programadas de los avisos pendientes de envío de las
   * {@link ProyectoFase} y los {@link ProyectoHito} del {@link Proyecto}.
   *
   * @param proyectoId identificador del {@link Proyecto}
   */
  public void enablePendingAvisos(Long proyectoId) {
    log.debug("enablePendingAvisos - proyectoId: {}", proyectoId);
    avisoTaskHelper.enablePendingTasks(findTareaProgramadaRefs(proyectoId));
  }

  /**
   * Desactiva las tareas programadas de los avisos pendientes de envío de las
   * {@link ProyectoFase} y los {@link ProyectoHito} del {@link Proyecto}, de
   * forma que no se envían mientras sigan desactivadas.
   *
   * @param proyectoId identificador del {@link Proyecto}
   */
  public void disablePendingAvisos(Long proyectoId) {
    log.debug("disablePendingAvisos - proyectoId: {}", proyectoId);
    avisoTaskHelper.disablePendingTasks(findTareaProgramadaRefs(proyectoId));
  }

  /**
   * Obtiene las referencias de las tareas programadas de los avisos de las
   * {@link ProyectoFase} y los {@link ProyectoHito} del {@link Proyecto}.
   *
   * @param proyectoId identificador del {@link Proyecto}
   * @return referencias de las tareas programadas
   */
  private List<String> findTareaProgramadaRefs(Long proyectoId) {
    Stream<String> faseRefs = proyectoFaseRepository
        .findAll(ProyectoFaseSpecifications.byProyectoId(proyectoId)).stream()
        .flatMap(fase -> Stream.of(fase.getProyectoFaseAviso1(), fase.getProyectoFaseAviso2()))
        .filter(Objects::nonNull)
        .map(ProyectoFaseAviso::getTareaProgramadaRef);

    Stream<String> hitoRefs = proyectoHitoRepository
        .findAll(ProyectoHitoSpecifications.byProyectoId(proyectoId)).stream()
        .map(ProyectoHito::getProyectoHitoAviso)
        .filter(Objects::nonNull)
        .map(ProyectoHitoAviso::getTareaProgramadaRef);

    return Stream.concat(faseRefs, hitoRefs).toList();
  }

}
