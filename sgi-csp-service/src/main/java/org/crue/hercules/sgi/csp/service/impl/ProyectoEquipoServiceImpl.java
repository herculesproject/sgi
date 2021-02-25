package org.crue.hercules.sgi.csp.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ProyectoEquipoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoEquipoSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoEquipoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ProyectoEquipo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoEquipoServiceImpl implements ProyectoEquipoService {

  private final ProyectoEquipoRepository repository;
  private final ProyectoRepository proyectoRepository;

  public ProyectoEquipoServiceImpl(ProyectoEquipoRepository repository, ProyectoRepository proyectoRepository) {
    this.repository = repository;
    this.proyectoRepository = proyectoRepository;
  }

  /**
   * Actualiza el listado de {@link ProyectoEquipo} de la {@link Proyecto} con el
   * listado proyectoEquipos añadiendo, editando o eliminando los elementos segun
   * proceda.
   *
   * @param proyectoId      Id de la {@link Proyecto}.
   * @param proyectoEquipos lista con los nuevos {@link ProyectoEquipo} a guardar.
   * @return la entidad {@link ProyectoEquipo} persistida.
   */
  @Override
  @Transactional
  public List<ProyectoEquipo> update(Long proyectoId, List<ProyectoEquipo> proyectoEquipos) {
    log.debug("update(Long proyectoId, List<ProyectoEquipo> proyectoEquipos) - start");

    Proyecto proyecto = proyectoRepository.findById(proyectoId)
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoId));

    List<ProyectoEquipo> proyectoEquipoesBD = repository.findAllByProyectoId(proyectoId);

    // Periodos eliminados
    List<ProyectoEquipo> proyectoEquiposEliminar = proyectoEquipoesBD.stream()
        .filter(periodo -> !proyectoEquipos.stream().map(ProyectoEquipo::getId).anyMatch(id -> id == periodo.getId()))
        .collect(Collectors.toList());

    if (!proyectoEquiposEliminar.isEmpty()) {
      repository.deleteAll(proyectoEquiposEliminar);
    }

    // Ordena los periodos por mesInicial
    List<ProyectoEquipo> proyectoEquipoFechaInicioNull = proyectoEquipos.stream()
        .filter(periodo -> periodo.getFechaInicio() == null).collect(Collectors.toList());

    List<ProyectoEquipo> proyectoEquipoConFechaInicio = proyectoEquipos.stream()
        .filter(periodo -> periodo.getFechaInicio() != null).collect(Collectors.toList());

    proyectoEquipoFechaInicioNull.sort(Comparator.comparing(ProyectoEquipo::getPersonaRef));

    proyectoEquipoConFechaInicio.sort(Comparator.comparing(ProyectoEquipo::getFechaInicio)
        .thenComparing(Comparator.comparing(ProyectoEquipo::getPersonaRef)));

    List<ProyectoEquipo> proyectoEquipoAll = new ArrayList<>();
    proyectoEquipoAll.addAll(proyectoEquipoFechaInicioNull);
    proyectoEquipoAll.addAll(proyectoEquipoConFechaInicio);

    // Validaciones

    ProyectoEquipo proyectoEquipoAnterior = null;
    for (ProyectoEquipo proyectoEquipo : proyectoEquipoAll) {

      // actualizando
      if (proyectoEquipo.getId() != null) {
        ProyectoEquipo proyectoEquipoBD = proyectoEquipoesBD.stream()
            .filter(periodo -> periodo.getId() == proyectoEquipo.getId()).findFirst()
            .orElseThrow(() -> new ProyectoEquipoNotFoundException(proyectoEquipo.getId()));

        Assert.isTrue(proyectoEquipoBD.getProyecto().getId() == proyectoEquipo.getProyecto().getId(),
            "No se puede modificar el proyecto del ProyectoEquipo");
      }

      proyectoEquipo.setProyecto(proyecto);

      if (proyectoEquipo.getFechaInicio() != null && proyectoEquipo.getFechaFin() != null) {
        Assert.isTrue(proyectoEquipo.getFechaInicio().isBefore(proyectoEquipo.getFechaFin()),
            "La fecha fin no puede ser superior a la fecha de inicio");

        Assert.isTrue(
            (proyectoEquipo.getFechaInicio().isAfter(proyectoEquipo.getProyecto().getFechaInicio())
                || proyectoEquipo.getFechaInicio().isEqual(proyectoEquipo.getProyecto().getFechaInicio()))
                && (proyectoEquipo.getFechaFin().isBefore(proyectoEquipo.getProyecto().getFechaFin())
                    || proyectoEquipo.getFechaFin().isEqual(proyectoEquipo.getProyecto().getFechaFin())),
            "Las fechas de proyecto equipo deben de estar dentro de la duración del proyecto");
      }

      Specification<ProyectoEquipo> specByProyectoId = ProyectoEquipoSpecifications
          .byProyectoId(proyectoEquipo.getProyecto().getId());

      Specification<ProyectoEquipo> specByIdNotEqual = ProyectoEquipoSpecifications
          .byIdNotEqual(proyectoEquipo.getId());

      Specification<ProyectoEquipo> specByRangoFechaSolapados = ProyectoEquipoSpecifications
          .byRangoFechaSolapados(proyectoEquipo.getFechaInicio(), proyectoEquipo.getFechaFin());

      Specification<ProyectoEquipo> specByPersonaRef = ProyectoEquipoSpecifications
          .byPersonaRef(proyectoEquipo.getPersonaRef());

      Specification<ProyectoEquipo> specs = Specification.where(specByProyectoId).and(specByRangoFechaSolapados)
          .and(specByRangoFechaSolapados).and(specByPersonaRef).and(specByIdNotEqual);

      if (proyectoEquipoAnterior != null
          && proyectoEquipoAnterior.getPersonaRef().equals(proyectoEquipo.getPersonaRef())) {
        Assert.isTrue(
            (repository.count(specs) == 0) && proyectoEquipoAnterior.getFechaFin() != null
                && proyectoEquipoAnterior.getFechaFin().isBefore(proyectoEquipo.getFechaInicio()),
            "El proyecto equipo se solapa con otro existente");
      }

      proyectoEquipoAnterior = proyectoEquipo;

    }

    List<ProyectoEquipo> returnValue = repository.saveAll(proyectoEquipoAll);
    log.debug("updateProyectoEquiposConvocatoria(Long proyectoId, List<ProyectoEquipo> proyectoEquipos) - end");

    return returnValue;
  }

  /**
   * Obtiene una entidad {@link ProyectoEquipo} por id.
   * 
   * @param id Identificador de la entidad {@link ProyectoEquipo}.
   * @return ProyectoEquipo la entidad {@link ProyectoEquipo}.
   */
  @Override
  public ProyectoEquipo findById(Long id) {
    log.debug("findById(Long id) - start");
    final ProyectoEquipo returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoEquipoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ProyectoEquipo} para una {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoEquipo} de la {@link Proyecto}
   *         paginadas.
   */
  public Page<ProyectoEquipo> findAllByProyecto(Long proyectoId, String query, Pageable pageable) {
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - start");
    Specification<ProyectoEquipo> specs = ProyectoEquipoSpecifications.byProyectoId(proyectoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoEquipo> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;
  }

}
